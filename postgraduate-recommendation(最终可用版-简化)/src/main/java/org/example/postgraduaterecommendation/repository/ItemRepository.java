package org.example.postgraduaterecommendation.repository;

import org.example.postgraduaterecommendation.dox.Item;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wuwenjin
 */
@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    //根据类别ID查询顶级节点
    @Query(value = "select * from item t1 where t1.major_category_id=:mcid and t1.parent_id = 0")
    List<Item> findTopByMajorCategoryId(@Param("mcid") long mcid);

    //递归查询指定分类下所有节点（包含子节点）
    @Query(value = """
            with recursive t0 as (
                select * from item t1 where t1.major_category_id=:mcid
                union
                select t2.* from item t2 join t0 on t2.parent_id=t0.id
            )
            select * from t0;
            """)
    List<Item> findByMajorCategoryId(@Param("mcid") Long mcid);

    //递归查询指定分类下指定父节点的所有子节点
    @Query(value = """
            with recursive t0 as (
                select * from item t1 where t1.major_category_id=:mcid and t1.id=:parentid
                union
                select t2.* from item t2 join t0 on t2.parent_id=t0.id
            )
            select * from t0;
            """)
    List<Item> findByMajorCategoryIdAndParentId(@Param("mcid") long mcid, @Param("parentid") long parentid);

    //根据ID和类别ID查询单个节点
    Optional<Item> findByIdAndMajorCategoryId(@Param("id") long id, @Param("mcid") long mcid);
}
