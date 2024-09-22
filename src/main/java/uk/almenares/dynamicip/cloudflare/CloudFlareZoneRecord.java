package uk.almenares.dynamicip.cloudflare;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CloudFlareZoneRecord(
        String id,
        @JsonProperty("zone_id")
        String zoneId,
        @JsonProperty("zone_name")
        String zoneName,
        String name,
        String type,
        String content,
        Boolean proxiable,
        Boolean proxied,
        int ttl,
        @JsonProperty("created_on")
        Instant createdOn,
        @JsonProperty("modified_on")
        Instant modifiedOn
) {
}
