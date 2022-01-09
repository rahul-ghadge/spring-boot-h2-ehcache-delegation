package com.arya.ehcache.delegation.service;

import com.arya.ehcache.delegation.entities.SuperHero;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface SuperHeroService {

    List<?> findAll();

    Optional<SuperHero> findById(int id);

    List<?> findByIdIn(List<Integer> ids);
}
