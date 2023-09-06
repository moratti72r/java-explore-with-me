package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.service.commentservice.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        log.info("Получен DELETE запрос /admin/comments/{}", commentId);
        commentService.deleteCommentForAdmin(commentId);
    }

    @GetMapping()
    public List<CommentFullDto> searchComment(@RequestParam String searchText) {
        log.info("Получен GET запрос /admin/comments?searchText={}", searchText);
        return commentService.searchCommentByText(searchText);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto getCommentById(@PathVariable long commentId) {
        log.info("Получен GET запрос /admin/comments/{}", commentId);
        return commentService.getCommentById(commentId);
    }
}
