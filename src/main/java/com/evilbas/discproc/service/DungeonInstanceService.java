package com.evilbas.discproc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.evilbas.discproc.engine.CombatInstance;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.creature.Creature;
import com.evilbas.rslengine.creature.Encounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DungeonInstanceService {
    private static final Logger log = LoggerFactory.getLogger(DungeonInstanceService.class);
    // private Map<String, CombatInstance> instances;

    @Autowired
    CharacterService characterService;

    public Encounter startCombat(String guid) {

        Character character = characterService.getCharacter(guid);
        if (character.getCurrentEncounter() == null) {
            character.setCurrentEncounter(generateMockEncounter());
        }
        characterService.saveCharacter(character);
        return character.getCurrentEncounter();
    }

    private static Encounter generateMockEncounter() {

        List<Creature> creatures = new ArrayList<>();
        Creature creature = new Creature();
        creature.setMaxHp(20L);
        creature.setCurrentHp(20L);
        creature.setName("Kobold");
        creature.setLevel(2);
        creatures.add(creature);
        return new Encounter(creatures);
    }

    public Encounter combatAttack(String guid, Integer targetSlot, Integer spellSlot) {
        Character character = characterService.getCharacter(guid);
        // calc damage from spellslot
        Creature c = character.getCurrentEncounter().getCreatureSlot(targetSlot);
        c.setCurrentHp(c.getCurrentHp() - 5);
        if (c.getCurrentHp() < 0L) {
            c.setCurrentHp(0L);
        }

        characterService.saveCharacter(character);
        return character.getCurrentEncounter();
    }
}
