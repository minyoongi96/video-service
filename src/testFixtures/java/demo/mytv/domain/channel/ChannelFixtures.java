package demo.mytv.domain.channel;

import com.demo.mytv.domain.channel.Channel;

public class ChannelFixtures {
    public static Channel stub(String id) {
        return Channel.builder()
                .id(id)
                .snippet(ChannelSnippetFixtures.stub())
                .statistics(ChannelStatisticsFixtures.stub())
                .contentOwnerId("userId")
                .build();
    }
}
