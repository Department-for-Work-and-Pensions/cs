package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.service.claim.ClaimUpdateService;
import gov.dwp.carers.monitor.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import utils.RenameThread;

import javax.inject.Inject;

@RestController
@Component
public class ApplicationUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationUpdate.class);

    private final ClaimUpdateService claimUpdateService;
    private final Counters counters;
    private final String applicationMetric;

    @RequestMapping(value = "/claim/{transactionId}/{status}", method = RequestMethod.PUT)
    public @ResponseBody String claimUpdate(@PathVariable("transactionId") final String transactionId, @PathVariable("status") final String status) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        LOGGER.info("/claim called with transactionId:" + transactionId + ",status:" + status);
        counters.incrementMetric(applicationMetric);
        return claimUpdateService.claimUpdate(transactionId, status);
    }

    @RequestMapping(value = "/claim/submit", method = RequestMethod.POST)
    public @ResponseBody String submitClaim(@RequestBody final String requestBody) {
        RenameThread.getTransactionIdAndRenameThread(requestBody);
        LOGGER.info("/claim/submit called");
        counters.incrementMetric(applicationMetric);
        return claimUpdateService.submitClaim(requestBody);
    }

    @RequestMapping(value = "/claim/submit-force-today", method = RequestMethod.POST)
    public @ResponseBody String submitClaimForceToday(@RequestBody final String requestBody) {
        RenameThread.getTransactionIdAndRenameThread(requestBody);
        LOGGER.info("/claim/submit-force-today called");
        counters.incrementMetric(applicationMetric);
        return claimUpdateService.submitClaimForceToday(requestBody);
    }

    @RequestMapping(value = "/purge/{originTag}", method = RequestMethod.POST)
    public @ResponseBody String purge(@PathVariable("originTag") final String originTag) {
        LOGGER.info("/purge called with originTag:" + originTag);
        counters.incrementMetric(applicationMetric);
        return claimUpdateService.purge(originTag);
    }

    @Inject
    public ApplicationUpdate(final ClaimUpdateService claimUpdateService,
                             final Counters counters,
                             @Value("${application.metric}") final String applicationMetric) {
        this.claimUpdateService = claimUpdateService;
        this.counters = counters;
        this.applicationMetric = applicationMetric;
    }
}
