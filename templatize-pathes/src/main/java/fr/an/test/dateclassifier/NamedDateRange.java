package fr.an.test.dateclassifier;

import java.time.LocalDate;
import java.time.Period;

/**
 * immutable named date range
 */
public final class NamedDateRange {

    public final String name;

    public final String detailed;

    public final boolean fromIncluded;
    public final boolean toIncluded;
    public final Period fromDateShift;
    public final Period toDateShift;

    public final LocalDate fromDate;
    public final LocalDate toDate;

    //---------------------------------------------------------------------------------------------

    public NamedDateRange(String name, String detailed, boolean fromIncluded, boolean toIncluded, Period fromDateShift, Period toDateShift, LocalDate fromDate, LocalDate toDate) {
        this.name = name;
        this.detailed = detailed;
        this.fromIncluded = fromIncluded;
        this.toIncluded = toIncluded;
        this.fromDateShift = fromDateShift;
        this.toDateShift = toDateShift;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    //---------------------------------------------------------------------------------------------

    public boolean contains(LocalDate date) {
        int cmpFrom = (fromDate != null) ? date.compareTo(fromDate) : +1;
        if (cmpFrom < 0) {
            return false;
        } else if (cmpFrom == 0) {
            return fromIncluded;
        } // else cmpFrom > 0
        int cmpTo = (toDate != null) ? date.compareTo(toDate) : -1;
        return (cmpTo < 0) ? true : (cmpTo == 0) ? toIncluded : false;
    }

    @Override
    public String toString() {
        return name + " : " + detailed + " : " //
                + ((fromIncluded) ? "[" : ")") + fromDate + ", " + toDate + ((toIncluded) ? "]" : "(")
                ;
    }

}
