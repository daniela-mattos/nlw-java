package com.intergalactic.planner.trip;

import com.intergalactic.planner.link.LinkResponse;
import com.intergalactic.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepostory repostory;

    @Autowired
    private ParticipantService participantService;

    public ResponseEntity<TripCreateResponse> createTrip(TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);
        this.repostory.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    public Trip findTripById(UUID id) {
        Optional<Trip> trip = this.repostory.findById(id);
        if (trip.isPresent()) {
            Trip newTrip = trip.get();
            return newTrip;
        }
        return null;
    }

    public Trip updateTrip(UUID id, TripRequestPayload payload) {

        Trip rawTrip = this.findTripById(id);

        if(rawTrip != null) {
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());
            this.repostory.save(rawTrip);
            return rawTrip;
        }
        return null;
    }

    public Trip confirmTrip(UUID id) {
        Trip rawTrip = this.findTripById(id);
        if(rawTrip != null) {
            rawTrip.setIsConfirmed(true);
            this.repostory.save(rawTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return rawTrip;
        }
        return null;
    }
}
