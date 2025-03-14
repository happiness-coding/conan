package com.happiness.conan.service;

import com.happiness.conan.domain.model.Label;
import com.happiness.conan.domain.model.Task;
import com.happiness.conan.domain.repository.LabelRepository;
import com.happiness.conan.domain.repository.TaskRepository;
import com.happiness.conan.exception.BizException;
import com.happiness.conan.exception.DomainCode;
import com.happiness.conan.web.dto.BatchUpdateRequestDTO;
import com.happiness.conan.web.dto.TaskCreateDTO;
import com.happiness.conan.web.dto.TaskUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findTasks(String status, String priority, List<Long> labels,
            LocalDate startDate, LocalDate endDate,
            String search, Pageable pageable) {

        // Filter by status
        if (status != null && !status.equals("all")) {
            boolean isCompleted = "completed".equals(status);
            return taskRepository.findByIsCompleted(isCompleted, pageable);
        }

        // Filter by priority
        if (priority != null) {
            try {
                Task.Priority priorityEnum = Task.Priority.valueOf(priority);
                return taskRepository.findByPriority(priorityEnum, pageable);
            } catch (IllegalArgumentException e) {
                // Invalid priority, return all tasks
                return taskRepository.findAll(pageable);
            }
        }

        // Filter by labels
        if (labels != null && !labels.isEmpty()) {
            return taskRepository.findByLabelIdsAll(labels, labels.size(), pageable);
        }

        // Filter by date range
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            return taskRepository.findByDueDateBetween(startDateTime, endDateTime, pageable);
        }

        // Search by title or description
        if (search != null && !search.isBlank()) {
            return taskRepository.findByTitleOrDescriptionContainingIgnoreCase(search, pageable);
        }

        // Default: return all tasks
        return taskRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Task createTask(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getDescription());
        task.setDueDate(taskCreateDTO.getDueDate());

        // Set priority (default to medium if not provided or invalid)
        if (taskCreateDTO.getPriority() != null) {
            try {
                task.setPriority(Task.Priority.valueOf(taskCreateDTO.getPriority()));
            } catch (IllegalArgumentException e) {
                task.setPriority(Task.Priority.medium);
            }
        } else {
            task.setPriority(Task.Priority.medium);
        }

        // Set completion status (default to false if not provided)
        task.setCompleted(taskCreateDTO.getIsCompleted() != null ? taskCreateDTO.getIsCompleted() : false);

        // Set labels if provided
        if (taskCreateDTO.getLabels() != null && !taskCreateDTO.getLabels().isEmpty()) {
            Set<Label> taskLabels = new HashSet<>(labelRepository.findAllById(taskCreateDTO.getLabels()));
            task.setLabels(taskLabels);
        }

        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new BizException(DomainCode.BAD_REQUEST));
    }

    @Override
    @Transactional
    public Task updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = getTaskById(id);

        // Update only the fields that are provided
        if (taskUpdateDTO.getTitle() != null) {
            task.setTitle(taskUpdateDTO.getTitle());
        }

        if (taskUpdateDTO.getDescription() != null) {
            task.setDescription(taskUpdateDTO.getDescription());
        }

        if (taskUpdateDTO.getDueDate() != null) {
            task.setDueDate(taskUpdateDTO.getDueDate());
        }

        if (taskUpdateDTO.getPriority() != null) {
            try {
                task.setPriority(Task.Priority.valueOf(taskUpdateDTO.getPriority()));
            } catch (IllegalArgumentException e) {
                // Keep existing priority if invalid
            }
        }

        if (taskUpdateDTO.getIsCompleted() != null) {
            task.setCompleted(taskUpdateDTO.getIsCompleted());
        }

        if (taskUpdateDTO.getLabels() != null) {
            Set<Label> taskLabels = new HashSet<>(labelRepository.findAllById(taskUpdateDTO.getLabels()));
            task.setLabels(taskLabels);
        }

        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public List<Task> batchUpdateTasks(BatchUpdateRequestDTO batchUpdateRequestDTO) {
        List<Task> updatedTasks = new ArrayList<>();

        for (BatchUpdateRequestDTO.TaskBatchUpdateDTO updateItem : batchUpdateRequestDTO.getUpdates()) {
            Long taskId = updateItem.getId();
            TaskUpdateDTO taskUpdateDTO = updateItem.getTask();

            try {
                Task updatedTask = updateTask(taskId, taskUpdateDTO);
                updatedTasks.add(updatedTask);
            } catch (Exception e) {
                // Skip tasks that don't exist
                continue;
            }
        }

        return updatedTasks;
    }

    @Override
    @Transactional
    public Task toggleTaskCompletion(Long id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }
}