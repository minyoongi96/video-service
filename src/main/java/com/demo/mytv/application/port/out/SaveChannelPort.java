package com.demo.mytv.application.port.out;

import com.demo.mytv.domain.channel.Channel;

public interface SaveChannelPort {
    void saveChannel(Channel channel);
}
