package br.com.senai.medicalone.controllers.exam;

import br.com.senai.medicalone.dtos.exam.ExamRequestDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.dtos.user.UserResponseDTO;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.exceptions.customexceptions.ExamNotFoundException;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exames")
public class ExamController {

    @Autowired
    private ExamService examService;

    @Autowired
    private PagedResourcesAssembler<ExamResponseDTO> pagedResourcesAssembler;

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
            @ApiResponse(responseCode = "404", description = "Exame não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame não encontrado\"}"))),
            @ApiResponse(responseCode = "403", description = "Exame não associado ao usuário autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Exame não associado ao usuário autenticado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getExamById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ExamResponseDTO exam = examService.getExamById(id);
                if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                    Long patientId = user.getPatientId();
                    if (!exam.getPatientId().equals(patientId)) {
                        return new ResponseEntity<>(Map.of("message", "Exame não associado ao usuário autenticado"), HttpStatus.FORBIDDEN);
                    }
                }
                return new ResponseEntity<>(Map.of("message", "Exame encontrado com sucesso", "exam", exam), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message", "Usuário não autenticado"), HttpStatus.UNAUTHORIZED);
        } catch (ExamNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", "Exame não encontrado"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao buscar exame"), HttpStatus.INTERNAL_SERVER_ERROR);
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
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                patientId = user.getPatientId();
            }
        }

        Page<ExamResponseDTO> responseDTOs = examService.listExams(name, patientId, pageable);
        PagedModel<EntityModel<ExamResponseDTO>> pagedModel = pagedResourcesAssembler.toModel(responseDTOs);
        List<ExamResponseDTO> exams = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "message", "Exames encontrados com sucesso",
                "exams", exams,
                "page", Map.of(
                        "size", responseDTOs.getSize(),
                        "totalElements", responseDTOs.getTotalElements(),
                        "totalPages", responseDTOs.getTotalPages(),
                        "number", responseDTOs.getNumber()
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}