package com.evilbas.discproc.service;

import com.evilbas.rslengine.ability.SpellType;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.item.ConsumableItem;
import com.evilbas.rslengine.item.Inventory;
import com.evilbas.rslengine.item.Item;
import com.evilbas.rslengine.item.ItemStack;
import com.evilbas.rslengine.item.property.ItemEffect;
import com.evilbas.rslengine.item.property.ItemRarity;
import com.evilbas.rslengine.item.property.ItemTarget;
import com.evilbas.rslengine.networking.InventoryInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    CharacterService characterService;

    public InventoryInteractionWrapper listInventory(String guid) {
        InventoryInteractionWrapper result = new InventoryInteractionWrapper();
        Character character = characterService.getCharacter(guid);
        if (character.getInventory() == null) {

            character.setInventory(new Inventory());
        }
        characterService.saveCharacter(character);
        result.setInventory(character.getInventory());
        return result;
    }

    public InventoryInteractionWrapper useItem(String guid, String item) {
        InventoryInteractionWrapper result = new InventoryInteractionWrapper();
        Character character = characterService.getCharacter(guid);

        for (ItemStack s : character.getInventory().getItems()) {
            if (s.getItem().getIcon().equals(item) && s.getItem() instanceof ConsumableItem) {
                useConsumableItem(character, s);
                break;
            }
        }

        characterService.saveCharacter(character);
        result.setInventory(character.getInventory());
        return result;
    }

    private void useConsumableItem(Character character, ItemStack s) {
        ConsumableItem item = (ConsumableItem) s.getItem();
        if (s.getAmount() <= 0)
            return;
        switch (item.getEffect()) {
            case HEAL:
                switch (item.getTarget()) {
                    case SELF:
                        character.heal(item.getValue());
                        break;
                    case ENEMY:
                        if (character.getViableTarget() != null) {
                            character.getViableTarget().heal(item.getValue());
                        }
                        break;

                }
                break;
            case HARM:
                switch (item.getTarget()) {
                    case SELF:
                        character.damage(item.getValue());
                        break;
                    case ENEMY:
                        if (character.getViableTarget() != null) {
                            character.getViableTarget().damage(item.getValue());
                        }
                        break;

                }
                break;
        }
        s.setAmount(s.getAmount() - 1);
        if (s.getAmount() <= 0) {
            character.getInventory().getItems().remove(s);
        }

    }

    public static Item generateHealingItem() {
        ConsumableItem item = new ConsumableItem();
        item.setName("Health Potion");
        item.setStackable(true);
        item.setEffect(ItemEffect.HEAL);
        item.setTarget(ItemTarget.SELF);
        item.setIcon("🧪");
        item.setValue(20L);
        item.setRarity(ItemRarity.COMMON);
        item.setType(SpellType.NATURE);
        return item;
    }

    public static Item generateHarmItem() {
        ConsumableItem item = new ConsumableItem();
        item.setName("Firebomb");
        item.setStackable(true);
        item.setEffect(ItemEffect.HARM);
        item.setTarget(ItemTarget.ENEMY);
        item.setIcon("💣");
        item.setValue(20L);
        item.setRarity(ItemRarity.COMMON);
        item.setType(SpellType.FIRE);
        return item;
    }
}
