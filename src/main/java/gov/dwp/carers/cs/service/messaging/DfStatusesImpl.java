package gov.dwp.carers.cs.service.messaging;

import gov.dwp.carers.cs.MessageDistributionException;
import gov.dwp.carers.cs.model.ClaimStatus;
import gov.dwp.carers.cs.model.ClaimSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
@Component
public class DfStatusesImpl implements DfStatuses {
    private static final Logger LOGGER = LoggerFactory.getLogger(DfStatusesImpl.class);

    private final RestTemplate restTemplate;
    private final Boolean dfEnabled;
    private final String dfUrl;

    @Override
    public List<ClaimSummary> getDfStatuses(final List<ClaimSummary> claimSummaries) {
        if (dfEnabled) {
            final List<ClaimStatus> claimStatuses = getStatuses(claimSummaries);
            claimStatuses.forEach(claimStatus ->
                    claimSummaries.stream()
                            .filter(claimSummary -> claimSummary.getTransactionId().equals(claimStatus.getId()))
                            .findFirst()
                            .ifPresent(claimSummary -> claimSummary.setDrsStatus(claimStatus.getDesc())
                            )
            );
        }
        return claimSummaries;
    }

    private List<ClaimStatus> getStatuses(final List<ClaimSummary> claimSummaries) {
        final List<String> transactionIds = claimSummaries.stream().map(claimSummary -> claimSummary.getTransactionId()).collect(Collectors.toList());
        final HttpHeaders headers = new HttpHeaders();
        final MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        final HttpEntity<List<String>> request = new HttpEntity<>(transactionIds, headers);
        try {
            ResponseEntity<List<ClaimStatus>> responseEntity = restTemplate.exchange(dfUrl + "/statuses", HttpMethod.POST, request, new ParameterizedTypeReference<List<ClaimStatus>>() {});
            processResponse(responseEntity);
            return responseEntity.getBody();
        } catch (RestClientException rce) {
            LOGGER.error("DF is unavailable! " + rce.getMessage() + ".", rce);
            throw new MessageDistributionException("DF is unavailable! " + rce.getMessage() + ".", rce);
        } catch (MessageDistributionException mde) {
            LOGGER.error("Status retrieval from DF failed. " + mde.getMessage() + ".", mde);
            throw mde;
        } catch (Exception e) {
            LOGGER.error("Status retrieval from DF failed.", e);
            throw new MessageDistributionException("Status retrieval from DF failed! " + e.getMessage() + ".", e);
        }
    }

    private Boolean processResponse(final ResponseEntity<List<ClaimStatus>> response) {
        final Integer responseStatus = response.getStatusCode().value();
        switch (responseStatus) {
            case org.apache.http.HttpStatus.SC_OK:
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Successfully retrieve transaction statues.");
                }
                break;
            case org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT:
                LOGGER.error("DF response - REQUEST_TIMEOUT: " + responseStatus + ": "+ response.toString() + ".");
                throw new MessageDistributionException("Failed distribution because of a timeout.");
            case org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE:
                LOGGER.error("DF response - SERVICE_UNAVAILABLE: " + responseStatus + ": "+ response.toString() + ".");
                throw new MessageDistributionException("DF is Unavailable.");
            case org.apache.http.HttpStatus.SC_BAD_REQUEST:
                LOGGER.error("DF response - BAD_REQUEST: " + responseStatus + ": "+ response.toString() + ". This should not happen.");
                throw new MessageDistributionException("DF is BAD REQUEST.");
            default:
                LOGGER.error("DF response: " + responseStatus + " : " + response.toString() + ".");
                throw new MessageDistributionException("DF returned error: " + responseStatus + " : " + response.toString() + ".");
        }
        return Boolean.TRUE;
    }

    @Inject
    public DfStatusesImpl(final RestTemplate restTemplate,
                          @Value("${df.enabled}") final Boolean dfEnabled,
                          @Value("${df.url}") final String dfUrl) {
        this.restTemplate = restTemplate;
        this.dfEnabled = dfEnabled;
        this.dfUrl = dfUrl;
    }
}
