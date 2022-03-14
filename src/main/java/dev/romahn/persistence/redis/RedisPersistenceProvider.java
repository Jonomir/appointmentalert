package dev.romahn.persistence.redis;

import dev.romahn.persistence.PersistenceProvider;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class RedisPersistenceProvider<K, V> implements PersistenceProvider<K, V> {

    @Inject private StatefulRedisConnection<K, V> connection;
    private RedisCommands<K, V> redis;

    @PostConstruct
    private void postConstruct() {
        redis = connection.sync();
    }

    @Override
    public void set(Map<K, V> map) {
        redis.scan().getKeys().forEach(redis::del);
        map.forEach((k, v) -> redis.set(k, v));
    }

    @Override
    public Map<K, V> get() {
        return redis.scan().getKeys().stream().collect(Collectors.toMap(k -> k, k -> redis.get(k)));
    }
}
