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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamMapper examMapper;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private ExamService examService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createExam_Success() {
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setName("Blood Test");
        requestDTO.setExamDate(LocalDate.now());
        requestDTO.setExamTime(LocalTime.now());
        requestDTO.setType("Routine");
        requestDTO.setLaboratory("LabCorp");
        requestDTO.setDocumentUrl("http://example.com/document");
        requestDTO.setResults("Normal");

        Patient patient = new Patient();
        patient.setId(1L);

        Exam exam = new Exam();
        exam.setId(1L);
        exam.setPatient(patient);

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(examRepository.existsByPatientIdAndExamDateAndExamTime(anyLong(), any(LocalDate.class), any(LocalTime.class))).thenReturn(false);
        when(examMapper.toEntity(any(ExamRequestDTO.class))).thenReturn(exam);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(examMapper.toResponseDTO(any(Exam.class))).thenReturn(new ExamResponseDTO());

        ExamResponseDTO responseDTO = examService.createExam(requestDTO);

        assertNotNull(responseDTO);
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    void createExam_PatientNotFound() {
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setPatientId(1L);

        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(PatientNotFoundException.class, () -> examService.createExam(requestDTO));
    }

    @Test
    void createExam_ExamAlreadyExists() {
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setName("Blood Test");
        requestDTO.setExamDate(LocalDate.now());
        requestDTO.setExamTime(LocalTime.now());
        requestDTO.setType("Routine");

        Patient patient = new Patient();
        patient.setId(1L);

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));
        when(examRepository.existsByPatientIdAndExamDateAndExamTime(anyLong(), any(LocalDate.class), any(LocalTime.class))).thenReturn(true);

        assertThrows(BadRequestException.class, () -> examService.createExam(requestDTO));
    }

    @Test
    void getExamById_Success() {
        Long id = 1L;
        Exam exam = new Exam();
        exam.setId(id);

        when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        when(examMapper.toResponseDTO(any(Exam.class))).thenReturn(new ExamResponseDTO());

        ExamResponseDTO responseDTO = examService.getExamById(id);

        assertNotNull(responseDTO);
    }

    @Test
    void getExamById_NotFound() {
        Long id = 1L;

        when(examRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ExamNotFoundException.class, () -> examService.getExamById(id));
    }

    @Test
    void updateExam_Success() {
        Long id = 1L;
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setName("Updated Exam");
        requestDTO.setExamDate(LocalDate.now());
        requestDTO.setExamTime(LocalTime.now());
        requestDTO.setType("Updated Type");
        requestDTO.setLaboratory("Updated Lab");
        requestDTO.setDocumentUrl("http://example.com/updated-document");
        requestDTO.setResults("Updated Results");

        Exam existingExam = new Exam();
        existingExam.setId(id);

        when(examRepository.findById(id)).thenReturn(Optional.of(existingExam));
        when(examRepository.save(any(Exam.class))).thenReturn(existingExam);
        when(examMapper.toResponseDTO(any(Exam.class))).thenReturn(new ExamResponseDTO());

        ExamResponseDTO responseDTO = examService.updateExam(id, requestDTO);

        assertNotNull(responseDTO);
    }

    @Test
    void updateExam_NotFound() {
        Long id = 1L;
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setName("Updated Exam");

        when(examRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ExamNotFoundException.class, () -> examService.updateExam(id, requestDTO));
    }

    @Test
    void deleteExam_Success() {
        Long id = 1L;

        when(examRepository.existsById(id)).thenReturn(true);

        examService.deleteExam(id);

        verify(examRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteExam_NotFound() {
        Long id = 1L;

        when(examRepository.existsById(id)).thenReturn(false);

        assertThrows(ExamNotFoundException.class, () -> examService.deleteExam(id));
    }

    @Test
    void listExams_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Exam> exams = List.of(new Exam(), new Exam());
        Page<Exam> examPage = new PageImpl<>(exams, pageable, exams.size());

        when(examRepository.findAll(pageable)).thenReturn(examPage);
        when(examMapper.toResponseDTO(any(Exam.class))).thenReturn(new ExamResponseDTO());

        Page<ExamResponseDTO> responseDTOs = examService.listExams(null, null, pageable);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.getContent().size());
    }

    @Test
    void listExams_ByName_Success() {
        String name = "Blood Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<Exam> exams = List.of(new Exam(), new Exam());
        Page<Exam> examPage = new PageImpl<>(exams, pageable, exams.size());

        when(examRepository.findByName(name, pageable)).thenReturn(examPage);
        when(examMapper.toResponseDTO(any(Exam.class))).thenReturn(new ExamResponseDTO());

        Page<ExamResponseDTO> responseDTOs = examService.listExams(name, null, pageable);

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.getContent().size());
    }

    @Test
    void createExam_MissingName_ShouldThrowException() {
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setPatientId(1L);
        requestDTO.setExamDate(LocalDate.now());
        requestDTO.setExamTime(LocalTime.now());
        requestDTO.setType("Routine");
        requestDTO.setLaboratory("LabCorp");
        requestDTO.setDocumentUrl("http://example.com/document");
        requestDTO.setResults("Normal");

        Patient patient = new Patient();
        patient.setId(1L);

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(patient));

        assertThrows(BadRequestException.class, () -> examService.createExam(requestDTO));
    }

    @Test
    void updateExam_MissingName_ShouldThrowException() {
        Long id = 1L;
        ExamRequestDTO requestDTO = new ExamRequestDTO();
        requestDTO.setExamDate(LocalDate.now());
        requestDTO.setExamTime(LocalTime.now());
        requestDTO.setType("Updated Type");
        requestDTO.setLaboratory("Updated Lab");
        requestDTO.setDocumentUrl("http://example.com/updated-document");
        requestDTO.setResults("Updated Results");

        Exam existingExam = new Exam();
        existingExam.setId(id);

        when(examRepository.findById(id)).thenReturn(Optional.of(existingExam));

        assertThrows(BadRequestException.class, () -> examService.updateExam(id, requestDTO));
    }

    @Test
    void listExams_NoExamsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Exam> examPage = new PageImpl<>(List.of(), pageable, 0);

        when(examRepository.findAll(pageable)).thenReturn(examPage);

        Page<ExamResponseDTO> responseDTOs = examService.listExams(null, null, pageable);

        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }
}