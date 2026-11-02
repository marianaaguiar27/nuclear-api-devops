package br.com.nuclearnodiadia.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        int status,
        String mensagem,
        List<String> detalhes,
        LocalDateTime timestamp
) {
    public static ErroResponse de(int status, String mensagem, List<String> detalhes) {
        return new ErroResponse(status, mensagem, detalhes, LocalDateTime.now());
    }
}