package com.socialmedia.adapter;

import com.socialmedia.api.instagram.InstagramAPI;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.model.*;
import com.socialmedia.service.RedeSocialService;


// Adapter para a API do Instagram
// Converte a interface do InstagramAPI para a interface RedeSocialService
public class InstagramAdapter implements RedeSocialService {
    private final InstagramAPI instagramAPI;
    private static final String PLATAFORMA = "Instagram";

    public InstagramAdapter() {
        this.instagramAPI = new InstagramAPI();
    }

    @Override
    public void autenticar(String credenciais) throws AutenticacaoException {
        try {
            // Credenciais no formato de access token
            instagramAPI.login(credenciais);
        } catch (Exception e) {
            throw new AutenticacaoException("Erro ao autenticar no Instagram: " + e.getMessage(), e);
        }
    }

    @Override
    public Publicacao publicar(Conteudo conteudo) throws PublicacaoException {
        try {
            validarConteudo(conteudo);
            
            InstagramAPI.InstagramMedia media;
            
            if (conteudo.getTipo() == TipoConteudo.STORY) {
                media = publicarStory(conteudo);
            } else {
                media = publicarPost(conteudo);
            }
            
            return new Publicacao.Builder()
                    .id(media.id)
                    .plataforma(PLATAFORMA)
                    .conteudo(conteudo)
                    .dataPublicacao(media.timestamp)
                    .status(StatusPublicacao.PUBLICADO)
                    .urlPublicacao(media.permalink)
                    .build();
        } catch (Exception e) {
            return criarPublicacaoFalha(conteudo, e.getMessage());
        }
    }

    @Override
    public Publicacao agendar(Conteudo conteudo) throws PublicacaoException {
        validarConteudo(conteudo);
        
        if (conteudo.getDataAgendamento() == null) {
            throw new PublicacaoException("Data de agendamento é obrigatória");
        }
        
        // Instagram suporta agendamento através de ferramentas de terceiros ou Creator Studio
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
            return instagramAPI.deleteMedia(publicacaoId);
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao remover mídia: " + e.getMessage(), e);
        }
    }

    @Override
    public Estatisticas obterEstatisticas(String publicacaoId) throws PublicacaoException {
        try {
            InstagramAPI.InstagramInsights insights = instagramAPI.getMediaInsights(publicacaoId);
            
            return new Estatisticas.Builder()
                    .publicacaoId(publicacaoId)
                    .plataforma(PLATAFORMA)
                    .visualizacoes(insights.reach)
                    .curtidas(insights.likes)
                    .compartilhamentos(insights.shares)
                    .comentarios(insights.comments)
                    .taxaEngajamento(insights.engagement)
                    .build();
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao obter insights: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNomePlataforma() {
        return PLATAFORMA;
    }

    @Override
    public boolean isAutenticado() {
        return instagramAPI.isAuthenticated();
    }

    private void validarConteudo(Conteudo conteudo) throws PublicacaoException {
        if (!isAutenticado()) {
            throw new PublicacaoException("Não autenticado no Instagram");
        }
        
        if (conteudo == null) {
            throw new PublicacaoException("Conteúdo não pode ser nulo");
        }
        
        if (conteudo.getMidias().isEmpty()) {
            throw new PublicacaoException("Instagram requer ao menos uma mídia (imagem ou vídeo)");
        }
    }

    private InstagramAPI.InstagramMedia publicarPost(Conteudo conteudo) throws Exception {
        InstagramAPI.MediaRequest request = new InstagramAPI.MediaRequest();
        request.caption = formatarCaption(conteudo);
        request.mediaType = determinarTipoMidia(conteudo);
        request.mediaUrl = conteudo.getMidias().get(0);
        
        if (conteudo.getMidias().size() > 1) {
            request.mediaType = "CAROUSEL_ALBUM";
            request.childMediaUrls = conteudo.getMidias().toArray(new String[0]);
        }
        
        return instagramAPI.createMediaPost(request);
    }

    private InstagramAPI.InstagramMedia publicarStory(Conteudo conteudo) throws Exception {
        String mediaUrl = conteudo.getMidias().get(0);
        boolean isVideo = mediaUrl.endsWith(".mp4") || mediaUrl.endsWith(".mov");
        
        return instagramAPI.createStory(
            isVideo ? null : mediaUrl,
            isVideo ? mediaUrl : null
        );
    }

    private String formatarCaption(Conteudo conteudo) {
        StringBuilder caption = new StringBuilder(conteudo.getTexto());
        
        // Instagram permite hashtags inline ou no final
        if (!conteudo.getHashtags().isEmpty()) {
            caption.append("\n\n");
            for (String hashtag : conteudo.getHashtags()) {
                caption.append("#").append(hashtag.replace("#", "")).append(" ");
            }
        }
        
        return caption.toString().trim();
    }

    private String determinarTipoMidia(Conteudo conteudo) {
        if (conteudo.getTipo() == TipoConteudo.VIDEO || conteudo.getTipo() == TipoConteudo.REEL) {
            return "VIDEO";
        }
        return "IMAGE";
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

