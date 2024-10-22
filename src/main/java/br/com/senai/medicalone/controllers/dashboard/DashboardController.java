package br.com.senai.medicalone.controllers.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import br.com.senai.medicalone.services.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardMetricsDTO> getDashboardData() {
        DashboardMetricsDTO statistics = dashboardService.generateDashboardMetrics();
        return ResponseEntity.ok(statistics);
    }
}