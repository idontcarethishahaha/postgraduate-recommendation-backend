package org.example.postgraduaterecommendation.repository;

/*
 * @author wuwenjin
 */
import org.example.postgraduaterecommendation.dox.College;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CollegeRepository extends ListCrudRepository<College, Long> {
    boolean existsByName(String name);
    // 根据id查询单个学院
    Optional<College> findById(Long id);
}