package com.zzpj.EventManager.service;

import com.zzpj.EventManager.model.Event;
import com.zzpj.EventManager.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public Event getEvent(Long id) {
        return eventRepository.findById(id).orElseThrow();
    }

    public void addEvent(Event event) {
        eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Event event) {
        Event updatedEvent = eventRepository.save(event);
        return updatedEvent;
    }
}
