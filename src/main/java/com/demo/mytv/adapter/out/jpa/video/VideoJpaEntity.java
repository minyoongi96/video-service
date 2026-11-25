package com.demo.mytv.adapter.out.jpa.video;

import com.demo.mytv.domain.video.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class VideoJpaEntity {

    @Id
    private String id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String fileUrl;
    private String channelId;
    private Long viewCount = 0L;
    private LocalDateTime publishedAt;

    public Video toDomain() {
        return Video.builder()
                .id(this.getId())
                .title(this.getTitle())
                .description(this.getDescription())
                .thumbnail(this.getThumbnailUrl())
                .fileUrl(this.getFileUrl())
                .channelId(this.getChannelId())
                .publishedAt(this.getPublishedAt())
                .build();
    }

    public static VideoJpaEntity from(Video video) {
        return new VideoJpaEntity(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getThumbnail(),
                video.getFileUrl(),
                video.getChannelId(),
                video.getViewCount(),
                video.getPublishedAt()
        );
    }

    public void updateViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
}
