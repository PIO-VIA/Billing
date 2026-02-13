package com.example.account.modules.facturation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.account.modules.facturation.service.ExternalServices.SellerService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TestController {
    @Autowired
    private SellerService sellerService;
    @GetMapping("{Id}")
    public String getMethodName(@RequestParam UUID param) {
        sellerService.getSellersByOrganization(param);
        return "done";
    }
    
}
