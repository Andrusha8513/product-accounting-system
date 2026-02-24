package org.example.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postservice.Model.Post;
import org.example.postservice.dto.CommunityPostEventDto;
import org.example.postservice.service.CommentService;
import org.example.postservice.service.PostService;
import org.example.postservice.service.SubCommentsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunityPostConsumer {

    private final PostService postService;
    private final CommentService commentService;
    private final SubCommentsService subCommentsService;
    @KafkaListener(topics = "community_posts", groupId = "post_group")
    public void handle(CommunityPostEventDto communityPostEventDto) {
        switch (communityPostEventDto.getAction()){
            case CREATE_POST:postService.createPostFromKafka(communityPostEventDto);
            log.info("Успешно создан пост в канале");
            break;
            case UPDATE_POST:postService.updatePostFromKafka(communityPostEventDto);
            log.info("Успешно изменен пост в канале");
            break;
            case DELETE_POST:postService.deletePostFromKafka(communityPostEventDto);
            log.info("Успешно удален пост в канале");
            break;
            case CREATE_COMMENT:commentService.createCommentFromKafka(communityPostEventDto);
            log.info("Успешно создан комментарий в канале");
            break;
            case UPDATE_COMMENT:commentService.updateCommentFromKafka(communityPostEventDto);
            log.info("Успешно изменен комментарий в канале");
            break;
            case DELETE_COMMENT:commentService.deleteCommentFromKafka(communityPostEventDto);
            log.info("Успешно удален комментарий в канале");
            break;
            case CREATE_REPLY:subCommentsService.createSubCommentFromKafka(communityPostEventDto);
            log.info("Успешно создан ответ на комментарий в канале");
            break;
            case UPDATE_REPLY:subCommentsService.updateSubCommentFromKafka(communityPostEventDto);
            log.info("Успешно изменен ответ на комментарий в канале");
            break;
            case DELETE_REPLY:subCommentsService.deleteSubCommentFromKafka(communityPostEventDto);
            log.info("Успешно удален ответ на комментарий в канале");
        }
    }
}
