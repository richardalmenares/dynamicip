package uk.almenares.dynamicip.amazonaws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmazonAwsIpAddressProviderTest {

    @Mock
    RestClient restClient;
    @Mock
    RestClient.Builder restClientBuilder;
    @Mock
    RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    @Mock
    RestClient.ResponseSpec responseSpec;

    AmazonAwsIpAddressProvider amazonAwsIpAddressProvider;

    @BeforeEach
    void setUp() {
        AmazonAwsProperties amazonAwsProperties = new AmazonAwsProperties("https://getip.aws.com");
        when(restClientBuilder.requestFactory(any(HttpComponentsClientHttpRequestFactory.class)))
                .thenReturn(restClientBuilder);
        when(restClientBuilder.baseUrl("https://getip.aws.com"))
                .thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        amazonAwsIpAddressProvider = new AmazonAwsIpAddressProvider(
                amazonAwsProperties,
                restClientBuilder
        );
    }

    @Test
    void getPublicIp_success() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("77.100.171.99\n");

        String actual = amazonAwsIpAddressProvider.getPublicIp();

        assertThat(actual).isEqualTo("77.100.171.99");

        verify(restClient).get();
        verify(requestHeadersUriSpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void getPublicIp_failure() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        when(requestHeadersUriSpec.retrieve()).thenThrow(new HttpClientErrorException(HttpStatusCode.valueOf(400)));

        assertThatThrownBy(() -> amazonAwsIpAddressProvider.getPublicIp())
                .isInstanceOf(HttpClientErrorException.class);

        verify(restClient).get();
        verify(requestHeadersUriSpec).retrieve();
    }
}