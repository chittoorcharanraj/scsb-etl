package org.recap.repository;

import org.recap.model.jpa.ETLRequestLogEntity;
import org.recap.repository.jpa.BaseRepository;

import java.util.List;

public interface ETLRequestLogDetailsRepository extends BaseRepository<ETLRequestLogEntity> {
    ETLRequestLogEntity findByEtlStatusId(Integer statusId);
    List<ETLRequestLogEntity> findAllByEtlStatusId(Integer statusId);

    List<ETLRequestLogEntity> findAllByEtlStatusIdOrderByRequestedTime(Integer statusId);
}
