package com.evilbas.discproc.controller;

import java.util.List;

import com.evilbas.discproc.service.CharacterService;

import com.evilbas.rslengine.character.Character;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CharacterController {

    private static final Logger log = LoggerFactory.getLogger(CharacterController.class);

    @Autowired
    CharacterService characterService;

    @GetMapping(value = "/character", produces = "application/json")
    public List<Character> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @PostMapping(value = "/character", produces = "application/json")
    public List<Character> updateCharacter(@RequestBody Character character) {
        characterService.saveCharacter(character);

        return characterService.getAllCharacters();
    }
}
