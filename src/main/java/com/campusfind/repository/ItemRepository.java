package com.campusfind.repository;

import com.campusfind.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Retrieve sorted items (newest first)
    List<Item> findAllByOrderByDateReportedDescIdDesc();

    // Recent items for homepage and dashboard
    List<Item> findTop6ByOrderByDateReportedDescIdDesc();
    List<Item> findTop10ByOrderByDateReportedDescIdDesc();

    // Standard counts
    long countByType(String type);
    long countByStatus(String status);
    long countByCategory(String category);

    // Multi-criteria filter query
    @Query("SELECT i FROM Item i WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:type IS NULL OR :type = '' OR UPPER(i.type) = UPPER(:type)) " +
           "AND (:category IS NULL OR :category = '' OR LOWER(i.category) = LOWER(:category)) " +
           "AND (:status IS NULL OR :status = '' OR UPPER(i.status) = UPPER(:status)) " +
           "ORDER BY i.dateReported DESC, i.id DESC")
    List<Item> filterItems(@Param("keyword") String keyword,
                           @Param("type") String type,
                           @Param("category") String category,
                           @Param("status") String status);

    // Group counts for dashboard visual charts
    @Query("SELECT i.category, COUNT(i) FROM Item i GROUP BY i.category")
    List<Object[]> countItemsGroupedByCategory();

    @Query("SELECT i.type, COUNT(i) FROM Item i GROUP BY i.type")
    List<Object[]> countItemsGroupedByType();

    @Query("SELECT i.status, COUNT(i) FROM Item i GROUP BY i.status")
    List<Object[]> countItemsGroupedByStatus();
}
