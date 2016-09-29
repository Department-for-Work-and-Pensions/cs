package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.cs.service.claim.ClaimRetrievalService;
import gov.dwp.carers.monitor.Counters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import utils.RenameThread;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@RestController
@Component
public class ApplicationRetrieval {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRetrieval.class);

    private final ClaimRetrievalService claimRetrievalService;
    private final Counters counters;
    private final String applicationMetric;

    @RequestMapping(value = "/claims/{currentDate}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody List<ClaimSummary> claimsForDate(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/claims called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.claimsForDate(currentDate, originTag);
    }

    @RequestMapping(value = "/claim/{transactionId}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody String claim(@PathVariable("transactionId") final String transactionId, @PathVariable("originTag") final String originTag) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/claim called with transactionId:" + transactionId + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.claim(transactionId, originTag);
    }

    @RequestMapping(value = "/circs/{currentDate}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody List<ClaimSummary> circs(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/circs called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.circs(currentDate, originTag);
    }

    @RequestMapping(value = "/claims/{currentDate}/{status}/{originTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<ClaimSummary> claimsForDateFiltered(@PathVariable("currentDate") final String currentDate, @PathVariable("status") final String status, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/claims called with currentDate:" + currentDate + ", status:" + status + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.claimsForDateFiltered(currentDate, status, originTag);
    }

    @RequestMapping(value = "/claims/surname/{currentDate}/{sortBy}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody List<ClaimSummary> claimsForDateFilteredBySurname(@PathVariable("currentDate") final String currentDate, @PathVariable("sortBy") final String sortBy, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/claims/surname called with currentDate:" + currentDate + ", sortBy:" + sortBy + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.claimsForDateFilteredBySurname(currentDate, sortBy, originTag);
    }

    @RequestMapping(value = "/counts/{statuses}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Long> claimsNumbersFiltered(@PathVariable("statuses") final String statuses, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/counts called with statuses:" + statuses + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.claimsNumbersFiltered(statuses, originTag);
    }

    @RequestMapping(value = "/countOfClaimsForTabs/{currentDate}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody  Map<String, TabCount> countOfClaimsForTabs(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/countOfClaimsForTabs called with currentDate:" + currentDate + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.countOfClaimsForTabs(currentDate, originTag);
    }

    @RequestMapping(value = "/render/{transactionId}/{originTag}", method = RequestMethod.GET)
    public @ResponseBody String render(@PathVariable("transactionId") final String transactionId, @PathVariable("originTag") final String originTag) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/render called with transactionId:" + transactionId + ", originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.render(transactionId, originTag);
    }

    @RequestMapping(value = "/export/{originTag}", method = RequestMethod.GET)
    public @ResponseBody List<List<String>> export(@PathVariable("originTag") final String originTag) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("/export called with originTag:" + originTag);
        }
        counters.incrementMetric(applicationMetric);
        return claimRetrievalService.export(originTag);
    }

    @Inject
    public ApplicationRetrieval(final ClaimRetrievalService claimRetrievalService,
                                final Counters counters,
                                @Value("${application.metric}") final String applicationMetric) {
        this.claimRetrievalService = claimRetrievalService;
        this.counters = counters;
        this.applicationMetric = applicationMetric;
    }
}
