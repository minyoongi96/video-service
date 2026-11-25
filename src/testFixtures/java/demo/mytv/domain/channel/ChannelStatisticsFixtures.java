package demo.mytv.domain.channel;

import com.demo.mytv.domain.channel.ChannelStatistics;

public class ChannelStatisticsFixtures {
    public static ChannelStatistics stub() {
        return ChannelStatisticsFixtures.stub(100, 5, 10, 20);
    }

    public static ChannelStatistics stub(int viewCount, int videoCount, int subscriberCount, int commentCount) {
        return ChannelStatistics.builder()
                .subscriberCount(subscriberCount)
                .videoCount(videoCount)
                .commentCount(commentCount)
                .build();
    }
}
