package com.socialmedia.adapter;

import com.socialmedia.api.twitter.TwitterAPI;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.model.*;
import com.socialmedia.service.RedeSocialService;

// Adapter para a API do Twitter
// Converte a interface do TwitterAPI para a interface RedeSocialService
public class TwitterAdapter implements RedeSocialService {
    private final TwitterAPI twitterAPI;
    private static final String PLATAFORMA = "Twitter";

    public TwitterAdapter() {
        this.twitterAPI = new TwitterAPI();
    }

    @Override
    public void autenticar(String credenciais) throws AutenticacaoException {
        try {
            // Credenciais no formato: "apiKey:apiSecret"
            String[] partes = credenciais.split(":");
            if (partes.length != 2) {
                throw new AutenticacaoException("Formato de credenciais inválido. Use: apiKey:apiSecret");
            }
            twitterAPI.authenticate(partes[0], partes[1]);
        } catch (Exception e) {
            throw new AutenticacaoException("Erro ao autenticar no Twitter: " + e.getMessage(), e);
        }
    }

    @Override
    public Publicacao publicar(Conteudo conteudo) throws PublicacaoException {
        try {
            validarConteudo(conteudo);
            
            String texto = formatarTexto(conteudo);
            String[] mediaUrls = conteudo.getMidias().toArray(new String[0]);
            
            TwitterAPI.TwitterPost post = twitterAPI.tweet(texto, mediaUrls);
            
            return new Publicacao.Builder()
                    .id(post.id)
                    .plataforma(PLATAFORMA)
                    .conteudo(conteudo)
                    .dataPublicacao(post.createdAt)
                    .status(StatusPublicacao.PUBLICADO)
                    .urlPublicacao("https://twitter.com/i/web/status/" + post.id)
                    .build();
        } catch (Exception e) {
            return criarPublicacaoFalha(conteudo, e.getMessage());
        }
    }

    @Override
    public Publicacao agendar(Conteudo conteudo) throws PublicacaoException {
        // Twitter API v2 suporta agendamento através de parâmetros específicos
        // Para simplificação, simulamos o agendamento
        validarConteudo(conteudo);
        
        if (conteudo.getDataAgendamento() == null) {
            throw new PublicacaoException("Data de agendamento é obrigatória");
        }
        
        return new Publicacao.Builder()
                .plataforma(PLATAFORMA)
                .conteudo(conteudo)
                .dataPublicacao(conteudo.getDataAgendamento())
                .status(StatusPublicacao.AGENDADO)
                .build();
    }

    @Override
    public boolean remover(String publicacaoId) throws PublicacaoException {
        try {
            return twitterAPI.deleteTweet(publicacaoId);
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao remover tweet: " + e.getMessage(), e);
        }
    }

    @Override
    public Estatisticas obterEstatisticas(String publicacaoId) throws PublicacaoException {
        try {
            TwitterAPI.TwitterMetrics metrics = twitterAPI.getTweetMetrics(publicacaoId);
            
            return new Estatisticas.Builder()
                    .publicacaoId(publicacaoId)
                    .plataforma(PLATAFORMA)
                    .visualizacoes(metrics.impressions)
                    .curtidas(metrics.likes)
                    .compartilhamentos(metrics.retweets)
                    .comentarios(metrics.replies)
                    .taxaEngajamento(metrics.engagementRate)
                    .build();
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao obter estatísticas: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNomePlataforma() {
        return PLATAFORMA;
    }

    @Override
    public boolean isAutenticado() {
        return twitterAPI.isAuthenticated();
    }

    private void validarConteudo(Conteudo conteudo) throws PublicacaoException {
        if (!isAutenticado()) {
            throw new PublicacaoException("Não autenticado no Twitter");
        }
        
        if (conteudo == null) {
            throw new PublicacaoException("Conteúdo não pode ser nulo");
        }
    }

    private String formatarTexto(Conteudo conteudo) {
        StringBuilder texto = new StringBuilder(conteudo.getTexto());
        
        // Adiciona hashtags ao final
        if (!conteudo.getHashtags().isEmpty()) {
            texto.append("\n\n");
            for (String hashtag : conteudo.getHashtags()) {
                texto.append("#").append(hashtag.replace("#", "")).append(" ");
            }
        }
        
        return texto.toString().trim();
    }

    private Publicacao criarPublicacaoFalha(Conteudo conteudo, String mensagemErro) {
        return new Publicacao.Builder()
                .plataforma(PLATAFORMA)
                .conteudo(conteudo)
                .status(StatusPublicacao.FALHOU)
                .mensagemErro(mensagemErro)
                .build();
    }
}

