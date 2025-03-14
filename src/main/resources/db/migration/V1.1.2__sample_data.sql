-- Insert sample labels
INSERT INTO labels (name, color) VALUES
                                     ('Bug', '#FF0000'),
                                     ('Feature', '#00FF00'),
                                     ('Enhancement', '#0000FF'),
                                     ('Documentation', '#FFFF00'),
                                     ('High Priority', '#FF00FF'),
                                     ('Backend', '#00FFFF'),
                                     ('Frontend', '#FFA500');

-- Insert sample tasks
INSERT INTO tasks (title, description, due_date, priority, is_completed, created_at, updated_at) VALUES
                                                                                                     ('Fix login issue', 'Users cannot login with correct credentials', '2025-04-01 12:00:00', 'high', false, '2025-03-14 06:08:47', '2025-03-14 06:08:47'),
                                                                                                     ('Implement user dashboard', 'Create a user dashboard showing activities', '2025-04-10 12:00:00', 'medium', false, '2025-03-14 06:08:47', '2025-03-14 06:08:47'),
                                                                                                     ('Update documentation', 'Update API documentation with new endpoints', '2025-03-25 12:00:00', 'low', false, '2025-03-14 06:08:47', '2025-03-14 06:08:47'),
                                                                                                     ('Performance optimization', 'Optimize database queries for task listing', '2025-03-30 12:00:00', 'medium', false, '2025-03-14 06:08:47', '2025-03-14 06:08:47'),
                                                                                                     ('Add export feature', 'Allow exporting tasks to CSV/Excel', '2025-04-15 12:00:00', 'low', false, '2025-03-14 06:08:47', '2025-03-14 06:08:47');

-- Associate tasks with labels
INSERT INTO task_labels (task_id, label_id) VALUES
                                                (1, 1), -- Fix login issue - Bug
                                                (1, 5), -- Fix login issue - High Priority
                                                (2, 2), -- Implement user dashboard - Feature
                                                (2, 7), -- Implement user dashboard - Frontend
                                                (3, 4), -- Update documentation - Documentation
                                                (4, 6), -- Performance optimization - Backend
                                                (4, 3), -- Performance optimization - Enhancement
                                                (5, 2), -- Add export feature - Feature
                                                (5, 6); -- Add export feature - Backend