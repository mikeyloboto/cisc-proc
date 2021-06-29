package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.DungeonInstanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CharacterCreatorController {

    private static final Logger log = LoggerFactory.getLogger(CharacterCreatorController.class);

    @Autowired
    DungeonInstanceService dungeonInstanceService;

    @GetMapping(value = "/combat/")
    public String startCombat(@RequestParam String guid) {
        dungeonInstanceService.startCombat(guid);
        return "Success";
    }
}
