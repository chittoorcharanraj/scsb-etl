package org.recap.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.commons.io.FilenameUtils;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.repository.XmlRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by angelind on 21/7/16.
 */
@Slf4j
@Component
public class XmlRouteBuilder{


    /**
     * Instantiates a new Xml route builder.
     *
     * @param context                             the context
     * @param xmlRecordRepository                 the xml record repository
     * @param xmlFileLoadReportProcessor          the xml file load report processor
     * @param xmlFileLoadExceptionReportProcessor the xml file load exception report processor
     * @param xmlFileLoadValidator                the xml file load validator
     * @param xmlTagName                          the xml tag name
     * @param inputDirectoryPath                  the input directory path
     * @param poolSize                            the pool size
     * @param maxPoolSize                         the max pool size
     */
    @Autowired
    public XmlRouteBuilder(CamelContext context, XmlRecordRepository xmlRecordRepository, XMLFileLoadReportProcessor xmlFileLoadReportProcessor, XMLFileLoadExceptionReportProcessor xmlFileLoadExceptionReportProcessor, XMLFileLoadValidator xmlFileLoadValidator,
                           @Value("${" + PropertyKeyConstants.ETL_SPLIT_XML_TAG_NAME + "}") String xmlTagName,
                           @Value("${" + PropertyKeyConstants.ETL_DATA_LOAD_DIRECTORY + "}") String inputDirectoryPath,
                           @Value("${" + PropertyKeyConstants.ETL_POOL_SIZE + "}") Integer poolSize, @Value("${" + PropertyKeyConstants.ETL_MAX_POOL_SIZE + "}") Integer maxPoolSize) {

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    FileEndpoint fileEndpoint = endpoint("file:" + inputDirectoryPath + "?moveFailed=" + inputDirectoryPath + File.separator + "exception", FileEndpoint.class);
                    fileEndpoint.setFilter(new XmlFileFilter());

                    from(fileEndpoint)
                            .onCompletion()
                            .process(xmlFileLoadReportProcessor)
                            .end()
                            .onException(Exception.class)
                            .process(xmlFileLoadExceptionReportProcessor)
                            .end()
                            .process(xmlFileLoadValidator)
                            .split()
                            .tokenizeXML(xmlTagName)
                            .streaming()
                            .parallelProcessing().threads(poolSize, maxPoolSize)
                            .process(new XmlProcessor(xmlRecordRepository));
                }
            });
        } catch (Exception e) {
            log.error(ScsbConstants.ERROR,e);
        }

    }

    /**
     * The type Xml file filter.
     */
    class XmlFileFilter implements GenericFileFilter {
        @Override
        public boolean accept(GenericFile file) {
            return "xml".equalsIgnoreCase(FilenameUtils.getExtension(file.getAbsoluteFilePath()));
        }
    }
}
