package gov.dwp.carers.cs.service.database;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
public class ClaimSummaryRowListMapper implements RowMapper<List<String>> {
    @Override
    public List<String> mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        final List<String> summary = new ArrayList<>();
        summary.add(resultSet.getString("nino"));
        summary.add(resultSet.getString("claimDateTime"));
        summary.add(resultSet.getString("claimType"));
        summary.add(resultSet.getString("surname"));
        summary.add(resultSet.getString("sortby"));
        summary.add(resultSet.getString("status"));
        summary.add(resultSet.getString("forename"));
        return summary;
    }
}
