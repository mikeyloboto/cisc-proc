package com.evilbas.discproc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        // If target is dead, find next viable target
        if (creature.getCurrentHp() == 0) {
            for (Creature c : character.getCurrentEncounter().getCreatures()) {
                if (c.getCurrentHp() > 0) {
                    creature = c;
                    break;
                }
            }
        }
        result.setMessage("Dealt 5 damage to " + creature.getName() + ".");
        Boolean death = creature.damage(5L);
        if (death)
            result.setMessage(result.getMessage() + "\n" + creature.getName() + " dies.");

        for (Creature c : character.getCurrentEncounter().getCreatures()) {
            if (c.getCurrentHp() > 0) {
                result.setFinished(false);
            }
        }

        if (result.isFinished()) {
            result.setMessage(result.getMessage() + "\nAll enemies are defeated. Granted "
                    + character.getCurrentEncounter().getEncounterExp(character) + "EXP.");
            character.addExperience(character.getCurrentEncounter().getEncounterExp(character));
            character.getInventory().addItem(InventoryService.generateHealingItem());
            character.getInventory().addItem(InventoryService.generateHarmItem());
            character.setCurrentEncounter(null);
        }

        result.setEncounter(character.getCurrentEncounter());
        characterService.saveCharacter(character);

        return result;
    }

    private static Encounter generateMockEncounter() {

        List<Creature> creatures = new ArrayList<>();

        Integer cNum = new Random().nextInt(3) + 1;
        for (int i = 0; i < cNum; i++) {
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
