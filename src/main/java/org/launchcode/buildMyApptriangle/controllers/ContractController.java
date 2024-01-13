package org.launchcode.buildMyApptriangle.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("contracts")
public class ContractController {

    @GetMapping("/")
    public String index() {
        return "contracts/index";
    }

    @GetMapping("add")
    public String displayAddContractForm() {
        return "contracts/add";
    }

    @GetMapping("delete")
    public String displayDeleteContractForm() {
        return "contracts/delete";
    }

//    @GetMapping("view/{contractId}")
}