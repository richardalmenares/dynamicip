package uk.almenares.dynamicip.cloudflare;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloudFlareResultInfo(
        int page,
        @JsonProperty("per_page")
        int perPage,
        int count,
        @JsonProperty("total_count")
        int totalCount,
        @JsonProperty("total_pages")
        int totalPages

) {
}
