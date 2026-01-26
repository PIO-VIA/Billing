package com.example.account.modules.facturation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.account.modules.facturation.dto.request.BondeReceptionCreateRequest;
import com.example.account.modules.facturation.dto.response.BondeReceptionResponse;
import com.example.account.modules.facturation.mapper.BondeReceptionMapper;
import com.example.account.modules.facturation.model.entity.BondeReception;
import com.example.account.modules.facturation.repository.BonReceptionRepository;
@Service
public class BonReceptionService {
    
    @Autowired
    private BonReceptionRepository bonReceptionRepository;
    @Autowired
    private BondeReceptionMapper bondeReceptionMapper;
    public  BondeReceptionResponse createBondeReception(BondeReceptionCreateRequest dto){
        BondeReception bondeReception=bondeReceptionMapper.toEntity(dto);
        return bondeReceptionMapper.toDto(bonReceptionRepository.save(bondeReception));

        
    }

    public List<BondeReceptionResponse> getAllBondeReception(){
        List<BondeReception> bons=bonReceptionRepository.findAll();
        return bondeReceptionMapper.toDtoList(bons);
    }

    public BondeReceptionResponse updateBondeReception(UUID id,BondeReceptionResponse dto) throws Exception{
        BondeReception bondeReception=bonReceptionRepository.findByIdGRN(id).orElseThrow(()->new Exception("Goods Receipt Note does not exists"));
        bondeReceptionMapper.updateEntityFromDto(dto,bondeReception);
        bonReceptionRepository.save(bondeReception);
        return bondeReceptionMapper.toDto(bondeReception);
    }

}
