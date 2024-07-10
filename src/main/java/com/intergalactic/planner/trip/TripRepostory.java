package com.intergalactic.planner.trip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripRepostory extends JpaRepository<Trip, UUID> {
}
