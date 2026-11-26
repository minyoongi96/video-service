package com.demo.mytv.application.port.in;

public interface VideoLikePort {
    Long addVideoLike(String videoId, String userId);
    Long removeVideoLike(String videoId, String userId);
    Boolean isVideoLikeMember(String videoId, String userId);
    Long getVideoLikeCount(String videoId);
}
