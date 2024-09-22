package uk.almenares.dynamicip;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "dynamic-ip")
public record DynamicIpProperties(
        Set<String> domains
) {
}
