package com.evilbas.discproc.service;

import com.evilbas.rslengine.ability.Spell;
import com.evilbas.rslengine.ability.Spellbook;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.creature.Creature;
import com.evilbas.rslengine.networking.SpellbookInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpellService {
    private static final Logger log = LoggerFactory.getLogger(SpellService.class);

    @Autowired
    CharacterService characterService;

    public SpellbookInteractionWrapper listSpells(String guid) {
        SpellbookInteractionWrapper result = new SpellbookInteractionWrapper();
        Character character = characterService.getCharacter(guid);
        if (character.getInventory() == null) {

            character.setSpellbook(new Spellbook());
        }
        characterService.saveCharacter(character);
        result.setSpellbook(character.getSpellbook());
        return result;
    }

    public SpellbookInteractionWrapper useSpell(String guid, String spell) {
        SpellbookInteractionWrapper result = new SpellbookInteractionWrapper();
        Character character = characterService.getCharacter(guid);
        log.info("spell: {}", spell);
        for (Spell s : character.getSpellbook().getSpells()) {
            if (s.getIcon().equals(spell)) {
                log.info("spell use");
                result.addMessage(useSpell(character, s));
                break;
            }
        }

        characterService.saveCharacter(character);
        result.setSpellbook(character.getSpellbook());
        return result;
    }

    private String useSpell(Character character, Spell s) {
        if (character.getMp() == 0L) {
            return "No mana to cast " + s.getSpellName();
        }
        switch (s.getEffect()) {
            case HEAL:
                switch (s.getTarget()) {
                    case SELF:
                        character.heal(s.getModifiers());
                        break;
                    case ENEMY:
                        if (character.getViableTarget() != null) {
                            character.getViableTarget().heal(s.getModifiers());
                        }
                        break;

                }
                break;
            case HARM:
                switch (s.getTarget()) {
                    case SELF:
                        character.damage(s.getModifiers());
                        break;
                    case ENEMY:
                        if (character.getViableTarget() != null) {
                            character.getViableTarget().damage(s.getModifiers());
                        }
                        break;
                    case MULTI_ENEMY:
                        if (character.getCurrentEncounter() != null) {
                            for (Creature m : character.getCurrentEncounter().getCreatures()) {
                                m.damage(s.getModifiers());
                            }
                        }
                        break;

                }
                break;
        }
        character.setMp(character.getMp() - s.getManaCost());
        if (character.getMp() < 0L) {
            character.setMp(0L);
        }
        return s.getSpellName() + " cast.";
    }
}
