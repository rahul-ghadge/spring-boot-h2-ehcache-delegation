package com.arya.ehcache.delegation.repository;

import com.arya.ehcache.delegation.entities.SuperHero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuperHeroRepository extends JpaRepository<SuperHero, Integer> {

    @Query("SELECT s FROM SuperHero s WHERE s.id = ?1")
    Optional<SuperHero> findById(Integer id);

    @Query("SELECT s FROM SuperHero s")
    List<SuperHero> findAll();

    @Query("SELECT s FROM SuperHero s where s.id in (:ids)")
    List<SuperHero> findByIdIn(List<Integer> ids);
}