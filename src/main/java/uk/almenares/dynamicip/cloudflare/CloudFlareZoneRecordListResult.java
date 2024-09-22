package uk.almenares.dynamicip.cloudflare;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record CloudFlareZoneRecordListResult(
        Collection<CloudFlareZoneRecord> result,
        Boolean success,
        @JsonProperty("result_info")
        CloudFlareResultInfo resultInfo
) {
}
