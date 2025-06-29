package org.kenyahmis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.kenyahmis.shared.dto.EventList;
import org.kenyahmis.shared.dto.APIResponse;
import org.kenyahmis.shared.dto.EventBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@RestController
@RequestMapping("/api/event")
public class EventController {
    private final static Logger LOG = LoggerFactory.getLogger(EventController.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventController(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Operation(
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Generic request containing different event types",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EventBase.class),
                            mediaType = "application/json",
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "New Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"new_case\",\"event\":{\"mflCode\":\"1234\",\"createdAt\":\"2024-01-01 00:00:00\",\"positiveHivTestDate\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Linked Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"linked_case\",\"event\":{\"mflCode\":\"1234\",\"createdAt\":\"2024-01-01 00:00:00\",\"positiveHivTestDate\":\"2024-01-01 00:00:00\",\"artStartDate\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "At Risk PBFW Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"at_risk_pbfw\",\"event\":{\"mflCode\":\"1234\",\"createdAt\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Prep Linked At Risk PBFW Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"prep_linked_at_risk_pbfw\",\"event\":{\"mflCode\":\"1234\",\"prepRegimen\":\"AZT\",\"prepNumber\":\"3455\",\"prepStartDate\":\"2024-01-01 00:00:00\",\"createdAt\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Eligible for VL Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"eligible_for_vl\",\"event\":{\"mflCode\":\"1234\",\"pregnancyStatus\":\"Pregnant\",\"breastFeedingStatus\":\"Yes\",\"lastVlResults\":\"300\",\"positiveHivTestDate\":\"2024-01-01 00:00:00\",\"visitDate\":\"2024-01-01 00:00:00\",\"artStartDate\":\"2024-01-01 00:00:00\",\"lastVlOrderDate\":\"2024-01-01 00:00:00\",\"lastVlResultsDate\":\"2024-01-01 00:00:00\",\"createdAt\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Unsuppressed Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"unsuppressed_viral_load\",\"event\":{\"mflCode\":\"1234\",\"pregnancyStatus\":\"Pregnant\",\"breastFeedingStatus\":\"Yes\",\"lastVlResults\":\"300\",\"positiveHivTestDate\":\"2024-01-01 00:00:00\",\"visitDate\":\"2024-01-01 00:00:00\",\"artStartDate\":\"2024-01-01 00:00:00\",\"lastVlOrderDate\":\"2024-01-01 00:00:00\",\"lastVlResultsDate\":\"2024-01-01 00:00:00\",\"lastEacEncounterDate\":\"2024-01-01 00:00:00\",\"createdAt\":\"2024-01-01 00:00:00\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Hei Without PCR Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"hei_without_pcr\",\"event\":{\"mflCode\":\"1234\",\"createdAt\":\"2024-01-01 00:00:00\",\"heiId\":\"455\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Hei Without Final Outcome Case",
                                            value = "[{\"client\":{\"county\":\"Nairobi\",\"subCounty\":\"Langata\",\"ward\":\"Langata\",\"patientPk\":\"505\",\"sex\":\"male\",\"dob\":\"1997-01-01\"},\"eventType\":\"hei_without_final_outcome\",\"event\":{\"mflCode\":\"1234\",\"createdAt\":\"2024-01-01 00:00:00\",\"heiId\":\"455\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Hei Aged 6 To 8 Weeks Case",
                                            value = "[{\"client\":{\"county\":\"Uasin Gishu\",\"subCounty\":\"Turbo\",\"ward\":\"Kiplombe\",\"patientPk\":\"977\",\"sex\":\"male\",\"dob\":\"2024-08-01\"},\"eventType\":\"hei_at_6_to_8_weeks\",\"event\":{\"mflCode\":\"33096\",\"createdAt\":\"2024-01-01 00:00:00\",\"heiId\":\"154\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Hei Aged 24 Weeks Case",
                                            value = "[{\"client\":{\"county\":\"Uasin Gishu\",\"subCounty\":\"Turbo\",\"ward\":\"Kiplombe\",\"patientPk\":\"977\",\"sex\":\"male\",\"dob\":\"2024-08-01\"},\"eventType\":\"hei_at_24_weeks\",\"event\":{\"mflCode\":\"33096\",\"createdAt\":\"2024-01-01 00:00:00\",\"heiId\":\"355\",\"updatedAt\":\"2024-01-01 00:00:00\"}}]"
                                    )
                            }
                    )
            )
    )
    @PutMapping(value = "sync")
    private ResponseEntity<APIResponse> createEvent(@RequestBody @Valid EventList<EventBase<?>> eventList) {
        LOG.info("Processing {} records", eventList.size());
        eventList.forEach((Consumer<? super EventBase<?>>) event -> kafkaTemplate.send("events", event));
        return new ResponseEntity<>(new APIResponse("Successfully added client events"),  HttpStatus.ACCEPTED);
    }
}
