package com.demo.mytv.application.port.in;

import com.demo.mytv.adapter.in.api.dto.ChannelRequest;
import com.demo.mytv.domain.channel.Channel;

public interface ChannelUseCase {
    Channel createChannel(ChannelRequest channelRequest);
    Channel updateChannel(String channelId, ChannelRequest channelRequest);
    Channel getChannel(String channelId);
}
