package com.tasklist.model;

import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Data
@Entity
@Table(name="task_list")
@RestResource(rel="tasklists",path="tasklists")
public class TaskList {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "tasklists")
    private Set<User> users = new HashSet<>();

    @Column(length = 1000000)
    private String jsonTaskList;

    @Column
    private String taskListName;

    @Column
    private String taskListDescription;

    public TaskList(String jsonTaskList) {
        this.jsonTaskList = jsonTaskList;
        this.taskListName = "New Tasklist";
    }

    public TaskList(String jsonTaskList, String taskListName) {
        this.jsonTaskList = jsonTaskList;
        this.taskListName = taskListName;
    }

    public TaskList() {
    }

    // If we print ths users it can create an infinite loop......
    @Override
    public String toString() {
        return "TaskList{" +
                "id=" + id +
                ", jsonTaskList='" + jsonTaskList + '\'' +
                '}';
    }
}

