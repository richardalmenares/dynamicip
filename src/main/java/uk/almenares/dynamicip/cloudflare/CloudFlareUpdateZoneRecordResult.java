package uk.almenares.dynamicip.cloudflare;

public record CloudFlareUpdateZoneRecordResult(
        Boolean success,
        CloudFlareZoneRecord result
) {
}
