package uk.almenares.dynamicip;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;


public class WiremockTestContainer {

    public static final WireMockContainer WIREMOCK_CONTAINER = new WireMockContainer(
            DockerImageName.parse(WireMockContainer.OFFICIAL_IMAGE_NAME)
    );

    @DynamicPropertySource
    public static void configureProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "amazon-aws.baseUrl",
                () -> "http://%s:%s/".formatted(
                        WIREMOCK_CONTAINER.getHost(),
                        WIREMOCK_CONTAINER.getMappedPort(8080)
                )
        );
        registry.add(
                "cloud-flare.baseUrl",
                () -> "http://%s:%s/client/v4/zones/".formatted(
                        WIREMOCK_CONTAINER.getHost(),
                        WIREMOCK_CONTAINER.getMappedPort(8080)
                )
        );
    }

    public static void configureWiremockClient() {
        WireMock.configureFor(
                WIREMOCK_CONTAINER.getHost(),
                WIREMOCK_CONTAINER.getMappedPort(8080)
        );
    }
}
