package uk.almenares.dynamicip.amazonaws;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.web.client.HttpClientErrorException;
import uk.almenares.dynamicip.WiremockTestContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = AmazonAwsIpAddressProvider.class
)
@EnableAutoConfiguration
@EnableConfigurationProperties(AmazonAwsProperties.class)
@ImportTestcontainers(WiremockTestContainer.class)
class AmazonAwsIpAddressProviderIntegrationTest {

    @Autowired
    private AmazonAwsIpAddressProvider amazonAwsIpAddressProvider;

    @BeforeEach
    void setUp() {
        WiremockTestContainer.configureWiremockClient();
        WireMock.reset();
    }

    @Test
    void test_getIpAddress_success() {
        WireMock.stubFor(
                WireMock.get("/").willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "text/plain;charset=UTF-8")
                                .withBody("77.100.171.99\n")
                )
        );

        String actual = amazonAwsIpAddressProvider.getPublicIp();

        assertThat(actual).isEqualTo("77.100.171.99");
    }

    @Test
    void test_getIpAddress_failure() {
        WireMock.stubFor(
                WireMock.get("/").willReturn(
                        WireMock.aResponse()
                                .withStatus(400)
                )
        );

        assertThatThrownBy(() -> amazonAwsIpAddressProvider.getPublicIp())
                .isInstanceOf(HttpClientErrorException.class);
    }
}