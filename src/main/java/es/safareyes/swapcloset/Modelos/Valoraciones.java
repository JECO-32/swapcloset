package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "valoraciones",
        schema = "swapcloset",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_valoracion_solicitud_autor",
                        columnNames = {
                                "id_solicitud",
                                "id_autor"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Valoraciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_solicitud",
            nullable = false
    )
    private Solicitudes solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_autor",
            nullable = false
    )
    private Perfil autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_valorado", nullable = false)
    private Perfil valorado;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void antesDeGuardar() {

        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}