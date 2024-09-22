package uk.almenares.dynamicip.cloudflare;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud-flare")
public record CloudFlareProperties(
        String baseUrl,
        String apiKey,
        String zoneId
) {
}
