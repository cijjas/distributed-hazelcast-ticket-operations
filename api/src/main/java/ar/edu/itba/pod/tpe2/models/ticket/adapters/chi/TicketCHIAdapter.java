package ar.edu.itba.pod.tpe2.models.ticket.adapters.chi;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketAdapter;

public class TicketCHIAdapter implements TicketAdapter {
    @Override
    public Ticket createTicket(String[] fields) {
        return new TicketCHI(fields);
    }
}
