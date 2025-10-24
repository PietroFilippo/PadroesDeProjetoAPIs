package com.socialmedia.adapter;

import com.socialmedia.api.linkedin.LinkedInAPI;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.model.*;
import com.socialmedia.service.RedeSocialService;


// Adapter para a API do LinkedIn
// Converte a interface do LinkedInAPI para a interface RedeSocialService
public class LinkedInAdapter implements RedeSocialService {
    private final LinkedInAPI linkedInAPI;
    private static final String PLATAFORMA = "LinkedIn";

    public LinkedInAdapter() {
        this.linkedInAPI = new LinkedInAPI();
    }

    @Override
    public void autenticar(String credenciais) throws AutenticacaoException {
        try {
            // Credenciais no formato: "clientId:clientSecret:redirectUri"
            String[] partes = credenciais.split(":");
            if (partes.length < 2) {
                throw new AutenticacaoException("Formato de credenciais inválido. Use: clientId:clientSecret[:redirectUri]");
            }
            
            String clientId = partes[0];
            String clientSecret = partes[1];
            String redirectUri = partes.length > 2 ? partes[2] : "https://localhost/callback";
            
            linkedInAPI.authorize(clientId, clientSecret, redirectUri);
        } catch (Exception e) {
            throw new AutenticacaoException("Erro ao autenticar no LinkedIn: " + e.getMessage(), e);
        }
    }

    @Override
    public Publicacao publicar(Conteudo conteudo) throws PublicacaoException {
        try {
            validarConteudo(conteudo);
            
            LinkedInAPI.LinkedInShare share;
            
            if (conteudo.getTipo() == TipoConteudo.ARTIGO) {
                share = publicarArtigo(conteudo);
            } else {
                share = publicarShare(conteudo);
            }
            
            return new Publicacao.Builder()
                    .id(share.shareUrn)
                    .plataforma(PLATAFORMA)
                    .conteudo(conteudo)
                    .dataPublicacao(share.created)
                    .status(StatusPublicacao.PUBLICADO)
                    .urlPublicacao("https://www.linkedin.com/feed/update/" + share.shareUrn)
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
        
        // LinkedIn suporta agendamento através de ferramentas de gestão
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
            return linkedInAPI.deleteShare(publicacaoId);
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao remover share: " + e.getMessage(), e);
        }
    }

    @Override
    public Estatisticas obterEstatisticas(String publicacaoId) throws PublicacaoException {
        try {
            LinkedInAPI.LinkedInAnalytics analytics = linkedInAPI.getShareStatistics(publicacaoId);
            
            return new Estatisticas.Builder()
                    .publicacaoId(publicacaoId)
                    .plataforma(PLATAFORMA)
                    .visualizacoes(analytics.impressionCount)
                    .curtidas(analytics.likeCount)
                    .compartilhamentos(analytics.shareCount)
                    .comentarios(analytics.commentCount)
                    .taxaEngajamento(analytics.engagementRate)
                    .build();
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao obter analytics: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNomePlataforma() {
        return PLATAFORMA;
    }

    @Override
    public boolean isAutenticado() {
        return linkedInAPI.isAuthenticated();
    }

    private void validarConteudo(Conteudo conteudo) throws PublicacaoException {
        if (!isAutenticado()) {
            throw new PublicacaoException("Não autenticado no LinkedIn");
        }
        
        if (conteudo == null) {
            throw new PublicacaoException("Conteúdo não pode ser nulo");
        }
    }

    private LinkedInAPI.LinkedInShare publicarShare(Conteudo conteudo) throws Exception {
        LinkedInAPI.ShareRequest request = new LinkedInAPI.ShareRequest();
        request.commentary = formatarComentario(conteudo);
        request.visibility = "PUBLIC";
        
        if (!conteudo.getMidias().isEmpty()) {
            String mediaUrl = conteudo.getMidias().get(0);
            if (mediaUrl.endsWith(".mp4") || mediaUrl.endsWith(".mov")) {
                request.mediaCategory = "VIDEO";
            } else {
                request.mediaCategory = "IMAGE";
            }
            request.mediaUrl = mediaUrl;
        } else {
            request.mediaCategory = "NONE";
        }
        
        return linkedInAPI.createShare(request);
    }

    private LinkedInAPI.LinkedInShare publicarArtigo(Conteudo conteudo) throws Exception {
        LinkedInAPI.ArticleRequest request = new LinkedInAPI.ArticleRequest();
        
        // Para artigos, o texto pode ser dividido em título e conteúdo
        String[] partes = conteudo.getTexto().split("\n", 2);
        request.title = partes[0];
        request.content = partes.length > 1 ? partes[1] : partes[0];
        
        if (!conteudo.getMidias().isEmpty()) {
            request.thumbnailUrl = conteudo.getMidias().get(0);
        }
        
        return linkedInAPI.createArticle(request);
    }

    private String formatarComentario(Conteudo conteudo) {
        StringBuilder comentario = new StringBuilder(conteudo.getTexto());
        
        // LinkedIn suporta hashtags inline
        if (!conteudo.getHashtags().isEmpty()) {
            comentario.append("\n\n");
            for (String hashtag : conteudo.getHashtags()) {
                comentario.append("#").append(hashtag.replace("#", "")).append(" ");
            }
        }
        
        return comentario.toString().trim();
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

