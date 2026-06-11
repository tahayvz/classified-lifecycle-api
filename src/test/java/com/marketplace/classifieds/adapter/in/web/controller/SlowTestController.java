package com.marketplace.classifieds.adapter.in.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlowTestController {

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(10);
        return "ok";
    }
}
