package br.com.senai.medicalone.controllers.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.services.exam.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exames")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping
    @Operation(summary = "Criar um novo exame", description = "Endpoint para criar um novo exame")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exame criado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame criado com sucesso\", \"exam\": {\"id\": 1, \"name\": \"Exame de Sangue\", \"description\": \"Descrição do exame\"}}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao criar exame", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao criar exame\"}")))
    })
    public ResponseEntity<Map<String, Object>> createExam(@RequestBody ExamRequestDTO dto) {
        try {
            ExamResponseDTO createdExam = examService.createExam(dto);
            return new ResponseEntity<>(Map.of("message", "Exame criado com sucesso", "exam", createdExam), HttpStatus.CREATED);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao criar exame"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter exame por ID", description = "Endpoint para obter um exame pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exame encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame encontrado com sucesso\", \"exam\": {\"id\": 1, \"name\": \"Exame de Sangue\", \"description\": \"Descrição do exame\"}}"))),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getExamById(@PathVariable Long id) {
        try {
            ExamResponseDTO exam = examService.getExamById(id);
            return new ResponseEntity<>(Map.of("message", "Exame encontrado com sucesso", "exam", exam), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Exame não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um exame", description = "Endpoint para atualizar um exame")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exame atualizado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame atualizado com sucesso\", \"exam\": {\"id\": 1, \"name\": \"Exame de Sangue\", \"description\": \"Descrição do exame\"}}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar exame", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao atualizar exame\"}")))
    })
    public ResponseEntity<Map<String, Object>> updateExam(@PathVariable Long id, @RequestBody ExamRequestDTO dto) {
        try {
            ExamResponseDTO updatedExam = examService.updateExam(id, dto);
            return new ResponseEntity<>(Map.of("message", "Exame atualizado com sucesso", "exam", updatedExam), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao atualizar exame"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um exame", description = "Endpoint para excluir um exame")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exame excluído com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame excluído com sucesso\"}"))),
            @ApiResponse(responseCode = "404", description = "Exame não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame não encontrado\"}")))
    })
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return new ResponseEntity<>(Map.of("message", "Exame excluído com sucesso"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Exame não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os exames", description = "Endpoint para listar todos os exames")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exames encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exames encontrados com sucesso\", \"exams\": [{\"id\": 1, \"name\": \"Exame de Sangue\", \"description\": \"Descrição do exame\"}]}")))
    })
    public ResponseEntity<Map<String, Object>> listExams(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamResponseDTO> responseDTOs = examService.listExams(name, pageable);
        return new ResponseEntity<>(Map.of("message", "Exames encontrados com sucesso", "exams", responseDTOs), HttpStatus.OK);
    }

    @GetMapping("/{patientId}/exames")
    @Operation(summary = "Listar exames por ID do paciente", description = "Endpoint para listar exames por ID do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exames encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exames encontrados com sucesso\", \"exams\": [{\"id\": 1, \"name\": \"Exame de Sangue\", \"description\": \"Descrição do exame\"}]}"))),
            @ApiResponse(responseCode = "404", description = "Exames não encontrados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exames não encontrados\"}")))
    })
    public ResponseEntity<Map<String, Object>> getExamsByPatientId(@PathVariable Long patientId) {
        try {
            List<ExamResponseDTO> exams = examService.getExamsByPatientId(patientId);
            return new ResponseEntity<>(Map.of("message", "Exames encontrados com sucesso", "exams", exams), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Exames não encontrados"), HttpStatus.NOT_FOUND);
        }
    }
}