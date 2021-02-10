package org.recap.repository;

import org.recap.model.jpa.ExportStatusEntity;
import org.recap.repository.jpa.BaseRepository;

public interface ExportStatusDetailsRepository extends BaseRepository<ExportStatusEntity> {

    ExportStatusEntity findByExportStatusCode(String statusCode);
}
