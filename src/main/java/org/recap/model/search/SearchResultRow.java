package org.recap.model.search;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 11/7/16.
 */
@Data
public class SearchResultRow extends AbstractSearchResultRow {
    private List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
}
