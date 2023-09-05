package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.service.commentservice.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/{userId}/comments/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@PathVariable long userId,
                                        @PathVariable long eventId,
                                        @RequestBody @Valid CommentShortDto commentDto) {
        log.info("Получен POST запрос /users/{}/comments/events/{}", userId, eventId);
        return commentService.createComment(userId, eventId, commentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        log.info("Получен DELETE запрос /users/{}/comments/{}", userId, commentId);
        commentService.deleteCommentForCommentator(userId, commentId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentFullDto updateComment(@PathVariable long userId,
                                        @PathVariable long commentId,
                                        @RequestBody @Valid CommentShortDto commentDto) {
        log.info("Получен PATCH запрос /users/{}/comments/{}", userId, commentId);
        return commentService.updateCommentForCommentator(userId, commentId, commentDto);
    }

    @PatchMapping("/{userId}/comments/{commentId}/likes")
    public CommentFullDto addLikeOnComment(@PathVariable long userId,
                                           @PathVariable long commentId) {
        log.info("Получен PATCH запрос /users/{}/comments/{}/likes", userId, commentId);
        return commentService.addLikeOnComment(userId, commentId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentFullDto> getCommentsByCommentator(@PathVariable long userId) {
        log.info("Получен GET запрос /users/{}/comments", userId);
        return commentService.getAllByCommentator(userId);
    }

    @GetMapping("/comments/events/{eventId}")
    public List<CommentFullDto> getCommentsByEvent(@PathVariable long eventId) {
        log.info("Получен GET запрос /comments/events/{}", eventId);
        return commentService.getAllByEvent(eventId);
    }
}
