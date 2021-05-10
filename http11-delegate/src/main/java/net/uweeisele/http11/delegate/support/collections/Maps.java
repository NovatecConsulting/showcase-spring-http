package net.uweeisele.http11.delegate.support.collections;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * Map builder which supports null keys and values.
 */
public final class Maps {

    public static <K,V> Map.Entry<K, V> entry(K key, V value) {
        return new SimpleEntry<>(key, value);
    }

    public static <K, V> Map<K, V> of(Map.Entry<K, V>... entries) {
        Map<K, V> map = new HashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
