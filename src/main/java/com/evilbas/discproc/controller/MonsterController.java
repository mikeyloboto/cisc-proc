package com.evilbas.discproc.controller;

import java.util.List;

import com.evilbas.discproc.service.MonsterService;
import com.evilbas.rslengine.creature.Creature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("monster")
public class MonsterController {

    private static final Logger log = LoggerFactory.getLogger(CombatController.class);

    @Autowired
    MonsterService monsterService;

    @GetMapping(value = "/", produces = "application/json")
    public List<Creature> getAllMonsters() {
        return monsterService.getCreatures();
    }

    @DeleteMapping(value = "/", produces = "application/json")
    public List<Creature> deleteMonster(@RequestParam String name) {
        return monsterService.deleteByName(name);
    }

    @PostMapping(value = "/", produces = "application/json")
    public List<Creature> addMonster(@RequestBody Creature creature) {
        return monsterService.addCreature(creature);
    }
}
