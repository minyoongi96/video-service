package com.demo.mytv.application;

import com.demo.mytv.adapter.in.api.dto.ChannelRequest;
import com.demo.mytv.application.port.in.ChannelUseCase;
import com.demo.mytv.application.port.out.LoadChannelPort;
import com.demo.mytv.application.port.out.SaveChannelPort;
import com.demo.mytv.domain.channel.Channel;
import com.demo.mytv.domain.channel.ChannelSnippet;
import com.demo.mytv.domain.channel.ChannelStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ChannelService implements ChannelUseCase {

    private final LoadChannelPort loadChannelPort;
    private final SaveChannelPort saveChannelPort;

    public ChannelService(LoadChannelPort loadChannelPort, SaveChannelPort saveChannelPort) {
        this.loadChannelPort = loadChannelPort;
        this.saveChannelPort = saveChannelPort;
    }

    @Override
    public Channel createChannel(ChannelRequest request) {
        var channel = Channel.builder()
                .id(UUID.randomUUID().toString())
                .snippet(
                        ChannelSnippet.builder()
                                .title(request.getSnippet().getTitle())
                                .description(request.getSnippet().getDescription())
                                .thumbnailUrl(request.getSnippet().getThumbnailUrl())
                                .publishedAt(LocalDateTime.now())
                                .build()
                )
                .statistics(ChannelStatistics.getDefaultStatistics())
                .contentOwnerId(request.getContentOwnerId())
                .build();

        saveChannelPort.saveChannel(channel);
        return channel;
    }

    @Override
    public Channel updateChannel(String channelId, ChannelRequest channelRequest) {
        var channel = loadChannelPort.loadChannel(channelId).get();
        channel.updateSnippet(channelRequest.getSnippet());

        saveChannelPort.saveChannel(channel);
        return channel;
    }

    @Override
    public Channel getChannel(String channelId) {
        return loadChannelPort.loadChannel(channelId).get();
    }
}
