package dev.romahn.persistence;

import java.util.Map;

public interface PersistenceProvider<K, V> {
    void set(Map<K, V> map);
    Map<K, V> get();
}
