package com.ecommerce.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    @GetMapping("/whoami")
    public String whoAmI(){
        String containerId = System.getenv("HOSTNAME");
        return "Handled by container: " + containerId + ".\n ";
    }
}
