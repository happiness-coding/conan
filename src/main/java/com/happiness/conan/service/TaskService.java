package com.happiness.conan.service;

import com.happiness.conan.domain.model.Task;
import com.happiness.conan.web.dto.BatchUpdateRequestDTO;
import com.happiness.conan.web.dto.TaskCreateDTO;
import com.happiness.conan.web.dto.TaskUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {

    Page<Task> findTasks(String status, String priority, List<Long> labels,
            LocalDate startDate, LocalDate endDate,
            String search, Pageable pageable);

    Task createTask(TaskCreateDTO taskCreateDTO);

    Task getTaskById(Long id);

    Task updateTask(Long id, TaskUpdateDTO taskUpdateDTO);

    void deleteTask(Long id);

    List<Task> batchUpdateTasks(BatchUpdateRequestDTO batchUpdateRequestDTO);

    Task toggleTaskCompletion(Long id);
}