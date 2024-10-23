package br.com.senai.medicalone.controllers.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import br.com.senai.medicalone.services.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Busca todos os dados dashboard", description = "Endpoint para obter dados do dashboard")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do dashboard obtidos com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"data\": {\"statistics\": {\"appointmentCount\": 4, \"userCount\": 7, \"patientCount\": 1, \"examCount\": 2}}, \"message\": \"Dados do dashboard obtidos com sucesso\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro ao obter dados do dashboard", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Erro ao obter dados do dashboard\"}")))
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            DashboardMetricsDTO statistics = dashboardService.generateDashboardMetrics();
            return new ResponseEntity<>(Map.of("message", "Dados do dashboard obtidos com sucesso", "data", statistics), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao obter dados do dashboard"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}