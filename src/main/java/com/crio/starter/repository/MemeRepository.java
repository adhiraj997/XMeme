package com.crio.starter.repository;

import com.crio.starter.data.MemeEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends MongoRepository<MemeEntity, String> {
  Optional<MemeEntity> findById(String id);

  // List<MemeEntity> findAll(Pageable pageable);

  List<MemeEntity> findByNameAndCaptionAndUrl(String name, String caption, String url);

    
}
