package org.recap.service.transmission.datadump;

import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbConstants;
import org.recap.model.export.DataDumpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by premkb on 28/9/16.
 */
@Slf4j
@Service
public class DataDumpTransmissionService {

    private List<DataDumpTransmissionInterface> dataDumpTransmissionInterfaceList;

    @Autowired
    private DataDumpFileSystemTranmissionService dataDumpFileSystemTranmissionService;

    @Autowired
    private DataDumpS3TransmissionService DataDumpS3TransmissionService;


    /**
     * Starts transmitting data dump files to a specified path.
     *
     * @param dataDumpRequest the data dump request
     * @param routeMap        the route map
     */
    public void startTranmission(DataDumpRequest dataDumpRequest, Map<String,String> routeMap){
        for(DataDumpTransmissionInterface dataDumpTransmissionInterface:getTransmissionService()){
            if(dataDumpTransmissionInterface.isInterested(dataDumpRequest)){
                try {
                    dataDumpTransmissionInterface.transmitDataDump(routeMap);
                } catch (Exception e) {
                    log.error(ScsbConstants.ERROR,e);
                }
            }
        }
    }

    /**
     * Get transmission service list.
     *
     * @return the list
     */
    public List<DataDumpTransmissionInterface> getTransmissionService(){
        if(CollectionUtils.isEmpty(dataDumpTransmissionInterfaceList)){
            dataDumpTransmissionInterfaceList = new ArrayList<>();
            dataDumpTransmissionInterfaceList.add(dataDumpFileSystemTranmissionService);
            dataDumpTransmissionInterfaceList.add(DataDumpS3TransmissionService);
        }
        return dataDumpTransmissionInterfaceList;
    }
}
