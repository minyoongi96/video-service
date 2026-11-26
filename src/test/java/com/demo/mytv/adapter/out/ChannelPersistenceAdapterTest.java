package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaEntity;
import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaEntityFixtures;
import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaRepository;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisHash;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisRepository;
import demo.mytv.domain.channel.ChannelFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelPersistenceAdapterTest {
    private ChannelPersistenceAdapter sut;

    private final ChannelJpaRepository channelJpaRepository = mock(ChannelJpaRepository.class);
    private final ChannelRedisRepository channelRedisRepository = mock(ChannelRedisRepository.class);

    @BeforeEach
    void setUp() {
        sut = new ChannelPersistenceAdapter(channelJpaRepository, channelRedisRepository);
    }

    @Nested
    @DisplayName("saveChannel")
    class SaveChannelTest {
        @Test
        @DisplayName("Channel 을 Jpa Repository 에 저장")
        void saveChannel() {
            // given
            var channel = ChannelFixtures.stub("channelId");

            // when
            sut.saveChannel(channel);

            // then
            var argumentCaptor = ArgumentCaptor.forClass(ChannelJpaEntity.class);
            verify(channelJpaRepository).save(argumentCaptor.capture());
            then(argumentCaptor.getValue())
                    .hasFieldOrPropertyWithValue("id", channel.getId())
                    .hasFieldOrPropertyWithValue("snippet.title", channel.getSnippet().getTitle());
            verify(channelRedisRepository).deleteById(channel.getId());
        }
    }

    @Nested
    @DisplayName("loadChannel")
    class LoadChannelTest {
        @Test
        @DisplayName("Redis 에서 Channel 을 찾을 수 있으면 RedisHash 에서 Channel 반환")
        void existRedis_returnRedisRepository() {
            // given
            var id = "channelId";
            given(channelRedisRepository.findById(any()))
                    .willReturn(Optional.of(ChannelRedisHash.from(ChannelFixtures.stub(id))));
            // when
            var result = sut.loadChannel(id);

            // then
            then(result)
                    .isPresent()
                    .hasValueSatisfying(channel -> then(channel)
                            .hasFieldOrPropertyWithValue("id", id));
        }
    }

    @Test
    @DisplayName("Redis 에서 Channel 을 찾을 수 없으면 Jpa 에서 Channel 반환")
    void notExistRedis_returnJpaRepository() {
        // given
        var id = "channelId";
        given(channelRedisRepository.findById(any()))
                .willReturn(Optional.empty());
        given(channelJpaRepository.findById(any()))
                .willReturn(Optional.of(ChannelJpaEntityFixtures.stub(id)));

        // when
        var result = sut.loadChannel(id);

        // then
        then(result)
                .isPresent()
                .hasValueSatisfying(channel -> then(channel)
                        .hasFieldOrPropertyWithValue("id", id));
    }

    @Test
    @DisplayName("Redis, Jpa 에서 Channel 을 찾을 수 없으면 empty Optional 반환")
    void notExist_returnEmpty() {
        // given
        var id = "channelId";
        given(channelRedisRepository.findById(any()))
                .willReturn(Optional.empty());
        given(channelJpaRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        var result = sut.loadChannel(id);

        // then
        then(result)
                .isNotPresent();
    }
}
