package org.recap.repository;

import org.recap.model.jpa.ETLRequestLogEntity;
import org.recap.repository.jpa.BaseRepository;

import java.util.List;

public interface ETLRequestLogDetailsRepository extends BaseRepository<ETLRequestLogEntity> {
    List<ETLRequestLogEntity> findAllByExportStatusId(Integer statusId);
    List<ETLRequestLogEntity> findByExportStatusIdAndTransmissionType(Integer statusId, String fetchTypeId);
    List<ETLRequestLogEntity> findByExportStatusIdAndTransmissionTypeOrderByRequestedTime(Integer statusId, String fetchTypeId);
}
