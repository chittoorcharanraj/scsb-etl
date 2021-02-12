package org.recap.model.search;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertTrue;

public class SearchItemResultRowUT extends BaseTestCaseUT {

    SearchItemResultRow searchItemResultRow;

    @Test
    public void testSearchItemResultRow() {
        searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setChronologyAndEnum("test");
        String data = "Test data";
        searchItemResultRow.equals(data);
        searchItemResultRow.hashCode();
        searchItemResultRow.compareTo(searchItemResultRow);
        assertTrue(true);
    }
}
