package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="solicitudes",schema = "swapcloset")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Solicitudes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="tipo_solicitud")
    private String tipo_solicitud;

    @Column (name="estado")
    private String estado;
}
