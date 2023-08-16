package org.recap.model.jpa;

import lombok.Data;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
@Data
@Entity
@Table(name = "bibliographic_t", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "BIBLIOGRAPHIC_ID"))
public class BibliographicEntity extends BibliographicAbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable=false, updatable=false)
    private InstitutionEntity institutionEntity;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_holdings_t", joinColumns = {
            @JoinColumn(name = "BIBLIOGRAPHIC_ID", referencedColumnName = "BIBLIOGRAPHIC_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "HOLDINGS_ID", referencedColumnName = "HOLDINGS_ID")})
    private List<HoldingsEntity> holdingsEntities;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_item_t", joinColumns = {
            @JoinColumn(name="BIBLIOGRAPHIC_ID", referencedColumnName = "BIBLIOGRAPHIC_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name="ITEM_ID", referencedColumnName = "ITEM_ID")})
    private List<ItemEntity> itemEntities;

    /**
     * Instantiates a new Bibliographic entity.
     */
    public BibliographicEntity() {
        super();
    }
}

