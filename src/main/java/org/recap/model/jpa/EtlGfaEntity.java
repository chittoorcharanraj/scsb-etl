package org.recap.model.jpa;

import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
