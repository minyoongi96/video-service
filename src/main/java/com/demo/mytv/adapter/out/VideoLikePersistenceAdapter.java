package com.demo.mytv.adapter.out;

import com.demo.mytv.application.port.out.VideoLikePort;
import com.demo.mytv.common.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoLikePersistenceAdapter implements VideoLikePort {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Long addVideoLike(String videoId, String userId) {
        var setOps = stringRedisTemplate.opsForSet();

        return setOps.add(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Long removeVideoLike(String videoId, String userId) {
        var setOps = stringRedisTemplate.opsForSet();

        return setOps.remove(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Boolean isVideoLikeMember(String videoId, String userId) {
        var setOps = stringRedisTemplate.opsForSet();

        return setOps.isMember(RedisKeyGenerator.getVideoLikeKey(videoId), userId);
    }

    @Override
    public Long getVideoLikeCount(String videoId) {
        var setOps = stringRedisTemplate.opsForSet();
        return setOps.size(RedisKeyGenerator.getVideoLikeKey(videoId));
    }
}
