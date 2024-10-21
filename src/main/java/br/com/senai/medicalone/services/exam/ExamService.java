package br.com.senai.medicalone.services.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.entities.exam.Exam;
import br.com.senai.medicalone.exceptions.customexceptions.ExamNotFoundException;
import br.com.senai.medicalone.mappers.exam.ExamMapper;
import br.com.senai.medicalone.repositories.exam.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamMapper examMapper;

    @Transactional
    public ExamResponseDTO createExam(ExamRequestDTO dto) {
        Exam exam = examMapper.toEntity(dto);
        exam = examRepository.save(exam);
        return examMapper.toResponseDTO(exam);
    }

    public ExamResponseDTO getExamById(Long id) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        return examMapper.toResponseDTO(examOptional.get());
    }

    @Transactional
    public ExamResponseDTO updateExam(Long id, ExamRequestDTO dto) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        Exam exam = examOptional.get();
        exam.setName(dto.getName());
        exam.setExamDate(dto.getExamDate());
        exam.setExamTime(dto.getExamTime());
        exam.setType(dto.getType());
        exam.setLaboratory(dto.getLaboratory());
        exam.setDocumentUrl(dto.getDocumentUrl());
        exam.setResults(dto.getResults());
        exam = examRepository.save(exam);
        return examMapper.toResponseDTO(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        examRepository.deleteById(id);
    }

    public List<ExamResponseDTO> listExams(String name) {
        List<Exam> exams;
        if (name != null && !name.isEmpty()) {
            exams = examRepository.findByName(name);
        } else {
            exams = examRepository.findAll();
        }
        return exams.stream()
                .map(examMapper::toResponseDTO)
                .toList();
    }
}