package com.example.account.controllers2;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.controllers2.services2.ClientService2;
import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.response.ClientResponse;

import org.apache.kafka.clients.ClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api2/clients")
public class ClientController23 {
    @Autowired
    private ClientService2 clientService2;
    @PostMapping
    public ResponseEntity<?> postMethodName(@RequestBody ClientCreateRequest request) {
        
        ClientResponse response=clientService2.createClient(request);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    
}
