package com.demo.mytv.application.port.out;

import com.demo.mytv.domain.channel.Channel;

import java.util.Optional;

public interface LoadChannelPort {
    Optional<Channel> loadChannel(String id);
}
