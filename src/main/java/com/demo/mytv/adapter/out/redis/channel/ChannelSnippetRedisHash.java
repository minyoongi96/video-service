package com.demo.mytv.adapter.out.redis.channel;

import com.demo.mytv.domain.channel.ChannelSnippet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash("channelSnippet")
@AllArgsConstructor
@Getter
public class ChannelSnippetRedisHash {
    private String title;
    private String description;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;

    public static ChannelSnippetRedisHash from(ChannelSnippet snippet) {
        return new ChannelSnippetRedisHash(
                snippet.getTitle(), snippet.getDescription(), snippet.getThumbnailUrl(), snippet.getPublishedAt()
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
