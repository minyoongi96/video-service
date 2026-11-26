package com.demo.mytv.application;

import com.demo.mytv.adapter.in.api.dto.VideoRequest;
import com.demo.mytv.application.port.out.VideoLikePort;
import com.demo.mytv.application.port.in.VideoUseCase;
import com.demo.mytv.application.port.out.LoadChannelPort;
import com.demo.mytv.application.port.out.LoadVideoPort;
import com.demo.mytv.application.port.out.SaveChannelPort;
import com.demo.mytv.application.port.out.SaveVideoPort;
import com.demo.mytv.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService implements VideoUseCase {
    private final LoadVideoPort loadVideoPort;
    private final SaveVideoPort saveVideoPort;
    private final LoadChannelPort loadChannelPort;
    private final SaveChannelPort saveChannelPort;
    private final VideoLikePort videoLikePort;

    @Override
    public Video getVideo(String videoId) {
        var video = loadVideoPort.loadVideo(videoId);
        var viewCount = loadVideoPort.getViewCount(videoId);
        var likeCount = videoLikePort.getVideoLikeCount(videoId);
        video.bindCount(viewCount, likeCount);

        return video;
    }

    @Override
    public List<Video> getVideoList(String channelId) {
        return loadVideoPort.loadVideoByChannel(channelId).stream()
                .map(video -> {
                    var viewCount = loadVideoPort.getViewCount(video.getId());
                    var likeCount = videoLikePort.getVideoLikeCount(video.getId());
                    video.bindCount(viewCount, likeCount);
                    return video;
                })
                .toList();
    }

    @Override
    public Video createVideo(VideoRequest videoRequest) {
        var video = Video.builder()
                .id(UUID.randomUUID().toString())
                .title(videoRequest.getTitle())
                .description(videoRequest.getDescription())
                .thumbnail(videoRequest.getThumbnailUrl())
                .channelId(videoRequest.getChannelId())
                .publishedAt(LocalDateTime.now())
                .build();

        saveVideoPort.saveVideo(video);

        var channel = loadChannelPort.loadChannel(videoRequest.getChannelId()).get();
        channel.getStatistics().upDateVideoCount(channel.getStatistics().getVideoCount() + 1);

        saveChannelPort.saveChannel(channel);

        // messagePort.sendNewVideoMessage

        return video;
    }

    @Override
    public void increaseViewCount(String videoId) {
        saveVideoPort.incrementViewCount(videoId);
    }
}
