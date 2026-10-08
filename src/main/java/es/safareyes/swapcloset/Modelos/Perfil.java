package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="perfil",schema = "swapcloset")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="username")
    private String username;

    @Column(name="nombre")
    private String nombre;

    @Column(name="apellido1")
    private String apellido1;

    @Column(name="apellido2")
    private String apellido2;

    @Column(name="biografia")
    private String biografia;

    @Column(name="Ciudad")
    private String Ciudad;

    @Column(name="email")
    private String email;

    @Column(name="talla_pantalon")
    private String talla_pantalon;

    @Column(name="talla_zapatos")
    private Integer talla_zapatos;

    @Column(name="talla_camiseta")
    private String talla_camiseta;
}
