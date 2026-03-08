package com.example.account.modules.facturation.service.ExternalServices.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import java.util.List;

@WritingConverter
public class ObjectListToJsonConverter implements Converter<List<?>, Json> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Json convert(List<?> source) {
        try {
            return Json.of(mapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting List to JSON", e);
        }
    }
} 