package com.example.channel_service.mapper;

import com.example.channel_service.Channel;
import com.example.channel_service.dto.ChannelDto;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {
    public Channel toEntity(ChannelDto channelDto){
        Channel channel = new Channel();
        channel.setTittle(channel.getTittle());
        channel.setOwner_id(channelDto.getOwnerId());
        return channel;
    }
}
