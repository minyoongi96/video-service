package com.demo.mytv.adapter.in.api;

import com.demo.mytv.adapter.in.api.dto.VideoRequest;
import com.demo.mytv.application.port.in.VideoUseCase;
import demo.mytv.adapter.in.dto.VideoRequestFixtures;
import demo.mytv.domain.video.VideoFixtures;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoApiController.class)
class VideoApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VideoUseCase videoUseCase;

    @Nested
    @DisplayName("GET /api/v1/videos/{videoId}")
    class GetVideoTest {

        @Test
        @DisplayName("video가 있으면 200 OK")
        void testGetVideo() throws Exception {
            // given
            var videoId = "videoId";
            given(videoUseCase.getVideo(any())).willReturn(VideoFixtures.stub(videoId));

            // when, then
            mockMvc
                    .perform(
                            get("/api/v1/videos/{videoId}", videoId)
                    )
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(videoId)
                    );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/videos?channelId={channelId}")
    class ListVideoTest {
        @Test
        @DisplayName("200 OK, 목록 확인")
        void testListVideo() throws Exception {
            // given
            var channelId = "channelId";
            var list = LongStream.range(1L, 4L)
                    .mapToObj(i -> VideoFixtures.stub("videoId" + i))
                    .toList();

            given(videoUseCase.getVideoList(any())).willReturn(list);

            // when, then
            mockMvc
                    .perform(
                            get("/api/v1/videos?channelId={channelId}", channelId)
                    )
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.size()").value(list.size()),
                            jsonPath("$[0].channelId").value(channelId)
                    );
        }
    }

    @Nested
    @DisplayName("POST /api/v1/videos")
    class CreateVideo {
        @Test
        @DisplayName(("200 OK, 생성된 Video id 를 반환"))
        void testCreateVideo() throws Exception {
            // given
            var videoRequest = new VideoRequest(
                    "video title",
                    "video description",
                    "https://test.com/thumbnail.jpg",
                    "channelId"
            );
            given(videoUseCase.createVideo(any())).willReturn(VideoFixtures.stub("videoId"));

            // when, then
            mockMvc
                    .perform(
                            post("/api/v1/videos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(videoRequest))
                    )
                    .andExpectAll(
                            status().isOk()
                    );

            verify(videoUseCase).createVideo(any());
        }

        @Nested
        @DisplayName("POST /api/v1/videos/view")
        class VideoViewCountTest {
            @Test
            @DisplayName("200 OK, 조회수 증가")
            void testVideoViewCount() throws Exception {
                // given
                var videoId = "videoId";
                willDoNothing().given(videoUseCase).increaseViewCount(any());

                // when, then
                mockMvc
                        .perform(
                                post("/api/v1/videos/{videoId}/view", videoId)
                        )
                        .andExpectAll(
                                status().isOk()
                        );
                verify(videoUseCase).increaseViewCount(videoId);
            }
        }
    }
}
