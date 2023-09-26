package org.recap.model.jpa;

import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Created by hemalathas on 21/7/16.
 */
@Entity
@Table(name = "etl_gfa_temp_t" )
@Data
public class EtlGfaEntity implements Serializable {

    @Id
    @Column(name = "ITEM_BARCODE")
    private String itemBarcode;

    @Column(name = "CUSTOMER_CODE")
    private String customer;

    @Column(name = "ITEM_STATUS")
    private String status;

}
