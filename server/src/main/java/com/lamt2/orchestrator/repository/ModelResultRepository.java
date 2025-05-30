package com.lamt2.orchestrator.repository;

import com.lamt2.orchestrator.entity.ModelResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelResultRepository extends MongoRepository<ModelResult, String>, CustomModelResultRepository {
    ModelResult findByModelId(String modelId);
    Page<ModelResult> findAll(Pageable pageable);
}
