package ar.edu.itba.pod.tpe2.models;

public class TicketAdapterFactory {
    public static Ticket createAdapter(String city, String[] fields) {
        if (city.equals("NYC")) {
            return new TicketNYCAdapter(fields);
        } else if (city.equals("CHI")) {
            return new TicketCHIAdapter(fields);
        } else {
            throw new IllegalArgumentException("Unknown city: " + city);
        }
    }
}
