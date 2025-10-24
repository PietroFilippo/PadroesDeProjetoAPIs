package com.socialmedia.api.linkedin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API simulada do LinkedIn
 * Simula a API REST do LinkedIn
 */
public class LinkedInAPI {
    private boolean authenticated = false;
    private final Map<String, LinkedInShare> shares = new HashMap<>();

    public void authorize(String clientId, String clientSecret, String redirectUri) throws Exception {
        if (clientId == null || clientSecret == null) {
            throw new Exception("LinkedIn API: Credenciais OAuth inválidas");
        }
        this.authenticated = true;
    }

    public LinkedInShare createShare(ShareRequest request) throws Exception {
        validarAutenticacao();
        
        if (request.commentary != null && request.commentary.length() > 3000) {
            throw new Exception("Comentário excede o limite de 3000 caracteres");
        }

        String shareId = "urn:li:share:" + UUID.randomUUID().toString().substring(0, 8);
        LinkedInShare share = new LinkedInShare();
        share.shareUrn = shareId;
        share.commentary = request.commentary;
        share.visibility = request.visibility != null ? request.visibility : "PUBLIC";
        share.mediaCategory = request.mediaCategory;
        share.mediaUrl = request.mediaUrl;
        share.created = LocalDateTime.now();
        share.lifecycleState = "PUBLISHED";
        
        shares.put(shareId, share);
        return share;
    }

    public LinkedInShare createArticle(ArticleRequest request) throws Exception {
        validarAutenticacao();
        
        if (request.title == null || request.title.isEmpty()) {
            throw new Exception("Título do artigo é obrigatório");
        }

        String articleId = "urn:li:article:" + UUID.randomUUID().toString().substring(0, 8);
        LinkedInShare article = new LinkedInShare();
        article.shareUrn = articleId;
        article.commentary = request.title;
        article.articleTitle = request.title;
        article.articleContent = request.content;
        article.visibility = "PUBLIC";
        article.created = LocalDateTime.now();
        article.lifecycleState = "PUBLISHED";
        
        shares.put(articleId, article);
        return article;
    }

    public boolean deleteShare(String shareUrn) throws Exception {
        validarAutenticacao();
        
        if (!shares.containsKey(shareUrn)) {
            throw new Exception("Share não encontrado: " + shareUrn);
        }
        
        shares.remove(shareUrn);
        return true;
    }

    public LinkedInAnalytics getShareStatistics(String shareUrn) throws Exception {
        validarAutenticacao();
        
        LinkedInShare share = shares.get(shareUrn);
        if (share == null) {
            throw new Exception("Share não encontrado: " + shareUrn);
        }

        LinkedInAnalytics analytics = new LinkedInAnalytics();
        analytics.shareUrn = shareUrn;
        analytics.impressionCount = (long) (Math.random() * 5000);
        analytics.likeCount = (long) (Math.random() * 300);
        analytics.commentCount = (long) (Math.random() * 50);
        analytics.shareCount = (long) (Math.random() * 80);
        analytics.clickCount = (long) (Math.random() * 400);
        analytics.engagementRate = ((analytics.likeCount + analytics.commentCount + analytics.shareCount) * 100.0) / analytics.impressionCount;
        
        return analytics;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void validarAutenticacao() throws Exception {
        if (!authenticated) {
            throw new Exception("LinkedIn API: Não autenticado");
        }
    }

    public static class ShareRequest {
        public String commentary;
        public String visibility; // PUBLIC, CONNECTIONS, LOGGED_IN
        public String mediaCategory; // NONE, IMAGE, VIDEO, ARTICLE
        public String mediaUrl;
    }

    public static class ArticleRequest {
        public String title;
        public String content;
        public String thumbnailUrl;
    }

    public static class LinkedInShare {
        public String shareUrn;
        public String commentary;
        public String visibility;
        public String mediaCategory;
        public String mediaUrl;
        public String articleTitle;
        public String articleContent;
        public LocalDateTime created;
        public String lifecycleState;
    }

    public static class LinkedInAnalytics {
        public String shareUrn;
        public long impressionCount;
        public long likeCount;
        public long commentCount;
        public long shareCount;
        public long clickCount;
        public double engagementRate;
    }
}

