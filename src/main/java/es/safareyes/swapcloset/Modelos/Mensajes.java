package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "mensajes", schema = "swapcloset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensajes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitudes solicitud;

    @Column(name = "hora_envio", nullable = false)
    private LocalTime horaEnvio;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDate fechaEnvio;

    @Column(name = "contenido", nullable = false,
            columnDefinition = "TEXT")
    private String contenido;
}
