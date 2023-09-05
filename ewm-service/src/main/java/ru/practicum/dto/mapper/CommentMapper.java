package ru.practicum.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentFullDto toCommentFullDto (Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(EventMapper.toEventShortForComment(comment.getEvent()))
                .commentator(UserMapper.toUserShortDto(comment.getCommentator()))
                .createdOn(comment.getCreatedOn())
                .patchedOn(comment.getPatchedOn())
                .likes(comment.getLikes())
                .build();
    }
}
