package gov.dwp.carers.cs.monitoring;

import gov.dwp.carers.CADSHealthCheck;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * Created by peterwhitehead on 28/06/2016.
 */
@Component
public class DrConnectionCheck extends CADSHealthCheck {

    private final Environment environment;
    private final RestTemplate restTemplate;

    @Inject
    public DrConnectionCheck(final @Value("${application.name}") String applicationName,
                             final @Value("${application.version}") String applicationVersion,
                             final Environment environment,
                             final RestTemplate restTemplate) {
        super(applicationName, applicationVersion.replace("-SNAPSHOT", ""), "-connection-dr");
        this.environment = environment;
        this.restTemplate = restTemplate;
    }

    @Override
    protected Result check() {
        Result rtnMsg;
        try {
            final String submissionServerEndpoint = environment.getProperty("dr.url") + "/ping";
            final ResponseEntity<String> response = restTemplate.getForEntity(submissionServerEndpoint, String.class);
            if (response.getStatusCode().value() == HttpStatus.SC_OK) {
                rtnMsg = Result.healthy();
            } else {
                rtnMsg = Result.unhealthy("dr ping failed: " + response.getStatusCode().value() + ".");
            }
        } catch (Exception e) {
            rtnMsg = Result.unhealthy(e);
        }
        return rtnMsg;
    }
}
