package ru.testapp.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AirportConfig {
    private static final Map<String, String> ZONES = loadZones();
    public static final int MAX_ATTEMPTS = 3;

    private static Map<String, String> loadZones() {
        try (InputStream in = AirportConfig.class.getResourceAsStream("/airports.yaml")) {
            if (in == null) return Collections.emptyMap();

            Yaml yaml = new Yaml(new Constructor(Map.class));
            Map<String, String> zones = yaml.load(in);
            return new ConcurrentHashMap<>(zones);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load airport config", e);
        }
    }

    public static ZoneId getZone(String airport) {
        String zone = ZONES.getOrDefault(airport, "UTC");
        return ZoneId.of(zone);
    }
}
