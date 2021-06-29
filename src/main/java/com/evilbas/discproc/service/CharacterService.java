package com.evilbas.discproc.service;

import com.evilbas.rslengine.character.Character;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class CharacterService {

    private static final Logger log = LoggerFactory.getLogger(CharacterService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    public Character getCharacter(String guid) {
        Query query = new Query(Criteria.where("characterGuid").is(guid));
        log.info("chars: {}", mongoTemplate.findOne(query, Character.class, "characters"));
        Character character = mongoTemplate.findOne(query, Character.class, "characters");

        log.info("character: {}", character);
        return character;
    }

    public Character saveCharacter(Character character) {
        Query query = new Query(Criteria.where("characterGuid").is(character.getCharacterGuid()));
        log.info("chars: {}", mongoTemplate.findOne(query, Character.class, "characters"));
        mongoTemplate.remove(query, Character.class, "characters");
        mongoTemplate.save(character, "characters");

        return getCharacter(character.getCharacterGuid());

    }
}
