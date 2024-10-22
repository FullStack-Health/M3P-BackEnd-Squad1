package br.com.senai.medicalone.mappers.record;

import br.com.senai.medicalone.dtos.record.RecordResponseDTO;
import br.com.senai.medicalone.entities.record.Record;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordMapper {

    @Autowired
    private ModelMapper modelMapper;

    public RecordResponseDTO toResponseDTO(Record entity) {
        return modelMapper.map(entity, RecordResponseDTO.class);
    }
}