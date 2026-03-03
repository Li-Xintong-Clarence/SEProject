package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("User not found");
    }

    @PostMapping
    public Result<String> add(@RequestBody User user) {
        if (userService.save(user)) {
            return Result.success("User created successfully");
        }
        return Result.error("Failed to create user");
    }

    @PutMapping
    public Result<String> update(@RequestBody User user) {
        if (userService.update(user)) {
            return Result.success("User updated successfully");
        }
        return Result.error("Failed to update user");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (userService.deleteById(id)) {
            return Result.success("User deleted successfully");
        }
        return Result.error("Failed to delete user");
    }
}
