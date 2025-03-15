package com.happiness.conan.web.controller;

import com.happiness.conan.domain.model.Task;
import com.happiness.conan.service.TaskService;
import com.happiness.conan.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "List tasks",
            description = "Get a list of tasks with optional filtering by status, priority, labels, date range, and search term"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved tasks",
                    content = @Content(schema = @Schema(implementation = TaskListResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<TaskListResponseDTO> listTasks(
            @Parameter(description = "Page number (starting from 1)", example = "1")
            @RequestParam(defaultValue = "1") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int limit,

            @Parameter(description = "Filter by task status", schema = @Schema(allowableValues = {"all", "active", "completed"}))
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by task priority", schema = @Schema(allowableValues = {"low", "medium", "high"}))
            @RequestParam(required = false) String priority,

            @Parameter(description = "Filter by label IDs")
            @RequestParam(required = false) List<Long> labels,

            @Parameter(description = "Filter by start date (format: yyyy-MM-dd)", example = "2025-03-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @Parameter(description = "Filter by end date (format: yyyy-MM-dd)", example = "2025-03-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,

            @Parameter(description = "Search term for task title/description")
            @RequestParam(required = false) String search,

            @Parameter(description = "Field to sort by", schema = @Schema(allowableValues = {"dueDate", "priority", "createdAt"}))
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {

        // Validate and normalize pagination
        page = Math.max(1, page);
        limit = Math.max(1, Math.min(100, limit));

        // Create Sort object based on sortBy and sortOrder
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortField;
        switch (sortBy) {
            case "dueDate":
                sortField = "dueDate";
                break;
            case "priority":
                sortField = "priority";
                break;
            case "createdAt":
            default:
                sortField = "createdAt";
                break;
        }

        Sort sort = Sort.by(direction, sortField);

        // Create pageable object (page is 0-based for Spring)
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        // Get tasks with filters
        Page<Task> tasksPage = taskService.findTasks(status, priority, labels, start, end, search, pageable);
        if (tasksPage == null) {
            tasksPage = Page.empty(pageable);
        }
        // Convert to DTOs
        List<TaskDTO> taskDTOs = tasksPage.getContent().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());

        // Create response
        TaskListResponseDTO response = TaskListResponseDTO.builder()
                .tasks(taskDTOs)
                .total(tasksPage.getTotalElements())
                .page(page)
                .limit(limit)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new task",
            description = "Create a new task with the provided details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task successfully created",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Parameter(description = "Task details", required = true)
            @Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        Task createdTask = taskService.createTask(taskCreateDTO);

        TaskResponseDTO response = TaskResponseDTO.builder()
                .task(TaskDTO.fromEntity(createdTask))
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get task by ID",
            description = "Get details of a specific task by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        Task task = taskService.getTaskById(id);

        TaskResponseDTO response = TaskResponseDTO.builder()
                .task(TaskDTO.fromEntity(task))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update task",
            description = "Update a specific task by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "Updated task details", required = true)
            @RequestBody TaskUpdateDTO taskUpdateDTO) {
        Task updatedTask = taskService.updateTask(id, taskUpdateDTO);

        TaskResponseDTO response = TaskResponseDTO.builder()
                .task(TaskDTO.fromEntity(updatedTask))
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete task",
            description = "Delete a specific task by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @Operation(
            summary = "Batch update tasks",
            description = "Update multiple tasks in a single request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks updated successfully",
                    content = @Content(schema = @Schema(implementation = BatchUpdateResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PatchMapping("/batch")
    public ResponseEntity<BatchUpdateResponseDTO> batchUpdateTasks(
            @Parameter(description = "Batch update request with task IDs and update details", required = true)
            @Valid @RequestBody BatchUpdateRequestDTO batchUpdateRequestDTO) {
        List<Task> updatedTasks = taskService.batchUpdateTasks(batchUpdateRequestDTO);

        List<TaskDTO> taskDTOs = updatedTasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());

        BatchUpdateResponseDTO response = BatchUpdateResponseDTO.builder()
                .tasks(taskDTOs)
                .updatedCount(taskDTOs.size())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Toggle task completion",
            description = "Toggle the completion status of a task"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task completion status toggled successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PatchMapping("/{id}/toggle-complete")
    public ResponseEntity<TaskResponseDTO> toggleTaskCompletion(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        Task updatedTask = taskService.toggleTaskCompletion(id);

        TaskResponseDTO response = TaskResponseDTO.builder()
                .task(TaskDTO.fromEntity(updatedTask))
                .build();

        return ResponseEntity.ok(response);
    }
}