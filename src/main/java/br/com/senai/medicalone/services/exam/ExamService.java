package br.com.senai.medicalone.services.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.entities.exam.Exam;
import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.ExamNotFoundException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
import br.com.senai.medicalone.mappers.exam.ExamMapper;
import br.com.senai.medicalone.repositories.exam.ExamRepository;
import br.com.senai.medicalone.repositories.patient.PatientRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private PatientRepository patientRepository;
    @Operation(summary = "Cria um novo exame", description = "Método para criar um novo exame")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exame criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })

    @Transactional
    public ExamResponseDTO createExam(ExamRequestDTO dto) {
        Optional<Patient> patientOptional = patientRepository.findById(dto.getPatientId());
        if (patientOptional.isEmpty()) {
            throw new PatientNotFoundException("Paciente não encontrado");
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new BadRequestException("Nome do exame é obrigatório");
        }
        if (dto.getExamDate() == null) {
            throw new BadRequestException("Data do exame é obrigatória");
        }
        if (dto.getExamTime() == null) {
            throw new BadRequestException("Hora do exame é obrigatória");
        }
        if (dto.getType() == null || dto.getType().isEmpty()) {
            throw new BadRequestException("Tipo do exame é obrigatório");
        }

         boolean exists = examRepository.existsByPatientIdAndExamDateAndExamTime(
                dto.getPatientId(), dto.getExamDate(), dto.getExamTime());
        if (exists) {
            throw new BadRequestException("Já existe um exame para este paciente na mesma data e hora");
        }

        Exam exam = examMapper.toEntity(dto);
        exam.setPatient(patientOptional.get());
        exam.setId(null);
        exam = examRepository.save(exam);
        return examMapper.toResponseDTO(exam);
    }

    @Operation(summary = "Busca exame pelo ID", description = "Método para obter um exame pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exame encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    public ExamResponseDTO getExamById(Long id) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        return examMapper.toResponseDTO(examOptional.get());
    }

    @Operation(summary = "Atualiza exame", description = "Método para atualizar um exame")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exame atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    @Transactional
    public ExamResponseDTO updateExam(Long id, ExamRequestDTO dto) {
        Optional<Exam> examOptional = examRepository.findById(id);
        if (examOptional.isEmpty()) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new BadRequestException("Exam name is required");
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

    @Operation(summary = "Deleta um exame", description = "Método para deletar um exame")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exame deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new ExamNotFoundException("Exame não encontrado");
        }
        examRepository.deleteById(id);
    }

    @Operation(summary = "Lista todos os exames", description = "Método para listar exames")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exames listados com sucesso")
    })
    public Page<ExamResponseDTO> listExams(String name, Long patientId, Pageable pageable) {
        Page<Exam> exams;
        if (patientId != null) {
            exams = examRepository.findByPatientId(patientId, pageable);
        } else if (name != null && !name.isEmpty()) {
            exams = examRepository.findByName(name, pageable);
        } else {
            exams = examRepository.findAll(pageable);
        }
        return exams.map(examMapper::toResponseDTO);
    }

    @Operation(summary = "Lista exames por ID do paciente", description = "Método para listar exames por ID do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exames encontrados com sucesso")
    })
    public Page<ExamResponseDTO> getExamsByPatientId(Long patientId, Pageable pageable) {
        Page<Exam> exams = examRepository.findByPatientId(patientId, pageable);
        return exams.map(examMapper::toResponseDTO);
    }

}