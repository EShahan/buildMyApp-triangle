package org.launchcode.buildMyApptriangle.controllers;

import jakarta.validation.Valid;
import org.launchcode.buildMyApptriangle.models.Employee;
import org.launchcode.buildMyApptriangle.models.Employee;
import org.launchcode.buildMyApptriangle.models.data.EmployeeRepository;
import org.launchcode.buildMyApptriangle.models.data.RoleRepository;
import org.launchcode.buildMyApptriangle.security.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("employees")
public class EmployeeController {
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employees/index";
    }

    @GetMapping("add")
    public String displayAddEmployeeForm(Model model) {
        model.addAttribute(new Employee());
        return "employees/add";
    }

    @PostMapping(
            value = "add",
            // In order to export to database when encrypted, the data has to be changed to a specific type.
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public String processAddEmployeeForm(@ModelAttribute @Valid Employee newEmployee,
                                         Errors errors, Model model) {
        newEmployee.setEmployeeRoles(Arrays.asList(roleRepository.findByName("ROLE_EMPLOYEE")));
        newEmployee.setPassword(passwordEncoder.encode(newEmployee.getPassword()));
        if (errors.hasErrors()) {
            return "employees/add";
        }

        try {
            userDetailsService.loadUserByUsername(newEmployee.getUsername());
        }   catch (Exception UsernameNotFoundException) {
            userDetailsService.createEmployee(newEmployee);
            return "redirect:";
        }
        return "register";
    }

    @GetMapping("delete")
    public String displayDeleteEmployeeForm(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "employees/delete";
    }

    @PostMapping("delete")
    public String processDeleteEmployeeForm(@ModelAttribute @Valid Employee deleteEmployee) {
        employeeRepository.deleteById(deleteEmployee.getId());
        return "redirect:";
    }

    @GetMapping("view/{id}")
    public String displayViewEmployee(Model model, @PathVariable Long id) {
        Optional optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = (Employee) optionalEmployee.get();
            model.addAttribute("employee", employee);
            return "employees/view";
        } else {
            return "redirect:/employees/";
        }
    }

    @GetMapping("view/{id}/update")
    public String displayUpdateEmployee(Model model, @PathVariable Long id) {
        Optional optionalEmpoyee = employeeRepository.findById(id);
        if (optionalEmpoyee.isPresent()) {
            Employee employee = (Employee) optionalEmpoyee.get();
            model.addAttribute("employee", employee);
            return "employees/update";
        } else {
            return "redirect:/employees/";
        }
    }

    @PostMapping(
            value = "view/{id}/update",
            // In order to export to database when encrypted, the data has to be changed to a specific type.
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public String processUpdateEmployee(Model model, @PathVariable Long id, @ModelAttribute @Valid Employee employee,
                                        Errors errors) {
        if (errors.hasErrors()) {
            return "view/"+ id + "/update";
        }
        else {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            employeeRepository.save(employee);
        }
        return "redirect:/employees/view/" + id;
    }
}