package uk.almenares.dynamicip;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dynamicip")
public class DynamicIpController {

    private final DynamicIpService dynamicIpService;

    public DynamicIpController(
            DynamicIpService dynamicIpService
    ) {
        this.dynamicIpService = dynamicIpService;
    }

    @PostMapping("updateAll")
    public void updateAllRecords() {
        dynamicIpService.updateDynamicIp();
    }

}
