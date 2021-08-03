package com.evilbas.discproc.dao.mongo;

import com.evilbas.rslengine.item.Item;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoItemRepository extends MongoRepository<Item, String> {
}
