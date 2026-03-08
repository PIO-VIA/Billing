package com.example.account.modules.facturation.service.ExternalServices.entity;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import java.io.IOException;
import java.util.List;

@ReadingConverter
public class JsonToClientSaleSizeConverter implements Converter<Json, List<ClientSaleSize>> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ClientSaleSize> convert(Json source) {
        try {
            return mapper.readValue(source.asString(), new TypeReference<List<ClientSaleSize>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to List<ClientSaleSize>", e);
        }
    }
}