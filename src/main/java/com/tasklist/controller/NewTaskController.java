package com.tasklist.controller;

import com.tasklist.model.TaskList;
import com.tasklist.model.User;
import com.tasklist.repository.TaskListRepository;
import com.tasklist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.tasklist.model.data.Data.newTaskString;


@BasePathAwareController
public class NewTaskController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    private String newTaskStringJson = newTaskString;

    @GetMapping(path = "/users/{user}/tasklists/new")
    public @ResponseBody ResponseEntity<?> createNewTaskList(@PathVariable("user") String user) {  //@RequestBody TaskList taskList @RequestBody binds json to a java object automatically
        System.out.println("Attempting to create a new tasklist for user: " + user);
        User userToUpdate = userRepository.findByUsername(user);
        if (userToUpdate!= null) {
            TaskList taskList = new TaskList(newTaskStringJson);
            userToUpdate.getTasklists().add(taskList);
            userToUpdate = userRepository.save(userToUpdate);
            System.out.println(userToUpdate);
            return ResponseEntity.ok("Tasklist created : " + taskList.toString());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/users/{user}/tasklists/new", consumes = "application/json")
    public @ResponseBody ResponseEntity<?> postNewTaskList(@PathVariable("user") String user, @RequestBody TaskList post_taskList) {  //@RequestBody TaskList taskList @RequestBody binds json to a java object automatically
        System.out.println("Attempting to create a new tasklist for user: " + user);
        User userToUpdate = userRepository.findByUsername(user);
        if (userToUpdate!= null) {
            post_taskList.setJsonTaskList(newTaskStringJson);
            userToUpdate.getTasklists().add(post_taskList);
            userToUpdate = userRepository.save(userToUpdate);
            System.out.println(userToUpdate);
            return ResponseEntity.ok("Tasklist created (ID won't display here) : " + post_taskList.toString());
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional  // Because fetch type is lazy for tasklist, we need Transactional to pull the users for the tasklist
    @DeleteMapping(path = "/users/{user}/tasklists/del/{id}")
    public @ResponseBody ResponseEntity<?> deleteTaskList(@PathVariable("user") String user, @PathVariable("id") long id) {
        System.out.println("If more then one user, only delete association");

        TaskList taskList = taskListRepository.getById(id);

        User requesting_user = userRepository.findByUsername(user);

        if (taskList!=null) {
            Set<User> user_set = taskList.getUsers();
            System.out.println(taskList);
            System.out.println(user_set);

            //If the user in the url is not associated to the taskList, it cannot delete the taskList
            if (!user_set.contains(requesting_user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

           if (user_set.size()==1) {
               for (User user1: user_set) {
                   //Removes the association
                   user1.getTasklists().remove(taskList);
                   userRepository.save(user1);
               }
                // TODO - see if this is redudant
               taskListRepository.delete(taskList);

               return ResponseEntity.ok("TaskList deleted");
           } else {
              User thisUser= userRepository.findByUsername(user);
              thisUser.getTasklists().remove(taskList);
              userRepository.save(thisUser);
              return ResponseEntity.ok("Association removed");
           }
        }
        return ResponseEntity.notFound().build();



    }


    @GetMapping(path = "/testing")
    public @ResponseBody ResponseEntity<?> testing() {
        List<String> list = new ArrayList<>();
        list.add("test");
        Resources<String> resources = new Resources<>(list);
        return ResponseEntity.ok(resources);
    }
}
