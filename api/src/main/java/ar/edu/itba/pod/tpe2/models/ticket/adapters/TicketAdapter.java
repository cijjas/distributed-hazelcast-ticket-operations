package ar.edu.itba.pod.tpe2.models.ticket.adapters;

import ar.edu.itba.pod.tpe2.models.ticket.Ticket;

public interface TicketAdapter {
    Ticket createTicket(String[] fields);
}


