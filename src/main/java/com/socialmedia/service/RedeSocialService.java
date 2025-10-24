package com.socialmedia.service;

import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.model.Conteudo;
import com.socialmedia.model.Estatisticas;
import com.socialmedia.model.Publicacao;

/**
 * Interface unificada para todas as redes sociais
 * Esta é a interface alvo do padrão Adapter
 */
public interface RedeSocialService {
    
    /**
     * Autentica na plataforma
     * @param credenciais Credenciais de autenticação
     * @throws AutenticacaoException se a autenticação falhar
     */
    void autenticar(String credenciais) throws AutenticacaoException;
    
    /**
     * Publica conteúdo na rede social
     * @param conteudo Conteúdo a ser publicado
     * @return Publicação realizada
     * @throws PublicacaoException se a publicação falhar
     */
    Publicacao publicar(Conteudo conteudo) throws PublicacaoException;
    
    /**
     * Agenda uma publicação para uma data futura
     * @param conteudo Conteúdo a ser agendado
     * @return Publicação agendada
     * @throws PublicacaoException se o agendamento falhar
     */
    Publicacao agendar(Conteudo conteudo) throws PublicacaoException;
    
    /**
     * Remove uma publicação
     * @param publicacaoId ID da publicação
     * @return true se removido com sucesso
     * @throws PublicacaoException se a remoção falhar
     */
    boolean remover(String publicacaoId) throws PublicacaoException;
    
    /**
     * Obtém estatísticas de uma publicação
     * @param publicacaoId ID da publicação
     * @return Estatísticas da publicação
     * @throws PublicacaoException se não conseguir obter estatísticas
     */
    Estatisticas obterEstatisticas(String publicacaoId) throws PublicacaoException;
    
    /**
     * Retorna o nome da plataforma
     * @return Nome da plataforma
     */
    String getNomePlataforma();
    
    /**
     * Verifica se está autenticado
     * @return true se autenticado
     */
    boolean isAutenticado();
}

