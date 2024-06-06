package ar.edu.itba.pod.tpe2.query4;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.mapreduce.KeyPredicate;

import java.time.LocalDate;

public class Query4KeyPredicate implements KeyPredicate<Ticket> {
    private final LocalDate from;
    private final LocalDate to;

    public Query4KeyPredicate(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean evaluate( Ticket ticket ) {
        return ticket.getIssueDate().isAfter(from) && ticket.getIssueDate().isBefore(to);
    }
}