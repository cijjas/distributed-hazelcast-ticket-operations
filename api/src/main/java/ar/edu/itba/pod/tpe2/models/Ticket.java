package ar.edu.itba.pod.tpe2.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface Ticket extends Serializable {
    String getPlate();
    LocalDate getIssueDate();
    String getInfractionCode();
    Double getFineAmount();
    String getCountyName();
    String getIssuingAgency();
}
