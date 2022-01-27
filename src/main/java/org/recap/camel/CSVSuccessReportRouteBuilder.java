package org.recap.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.model.csv.SCSBCSVSuccessRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by angelind on 18/8/16.
 */
@Slf4j
@Component
public class CSVSuccessReportRouteBuilder {

    /**
     * Instantiates a new Csv success report route builder.
     *
     * @param context          the context
     * @param reportsDirectory the reports directory
     */
    @Autowired
    public CSVSuccessReportRouteBuilder(CamelContext context, @Value("${" + PropertyKeyConstants.ETL_REPORT_DIRECTORY + "}") String reportsDirectory) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from(ScsbConstants.CSV_SUCCESS_Q)
                            .routeId(ScsbConstants.CSV_SUCCESS_ROUTE_ID)
                            .process(new FileNameProcessorForSuccessRecord())
                            .marshal().bindy(BindyType.Csv, SCSBCSVSuccessRecord.class)
                            .to("file:" + reportsDirectory + File.separator + "?fileName=${in.header.fileName}-${in.header.reportType}-${date:now:ddMMMyyyy}.csv&fileExist=append");
                }
            });
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR,e);
        }
    }
}
