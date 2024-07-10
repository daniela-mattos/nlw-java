package com.intergalactic.planner.trip;

import com.intergalactic.planner.activity.ActivityData;
import com.intergalactic.planner.activity.ActivityRequestPayload;
import com.intergalactic.planner.activity.ActivityResponse;
import com.intergalactic.planner.activity.ActivityService;
import com.intergalactic.planner.link.LinkData;
import com.intergalactic.planner.link.LinkRequestPayload;
import com.intergalactic.planner.link.LinkResponse;
import com.intergalactic.planner.link.LinkService;
import com.intergalactic.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        var newTrip = this.tripService.createTrip(payload);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getBody().tripId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Trip trip = this.tripService.findTripById(id);

        return ResponseEntity.ok(trip);
    }

    @PutMapping("{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
        Trip trip = this.tripService.updateTrip(id, payload);

        return ResponseEntity.ok(trip);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Trip trip = this.tripService.confirmTrip(id);

        return ResponseEntity.ok(trip);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
                                                                       @RequestBody ParticipantRequestPayload payload) {
        Trip trip = this.tripService.findTripById(id);

        if (trip != null) {
            Trip rawTrip = trip;

            ParticipantCreateResponse participantResponse = this.participantService
                    .registerParticipantToTrip(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }
            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantData> participantList = this.participantService.getAllParticipantsFromTrip(id);

        return ResponseEntity.ok(participantList);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivitie(@PathVariable UUID id,
                                                    @RequestBody ActivityRequestPayload payload) {
        Trip trip = this.tripService.findTripById(id);

        if (trip != null) {
            Trip rawTrip = trip;

            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id) {
        List<ActivityData> activitiesList = this.activityService.getAllActivitiesFromTrip(id);

        return ResponseEntity.ok(activitiesList);
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id,
                                                     @RequestBody LinkRequestPayload payload) {
        Trip trip = this.tripService.findTripById(id);

        if (trip != null) {
            Trip rawTrip = trip;

            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinksFromId(@PathVariable UUID id) {
        List<LinkData> linksList = this.linkService.getAllLinksFromTrip(id);

        return ResponseEntity.ok(linksList);
    }

}
