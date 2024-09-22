package uk.almenares.dynamicip;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.wiremock.integrations.testcontainers.WireMockContainer.WIREMOCK_2_LATEST;

@Testcontainers
public class WiremockTestContainer {

    @Container
    public static final WireMockContainer WIRE_MOCK_CONTAINER = new WireMockContainer(WIREMOCK_2_LATEST);

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "amazon-aws.baseUrl",
                () -> "http://%s:%s/".formatted(
                        WIRE_MOCK_CONTAINER.getHost(),
                        WIRE_MOCK_CONTAINER.getMappedPort(8080)
                )
        );
        registry.add(
                "cloud-flare.baseUrl",
                () -> "http://%s:%s/client/v4/zones/".formatted(
                        WIRE_MOCK_CONTAINER.getHost(),
                        WIRE_MOCK_CONTAINER.getMappedPort(8080)
                )
        );
    }

    public static void configureWiremockClient() {
        WireMock.configureFor(
                WIRE_MOCK_CONTAINER.getHost(),
                WIRE_MOCK_CONTAINER.getMappedPort(8080)
        );
    }

}
