package com.demo.mytv.adapter.in.api;

import com.demo.mytv.adapter.in.api.dto.CommandResponse;
import com.demo.mytv.adapter.in.api.dto.VideoRequest;
import com.demo.mytv.application.port.in.VideoUseCase;
import com.demo.mytv.domain.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoApiController {
    private final VideoUseCase videoUseCase;

    @GetMapping("{videoId}")
    public Video getVideo(@PathVariable String videoId) {
        return videoUseCase.getVideo(videoId);
    }

    @GetMapping(params = "channelId")
    public List<Video> listVideo(@RequestParam String channelId) {
        return videoUseCase.getVideoList(channelId);
    }

    @PostMapping
    public CommandResponse createVideo(@RequestBody VideoRequest videoRequest) {
        var video = videoUseCase.createVideo(videoRequest);
        return new CommandResponse(video.getId());
    }

    @PostMapping("{videoId}/view")
    public void increaseVideoViewCount(@PathVariable String videoId) {
        videoUseCase.increaseViewCount(videoId);
    }
}
