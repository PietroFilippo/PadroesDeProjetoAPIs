package com.socialmedia.service;

import com.socialmedia.config.ConfiguracaoPlataforma;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.factory.RedeSocialFactory;
import com.socialmedia.model.Conteudo;
import com.socialmedia.model.Estatisticas;
import com.socialmedia.model.Publicacao;
import com.socialmedia.strategy.RespostaStrategy;
import com.socialmedia.strategy.RespostaUnificada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gerenciador unificado de múltiplas plataformas de mídia social
 * Coordena as operações entre diferentes adapters
 */
public class GerenciadorMidiaSocial {
    private final Map<String, RedeSocialService> plataformasAtivas;
    private RespostaStrategy respostaStrategy;

    public GerenciadorMidiaSocial(RespostaStrategy respostaStrategy) {
        this.plataformasAtivas = new HashMap<>();
        this.respostaStrategy = respostaStrategy;
    }

    /**
     * Adiciona e autentica uma plataforma
     */
    public void adicionarPlataforma(ConfiguracaoPlataforma config) throws AutenticacaoException {
        if (!config.isAtiva()) {
            return;
        }

        RedeSocialService service = RedeSocialFactory.criarAutenticado(
            config.getPlataforma(),
            config.getCredenciais()
        );
        
        plataformasAtivas.put(config.getPlataforma().toUpperCase(), service);
    }

    /**
     * Remove uma plataforma
     */
    public boolean removerPlataforma(String plataforma) {
        return plataformasAtivas.remove(plataforma.toUpperCase()) != null;
    }

    /**
     * Publica conteúdo em múltiplas plataformas simultaneamente
     */
    public RespostaUnificada publicarEmMultiplasPlataformas(
            Conteudo conteudo, 
            List<String> plataformas) {
        
        List<Publicacao> resultados = plataformas.stream()
            .map(String::toUpperCase)
            .filter(plataformasAtivas::containsKey)
            .map(plataforma -> publicarEmPlataforma(plataforma, conteudo))
            .collect(Collectors.toList());

        return respostaStrategy.processar(resultados);
    }

    /**
     * Publica em todas as plataformas ativas
     */
    public RespostaUnificada publicarEmTodasPlataformas(Conteudo conteudo) {
        List<Publicacao> resultados = plataformasAtivas.values().stream()
            .map(service -> {
                try {
                    return service.publicar(conteudo);
                } catch (PublicacaoException e) {
                    return criarPublicacaoErro(service.getNomePlataforma(), conteudo, e);
                }
            })
            .collect(Collectors.toList());

        return respostaStrategy.processar(resultados);
    }

    /**
     * Agenda publicação em múltiplas plataformas
     */
    public RespostaUnificada agendarEmMultiplasPlataformas(
            Conteudo conteudo, 
            List<String> plataformas) {
        
        List<Publicacao> resultados = plataformas.stream()
            .map(String::toUpperCase)
            .filter(plataformasAtivas::containsKey)
            .map(plataforma -> agendarEmPlataforma(plataforma, conteudo))
            .collect(Collectors.toList());

        return respostaStrategy.processar(resultados);
    }

    /**
     * Remove uma publicação de uma plataforma específica
     */
    public boolean removerPublicacao(String plataforma, String publicacaoId) 
            throws PublicacaoException {
        RedeSocialService service = obterService(plataforma);
        return service.remover(publicacaoId);
    }

    /**
     * Obtém estatísticas de uma publicação
     */
    public Estatisticas obterEstatisticas(String plataforma, String publicacaoId) 
            throws PublicacaoException {
        RedeSocialService service = obterService(plataforma);
        return service.obterEstatisticas(publicacaoId);
    }

    /**
     * Obtém estatísticas consolidadas de múltiplas plataformas
     */
    public List<Estatisticas> obterEstatisticasConsolidadas(
            Map<String, String> publicacoesPorPlataforma) {
        
        return publicacoesPorPlataforma.entrySet().stream()
            .map(entry -> {
                try {
                    return obterEstatisticas(entry.getKey(), entry.getValue());
                } catch (PublicacaoException e) {
                    return null;
                }
            })
            .filter(stats -> stats != null)
            .collect(Collectors.toList());
    }

    /**
     * Altera a strategy de processamento de respostas
     */
    public void setRespostaStrategy(RespostaStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy não pode ser nula");
        }
        this.respostaStrategy = strategy;
    }

    /**
     * Retorna as plataformas ativas
     */
    public List<String> getPlataformasAtivas() {
        return new ArrayList<>(plataformasAtivas.keySet());
    }

    /**
     * Verifica se uma plataforma está ativa
     */
    public boolean isPlataformaAtiva(String plataforma) {
        return plataformasAtivas.containsKey(plataforma.toUpperCase());
    }

    // Métodos privados auxiliares

    private Publicacao publicarEmPlataforma(String plataforma, Conteudo conteudo) {
        try {
            RedeSocialService service = plataformasAtivas.get(plataforma);
            return service.publicar(conteudo);
        } catch (PublicacaoException e) {
            return criarPublicacaoErro(plataforma, conteudo, e);
        }
    }

    private Publicacao agendarEmPlataforma(String plataforma, Conteudo conteudo) {
        try {
            RedeSocialService service = plataformasAtivas.get(plataforma);
            return service.agendar(conteudo);
        } catch (PublicacaoException e) {
            return criarPublicacaoErro(plataforma, conteudo, e);
        }
    }

    private RedeSocialService obterService(String plataforma) throws PublicacaoException {
        RedeSocialService service = plataformasAtivas.get(plataforma.toUpperCase());
        if (service == null) {
            throw new PublicacaoException("Plataforma não está ativa: " + plataforma);
        }
        return service;
    }

    private Publicacao criarPublicacaoErro(String plataforma, Conteudo conteudo, Exception e) {
        return new Publicacao.Builder()
            .plataforma(plataforma)
            .conteudo(conteudo)
            .status(com.socialmedia.model.StatusPublicacao.FALHOU)
            .mensagemErro(e.getMessage())
            .build();
    }
}

