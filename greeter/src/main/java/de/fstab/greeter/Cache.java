package de.fstab.greeter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Cache {

    private final int size;
    private final Map<String, String> cache;
    private final Random random = new Random(1);

    public Cache(int size) {
        this.size = size;
        cache = new HashMap<>(size);
    }

    public String get(String key) {
        try {
            // Reading from this cache takes about 70ms.
            Thread.sleep((long) Math.abs(50.0 + 20 * random.nextGaussian()));
            return cache.get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean put(String key, String value) {
        if (cache.size() >= size) {
            return false;
        }
        cache.put(key, value);
        return true;
    }
}
