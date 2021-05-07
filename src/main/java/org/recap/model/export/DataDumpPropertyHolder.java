package org.recap.model.export;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by rajeshbabuk on 05/May/2021
 */
@Data
@Service
public class DataDumpPropertyHolder {
    @Value("${etl.data.dump.records.per.file}")
    String dataDumpRecordsPerFile;

    @Value("${etl.data.dump.generate.bib.entity.thread.size}")
    Integer dataDumpBibEntityThreadSize;

    @Value("${etl.data.dump.generate.bib.entity.batch.size}")
    Integer dataDumpBibEntityBatchSize;

    @Value("${etl.data.dump.marc.format.thread.size}")
    Integer dataDumpMarcFormatThreadSize;

    @Value("${etl.data.dump.marc.format.batch.size}")
    Integer dataDumpMarcFormatBatchSize;

    @Value("${etl.data.dump.scsb.format.thread.size}")
    Integer dataDumpScsbFormatThreadSize;

    @Value("${etl.data.dump.scsb.format.batch.size}")
    Integer dataDumpScsbFormatBatchSize;

    @Value("${etl.data.dump.deleted.records.thread.size}")
    Integer dataDumpDeletedRecordsThreadSize;

    @Value("${etl.data.dump.deleted.records.batch.size}")
    Integer dataDumpDeletedRecordsBatchSize;
}
