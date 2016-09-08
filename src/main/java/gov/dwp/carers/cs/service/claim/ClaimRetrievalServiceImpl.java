package gov.dwp.carers.cs.service.claim;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
@Component
public class ClaimRetrievalServiceImpl implements ClaimRetrievalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimRetrievalServiceImpl.class);

    private final DatabaseClaimService databaseClaimService;

    @Override
    public List<ClaimSummary> claimsForDate(final String currentDate, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimsForDate called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        return databaseClaimService.claims(currentDate, originTag);
    }

    @Override
    public String claim(final String transactionId, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claim called with transactionId:" + transactionId + ", originTag:" + originTag);
        }
        return databaseClaimService.fullClaim(transactionId, originTag);
    }

    @Override
    public List<ClaimSummary> circs(final String currentDate, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimsForDate called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        return databaseClaimService.circs(originTag, currentDate);
    }

    @Override
    public List<ClaimSummary> claimsForDateFiltered(final String currentDate, final String status, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimsForDateFiltered called with currentDate:" + currentDate + ", status:" + status + ", originTag:" + originTag);
        }
        return databaseClaimService.claimsFiltered(originTag, currentDate, status);
    }

    @Override
    public List<ClaimSummary> claimsForDateFilteredBySurname(final String currentDate, final String sortBy, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimsForDateFilteredBySurname called with currentDate:" + currentDate + ", sortBy:" + sortBy + ", originTag:" + originTag);
        }
        return databaseClaimService.claimsFilteredBySurname(originTag, currentDate, sortBy);
    }

    @Override
    public Map<String, Long> claimsNumbersFiltered(final String statuses, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("claimsNumbersFiltered called with statuses:" + statuses + ", originTag:" + originTag);
        }
        return databaseClaimService.claimNumbersFiltered(originTag, Arrays.asList(statuses.split(",")));
    }

    @Override
    public  Map<String, TabCount> countOfClaimsForTabs(final String currentDate, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("countOfClaimsForTabs called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        return databaseClaimService.constructClaimSummaryWithTabTotals(originTag, currentDate);
    }

    @Override
    public String render(final String transactionId, final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("render called with transactionId:" + transactionId + ", originTag:" + originTag);
        }
        return databaseClaimService.fullClaim(transactionId, originTag);
    }

    @Override
    public List<List<String>> export(final String originTag) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("export called with originTag:" + originTag);
        }
        return databaseClaimService.export(originTag);
    }

    @Inject
    public ClaimRetrievalServiceImpl(DatabaseClaimService databaseClaimService) {
        this.databaseClaimService = databaseClaimService;
    }
}
