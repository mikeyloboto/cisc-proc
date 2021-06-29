package com.evilbas.discproc.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.evilbas.rslengine.character.Character;

public class CombatInstance extends Thread {
    private static final Logger log = LoggerFactory.getLogger(CombatInstance.class);

    private Character character;

    public CombatInstance(Character character) {
        this.character = character;
    }

    @Override
    public void run() {
        log.debug("Running new instance");
    }

    public Character getCharacter() {
        return character;
    }

    public void processCommand(Integer command) {
        log.debug("Command: {}", command);
    }

}
