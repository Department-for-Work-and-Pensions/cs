package gov.dwp.carers.cs.controllers;

import gov.dwp.carers.cs.model.ClaimSummary;
import gov.dwp.carers.cs.model.TabCount;
import gov.dwp.carers.cs.service.claim.ClaimRetrievalService;
import gov.dwp.carers.monitor.Counters;
import gov.dwp.exceptions.DwpRuntimeException;
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
    @ResponseBody
    public List<ClaimSummary> claimsForDate(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /claims/{currentDate}/{originTag} ApplicationRetrieval.claimsForDate called with currentDate:{} originTag:{}", currentDate, originTag);
        List<ClaimSummary> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.claimsForDate(currentDate, originTag);
        } finally {
            LOGGER.info("ENDED /claims/{currentDate}/{originTag} ApplicationRetrieval.claimsForDate called with currentDate:{} originTag:{}", currentDate, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/claim/{transactionId}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public String claim(@PathVariable("transactionId") final String transactionId, @PathVariable("originTag") final String originTag) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        LOGGER.info("STARTED /claims/{transactionId}/{originTag} ApplicationRetrieval.claim called with transactionId:{}", transactionId);
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.claim(transactionId, originTag);
        } finally {
            LOGGER.info("ENDED /claims/{transactionId}/{originTag} ApplicationRetrieval.claim called with transactionId:{}", transactionId);
        }
        return response;
    }

    @RequestMapping(value = "/circs/{currentDate}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public List<ClaimSummary> circs(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /circs/{currentDate}/{originTag} ApplicationRetrieval.circs called with currentDate:{} originTag:{}", currentDate, originTag);
        List<ClaimSummary> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.circs(currentDate, originTag);
        } finally {
            LOGGER.info("ENDED /circs/{currentDate}/{originTag} ApplicationRetrieval.circs called with currentDate:{} originTag:{}", currentDate, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/claims/{currentDate}/{status}/{originTag}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ClaimSummary> claimsForDateFiltered(@PathVariable("currentDate") final String currentDate, @PathVariable("status") final String status, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /claims/{currentDate}/{status}/{originTag} ApplicationRetrieval.claimsForDateFiltered called with currentDate:{} status:{} originTag:{}", currentDate, status, originTag);
        List<ClaimSummary> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.claimsForDateFiltered(currentDate, status, originTag);
        } finally {
            LOGGER.info("ENDED /claims/{currentDate}/{status}/{originTag} ApplicationRetrieval.claimsForDateFiltered called with currentDate:{} status:{} originTag:{}", currentDate, status, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/claims/surname/{currentDate}/{sortBy}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public List<ClaimSummary> claimsForDateFilteredBySurname(@PathVariable("currentDate") final String currentDate, @PathVariable("sortBy") final String sortBy, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /claims/surname/{currentDate}/{sortBy}/{originTag} ApplicationRetrieval.claimsForDateFilteredBySurname called with currentDate:{} sortBy:{} originTag:{}", currentDate, sortBy, originTag);
        List<ClaimSummary> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.claimsForDateFilteredBySurname(currentDate, sortBy, originTag);
        } finally {
            LOGGER.info("ENDED /claims/surname/{currentDate}/{sortBy}/{originTag} ApplicationRetrieval.claimsForDateFilteredBySurname called with currentDate:{} sortBy:{} originTag:{}", currentDate, sortBy, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/counts/{statuses}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Long> claimsNumbersFiltered(@PathVariable("statuses") final String statuses, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /counts/{statuses}/{originTag} ApplicationRetrieval.claimsNumbersFiltered called with statuses:{} originTag:{}", statuses, originTag);
        Map<String, Long> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.claimsNumbersFiltered(statuses, originTag);
        } finally {
            LOGGER.info("ENDED /counts/{statuses}/{originTag} ApplicationRetrieval.claimsNumbersFiltered called with statuses:{} originTag:{}", statuses, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/countOfClaimsForTabs/{currentDate}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TabCount> countOfClaimsForTabs(@PathVariable("currentDate") final String currentDate, @PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED countOfClaimsForTabs/{currentDate}/{originTag} ApplicationRetrieval.countOfClaimsForTabs called with currentDate:{} originTag:{}", currentDate, originTag);
        Map<String, TabCount> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.countOfClaimsForTabs(currentDate, originTag);
        } finally {
            LOGGER.info("ENDED countOfClaimsForTabs/{currentDate}/{originTag} ApplicationRetrieval.countOfClaimsForTabs called with currentDate:{} originTag:{}", currentDate, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/render/{transactionId}/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public String render(@PathVariable("transactionId") final String transactionId, @PathVariable("originTag") final String originTag) {
        RenameThread.renameThreadFromTransactionId(transactionId);
        LOGGER.info("STARTED /render/{transactionId}/{originTag} ApplicationRetrieval.render called with transactionId:{} originTag:{}", transactionId, originTag);
        String response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.render(transactionId, originTag);
        } finally {
            LOGGER.info("ENDED /render/{transactionId}/{originTag} ApplicationRetrieval.render called with transactionId:{} originTag:{}", transactionId, originTag);
        }
        return response;
    }

    @RequestMapping(value = "/export/{originTag}", method = RequestMethod.GET)
    @ResponseBody
    public List<List<String>> export(@PathVariable("originTag") final String originTag) {
        LOGGER.info("STARTED /render/{originTag} ApplicationRetrieval.export called with originTag:{}", originTag);
        List<List<String>> response = null;
        try {
            counters.incrementMetric(applicationMetric);
            response = claimRetrievalService.export(originTag);
        } finally {
            LOGGER.info("ENDED /render/{originTag} ApplicationRetrieval.export called with originTag:{}", originTag);
        }
        return response;
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
