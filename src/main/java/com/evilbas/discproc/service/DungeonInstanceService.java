package com.evilbas.discproc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.evilbas.discproc.engine.CombatInstance;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.creature.Creature;
import com.evilbas.rslengine.creature.Encounter;
import com.evilbas.rslengine.networking.CombatResultWrapper;

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

    public CombatResultWrapper startCombat(String guid) {
        CombatResultWrapper result = new CombatResultWrapper();
        result.setFinished(false);
        Character character = characterService.getCharacter(guid);
        if (character.getCurrentEncounter() == null) {

            character.setCurrentEncounter(generateMockEncounter());
            result.setMessage("Starting fight with " + character.getCurrentEncounter().getCreatureSlot(0));
        } else {
            result.setMessage("Currently fighting " + character.getCurrentEncounter().getCreatureSlot(0));
        }
        characterService.saveCharacter(character);
        result.setEncounter(character.getCurrentEncounter());
        return result;
    }

    public CombatResultWrapper combatAttack(String guid, Integer targetSlot, Integer spellSlot) {

        CombatResultWrapper result = new CombatResultWrapper();
        result.setFinished(true);

        Character character = characterService.getCharacter(guid);
        // calc damage from spellslot
        Creature creature = character.getCurrentEncounter().getCreatureSlot(targetSlot);
        creature.setCurrentHp(creature.getCurrentHp() - 5);
        if (creature.getCurrentHp() < 0L) {
            creature.setCurrentHp(0L);
        }

        for (Creature c : character.getCurrentEncounter().getCreatures()) {
            if (c.getCurrentHp() > 0) {
                result.setFinished(false);
            }
        }

        if (result.isFinished()) {
            result.setMessage(character.getCurrentEncounter().getCreatureSlot(0).getName() + " defeated. Granted "
                    + character.getCurrentEncounter().getEncounterExp(character) + "EXP.");
            character.addExperience(character.getCurrentEncounter().getEncounterExp(character));
        } else {
            result.setMessage(
                    "Dealt 5 damage to " + character.getCurrentEncounter().getCreatureSlot(0).getName() + ".");
        }

        characterService.saveCharacter(character);
        result.setEncounter(character.getCurrentEncounter());
        return result;
    }

    private static Encounter generateMockEncounter() {

        List<Creature> creatures = new ArrayList<>();

        // Integer cNum = new Random().nextInt(3) + 1;
        for (int i = 0; i < 1; i++) {
            Creature creature = new Creature();
            creature.setMaxHp(20L);
            creature.setCurrentHp(20L);
            creature.setName("Kobold");
            creature.setLevel(2);
            creatures.add(creature);
        }
        return new Encounter(creatures);
    }
}
