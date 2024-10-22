package br.com.senai.medicalone.dtos.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "DTO que representa as métricas do dashboard")
public class DashboardMetricsDTO {

    @Schema(description = "Dados do dashboard", example = "{\"statistics\": {\"appointmentCount\": 4, \"userCount\": 7, \"patientCount\": 1, \"examCount\": 2}}")
    private Map<String, Long> statistics;

    public DashboardMetricsDTO(Map<String, Long> statistics) {
        this.statistics = statistics;
    }

    public Map<String, Long> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Long> statistics) {
        this.statistics = statistics;
    }
}