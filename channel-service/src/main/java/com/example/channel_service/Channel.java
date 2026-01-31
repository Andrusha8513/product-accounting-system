package com.example.channel_service;

import com.example.user_service.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channel_id;
    private String tittle; // просто название
    private String name; //публичная ссылка как в телеге
    private String description;
    private Boolean isPrivet;
    private Long owner_id;
    private List<Users> admins;// мб сделаю просто айдишник админов , но пока так
   // private List<Message> messages;
    private Long avatar_id;
    private Set<Users> users;

    @ElementCollection(targetClass = ChannelRole.class , fetch = FetchType.EAGER)
    @CollectionTable(name = "channelr_role" , joinColumns = @JoinColumn(name = "users_id"))//тут подвязка к одной бд ,
    @Enumerated(EnumType.STRING)                                                                                     // что не очень хорошо , но пока так
    private Set<ChannelRole> channelRoles;
}
