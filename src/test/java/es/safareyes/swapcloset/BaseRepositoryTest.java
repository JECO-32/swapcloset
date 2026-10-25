package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.repositorios.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

abstract class BaseRepositoryTest {

    @Autowired protected UsuarioRepository usuarioRepository;
    @Autowired protected PerfilRepository perfilRepository;
    @Autowired protected CategoriaRepository categoriaRepository;
    @Autowired protected PrendaRepository prendaRepository;
    @Autowired protected EstilosRepository estilosRepository;
    @Autowired protected SolicitudesRepository solicitudesRepository;
    @Autowired protected EntityManager em;

    protected Perfil crearPerfil(String prefijo) {

        String username = prefijo + "_" + UUID.randomUUID().toString().substring(0, 8);

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .username(username)
                .contrasena("1234")
                .rol(RolUsuario.USUARIO)
                .build());

        return perfilRepository.save(Perfil.builder()
                .usuario(usuario)
                .nombre("Test")
                .apellido1("Prueba")
                .email(username + "@test.es")
                .fechaNacimiento(LocalDate.of(2005, 5, 10))
                .build());
    }

    protected Categoria crearCategoria(String nombre) {

        return categoriaRepository.save(Categoria.builder()
                .nombre(nombre + "_" + UUID.randomUUID().toString().substring(0, 8))
                .build());
    }

    protected Prenda crearPrenda(Perfil perfil, Categoria categoria,
                                 String titulo, EstadoPrenda estado) {

        return crearPrenda(perfil, categoria, titulo, estado, "M");
    }

    protected Prenda crearPrenda(Perfil perfil, Categoria categoria,
                                 String titulo, EstadoPrenda estado, String talla) {

        return prendaRepository.save(Prenda.builder()
                .perfil(perfil)
                .categoria(categoria)
                .titulo(titulo)
                .talla(talla)
                .estadoConservacion(EstadoConservacion.BUEN_ESTADO)
                .estadoPrenda(estado)
                .permitePrestamo(true)
                .permiteIntercambio(true)
                .build());
    }

    protected Solicitudes crearSolicitud(Perfil solicitante, Prenda prenda,
                                         TipoSolicitud tipo, EstadoSolicitud estado) {

        return solicitudesRepository.save(Solicitudes.builder()
                .solicitante(solicitante)
                .prendaSolicitada(prenda)
                .tipoSolicitud(tipo)
                .estado(estado)
                .build());
    }
}