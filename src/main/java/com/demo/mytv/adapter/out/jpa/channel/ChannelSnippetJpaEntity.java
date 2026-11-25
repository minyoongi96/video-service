package com.demo.mytv.adapter.out.jpa.channel;

import com.demo.mytv.domain.channel.ChannelSnippet;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChannelSnippetJpaEntity {
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;

    public static ChannelSnippetJpaEntity from(ChannelSnippet channelSnippet) {
        return new ChannelSnippetJpaEntity(
                channelSnippet.getTitle(), channelSnippet.getDescription(), channelSnippet.getThumbnailUrl(), channelSnippet.getPublishedAt()
        );
    }

    public ChannelSnippet toDomain() {
        return ChannelSnippet.builder()
                .title(this.getTitle())
                .description(this.getDescription())
                .thumbnailUrl(this.getThumbnailUrl())
                .publishedAt(this.getPublishedAt())
                .build();
    }
}
