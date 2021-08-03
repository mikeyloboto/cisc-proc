package com.evilbas.discproc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.evilbas.rslengine.ability.SpellType;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.creature.Creature;
import com.evilbas.rslengine.creature.Encounter;
import com.evilbas.rslengine.damage.DamageModifier;
import com.evilbas.rslengine.item.ItemWeapon;
import com.evilbas.rslengine.item.property.ItemRarity;
import com.evilbas.rslengine.networking.CombatResultWrapper;
import com.evilbas.rslengine.util.CombatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DungeonInstanceService {
    private static final Logger log = LoggerFactory.getLogger(DungeonInstanceService.class);

    @Autowired
    CharacterService characterService;

    @Autowired
    MonsterService monsterService;

    public CombatResultWrapper startCombat(String guid) {
        CombatResultWrapper result = new CombatResultWrapper();
        result.setFinished(false);
        Character character = characterService.getCharacter(guid);
        if (character.getCurrentEncounter() == null) {

            character.setCurrentEncounter(generateEncounter(character.getCharacterLevel()));
            result.addMessage("Starting fight with " + character.getCurrentEncounter().getCreatureSlot(0));
        } else {
            result.addMessage("Currently fighting " + character.getCurrentEncounter().getCreatureSlot(0));
        }
        characterService.saveCharacter(character);
        result.setEncounter(character.getCurrentEncounter());
        return result;
    }

    public CombatResultWrapper combatAttack(String guid, Integer targetSlot, Integer spellSlot) {

        // Turn beginning

        CombatResultWrapper result = new CombatResultWrapper();
        result.setFinished(true);

        Character character = characterService.getCharacter(guid);
        // calc damage from spellslot
        if (character.getCurrentEncounter() == null) {
            result.addMessage("Not fighting anyone currently");
            return result;
        }
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

        // Calculate Damage

        List<DamageModifier> modifiers = new ArrayList<>();

        if (character.getEquippedWeapon() == null) {
            modifiers.add(CombatUtil.getUnarmedDamageModifier());
        } else {
            modifiers.add(CombatUtil.convertPhysicalDamageModifier(character.getEquippedWeapon().getBaseDamage()));
            if (character.getEquippedWeapon().getModifiers() != null) {
                modifiers.addAll(character.getEquippedWeapon().getModifiers());
            }

            if (character.getEquippedWeapon().getTempModifiers() != null) {
                modifiers.addAll(character.getEquippedWeapon().getTempModifiers());
                for (DamageModifier dm : character.getEquippedWeapon().getTempModifiers()) {
                    dm.setTemporaryRemaining(dm.getTemporaryRemaining() - 1);
                    if (dm.getTemporaryRemaining() == 0) {
                        character.getEquippedWeapon().getTempModifiers().remove(dm);
                    }
                }
            }
        }

        Boolean death = creature.damage(modifiers);

        for (DamageModifier m : modifiers) {
            result.addMessage("Dealt " + m.getAmount() + " " + m.getDamageType().getReadableName(false) + " damage to "
                    + creature.getName() + ".");
        }

        if (death)
            result.addMessage(creature.getName() + " dies.");

        for (Creature c : character.getCurrentEncounter().getCreatures()) {
            if (c.getCurrentHp() > 0) {
                result.setFinished(false);
            }
        }

        // Enemy turn

        Boolean charDeath = false;

        if (!result.isFinished()) {
            Creature attacker = character.getViableTarget();
            DamageModifier modifier = new DamageModifier(SpellType.PHYSICAL, CombatUtil.calculateDamage(attacker));
            charDeath = character.damage(modifier);
            result.addMessage(attacker.getName() + " dealt " + modifier.getAmount() + " "
                    + modifier.getDamageType().getReadableName(false) + " damage.");
            result.setFinished(charDeath);
        }

        if (result.isFinished() && !charDeath) {
            result.addMessage("All enemies are defeated. Granted "
                    + character.getCurrentEncounter().getEncounterExp(character) + "EXP.");
            character.addExperience(character.getCurrentEncounter().getEncounterExp(character));

            // TODO: Remove temp prizes
            character.getInventory().addItem(InventoryService.generateHealingItem());
            character.getInventory().addItem(InventoryService.generateHarmItem());
            character.getInventory().addItem(InventoryService.generateAoeItem());

            character.setCurrentEncounter(null);
        }

        else if (result.isFinished() && charDeath) {
            result.addMessage("You have fainted and woke up in the nearby tavern.");
            character.setCurrentHp(character.getMaxHp());

            character.setCurrentEncounter(null);
        }

        result.setEncounter(character.getCurrentEncounter());
        characterService.saveCharacter(character);

        return result;
    }

    private Encounter generateEncounter(Integer playerLevel) {

        List<Creature> creatures = new ArrayList<>();
        Integer enemiesLimit = (int) Math.floor((double) playerLevel / 3.0d);
        Integer cNum = new Random().nextInt(enemiesLimit) + 1;
        for (int i = 0; i < cNum; i++) {
            creatures.add(monsterService.getRandomScaledCreature(playerLevel));
        }
        return new Encounter(creatures);
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

    private static ItemWeapon generateOPWeapon() {
        ItemWeapon w = new ItemWeapon();
        w.setName("OP Sword of the Dragon");
        w.setStackable(false);
        w.setRarity(ItemRarity.LEGENDARY);
        w.setBaseDamage(10L);
        w.addModifier(new DamageModifier(SpellType.FIRE, 25L));
        w.addModifier(new DamageModifier(SpellType.CHAOS, 5L));
        return w;
    }
}
