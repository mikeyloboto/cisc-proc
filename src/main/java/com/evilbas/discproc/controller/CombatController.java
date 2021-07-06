package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.DungeonInstanceService;
import com.evilbas.rslengine.creature.Encounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CombatController {

    private static final Logger log = LoggerFactory.getLogger(CombatController.class);

    @Autowired
    DungeonInstanceService dungeonInstanceService;

    @GetMapping(value = "/combat", produces = "application/json")
    public Encounter startCombat(@RequestParam String guid) {
        return dungeonInstanceService.startCombat(guid);
    }

    @GetMapping(value = "/combat/attack", produces = "application/json")
    public Encounter combatAttack(@RequestParam String guid, @RequestParam Integer targetSlot,
            @RequestParam Integer spellSlot) {
        return dungeonInstanceService.combatAttack(guid, targetSlot, spellSlot);
    }
}
