package com.zzpj.eventmanager.repository;

import com.zzpj.eventmanager.model.PlaceDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<PlaceDAO, String> {
}
