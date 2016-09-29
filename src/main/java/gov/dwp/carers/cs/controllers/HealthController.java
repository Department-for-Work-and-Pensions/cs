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

@RestController
@Component
public class HealthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

    private final MonitorRegistration monitorRegistration;

    private static final String ERROR = "Failed health check.";

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public @ResponseBody String ping() {
        return "";
    }

    @RequestMapping(value = "/report/health", method = RequestMethod.GET)
    public @ResponseBody String health() {
        String msg = ERROR;
        try {
            final String health = monitorRegistration.retrievePrintFriendlyHealthCheck();
            if (health !=  null) {
                msg = health;
            }
        } catch (Exception e) {
            LOGGER.error(ERROR, e);
        }
        return msg;
    }

    @Inject
    public HealthController(final MonitorRegistration monitorRegistration) {
        this.monitorRegistration = monitorRegistration;
    }
}
