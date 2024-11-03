package br.com.senai.medicalone.controllers.patient;

import br.com.senai.medicalone.dtos.patient.PatientRecordDTO;
import br.com.senai.medicalone.dtos.patient.PatientRequestDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.entities.user.User;
import br.com.senai.medicalone.exceptions.customexceptions.PatientAlreadyExistsException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientHasLinkedRecordsException;
import br.com.senai.medicalone.exceptions.customexceptions.PatientNotFoundException;
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
@RequestMapping("/api/pacientes")
@Tag(name = "Patient Controller", description = "Endpoints para gerenciamento de pacientes")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRecordService patientRecordService;

    @Autowired
    private PagedResourcesAssembler<PatientResponseDTO> pagedResourcesAssembler;

    @PostMapping
    @Operation(summary = "Cria um paciente", description = "Endpoint para criar um novo paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente criado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}}"))),
            @ApiResponse(responseCode = "400", description = "Dados ausentes ou incorretos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Dados ausentes ou incorretos\"}"))),
            @ApiResponse(responseCode = "409", description = "Paciente já cadastrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente já cadastrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody PatientRequestDTO patientRequestDTO) {
        try {
            PatientResponseDTO responseDTO = patientService.createPatient(patientRequestDTO);
            return new ResponseEntity<>(Map.of("message", "Paciente criado com sucesso", "patient", responseDTO), HttpStatus.CREATED);
        } catch (PatientAlreadyExistsException e) {
            return new ResponseEntity<>(Map.of("message", "Paciente já cadastrado"), HttpStatus.CONFLICT);
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
        try {
            boolean isDeleted = patientService.deletePatient(id);
            if (isDeleted) {
                return new ResponseEntity<>(Map.of("message", "Paciente excluído com sucesso"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
            }
        } catch (PatientNotFoundException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (PatientHasLinkedRecordsException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    @Operation(summary = "Busca todos os pacientes", description = "Endpoint para obter todos os pacientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes encontrados com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Pacientes encontrados com sucesso\", \"patients\": [{\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\"}]}")))
    })
    public ResponseEntity<Map<String, Object>> getAllPatients(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientResponseDTO> responseDTOs = patientService.getAllPatientsFiltered(searchTerm,pageable);
        PagedModel<EntityModel<PatientResponseDTO>> pagedModel = pagedResourcesAssembler.toModel(responseDTOs);

        List<PatientResponseDTO> patients = pagedModel.getContent().stream()
                                                    .map(EntityModel::getContent)
                                                    .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "message", "Pacientes encontrados com sucesso",
                "patients", patients,
                "page", Map.of(
                        "size", responseDTOs.getSize(),
                        "totalElements", responseDTOs.getTotalElements(),
                        "totalPages", responseDTOs.getTotalPages(),
                        "number", responseDTOs.getNumber()
                )
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
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
    public ResponseEntity<Map<String, Object>> getPatientsByName(@PathVariable("name") String name) {
        try {
            String decodedName = java.net.URLDecoder.decode(name, "UTF-8");
            List<PatientResponseDTO> responseDTOs = patientService.getPatientsByName(decodedName);
            if (responseDTOs.isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Pacientes não encontrados"), HttpStatus.NOT_FOUND);
            }
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

    @GetMapping("/email/{email}")
    @Operation(summary = "Busca paciente por email", description = "Endpoint para obter um paciente pelo email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente encontrado com sucesso\", \"patient\": {\"id\": 1, \"name\": \"John Doe\", \"cpf\": \"123.456.789-00\", \"phone\": \"(99) 9 9999-9999\", \"email\": \"john.doe@example.com\"}}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientByEmail(@PathVariable String email) {
        try {
            PatientResponseDTO responseDTO = patientService.getPatientByEmail(email);
            return new ResponseEntity<>(Map.of("message", "Paciente encontrado com sucesso", "patient", responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
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
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientRecordDTO> records = patientRecordService.getAllPatientRecords(name, id, pageable);
        return new ResponseEntity<>(Map.of("message", "Prontuários encontrados com sucesso", "records", records), HttpStatus.OK);
    }

    @GetMapping("/{id}/prontuarios")
    @Operation(summary = "Busca prontuario de um paciente ID", description = "Endpoint para obter um prontuário de paciente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Prontuário encontrado com sucesso\", \"record\": {\"id\": 1, \"name\": \"John Doe\", \"exams\": [...], \"appointments\": [...]}\"}"))),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Paciente não encontrado\"}"))),
            @ApiResponse(responseCode = "403", description = "Prontuário não associado ao usuário autenticado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Prontuário não associado ao usuário autenticado\"}")))
    })
    public ResponseEntity<Map<String, Object>> getPatientRecord(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"))) {
                    Long patientId = user.getPatientId();
                    if (!id.equals(patientId)) {
                        return new ResponseEntity<>(Map.of("message", "Prontuário não associado ao usuário autenticado"), HttpStatus.FORBIDDEN);
                    }
                }
                PatientRecordDTO record = patientRecordService.getPatientRecord(id);
                return new ResponseEntity<>(Map.of("message", "Prontuário encontrado com sucesso", "record", record), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message", "Usuário não autenticado"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Paciente não encontrado"), HttpStatus.NOT_FOUND);
        }
    }
}