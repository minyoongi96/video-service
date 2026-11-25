package com.demo.mytv.adapter.out.jpa.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoJpaRepository extends JpaRepository<VideoJpaEntity, String> {
    List<VideoJpaEntity> findByChannelId(String channelId);
}
