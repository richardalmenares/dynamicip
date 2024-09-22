package uk.almenares.dynamicip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class DynamicIpService {

    private final Logger LOG = LoggerFactory.getLogger(DynamicIpService.class);

    private final IpAddressProvider ipAddressProvider;
    private final DynamicIpProperties dynamicIpProperties;
    private final DnsRecordUpdater dnsRecordUpdater;

    public DynamicIpService(IpAddressProvider ipAddressProvider, DynamicIpProperties dynamicIpProperties, DnsRecordUpdater dnsRecordUpdater) {
        this.ipAddressProvider = ipAddressProvider;
        this.dynamicIpProperties = dynamicIpProperties;
        this.dnsRecordUpdater = dnsRecordUpdater;
    }

    @Scheduled(cron = "${dynamic-ip.cron}")
    public void updateDynamicIp() {
        LOG.info("Starting the update ip job");

        try {
            Set<String> domains = dynamicIpProperties.domains();
            String publicIp = ipAddressProvider.getPublicIp();

            Collection<DnsRecordUpdater.UpdateResult> results = dnsRecordUpdater.update(
                    publicIp,
                    domains
            );

            LOG.info("Successfully completed the update ip[{}] job with results: [{}]", publicIp, results);
        } catch (RuntimeException e) {
            LOG.info("The update ip job failed", e);
        }
    }

}
