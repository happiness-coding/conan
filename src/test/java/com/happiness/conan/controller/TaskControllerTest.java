package com.happiness.conan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiness.conan.domain.model.Label;
import com.happiness.conan.domain.model.Task;
import com.happiness.conan.service.TaskService;
import com.happiness.conan.web.controller.TaskController;
import com.happiness.conan.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private Task sampleTask;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        // Set up labels
        Label featureLabel = new Label();
        featureLabel.setId(1L);
        featureLabel.setName("Feature");
        featureLabel.setColor("blue");

        // Set up a sample task
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority(Task.Priority.medium)
                .isCompleted(false)
                .dueDate(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .labels(new HashSet<>(Collections.singletonList(featureLabel)))
                .build();

        taskList = List.of(sampleTask);
    }

    @Test
    @DisplayName("GET /api/v1/tasks should return list of tasks")
    void listTasks_ShouldReturnTaskList() throws Exception {
        // Given
        Page<Task> taskPage = new PageImpl<>(taskList);
        when(taskService.findTasks(
                any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(taskPage); // Return page with sampleTask

        // When/Then
        mockMvc.perform(get("/api/v1/tasks")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(1)))
                .andExpect(jsonPath("$.tasks[0].title", is("Test Task")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.limit", is(10)));
    }

    @Test
    @DisplayName("POST /api/v1/tasks should create a new task")
    void createTask_ShouldCreateAndReturnTask() throws Exception {
        // Given
        TaskCreateDTO createDTO = TaskCreateDTO.builder()
                .title("New Task")
                .description("New Description")
                .priority("high")
                .build();

        when(taskService.createTask(any(TaskCreateDTO.class))).thenReturn(sampleTask);

        // When/Then
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.task.id", is(1)))
                .andExpect(jsonPath("$.task.title", is("Test Task")));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} should return task by id")
    void getTaskById_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.getTaskById(1L)).thenReturn(sampleTask);

        // When/Then
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id", is(1)))
                .andExpect(jsonPath("$.task.title", is("Test Task")))
                .andExpect(jsonPath("$.task.priority", is("medium")))
                .andExpect(jsonPath("$.task.labels", hasSize(1)));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id} should update task")
    void updateTask_ShouldUpdateAndReturnTask() throws Exception {
        // Given
        TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                .title("Updated Task")
                .priority("high")
                .isCompleted(true)
                .build();

        Task updatedTask = Task.builder()
                .id(1L)
                .title("Updated Task")
                .description("Test Description")
                .priority(Task.Priority.high)
                .isCompleted(true)
                .build();

        when(taskService.updateTask(eq(1L), any(TaskUpdateDTO.class))).thenReturn(updatedTask);

        // When/Then
        mockMvc.perform(patch("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id", is(1)))
                .andExpect(jsonPath("$.task.title", is("Updated Task")))
                .andExpect(jsonPath("$.task.priority", is("high")))
                .andExpect(jsonPath("$.task.isCompleted", is(true)));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} should delete task")
    void deleteTask_ShouldReturnSuccess() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(taskService).deleteTask(1L);
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/batch should update multiple tasks")
    void batchUpdateTasks_ShouldUpdateAndReturnTasks() throws Exception {
        // Given
        TaskUpdateDTO updateDTO1 = TaskUpdateDTO.builder()
                .title("Updated Task 1")
                .priority("high")
                .build();

        BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
        batchItem.setId(1L);
        batchItem.setTask(updateDTO1);

        BatchUpdateRequestDTO batchRequest = new BatchUpdateRequestDTO();
        batchRequest.setUpdates(List.of(batchItem));

        when(taskService.batchUpdateTasks(any(BatchUpdateRequestDTO.class))).thenReturn(taskList);

        // When/Then
        mockMvc.perform(patch("/api/v1/tasks/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(1)))
                .andExpect(jsonPath("$.updatedCount", is(1)));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/toggle-complete should toggle task completion")
    void toggleTaskCompletion_ShouldToggleAndReturnTask() throws Exception {
        // Given
        Task toggledTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .isCompleted(true) // Toggled from false to true
                .build();

        when(taskService.toggleTaskCompletion(1L)).thenReturn(toggledTask);

        // When/Then
        mockMvc.perform(patch("/api/v1/tasks/1/toggle-complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id", is(1)))
                .andExpect(jsonPath("$.task.title", is("Test Task")))
                .andExpect(jsonPath("$.task.isCompleted", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/tasks with filters should return filtered tasks")
    void listTasksWithFilters_ShouldReturnFilteredTasks() throws Exception {
        // Given
        Page<Task> taskPage = new PageImpl<>(taskList, PageRequest.of(0, 10), taskList.size());

        when(taskService.findTasks(
                eq("active"), eq("high"), anyList(),
                any(LocalDate.class), any(LocalDate.class),
                eq("search"), any(Pageable.class)))
                .thenReturn(taskPage);

        // When/Then
        mockMvc.perform(get("/api/v1/tasks")
                .param("status", "active")
                .param("priority", "high")
                .param("labels", "1", "2")
                .param("start", "2023-01-01")
                .param("end", "2023-12-31")
                .param("search", "search")
                .param("sortBy", "dueDate")
                .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(1)))
                .andExpect(jsonPath("$.total", is(1)));

        // Verify correct parameters were passed to service
        verify(taskService).findTasks(
                eq("active"),
                eq("high"),
                anyList(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq("search"),
                any(Pageable.class));
    }
}