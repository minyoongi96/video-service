package com.demo.mytv.adapter.out.redis.channel;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChannelRedisRepository extends CrudRepository<ChannelRedisHash, String> {
    List<ChannelRedisHash> findByContentOwnerId(String contentOwnerId);
}
