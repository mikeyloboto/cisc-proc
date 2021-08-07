package com.evilbas.discproc.dao.mongo;

import com.evilbas.rslengine.ability.Spell;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoSpellRepository extends MongoRepository<Spell, String> {
}
