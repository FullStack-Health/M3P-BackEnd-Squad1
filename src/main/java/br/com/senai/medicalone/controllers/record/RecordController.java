package br.com.senai.medicalone.controllers.record;

import br.com.senai.medicalone.dtos.record.RecordResponseDTO;
import br.com.senai.medicalone.dtos.patient.PatientResponseDTO;
import br.com.senai.medicalone.services.record.RecordService;
import br.com.senai.medicalone.utils.JwtUtil; // Importando JwtUtil
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import br.com.senai.medicalone.utils.JwtUtil;

@RestController
@RequestMapping("/api/pacientes")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/{id}/prontuarios")
    @Operation(summary = "Buscar prontuário do paciente", description = "Busca o prontuário do paciente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prontuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    public ResponseEntity<RecordResponseDTO> getPatientChart(@PathVariable Long id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        Long authenticatedPatientId = jwtUtil.getIdFromToken(token);

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE")) &&
                !authenticatedPatientId.equals(id)) {
            return ResponseEntity.status(403).build();
        }

        RecordResponseDTO chart = recordService.getChartByPatientId(id);
        return ResponseEntity.ok(chart);
    }
}