package com.zzpj.eventmanager.mapper;

import com.zzpj.eventmanager.model.PlaceDAO;
import com.zzpj.openapi.model.Place;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PlaceMapper {

    @Mapping(source = "placeType", target = "placeType")
    PlaceDAO toPlaceDAO(Place place);

    @Mapping(source = "placeType", target = "placeType")
    Place toPlace(PlaceDAO placeDAO);
}
