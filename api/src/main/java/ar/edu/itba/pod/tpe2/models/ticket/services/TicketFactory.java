package ar.edu.itba.pod.tpe2.models.ticket.services;

import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketCHI;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.TicketNYC;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class TicketFactory implements DataSerializableFactory {


    @Override
    public IdentifiedDataSerializable create(int i) {
        switch (City.values()[i]) {
            case CHI -> new TicketCHI();
            case NYC -> new TicketNYC();
            default -> throw new IllegalArgumentException("Invalid type: " + i);
        }
        return null;
    }
}
