package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.cs.model.ClaimSummaryCount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
public class ClaimSummaryCountRowMapper implements RowMapper<ClaimSummaryCount> {
    @Override
    public ClaimSummaryCount mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        return new ClaimSummaryCount(resultSet.getString("daysDate"), resultSet.getLong("dailyCount"));
    }
}
