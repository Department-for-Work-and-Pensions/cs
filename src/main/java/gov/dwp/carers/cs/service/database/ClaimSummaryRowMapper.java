package gov.dwp.carers.cs.service.database;

import gov.dwp.carers.cs.model.ClaimSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by peterwhitehead on 05/09/2016.
 */
public class ClaimSummaryRowMapper implements RowMapper<ClaimSummary> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimSummaryRowMapper.class);

    private final transient SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");

    @Override
    public ClaimSummary mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        Long seconds = 0L;
        try {
            seconds = simpleDateFormat.parse(resultSet.getString("claimDateTime")).getTime();
        } catch (ParseException pe) {
            LOGGER.error("Unable to parse date:" + resultSet.getString("claimDateTime"), pe);
        }
        return new ClaimSummary(resultSet.getString("transactionId"),
                resultSet.getString("claimType"), resultSet.getString("nino"),
                resultSet.getString("forename"), resultSet.getString("surname"),
                seconds, resultSet.getString("status"));
    }
}
