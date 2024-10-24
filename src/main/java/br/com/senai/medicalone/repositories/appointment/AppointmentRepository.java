package br.com.senai.medicalone.repositories.appointment;

import br.com.senai.medicalone.entities.appointment.Appointment;
import br.com.senai.medicalone.entities.exam.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    Page<Appointment> findAll(Pageable pageable);
    Optional<Appointment> findByPatientIdAndAppointmentDateAndAppointmentTime(Long patientId, LocalDate appointmentDate, LocalTime appointmentTime);
}