package org.example.postgraduaterecommendation.repository;

/*
 * @author wuwenjin
 */

import org.example.postgraduaterecommendation.dox.Application;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long> {


}
