package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.InventoryService;
import com.evilbas.rslengine.item.Item;
import com.evilbas.rslengine.networking.InventoryActionRequest;
import com.evilbas.rslengine.networking.InventoryInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    InventoryService inventoryService;

    @PostMapping(value = "/inventory", produces = "application/json", consumes = "application/json")
    public InventoryInteractionWrapper listInventory(@RequestBody InventoryActionRequest request) {
        return inventoryService.listInventory(request.getGuid());
    }

    @PostMapping(value = "/inventory/use", produces = "application/json", consumes = "application/json")
    public InventoryInteractionWrapper combatAttack(@RequestBody InventoryActionRequest request) {
        return inventoryService.useItem(request.getGuid(), request.getItem());
    }

    @PutMapping(value = "/item", produces = "application/json", consumes = "application/json")
    public Item createItem(@RequestBody Item item) {
        return inventoryService.createItem(item);
    }

    @PostMapping(value = "/item", produces = "application/json", consumes = "application/json")
    public Item getItem(@RequestBody Item item) {
        return inventoryService.getItem(item.getIcon());
    }

    @DeleteMapping(value = "/item", produces = "application/json", consumes = "application/json")
    public void deleteItem(@RequestBody Item item) {
        inventoryService.deleteItem(item.getIcon());
    }
}
