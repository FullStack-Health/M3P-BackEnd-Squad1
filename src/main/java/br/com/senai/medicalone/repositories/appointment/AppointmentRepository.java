package br.com.senai.medicalone.repositories.appointment;

import br.com.senai.medicalone.entities.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}