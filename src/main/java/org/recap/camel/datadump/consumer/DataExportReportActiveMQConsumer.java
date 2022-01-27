package org.recap.camel.datadump.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.DataExportFailureReport;
import org.recap.model.jparw.ReportDataEntity;
import org.recap.model.jparw.ReportEntity;
import org.recap.repositoryrw.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by peris on 11/11/16.
 */
@Slf4j
@Component
public class DataExportReportActiveMQConsumer {
   /**
     * The Report detail repository.
     */
    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * This method is invoked by the route to save the success report entity for data export using the values in Map.
     *
     * @param body the body
     * @return the report entity
     */
    public ReportEntity saveSuccessReportEntity(Map body){
        String requestingInstitutionCode = (String) body.get(ScsbConstants.REQUESTING_INST_CODE);
        String institutionCodes = (String) body.get(ScsbConstants.INSTITUTION_CODES);
        String fetchType = (String) body.get(ScsbConstants.FETCH_TYPE);
        String collectionGroupIds = (String) body.get(ScsbConstants.COLLECTION_GROUP_IDS);
        String transmissionType = (String) body.get(ScsbConstants.TRANSMISSION_TYPE);
        String exportFormat = (String) body.get(ScsbConstants.EXPORT_FORMAT);
        String fromDate = body.get(ScsbConstants.EXPORT_FROM_DATE) != null ? (String) body.get(ScsbConstants.EXPORT_FROM_DATE) :"";
        String toEmailId = (String) body.get(ScsbConstants.TO_EMAIL_ID);
        String type = (String) body.get(ScsbConstants.BATCH_EXPORT);
        String requestId = (String) (body.get(ScsbCommonConstants.REQUEST_ID));
        String numBibsExported = (String) body.get(ScsbConstants.NUM_BIBS_EXPORTED);
        String numRecords = (String) body.get(ScsbConstants.NUM_RECORDS);
        Integer exportedItemCount = (Integer) body.get(ScsbConstants.ITEM_EXPORTED_COUNT);
        String imsDepositoryCodes = (String) body.get(ScsbConstants.IMS_DEPOSITORY);
        log.info("No. of bib exported for a single batch---->{}",numRecords);
        log.info("No. of item exported for a single batch---->{}",exportedItemCount);

        List<ReportEntity> byFileName = getReportDetailRepository().findByFileNameAndType(requestId, ScsbConstants.BATCH_EXPORT_SUCCESS);

        ReportEntity reportEntity;
        if (CollectionUtils.isEmpty(byFileName)) {
            reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(requestingInstitutionCode);
            reportEntity.setType(type);
            reportEntity.setFileName(requestId);

            ArrayList<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntities.add(reportDataEntity);
            reportDataEntity.setHeaderName(numBibsExported);
            reportDataEntity.setHeaderValue(numRecords);
            reportEntity.setReportDataEntities(reportDataEntities);

            ReportDataEntity reportDataEntityReqInst = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityReqInst);
            reportDataEntityReqInst.setHeaderName("RequestingInstitution");
            reportDataEntityReqInst.setHeaderValue(requestingInstitutionCode);

            ReportDataEntity reportDataEntityInstCodes = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityInstCodes);
            reportDataEntityInstCodes.setHeaderName("InstitutionCodes");
            reportDataEntityInstCodes.setHeaderValue(institutionCodes);

            ReportDataEntity reportDataEntityFetchType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFetchType);
            reportDataEntityFetchType.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
            reportDataEntityFetchType.setHeaderValue(fetchType);

            ReportDataEntity reportDataEntityFromDate = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFromDate);
            reportDataEntityFromDate.setHeaderName("ExportFromDate");
            reportDataEntityFromDate.setHeaderValue(fromDate.replaceAll("null",""));

            ReportDataEntity reportDataEntityCollecGroupIds = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityCollecGroupIds);
            reportDataEntityCollecGroupIds.setHeaderName("CollectionGroupIds");
            reportDataEntityCollecGroupIds.setHeaderValue(collectionGroupIds);

            ReportDataEntity reportDataEntityTransType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityTransType);
            reportDataEntityTransType.setHeaderName("TransmissionType");
            reportDataEntityTransType.setHeaderValue(transmissionType);

            ReportDataEntity reportDataEntityExportFormat = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportFormat);
            reportDataEntityExportFormat.setHeaderName("ExportFormat");
            reportDataEntityExportFormat.setHeaderValue(exportFormat);

            ReportDataEntity reportDataEntityMailedTo = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityMailedTo);
            reportDataEntityMailedTo.setHeaderName("ToEmailId");
            reportDataEntityMailedTo.setHeaderValue(toEmailId);

            ReportDataEntity reportDataEntityExportedItemCount = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportedItemCount);
            reportDataEntityExportedItemCount.setHeaderName(ScsbConstants.EXPORTED_ITEM_COUNT);
            reportDataEntityExportedItemCount.setHeaderValue(String.valueOf(exportedItemCount));

            ReportDataEntity reportDataEntityImsDepository = new ReportDataEntity();
            reportDataEntityImsDepository.setHeaderName(ScsbConstants.IMS_DEPOSITORY);
            reportDataEntityImsDepository.setHeaderValue(imsDepositoryCodes);
            reportDataEntities.add(reportDataEntityImsDepository);


        } else {
            reportEntity = byFileName.get(0);
            List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
            for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
                ReportDataEntity reportDataEntity = iterator.next();
                if (reportDataEntity.getHeaderName().equals(numBibsExported)) {
                    log.info("Updated bib count-->{}",(Integer.valueOf(reportDataEntity.getHeaderValue()) + Integer.valueOf(numRecords)));
                    reportDataEntity.setHeaderValue(String.valueOf(Integer.valueOf(reportDataEntity.getHeaderValue()) + Integer.valueOf(numRecords)));
                }
                if(reportDataEntity.getHeaderName().equals(ScsbConstants.EXPORTED_ITEM_COUNT)){
                    log.info("Updated item count-->{}",(Integer.valueOf(reportDataEntity.getHeaderValue())+exportedItemCount));
                    reportDataEntity.setHeaderValue(String.valueOf(Integer.valueOf(reportDataEntity.getHeaderValue())+exportedItemCount));
                }
            }
        }

        getReportDetailRepository().save(reportEntity);

        return reportEntity;
    }

    /**
     * This method is invoked by the route to save the failure report entity for data export using the values in Map.
     *
     * @param body the body
     * @return the report entity
     */
    public ReportEntity saveFailureReportEntity(Map body){

        String requestingInstitutionCode = (String) body.get(ScsbConstants.REQUESTING_INST_CODE);
        String institutionCodes = (String) body.get(ScsbConstants.INSTITUTION_CODES);
        String fetchType = (String) body.get(ScsbConstants.FETCH_TYPE);
        String collectionGroupIds = (String) body.get(ScsbConstants.COLLECTION_GROUP_IDS);
        String transmissionType = (String) body.get(ScsbConstants.TRANSMISSION_TYPE);
        String exportFormat = (String) body.get(ScsbConstants.EXPORT_FORMAT);
        String fromDate = body.get(ScsbConstants.EXPORT_FROM_DATE) != null ? (String) body.get(ScsbConstants.EXPORT_FROM_DATE) :"";
        String toEmailId = (String) body.get(ScsbConstants.TO_EMAIL_ID);
        String type = (String) body.get(ScsbConstants.BATCH_EXPORT);
        String requestId = (String) (body.get(ScsbCommonConstants.REQUEST_ID));
        String failedBibs = (String) body.get(ScsbConstants.FAILED_BIBS);
        String numRecords = (String) body.get(ScsbConstants.NUM_RECORDS);
        String failureCause = (String) body.get(ScsbConstants.FAILURE_CAUSE);
        List<String> failureList = (List<String>) body.get(ScsbConstants.FAILURE_LIST);
        String imsDepositoryCodes = (String) body.get(ScsbConstants.IMS_DEPOSITORY);

        List<ReportEntity> byFileName = getReportDetailRepository().findByFileNameAndType(requestId, ScsbConstants.BATCH_EXPORT_FAILURE);


        ReportEntity reportEntity;
        if (CollectionUtils.isEmpty(byFileName)) {
            exportFailureToReport(fetchType, requestId, failureList,requestingInstitutionCode);
            reportEntity = new ReportEntity();
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(requestingInstitutionCode);
            reportEntity.setType(type);
            reportEntity.setFileName(requestId);

            ArrayList<ReportDataEntity> reportDataEntities = new ArrayList<>();
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntities.add(reportDataEntity);
            reportDataEntity.setHeaderName(failedBibs);
            reportDataEntity.setHeaderValue(numRecords);
            reportEntity.setReportDataEntities(reportDataEntities);

            ReportDataEntity reportDataEntityReqInst = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityReqInst);
            reportDataEntityReqInst.setHeaderName("RequestingInstitution");
            reportDataEntityReqInst.setHeaderValue(requestingInstitutionCode);

            ReportDataEntity reportDataEntityFailureCause = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFailureCause);
            reportDataEntityFailureCause.setHeaderName(ScsbConstants.FAILURE_CAUSE);
            reportDataEntityFailureCause.setHeaderValue(failureCause);

            ReportDataEntity reportDataEntityInstCodes = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityInstCodes);
            reportDataEntityInstCodes.setHeaderName("InstitutionCodes");
            reportDataEntityInstCodes.setHeaderValue(institutionCodes);

            ReportDataEntity reportDataEntityFetchType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFetchType);
            reportDataEntityFetchType.setHeaderName(ScsbConstants.HEADER_FETCH_TYPE);
            reportDataEntityFetchType.setHeaderValue(fetchType);

            ReportDataEntity reportDataEntityFromDate = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityFromDate);
            reportDataEntityFromDate.setHeaderName("ExportFromDate");
            reportDataEntityFromDate.setHeaderValue(fromDate.replaceAll("null",""));

            ReportDataEntity reportDataEntityCollecGroupIds = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityCollecGroupIds);
            reportDataEntityCollecGroupIds.setHeaderName("CollectionGroupIds");
            reportDataEntityCollecGroupIds.setHeaderValue(collectionGroupIds);

            ReportDataEntity reportDataEntityTransType = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityTransType);
            reportDataEntityTransType.setHeaderName("TransmissionType");
            reportDataEntityTransType.setHeaderValue(transmissionType);

            ReportDataEntity reportDataEntityExportFormat = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityExportFormat);
            reportDataEntityExportFormat.setHeaderName("ExportFormat");
            reportDataEntityExportFormat.setHeaderValue(exportFormat);

            ReportDataEntity reportDataEntityMailedTo = new ReportDataEntity();
            reportDataEntities.add(reportDataEntityMailedTo);
            reportDataEntityMailedTo.setHeaderName("ToEmailId");
            reportDataEntityMailedTo.setHeaderValue(toEmailId);

            ReportDataEntity reportDataEntityImsDepository = new ReportDataEntity();
            reportDataEntityImsDepository.setHeaderName(ScsbConstants.IMS_DEPOSITORY);
            reportDataEntityImsDepository.setHeaderValue(imsDepositoryCodes);
            reportDataEntities.add(reportDataEntityImsDepository);

        } else {
            reportEntity = byFileName.get(0);
            List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
            for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
                ReportDataEntity reportDataEntity = iterator.next();
                if (reportDataEntity.getHeaderName().equals(ScsbConstants.FAILED_BIBS)) {
                    Integer exitingRecords = Integer.valueOf(reportDataEntity.getHeaderValue());
                    reportDataEntity.setHeaderValue(String.valueOf(exitingRecords + Integer.valueOf(numRecords)));
                }
            }
            exportFailureToReport(fetchType, requestId, failureList,requestingInstitutionCode);
        }

        getReportDetailRepository().saveAndFlush(reportEntity);

        return reportEntity;
    }

    public void exportFailureToReport(String fetchType, String requestId, List<String> failureList,String requestingInstitutionCode) {
        List<DataExportFailureReport> dataExportFailureReportList = new ArrayList<>();
        String fetchTypeValue = getFetchType(fetchType);
        for (String failure : failureList) {
            populateExportFailureRecords(requestId, dataExportFailureReportList, fetchTypeValue, failure,requestingInstitutionCode);
        }

        producerTemplate.sendBodyAndHeader(ScsbConstants.DATADUMP_FAILURE_REPORT_SFTP_Q, dataExportFailureReportList, ScsbConstants.HEADER_FETCH_TYPE, fetchTypeValue);
    }

    public void populateExportFailureRecords(String requestId, List<DataExportFailureReport> dataExportFailureReportList, String fetchTypeValue, String failure,String requestingInstitutionCode) {
        String[] split = failure.split("\\*");
        DataExportFailureReport dataExportFailureReport = new DataExportFailureReport();
        dataExportFailureReport.setOwningInstitutionBibId(split[0]);
        dataExportFailureReport.setFailureReason(split[1]);
        dataExportFailureReport.setFilename(fetchTypeValue + "-FailureReport-" + requestId);
        dataExportFailureReport.setReportType(fetchTypeValue);
        dataExportFailureReport.setRequestingInstitutionCode(requestingInstitutionCode);
        dataExportFailureReportList.add(dataExportFailureReport);
    }

    public String getFetchType(String fetchTypeNumber) {
        String fetchType ="";
        switch (fetchTypeNumber) {
            case "10":
                fetchType="Full Dump";
                break;
            case "1":
                fetchType= "Incremental";
                break;
            case "2":
                fetchType= "Deleted";
                break;
            default:
                fetchType= "Export";

        }
        return fetchType;
    }

    /**
     * Gets report detail repository.
     *
     * @return ReportDetailRepository
     */
    private ReportDetailRepository getReportDetailRepository() {
        return reportDetailRepository;
    }

    /**
     * Sets report detail repository.
     *
     * @param reportDetailRepository
     */
    public void setReportDetailRepository(ReportDetailRepository reportDetailRepository) {
        this.reportDetailRepository = reportDetailRepository;
    }

}
