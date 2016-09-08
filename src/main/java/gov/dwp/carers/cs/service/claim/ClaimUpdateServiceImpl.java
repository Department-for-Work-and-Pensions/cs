package gov.dwp.carers.cs.service.claim;

import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import gov.dwp.carers.cs.service.messaging.DrSubmitter;
import gov.dwp.carers.xml.helpers.XMLExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
@Component
public class ClaimUpdateServiceImpl implements ClaimUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimUpdateServiceImpl.class);

    private final DatabaseClaimService databaseClaimService;
    private final DrSubmitter drSubmitter;
    private final Boolean dfEnabled;
    private final XMLExtractor xmlExtractor;;

    @Override
    public String claimUpdate(final String transactionId, final String status) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimUpdate called with transactionId:" + transactionId + ", status:" + status);
        }
        String rtn = "Success";
        if (!databaseClaimService.updateClaim(transactionId, status)) {
            rtn = "Failure";
        }
        return rtn;
    }

    @Override
    public String submitClaim(final String requestBody) {
        return submitClaim(requestBody, false);
    }

    @Override
    public String submitClaimForceToday(final String requestBody) {
        return submitClaim(requestBody, true);
    }

    @Override
    public String purge(final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("purge called with originTag:" + originTag);
        }
        String rtn = "Success";
        if (!databaseClaimService.purge(originTag)) {
            rtn = "Failure";
        }
        return rtn;
    }

    private String submitClaim(final String xml, final Boolean force) {
        LOGGER.debug("submitClaim called.");
        final String originTag = xmlExtractor.extractSingleElement(xml, "Origin");
        final String transactionId = xmlExtractor.extractTransactionId(xml);
        Boolean result = databaseClaimService.submitMessage(xml, force, originTag, transactionId);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("originTag:" + originTag);
        }
        // submit to drs only if it is enabled
        if (result && dfEnabled && "GB".equals(originTag)) {
            drSubmitter.drSubmit(xml, transactionId);
        }
        return "";
    }

    @Inject
    public ClaimUpdateServiceImpl(final DatabaseClaimService databaseClaimService,
                                  final DrSubmitter drSubmitter,
                                  @Value("${df.enabled}") final Boolean dfEnabled,
                                  final XMLExtractor xmlExtractor) {
        this.databaseClaimService = databaseClaimService;
        this.drSubmitter = drSubmitter;
        this.dfEnabled = dfEnabled;
        this.xmlExtractor = xmlExtractor;
    }
}
