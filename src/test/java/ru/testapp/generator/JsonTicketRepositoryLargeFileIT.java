package ru.testapp.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.testapp.calculation.MinFlightTimeStrategy;
import ru.testapp.calculation.PriceDifferenceStrategy;
import ru.testapp.domain.Ticket;
import ru.testapp.repository.JsonTicketRepository;
import ru.testapp.service.AnalysisService;
import ru.testapp.service.CalculationType;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonTicketRepositoryLargeFileIT {

    @Test
    void largeFile_usesStreaming_notCache(@TempDir Path tmp) throws Exception {
        // генерируем большой файл > 3MB (например, 120_000 записей можно уменьшить, если нужно)
        Path file = tmp.resolve("big-tickets.json");
        // число записей подбираем экспериментально — например 30_000; зависит от формата output,
        // но мы будем проверять размер после генерации
        int num = 40_000;
        TestDataGenerator.generateLargeFile(file, num);

        long size = java.nio.file.Files.size(file);
        assertTrue(size > 1_000_000L, "generated should be >1MB for test (actual " + size + ")");

        // создаем репо с включенным cache и threshold = 1MB (т.е. файл больше порога => cache не будет использоваться)
        long threshold = 1024 * 1024L;
        JsonTicketRepository repo = new JsonTicketRepository(file, true, threshold);

        // первый проход — поток (не должен заполнить cachedTickets)
        try (Stream<Ticket> s = repo.streamTickets()) {
            long count = s.count();
            assertTrue(count >= num * 0.9, "should read lots of tickets (got " + count + ")");
        }

        // рефлексия: cachedTickets должен быть null (потоковый путь)
        Field cachedField = JsonTicketRepository.class.getDeclaredField("cachedTickets");
        cachedField.setAccessible(true);
        Object cached = cachedField.get(repo);
        assertNull(cached, "cachedTickets should be null for large file (streaming used)");

        // теперь создаём маленький файл и проверим, что кэширование работает
        Path smallFile = tmp.resolve("small.json");
        TestDataGenerator.generateLargeFile(smallFile, 50); // ~small
        JsonTicketRepository repo2 = new JsonTicketRepository(smallFile, true, 100_000_000L); // high threshold => caching allowed
        try (Stream<Ticket> s = repo2.streamTickets()) {
            long c = s.count();
            assertEquals(50, c);
        }
        Object cached2 = cachedField.get(repo2);
        assertNotNull(cached2, "cachedTickets should be populated for small file");
    }

    @Test
    void largeFile_usesStreaming_and_smallFile_usesCache(@TempDir Path tmp) throws Exception {
        // 1) генерируем большой файл (чтобы превышал порог)
        Path big = tmp.resolve("big.json");
        int bigCount = 30_000; // достаточно, чтобы получить большой файл
        TestDataGenerator.generateLargeFile(big, bigCount);

        long bigSize = Files.size(big);
        // проверяем, что файл большой (условие теста)
        assertTrue(bigSize > 1_000_000L, "big file should be >1MB; actual=" + bigSize);

        // Создаём JsonTicketRepository с cacheEnabled=true, но low threshold => file > threshold => streaming
        long thresholdForStream = 500_000L; // 0.5MB => гарантированно меньше big
        JsonTicketRepository repoStream = new JsonTicketRepository(big, true, thresholdForStream);
        AnalysisService serviceStream = new AnalysisService(repoStream);
        serviceStream.register(CalculationType.MIN_FLIGHT_TIME,
                new MinFlightTimeStrategy());
        serviceStream.register(CalculationType.PRICE_DIFFERENCE,
               new PriceDifferenceStrategy());

        // Выводим результат в файл и в консоль
        Path outStream = tmp.resolve("out_stream.txt");
        Map<String, Object> resultsStream = serviceStream.calculateAll("VVO", "TLV");
        String reportStream = "STREAM run results: " + resultsStream.toString();
        Files.writeString(outStream, reportStream, StandardCharsets.UTF_8);
        System.out.println(reportStream);

        // Проверяем, что репо НЕ закэшировал (cachedTickets == null)
        Field f = JsonTicketRepository.class.getDeclaredField("cachedTickets");
        f.setAccessible(true);
        Object cachedVal = f.get(repoStream);
        assertNull(cachedVal, "For large file cachedTickets must be null (streaming)");

        // 2) генерируем маленький файл (маленький — порог выше размера)
        Path small = tmp.resolve("small.json");
        TestDataGenerator.generateLargeFile(small, 50);
        long smallSize = Files.size(small);
        assertTrue(smallSize < thresholdForStream * 10, "small file is small enough");

        // Теперь repo with high threshold => caching WILL be used
        JsonTicketRepository repoCache = new JsonTicketRepository(small, true, 10_000_000L); // 10MB threshold
        AnalysisService serviceCache = new AnalysisService(repoCache);
        serviceCache.register(CalculationType.MIN_FLIGHT_TIME, new MinFlightTimeStrategy());
        serviceCache.register(CalculationType.PRICE_DIFFERENCE, new PriceDifferenceStrategy());

        Path outCache = tmp.resolve("out_cache.txt");
        Map<String, Object> resultsCache = serviceCache.calculateAll("VVO", "TLV");
        String reportCache = "CACHE run results: " + resultsCache.toString();
        Files.writeString(outCache, reportCache, StandardCharsets.UTF_8);
        System.out.println(reportCache);

        // cachedTickets поле должно быть заполнено
        Object cachedVal2 = f.get(repoCache);
        assertNotNull(cachedVal2, "For small file cachedTickets must be populated (cache)");

        // Доп. проверка: данные реально читаются
        try (Stream<Ticket> s = repoCache.streamTickets()) {
            assertTrue(s.findAny().isPresent());
        }
    }

    @Test
    void largeFile_usesStreamingMode(@TempDir Path tmp) throws Exception {
        // Генерируем большой файл
        Path bigFile = tmp.resolve("big.json");
        TestDataGenerator.generateLargeFile(bigFile, 30_000);
        long bigSize = Files.size(bigFile);
        assertTrue(bigSize > 1_000_000L, "Файл должен быть большим");

        // Создаем репозиторий с низким порогом
        long threshold = 500_000L;
        JsonTicketRepository repo = new JsonTicketRepository(bigFile, true, threshold);

        // 1. Проверяем, что используется потоковый режим
        try (Stream<Ticket> stream = repo.streamTickets()) {
            assertTrue(stream.count() > 0, "Должны быть прочитаны билеты");
        }

        // 2. Проверяем, что кэш не инициализирован
        Field f = JsonTicketRepository.class.getDeclaredField("cachedTickets");
        f.setAccessible(true);
        assertNull(f.get(repo), "Кэш не должен инициализироваться для больших файлов");

        // 3. Проверяем логи (опционально)
        // Должно быть сообщение о потоковой обработке
    }

    @Test
    void smallFile_usesCacheMode(@TempDir Path tmp) throws Exception {
        // Генерируем маленький файл
        Path smallFile = tmp.resolve("small.json");
        TestDataGenerator.generateLargeFile(smallFile, 50);
        long smallSize = Files.size(smallFile);
        assertTrue(smallSize < 100_000L, "Файл должен быть маленьким");

        // Создаем репозиторий с высоким порогом
        long threshold = 10_000_000L;
        JsonTicketRepository repo = new JsonTicketRepository(smallFile, true, threshold);

        // 1. Проверяем, что используется кэшированный режим
        try (Stream<Ticket> stream = repo.streamTickets()) {
            assertTrue(stream.count() > 0, "Должны быть прочитаны билеты");
        }

        // 2. Проверяем, что кэш инициализирован
        Field f = JsonTicketRepository.class.getDeclaredField("cachedTickets");
        f.setAccessible(true);
        assertNotNull(f.get(repo), "Кэш должен инициализироваться для маленьких файлов");

        // 3. Проверяем, что при повторном вызове используется кэш
        // (добавляем логирование или счетчик инициализаций)
    }
}
