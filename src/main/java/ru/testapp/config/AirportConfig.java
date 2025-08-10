package ru.testapp.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Загружает сопоставление кода аэропорта -> zoneId из resources/airports.yaml
 */
@Slf4j
public final class AirportConfig {
    private static final Map<String, String> ZONES = loadZones();

    private static Map<String,String> loadZones() {
        try (InputStream in = AirportConfig.class.getResourceAsStream("/airports.yaml")) {
            if (in == null) return Collections.emptyMap();
            Yaml yaml = new Yaml();
            Map<String,String> map = yaml.load(in);
            return new ConcurrentHashMap<>(map);
        } catch (Exception e) {
            log.warn("Failed to load airport config", e);
            return Collections.emptyMap();
        }
    }

    public static ZoneId getZone(String airport) {
        String zone = ZONES.getOrDefault(airport, "UTC");
        return ZoneId.of(zone);
    }
}
