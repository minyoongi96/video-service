package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.video.VideoJpaRepository;
import com.demo.mytv.config.TestRedisConfig;
import com.demo.mytv.domain.video.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.demo.mytv.common.CacheNames.VIDEO;
import static com.demo.mytv.common.CacheNames.VIDEO_LIST;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TestRedisConfig.class)
@Transactional
@Sql("/com/demo/mytv/adapter/out/VideoPersistenceAdapterIntTest.sql")
public class VideoPersistenceAdapterIntTest {

    @Autowired
    private VideoPersistenceAdapter sut;

    @MockitoSpyBean
    private VideoJpaRepository videoJpaRepository;
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;
    @Autowired
    private RedisCacheManager redisCacheManager;

    @BeforeEach
    void setUp() {
        redisCacheManager.getCache(VIDEO).clear();
        redisCacheManager.getCache(VIDEO_LIST).clear();
    }

    @Test
    void loadVideo() {
        var videoId = "video1";
        for (int i = 0; i < 3; i++) {
            sut.loadVideo(videoId);
        }

        verify(videoJpaRepository, times(1)).findById(videoId);
    }

    @Test
    void loadVideoByChannel() {
        for (int i = 0; i < 3; i++) {
            sut.loadVideoByChannel("channelId");
        }

        verify(videoJpaRepository, times(1)).findByChannelId("channelId");
    }

    @Test
    void saveVideoThenEvictVideoListCache() {
        // given
        sut.loadVideoByChannel("channel1");

        var video = Video.builder()
                .id("video3").title("video3").description("video3").thumbnail("https://example.com/image.jpg")
                .channelId("channel1").publishedAt(LocalDateTime.now())
                .build();

        // When
        sut.saveVideo(video);

        // Then
        then(redisCacheManager.getCache(VIDEO_LIST).get("channel1"))
                .isNull();
    }

    @Test
    void saveVideoThenEvictVideoCache() {
        // Given
        var video = sut.loadVideo("video1");
        then(redisCacheManager.getCache(VIDEO).get("video1")).isNotNull();

        var updatedVideo = Video.builder()
                .id(video.getId()).title(video.getTitle()).description(video.getDescription()).thumbnail(video.getThumbnail())
                .viewCount(200L)
                .channelId(video.getChannelId()).publishedAt(video.getPublishedAt())
                .build();

        // When
        sut.saveVideo(updatedVideo);

        // Then
        then(redisCacheManager.getCache("video").get("video1"))
                .isNull();
    }
}
