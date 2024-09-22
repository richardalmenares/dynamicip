package uk.almenares.dynamicip.amazonaws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amazon-aws")
public record AmazonAwsProperties(
       String baseUrl
) {}
