package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaEntity;
import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaRepository;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisHash;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisRepository;
import com.demo.mytv.application.port.out.LoadChannelPort;
import com.demo.mytv.application.port.out.SaveChannelPort;
import com.demo.mytv.domain.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChannelPersistenceAdapter implements LoadChannelPort, SaveChannelPort {

    private final ChannelJpaRepository channelJpaRepository;
    private final ChannelRedisRepository channelRedisRepository;

    public ChannelPersistenceAdapter(ChannelJpaRepository channelJpaRepository, ChannelRedisRepository channelRedisRepository) {
        this.channelJpaRepository = channelJpaRepository;
        this.channelRedisRepository = channelRedisRepository;
    }

    @Override
    public void saveChannel(Channel channel) {
        // 캐시 삭제 -> 다시 저장
        channelRedisRepository.deleteById(channel.getId());
        channelRedisRepository.save(ChannelRedisHash.from(channel));
        // DB 저장
        channelJpaRepository.save(ChannelJpaEntity.from(channel));

    }

    @Override
    public Optional<Channel> loadChannel(String id) {
        return channelRedisRepository.findById(id)
                // redis cache hit
                .map(ChannelRedisHash::toDomain)
                .or(() -> {
                    // redis cache miss
                    var optionalEntity = channelJpaRepository.findById(id);
                    optionalEntity.ifPresent(jpaEntity -> channelRedisRepository.save(ChannelRedisHash.from(jpaEntity.toDomain())));

                    return optionalEntity.map(ChannelJpaEntity::toDomain);
                });
    }
}
