package com.example.account.modules.facturation.service.ExternalServices.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import java.io.IOException;
import java.util.List;

@ReadingConverter
public class JsonToPromotionConverter implements Converter<Json, List<SaleSizePromotion>> {
    
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public List<SaleSizePromotion> convert(Json source) {
        if (source == null) return null;
        try {
            // No more "Product." prefix needed
            return mapper.readValue(source.asString(), new TypeReference<List<SaleSizePromotion>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to List<SaleSizePromotion>", e);
        }
    }
}