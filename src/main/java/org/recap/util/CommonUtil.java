package org.recap.util;

import lombok.extern.slf4j.Slf4j;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.InstitutionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by rajeshbabuk on 11/May/2021
 */
@Slf4j
@Service
public class CommonUtil {

    @Value("${scsb.support.institution}")
    private String supportInstitution;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    /**
     * Get All Institution Codes Except Support Institution
     * @return institutionCodes
     */
    public List<String> findAllInstitutionCodesExceptSupportInstitution() {
        return institutionDetailsRepository.findAllInstitutionCodesExceptSupportInstitution(supportInstitution);
    }

    /**
     * Get All Institution Codes Except Support Institution
     * @return institutionCodes
     */
    public List<InstitutionEntity> findAllInstitutionsExceptSupportInstitution() {
        return institutionDetailsRepository.findAllInstitutionsExceptSupportInstitution(supportInstitution);
    }
}
