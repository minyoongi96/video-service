package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.video.VideoJpaEntity;
import com.demo.mytv.adapter.out.jpa.video.VideoJpaRepository;
import com.demo.mytv.application.port.out.LoadVideoPort;
import com.demo.mytv.application.port.out.SaveVideoPort;
import com.demo.mytv.common.RedisKeyGenerator;
import com.demo.mytv.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.demo.mytv.common.CacheNames.VIDEO;
import static com.demo.mytv.common.CacheNames.VIDEO_LIST;
import static com.demo.mytv.common.RedisKeyGenerator.getVideoViewCountKey;

@Component
@RequiredArgsConstructor
public class VideoPersistenceAdapter implements LoadVideoPort, SaveVideoPort {

    private final VideoJpaRepository videoJpaRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    @Cacheable(cacheNames = VIDEO, key = "#videoId")
    public Video loadVideo(String videoId) {
        return videoJpaRepository.findById(videoId)
                .map(VideoJpaEntity::toDomain)
                .orElseThrow();
    }

    @Override
    @Cacheable(cacheNames = VIDEO_LIST, key = "#channelId")
    public List<Video> loadVideoByChannel(String channelId) {
        return videoJpaRepository.findByChannelId(channelId).stream()
                .map(VideoJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Long getViewCount(String videoId) {
        var videoViewCountKey = getVideoViewCountKey(videoId);
        var viewCount = redisTemplate.opsForValue().get(videoViewCountKey);
        return viewCount == null ? 0 : viewCount;
    }

    @Override
    public List<String> getAllVideoIdsWithViewCount() {
        var members = stringRedisTemplate.opsForSet().members(RedisKeyGenerator.getVideoViewCountSetKey());
        if (members == null) {
            return Collections.emptyList();
        }
        return members.stream().toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = VIDEO_LIST, key = "#video.channelId"),
            @CacheEvict(cacheNames = VIDEO, key = "#video.id")
    })
    public void saveVideo(Video video) {
        videoJpaRepository.save(VideoJpaEntity.from(video));
    }

    @Override
    public void incrementViewCount(String videoId) {
        var videoViewCountKey = getVideoViewCountKey(videoId);
        redisTemplate.opsForValue().increment(videoViewCountKey);
    }

    @Override
    public void syncViewCount(String videoId) {
        // DB와 캐시 간 viewCount 동기화
        videoJpaRepository.findById(videoId)
                .ifPresent(videoJpaEntity -> {
                    // video:view-count:videoId
                    var viewCount = redisTemplate.opsForValue().get(RedisKeyGenerator.getVideoViewCountKey(videoId));
                    videoJpaEntity.updateViewCount(viewCount);
                    videoJpaRepository.save(videoJpaEntity);

                    redisTemplate.opsForSet().remove(RedisKeyGenerator.getVideoViewCountSetKey(), videoId);
                });
    }
}
