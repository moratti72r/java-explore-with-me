package ru.practicum.service.commentservice;

import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;

import java.util.List;

public interface CommentService {
    CommentFullDto createComment(long userId, long eventId, CommentShortDto commentDto);

    void deleteCommentForCommentator(long userId, long commentId);

    CommentFullDto updateCommentForCommentator(long userId, long commentId, CommentShortDto commentDto);

    CommentFullDto addLikeOnComment(long userId, long commentId);

    List<CommentFullDto> getAllByCommentator(long userId);

    List<CommentFullDto> getAllByEvent(long eventId);

    void deleteCommentForAdmin(long commentId);

    List<CommentFullDto> searchCommentByText(String searchText);

    CommentFullDto getCommentById(long commentId);
}
