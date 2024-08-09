package com.hotmart.handson.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public abstract class AbstractStatsRepository {

    private static final int INCREMENT_AMOUNT = +1;
    private static final int DECREMENT_AMOUNT = -1;

    private final MongoOperations mongoOperations;

    private Boolean inc(String uuid, String field, Class<?> entity, int amount) {
        return mongoOperations.updateFirst(
                new Query(Criteria.where("uuid").is(uuid)),
                new Update().inc(field, amount),
                entity
        ).wasAcknowledged();
    }

    protected Boolean inc(String uuid, String field, Class<?> entity) {
        return inc(uuid, field, entity, INCREMENT_AMOUNT);
    }

    protected Boolean dec(String uuid, String field, Class<?> entity) {
        return inc(uuid, field, entity, DECREMENT_AMOUNT);
    }
}
