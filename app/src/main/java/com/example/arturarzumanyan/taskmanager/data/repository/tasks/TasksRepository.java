package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.ArrayList;

public interface TasksRepository {
    ArrayList<Task> getTasks();

    ArrayList<Task> getTasksFromTaskList(int taskListId);

    void addTask(Task task);

    void updateTaskList(Task task);

    void deleteTask(Task task);
}
