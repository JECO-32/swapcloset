package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.Categoria;
import es.safareyes.swapcloset.repositorios.CategoriaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void guardarCategoria() {

        Categoria categoria = new Categoria();
        categoria.setNombre("Sudaderas");

        Categoria guardada = categoriaRepository.save(categoria);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Sudaderas");
    }

    @Test
    void buscarCategoriaPorNombre() {

        Categoria categoria = new Categoria();
        categoria.setNombre("Camisetas");

        categoriaRepository.save(categoria);

        Optional<Categoria> resultado =
                categoriaRepository.findByNombre("Camisetas");

        assertThat(resultado).isPresent();

        assertThat(resultado.get().getNombre())
                .isEqualTo("Camisetas");
    }

    @Test
    void buscarCategoriaIgnorandoMayusculas() {

        Categoria categoria = new Categoria();
        categoria.setNombre("Pantalones");

        categoriaRepository.save(categoria);

        boolean existe =
                categoriaRepository.existsByNombreIgnoreCase(
                        "PANTALONES"
                );

        assertThat(existe).isTrue();
    }

    @Test
    void buscarCategoriaQueContengaTexto() {

        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Camisetas deportivas");

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Camisetas casual");

        categoriaRepository.save(categoria1);
        categoriaRepository.save(categoria2);

        List<Categoria> resultado =
                categoriaRepository.findByNombreContainingIgnoreCase(
                        "camisetas"
                );

        assertThat(resultado).hasSize(2);
    }
}