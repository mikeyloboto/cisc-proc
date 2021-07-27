package com.evilbas.discproc.dao.mongo;

import com.evilbas.rslengine.creature.Creature;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoCreatureRepository extends MongoRepository<Creature, String> {
}
