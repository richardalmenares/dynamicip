package uk.almenares.dynamicip.cloudflare;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import uk.almenares.dynamicip.DnsRecordUpdater;
import uk.almenares.dynamicip.WiremockTestContainer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = CloudFlareDnsRecordUpdater.class
)
@EnableAutoConfiguration
@EnableConfigurationProperties(CloudFlareProperties.class)
@ImportTestcontainers(WiremockTestContainer.class)
@ActiveProfiles("cfinteg")
class CloudFlareDnsRecordUpdaterIntegrationTest {

    @Autowired
    private CloudFlareDnsRecordUpdater cloudFlareDnsRecordUpdater;

    @BeforeEach
    void setUp() {
        WiremockTestContainer.configureWiremockClient();
        WireMock.reset();
    }

    @Test
    void test_updateIp_success() {
        WireMock.stubFor(
                WireMock.get("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records?page=1").willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                                        {
                                                            "result": [
                                                                {
                                                                    "id": "ab437c76d3afc8dcc2e19d354tbnfef6",
                                                                    "zone_id": "ab510c76d3afc8dcc2e19d354ebbdaa1",
                                                                    "zone_name": "test.uk",
                                                                    "name": "name1.test.uk",
                                                                    "type": "A",
                                                                    "content": "192.168.1.1",
                                                                    "proxiable": true,
                                                                    "proxied": false,
                                                                    "ttl": 1,
                                                                    "settings": {},
                                                                    "meta": {
                                                                        "auto_added": false,
                                                                        "managed_by_apps": false,
                                                                        "managed_by_argo_tunnel": false
                                                                    },
                                                                    "comment": null,
                                                                    "tags": [],
                                                                    "created_on": "2024-09-03T18:08:23.977124Z",
                                                                    "modified_on": "2024-09-03T18:38:45.776074Z"
                                                                },
                                                                {
                                                                    "id": "ab437c76d3afc8dfg2e19d354rdbklf6",
                                                                    "zone_id": "ab510c76d3afc8dcc2e19d354ebbdaa1",
                                                                    "zone_name": "test.uk",
                                                                    "name": "name2.test.uk",
                                                                    "type": "A",
                                                                    "content": "192.168.1.1",
                                                                    "proxiable": true,
                                                                    "proxied": false,
                                                                    "ttl": 1,
                                                                    "settings": {},
                                                                    "meta": {
                                                                        "auto_added": false,
                                                                        "managed_by_apps": false,
                                                                        "managed_by_argo_tunnel": false
                                                                    },
                                                                    "comment": null,
                                                                    "tags": [],
                                                                    "created_on": "2024-09-03T18:08:23.977124Z",
                                                                    "modified_on": "2024-09-03T18:38:45.776074Z"
                                                                }
                                                            ],
                                                            "success": true,
                                                            "errors": [],
                                                            "messages": [],
                                                            "result_info": {
                                                                "page": 1,
                                                                "per_page": 2,
                                                                "count": 2,
                                                                "total_count": 4,
                                                                "total_pages": 2
                                                            }
                                                        }
                                                """
                                )
                )
        );
        WireMock.stubFor(
                WireMock.get("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records?page=2").willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                                        {
                                                            "result": [
                                                                {
                                                                    "id": "ab437c76d3afc8dcc2e19d354tbnfef7",
                                                                    "zone_id": "ab510c76d3afc8dcc2e19d354ebbdaa1",
                                                                    "zone_name": "test.uk",
                                                                    "name": "name3.test.uk",
                                                                    "type": "A",
                                                                    "content": "192.168.1.1",
                                                                    "proxiable": true,
                                                                    "proxied": false,
                                                                    "ttl": 1,
                                                                    "settings": {},
                                                                    "meta": {
                                                                        "auto_added": false,
                                                                        "managed_by_apps": false,
                                                                        "managed_by_argo_tunnel": false
                                                                    },
                                                                    "comment": null,
                                                                    "tags": [],
                                                                    "created_on": "2024-09-03T18:08:23.977124Z",
                                                                    "modified_on": "2024-09-03T18:38:45.776074Z"
                                                                },
                                                                {
                                                                    "id": "ab437c76d3afc8dfg2e19d354rdbklf8",
                                                                    "zone_id": "ab510c76d3afc8dcc2e19d354ebbdaa1",
                                                                    "zone_name": "test.uk",
                                                                    "name": "name4.test.uk",
                                                                    "type": "A",
                                                                    "content": "192.168.1.1",
                                                                    "proxiable": true,
                                                                    "proxied": false,
                                                                    "ttl": 1,
                                                                    "settings": {},
                                                                    "meta": {
                                                                        "auto_added": false,
                                                                        "managed_by_apps": false,
                                                                        "managed_by_argo_tunnel": false
                                                                    },
                                                                    "comment": null,
                                                                    "tags": [],
                                                                    "created_on": "2024-09-03T18:08:23.977124Z",
                                                                    "modified_on": "2024-09-03T18:38:45.776074Z"
                                                                }
                                                            ],
                                                            "success": true,
                                                            "errors": [],
                                                            "messages": [],
                                                            "result_info": {
                                                                "page": 2,
                                                                "per_page": 2,
                                                                "count": 2,
                                                                "total_count": 4,
                                                                "total_pages": 2
                                                            }
                                                        }
                                                """
                                )
                )
        );
        WireMock.stubFor(
                WireMock.patch("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records/ab437c76d3afc8dfg2e19d354rdbklf8").willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(
                                        """
                                                        {
                                                          "errors": [],
                                                          "messages": [],
                                                          "success": true,
                                                          "result": {
                                                                    "id": "ab437c76d3afc8dfg2e19d354rdbklf8",
                                                                    "zone_id": "ab510c76d3afc8dcc2e19d354ebbdaa1",
                                                                    "zone_name": "test.uk",
                                                                    "name": "name4.test.uk",
                                                                    "type": "A",
                                                                    "content": "192.168.1.2",
                                                                    "proxiable": true,
                                                                    "proxied": false,
                                                                    "ttl": 1,
                                                                    "settings": {},
                                                                    "meta": {
                                                                        "auto_added": false,
                                                                        "managed_by_apps": false,
                                                                        "managed_by_argo_tunnel": false
                                                                    },
                                                                    "comment": null,
                                                                    "tags": [],
                                                                    "created_on": "2024-09-03T18:08:23.977124Z",
                                                                    "modified_on": "2024-09-03T18:38:45.776074Z"
                                                                }
                                                        }
                                                """
                                )
                )
        );

        Collection<DnsRecordUpdater.UpdateResult> updateResults = cloudFlareDnsRecordUpdater.update(
                "192.168.1.2",
                Set.of("name4.test.uk")
        );

        assertThat(updateResults)
                .hasSize(1)
                .contains(new DnsRecordUpdater.UpdateResult(
                        "name4.test.uk",
                        "192.168.1.1",
                        "192.168.1.2"
                ));

        List<LoggedRequest> getDnsRecords_1 = WireMock.findAll(WireMock.getRequestedFor(WireMock.urlEqualTo("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records?page=1")));
        List<LoggedRequest> getDnsRecords_2 = WireMock.findAll(WireMock.getRequestedFor(WireMock.urlEqualTo("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records?page=2")));
        List<LoggedRequest> updateDnsRecord = WireMock.findAll(WireMock.patchRequestedFor(WireMock.urlEqualTo("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records/ab437c76d3afc8dfg2e19d354rdbklf8")));

        assertThat(getDnsRecords_1).hasSize(1);
        assertThat(getDnsRecords_2).hasSize(1);
        assertThat(updateDnsRecord).hasSize(1);
        assertThatJson(updateDnsRecord.getFirst().getBodyAsString()).isEqualTo(
                """
                        {
                            "content": "192.168.1.2"
                        }
                        """
        );
    }

    @Test
    void test_updateIp_failure() {
        WireMock.stubFor(
                WireMock.get("/client/v4/zones/ab510c76d3afc8dcc2e19d354ebbdaa1/dns_records?page=1").willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(400)
                )
        );

        assertThatThrownBy(() -> cloudFlareDnsRecordUpdater.update(
                "192.168.1.2",
                Set.of("name4.test.uk")
        )).isInstanceOf(HttpClientErrorException.class);
    }

}