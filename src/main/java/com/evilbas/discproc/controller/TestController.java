package com.evilbas.discproc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getTestOutput() {
        return "Success!";
    }
}
