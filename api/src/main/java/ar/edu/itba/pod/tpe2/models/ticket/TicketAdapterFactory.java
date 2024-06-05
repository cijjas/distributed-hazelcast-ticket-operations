package ar.edu.itba.pod.tpe2.models.ticket;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketAdapter;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.chi.TicketCHI;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.chi.TicketCHIAdapter;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.nyc.TicketNYC;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.nyc.TicketNYCAdapter;

public class TicketAdapterFactory {
    public static TicketAdapter getAdapter(String city) {
        return switch (city.toUpperCase()) {
            case "NYC" -> new TicketNYCAdapter();
            case "CHI" -> new TicketCHIAdapter();
            default -> throw new IllegalArgumentException("Unknown city: " + city);
        };
    }
}
