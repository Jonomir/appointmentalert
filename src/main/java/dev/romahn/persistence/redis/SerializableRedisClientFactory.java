package dev.romahn.persistence.redis;


import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.micronaut.configuration.lettuce.DefaultRedisClientFactory;
import io.micronaut.configuration.lettuce.DefaultRedisConfiguration;
import io.micronaut.context.annotation.*;
import jakarta.inject.Singleton;

import java.io.Serializable;

@Requires(beans = DefaultRedisConfiguration.class)
@Singleton
@Factory
@Replaces(factory = DefaultRedisClientFactory.class)
public class SerializableRedisClientFactory extends DefaultRedisClientFactory {

    @Bean(preDestroy = "close")
    @Singleton
    @Primary
    public StatefulRedisConnection<Serializable, Serializable> serializableRedisConnection(@Primary RedisClient redisClient) {
        return redisClient.connect(new SerializableObjectCodec());
    }

    @Bean(preDestroy = "close")
    @Singleton
    public StatefulRedisPubSubConnection<Serializable, Serializable> serializableRedisPubSubConnection(@Primary RedisClient redisClient) {
        return redisClient.connectPubSub(new SerializableObjectCodec());
    }
}
