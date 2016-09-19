package gov.dwp.carers.cs.helpers;

import gov.dwp.carers.helper.TestMessage;
import gov.dwp.carers.xml.helpers.XMLExtractor;
import gov.dwp.carers.xml.helpers.XmlSchemaDecryptor;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
public class ClaimServiceHelperTest {
    private ClaimServiceHelper claimServiceHelper;
    private XmlSchemaDecryptor xmlSchemaDecryptor;
    private String msg;
    private Document doc;

    @Before
    public void setUp() throws Exception {
        xmlSchemaDecryptor = new XmlSchemaDecryptor();
        givenMessageHasArrived(TestMessage.ValidXMLWithRSASignature.getFileName());
        claimServiceHelper = new ClaimServiceHelper(xmlSchemaDecryptor, new XMLExtractor());
    }

    @Test
    public void testGetSurname() throws Exception {
        assertThat(claimServiceHelper.getSurname(doc, "claim"), is("kReSbgCqmecC811wWBAJRTNvSdCakyF/wb2smR5nO4CdqjtR/q1V8NSb/JWZM7Ju"));
    }

    @Test
    public void testGetForename() throws Exception {
        assertThat(claimServiceHelper.getForename(doc, "claim"), is("Joe"));
    }

    @Test
    public void testGetNino() throws Exception {
        assertThat(claimServiceHelper.getNino(doc, "claim"), is("xp603+PDI2247ojS0arQR7e/OLkrfGneVyiSgLxE8gsP7WKthywHGJwkVQZamGQv"));
    }

    @Test
    public void testGetTimeGeneratedForce() throws Exception {
        final String date = DateTimeFormatter.ofPattern("ddMMyyyyHHmm").format(LocalDateTime.now());
        assertThat(claimServiceHelper.getTimeGenerated(doc, true), is(date));
    }

    @Test
    public void testGetTimeGenerated() throws Exception {
        assertThat(claimServiceHelper.getTimeGenerated(doc, false), is("110720161641"));
    }

    @Test
    public void testGetClaimType() throws Exception {
        assertThat(claimServiceHelper.getClaimType(doc), is("claim"));
    }

    @Test
    public void testGetSortBy() throws Exception {
        assertThat(claimServiceHelper.getSortBy("kReSbgCqmecC811wWBAJRTNvSdCakyF/wb2smR5nO4CdqjtR/q1V8NSb/JWZM7Ju"), is("b"));
    }

    @Test
    public void testGetClaimSummaryKeyValue() throws Exception {
        final Map<String, String> summaryKeyValues = createSummaryKeyValue("110720161641");
        final Map<String, String> summaryKeyValuesNew = claimServiceHelper.getClaimSummaryKeyValue(msg, false);
        org.assertj.core.api.Assertions.assertThat(summaryKeyValues).containsAllEntriesOf(summaryKeyValuesNew);
    }

    @Test
    public void testGetClaimSummaryKeyValueForce() throws Exception {
        final String date = DateTimeFormatter.ofPattern("ddMMyyyyHHmm").format(LocalDateTime.now());
        final Map<String, String> summaryKeyValues = createSummaryKeyValue(date);
        final Map<String, String> summaryKeyValuesNew = claimServiceHelper.getClaimSummaryKeyValue(msg, true);
        org.assertj.core.api.Assertions.assertThat(summaryKeyValues).containsAllEntriesOf(summaryKeyValuesNew);
    }

    private void givenMessageHasArrived(final String fileName) throws Exception {
        this.msg = TestUtils.loadXmlFromFile(fileName);
        doc = xmlSchemaDecryptor.createDocumentFromXML(msg);
    }

    private Map<String, String> createSummaryKeyValue(final String date) {
        final Map<String, String> summaryKeyValues = new ConcurrentHashMap<>();
        summaryKeyValues.put("claimType", "claim");
        summaryKeyValues.put("forename", "Joe");
        summaryKeyValues.put("surname", "kReSbgCqmecC811wWBAJRTNvSdCakyF/wb2smR5nO4CdqjtR/q1V8NSb/JWZM7Ju");
        summaryKeyValues.put("claimDateTime", date);
        summaryKeyValues.put("sortby", "b");
        summaryKeyValues.put("nino", "xp603+PDI2247ojS0arQR7e/OLkrfGneVyiSgLxE8gsP7WKthywHGJwkVQZamGQv");
        summaryKeyValues.put("status", "received");
        return summaryKeyValues;
    }
}