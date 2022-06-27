package com.legossol.jpamybatisdemo.controller;

import com.legossol.jpamybatisdemo.service.MyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {
    private final MyServiceImpl myServiceImpl;

    @PostMapping("/create")
    public void createTest(@RequestParam String name){
        myServiceImpl.createOne(name);
    }
}
