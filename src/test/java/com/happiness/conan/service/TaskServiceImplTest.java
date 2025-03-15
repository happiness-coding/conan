package com.happiness.conan.service;

import com.happiness.conan.domain.model.Label;
import com.happiness.conan.domain.model.Task;
import com.happiness.conan.domain.repository.LabelRepository;
import com.happiness.conan.domain.repository.TaskRepository;
import com.happiness.conan.exception.BizException;
import com.happiness.conan.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        // Set up test data
        sampleTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority(Task.Priority.medium)
                .isCompleted(false)
                .dueDate(LocalDateTime.now().plusDays(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .labels(new HashSet<>())
                .build();

        taskList = List.of(sampleTask);
    }

    @Nested
    @DisplayName("Finding Tasks")
    class FindTasks {

        @Test
        @DisplayName("Given no filters, when findTasks is called, then return all tasks")
        void givenNoFilters_whenFindTasks_thenReturnAllTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, null, null, null, null, null, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Task");
            verify(taskRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Given status filter, when findTasks is called, then return filtered tasks")
        void givenStatusFilter_whenFindTasks_thenReturnFilteredTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findByIsCompleted(anyBoolean(), any(Pageable.class))).thenReturn(
                    taskPage);

            // When
            Page<Task> result = taskService.findTasks("active", null, null, null, null, null,
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findByIsCompleted(eq(false), eq(pageable));
        }

        @Test
        @DisplayName("Given priority filter, when findTasks is called, then return filtered tasks")
        void givenPriorityFilter_whenFindTasks_thenReturnFilteredTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findByPriority(any(Task.Priority.class),
                    any(Pageable.class))).thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, "high", null, null, null, null,
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findByPriority(eq(Task.Priority.high), eq(pageable));
        }

        @Test
        @DisplayName("Given invalid priority filter, when findTasks is called, then return all tasks")
        void givenInvalidPriorityFilter_whenFindTasks_thenReturnAllTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, "invalid", null, null, null, null,
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Given label filter, when findTasks is called, then return filtered tasks")
        void givenLabelFilter_whenFindTasks_thenReturnFilteredTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            List<Long> labelIds = List.of(1L, 2L);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findByLabelIdsAll(anyList(), anyLong(),
                    any(Pageable.class))).thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, null, labelIds, null, null, null,
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findByLabelIdsAll(eq(labelIds), eq(2L), eq(pageable));
        }

        @Test
        @DisplayName("Given date range filter, when findTasks is called, then return filtered tasks")
        void givenDateRangeFilter_whenFindTasks_thenReturnFilteredTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now().plusDays(7);
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findByDueDateBetween(any(LocalDateTime.class),
                    any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, null, null, start, end, null, pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findByDueDateBetween(
                    eq(start.atStartOfDay()),
                    eq(end.atTime(LocalTime.MAX)),
                    eq(pageable)
            );
        }

        @Test
        @DisplayName("Given search term, when findTasks is called, then return matching tasks")
        void givenSearchTerm_whenFindTasks_thenReturnMatchingTasks() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            String search = "test";
            Page<Task> taskPage = new PageImpl<>(taskList, pageable, taskList.size());
            when(taskRepository.findByTitleOrDescriptionContainingIgnoreCase(anyString(),
                    any(Pageable.class)))
                    .thenReturn(taskPage);

            // When
            Page<Task> result = taskService.findTasks(null, null, null, null, null, search,
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(taskRepository).findByTitleOrDescriptionContainingIgnoreCase(eq(search),
                    eq(pageable));
        }
    }

    @Nested
    @DisplayName("Create Task")
    class CreateTask {

        @Test
        @DisplayName("Given valid task data, when createTask is called, then create and return task")
        void givenValidTaskData_whenCreateTask_thenCreateAndReturnTask() {
            // Given
            TaskCreateDTO createDTO = TaskCreateDTO.builder()
                    .title("New Task")
                    .description("New Description")
                    .priority("high")
                    .isCompleted(false)
                    .dueDate(LocalDateTime.now().plusDays(1))
                    .build();

            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            Task result = taskService.createTask(createDTO);

            // Then
            assertThat(result).isNotNull();
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getTitle()).isEqualTo("New Task");
            assertThat(savedTask.getPriority()).isEqualTo(Task.Priority.high);
        }

        @Test
        @DisplayName("Given task with labels, when createTask is called, then create task with labels")
        void givenTaskWithLabels_whenCreateTask_thenCreateTaskWithLabels() {
            // Given
            LabelDTO label1 = new LabelDTO(1L, "Feature", "blue");
            LabelDTO label2 = new LabelDTO(2L, "Bug", "red");
            List<LabelDTO> labels = List.of(label1, label2);

            TaskCreateDTO createDTO = TaskCreateDTO.builder()
                    .title("New Task")
                    .description("New Description")
                    .priority("high")
                    .labels(labels)
                    .build();

            Label labelEntity1 = new Label();
            labelEntity1.setId(1L);
            labelEntity1.setName("Feature");

            Label labelEntity2 = new Label();
            labelEntity2.setId(2L);
            labelEntity2.setName("Bug");

            when(labelRepository.findAllById(anyList()))
                    .thenReturn(List.of(labelEntity1, labelEntity2));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            taskService.createTask(createDTO);

            // Then
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getLabels()).hasSize(2);
        }

        @Test
        @DisplayName("Given no priority, when createTask is called, then default to medium priority")
        void givenNoPriority_whenCreateTask_thenDefaultToMediumPriority() {
            // Given
            TaskCreateDTO createDTO = TaskCreateDTO.builder()
                    .title("New Task")
                    .description("New Description")
                    .priority(null)
                    .build();

            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            taskService.createTask(createDTO);

            // Then
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getPriority()).isEqualTo(Task.Priority.medium);
        }
    }

    @Nested
    @DisplayName("Get Task By ID")
    class GetTaskById {

        @Test
        @DisplayName("Given existing task ID, when getTaskById is called, then return task")
        void givenExistingTaskId_whenGetTaskById_thenReturnTask() {
            // Given
            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(sampleTask));

            // When
            Task result = taskService.getTaskById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Task");
        }

        @Test
        @DisplayName("Given non-existing task ID, when getTaskById is called, then throw exception")
        void givenNonExistingTaskId_whenGetTaskById_thenThrowException() {
            // Given
            when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BizException.class, () -> taskService.getTaskById(999L));
        }
    }

    @Nested
    @DisplayName("Update Task")
    class UpdateTask {

        @Test
        @DisplayName("Given existing task and update data, when updateTask is called, then update and return task")
        void givenExistingTaskAndUpdateData_whenUpdateTask_thenUpdateAndReturnTask() {
            // Given
            TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                    .title("Updated Title")
                    .description("Updated Description")
                    .priority("low")
                    .isCompleted(true)
                    .build();

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            Task result = taskService.updateTask(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getTitle()).isEqualTo("Updated Title");
            assertThat(savedTask.getDescription()).isEqualTo("Updated Description");
            assertThat(savedTask.getPriority()).isEqualTo(Task.Priority.low);
            assertThat(savedTask.isCompleted()).isTrue();
        }

    }

    @Nested
    @DisplayName("Delete Task")
    class DeleteTask {

        @Test
        @DisplayName("Given existing task ID, when deleteTask is called, then delete task")
        void givenExistingTaskId_whenDeleteTask_thenDeleteTask() {
            // Given
            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(sampleTask));
            doNothing().when(taskRepository).delete(any(Task.class));

            // When
            taskService.deleteTask(1L);

            // Then
            verify(taskRepository).findById(1L);
            verify(taskRepository).delete(sampleTask);
        }

        @Test
        @DisplayName("Given non-existing task ID, when deleteTask is called, then throw exception")
        void givenNonExistingTaskId_whenDeleteTask_thenThrowException() {
            // Given
            when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BizException.class, () -> taskService.deleteTask(999L));
            verify(taskRepository, never()).delete(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Batch Update Tasks")
    class BatchUpdateTasks {

        @Test
        @DisplayName("Given batch update request, when batchUpdateTasks is called, then update and return tasks")
        void givenBatchUpdateRequest_whenBatchUpdateTasks_thenUpdateAndReturnTasks() {
            // Given
            TaskUpdateDTO updateDTO1 = TaskUpdateDTO.builder()
                    .title("Updated Task 1")
                    .priority("high")
                    .build();

            TaskUpdateDTO updateDTO2 = TaskUpdateDTO.builder()
                    .title("Updated Task 2")
                    .isCompleted(true)
                    .build();

            BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem1 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
            batchItem1.setId(1L);
            batchItem1.setTask(updateDTO1);

            BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem2 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
            batchItem2.setId(2L);
            batchItem2.setTask(updateDTO2);

            BatchUpdateRequestDTO batchUpdateRequestDTO = new BatchUpdateRequestDTO();
            batchUpdateRequestDTO.setUpdates(List.of(batchItem1, batchItem2));

            Task task1 = Task.builder()
                    .id(1L)
                    .title("Updated Task 1")
                    .priority(Task.Priority.high)
                    .build();

            Task task2 = Task.builder()
                    .id(2L)
                    .title("Updated Task 2")
                    .isCompleted(true)
                    .build();

            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.findById(2L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(Task.class))).thenReturn(task1).thenReturn(task2);

            // When
            List<Task> result = taskService.batchUpdateTasks(batchUpdateRequestDTO);

            // Then
            assertThat(result).hasSize(2);
            verify(taskRepository, times(2)).save(any(Task.class));
        }

        @Test
        @DisplayName("Given batch update with non-existing task, when batchUpdateTasks is called, then skip non-existing task")
        void givenBatchUpdateWithNonExistingTask_whenBatchUpdateTasks_thenSkipNonExistingTask() {
            // Given
            TaskUpdateDTO updateDTO1 = TaskUpdateDTO.builder()
                    .title("Updated Task 1")
                    .build();

            TaskUpdateDTO updateDTO2 = TaskUpdateDTO.builder()
                    .title("Updated Task 2")
                    .build();

            BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem1 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
            batchItem1.setId(1L);
            batchItem1.setTask(updateDTO1);

            BatchUpdateRequestDTO.TaskBatchUpdateDTO batchItem2 = new BatchUpdateRequestDTO.TaskBatchUpdateDTO();
            batchItem2.setId(999L); // Non-existing ID
            batchItem2.setTask(updateDTO2);

            BatchUpdateRequestDTO batchUpdateRequestDTO = new BatchUpdateRequestDTO();
            batchUpdateRequestDTO.setUpdates(List.of(batchItem1, batchItem2));

            when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(taskRepository.findById(999L)).thenReturn(Optional.empty()); // Simulating non-existing task
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            List<Task> result = taskService.batchUpdateTasks(batchUpdateRequestDTO);

            // Then
            assertThat(result).hasSize(1); // Only one task should be updated
            verify(taskRepository, times(1)).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Toggle Task Completion")
    class ToggleTaskCompletion {

        @Test
        @DisplayName("Given incomplete task, when toggleTaskCompletion is called, then mark as complete")
        void givenIncompleteTask_whenToggleTaskCompletion_thenMarkAsComplete() {
            // Given
            Task task = Task.builder()
                    .id(1L)
                    .title("Test Task")
                    .isCompleted(false)
                    .build();

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Task result = taskService.toggleTaskCompletion(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isCompleted()).isTrue();

            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("Given complete task, when toggleTaskCompletion is called, then mark as incomplete")
        void givenCompleteTask_whenToggleTaskCompletion_thenMarkAsIncomplete() {
            // Given
            Task task = Task.builder()
                    .id(1L)
                    .title("Test Task")
                    .isCompleted(true)
                    .build();

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Task result = taskService.toggleTaskCompletion(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isCompleted()).isFalse();

            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("Given non-existing task ID, when toggleTaskCompletion is called, then throw exception")
        void givenNonExistingTaskId_whenToggleTaskCompletion_thenThrowException() {
            // Given
            when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(BizException.class, () -> taskService.toggleTaskCompletion(999L));
            verify(taskRepository, never()).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Update Task with Labels")
    class UpdateTaskWithLabels {

        @Test
        @DisplayName("Given task update with labels, when updateTask is called, then update task with new labels")
        void givenTaskUpdateWithLabels_whenUpdateTask_thenUpdateTaskWithNewLabels() {
            // Given
            LabelDTO label1 = new LabelDTO(1L, "Feature", "blue");
            LabelDTO label2 = new LabelDTO(2L, "Bug", "red");
            List<LabelDTO> labels = List.of(label1, label2);

            TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .labels(labels)
                    .build();

            Label labelEntity1 = new Label();
            labelEntity1.setId(1L);
            labelEntity1.setName("Feature");

            Label labelEntity2 = new Label();
            labelEntity2.setId(2L);
            labelEntity2.setName("Bug");

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(sampleTask));
            when(labelRepository.findAllById(anyList())).thenReturn(List.of(labelEntity1, labelEntity2));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            Task result = taskService.updateTask(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();

            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getLabels()).hasSize(2);

            ArgumentCaptor<List<Long>> labelIdsCaptor = ArgumentCaptor.forClass(List.class);
            verify(labelRepository).findAllById(labelIdsCaptor.capture());
            List<Long> capturedLabelIds = labelIdsCaptor.getValue();
            assertThat(capturedLabelIds).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("Given invalid priority in update, when updateTask is called, then keep existing priority")
        void givenInvalidPriorityInUpdate_whenUpdateTask_thenKeepExistingPriority() {
            // Given
            Task.Priority originalPriority = sampleTask.getPriority();
            TaskUpdateDTO updateDTO = TaskUpdateDTO.builder()
                    .title("Updated Task")
                    .priority("invalid_priority")
                    .build();

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(sampleTask));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            // When
            taskService.updateTask(1L, updateDTO);

            // Then
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getPriority()).isEqualTo(originalPriority);
        }
    }
}