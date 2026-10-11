package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "perfil", schema = "swapcloset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "username",
            nullable = false,
            unique = true
    )
    private Usuario usuario;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido1", nullable = false, length = 80)
    private String apellido1;

    @Column(name = "apellido2", length = 80)
    private String apellido2;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(name = "talla_pantalon", length = 20)
    private String tallaPantalon;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "talla_zapatos", length = 20)
    private String tallaZapatos;

    @Column(name = "talla_camiseta", length = 20)
    private String tallaCamiseta;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "perfil_estilo",
            schema = "swapcloset",
            joinColumns = @JoinColumn(name = "id_perfil"),
            inverseJoinColumns = @JoinColumn(name = "id_estilo")
    )
    @Builder.Default
    private Set<Estilos> estilos = new HashSet<>();

    @Column(
            name = "creado_en",
            nullable = false,
            updatable = false
    )
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    public void antesDeGuardar() {

        LocalDateTime ahora = LocalDateTime.now();

        if (creadoEn == null) {
            creadoEn = ahora;
        }

        actualizadoEn = ahora;
    }

    @PreUpdate
    public void antesDeActualizar() {
        actualizadoEn = LocalDateTime.now();
    }
}