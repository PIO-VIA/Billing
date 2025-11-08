package com.example.account.controllers2.services2;
import com.example.account.service.consumer.ClientEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.dto.request.ClientCreateRequest;
import com.example.account.dto.response.ClientResponse;
import com.example.account.mapper.ClientMapper;
import com.example.account.model.entity.Client;
import com.example.account.repository.ClientRepository;

@Service
public class ClientService2 {

    private final ClientEventConsumer clientEventConsumer;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientMapper clientMapper;

    ClientService2(ClientEventConsumer clientEventConsumer) {
        this.clientEventConsumer = clientEventConsumer;
    }
    public ClientResponse createClient(ClientCreateRequest request){
        Client client=clientMapper.toEntity(request);

          System.out.println(client);
          
        clientRepository.save(client);
      
        return clientMapper.toResponse(client);
    }
}
