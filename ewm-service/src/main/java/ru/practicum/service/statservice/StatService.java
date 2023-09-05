package ru.practicum.service.statservice;

import ru.practicum.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface StatService {

    Map<Long, Long> getConfirmedRequests(List<Event> events);

    Map<Long, Long> getViews(List<Event> events);

    void addHits(HttpServletRequest request);
}
