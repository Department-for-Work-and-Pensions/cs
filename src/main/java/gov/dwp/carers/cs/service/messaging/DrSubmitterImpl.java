package gov.dwp.carers.cs.service.messaging;

import gov.dwp.carers.cs.MessageDistributionException;
import gov.dwp.carers.cs.helpers.ClaimServiceStatus;
import gov.dwp.carers.cs.service.database.DatabaseClaimService;
import gov.dwp.carers.monitor.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Created by peterwhitehead on 30/06/2016.
 */
@Component
public class DrSubmitterImpl implements DrSubmitter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrSubmitterImpl.class);

    private final RestTemplate restTemplate;
    private final DatabaseClaimService databaseClaimService;
    private final Counters counters;
    private final String url;
    private final String errorMetric;

    @Override
    public Boolean drSubmit(final String msg, final String transactionId) throws MessageDistributionException {
        Boolean rtn = Boolean.FALSE;
        try {
            final HttpHeaders headers = new HttpHeaders();
            final MediaType mediaType = new MediaType("application", "xml", StandardCharsets.UTF_8);
            headers.setContentType(mediaType);
            final HttpEntity<String> request = new HttpEntity<>(msg, headers);
            final ResponseEntity<String> response = restTemplate.exchange(url + "/submission", HttpMethod.POST, request, String.class);
            rtn = processResponse(response, transactionId);
        } catch (RestClientException rce) {
            updateStatus(transactionId, ClaimServiceStatus.FAILED_TO_SEND.getStatus());
            counters.incrementMetric(errorMetric + ClaimServiceStatus.FAILED_TO_SEND.getStatus());
            LOGGER.error("DR Received is unavailable! " + rce.getMessage() + ". transactionId [" + transactionId + "].", rce);
            throw new MessageDistributionException("DR Received is unavailable! " + rce.getMessage() + ".", rce);
        } catch (MessageDistributionException mde) {
            updateStatus(transactionId, ClaimServiceStatus.FAILED_TO_SEND.getStatus());
            LOGGER.error("Distribution to DR Received failed. " + mde.getMessage() + ". transactionId [" + transactionId + "].", mde);
            throw mde;
        } catch (Exception e) {
            counters.incrementMetric(errorMetric + ClaimServiceStatus.FAILED_TO_SEND);
            LOGGER.error("Distribution to DR Received failed. transactionId [" + transactionId + "]", e);
            updateStatus(transactionId, ClaimServiceStatus.FAILED_TO_SEND.getStatus());
        }
        return rtn;
    }

    private Boolean processResponse(final ResponseEntity<String> response, final String transactionId) {
        final Integer responseStatus = response.getStatusCode().value();
        switch (responseStatus) {
            case org.apache.http.HttpStatus.SC_OK:
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Successfully distributed transactionId [" + transactionId + "]");
                }
                updateStatus(transactionId, ClaimServiceStatus.SUCCESSFULLY_SENT.getStatus());
                break;
            case org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT:
                LOGGER.error("DR Received response - REQUEST_TIMEOUT: " + responseStatus + ": "+ response.toString() + " transactionId [" + transactionId + "].");
                counters.incrementMetric(errorMetric + responseStatus);
                throw new MessageDistributionException("Failed distribution because of a timeout.");
            case org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE:
                LOGGER.error("DR Received response - SERVICE_UNAVAILABLE: " + responseStatus + ": "+ response.toString() + ". transactionId [" + transactionId + "].");
                counters.incrementMetric(errorMetric + responseStatus);
                throw new MessageDistributionException("DR Received is Unavailable.");
            case org.apache.http.HttpStatus.SC_BAD_REQUEST:
                LOGGER.error("DR Received response - BAD_REQUEST: " + responseStatus + ": "+ response.toString() + ". transactionId [" + transactionId + "]. This should not happen. Probably a discrepancy between ingress database and claim database (transactionIds). Claim stored in suspicious table.");
                counters.incrementMetric(errorMetric + responseStatus);
                throw new MessageDistributionException("DR Received returned bad request.");
            default:
                LOGGER.error("DR Received response: " + responseStatus + " : " + response.toString() +". transactionId [" + transactionId + "].");
                counters.incrementMetric(errorMetric + responseStatus);
                throw new MessageDistributionException("DR Received returned error: " + responseStatus + " : " + response.toString() +". transactionId [" + transactionId + "].");
        }
        return Boolean.TRUE;
    }

    private void updateStatus(final String transactionId, final Integer status) {
        try {
            databaseClaimService.updateStatus(transactionId, status);
        } catch (SQLException e) {
            LOGGER.error("Unable to set status[" + status + "] on transactionId[" + transactionId + "].", e);
            throw new MessageDistributionException("Unable to set status[" + status + "] on transactionId[" + transactionId + "].", e);
        }
    }

    @Inject
    public DrSubmitterImpl(final RestTemplate restTemplate,
                           final DatabaseClaimService databaseClaimService,
                           final Counters counters,
                           @Value("${dr.url}") final String url,
                           @Value("${rest-error-metric}") final String errorMetric) {
        this.restTemplate = restTemplate;
        this.databaseClaimService = databaseClaimService;
        this.counters = counters;
        this.url = url;
        this.errorMetric = errorMetric;
    }
}
