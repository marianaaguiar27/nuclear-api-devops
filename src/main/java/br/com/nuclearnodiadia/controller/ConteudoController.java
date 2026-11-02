package br.com.nuclearnodiadia.controller;

import br.com.nuclearnodiadia.dto.ConteudoRequest;
import br.com.nuclearnodiadia.dto.ConteudoResponse;
import br.com.nuclearnodiadia.model.Categoria;
import br.com.nuclearnodiadia.service.ConteudoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conteudos")
@Tag(name = "Conteudos", description = "Curiosidades sobre aplicacoes da tecnologia nuclear")
public class ConteudoController {

    private final ConteudoService service;

    public ConteudoController(ConteudoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista os conteudos, com filtro opcional por categoria")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public List<ConteudoResponse> listar(
            @Parameter(description = "SAUDE, INDUSTRIA, AGRICULTURA, PESQUISA ou ENERGIA")
            @RequestParam(required = false) Categoria categoria) {
        return service.listar(categoria);
    }

    @GetMapping("/busca")
    @Operation(summary = "Busca por texto no titulo ou na descricao")
    @ApiResponse(responseCode = "200", description = "Resultados da busca")
    public List<ConteudoResponse> buscar(
            @Parameter(description = "Trecho a procurar, ex.: radiacao")
            @RequestParam String texto) {
        return service.buscarPorTexto(texto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um conteudo pelo id")
    @ApiResponse(responseCode = "200", description = "Conteudo encontrado")
    @ApiResponse(responseCode = "404", description = "Conteudo nao encontrado")
    public ConteudoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo conteudo")
    @ApiResponse(responseCode = "201", description = "Conteudo criado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    public ResponseEntity<ConteudoResponse> criar(@Valid @RequestBody ConteudoRequest req) {
        ConteudoResponse criado = service.criar(req);
        URI location = URI.create("/api/conteudos/" + criado.id());
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um conteudo existente")
    @ApiResponse(responseCode = "200", description = "Conteudo atualizado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "404", description = "Conteudo nao encontrado")
    public ConteudoResponse atualizar(@PathVariable Long id,
                                      @Valid @RequestBody ConteudoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um conteudo")
    @ApiResponse(responseCode = "204", description = "Conteudo removido")
    @ApiResponse(responseCode = "404", description = "Conteudo nao encontrado")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}