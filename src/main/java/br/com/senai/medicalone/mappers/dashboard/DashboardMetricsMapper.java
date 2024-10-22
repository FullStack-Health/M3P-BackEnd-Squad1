package br.com.senai.medicalone.mappers.dashboard;

import br.com.senai.medicalone.dtos.dashboard.DashboardMetricsDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DashboardMetricsMapper {

    @Autowired
    private ModelMapper modelMapper;

    public DashboardMetricsDTO toDTO(Map<String, Long> statistics) {
        return new DashboardMetricsDTO(statistics);
    }
}