package demo.mytv.domain.video;

import com.demo.mytv.domain.video.Video;

public class VideoFixtures {
    public static Video stub(String id) {
        return Video.builder()
                .id(id)
                .title("video title")
                .description("video description")
                .thumbnail("https://test.com/thumbnail.jpg")
                .fileUrl("https://test.com/video.mp4")
                .channelId("channelId")
                .viewCount(0L)
                .build();
    }
}
