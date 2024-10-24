package br.com.senai.medicalone.controllers.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.exceptions.customexceptions.BadRequestException;
import br.com.senai.medicalone.services.appointment.AppointmentService;
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
@RequestMapping("/api/consultas")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

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
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta não encontrada\"}")))
    })
    public ResponseEntity<Map<String, Object>> getAppointmentById(@PathVariable Long id) {
        try {
            AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
            return new ResponseEntity<>(Map.of("message", "Consulta encontrada com sucesso", "appointment", appointment), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Consulta não encontrada"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma consulta", description = "Endpoint para atualizar uma consulta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consulta atualizada com sucesso\", \"appointment\": {\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}}"))),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar consulta", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao atualizar consulta\"}")))
    })
    public ResponseEntity<Map<String, Object>> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDTO dto) {
        try {
            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
            return new ResponseEntity<>(Map.of("message", "Consulta atualizada com sucesso", "appointment", updatedAppointment), HttpStatus.OK);
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppointmentResponseDTO> responseDTOs = appointmentService.listAppointments(pageable);
        return new ResponseEntity<>(Map.of("message", "Consultas encontradas com sucesso", "appointments", responseDTOs), HttpStatus.OK);
    }

    @GetMapping("/{patientId}/consultas")
    @Operation(summary = "Listar consultas por ID do paciente", description = "Endpoint para listar consultas por ID do paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas encontradas com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consultas encontradas com sucesso\", \"appointments\": [{\"id\": 1, \"date\": \"2023-10-01\", \"patientId\": 123}]}"))),
            @ApiResponse(responseCode = "404", description = "Consultas não encontradas", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Consultas não encontradas\"}")))
    })
    public ResponseEntity<Map<String, Object>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPatientId(patientId);
            return new ResponseEntity<>(Map.of("message", "Consultas encontradas com sucesso", "appointments", appointments), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Consultas não encontradas"), HttpStatus.NOT_FOUND);
        }
    }
}