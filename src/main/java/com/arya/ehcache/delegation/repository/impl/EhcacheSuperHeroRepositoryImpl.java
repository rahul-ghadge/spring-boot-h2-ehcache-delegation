package com.arya.ehcache.delegation.repository.impl;

import com.arya.ehcache.delegation.entities.SuperHero;
import com.arya.ehcache.delegation.repository.SuperHeroRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class EhcacheSuperHeroRepositoryImpl implements SuperHeroRepository {

    private final SuperHeroRepository repository;
    private final Ehcache ehcache;

    public EhcacheSuperHeroRepositoryImpl(@Lazy SuperHeroRepository repository,
                                          Ehcache ehcache) {
        this.repository = repository;
        this.ehcache = ehcache;
    }

    @Override
    public Optional<SuperHero> findById(Integer id) {
        Element superHeroElement = ehcache.get(id);

        if (Objects.isNull(superHeroElement)) {
            log.info("* Fetching Super hero from DB for Id: {}", id);
            Optional<SuperHero> optionalSuperHero = this.repository.findById(id);
            optionalSuperHero.ifPresent(superHero -> ehcache.put(new Element(id, superHero)));
            return optionalSuperHero;
        } else {
            log.info("* Super hero from cache with Id: {} hit count: {}", id, superHeroElement.getHitCount());
            return Optional.of((SuperHero) superHeroElement.getObjectValue());
        }
    }

    @Override
    public List<SuperHero> findAll() {
        return this.repository.findAll();
    }

    @Override
    public List<SuperHero> findByIdIn(List<Integer> ids) {
        List<SuperHero> superHeroes = new ArrayList<>();
        List<Integer> modifiableIds = new ArrayList<>(ids);

        List<Integer> cachedIds = ids.stream()
                .filter(id -> {
                    Element superHeroElement = ehcache.get(id);
                    if (Objects.nonNull(superHeroElement)) {
                        log.info("* Super hero from cache with Id: {} hit count: {}", id, superHeroElement.getHitCount());
                        superHeroes.add((SuperHero) superHeroElement.getObjectValue());
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());

        modifiableIds.removeAll(cachedIds);

        this.repository.findByIdIn(modifiableIds)
                .forEach(hero -> {
                    log.info("* Fetched Super hero from DB and added in cache for Id: {}", hero.getId());
                    ehcache.put(new Element(hero.getId(), hero));
                    superHeroes.add(hero);
                });

        return superHeroes;
    }


    @Override
    public List<SuperHero> findAll(Sort sort) {
        return this.repository.findAll(sort);
    }

    @Override
    public Page<SuperHero> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Override
    public List<SuperHero> findAllById(Iterable<Integer> integers) {
        return this.repository.findAllById(integers);
    }

    @Override
    public long count() {
        return this.repository.count();
    }

    @Override
    public void deleteById(Integer s) {
        this.repository.deleteById(s);
    }

    @Override
    public void delete(SuperHero entity) {
        this.repository.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        this.repository.deleteAllById(ids);
    }

    @Override
    public void deleteAll(Iterable<? extends SuperHero> entities) {
        this.repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        this.repository.deleteAll();
    }

    @Override
    public SuperHero save(SuperHero entity) {
        return this.repository.save(entity);
    }

    @Override
    public <S extends SuperHero> List<S> saveAll(Iterable<S> entities) {
        return this.repository.saveAll(entities);
    }


    @Override
    public boolean existsById(Integer s) {
        return this.repository.existsById(s);
    }

    @Override
    public void flush() {
        this.repository.flush();
    }

    @Override
    public <S extends SuperHero> S saveAndFlush(S entity) {
        return this.repository.saveAndFlush(entity);
    }

    @Override
    public <S extends SuperHero> List<S> saveAllAndFlush(Iterable<S> entities) {
        return this.repository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<SuperHero> entities) {
        this.repository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> strings) {
        this.repository.deleteAllByIdInBatch(strings);
    }

    @Override
    public void deleteAllInBatch() {
        this.repository.deleteAllInBatch();
    }

    @Override
    public SuperHero getOne(Integer s) {
        return this.repository.getById(s);
    }

    @Override
    public SuperHero getById(Integer s) {
        return this.repository.getById(s);
    }

    @Override
    public <S extends SuperHero> Optional<S> findOne(Example<S> example) {
        return this.repository.findOne(example);
    }

    @Override
    public <S extends SuperHero> List<S> findAll(Example<S> example) {
        return this.repository.findAll(example);
    }

    @Override
    public <S extends SuperHero> List<S> findAll(Example<S> example, Sort sort) {
        return this.repository.findAll(example, sort);
    }

    @Override
    public <S extends SuperHero> Page<S> findAll(Example<S> example, Pageable pageable) {
        return this.repository.findAll(example, pageable);
    }

    @Override
    public <S extends SuperHero> long count(Example<S> example) {
        return this.repository.count(example);
    }

    @Override
    public <S extends SuperHero> boolean exists(Example<S> example) {
        return this.repository.exists(example);
    }

    @Override
    public <S extends SuperHero, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return this.repository.findBy(example, queryFunction);
    }
}
