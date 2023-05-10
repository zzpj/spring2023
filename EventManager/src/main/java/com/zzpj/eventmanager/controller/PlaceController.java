package com.zzpj.eventmanager.controller;

import com.zzpj.eventmanager.service.PlaceService;
import com.zzpj.openapi.PlacesApi;
import com.zzpj.openapi.model.Place;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class PlaceController implements PlacesApi {

    private PlaceService placeService;

    @Override
    public ResponseEntity<Void> createPlace(Place place) {
        placeService.addNewPlace(place);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deletePlaceById(String placeId) {
        placeService.deleteById(placeId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @Override
    public ResponseEntity<Place> getPlaceById(String placeId) {
        return ResponseEntity.ok(placeService.getPlaceById(placeId));
    }
}
