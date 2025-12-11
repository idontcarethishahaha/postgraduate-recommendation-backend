package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.ApplicationEvidence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuwenjin
 */
@Repository
public interface ApplicationEvidenceRepository extends CrudRepository<ApplicationEvidence, Long> {

}

