package com.demo.mytv.adapter.out;

import com.demo.mytv.adapter.out.jpa.channel.ChannelJpaRepository;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisHash;
import com.demo.mytv.adapter.out.redis.channel.ChannelRedisRepository;
import com.demo.mytv.config.AnotherTestRedisConfig;
import com.demo.mytv.config.TestRedisConfig;
import com.demo.mytv.domain.channel.Channel;
import com.demo.mytv.domain.channel.ChannelSnippet;
import com.demo.mytv.domain.channel.ChannelStatistics;
import demo.mytv.domain.channel.ChannelFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(
        initializers = AnotherTestRedisConfig.RedisInitializer.class
)
@Transactional
@Sql("/com/demo/mytv/adapter/out/ChannelPersistenceAdapterIntTest.sql")
public class ChannelPersistenceAdapterIntTest {
    @Autowired
    private ChannelPersistenceAdapter sut;

    @MockitoSpyBean
    private ChannelJpaRepository channelJpaRepository;

    @MockitoSpyBean
    private ChannelRedisRepository channelRedisRepository;

    @BeforeEach
    void setUp() {
        channelRedisRepository.deleteAll();
    }

    @Nested
    @DisplayName("loadChannel")
    class LoadChannelTest {
        @Test
        @DisplayName("Redis cache에서 찾을 수 없으면 Jpa 에서 찾음")
        void cacheMissAndJpaHitTest() {
            // when
            for (int i = 0; i < 3; i++) {
                sut.loadChannel("channel1");
            }

            // then
            verify(channelJpaRepository, times(1)).findById("channel1");
            verify(channelRedisRepository, times(3)).findById("channel1");
        }

        @Test
        @DisplayName("Redis Cache에서 찾을 수 있으면 Jpa 호출하지 않음")
        void cacheHitTest() {
            var channel = Channel.builder().id("channel1").contentOwnerId("user")
                    .statistics(ChannelStatistics.getDefaultStatistics())
                    .snippet(ChannelSnippet.builder().title("channel").description("desc").thumbnailUrl("thumb").build())
                    .build();
            channelRedisRepository.save(ChannelRedisHash.from(channel));

            for (int i = 0; i < 3; i++) {
                sut.loadChannel("channel1");
            }

            verify(channelJpaRepository, never()).findById("channel1");
        }

        @Nested
        @DisplayName("save Channel")
        class SaveChannel {
            @Test
            void testCreateAndRedisDelete() {
                var channel = ChannelFixtures.stub("channelTest");
                sut.saveChannel(channel);

                var savedChannel = channelRedisRepository.findById("channelTest");

                then(savedChannel).isPresent();
                then(savedChannel.get().getId()).isEqualTo("channelTest");
            }

            @Test
            void testUpdateAndLoadChannel() {
                // given
                var channel = sut.loadChannel("channel1").get();
                System.out.println(channel);
                var updatedChannel = Channel.builder()
                        .id(channel.getId())
                        .snippet(ChannelSnippet.builder().title("new title").description("new description").thumbnailUrl("https://test.com/newimage.jpg").build())
                        .statistics(channel.getStatistics())
                        .contentOwnerId(channel.getContentOwnerId())
                        .build();

                // When
                sut.saveChannel(updatedChannel);

                // Then
                var result = sut.loadChannel("channel1");
                then(result)
                        .isPresent();
                then(result.get())
                        .hasFieldOrPropertyWithValue("id", channel.getId())
                        .hasFieldOrPropertyWithValue("snippet.title", "new title")
                        .hasFieldOrPropertyWithValue("snippet.description", "new description")
                        .hasFieldOrPropertyWithValue("snippet.thumbnailUrl", "https://test.com/newimage.jpg");
                System.out.println(result.get());
            }
        }
    }
}
