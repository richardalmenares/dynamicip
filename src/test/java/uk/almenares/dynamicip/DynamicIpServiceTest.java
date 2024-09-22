package uk.almenares.dynamicip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicIpServiceTest {

    @Mock
    IpAddressProvider ipAddressProvider;
    @Mock
    DnsRecordUpdater dnsRecordUpdater;

    DynamicIpService dynamicIpService;

    @BeforeEach
    void setUp() {
        DynamicIpProperties dynamicIpProperties = new DynamicIpProperties(
                Set.of("test.domain.uk")
        );
        dynamicIpService = new DynamicIpService(
                ipAddressProvider,
                dynamicIpProperties,
                dnsRecordUpdater
        );
    }

    @Test
    void test_updateDynamicIp_success() {
        when(ipAddressProvider.getPublicIp()).thenReturn("192.168.1.1");
        when(dnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test.domain.uk")
        )).thenReturn(List.of(
                new DnsRecordUpdater.UpdateResult(
                        "test.domain.uk",
                        "192.168.1.2",
                        "192.168.1.1"
                )
        ));

        dynamicIpService.updateDynamicIp();

        verify(ipAddressProvider).getPublicIp();
        verify(dnsRecordUpdater).update(
                "192.168.1.1",
                Set.of("test.domain.uk")
        );
    }

    @Test
    void test_updateDynamicIp_failure() {
        when(ipAddressProvider.getPublicIp()).thenReturn("192.168.1.1");
        when(dnsRecordUpdater.update(
                "192.168.1.1",
                Set.of("test.domain.uk")
        )).thenThrow(new RuntimeException("TEST"));

        dynamicIpService.updateDynamicIp();

        verify(ipAddressProvider).getPublicIp();
        verify(dnsRecordUpdater).update(
                "192.168.1.1",
                Set.of("test.domain.uk")
        );
    }

}