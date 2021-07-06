package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.DungeonInstanceService;
import com.evilbas.discproc.service.InventoryService;
import com.evilbas.rslengine.networking.InventoryInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    InventoryService inventoryService;

    @GetMapping(value = "/inventory", produces = "application/json")
    public InventoryInteractionWrapper listInventory(@RequestParam String guid) {
        return inventoryService.listInventory(guid);
    }

    @GetMapping(value = "/inventory/use", produces = "application/json")
    public InventoryInteractionWrapper combatAttack(@RequestParam String guid, @RequestParam String item) {
        return inventoryService.useItem(guid, item);
    }
}
