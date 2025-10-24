package com.socialmedia.api.instagram;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API simulada do Instagram
 * Simula a Graph API do Instagram
 */
public class InstagramAPI {
    private boolean authenticated = false;
    private final Map<String, InstagramMedia> medias = new HashMap<>();

    public void login(String accessToken) throws Exception {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new Exception("Instagram API: Access token inválido");
        }
        this.authenticated = true;
    }

    public InstagramMedia createMediaPost(MediaRequest request) throws Exception {
        validarAutenticacao();
        
        if (request.caption != null && request.caption.length() > 2200) {
            throw new Exception("Caption excede o limite de 2200 caracteres");
        }
        
        if (request.mediaType == null) {
            throw new Exception("Tipo de mídia é obrigatório");
        }

        String mediaId = "ig_" + UUID.randomUUID().toString().substring(0, 8);
        InstagramMedia media = new InstagramMedia();
        media.id = mediaId;
        media.caption = request.caption;
        media.mediaType = request.mediaType;
        media.mediaUrl = request.mediaUrl;
        media.permalink = "https://instagram.com/p/" + mediaId;
        media.timestamp = LocalDateTime.now();
        media.likeCount = 0;
        media.commentCount = 0;
        
        medias.put(mediaId, media);
        return media;
    }

    public InstagramMedia createStory(String imageUrl, String videoUrl) throws Exception {
        validarAutenticacao();
        
        String storyId = "ig_story_" + UUID.randomUUID().toString().substring(0, 8);
        InstagramMedia story = new InstagramMedia();
        story.id = storyId;
        story.mediaType = videoUrl != null ? "VIDEO" : "IMAGE";
        story.mediaUrl = videoUrl != null ? videoUrl : imageUrl;
        story.timestamp = LocalDateTime.now();
        story.isStory = true;
        
        medias.put(storyId, story);
        return story;
    }

    public boolean deleteMedia(String mediaId) throws Exception {
        validarAutenticacao();
        
        if (!medias.containsKey(mediaId)) {
            throw new Exception("Mídia não encontrada: " + mediaId);
        }
        
        medias.remove(mediaId);
        return true;
    }

    public InstagramInsights getMediaInsights(String mediaId) throws Exception {
        validarAutenticacao();
        
        InstagramMedia media = medias.get(mediaId);
        if (media == null) {
            throw new Exception("Mídia não encontrada: " + mediaId);
        }

        InstagramInsights insights = new InstagramInsights();
        insights.mediaId = mediaId;
        insights.reach = (long) (Math.random() * 15000);
        insights.impressions = (long) (insights.reach * 1.5);
        insights.likes = (long) (Math.random() * 800);
        insights.comments = (long) (Math.random() * 100);
        insights.shares = (long) (Math.random() * 50);
        insights.saves = (long) (Math.random() * 200);
        insights.engagement = ((insights.likes + insights.comments + insights.shares + insights.saves) * 100.0) / insights.reach;
        
        return insights;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void validarAutenticacao() throws Exception {
        if (!authenticated) {
            throw new Exception("Instagram API: Não autenticado");
        }
    }

    public static class MediaRequest {
        public String caption;
        public String mediaType; // IMAGE, VIDEO, CAROUSEL_ALBUM
        public String mediaUrl;
        public String[] childMediaUrls; // Para carrossel
    }

    public static class InstagramMedia {
        public String id;
        public String caption;
        public String mediaType;
        public String mediaUrl;
        public String permalink;
        public LocalDateTime timestamp;
        public long likeCount;
        public long commentCount;
        public boolean isStory = false;
    }

    public static class InstagramInsights {
        public String mediaId;
        public long reach;
        public long impressions;
        public long likes;
        public long comments;
        public long shares;
        public long saves;
        public double engagement;
    }
}

