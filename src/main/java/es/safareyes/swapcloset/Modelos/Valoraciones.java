package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="valoraciones",schema = "swapcloset")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Valoraciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="comentario")
    private String comentario;

    @Column(name="puntuacion")
    private Integer puntuacion;

    @ManyToMany(fecha=FetchType.LAZY)
    @JoinColumn(name="id_perfil")
    private Valoraciones id_perfil;
}
