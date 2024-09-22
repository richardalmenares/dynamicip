package uk.almenares.dynamicip.cloudflare;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uk.almenares.dynamicip.DnsRecordUpdater;

import java.util.Collection;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class CloudFlareDnsRecordUpdater implements DnsRecordUpdater {

    private final CloudFlareProperties cloudFlareProperties;
    private final RestClient restClient;

    public CloudFlareDnsRecordUpdater(
            CloudFlareProperties cloudFlareProperties,
            RestClient.Builder restclientBuilder
    ) {
        this.cloudFlareProperties = cloudFlareProperties;
        this.restClient = restclientBuilder
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(cloudFlareProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cloudFlareProperties.apiKey())
                .build();
    }

    @Override
    public Collection<UpdateResult> update(String publicIpAddress, Set<String> domains) {
        CloudFlareZoneRecordListResult result = getZoneRecords(1);
        Stream<CloudFlareZoneRecord> zoneRecordStream = getCloudFlareZoneRecordStream(result);

        return zoneRecordStream
                .filter(zoneRecord -> domains.contains(zoneRecord.name()))
                .filter(zoneRecord -> !publicIpAddress.equals(zoneRecord.content()))
                .map(zoneRecord -> updateZoneRecord(publicIpAddress, zoneRecord))
                .toList();
    }

    private Stream<CloudFlareZoneRecord> getCloudFlareZoneRecordStream(CloudFlareZoneRecordListResult result) {
        int totalPages = result.resultInfo().totalPages();
        Stream<CloudFlareZoneRecord> zoneRecordStream = result.result().stream();

        // get all pages
        if (totalPages > 1) {
            Stream<CloudFlareZoneRecord> furtherZoneRecordStream = IntStream.range(2, totalPages + 1)
                    .mapToObj(this::getZoneRecords)
                    .flatMap(furtherResults -> furtherResults.result().stream());
            zoneRecordStream = Stream.concat(zoneRecordStream, furtherZoneRecordStream);
        }

        return zoneRecordStream;
    }

    private CloudFlareZoneRecordListResult getZoneRecords(int pageNumber) {
        return restClient.get()
                .uri("/{zone_id}/dns_records?page={page}", cloudFlareProperties.zoneId(), pageNumber)
                .retrieve()
                .body(CloudFlareZoneRecordListResult.class);
    }

    private UpdateResult updateZoneRecord(
            String publicIpAddress,
            CloudFlareZoneRecord cloudFlareZoneRecord
    ) {
        CloudFlareUpdateZoneRecordResult updateZoneRecordResult = updateZoneRecord(
                publicIpAddress,
                cloudFlareZoneRecord.id()
        );

        return new UpdateResult(
                updateZoneRecordResult.result().name(),
                cloudFlareZoneRecord.content(),
                updateZoneRecordResult.result().content()
        );
    }

    private CloudFlareUpdateZoneRecordResult updateZoneRecord(
            String newIp,
            String recordId
    ) {
        return restClient.patch()
                .uri(
                        "/{zone_id}/dns_records/{dns_record_id}",
                        cloudFlareProperties.zoneId(),
                        recordId
                )
                .body(new CloudFlareZoneUpdateRequest(newIp))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(CloudFlareUpdateZoneRecordResult.class);
    }
}
