package com.demo.mytv.application;

import com.demo.mytv.application.port.out.*;
import demo.mytv.adapter.in.dto.VideoRequestFixtures;
import demo.mytv.domain.channel.ChannelFixtures;
import demo.mytv.domain.video.VideoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class VideoServiceTest {

    private VideoService sut;

    private final LoadVideoPort loadVideoPort = mock(LoadVideoPort.class);
    private final SaveVideoPort saveVideoPort = mock(SaveVideoPort.class);
    private final LoadChannelPort loadChannelPort = mock(LoadChannelPort.class);
    private final SaveChannelPort saveChannelPort = mock(SaveChannelPort.class);
    private final VideoLikePort videoLikePort = mock(VideoLikePort.class);

    @BeforeEach
    void setUp() {
        sut = new VideoService(loadVideoPort, saveVideoPort, loadChannelPort, saveChannelPort, videoLikePort);
    }

    @Test
    @DisplayName("videoId로 조회시 Video 반환")
    void testLoadVideo() {
        // given
        var videoId = "videoId";

        given(loadVideoPort.loadVideo(any())).willReturn(VideoFixtures.stub(videoId));
        given(loadVideoPort.getViewCount(any())).willReturn(150L);
        given(videoLikePort.getVideoLikeCount(any())).willReturn(1000L);

        // when
        var result = sut.getVideo(videoId);

        // then
        then(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", videoId)
                .hasFieldOrPropertyWithValue("viewCount", 150L)
                .hasFieldOrPropertyWithValue("likeCount", 1000L);
    }

    @Test
    @DisplayName("channelId로 조회 시 Video 목록 반환")
    void testListVideosByChannelId() {
        // given
        var channelId = "channelId";
        var list = LongStream.range(1L, 4L)
                .mapToObj(i -> VideoFixtures.stub("videoId" + i))
                .toList();

        given(loadVideoPort.loadVideoByChannel(any())).willReturn(list);
        given(loadVideoPort.getViewCount(any())).willReturn(100L, 300L, 500L);
        given(videoLikePort.getVideoLikeCount(any())).willReturn(1000L, 1500L, 2000L);

        // when
        var result = sut.getVideoList(channelId);

        // then
        then(result)
                .hasSize(3)
                .extracting("channelId").containsOnly(channelId);

        then(result)
                .extracting("viewCount", "likeCount")
                .contains(tuple(100L, 1000L), tuple(300L, 1500L), tuple(500L, 2000L));
    }

    @Test
    void testCreateVideo() {
        // given
        var videoRequest = VideoRequestFixtures.stub();
        willDoNothing().given(saveVideoPort).saveVideo(any());
        given(loadChannelPort.loadChannel(any())).willReturn(Optional.of(ChannelFixtures.stub(videoRequest.getChannelId())));

        // when
        var result = sut.createVideo(videoRequest);

        // then
        then(result)
                .isNotNull()
                .hasFieldOrProperty("id");
        verify(saveVideoPort).saveVideo(any());
        verify(saveChannelPort).saveChannel(any());

    }

    @Test
    void testIncreaseViewCount() {
        sut.increaseViewCount("videoId");

        verify(saveVideoPort).incrementViewCount("videoId");
    }
}
