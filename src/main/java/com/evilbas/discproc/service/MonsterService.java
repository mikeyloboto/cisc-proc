package com.evilbas.discproc.service;

import java.util.List;
import java.util.Random;

import com.evilbas.discproc.util.Constants;
import com.evilbas.rslengine.creature.Creature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class MonsterService {

    private static final Logger log = LoggerFactory.getLogger(CharacterService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    private Random randomProvider;

    public Creature pickRandomCreature() {
        if (randomProvider == null)
            randomProvider = new Random();

        List<Creature> creatures = getCreatures();
        var index = randomProvider.nextInt(creatures.size());
        return creatures.get(index);

    }

    public Creature getRandomScaledCreature(Integer level) {
        Creature baseCreature = pickRandomCreature();
        var scaleLevel = level + randomProvider.nextInt(7) - 3;

        baseCreature.setMaxHp(baseCreature.getMaxHp() + Math.round(5 * scaleLevel * (baseCreature.getMaxHp() / 10f)));
        baseCreature.setCurrentHp(baseCreature.getMaxHp());

        baseCreature.setLevel(scaleLevel);

        return baseCreature;

    }

    public List<Creature> getCreatures() {
        List<Creature> creatures = mongoTemplate.findAll(Creature.class, Constants.MONGO_CREATURES_SCHEMA);

        log.debug("character: {}", creatures);
        return creatures;
    }

    public List<Creature> addCreature(Creature creature) {
        mongoTemplate.save(creature, Constants.MONGO_CREATURES_SCHEMA);

        return getCreatures();
    }

    public List<Creature> deleteByName(String name) {

        Query query = new Query(Criteria.where("name").is(name));
        mongoTemplate.remove(query, Creature.class, Constants.MONGO_CREATURES_SCHEMA);

        return getCreatures();

    }
}
