package es.safareyes.swapcloset.Modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prenda", schema = "swapcloset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prenda")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "marca", length = 80)
    private String marca;

    @Column(name = "talla", length = 30)
    private String talla;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conservacion", nullable = false, length = 30)
    private EstadoConservacion estadoConservacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_prenda", nullable = false, length = 30)
    private EstadoPrenda estadoPrenda;

    @Column(name = "permite_prestamo", nullable = false)
    private Boolean permitePrestamo;

    @Column(name = "permite_intercambio", nullable = false)
    private Boolean permiteIntercambio;

    @Column(name = "fecha_publicacion", nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    @OneToMany(mappedBy = "prenda", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Foto> fotos = new ArrayList<>();

    @PrePersist
    public void antesDeGuardar() {
        if (fechaPublicacion == null) fechaPublicacion = LocalDateTime.now();
        if (estadoPrenda == null) estadoPrenda = EstadoPrenda.BORRADOR;
    }
}