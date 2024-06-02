package ar.edu.itba.pod.tpe2.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Infraction {
    private String code;
    private String description;
}

