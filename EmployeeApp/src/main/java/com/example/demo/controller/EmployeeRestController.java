package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    @Autowired
    private EmployeeService employeeService;

    // GET all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // POST create a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        try {
            if (!employeeService.isValidDesignation(employee.getDesignation())) {
                return ResponseEntity.badRequest()
                        .body("Designation must be: trainer, tester, or programmer.");
            }
            employee.setSalary(employeeService.getSalaryForDesignation(employee.getDesignation()));
            Employee saved = employeeService.saveEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT raise salary by percentage
    @PutMapping("/{id}/salary")
    public ResponseEntity<?> raiseSalary(@PathVariable Integer id,
                                         @RequestParam double percentage) {
        try {
            Employee updated = employeeService.raiseSalary(id, percentage);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET employee by ID – explicit version avoids type inference issues
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Integer id) {
        Optional<Employee> employeeOpt = employeeService.findEmployeeById(id);
        if (employeeOpt.isPresent()) {
            return ResponseEntity.ok(employeeOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found with id: " + id);
        }
    }
}