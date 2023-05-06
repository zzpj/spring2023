package com.zzpj.EventManager.controller;

import com.zzpj.EventManager.model.Event;
import com.zzpj.EventManager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/getAllEvents")
    public List<Event> getAllEvent() {
        return eventService.getAllEvents();
    }

    @GetMapping("/getEvent/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/addEvent")
    public void addEvent(@Valid @RequestBody Event event) {
        eventService.addEvent(event);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }


    @PutMapping("/update/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(event);
    }
}
