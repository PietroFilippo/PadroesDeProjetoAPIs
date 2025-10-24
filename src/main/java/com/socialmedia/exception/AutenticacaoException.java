package com.socialmedia.exception;

/**
 * Exceção para erros de autenticação
 */
public class AutenticacaoException extends Exception {
    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }

    public AutenticacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

