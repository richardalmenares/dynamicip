package uk.almenares.dynamicip.amazonaws;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uk.almenares.dynamicip.IpAddressProvider;

@Component
public class AmazonAwsIpAddressProvider implements IpAddressProvider {

    private final RestClient restClient;

    public AmazonAwsIpAddressProvider(
            AmazonAwsProperties amazonAwsProperties,
            RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(amazonAwsProperties.baseUrl())
                .build();
    }

    @Override
    public String getPublicIp() {
        return restClient.get()
                .retrieve()
                .body(String.class)
                .trim();
    }

}
