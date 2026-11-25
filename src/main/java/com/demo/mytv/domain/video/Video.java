package com.demo.mytv.domain.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Video implements Serializable {

    private String id;
    private String title;
    private String description;
    private String thumbnail;
    private String fileUrl;
    private String channelId;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime publishedAt;

    public void bindCount(Long viewCount, Long likeCount) {
        this.viewCount = viewCount;
        this.likeCount = likeCount;
    }
}
