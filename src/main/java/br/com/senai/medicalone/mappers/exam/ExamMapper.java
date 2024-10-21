package br.com.senai.medicalone.mappers.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.entities.exam.Exam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Exam toEntity(ExamRequestDTO dto) {
        return modelMapper.map(dto, Exam.class);
    }

    public ExamResponseDTO toResponseDTO(Exam entity) {
        return modelMapper.map(entity, ExamResponseDTO.class);
    }
}