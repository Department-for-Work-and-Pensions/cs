package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.monitor.MonitorRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@Component
public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

    private final MonitorRegistration monitorRegistration;

    private static final String ERROR = "Failed health check.";

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public
    @ResponseBody
    String ping() {
        LOGGER.info("STARTED /ping HealthController.ping");
        LOGGER.info("ENDED /ping HealthController.ping");
        return "";
    }

    @RequestMapping(value = "/report/health", method = RequestMethod.GET)
    @ResponseBody
    public String health() {
        LOGGER.info("STARTED /report/health HealthController.health");
        String response = ERROR;
        try {
            final String health = monitorRegistration.retrievePrintFriendlyHealthCheck();
            if (health != null) {
                response = health;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to report health.", e);
        } finally {
            LOGGER.info("ENDED /report/health HealthController.health");
        }
        return response;
    }

    @Inject
    public HealthController(final MonitorRegistration monitorRegistration) {
        this.monitorRegistration = monitorRegistration;
    }
}
