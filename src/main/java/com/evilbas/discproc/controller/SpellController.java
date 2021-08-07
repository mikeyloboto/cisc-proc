package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.SpellService;
import com.evilbas.rslengine.ability.Spell;
import com.evilbas.rslengine.networking.SpellbookActionRequest;
import com.evilbas.rslengine.networking.SpellbookInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpellController {

    private static final Logger log = LoggerFactory.getLogger(SpellController.class);

    @Autowired
    private SpellService spellService;

    @PostMapping(value = "/spellbook", produces = "application/json", consumes = "application/json")
    public SpellbookInteractionWrapper listInventory(@RequestBody SpellbookActionRequest request) {
        return spellService.listSpells(request.getGuid());
    }

    @PostMapping(value = "/spellbook/use", produces = "application/json", consumes = "application/json")
    public SpellbookInteractionWrapper combatAttack(@RequestBody SpellbookActionRequest request) {
        return spellService.useSpell(request.getGuid(), request.getSpell());
    }

    @PutMapping(value = "/spell", produces = "application/json", consumes = "application/json")
    public Spell createSpell(@RequestBody Spell spell) {
        return spellService.createSpell(spell);
    }

    @PostMapping(value = "/spell", produces = "application/json", consumes = "application/json")
    public Spell getSpell(@RequestBody Spell spell) {
        return spellService.getSpell(spell.getIcon());
    }

    @DeleteMapping(value = "/spell", produces = "application/json", consumes = "application/json")
    public void deleteSpell(@RequestBody Spell spell) {
        spellService.deleteSpell(spell.getIcon());
    }
}
