package com.happiness.conan.service;

import com.happiness.conan.domain.model.Label;
import com.happiness.conan.domain.model.Task;
import com.happiness.conan.domain.repository.LabelRepository;
import com.happiness.conan.domain.repository.TaskRepository;
import com.happiness.conan.exception.BizException;
import com.happiness.conan.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    private Label featureLabel;
    private Label bugLabel;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Create test labels
        featureLabel = new Label();
        featureLabel.setName("Feature");
        featureLabel.setColor("blue");
        featureLabel = labelRepository.save(featureLabel);

        bugLabel = new Label();
        bugLabel.setName("Bug");
        bugLabel.setColor("red");
        bugLabel = labelRepository.save(bugLabel);

        // Create test task
        testTask = new Task();
        testTask.setTitle("Integration Test Task");
        testTask.setDescription("Testing task service integration");
        testTask.setPriority(Task.Priority.medium);
        testTask.setCompleted(false);
        testTask.setDueDate(LocalDateTime.now().plusDays(1));
        testTask = taskRepository.save(testTask);
    }

    @Test
    @DisplayName("When creating a task, it should be saved in the database")
    void whenCreateTask_thenTaskIsSavedInDatabase() {
        // Given
        TaskCreateDTO createDTO = TaskCreateDTO.builder()
                .title("New Integration Task")
                .description("Task created in integration test")
                .priority("high")
                .isCompleted(false)
                .dueDate(LocalDateTime.now().plusDays(2))
                .labels(List.of(
                        new LabelDTO(featureLabel.getId(), null, null),
                        new LabelDTO(bugLabel.getId(), null, null)
                ))
                .build();

        // When
        Task createdTask = taskService.createTask(createDTO);

        // Then
        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getId()).isNotNull();

        Task persistedTask = taskRepository.findById(createdTask.getId()).orElse(null);
        assertThat(persistedTask).isNotNull();
        assertThat(persistedTask.getTitle()).isEqualTo("New Integration Task");
        assertThat(persistedTask.getPriority()).isEqualTo(Task.Priority.high);
        assertThat(persistedTask.getLabels()).hasSize(2);
    }

    @Test
    @DisplayName("When updating a task, changes should be saved in the database")
    void whenUpdateTask_thenChangesAreSavedInDatabase() {
        // Given
        TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                .title("Updated Integration Task")
                .description("Updated in integration test")
                .priority("low")
                .isCompleted(true)
                .labels(List.of(new LabelDTO(featureLabel.getId(), null, null)))
                .build();

        // When
        Task updatedTask = taskService.updateTask(testTask.getId(), updateDTO);

        // Then
        assertThat(updatedTask).isNotNull();
        Task persistedTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(persistedTask).isNotNull();
        assertThat(persistedTask.getTitle()).isEqualTo("Updated Integration Task");
        assertThat(persistedTask.getDescription()).isEqualTo("Updated in integration test");
        assertThat(persistedTask.getPriority()).isEqualTo(Task.Priority.low);
        assertThat(persistedTask.isCompleted()).isTrue();
        assertThat(persistedTask.getLabels()).hasSize(1);
    }

    @Test
    @DisplayName("When finding tasks with filters, should return matching tasks")
    void whenFindTasksWithFilters_thenReturnMatchingTasks() {
        // Given - create multiple tasks with different attributes
        TaskCreateDTO highPriorityTask = TaskCreateDTO.builder()
                .title("High Priority Task")
                .priority("high")
                .build();

        TaskCreateDTO completedTask = TaskCreateDTO.builder()
                .title("Completed Task")
                .isCompleted(true)
                .build();

        taskService.createTask(highPriorityTask);
        taskService.createTask(completedTask);

        Pageable pageable = PageRequest.of(0, 10);

        // When & Then - test various filters

        // Test priority filter
        Page<Task> highPriorityTasks = taskService.findTasks(null, "high", null, null, null, null, pageable);
        assertThat(highPriorityTasks.getContent())
                .isNotEmpty()
                .allMatch(task -> task.getPriority() == Task.Priority.high);

        // Test status filter
        Page<Task> completedTasks = taskService.findTasks("completed", null, null, null, null, null, pageable);
        assertThat(completedTasks.getContent())
                .isNotEmpty()
                .allMatch(Task::isCompleted);

        // Test search filter
        Page<Task> searchedTasks = taskService.findTasks(null, null, null, null, null, "Integration", pageable);
        assertThat(searchedTasks.getContent())
                .isNotEmpty()
                .allMatch(task ->
                        task.getTitle().contains("Integration") ||
                        (task.getDescription() != null && task.getDescription().contains("Integration")));
    }

    @Test
    @DisplayName("When deleting a task, it should be removed from the database")
    void whenDeleteTask_thenTaskIsRemovedFromDatabase() {
        // Given
        Long taskId = testTask.getId();

        // When
        taskService.deleteTask(taskId);

        // Then
        assertThat(taskRepository.findById(taskId)).isEmpty();
    }

    @Test
    @DisplayName("When deleting a non-existent task, should throw exception")
    void whenDeleteNonExistentTask_thenThrowException() {
        // Given
        Long nonExistentId = 9999L;

        // When & Then
        assertThrows(BizException.class, () -> taskService.deleteTask(nonExistentId));
    }

    @Test
    @DisplayName("When batch updating tasks, changes should be applied to all tasks")
    void whenBatchUpdateTasks_thenChangesAreAppliedToAllTasks() {
        // Given
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setPriority(Task.Priority.low);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setPriority(Task.Priority.medium);
        taskRepository.save(task2);


        TaskUpdateDTO updateDTO1 = TaskUpdateDTO.builder()
                .title("Updated Task 1")
                .priority("high")
                .build();

        TaskUpdateDTO updateDTO2 = TaskUpdateDTO.builder()
                .title("Updated Task 2")
                .isCompleted(true)
                .build();

        BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem1 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
        batchItem1.setId(task1.getId());
        batchItem1.setTask(updateDTO1);

        BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem2 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
        batchItem2.setId(task2.getId());
        batchItem2.setTask(updateDTO2);

        BatchUpdateRequestDTO batchUpdateRequestDTO = new BatchUpdateRequestDTO();
        batchUpdateRequestDTO.setUpdates(List.of(batchItem1, batchItem2));

        // When
        List<Task> updatedTasks = taskService.batchUpdateTasks(batchUpdateRequestDTO);

        // Then
        assertThat(updatedTasks).hasSize(2);

        Task persistedTask1 = taskRepository.findById(task1.getId()).orElse(null);
        assertThat(persistedTask1).isNotNull();
        assertThat(persistedTask1.getTitle()).isEqualTo("Updated Task 1");
        assertThat(persistedTask1.getPriority()).isEqualTo(Task.Priority.high);

        Task persistedTask2 = taskRepository.findById(task2.getId()).orElse(null);
        assertThat(persistedTask2).isNotNull();
        assertThat(persistedTask2.getTitle()).isEqualTo("Updated Task 2");
        assertThat(persistedTask2.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("When toggling task completion, status should be inverted")
    void whenToggleTaskCompletion_thenStatusIsInverted() {
        // Given
        boolean initialStatus = testTask.isCompleted();

        // When
        Task updatedTask = taskService.toggleTaskCompletion(testTask.getId());

        // Then
        assertThat(updatedTask.isCompleted()).isEqualTo(!initialStatus);

        Task persistedTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(persistedTask).isNotNull();
        assertThat(persistedTask.isCompleted()).isEqualTo(!initialStatus);
    }
}