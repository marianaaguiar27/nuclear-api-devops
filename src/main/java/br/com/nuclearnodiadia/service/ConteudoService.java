package br.com.nuclearnodiadia.service;

import br.com.nuclearnodiadia.dto.ConteudoRequest;
import br.com.nuclearnodiadia.dto.ConteudoResponse;
import br.com.nuclearnodiadia.exception.RecursoNaoEncontradoException;
import br.com.nuclearnodiadia.model.Categoria;
import br.com.nuclearnodiadia.model.Conteudo;
import br.com.nuclearnodiadia.repository.ConteudoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConteudoService {

    private final ConteudoRepository repository;

    public ConteudoService(ConteudoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ConteudoResponse> listar(Categoria categoria) {
        List<Conteudo> lista = (categoria == null)
                ? repository.findAll()
                : repository.findByCategoria(categoria);
        return lista.stream().map(ConteudoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ConteudoResponse buscarPorId(Long id) {
        return ConteudoResponse.de(obter(id));
    }

    @Transactional(readOnly = true)
    public List<ConteudoResponse> buscarPorTexto(String texto) {
        return repository
                .findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(texto, texto)
                .stream().map(ConteudoResponse::de).toList();
    }

    @Transactional
    public ConteudoResponse criar(ConteudoRequest req) {
        Conteudo c = new Conteudo();
        preencher(c, req);
        return ConteudoResponse.de(repository.save(c));
    }

    @Transactional
    public ConteudoResponse atualizar(Long id, ConteudoRequest req) {
        Conteudo c = obter(id);
        preencher(c, req);
        return ConteudoResponse.de(repository.save(c));
    }

    @Transactional
    public void remover(Long id) {
        repository.delete(obter(id));
    }

    private Conteudo obter(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Conteudo nao encontrado com id " + id));
    }

    private void preencher(Conteudo c, ConteudoRequest req) {
        c.setTitulo(req.titulo().trim());
        c.setCategoria(req.categoria());
        c.setDescricao(req.descricao().trim());
        c.setNivel(req.nivel());
        c.setFonte(req.fonte());
    }
}