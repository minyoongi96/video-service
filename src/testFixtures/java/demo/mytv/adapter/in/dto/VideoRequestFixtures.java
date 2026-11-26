package demo.mytv.adapter.in.dto;

import com.demo.mytv.adapter.in.api.dto.VideoRequest;

public class VideoRequestFixtures {

    public static VideoRequest stub() {
        return new VideoRequest(
                "title",
                "description",
                "https://test.com/thumbnail.jpg",
                "channelId"
        );
    }
}
