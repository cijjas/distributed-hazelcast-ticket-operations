package ar.edu.itba.pod.tpe2.models.ticket.adapters.nyc;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketAdapter;

public class TicketNYCAdapter implements TicketAdapter {
    @Override
    public Ticket createTicket(String[] fields) {
        return new TicketNYC(fields);
    }
}
