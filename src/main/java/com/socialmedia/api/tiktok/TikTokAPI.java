package com.socialmedia.api.tiktok;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API simulada do TikTok
 * Simula a TikTok for Developers API
 */
public class TikTokAPI {
    private boolean authenticated = false;
    private final Map<String, TikTokVideo> videos = new HashMap<>();

    public void authenticate(String appId, String appSecret, String code) throws Exception {
        if (appId == null || appSecret == null) {
            throw new Exception("TikTok API: Credenciais inválidas");
        }
        this.authenticated = true;
    }

    public TikTokVideo uploadVideo(VideoUploadRequest request) throws Exception {
        validarAutenticacao();
        
        if (request.videoUrl == null || request.videoUrl.isEmpty()) {
            throw new Exception("URL do vídeo é obrigatória");
        }
        
        if (request.caption != null && request.caption.length() > 2200) {
            throw new Exception("Caption excede o limite de 2200 caracteres");
        }

        String videoId = "tk_" + UUID.randomUUID().toString().substring(0, 8);
        TikTokVideo video = new TikTokVideo();
        video.videoId = videoId;
        video.caption = request.caption;
        video.videoUrl = request.videoUrl;
        video.coverImageUrl = request.coverImageUrl;
        video.hashtags = request.hashtags;
        video.privacyLevel = request.privacyLevel != null ? request.privacyLevel : "PUBLIC_TO_EVERYONE";
        video.createTime = LocalDateTime.now();
        video.shareUrl = "https://tiktok.com/@user/video/" + videoId;
        
        videos.put(videoId, video);
        return video;
    }

    public boolean deleteVideo(String videoId) throws Exception {
        validarAutenticacao();
        
        if (!videos.containsKey(videoId)) {
            throw new Exception("Vídeo não encontrado: " + videoId);
        }
        
        videos.remove(videoId);
        return true;
    }

    public TikTokVideoInfo getVideoInfo(String videoId) throws Exception {
        validarAutenticacao();
        
        TikTokVideo video = videos.get(videoId);
        if (video == null) {
            throw new Exception("Vídeo não encontrado: " + videoId);
        }

        TikTokVideoInfo info = new TikTokVideoInfo();
        info.videoId = videoId;
        info.viewCount = (long) (Math.random() * 100000);
        info.likeCount = (long) (Math.random() * 5000);
        info.commentCount = (long) (Math.random() * 500);
        info.shareCount = (long) (Math.random() * 1000);
        info.playDuration = (long) (15 + Math.random() * 45); // 15-60 segundos
        info.engagementRate = ((info.likeCount + info.commentCount + info.shareCount) * 100.0) / info.viewCount;
        
        return info;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private void validarAutenticacao() throws Exception {
        if (!authenticated) {
            throw new Exception("TikTok API: Não autenticado");
        }
    }

    public static class VideoUploadRequest {
        public String videoUrl;
        public String caption;
        public String coverImageUrl;
        public String[] hashtags;
        public String privacyLevel; // PUBLIC_TO_EVERYONE, MUTUAL_FOLLOW_FRIENDS, SELF_ONLY
        public boolean disableComment = false;
        public boolean disableDuet = false;
        public boolean disableStitch = false;
    }

    public static class TikTokVideo {
        public String videoId;
        public String caption;
        public String videoUrl;
        public String coverImageUrl;
        public String[] hashtags;
        public String privacyLevel;
        public String shareUrl;
        public LocalDateTime createTime;
    }

    public static class TikTokVideoInfo {
        public String videoId;
        public long viewCount;
        public long likeCount;
        public long commentCount;
        public long shareCount;
        public long playDuration;
        public double engagementRate;
    }
}

