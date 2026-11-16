package com.smartcampus.model;

/**
 * Represents a scheduled campus event or seminar.
 */
public class CampusEvent {
    private String eventId;
    private String eventName;
    private String eventDate; // YYYY-MM-DD
    private String venue;
    private String organizer;
    private String description;

    public CampusEvent(String eventId, String eventName, String eventDate,
                       String venue, String organizer, String description) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.venue = venue;
        this.organizer = organizer;
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-34s | Date: %-10s | Venue: %-22s | By: %s",
                eventId, eventName, eventDate, venue, organizer);
    }
}
