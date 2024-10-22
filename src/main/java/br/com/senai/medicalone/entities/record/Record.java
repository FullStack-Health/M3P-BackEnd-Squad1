package br.com.senai.medicalone.entities.record;

import br.com.senai.medicalone.entities.patient.Patient;
import br.com.senai.medicalone.entities.exam.Exam;
import br.com.senai.medicalone.entities.appointment.Appointment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "tb_record")
@Data
@NoArgsConstructor
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Exam> exams;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
}