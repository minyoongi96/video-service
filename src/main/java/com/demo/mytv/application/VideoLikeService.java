package com.demo.mytv.application;

import com.demo.mytv.application.port.in.VideoLikeUseCase;
import com.demo.mytv.application.port.out.VideoLikePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoLikeService implements VideoLikeUseCase {
    private final VideoLikePort videoLikePort;

    @Override
    public Long likeVideo(String videoId, String userId) {
        return videoLikePort.addVideoLike(videoId, userId);
    }

    @Override
    public Long unlikeVideo(String videoId, String userId) {
        return videoLikePort.removeVideoLike(videoId, userId);
    }

    @Override
    public Boolean isLikedVideo(String videoId, String userId) {
        return videoLikePort.isVideoLikeMember(videoId, userId);
    }

    @Override
    public Long getVideoLikeCount(String videoId) {
        return videoLikePort.getVideoLikeCount(videoId);
    }
}
