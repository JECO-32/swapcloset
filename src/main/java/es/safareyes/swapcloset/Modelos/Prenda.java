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
public class Prenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

}
