package uk.almenares.dynamicip;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.Mockito.verify;

@WebMvcTest(DynamicIpController.class)
@AutoConfigureRestTestClient
class DynamicIpControllerTest {

    @MockitoBean
    DynamicIpService service;

    @Autowired
    RestTestClient restTestClient;

    @Test
    void updateAllRecords() {
        restTestClient.post()
                .uri("/api/v1/dynaminip/updateAll")
                .exchange()
                .expectStatus().isOk();

        verify(service).updateDynamicIp();
    }
}