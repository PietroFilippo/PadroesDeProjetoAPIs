package com.socialmedia.adapter;

import com.socialmedia.api.tiktok.TikTokAPI;
import com.socialmedia.exception.AutenticacaoException;
import com.socialmedia.exception.PublicacaoException;
import com.socialmedia.model.*;
import com.socialmedia.service.RedeSocialService;

// Adapter para a API do TikTok
// Converte a interface do TikTokAPI para a interface RedeSocialService
public class TikTokAdapter implements RedeSocialService {
    private final TikTokAPI tiktokAPI;
    private static final String PLATAFORMA = "TikTok";

    public TikTokAdapter() {
        this.tiktokAPI = new TikTokAPI();
    }

    @Override
    public void autenticar(String credenciais) throws AutenticacaoException {
        try {
            // Credenciais no formato: "appId:appSecret:authCode"
            String[] partes = credenciais.split(":");
            if (partes.length != 3) {
                throw new AutenticacaoException("Formato de credenciais inválido. Use: appId:appSecret:authCode");
            }
            
            tiktokAPI.authenticate(partes[0], partes[1], partes[2]);
        } catch (Exception e) {
            throw new AutenticacaoException("Erro ao autenticar no TikTok: " + e.getMessage(), e);
        }
    }

    @Override
    public Publicacao publicar(Conteudo conteudo) throws PublicacaoException {
        try {
            validarConteudo(conteudo);
            
            TikTokAPI.VideoUploadRequest request = criarRequestVideo(conteudo);
            TikTokAPI.TikTokVideo video = tiktokAPI.uploadVideo(request);
            
            return new Publicacao.Builder()
                    .id(video.videoId)
                    .plataforma(PLATAFORMA)
                    .conteudo(conteudo)
                    .dataPublicacao(video.createTime)
                    .status(StatusPublicacao.PUBLICADO)
                    .urlPublicacao(video.shareUrl)
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
        
        // TikTok permite agendamento através de ferramentas de criadores
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
            return tiktokAPI.deleteVideo(publicacaoId);
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao remover vídeo: " + e.getMessage(), e);
        }
    }

    @Override
    public Estatisticas obterEstatisticas(String publicacaoId) throws PublicacaoException {
        try {
            TikTokAPI.TikTokVideoInfo info = tiktokAPI.getVideoInfo(publicacaoId);
            
            return new Estatisticas.Builder()
                    .publicacaoId(publicacaoId)
                    .plataforma(PLATAFORMA)
                    .visualizacoes(info.viewCount)
                    .curtidas(info.likeCount)
                    .compartilhamentos(info.shareCount)
                    .comentarios(info.commentCount)
                    .taxaEngajamento(info.engagementRate)
                    .build();
        } catch (Exception e) {
            throw new PublicacaoException("Erro ao obter info do vídeo: " + e.getMessage(), e);
        }
    }

    @Override
    public String getNomePlataforma() {
        return PLATAFORMA;
    }

    @Override
    public boolean isAutenticado() {
        return tiktokAPI.isAuthenticated();
    }

    private void validarConteudo(Conteudo conteudo) throws PublicacaoException {
        if (!isAutenticado()) {
            throw new PublicacaoException("Não autenticado no TikTok");
        }
        
        if (conteudo == null) {
            throw new PublicacaoException("Conteúdo não pode ser nulo");
        }
        
        if (conteudo.getMidias().isEmpty()) {
            throw new PublicacaoException("TikTok requer um vídeo");
        }
        
        String videoUrl = conteudo.getMidias().get(0);
        if (!videoUrl.endsWith(".mp4") && !videoUrl.endsWith(".mov")) {
            throw new PublicacaoException("TikTok aceita apenas vídeos (.mp4, .mov)");
        }
    }

    private TikTokAPI.VideoUploadRequest criarRequestVideo(Conteudo conteudo) {
        TikTokAPI.VideoUploadRequest request = new TikTokAPI.VideoUploadRequest();
        request.videoUrl = conteudo.getMidias().get(0);
        request.caption = formatarCaption(conteudo);
        request.privacyLevel = "PUBLIC_TO_EVERYONE";
        
        // Extrai hashtags
        if (!conteudo.getHashtags().isEmpty()) {
            request.hashtags = conteudo.getHashtags().stream()
                    .map(tag -> tag.replace("#", ""))
                    .toArray(String[]::new);
        }
        
        // Cover image se disponível
        if (conteudo.getMidias().size() > 1) {
            request.coverImageUrl = conteudo.getMidias().get(1);
        }
        
        return request;
    }

    private String formatarCaption(Conteudo conteudo) {
        StringBuilder caption = new StringBuilder(conteudo.getTexto());
        
        // TikTok geralmente mantém hashtags inline na caption
        if (!conteudo.getHashtags().isEmpty()) {
            caption.append(" ");
            for (String hashtag : conteudo.getHashtags()) {
                caption.append("#").append(hashtag.replace("#", "")).append(" ");
            }
        }
        
        return caption.toString().trim();
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

