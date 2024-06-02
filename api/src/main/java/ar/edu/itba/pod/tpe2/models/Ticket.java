package ar.edu.itba.pod.tpe2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {
    private String plate;
    private LocalDate issueDate;
    private Integer infractionCode;
    private Double fineAmount;
    private String countyName;
    private String issuingAgency;
}
