package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.cs.model.ClaimSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.ResultSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by peterwhitehead on 12/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClaimSummaryRowMapperTest {
    private ClaimSummaryRowMapper claimSummaryRowMapper;

    @Mock
    ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        claimSummaryRowMapper = new ClaimSummaryRowMapper();
    }

    @Test
    public void testMapRow() throws Exception {
        when(resultSet.getString("transactionId")).thenReturn("1610000234");
        when(resultSet.getString("claimDateTime")).thenReturn("140920160909");
        when(resultSet.getString("claimType")).thenReturn("claim");
        when(resultSet.getString("nino")).thenReturn("AB123456B");
        when(resultSet.getString("forename")).thenReturn("fred");
        when(resultSet.getString("surname")).thenReturn("bieber");
        when(resultSet.getString("status")).thenReturn("received");
        ClaimSummary claimSummary = new ClaimSummary("1610000234", "claim", "AB123456B", "fred", "bieber", 1473840540000L, "received");
        org.assertj.core.api.Assertions.assertThat(claimSummaryRowMapper.mapRow(resultSet, 1)).isEqualToComparingFieldByField(claimSummary);
    }

    @Test(expected = NullPointerException.class)
    public void testMapRowThrowsException() throws Exception {
        when(resultSet.getString("transactionId")).thenReturn("1610000234");
        when(resultSet.getString("claimDateTime1")).thenReturn("140920160909");
        claimSummaryRowMapper.mapRow(resultSet, 1);
    }
}