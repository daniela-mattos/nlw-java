package com.intergalactic.planner.trip;

import com.intergalactic.planner.link.LinkResponse;
import com.intergalactic.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
}
