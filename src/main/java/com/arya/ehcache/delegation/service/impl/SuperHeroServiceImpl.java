package com.arya.ehcache.delegation.service.impl;

import com.arya.ehcache.delegation.entities.SuperHero;
import com.arya.ehcache.delegation.repository.SuperHeroRepository;
import com.arya.ehcache.delegation.service.SuperHeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SuperHeroServiceImpl implements SuperHeroService {

    private final SuperHeroRepository superHeroRepository;

    @Override
    public List<?> findAll() {
        return superHeroRepository.findAll();
    }

    @Override
    public Optional<SuperHero> findById(int id) {
        return superHeroRepository.findById(id);
    }

    @Override
    public List<?> findByIdIn(List<Integer> ids) {
        return superHeroRepository.findByIdIn(ids);
    }
}
