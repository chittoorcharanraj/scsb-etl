package org.recap.repository;

import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.repository.jpa.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by angelind on 27/6/16.
 */
public interface CollectionGroupDetailsRepository extends BaseRepository<CollectionGroupEntity> {

    /**
     * Find by collection group code collection group entity.
     *
     * @param collectionGroupCode the collection group code
     * @return the collection group entity
     */
    CollectionGroupEntity findByCollectionGroupCode(String collectionGroupCode);

    @Query(value = "select * from collection_group_t where COLLECTION_GROUP_ID in (?1)",nativeQuery = true)
    List<CollectionGroupEntity> findAllByIds(List<Integer> collectionGroupIds);
}
