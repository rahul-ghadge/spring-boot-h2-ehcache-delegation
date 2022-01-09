package com.arya.ehcache.delegation.controller;

import com.arya.ehcache.delegation.annotation.LogObjectAfter;
import com.arya.ehcache.delegation.annotation.LogObjectBefore;
import com.arya.ehcache.delegation.entities.SuperHero;
import com.arya.ehcache.delegation.repository.SuperHeroRepository;
import com.arya.ehcache.delegation.service.SuperHeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/super-heroes")
public class SuperHeroController {

    private final SuperHeroService superHeroService;

    @LogObjectAfter
    @GetMapping
    public ResponseEntity<List<?>> findAll() {
        List<?> list = superHeroService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @LogObjectBefore
    @LogObjectAfter
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        Optional<SuperHero> superHero = superHeroService.findById(id);
        return ResponseEntity.ok().body(superHero);
    }

    @LogObjectBefore
    @LogObjectAfter
    @GetMapping("/in")
    public ResponseEntity<List<?>> findByIdIn(@RequestParam List<Integer> ids) {
        List<?> list = superHeroService.findByIdIn(ids);
        return ResponseEntity.ok().body(list);
    }

}