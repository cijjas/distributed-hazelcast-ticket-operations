package ar.edu.itba.pod.tpe2.models.ticket;

import java.io.Serializable;
import java.time.LocalDate;

public interface Ticket  {
    String getPlate();
    LocalDate getIssueDate();
    String getInfractionCode();
    Double getFineAmount();
    String getCountyName();
    String getIssuingAgency();
    Ticket createTicket(String[] fields);

}
