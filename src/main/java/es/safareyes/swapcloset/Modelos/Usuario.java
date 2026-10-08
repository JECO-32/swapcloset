package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="usuario",schema = "swapcloset")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="username")
    private String username;

    @Column(name="rol")
    private String rol;

    @Column(name="contraseña")
    private String contraseña;
}
