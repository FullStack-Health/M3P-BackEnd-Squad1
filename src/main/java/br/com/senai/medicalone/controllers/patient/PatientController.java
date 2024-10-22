package br.com.senai.medicalone.controllers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.services.patient.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Patient Controller", description = "Endpoints para gerenciamento de pacientes")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    @Operation(summary = "Create a new patient", description = "Endpoint para criar um novo paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente criado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}}"))),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Dados ausentes ou incorretos\"}")))
    })
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            PatientResponseDTO responseDTO = patientService.createPatient(patientRequestDTO);
            return new ResponseEntity<>(Map.of("message", "Paciente criado com sucesso", "patient", responseDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Dados ausentes ou incorretos"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Endpoint para obter um paciente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente encontrado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientById(@PathVariable Long id) {
        try {
            PatientResponseDTO responseDTO = patientService.getPatientById(id);
            return new ResponseEntity<>(Map.of("message", "Paciente encontrado com sucesso", "patient", responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a patient", description = "Endpoint para atualizar um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente atualizado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}}"))),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Dados ausentes ou incorretos\"}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> updatePatient(@PathVariable Long id, @RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            PatientResponseDTO responseDTO = patientService.updatePatient(id, patientRequestDTO);
            return new ResponseEntity<>(Map.of("message", "Paciente atualizado com sucesso", "patient", responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Dados ausentes ou incorretos"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Endpoint para excluir um paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente excluído com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente excluído com sucesso\"}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, String>> deletePatient(@PathVariable Long id) {
        boolean isDeleted = patientService.deletePatient(id);
        if (isDeleted) {
            return new ResponseEntity<>(Map.of("message", "Paciente excluído com sucesso"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Get all patients", description = "Endpoint para obter todos os pacientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes encontrados com sucesso\", \"patients\": [{\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}]}")))
    })
    public ResponseEntity<Map<String, Object>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientResponseDTO> responseDTOs = patientService.getAllPatients(pageable);
        return new ResponseEntity<>(Map.of("message", "Pacientes encontrados com sucesso", "patients", responseDTOs), HttpStatus.OK);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Get patient by CPF", description = "Endpoint para obter um paciente pelo CPF")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente encontrado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientByCpf(@PathVariable String cpf) {
        try {
            PatientResponseDTO responseDTO = patientService.getPatientByCpf(cpf);
            return new ResponseEntity<>(Map.of("message", "Paciente encontrado com sucesso", "patient", responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/nome/{name}")
    @Operation(summary = "Get patients by name", description = "Endpoint para obter pacientes pelo nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes encontrados com sucesso\", \"patients\": [{\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}]}"))),
            @ApiResponse(responseCode = "404", description = "Pacientes não encontrados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes não encontrados\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientsByName(@PathVariable String name) {
        try {
            List<PatientResponseDTO> responseDTOs = patientService.getPatientsByName(name);
            return new ResponseEntity<>(Map.of("message", "Pacientes encontrados com sucesso", "patients", responseDTOs), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Pacientes não encontrados"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/telefone/{phone}")
    @Operation(summary = "Get patients by phone", description = "Endpoint para obter pacientes pelo telefone")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes encontrados com sucesso\", \"patients\": [{\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}]}"))),
            @ApiResponse(responseCode = "404", description = "Pacientes não encontrados", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes não encontrados\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientsByPhone(@PathVariable String phone) {
        try {
            List<PatientResponseDTO> responseDTOs = patientService.getPatientsByPhone(phone);
            return new ResponseEntity<>(Map.of("message", "Pacientes encontrados com sucesso", "patients", responseDTOs), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Pacientes não encontrados"), HttpStatus.NOT_FOUND);
        }
    }
}