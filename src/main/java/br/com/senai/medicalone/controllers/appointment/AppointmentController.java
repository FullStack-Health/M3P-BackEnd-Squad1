package br.com.senai.medicalone.controllers.appointment;

import br.com.senai.medicalone.dtos.appointment.AppointmentRequestDTO;
import br.com.senai.medicalone.dtos.appointment.AppointmentResponseDTO;
import br.com.senai.medicalone.services.appointment.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Criar uma nova consulta", description = "Endpoint para criar uma nova consulta")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO createdAppointment = appointmentService.createAppointment(dto);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter consulta por ID", description = "Endpoint para obter uma consulta pelo ID")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma consulta", description = "Endpoint para atualizar uma consulta")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma consulta", description = "Endpoint para excluir uma consulta")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar todas as consultas", description = "Endpoint para listar todas as consultas")
    public ResponseEntity<List<AppointmentResponseDTO>> listAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.listAppointments();
        return ResponseEntity.ok(appointments);
    }
}