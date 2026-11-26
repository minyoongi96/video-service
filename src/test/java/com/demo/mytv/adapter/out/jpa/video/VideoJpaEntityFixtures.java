package com.demo.mytv.adapter.out.jpa.video;

import java.time.LocalDateTime;

public class VideoJpaEntityFixtures {
    public static VideoJpaEntity stub(String id) {
        return new VideoJpaEntity(
                id,
                "title",
                "description",
                "https://test.com/thumbnail.jpg",
                "https://test.com/video.mp4",
                "channelId",
                10L,
                LocalDateTime.now()
        );
    }
}
