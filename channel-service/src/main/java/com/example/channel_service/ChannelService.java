package com.example.channel_service;

import com.example.channel_service.dto.ChannelDto;
import com.example.channel_service.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserClient userClient;
    private final ChannelMapper channelMapper;

    public void createChannel(ChannelDto channelDto){

        Channel channel = channelMapper.toEntity(channelDto);
        String public_link = ("@k/ara/" + channel.getTittle());
        channel.setName(public_link);
//        channel.setChannelRoles();
    }
}
