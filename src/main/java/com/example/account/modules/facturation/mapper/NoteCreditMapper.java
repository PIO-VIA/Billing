package com.example.account.modules.facturation.mapper;

import com.example.account.modules.core.mapper.BaseMapper;
import com.example.account.modules.facturation.dto.request.NoteCreditRequest;
import com.example.account.modules.facturation.dto.response.LigneFactureResponse;
import com.example.account.modules.facturation.dto.response.NoteCreditResponse;
import com.example.account.modules.facturation.model.entity.LigneNoteCredit;
import com.example.account.modules.facturation.model.entity.NoteCredit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface NoteCreditMapper extends BaseMapper<NoteCredit, NoteCreditRequest, NoteCreditRequest, NoteCreditResponse> {

    @Override
    @Mapping(target = "idNoteCredit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    NoteCredit toEntity(NoteCreditRequest createRequest);

    @Override
    @Mapping(target = "idCNoteCredit", source = "idNoteCredit")
    NoteCreditResponse toResponse(NoteCredit entity);

    @Override
    @Mapping(target = "idNoteCredit", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    void updateEntityFromRequest(NoteCreditRequest updateRequest, @MappingTarget NoteCredit entity);

    List<NoteCreditResponse> toResponseList(List<NoteCredit> entities);

    // Mapping for lines
    @Mapping(target = "idLigne", source = "idLigne")
    @Mapping(target = "idFacture", ignore = true) // Not a facture
    LigneFactureResponse toLigneResponse(LigneNoteCredit ligne);

    List<LigneFactureResponse> toLigneResponseList(List<LigneNoteCredit> lignes);
}
