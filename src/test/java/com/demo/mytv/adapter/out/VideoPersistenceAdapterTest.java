package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.video.VideoJpaEntity;
import com.demo.mytv.adapter.out.jpa.video.VideoJpaEntityFixtures;
import com.demo.mytv.adapter.out.jpa.video.VideoJpaRepository;
import demo.mytv.domain.video.VideoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VideoPersistenceAdapterTest {

    private VideoPersistenceAdapter sut;

    private final VideoJpaRepository videoJpaRepository = mock(VideoJpaRepository.class);
    private final RedisTemplate<String, Long> redisTemplate = mock(RedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class, Mockito.RETURNS_DEEP_STUBS);
    private final ValueOperations<String, Long> valueOperations = mock(ValueOperations.class);

    @BeforeEach
    void setUp() {
        sut = new VideoPersistenceAdapter(videoJpaRepository, redisTemplate, stringRedisTemplate);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    class LoadVideo {
        @Test
        void existVideo_then_returnVideo() {
            // given
            var videoJpaEntity = VideoJpaEntityFixtures.stub("video1");
            given(videoJpaRepository.findById(videoJpaEntity.getId()))
                    .willReturn(Optional.of(videoJpaEntity));

            // when
            var result = sut.loadVideo("video1");

            // then
            then(result)
                    .extracting("id")
                    .isEqualTo("video1");
        }

        @Test
        void notExistVideo_then_throwException() {
            // given
            given(videoJpaRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when
            var assertion = thenThrownBy(() -> sut.loadVideo("video1"));

            // then
            assertion.isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    void testLoadVideoByChannel() {
        // given
        var videoJpaEntity1 = VideoJpaEntityFixtures.stub("video1");
        var videoJpaEntity2 = VideoJpaEntityFixtures.stub("video2");
        given(videoJpaRepository.findByChannelId(any()))
                .willReturn(List.of(videoJpaEntity1, videoJpaEntity2));

        // when
        var result = sut.loadVideoByChannel("channelId");

        // then
        then(result)
                .hasSize(2)
                .extracting("id", "title", "description")
                .containsExactly(tuple("video1", "title", "description"), tuple("video2", "title", "description"));
    }

    @Test
    void testCreateVideo() {
        // given
        var video = VideoFixtures.stub("videoId");
        ArgumentCaptor<VideoJpaEntity> captor = ArgumentCaptor.forClass(VideoJpaEntity.class);

        // when
        sut.saveVideo(video);

        // then
        verify(videoJpaRepository).save(captor.capture());
        then(captor.getValue())
                .hasFieldOrPropertyWithValue("id", video.getId())
                .hasFieldOrPropertyWithValue("title", video.getTitle())
                .hasFieldOrPropertyWithValue("description", video.getDescription())
                .hasFieldOrPropertyWithValue("thumbnailUrl", video.getThumbnail())
                .hasFieldOrPropertyWithValue("fileUrl", video.getFileUrl())
                .hasFieldOrPropertyWithValue("channelId", video.getChannelId());
    }

    @Test
    void testGetViewCount() {
        given(valueOperations.increment(any()))
                .willReturn(5L);

        sut.incrementViewCount("video1");

        verify(valueOperations).increment("video:view-count:video1");
    }

    @Test
    void testGetViewCountWhenNullValueThenZero() {
        given(redisTemplate.opsForValue().get(any()))
                .willReturn(null);

        var result = sut.getViewCount("video1");

        then(result)
                .isEqualTo(0L);
    }

    @Test
    void testGetAllVideoIdsWithViewCount() {
        given(stringRedisTemplate.opsForSet().members(any()))
                .willReturn(Set.of("videoId1", "videoId2", "videoId3"));

        var result = sut.getAllVideoIdsWithViewCount();

        then(result)
                .contains("videoId1", "videoId2", "videoId3");
    }

    @Test
    void testSyncViewCount() {
        given(redisTemplate.opsForValue().get(any()))
                .willReturn(20L);
        given(videoJpaRepository.findById(any()))
                .willReturn(Optional.of(new VideoJpaEntity()));

        sut.syncViewCount("videoId");

        var captor = ArgumentCaptor.forClass(VideoJpaEntity.class);
        verify(videoJpaRepository).save(captor.capture());
        then(captor.getValue().getViewCount())
                .isEqualTo(20L);
    }
}
