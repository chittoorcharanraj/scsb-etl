package org.recap.model.jparw;

import lombok.Data;

import org.recap.model.jpa.AbstractEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ETL_EXPORT_STATUS_T", catalog = "")
@Data
@AttributeOverride(
        name = "id",
        column = @Column(
                name = "EXPORT_STATUS_ID"
        )
)
public class ExportStatusEntity extends AbstractEntity<Integer> {

    @Column(name = "EXPORT_STATUS_CODE")
    private String exportStatusCode;

    @Column(name = "EXPORT_STATUS_DESC")
    private String exportStatusDesc;
}
