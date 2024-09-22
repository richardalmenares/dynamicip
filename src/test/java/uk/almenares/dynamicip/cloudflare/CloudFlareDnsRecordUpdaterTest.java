package uk.almenares.dynamicip.cloudflare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import uk.almenares.dynamicip.DnsRecordUpdater;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudFlareDnsRecordUpdaterTest {

    @Mock
    RestClient restClient;
    @Mock
    RestClient.Builder restClientBuilder;
    @Mock
    RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    RestClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock
    RestClient.ResponseSpec responseSpec;
    @Mock
    RestClient.RequestBodySpec requestBodySpec;
    @Mock
    RestClient.RequestBodyUriSpec requestBodyUriSpec;

    CloudFlareDnsRecordUpdater cloudFlareDnsRecordUpdater;

    @BeforeEach
    void setUp() {
        CloudFlareProperties cloudFlareProperties = new CloudFlareProperties(
                "https://cloudflare.com/api/zones",
                "apiKey",
                "someZone"
        );
        when(restClientBuilder.requestFactory(any(HttpComponentsClientHttpRequestFactory.class)))
                .thenReturn(restClientBuilder);
        when(restClientBuilder.baseUrl("https://cloudflare.com/api/zones")).thenReturn(restClientBuilder);
        when(restClientBuilder.defaultHeader(
                "Authorization",
                "Bearer apiKey"
        )).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        cloudFlareDnsRecordUpdater = new CloudFlareDnsRecordUpdater(
                cloudFlareProperties,
                restClientBuilder
        );
    }

    @Test
    void update_success_onePage_withUpdatesCalls() {
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id",
                        "someZone",
                        "name.com",
                        "test.name.com",
                        "A",
                        "192.168.1.1",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        1,
                        100,
                        1,
                        1,
                        1
                )
        );
        CloudFlareZoneUpdateRequest updateRequest = new CloudFlareZoneUpdateRequest("192.168.1.2");
        CloudFlareUpdateZoneRecordResult cloudFlareUpdateZoneRecordResult = new CloudFlareUpdateZoneRecordResult(
                true,
                new CloudFlareZoneRecord(
                        "id",
                        "someZone",
                        "name.com",
                        "test.name.com",
                        "A",
                        "192.168.1.2",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )
        );

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareZoneRecordListResult.class))
                .thenReturn(cloudFlareZoneRecordListResult);
        when(restClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(
                "/{zone_id}/dns_records/{dns_record_id}",
                "someZone",
                "id"
        )).thenReturn(requestBodySpec);
        when(requestBodySpec.body(updateRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(
                "Content-Type",
                "application/json"
        )).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareUpdateZoneRecordResult.class))
                .thenReturn(cloudFlareUpdateZoneRecordResult);

        Collection<DnsRecordUpdater.UpdateResult> updateResults = cloudFlareDnsRecordUpdater.update(
                "192.168.1.2",
                Set.of("test.name.com")
        );

        assertThat(updateResults)
                .hasSize(1)
                .contains(new DnsRecordUpdater.UpdateResult(
                        "test.name.com",
                        "192.168.1.1",
                        "192.168.1.2"
                ));

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(CloudFlareZoneRecordListResult.class);
        verify(restClient).patch();
        verify(requestBodyUriSpec).uri(
                "/{zone_id}/dns_records/{dns_record_id}",
                "someZone",
                "id"
        );
        verify(requestBodySpec).body(updateRequest);
        verify(requestBodySpec).header(
                "Content-Type",
                "application/json"
        );
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(CloudFlareUpdateZoneRecordResult.class);
    }

    @Test
    void update_success_onePage_noUpdateCalls() {
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id",
                        "someZone",
                        "name.com",
                        "test.name.com",
                        "A",
                        "192.168.1.1",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        1,
                        100,
                        1,
                        1,
                        1
                )
        );

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareZoneRecordListResult.class))
                .thenReturn(cloudFlareZoneRecordListResult);

        Collection<DnsRecordUpdater.UpdateResult> updateResults = cloudFlareDnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test.name.com")
        );

        assertThat(updateResults).isEmpty();

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(CloudFlareZoneRecordListResult.class);
    }

    @Test
    void update_success_multiplePages_noUpdateCalls() {
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult_1 = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id",
                        "someZone",
                        "name.com",
                        "test.name.com",
                        "A",
                        "192.168.1.1",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        1,
                        1,
                        1,
                        2,
                        2
                )
        );
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult_2 = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id2",
                        "someZone",
                        "name.com",
                        "test2.name.com",
                        "A",
                        "192.168.1.2",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        2,
                        1,
                        1,
                        2,
                        2
                )
        );

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                2
        );
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareZoneRecordListResult.class))
                .thenReturn(cloudFlareZoneRecordListResult_1, cloudFlareZoneRecordListResult_2);

        Collection<DnsRecordUpdater.UpdateResult> updateResults = cloudFlareDnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test.name.com")
        );

        assertThat(updateResults).isEmpty();

        verify(restClient, times(2)).get();
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                2
        );
        verify(requestHeadersSpec, times(2)).retrieve();
        verify(responseSpec, times(2)).body(CloudFlareZoneRecordListResult.class);
    }

    @Test
    void update_success_multiplePages_withUpdateCalls() {
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult_1 = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id",
                        "someZone",
                        "name.com",
                        "test.name.com",
                        "A",
                        "192.168.1.1",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        1,
                        1,
                        1,
                        2,
                        2
                )
        );
        CloudFlareZoneRecordListResult cloudFlareZoneRecordListResult_2 = new CloudFlareZoneRecordListResult(
                List.of(new CloudFlareZoneRecord(
                        "id2",
                        "someZone",
                        "name.com",
                        "test2.name.com",
                        "A",
                        "192.168.1.2",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )),
                true,
                new CloudFlareResultInfo(
                        2,
                        1,
                        1,
                        2,
                        2
                )
        );
        CloudFlareZoneUpdateRequest updateRequest = new CloudFlareZoneUpdateRequest("192.168.1.1");
        CloudFlareUpdateZoneRecordResult cloudFlareUpdateZoneRecordResult = new CloudFlareUpdateZoneRecordResult(
                true,
                new CloudFlareZoneRecord(
                        "id2",
                        "someZone",
                        "name.com",
                        "test2.name.com",
                        "A",
                        "192.168.1.1",
                        false,
                        false,
                        0,
                        Instant.ofEpochMilli(1726940696168L),
                        Instant.ofEpochMilli(1726940706842L)
                )
        );

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                2
        );
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareZoneRecordListResult.class))
                .thenReturn(cloudFlareZoneRecordListResult_1, cloudFlareZoneRecordListResult_2);
        when(restClient.patch()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(
                "/{zone_id}/dns_records/{dns_record_id}",
                "someZone",
                "id2"
        )).thenReturn(requestBodySpec);
        when(requestBodySpec.body(updateRequest)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(
                "Content-Type",
                "application/json"
        )).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CloudFlareUpdateZoneRecordResult.class))
                .thenReturn(cloudFlareUpdateZoneRecordResult);

        Collection<DnsRecordUpdater.UpdateResult> updateResults = cloudFlareDnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test2.name.com")
        );

        assertThat(updateResults)
                .hasSize(1)
                .contains(new DnsRecordUpdater.UpdateResult(
                        "test2.name.com",
                        "192.168.1.2",
                        "192.168.1.1"
                ));;

        verify(restClient, times(2)).get();
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                2
        );
        verify(requestHeadersSpec, times(2)).retrieve();
        verify(responseSpec, times(2)).body(CloudFlareZoneRecordListResult.class);
        verify(restClient).patch();
        verify(requestBodyUriSpec).uri(
                "/{zone_id}/dns_records/{dns_record_id}",
                "someZone",
                "id2"
        );
        verify(requestBodySpec).body(updateRequest);
        verify(requestBodySpec).header(
                "Content-Type",
                "application/json"
        );
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(CloudFlareUpdateZoneRecordResult.class);
    }

    @Test
    void update_failure() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        when(requestHeadersSpec.retrieve()).thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(400)));

        assertThatThrownBy(() -> cloudFlareDnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test.name.com")
        )).isInstanceOf(HttpClientErrorException.class);

        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                "/{zone_id}/dns_records?page={page}",
                "someZone",
                1
        );
        verify(requestHeadersSpec).retrieve();
    }
}