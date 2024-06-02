package ar.edu.itba.pod.tpe2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Ticket {
    private String plate;
    private LocalDate issueDate;
    private String infractionCode;
    private Double fineAmount;
    private String countyName;
    private String issuingAgency;

    @Override
    public boolean equals(Object o){
        if(o == this) return true;
        if (!(o instanceof Ticket t)) return false;
        return t.getPlate().equals(plate) && t.getIssueDate().equals(issueDate) && t.getInfractionCode().equals(infractionCode) && t.getFineAmount().equals(fineAmount) && t.getCountyName().equals(countyName) && t.getIssuingAgency().equals(issuingAgency);
    }
    @Override
    public int hashCode() {
        return Objects.hash(plate, issueDate, infractionCode);
    }


}
