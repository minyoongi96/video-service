package com.demo.mytv.application.port.out;

import com.demo.mytv.domain.video.Video;

public interface SaveVideoPort {
    void saveVideo(Video video);
    void incrementViewCount(String videoId);
    void syncViewCount(String videoId);
}
