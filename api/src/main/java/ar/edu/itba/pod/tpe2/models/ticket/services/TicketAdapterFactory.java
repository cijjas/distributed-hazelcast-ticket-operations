package ar.edu.itba.pod.tpe2.models.ticket;

import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketCHI;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketNYC;

public class TicketAdapterFactory {
    public static Ticket getAdapter(City city) {
        return switch (city) {
            case NYC -> new TicketNYC();
            case CHI -> new TicketCHI();
            default -> throw new IllegalArgumentException("Invalid city: " + city);
        };
    }
}
