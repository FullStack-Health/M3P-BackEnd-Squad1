package br.com.senai.medicalone.controllers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.services.patient.PatientRecordService;
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

    @Autowired
    private PatientRecordService patientRecordService;

    @PostMapping
    @Operation(summary = "Cria um paciente", description = "Endpoint para criar um novo paciente")
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
    @Operation(summary = "Busca paciente por ID", description = "Endpoint para obter um paciente pelo ID")
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
    @Operation(summary = "Atualiza paciente", description = "Endpoint para atualizar um paciente")
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
    @Operation(summary = "Deleta um paciente", description = "Endpoint para excluir um paciente")
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
    @Operation(summary = "Busca todos os pacientes", description = "Endpoint para obter todos os pacientes")
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
    @Operation(summary = "Busca paciente por cpf", description = "Endpoint para obter um paciente pelo CPF")
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
    @Operation(summary = "Busca paciente por nome", description = "Endpoint para obter pacientes pelo nome")
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
    @Operation(summary = "Busca paciente por telefone", description = "Endpoint para obter pacientes pelo telefone")
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

    @GetMapping("/prontuarios")
    @Operation(summary = "Busca todos os prontuarios", description = "Endpoint para obter todos os prontuários de pacientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuários encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Prontuários encontrados com sucesso\", \"records\": [{\"id\": 1, \"name\": \"John Doe\", \"exams\": [...], \"appointments\": [...]}]}")))
    })
    public ResponseEntity<Map<String, Object>> getAllPatientRecords(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientRecordDTO> records = patientRecordService.getAllPatientRecords(name, id, pageable);
        return new ResponseEntity<>(Map.of("message", "Prontuários encontrados com sucesso", "records", records), HttpStatus.OK);
    }

    @GetMapping("/{id}/prontuarios")
    @Operation(summary = "Busca prontuario de um paciente ID", description = "Endpoint para obter um prontuário de paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Prontuário encontrado com sucesso\", \"record\": {\"id\": 1, \"name\": \"John Doe\", \"exams\": [...], \"appointments\": [...]}\"}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientRecord(@PathVariable Long id) {
        try {
            PatientRecordDTO record = patientRecordService.getPatientRecord(id);
            return new ResponseEntity<>(Map.of("message", "Prontuário encontrado com sucesso", "record", record), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
        }
    }
}