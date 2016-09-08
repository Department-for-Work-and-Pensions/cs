package gov.dwp.carers.cs.service.claim;

/**
 * Created by peterwhitehead on 26/08/2016.
 */
public interface ClaimUpdateService {
    String claimUpdate(final String transactionId, final String status);
    String submitClaim(final String requestBody);
    String submitClaimForceToday(final String requestBody);
    String purge(final String originTag);
}
