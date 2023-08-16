package org.recap.camel;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.BibliographicDetailsRepository;
import org.recap.repository.HoldingsDetailsRepository;
import org.recap.repository.ItemDetailsRepository;
import org.recap.service.BibliographicRepositoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * Created by premkb on 28/6/17.
 */
@Service
public class EtlDataLoadDAOService {

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    BibliographicRepositoryDAO bibliographicRepositoryDAO;


    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the bibliographic entity.
     *
     * @param bibliographicEntity the bibliographic entity
     */
    @Transactional
    public void saveBibliographicEntity(BibliographicEntity bibliographicEntity) {
        bibliographicRepositoryDAO.saveOrUpdate(bibliographicEntity);
        entityManager.clear();
    }


    /**
     * Saves holdings entity.
     *
     * @param holdingsEntity the holdings entity
     * @return the holdings entity
     */
    @Transactional
    public HoldingsEntity savedHoldingsEntity(HoldingsEntity holdingsEntity){
        HoldingsEntity savedHoldingsEntity = holdingsDetailsRepository.save(holdingsEntity);
        flushAndClearSession();
        return savedHoldingsEntity;
    }

    /**
     * Saves item entity.
     *
     * @param itemEntity the item entity
     * @return the item entity
     */
    @Transactional
    public ItemEntity saveItemEntity(ItemEntity itemEntity){
        ItemEntity savedItemEntity = itemDetailsRepository.save(itemEntity);
        flushAndClearSession();
        return savedItemEntity;
    }

    /**
     * Flush and clears the session.
     */
    public void flushAndClearSession() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Clears the session.
     */
    public void clearSession() {
        entityManager.clear();
    }
}
