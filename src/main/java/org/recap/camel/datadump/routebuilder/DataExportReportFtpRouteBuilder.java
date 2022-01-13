package org.recap.camel.datadump.routebuilder;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.camel.datadump.FileNameProcessorForDataDumpFailure;
import org.recap.camel.datadump.FileNameProcessorForDataDumpSuccess;
import org.recap.model.csv.DataDumpFailureReport;
import org.recap.model.csv.DataDumpSuccessReport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by premkb on 01/10/16.
 */
@Slf4j
@Component
public class DataExportReportFtpRouteBuilder {


    /**
     * Instantiates a new Data export report ftp route builder.
     *
     * @param context                      the context
     * @param s3OnlyReportRemoteServer     the ftp only report remote server
     * @param s3DumpWithReportRemoteServer the ftp dump with report remote server
     */
    @Autowired
    public DataExportReportFtpRouteBuilder(CamelContext context, @Value("${" + PropertyKeyConstants.S3_ADD_S3_ROUTES_ON_STARTUP + "}") boolean addS3RoutesOnStartup, @Value("${" + PropertyKeyConstants.S3_DATADUMP_REPORT_REMOTE_SERVER + "}") String s3OnlyReportRemoteServer,
                                           @Value("${" + PropertyKeyConstants.S3_DATA_DUMP_DIR + "}") String s3DumpWithReportRemoteServer) {
        try {
            if (addS3RoutesOnStartup) {
                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATADUMP_SUCCESS_REPORT_FTP_Q)
                                .routeId(ScsbConstants.DATADUMP_SUCCESS_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpSuccess())
                                .marshal().bindy(BindyType.Csv, DataDumpSuccessReport.class)
                                .setHeader(S3Constants.KEY, simple(s3OnlyReportRemoteServer + "${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv"))
                                .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATADUMP_FAILURE_REPORT_FTP_Q)
                                .routeId(ScsbConstants.DATADUMP_FAILURE_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpFailure())
                                .marshal().bindy(BindyType.Csv, DataDumpFailureReport.class)
                                .setHeader(S3Constants.KEY, simple(s3OnlyReportRemoteServer + "${in.header.directoryName}/${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv"))
                                .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATAEXPORT_WITH_SUCCESS_REPORT_FTP_Q)
                                .routeId(ScsbConstants.DATAEXPORT_WITH_SUCCESS_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpSuccess())
                                .marshal().bindy(BindyType.Csv, DataDumpSuccessReport.class)
                                .setHeader(S3Constants.KEY, simple(s3DumpWithReportRemoteServer + "${in.header.fileName}.csv"))
                                .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });

                context.addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        from(ScsbConstants.DATAEXPORT_WITH_FAILURE_REPORT_FTP_Q)
                                .routeId(ScsbConstants.DATAEXPORT_WITH_FAILURE_REPORT_FTP_ROUTE_ID)
                                .process(new FileNameProcessorForDataDumpFailure())
                                .marshal().bindy(BindyType.Csv, DataDumpFailureReport.class)
                                .setHeader(S3Constants.KEY, simple(s3DumpWithReportRemoteServer + "${in.header.fileName}.csv"))
                                .to(ScsbConstants.SCSB_CAMEL_S3_TO_ENDPOINT);
                    }
                });
            }
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR, e);
        }
    }
}
