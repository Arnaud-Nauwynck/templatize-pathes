package fr.an.test.dateclassifier;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecentDateClassifier {

    private LocalDate relativeDate;

    private final NamedDateRange[] dateRanges;

    private final int rangeIndexOfD;
    private LocalDate relativeDminus1;
    private final int rangeIndexOfDminus1;

    public static class NamedRelativeDateRange {
        public final String name;
        public final String detailed;
        public final boolean fromIncluded;
        public final boolean toIncluded;
        public final Period fromDateShift;
        public final Period toDateShift;

        public NamedRelativeDateRange(String name, String detailed, boolean fromIncluded, boolean toIncluded, Period fromDateShift, Period toDateShift) {
            this.name = name;
            this.detailed = detailed;
            this.fromIncluded = fromIncluded;
            this.toIncluded = toIncluded;
            this.fromDateShift = fromDateShift;
            this.toDateShift = toDateShift;
        }

        public static NamedRelativeDateRange[] createDefaultSplits() {
            return new NamedRelativeDateRange[] {
                    new NamedRelativeDateRange("<-2Y", ")-inf, -2y(",  false, false, Period.ofDays(Integer.MIN_VALUE), Period.ofYears(-2)),
                    new NamedRelativeDateRange(">=-2Y", "[-2Y, -1Y(", true, false, Period.ofYears(-2), Period.ofYears(-1)),
                    new NamedRelativeDateRange(">=-1Y", "[-1Y, -6M(", true, false, Period.ofYears(-1), Period.ofMonths(-6)),
                    new NamedRelativeDateRange(">=-6M", "[-6m, -3M(", true, false, Period.ofMonths(-6), Period.ofMonths(-3)),
                    new NamedRelativeDateRange(">=-3M", "[-3M, -1M(", true, false, Period.ofMonths(-3), Period.ofMonths(-1)),
                    new NamedRelativeDateRange(">=-1M", "[-1M, -2W(", true, false, Period.ofMonths(-1), Period.ofDays(-14)),
                    new NamedRelativeDateRange(">=-2W", "[-2W, -1W(", true, false, Period.ofDays(-14), Period.ofDays(-7)),
                    new NamedRelativeDateRange(">=-1W", "[-1W, d-2(", true, false, Period.ofDays(-7), Period.ofDays(-2)),
                    new NamedRelativeDateRange("d-2", "d-2", true, false, Period.ofDays(-2), Period.ofDays(-1)),
                    new NamedRelativeDateRange("d-1", "d-1", true, false, Period.ofDays(-1), Period.ZERO),
                    new NamedRelativeDateRange("d", "d", true, false, Period.ZERO, Period.ofDays(+1)),
                    new NamedRelativeDateRange("future", "[d+1, inf(", true, false, Period.ofDays(+1), Period.ofDays(Integer.MAX_VALUE)),
            };
        }
    }


    //---------------------------------------------------------------------------------------------

    public RecentDateClassifier(LocalDate relativeDate, NamedRelativeDateRange[] splits) {
        this.relativeDate = relativeDate;
        this.dateRanges = computeDateRangesFor(relativeDate, splits);
        this.rangeIndexOfD = naiveFindRangeIndex(relativeDate);
        this.relativeDminus1 = relativeDate.minusDays(1);
        this.rangeIndexOfDminus1 = naiveFindRangeIndex(relativeDminus1);
    }

    private static NamedDateRange[] computeDateRangesFor(LocalDate relativeDate, NamedRelativeDateRange[] ranges) {
        int len = ranges.length;
        NamedDateRange[] res = new NamedDateRange[len];
        LocalDate prevDate = null;
        boolean prevIncluded = false;
        for (int i = 0; i < len; i++) {
            NamedRelativeDateRange range = ranges[i];
            LocalDate fromDate = (LocalDate) range.fromDateShift.addTo(relativeDate);
            LocalDate toDate = (LocalDate) range.toDateShift.addTo(relativeDate);
            if (fromDate.equals(toDate)) {
                ensureIncludedOnce(range.fromIncluded, range.toIncluded);
            }
            res[i] = new NamedDateRange(range.name, range.detailed, //
                    range.fromIncluded, range.toIncluded, range.fromDateShift, range.toDateShift, //
                    fromDate, toDate);
            // check increasing dates
            if (prevDate != null) {
                int cmpPrev = prevDate.compareTo(fromDate);
                if (cmpPrev == 0) {
                    ensureIncludedOnce(prevIncluded, range.fromIncluded);
                } else if (cmpPrev > 0) {
                    throw new IllegalArgumentException();
                }
            }
            prevDate = toDate;
            prevIncluded = range.toIncluded;
        }
        return res;
    }

    private static void ensureIncludedOnce(boolean included1, boolean included2) {
        if(!included1 && !included2) {
            throw new IllegalArgumentException();
        }
        if (included1 && included2) {
            throw new IllegalArgumentException();
        }
    }

    //---------------------------------------------------------------------------------------------

    public List<NamedDateRange> getDateRanges() {
        return Arrays.asList(dateRanges);
    }

    public NamedDateRange findRange(LocalDate date) {
        Objects.requireNonNull(date);
        // optim for very frequent case: d
        int cmpD = date.compareTo(relativeDate);
        if (cmpD == 0) {
            return (rangeIndexOfD != -1)? dateRanges[rangeIndexOfD] : null;
        } else if (cmpD < 0) { // date < relativeDate
            // optim for very frequent case: d-1
            if (date.equals(relativeDminus1)) {
                return (rangeIndexOfDminus1 != -1)? dateRanges[rangeIndexOfDminus1] : null;
            }
            int resIdx = binarySearch(dateRanges, 0, rangeIndexOfDminus1, date);
            return (resIdx >= 0)? dateRanges[resIdx] : null;
        } else { // date > relativeDate
            int resIdx = binarySearch(dateRanges, rangeIndexOfD, dateRanges.length, date);
            return (resIdx >= 0)? dateRanges[resIdx] : null;
        }
    }

    //---------------------------------------------------------------------------------------------

    /*pp*/ int naiveFindRangeIndex(LocalDate date) {
        int len = dateRanges.length;
        for(int i = 0; i < len; i++) {
            NamedDateRange dateRange = dateRanges[i];
            if (dateRange.contains(date)) {
                return i;
            }
        }
        return -1;
    }

    private static int binarySearch(NamedDateRange[] a, int fromIndex, int toIndex,
                                                LocalDate key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            NamedDateRange midRange = a[mid];
            LocalDate midRangeFromDate = midRange.fromDate;
            int cmp = midRangeFromDate.compareTo(key);
            if (cmp < 0) { // midRange.fromDate < key
                int cmpRangeDateTo = midRange.toDate.compareTo(key);
                if (cmpRangeDateTo < 0) { // midRange.toDate < key
                    low = mid + 1;
                } else if (cmpRangeDateTo == 0) { // midRange.toDate == key
                    if (midRange.toIncluded) {
                        return mid;
                    } else { // .. midRange.toDate( == key
                        low = mid+1;
                    }
                } else { // midRange.toDate > key
                    return mid;
                }
            } else if (cmp == 0) { // midRange.fromDate == key
                return (midRange.fromIncluded)? mid : -(low+1);
            } else { // key < midRange.fromDate
                high = mid - 1;
            }
        }
        return -(low+1);
    }

}
