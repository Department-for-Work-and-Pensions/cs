package gov.dwp.carers.cs.monitoring;

import gov.dwp.carers.CADSHealthCheck;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
@Component
public class DBHealthCheck extends CADSHealthCheck {
    private final DatabaseClaimService databaseClaimService;

    @Inject
    public DBHealthCheck(final @Value("${application.name}") String applicationName,
                         final @Value("${application.version}") String applicationVersion,
                         final DatabaseClaimService databaseClaimService) {
        super(applicationName, applicationVersion.replace("-SNAPSHOT", ""), "-inspection-db");
        this.databaseClaimService = databaseClaimService;
    }

    @Override
    protected CADSHealthCheck.Result check() {
        CADSHealthCheck.Result rtn;
        try {
            databaseClaimService.health();
            rtn = CADSHealthCheck.Result.healthy();
        } catch (Exception e) {
            rtn = CADSHealthCheck.Result.unhealthy(e);
        }
        return rtn;
    }
}

