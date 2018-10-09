package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public interface TaskListsRepository {
    ArrayList<TaskList> getTaskLists();

    void addTaskList(TaskList taskList);

    void updateTaskList(TaskList taskList);

    void deleteTaskList(TaskList taskList);
}
