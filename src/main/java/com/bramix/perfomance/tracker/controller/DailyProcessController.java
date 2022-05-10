package com.bramix.perfomance.tracker.controller;

import com.bramix.perfomance.tracker.service.DailyProcessService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DailyProcessController {
    private final DailyProcessService dailyProcessService;

    @PostMapping("start/daily/process")
    public void startDailyProcess() {
        dailyProcessService.startDailyProcess();
    }
}
