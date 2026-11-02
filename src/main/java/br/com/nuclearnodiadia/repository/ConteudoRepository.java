package br.com.nuclearnodiadia.repository;

import br.com.nuclearnodiadia.model.Categoria;
import br.com.nuclearnodiadia.model.Conteudo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {

    List<Conteudo> findByCategoria(Categoria categoria);

    // Busca por texto procura no titulo ou na descricao
    List<Conteudo> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String titulo, String descricao);
}