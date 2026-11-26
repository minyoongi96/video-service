package com.demo.mytv.adapter.in.api;

import com.demo.mytv.adapter.in.api.dto.ChannelRequest;
import com.demo.mytv.adapter.in.api.dto.ChannelSnippetRequest;
import com.demo.mytv.application.port.in.ChannelUseCase;
import demo.mytv.domain.channel.ChannelFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelApiController.class)
class ChannelApiControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelUseCase channelUseCase;

    @Test
    @DisplayName("POST /api/v1/channels")
    void testCreateChannel() throws Exception {
        // given
        var channelId = "channelId";
        var channelRequest = new ChannelRequest(
                new ChannelSnippetRequest("title", "description", "https://test.com/thumbnail.jpg"), "userId"
        );
        given(channelUseCase.createChannel(any())).willReturn(ChannelFixtures.stub(channelId));

        // when
        mockMvc
                .perform(
                        post("/api/v1/channels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(channelRequest))
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(channelId)
                );

        verify(channelUseCase).createChannel(
                argThat(param -> {
                    then(param.getSnippet())
                            .hasFieldOrPropertyWithValue("title", "title")
                            .hasFieldOrPropertyWithValue("description", "description")
                            .hasFieldOrPropertyWithValue("thumbnailUrl", "https://test.com/thumbnail.jpg");
                    then(param.getContentOwnerId())
                            .isEqualTo("userId");
                    return true;
                })
        );
    }

    @Test
    @DisplayName("PUT /api/v1/channels")
    void testUpdateChannel() throws Exception {
        // given
        var channelRequest = new ChannelRequest(new ChannelSnippetRequest("title2", "description2", "https://example.com/thumbnail2.jpg"), "userId");
        given(channelUseCase.updateChannel(any(), any())).willReturn(ChannelFixtures.stub("channelId"));

        // when
        mockMvc
                .perform(
                        put("/api/v1/channels/{id}", "channelId")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(channelRequest))
                )
                .andExpectAll(
                        status().isOk()
                );

        // then
        verify(channelUseCase).updateChannel(
                eq("channelId"),
                argThat(param -> {
                    then(param.getSnippet())
                            .hasFieldOrPropertyWithValue("title", "title2")
                            .hasFieldOrPropertyWithValue("description", "description2")
                            .hasFieldOrPropertyWithValue("thumbnailUrl", "https://example.com/thumbnail2.jpg");
                    then(param.getContentOwnerId())
                            .isEqualTo("userId");
                    return true;
                })
        );
    }
}
