package com.myshop.dto;

import com.myshop.domain.Notification;
//import com.myshop.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class NewsFeedDto {
    private List<NotificationDto> notificationDtos;
//    private List<PostDto> postDtos;

    public static NewsFeedDto getNewsfeedDto(List<Notification> notifications ) {
        return new NewsFeedDto(
                notifications.stream()
                        .map(NotificationDto::getFollowNotification)
                        .sorted(Comparator.comparing(NotificationDto::getCreatedAt).reversed())
                        .collect(Collectors.toList())
//                ,posts.stream()
//                        .map(PostDto::of)
//                        .sorted(Comparator.comparing(PostDto::getId).reversed())
//                        .collect(Collectors.toList())
        );
    }
}