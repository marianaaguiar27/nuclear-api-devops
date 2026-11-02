package br.com.nuclearnodiadia.dto;

import br.com.nuclearnodiadia.model.Categoria;
import br.com.nuclearnodiadia.model.Conteudo;
import br.com.nuclearnodiadia.model.Nivel;

public record ConteudoResponse(
        Long id,
        String titulo,
        Categoria categoria,
        String descricao,
        Nivel nivel,
        String fonte
) {
    public static ConteudoResponse de(Conteudo c) {
        return new ConteudoResponse(
                c.getId(), c.getTitulo(), c.getCategoria(),
                c.getDescricao(), c.getNivel(), c.getFonte());
    }
}