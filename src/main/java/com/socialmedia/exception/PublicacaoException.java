package com.socialmedia.exception;

/**
 * Exceção para erros relacionados a publicações
 */
public class PublicacaoException extends Exception {
    public PublicacaoException(String mensagem) {
        super(mensagem);
    }

    public PublicacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

