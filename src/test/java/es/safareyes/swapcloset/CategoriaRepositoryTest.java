package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;

import org.junit.jupiter.api.Test;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoriaRepositoryTest extends BaseRepositoryTest {

    @Test
    void categoriasConPrendasDisponibles() {

        Perfil perfil = crearPerfil("test_categoria_get");
        Categoria categoria = crearCategoria("Categoria GET ZZ");

        crearPrenda(perfil, categoria, "Disponible", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Borrador", EstadoPrenda.BORRADOR);

        em.flush();

        List<Object[]> resultado = categoriaRepository.categoriasConNumeroDisponibles();

        Object[] fila = resultado.stream()
                .filter(r -> "Categoria GET ZZ".equals(r[1]))
                .findFirst()
                .orElseThrow();

        assertThat(((Number) fila[2]).longValue()).isEqualTo(1L);
    }
}