package br.com.senai.medicalone.controllers.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.services.exam.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody ExamRequestDTO dto) {
        ExamResponseDTO createdExam = examService.createExam(dto);
        return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Long id) {
        ExamResponseDTO exam = examService.getExamById(id);
        return ResponseEntity.ok(exam);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Long id, @RequestBody ExamRequestDTO dto) {
        ExamResponseDTO updatedExam = examService.updateExam(id, dto);
        return ResponseEntity.ok(updatedExam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> listExams(@RequestParam(required = false) String name) {
        List<ExamResponseDTO> exams = examService.listExams(name);
        return ResponseEntity.ok(exams);
    }
}