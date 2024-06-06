package ar.edu.itba.pod.tpe2.models;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketCHI;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketNYC;
import lombok.Getter;

@Getter
public enum City {
    UNKNOWN(null),
    NYC(TicketNYC.class),
    CHI(TicketCHI.class);

    private final Class<? extends Ticket> ticketClass;

    City(Class<? extends Ticket> ticketClass) {
        this.ticketClass = ticketClass;
    }

    public static City fromString(String city) {
        try {
            return City.valueOf(city.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown city: " + city, e);
        }
    }
}
