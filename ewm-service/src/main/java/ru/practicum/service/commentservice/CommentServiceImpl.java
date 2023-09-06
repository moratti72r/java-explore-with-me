package ru.practicum.service.commentservice;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentFullDto;
import ru.practicum.dto.comment.CommentShortDto;
import ru.practicum.dto.mapper.CommentMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotMeetLogicAppException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;

    UserRepository userRepository;

    EventRepository eventRepository;

    @Override
    public CommentFullDto createComment(long userId, long eventId, CommentShortDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotMeetLogicAppException("Событие должно быть опубликовано");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .commentator(user)
                .createdOn(LocalDateTime.now())
                .build();

        Comment result = commentRepository.save(comment);
        log.info("Комментарий с id={} добавлен", result.getId());

        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public void deleteCommentForCommentator(long userId, long commentId) {
        if (commentRepository.existsByIdAndCommentatorId(commentId, userId)) {
            commentRepository.deleteById(commentId);
            log.info("Комментарий с id={} комментатором с id={} удален", commentId, userId);
        } else
            throw new NotFoundException("Comment with id=" + commentId + " where commentator has id=" + userId + "  was not found");
    }

    @Override
    public CommentFullDto updateCommentForCommentator(long userId, long commentId, CommentShortDto commentDto) {
        Comment comment = commentRepository.findCommentByIdAndCommentatorId(commentId, userId).orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " where commentator has id=" + userId + "  was not found"));

        comment.setText(commentDto.getText());
        comment.setPatchedOn(LocalDateTime.now());

        commentRepository.save(comment);
        log.info("Комментарий с id={} от комментатора с id={} обновлен", commentId, userId);

        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public CommentFullDto addLikeOnComment(long userId, long commentId) {
        if (commentRepository.existsByIdAndCommentatorId(commentId, userId)) {
            throw new NotMeetLogicAppException("can not rate your comments");
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + "  was not found"));
        comment.setLikes(comment.getLikes() + 1L);
        commentRepository.save(comment);

        log.info("Пользователь с id={} оценил комментарий с id={}", userId, commentId);
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public List<CommentFullDto> getAllByCommentator(long userId) {
        List<CommentFullDto> result = commentRepository.findAllByCommentatorId(userId).stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
        log.info("Получен список комментариев пользователя с id={}", userId);
        return result;
    }

    @Override
    public List<CommentFullDto> getAllByEvent(long eventId) {
        List<CommentFullDto> result = commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
        log.info("Получен список комментариев на событие с id={}", eventId);
        return result;
    }

    @Override
    public void deleteCommentForAdmin(long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            log.info("Комментарий с id={} удален администратором", commentId);
        } else throw new NotFoundException("Comment with id=" + commentId + "  was not found");
    }

    @Override
    public List<CommentFullDto> searchCommentByText(String searchText) {
        List<CommentFullDto> result = commentRepository.findAllByTextContainingIgnoreCase(searchText).stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());

        log.info("Получен список поиском по тексту - {}", searchText);
        return result;
    }

    @Override
    public CommentFullDto getCommentById(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + "  was not found"));
        log.info("Получен комментарий по id={}", commentId);

        return CommentMapper.toCommentFullDto(comment);

    }

}
