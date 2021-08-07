package com.evilbas.discproc.service;

import com.evilbas.discproc.util.Constants;
import com.evilbas.rslengine.character.Character;
import com.evilbas.rslengine.creature.Creature;
import com.evilbas.rslengine.item.ConsumableItem;
import com.evilbas.rslengine.item.Inventory;
import com.evilbas.rslengine.item.Item;
import com.evilbas.rslengine.item.ItemStack;
import com.evilbas.rslengine.networking.InventoryInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    CharacterService characterService;

    @Autowired
    MongoTemplate mongoTemplate;

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
        log.info("item: {}", item);
        for (ItemStack s : character.getInventory().getItems()) {
            if (s.getItem().getIcon().equals(item)) {
                log.info("consumable item use");
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
                    case MULTI_ENEMY:
                        if (character.getCurrentEncounter() != null) {
                            for (Creature m : character.getCurrentEncounter().getCreatures()) {
                                m.damage(item.getValue());
                            }
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

    public Item createItem(Item item) {
        mongoTemplate.save(item, Constants.MONGO_ITEMS_SCHEMA);
        return getItem(item.getIcon());
    }

    public Item getItem(String icon) {
        Query query = new Query(Criteria.where("icon").is(icon));
        return mongoTemplate.findOne(query, Item.class);
    }

    public void deleteItem(String icon) {

        Query query = new Query(Criteria.where("icon").is(icon));
        mongoTemplate.remove(query, Item.class, Constants.MONGO_ITEMS_SCHEMA);

    }

}
