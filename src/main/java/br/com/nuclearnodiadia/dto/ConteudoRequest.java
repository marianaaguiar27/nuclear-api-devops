package br.com.nuclearnodiadia.dto;

import br.com.nuclearnodiadia.model.Categoria;
import br.com.nuclearnodiadia.model.Nivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConteudoRequest(

        @NotBlank(message = "O titulo e obrigatorio")
        @Size(min = 3, max = 120, message = "O titulo deve ter entre 3 e 120 caracteres")
        String titulo,

        @NotNull(message = "A categoria e obrigatoria")
        Categoria categoria,

        @NotBlank(message = "A descricao e obrigatoria")
        @Size(min = 20, max = 1000, message = "A descricao deve ter entre 20 e 1000 caracteres")
        String descricao,

        @NotNull(message = "O nivel e obrigatorio")
        Nivel nivel,

        @Size(max = 150, message = "A fonte deve ter no maximo 150 caracteres")
        String fonte
) {
}