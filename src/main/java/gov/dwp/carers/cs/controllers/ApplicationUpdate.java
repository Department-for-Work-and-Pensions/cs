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
    @ResponseBody
    public String claimUpdate(@PathVariable("transactionId") final String transactionId, @PathVariable("status") final String status) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        LOGGER.info("STARTED /claim/{transactionId}/{status} ApplicationUpdate.claimUpdate called with transactionId:{} status:{}", transactionId, status);
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimUpdateService.claimUpdate(transactionId, status);
        } finally {
            LOGGER.info("ENDED /claim/{transactionId}/{status} ApplicationUpdate.claimUpdate called with transactionId:{} status:{}", transactionId, status);
        }
        return response;
    }

    @RequestMapping(value = "/claim/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submitClaim(@RequestBody final String requestBody) {
        RenameThread.getTransactionIdAndRenameThread(requestBody);
        LOGGER.info("STARTED /claim/submit ApplicationUpdate.submitClaim called with requestBody");
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimUpdateService.submitClaim(requestBody);
        } finally {
            LOGGER.info("ENDED /claim/submit ApplicationUpdate.submitClaim called with requestBody");
        }
        return response;
    }

    @RequestMapping(value = "/claim/submit-force-today", method = RequestMethod.POST)
    @ResponseBody
    public String submitClaimForceToday(@RequestBody final String requestBody) {
        RenameThread.getTransactionIdAndRenameThread(requestBody);
        LOGGER.info("STARTED /claim/submit-force-today ApplicationUpdate.submitClaimForceToday called with requestBody");
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimUpdateService.submitClaimForceToday(requestBody);
        } finally {
            LOGGER.info("ENDED /claim/submit-force-today ApplicationUpdate.submitClaimForceToday called with requestBody");
        }
        return response;
    }

    @RequestMapping(value = "/purge/{originTag}", method = RequestMethod.POST)
    @ResponseBody
    public String purge(@PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /purge ApplicationUpdate.purge with originTag:{}", originTag);
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimUpdateService.purge(originTag);
        } finally {
            LOGGER.info("ENDED /purge ApplicationUpdate.purge with originTag:{}", originTag);
        }
        return response;
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
