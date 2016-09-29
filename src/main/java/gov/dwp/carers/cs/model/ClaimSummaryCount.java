package gov.dwp.carers.cs.model;

/**
 * Created by peterwhitehead on 06/09/2016.
 */
public class ClaimSummaryCount {
    private String day;
    private Long count;

    public ClaimSummaryCount(final String day, final Long count) {
        this.day = day;
        this.count = count;
    }

    public String getDay() {
        return day;
    }

    public void setDay(final String day) {
        this.day = day;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(final Long count) {
        this.count = count;
    }
}
