package br.com.senai.medicalone.controllers.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.dtos.exam.ExamResponseDTO;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.AppointmentNotFoundException;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.services.appointment.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consultas")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PagedResourcesAssembler<AppointmentResponseDTO> pagedResourcesAssembler;


    @PostMapping
    @Operation(summary = "Criar uma nova consulta", description = "Endpoint para criar uma nova consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta criada com sucesso\", \"appointment\": {\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao criar consulta", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao criar consulta\"}")))
    })
    public ResponseEntity<Map<String, Object>> createAppointment(@RequestBody AppointmentRequestDTO dto) {
        try {
            AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(dto);
            return new ResponseEntity<>(Map.of("message", "Consulta criada com sucesso", "appointment", createdAppointment), HttpStatus.CREATED);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao criar consulta"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter consulta por ID", description = "Endpoint para obter uma consulta pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta encontrada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta encontrada com sucesso\", \"appointment\": {\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}}"))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada\"}"))),
            @ApiResponse(responseCode = "403", description = "Consulta não associada ao usuário autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta não associada ao usuário autenticado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getAppointmentById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
                if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                    Long patientId = user.getPatientId();
                    if (!appointment.getPatientId().equals(patientId)) {
                        return new ResponseEntity<>(Map.of("message", "Consulta não associada ao usuário autenticado"), HttpStatus.FORBIDDEN);
                    }
                }
                return new ResponseEntity<>(Map.of("message", "Consulta encontrada com sucesso", "appointment", appointment), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message", "Usuário não autenticado"), HttpStatus.UNAUTHORIZED);
        } catch (AppointmentNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", "Consulta não encontrada"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao buscar consulta"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma consulta", description = "Endpoint para atualizar uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta atualizada com sucesso\", \"appointment\": {\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar consulta", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao atualizar consulta\"}")))
    })
    public ResponseEntity<Map<String, Object>> updateAppointment(@PathVariable Long id, @Valid @Validated @RequestBody AppointmentRequestDTO dto) {
        try {
            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
            return new ResponseEntity<>(Map.of("message", "Consulta atualizada com sucesso", "appointment", updatedAppointment), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao atualizar consulta"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma consulta", description = "Endpoint para excluir uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta excluída com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta excluída com sucesso\"}"))),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada\"}")))
    })
    public ResponseEntity<Map<String, String>> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return new ResponseEntity<>(Map.of("message", "Consulta excluída com sucesso"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Consulta não encontrada"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas as consultas", description = "Endpoint para listar todas as consultas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas encontradas com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consultas encontradas com sucesso\", \"appointments\": [{\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}]}")))
    })
    public ResponseEntity<Map<String, Object>> listAppointments(
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

        Page<AppointmentResponseDTO> responseDTOs = appointmentService.listAppointments(name, patientId, pageable);
        PagedModel<EntityModel<AppointmentResponseDTO>> pagedModel = pagedResourcesAssembler.toModel(responseDTOs);
        List<AppointmentResponseDTO> appointments = pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "message", "Consultas encontradas com sucesso",
                "appointments", appointments,
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