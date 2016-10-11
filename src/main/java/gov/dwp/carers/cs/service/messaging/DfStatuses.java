package gov.dwp.carers.cs.service.messaging;

import gov.dwp.carers.cs.model.ClaimSummary;

import java.util.List;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
public interface DfStatuses {
    List<ClaimSummary> getDfStatuses(final List<ClaimSummary> claimSummaries);
}
