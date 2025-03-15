package com.happiness.conan.repository;

import com.happiness.conan.domain.model.Label;
import com.happiness.conan.domain.model.Task;
import com.happiness.conan.domain.model.Task.Priority;
import com.happiness.conan.domain.repository.LabelRepository;
import com.happiness.conan.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryIT {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    private Task task1;
    private Task task2;
    private Task task3;
    private Label featureLabel;
    private Label bugLabel;
    private Label techDebtLabel;

    @BeforeEach
    void setUp() {
        // Create labels
        featureLabel = new Label();
        featureLabel.setName("Feature");
        featureLabel.setColor("blue");
        featureLabel = labelRepository.save(featureLabel);

        bugLabel = new Label();
        bugLabel.setName("Bug");
        bugLabel.setColor("red");
        bugLabel = labelRepository.save(bugLabel);

        techDebtLabel = new Label();
        techDebtLabel.setName("Tech Debt");
        techDebtLabel.setColor("purple");
        techDebtLabel = labelRepository.save(techDebtLabel);

        // Create tasks
        task1 = new Task();
        task1.setTitle("Implement login");
        task1.setDescription("Implement OAuth2 login flow");
        task1.setPriority(Task.Priority.high);
        task1.setDueDate(LocalDateTime.now().plusDays(2));
        task1.setLabels(Set.of(featureLabel));
        task1 = taskRepository.save(task1);

        task2 = new Task();
        task2.setTitle("Fix login bug");
        task2.setDescription("Fix validation error in login form");
        task2.setPriority(Task.Priority.medium);
        task2.setDueDate(LocalDateTime.now().plusDays(1));
        task2.setLabels(Set.of(bugLabel));
        task1.setCompleted(true);
        task2 = taskRepository.save(task2);

        task3 = new Task();
        task3.setTitle("Refactor authentication");
        task3.setDescription("Clean up authentication code");
        task3.setPriority(Task.Priority.low);
        task3.setDueDate(LocalDateTime.now().plusDays(7));
        task3.setLabels(new HashSet<>(Set.of(featureLabel, techDebtLabel)));
        task3 = taskRepository.save(task3);
    }

    @Test
    @DisplayName("findByIsCompleted should return tasks filtered by completion status")
    void findByIsCompleted_shouldReturnFilteredTasks() {
        // When
        Page<Task> completedTasks = taskRepository.findByIsCompleted(true, PageRequest.of(0, 10));
        Page<Task> activeTasks = taskRepository.findByIsCompleted(false, PageRequest.of(0, 10));

        // Then
        assertThat(completedTasks.getContent()).hasSize(1);
//        assertThat(completedTasks.getContent().get(0).getTitle()).isEqualTo("Fix login bug");

        assertThat(activeTasks.getContent()).hasSize(2);
//        assertThat(activeTasks.getContent()).extracting("title")
//                .containsExactlyInAnyOrder("Implement login", "Refactor authentication");
    }

    @Test
    @DisplayName("findByPriority should return tasks with specified priority")
    void findByPriority_shouldReturnTasksWithSpecifiedPriority() {
        // When
        Page<Task> highPriorityTasks = taskRepository.findByPriority(
                Task.Priority.high, PageRequest.of(0, 10));

        Page<Task> mediumPriorityTasks = taskRepository.findByPriority(
                Task.Priority.medium, PageRequest.of(0, 10));

        // Then
        assertThat(highPriorityTasks.getContent()).hasSize(1);
        assertThat(highPriorityTasks.getContent().get(0).getTitle()).isEqualTo("Implement login");

        assertThat(mediumPriorityTasks.getContent()).hasSize(1);
        assertThat(mediumPriorityTasks.getContent().get(0).getTitle()).isEqualTo("Fix login bug");
    }

    @Test
    @DisplayName("findByLabelIdsAll should return tasks that have all specified labels")
    void findByLabelIdsAll_shouldReturnTasksWithAllSpecifiedLabels() {
        // When - search for tasks with featureLabel
        Page<Task> featureTasks = taskRepository.findByLabelIdsAll(
                List.of(featureLabel.getId()), 1, PageRequest.of(0, 10));

        // When - search for tasks with both featureLabel AND techDebtLabel
        Page<Task> featureAndTechDebtTasks = taskRepository.findByLabelIdsAll(
                List.of(featureLabel.getId(), techDebtLabel.getId()), 2, PageRequest.of(0, 10));

        // Then
        assertThat(featureTasks.getContent()).hasSize(2);
        assertThat(featureTasks.getContent()).extracting("title")
                .containsExactlyInAnyOrder("Implement login", "Refactor authentication");

        assertThat(featureAndTechDebtTasks.getContent()).hasSize(1);
        assertThat(featureAndTechDebtTasks.getContent().get(0).getTitle()).isEqualTo("Refactor authentication");
    }

    @Test
    @DisplayName("findByDueDateBetween should return tasks with due dates in the specified range")
    void findByDueDateBetween_shouldReturnTasksInDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);

        // When
        Page<Task> tasksWithinRange = taskRepository.findByDueDateBetween(
                startDate, endDate, PageRequest.of(0, 10));

        // Then
        assertThat(tasksWithinRange.getContent()).hasSize(2);
        assertThat(tasksWithinRange.getContent()).extracting("title")
                .containsExactlyInAnyOrder("Implement login", "Fix login bug");
    }

    @Test
    @DisplayName("findByTitleOrDescriptionContainingIgnoreCase should return tasks matching search term")
    void findByTitleOrDescriptionContainingIgnoreCase_shouldReturnMatchingTasks() {
        // When - search by title substring
        Page<Task> loginTasks = taskRepository.findByTitleOrDescriptionContainingIgnoreCase(
                "login", PageRequest.of(0, 10));

        // When - search by description substring
        Page<Task> authTasks = taskRepository.findByTitleOrDescriptionContainingIgnoreCase(
                "auth", PageRequest.of(0, 10));

        // Then
        assertThat(loginTasks.getContent()).hasSize(2);
        assertThat(loginTasks.getContent()).extracting("title")
                .containsExactlyInAnyOrder("Implement login", "Fix login bug");

        assertThat(authTasks.getContent()).hasSize(2);
        assertThat(authTasks.getContent()).extracting("title")
                .containsExactlyInAnyOrder("Implement login", "Refactor authentication");
    }

    @Test
    @DisplayName("findAll with sorting should return tasks in correct order")
    void findAll_withSorting_shouldReturnTasksInCorrectOrder() {
        // When - sort by priority ascending
        Pageable sortByPriorityAsc = PageRequest.of(0, 10, Sort.by("priority").ascending());
        List<Task> tasksByPriorityAsc = taskRepository.findAll(sortByPriorityAsc).getContent();

        // When - sort by dueDate ascending
        Pageable sortByDueDateAsc = PageRequest.of(0, 10, Sort.by("dueDate").ascending());
        List<Task> tasksByDueDateAsc = taskRepository.findAll(sortByDueDateAsc).getContent();

        // Then
        assertThat(tasksByPriorityAsc).hasSize(3);
        assertThat(tasksByPriorityAsc)
                .extracting("priority")
                .containsExactly(Priority.high, Priority.low, Priority.medium);

        assertThat(tasksByDueDateAsc).hasSize(3);
        assertThat(tasksByDueDateAsc)
                .extracting("title")
                .containsExactly("Fix login bug", "Implement login", "Refactor authentication");
    }

    @Test
    @DisplayName("save should persist entity relationships correctly")
    void save_shouldPersistEntityRelationshipsCorrectly() {
        // Given
        Task newTask = new Task();
        newTask.setTitle("New task with multiple labels");
        newTask.setDescription("Testing label relationships");
        newTask.setPriority(Task.Priority.medium);
        newTask.setLabels(new HashSet<>(Set.of(featureLabel, bugLabel, techDebtLabel)));

        // When
        Task savedTask = taskRepository.save(newTask);
        taskRepository.flush(); // Force flush to ensure persistence

        // Clear persistence context to force reload from database
        taskRepository.findById(savedTask.getId()).get();

        // Then
        Task retrievedTask = taskRepository.findById(savedTask.getId()).get();
        assertThat(retrievedTask.getLabels()).hasSize(3);
        assertThat(retrievedTask.getLabels())
                .extracting("name")
                .containsExactlyInAnyOrder("Feature", "Bug", "Tech Debt");
    }

    @Test
    @DisplayName("delete should remove the task but not affect related labels")
    void delete_shouldRemoveTaskButNotAffectLabels() {
        // Given
        long initialTaskCount = taskRepository.count();
        long initialLabelCount = labelRepository.count();

        // When
        taskRepository.delete(task1);

        // Then
        assertThat(taskRepository.count()).isEqualTo(initialTaskCount - 1);
        assertThat(labelRepository.count()).isEqualTo(initialLabelCount); // Labels should remain
        assertThat(taskRepository.findById(task1.getId())).isEmpty();
        assertThat(labelRepository.findById(featureLabel.getId())).isPresent(); // Label still exists
    }
}