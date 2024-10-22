package br.com.senai.medicalone.dtos.dashboard;

import java.util.Map;

public class DashboardMetricsDTO {
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