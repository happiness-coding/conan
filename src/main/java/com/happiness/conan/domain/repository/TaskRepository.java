package com.happiness.conan.domain.repository;

import com.happiness.conan.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByIsCompleted(boolean isCompleted, Pageable pageable);

    Page<Task> findByPriority(Task.Priority priority, Pageable pageable);

    @Query("SELECT t FROM Task t JOIN t.labels l WHERE l.id IN :labelIds GROUP BY t HAVING COUNT(DISTINCT l.id) = :labelCount")
    Page<Task> findByLabelIdsAll(@Param("labelIds") List<Long> labelIds, @Param("labelCount") long labelCount, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    Page<Task> findByDueDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Task> findByTitleOrDescriptionContainingIgnoreCase(@Param("search") String search, Pageable pageable);
}