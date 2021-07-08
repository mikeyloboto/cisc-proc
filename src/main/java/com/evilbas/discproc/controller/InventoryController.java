package com.evilbas.discproc.controller;

import com.evilbas.discproc.service.InventoryService;
import com.evilbas.rslengine.networking.InventoryActionRequest;
import com.evilbas.rslengine.networking.InventoryInteractionWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
}
