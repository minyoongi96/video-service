package com.demo.mytv.application.port.in;

import com.demo.mytv.adapter.in.api.dto.VideoRequest;
import com.demo.mytv.domain.video.Video;

import java.util.List;

public interface VideoUseCase {
    Video getVideo(String videoId);

    List<Video> getVideoList(String channelId);

    Video createVideo(VideoRequest videoRequest);

    void increaseViewCount(String videoId);
}
