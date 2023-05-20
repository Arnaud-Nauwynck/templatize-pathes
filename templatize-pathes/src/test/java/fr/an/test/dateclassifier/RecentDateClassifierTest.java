package fr.an.test.dateclassifier;

import java.time.LocalDate;

import fr.an.test.dateclassifier.NamedDateRange;
import fr.an.test.dateclassifier.RecentDateClassifier;
import fr.an.test.dateclassifier.RecentDateClassifier.NamedRelativeDateRange;
import org.junit.Assert;
import org.junit.Test;

public class RecentDateClassifierTest {

    private static final LocalDate testDate = LocalDate.of(2023, 1, 24);
    private static final RecentDateClassifier sut = new RecentDateClassifier(testDate,
            NamedRelativeDateRange.createDefaultSplits());

    @Test
    public void testPrintDates() {
        for(NamedDateRange range : sut.getDateRanges()) {
            System.out.println(range);
        }
    }

    @Test
    public void testDates() {
        assertFindRange("future", testDate.plusDays(1));
        assertFindRange("d", testDate);

        assertFindRange("d-1", testDate.minusDays(1));
        assertFindRange("d-2", testDate.minusDays(2));
        assertFindRange(">=-1W", testDate.minusDays(3));
        assertFindRange(">=-1W", testDate.minusDays(6));
        assertFindRange(">=-1W", testDate.minusDays(7));

        assertFindRange(">=-2W", testDate.minusDays(8));
        assertFindRange(">=-2W", testDate.minusDays(2*7-1));
        assertFindRange(">=-2W", testDate.minusDays(2*7));

        assertFindRange(">=-1M", testDate.minusDays(2*7+1));
        assertFindRange(">=-1M", testDate.minusMonths(1).plusDays(1));
        assertFindRange(">=-1M", testDate.minusMonths(1));

        assertFindRange(">=-3M", testDate.minusMonths(1).minusDays(1));
        assertFindRange(">=-3M", testDate.minusMonths(3).plusDays(1));
        assertFindRange(">=-3M", testDate.minusMonths(3));

        assertFindRange(">=-6M", testDate.minusMonths(3).minusDays(1));
        assertFindRange(">=-6M", testDate.minusMonths(6).plusDays(1));
        assertFindRange(">=-6M", testDate.minusMonths(6));

        assertFindRange(">=-1Y", testDate.minusMonths(6).minusDays(1));
        assertFindRange(">=-1Y", testDate.minusYears(1).plusDays(1));
        assertFindRange(">=-1Y", testDate.minusYears(1));

        assertFindRange(">=-2Y", testDate.minusYears(1).minusDays(1));
        assertFindRange(">=-2Y", testDate.minusYears(2).plusDays(1));
        assertFindRange(">=-2Y", testDate.minusYears(2));

        assertFindRange("<-2Y", testDate.minusYears(2).minusDays(1));
        assertFindRange("<-2Y", testDate.minusYears(3));
    }

    private void assertFindRange(String expected, LocalDate day) {
        NamedDateRange range = sut.findRange(day);
        Assert.assertEquals(expected, range.name);
    }

}