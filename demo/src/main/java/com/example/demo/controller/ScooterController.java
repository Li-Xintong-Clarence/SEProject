package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Scooter;
import com.example.demo.service.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/scooters")
@CrossOrigin
public class ScooterController {

    @Autowired
    private ScooterService scooterService;

    @GetMapping
    public Result<List<Scooter>> findAll() {
        return Result.success(scooterService.findAll());
    }

    @GetMapping("/available")
    public Result<List<Scooter>> findAvailable() {
        return Result.success(scooterService.findAvailable());
    }

    @GetMapping("/{id}")
    public Result<Scooter> findById(@PathVariable Long id) {
        Scooter scooter = scooterService.findById(id);
        if (scooter != null) {
            return Result.success(scooter);
        }
        return Result.error("Scooter not found");
    }

    @PostMapping
    public Result<String> add(@RequestBody Scooter scooter) {
        if (scooterService.save(scooter)) {
            return Result.success("Scooter created successfully");
        }
        return Result.error("Failed to create scooter");
    }

    @PutMapping
    public Result<String> update(@RequestBody Scooter scooter) {
        if (scooterService.update(scooter)) {
            return Result.success("Scooter updated successfully");
        }
        return Result.error("Failed to update scooter");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        if (scooterService.deleteById(id)) {
            return Result.success("Scooter deleted successfully");
        }
        return Result.error("Failed to delete scooter");
    }
}
