package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ETL_EXPORT_STATUS_T", schema = "recap", catalog = "")
@Getter
@Setter
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
