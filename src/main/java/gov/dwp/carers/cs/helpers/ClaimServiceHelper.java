package gov.dwp.carers.cs.helpers;

import gov.dwp.carers.security.encryption.EncryptorAES;
import gov.dwp.carers.xml.helpers.XMLExtractor;
import gov.dwp.carers.xml.helpers.XmlSchemaDecryptor;
import gov.dwp.exceptions.DwpRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by peterwhitehead on 06/09/2016.
 */
@Component
public class ClaimServiceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimServiceHelper.class);

    private final EncryptorAES encryptorAES;
    private final XmlSchemaDecryptor xmlSchemaDecryptor;
    private final XMLExtractor xmlExtractor;
    public static final String STATUS_KEY = "status";
    public static final String CLAIM_DATETIME_KEY = "claimDateTime";
    public static final String SORT_KEY = "sortby";
    public static final String CLAIM_TYPE_KEY = "claimType";
    public static final String NINO_KEY = "nino";
    public static final String FORENAME_KEY = "forename";
    public static final String SURNAME_KEY = "surname";

    public String getSurname(final Document doc, final String claimType) {
        String surname = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction","DWPCAClaim","Claimant","Surname","Answer"));
        if ("circs".equals(claimType)) {
            surname = "";
        }
        return surname;
    }

    public String getForename(final Document doc, final String claimType) {
        String forename = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction","DWPCAClaim","Claimant","OtherNames","Answer"));
        if ("circs".equals(claimType)) {
            forename = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction", "DWPCAChangeOfCircumstances", "ClaimantDetails", "FullName", "Answer"));
        }
        return forename;
    }

    public String getNino(final Document doc, final String claimType) {
        String nino = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction","DWPCAClaim","Claimant","NationalInsuranceNumber","Answer"));
        if ("circs".equals(claimType)) {
            nino = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction","DWPCAChangeOfCircumstances","ClaimantDetails","NationalInsuranceNumber","Answer"));
        }
        return nino;
    }

    public String getTimeGenerated(final Document doc, final Boolean forceToday) {
        String dateTimeGenerated;
        if (forceToday) {
            dateTimeGenerated = DateTimeFormatter.ofPattern("ddMMyyyyHHmm").format(LocalDateTime.now());
        } else {
            dateTimeGenerated = xmlExtractor.getTextFromXmlNode(doc, "DWPCATransaction", Arrays.asList("DWPCATransaction","DateTimeGenerated"));
            dateTimeGenerated = dateTimeGenerated.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
        }
        return dateTimeGenerated;
    }

    public String getClaimType(final Document doc) {
        String claimType = "claim";
        if (doc.getElementsByTagName("DWPCAChangeOfCircumstances").getLength() > 0) {
            claimType = "circs";
        }
        return claimType;
    }

    public String getSortBy(final String surname) {
        final String sortBy = surname.isEmpty() ? "" : decryptString(surname).trim().substring(0, 1).toLowerCase();
        return sortBy;
    }

    public Map<String, String> getClaimSummaryKeyValue(final String xml, final Boolean forceToday) {
        Document doc;
        try {
            doc = xmlSchemaDecryptor.createDocumentFromXML(xml);
        } catch (Exception e) {
            LOGGER.error("Unable to create document model from xml, error:" + e.getMessage(), e);
            throw new DwpRuntimeException(e);
        }

        final String claimType = getClaimType(doc);
        final String surname = getSurname(doc, claimType);
        final String status = "received";

        final Map<String, String> summaryKeyValues = new ConcurrentHashMap<>();
        summaryKeyValues.put(CLAIM_TYPE_KEY, claimType);
        summaryKeyValues.put(NINO_KEY, getNino(doc, claimType));
        summaryKeyValues.put(FORENAME_KEY, getForename(doc, claimType));
        summaryKeyValues.put(SURNAME_KEY, surname);

        summaryKeyValues.put(CLAIM_DATETIME_KEY, getTimeGenerated(doc, forceToday));
        summaryKeyValues.put(STATUS_KEY, status);
        summaryKeyValues.put(SORT_KEY, getSortBy(surname));
        return summaryKeyValues;
    }

    private String decryptString(final String text) {
        try {
            return encryptorAES.decrypt(DatatypeConverter.parseBase64Binary(text));
        } catch (DwpRuntimeException e) {
            LOGGER.error("Could not decrypt node, error:" + e.getMessage(), e);
            throw e;
        }
    }

    public ClaimServiceHelper(final XmlSchemaDecryptor xmlSchemaDecryptor, final XMLExtractor xmlExtractor) {
        this.encryptorAES = new EncryptorAES();
        this.xmlSchemaDecryptor = xmlSchemaDecryptor;
        this.xmlExtractor = xmlExtractor;
    }
}
