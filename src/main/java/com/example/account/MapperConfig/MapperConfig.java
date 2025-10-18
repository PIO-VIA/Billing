package com.example.account.MapperConfig;

import com.example.account.mapper.ClientMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ClientMapper clientMapper() {
        // Use MapStruct factory to get the implementation
        return Mappers.getMapper(ClientMapper.class);
    }
}
