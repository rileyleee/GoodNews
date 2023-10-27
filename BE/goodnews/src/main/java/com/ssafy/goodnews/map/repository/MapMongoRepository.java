package com.ssafy.goodnews.map.repository;

import com.ssafy.goodnews.map.domain.OffMapInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapMongoRepository extends MongoRepository<OffMapInfo,String> {

    Optional<OffMapInfo> findFirstByNameRegex(String name);
}
