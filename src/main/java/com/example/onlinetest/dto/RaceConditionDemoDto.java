package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат демонстрации race condition")
public record RaceConditionDemoDto(
    @Schema(description = "Количество потоков") int threads,
    @Schema(description = "Инкрементов на поток") int incrementsPerThread,
    @Schema(description = "Ожидаемое значение") int expected,
    @Schema(description = "Небезопасный счетчик (должен быть меньше expected)") int unsafeCounter,
    @Schema(description = "Безопасный счетчик (synchronized)") int synchronizedCounter,
    @Schema(description = "Безопасный счетчик (AtomicInteger)") int atomicCounter,
    @Schema(description = "Обнаружен ли race condition") boolean raceConditionDetected
) { }