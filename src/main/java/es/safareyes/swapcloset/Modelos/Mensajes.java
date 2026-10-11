package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes", schema = "swapcloset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensajes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_solicitud",
            nullable = false
    )
    private Solicitudes solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor", nullable = false)
    private Perfil autor;

    @Column(name = "contenido", nullable = false, length = 1000)
    private String contenido;

    @Column(name = "enviado_en", nullable = false, updatable = false)
    private LocalDateTime enviadoEn;

    @PrePersist
    public void antesDeGuardar() {

        if (enviadoEn == null) {
            enviadoEn = LocalDateTime.now();
        }
    }
}