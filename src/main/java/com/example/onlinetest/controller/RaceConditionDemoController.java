package com.example.onlinetest.controller;

import com.example.onlinetest.dto.RaceConditionDemoDto;
import com.example.onlinetest.service.RaceConditionDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/race-demo")
@RequiredArgsConstructor
@Tag(name = "Race Condition Demo", description = "Демонстрация race condition и решений")
public class RaceConditionDemoController {

    private final RaceConditionDemoService raceConditionDemoService;

    @GetMapping("/run")
  @Operation(summary = "Запустить демонстрацию race condition")
  public ResponseEntity<RaceConditionDemoDto> runDemo(
        @Parameter(description = "Количество потоков (должно быть >= 50)", example = "64")
        @RequestParam(defaultValue = "64") int threads,

        @Parameter(description = "Количество инкрементов на поток", example = "10000")
        @RequestParam(defaultValue = "10000") int incrementsPerThread
    )   {
        RaceConditionDemoDto result = raceConditionDemoService.runDemo(threads, incrementsPerThread);
        return ResponseEntity.ok(result);
    }
}