package com.zzpj.eventmanager.service;


import com.zzpj.eventmanager.mapper.PlaceMapper;
import com.zzpj.eventmanager.model.PlaceDAO;
import com.zzpj.eventmanager.repository.PlaceRepository;
import com.zzpj.openapi.model.Place;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public void addNewPlace(Place place) {
        PlaceDAO placeDAO = placeMapper.toPlaceDAO(place);
        placeRepository.save(placeDAO);
    }

    public Place getPlaceById(String id) {
        return placeRepository.findById(id).map(placeMapper::toPlace).orElseThrow();
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll().stream().map(placeMapper::toPlace).toList();
    }

    public void deleteById(String id) {
        placeRepository.deleteById(id);
    }

}
