package com.demo.mytv.application;

import com.demo.mytv.adapter.in.api.dto.ChannelRequest;
import com.demo.mytv.adapter.in.api.dto.ChannelSnippetRequest;
import com.demo.mytv.application.port.out.LoadChannelPort;
import com.demo.mytv.application.port.out.SaveChannelPort;
import demo.mytv.domain.channel.ChannelFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;

class ChannelServiceTest {
    private ChannelService channelService;

    private final LoadChannelPort loadChannelPort = mock(LoadChannelPort.class);
    private final SaveChannelPort saveChannelPort = mock(SaveChannelPort.class);

    @BeforeEach
    void setUp() {
        channelService = new ChannelService(loadChannelPort, saveChannelPort);
    }

    @Test
    @DisplayName("createChannel")
    void testCreateChannel() {
        // given
        var channelRequest = new ChannelRequest(
                new ChannelSnippetRequest("title", "description", "https://test.com/thumbnail.jpg"),
                "userId");
        willDoNothing().given(saveChannelPort).saveChannel(any());

        // when
        var result = channelService.createChannel(channelRequest);

        // then
        then(result)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("snippet.title", "title")
                .hasFieldOrPropertyWithValue("snippet.description", "description")
                .hasFieldOrPropertyWithValue("snippet.thumbnailUrl", "https://test.com/thumbnail.jpg")
                .hasFieldOrPropertyWithValue("statistics.videoCount", 0L)
                .hasFieldOrPropertyWithValue("statistics.commentCount", 0L)
                .hasFieldOrPropertyWithValue("statistics.subscriberCount", 0L)
                .hasFieldOrPropertyWithValue("contentOwnerId", "userId");
    }

    @Test
    @DisplayName("updateChannel")
    void testUpdateChannel() {
        // given
        var channelId = "channelId";
        var channelRequest = new ChannelRequest(
                new ChannelSnippetRequest("title2", "description2", "https://test.com/thumbnail2.jpg"),
                "userId"
        );
        given(loadChannelPort.loadChannel(any())).willReturn(Optional.of(ChannelFixtures.stub(channelId)));
        willDoNothing().given(saveChannelPort).saveChannel(any());

        // when
        var result = channelService.updateChannel(channelId, channelRequest);

        // then
        then(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", channelId)
                .hasFieldOrPropertyWithValue("snippet.title", "title2")
                .hasFieldOrPropertyWithValue("snippet.description", "description2")
                .hasFieldOrPropertyWithValue("snippet.thumbnailUrl", "https://test.com/thumbnail2.jpg")
                .hasFieldOrPropertyWithValue("contentOwnerId", "userId");
    }

    @Test
    @DisplayName("getChannel")
    void testGetChannel() {
        // given
        var id = "channelId";
        given(loadChannelPort.loadChannel(any())).willReturn(Optional.of(ChannelFixtures.stub(id)));

        // when
        var result = channelService.getChannel(id);

        // then
        then(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id);
    }
}
