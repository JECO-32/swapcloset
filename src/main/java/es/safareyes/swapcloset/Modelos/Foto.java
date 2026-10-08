package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="foto",schema = "swapcloset")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="url")
    private Integer url;

    @Column(name="orden")
    private Integer orden;
}
