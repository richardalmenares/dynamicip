package uk.almenares.dynamicip;

import java.util.Collection;
import java.util.Set;

public interface DnsRecordUpdater {

    Collection<UpdateResult> update(String publicIpAddress, Set<String> domains);

    record UpdateResult(
            String domain,
            String previousIp,
            String updatedIp
    ){}

}
