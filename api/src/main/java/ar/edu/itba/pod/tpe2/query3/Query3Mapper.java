package ar.edu.itba.pod.tpe2.query3;

import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class Query3Mapper implements Mapper<Long, Ticket, String, Double> {
    @Override
    public void map(Long id, Ticket ticket, Context<String, Double> context) {
        context.emit(ticket.getIssuingAgency(), ticket.getFineAmount());
    }
}
