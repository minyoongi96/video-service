package com.demo.mytv.adapter.in.api;

import com.demo.mytv.adapter.in.api.dto.ChannelRequest;
import com.demo.mytv.adapter.in.api.dto.CommandResponse;
import com.demo.mytv.application.port.in.ChannelUseCase;
import com.demo.mytv.domain.channel.Channel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelApiController {
    private final ChannelUseCase channelUseCase;

    public ChannelApiController(ChannelUseCase channelUseCase) {
        this.channelUseCase = channelUseCase;
    }

    @PostMapping
    public CommandResponse createChannel(@RequestBody ChannelRequest channelRequest) {
        var channel = channelUseCase.createChannel(channelRequest);

        return new CommandResponse(channel.getId());
    }

    @PutMapping("{channelId}")
    public void updateChannel(@PathVariable String channelId, @RequestBody ChannelRequest channelRequest) {
        channelUseCase.updateChannel(channelId, channelRequest);
    }

    @GetMapping("{channelId}")
    public Channel getChannel(@PathVariable String channelId) {
        return channelUseCase.getChannel(channelId);
    }
}
