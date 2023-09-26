package org.recap.model.search;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
@Data
public class SearchRecordsRequest implements Serializable {
    private String fieldValue = "";
    private String fieldName;
    private List<String> owningInstitutions = null;
    private List<String> collectionGroupDesignations = null;
    private List<String> availability = null;
    private List<String> materialTypes = null;
    private List<String> useRestrictions = null;
    private List<SearchResultRow> searchResultRows = new ArrayList<>();
    private Integer totalPageCount = 0;
    private String totalBibRecordsCount = "0";
    private String totalItemRecordsCount = "0";
    private String totalRecordsCount = "0";
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private boolean showResults = false;
    private boolean selectAll = false;
    private boolean selectAllFacets = false;
    private boolean showTotalCount = false;
    private Integer index;
    private String errorMessage;
    private boolean isDeleted = false;
    private String requestingInstitution = "";
    private List<String> imsDepositoryCodes = null;

    /**
     * Reset.
     */
    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }
}
