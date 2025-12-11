package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
//持久层接口
@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByAccount(String account);
    boolean existsByAccount(String account);
    List<User> findByCollegeIdAndRole(Long collegeId, String role);
    Optional<User> findById(Long id);
}