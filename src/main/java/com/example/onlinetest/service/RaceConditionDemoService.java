package com.example.onlinetest.service;

import com.example.onlinetest.dto.RaceConditionDemoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RaceConditionDemoService {

    public RaceConditionDemoDto runDemo(int threads, int incrementsPerThread) {

        if (threads < 50) {
            throw new IllegalArgumentException("Количество потоков должно быть >= 50 для демонстрации race condition");
        }
        if (incrementsPerThread <= 0) {
            throw new IllegalArgumentException("Количество инкрементов должно быть > 0");
        }

        UnsafeCounter unsafeCounter = new UnsafeCounter();
        SynchronizedCounter synchronizedCounter = new SynchronizedCounter();
        AtomicInteger atomicCounter = new AtomicInteger();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threads);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            for (int i = 0; i < threads; i++) {
                executor.execute(() -> {
                    try {
                        startLatch.await();
                        for (int j = 0; j < incrementsPerThread; j++) {
                            unsafeCounter.increment();
                            synchronizedCounter.increment();
                            atomicCounter.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finishLatch.countDown();
                    }
                });
            }

            long startTime = System.currentTimeMillis();
            startLatch.countDown();

            boolean completed = finishLatch.await(60, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!completed) {
                throw new IllegalStateException("Демонстрация race condition не завершилась за 60 секунд");
            }

            int expected = threads * incrementsPerThread;
            int unsafe = unsafeCounter.get();
            int synced = synchronizedCounter.get();
            int atomic = atomicCounter.get();
            boolean raceDetected = unsafe < expected && synced == expected && atomic == expected;

            log.info("=== RACE CONDITION DEMO ===");
            log.info("Потоков: {}, Инкрементов: {}", threads, incrementsPerThread);
            log.info("Ожидалось: {}", expected);
            log.info("Unsafe: {} (потеряно: {})", unsafe, expected - unsafe);
            log.info("Synchronized: {}", synced);
            log.info("Atomic: {}", atomic);
            log.info("Race condition обнаружен: {}", raceDetected);
            log.info("Время выполнения: {} ms", duration);

            return new RaceConditionDemoDto(
                threads,
                incrementsPerThread,
                expected,
                unsafe,
                synced,
                atomic,
                raceDetected
      );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Демонстрация прервана", e);
        } finally {
            executor.shutdown();
        }
    }

    private static class UnsafeCounter {
        private int value = 0;

        void increment() {
            value++;
        }

        int get() {
            return value;
        }
    }

    private static class SynchronizedCounter {
        private int value = 0;

        synchronized void increment() {
            value++;
        }

        synchronized int get() {
            return value;
        }
    }
}