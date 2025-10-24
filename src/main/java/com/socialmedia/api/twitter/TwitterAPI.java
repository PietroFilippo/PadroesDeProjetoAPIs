package com.socialmedia.api.twitter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API simulada do Twitter (X)
 * Simula a API real com suas características específicas
 */
public class TwitterAPI {
    private static final int MAX_CARACTERES = 280;
    private boolean authenticated = false;
    private final Map<String, TwitterPost> posts = new HashMap<>();

    public void authenticate(String apiKey, String apiSecret) throws Exception {
        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            throw new Exception("Twitter API: Credenciais inválidas");
        }
        this.authenticated = true;
    }

    public TwitterPost tweet(String text, String[] mediaUrls) throws Exception {
        validarAutenticacao();
        
        if (text.length() > MAX_CARACTERES) {
            throw new Exception("Tweet excede o limite de " + MAX_CARACTERES + " caracteres");
        }

        String tweetId = "tw_" + UUID.randomUUID().toString().substring(0, 8);
        TwitterPost post = new TwitterPost();
        post.id = tweetId;
        post.text = text;
        post.mediaUrls = mediaUrls;
        post.createdAt = LocalDateTime.now();
        post.likes = 0;
        post.retweets = 0;
        post.replies = 0;
        
        posts.put(tweetId, post);
        return post;
    }

    public boolean deleteTweet(String tweetId) throws Exception {
        validarAutenticacao();
        
        if (!posts.containsKey(tweetId)) {
            throw new Exception("Tweet não encontrado: " + tweetId);
        }
        
        posts.remove(tweetId);
        return true;
    }

    public TwitterMetrics getTweetMetrics(String tweetId) throws Exception {
        validarAutenticacao();
        
        TwitterPost post = posts.get(tweetId);
        if (post == null) {
            throw new Exception("Tweet não encontrado: " + tweetId);
        }

        TwitterMetrics metrics = new TwitterMetrics();
        metrics.tweetId = tweetId;
        metrics.impressions = (long) (Math.random() * 10000);
        metrics.likes = (long) (Math.random() * 500);
        metrics.retweets = (long) (Math.random() * 100);
        metrics.replies = (long) (Math.random() * 50);
        metrics.engagementRate = ((metrics.likes + metrics.retweets + metrics.replies) * 100.0) / metrics.impressions;
        
        return metrics;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void validarAutenticacao() throws Exception {
        if (!authenticated) {
            throw new Exception("Twitter API: Não autenticado");
        }
    }

    public static class TwitterPost {
        public String id;
        public String text;
        public String[] mediaUrls;
        public LocalDateTime createdAt;
        public long likes;
        public long retweets;
        public long replies;
    }

    public static class TwitterMetrics {
        public String tweetId;
        public long impressions;
        public long likes;
        public long retweets;
        public long replies;
        public double engagementRate;
    }
}

