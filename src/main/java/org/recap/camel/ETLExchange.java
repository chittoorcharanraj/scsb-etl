package org.recap.camel;

import lombok.Data;

import org.recap.model.jpa.BibliographicEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 4/7/16.
 */
@Data
public class ETLExchange implements Serializable {

    private List<BibliographicEntity> bibliographicEntities;
    private Map<String, Integer> institutionEntityMap;
    private Map<String, Integer> collectionGroupMap;

}
