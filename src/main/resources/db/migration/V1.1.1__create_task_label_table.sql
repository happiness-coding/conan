-- Create labels table
CREATE TABLE labels (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        color VARCHAR(20) NOT NULL
);

-- Create tasks table
CREATE TABLE tasks (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       due_date TIMESTAMP,
                       priority VARCHAR(10) NOT NULL,
                       is_completed BOOLEAN NOT NULL DEFAULT false,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

-- Create task_labels join table
CREATE TABLE task_labels (
                             task_id BIGINT NOT NULL,
                             label_id BIGINT NOT NULL,
                             PRIMARY KEY (task_id, label_id),
                             CONSTRAINT fk_task_labels_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
                             CONSTRAINT fk_task_labels_label FOREIGN KEY (label_id) REFERENCES labels (id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_tasks_is_completed ON tasks (is_completed);
CREATE INDEX idx_tasks_priority ON tasks (priority);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);