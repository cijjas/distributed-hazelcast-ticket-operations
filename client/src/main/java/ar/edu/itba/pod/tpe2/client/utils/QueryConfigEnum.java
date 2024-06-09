package ar.edu.itba.pod.tpe2.client.utils;

import lombok.Getter;

@Getter
public enum QueryConfigEnum {
    QUERY1("query1", "time1.txt", "Infraction;Tickets", "query1.csv"),
    QUERY2("query2", "time2.txt", "County;InfractionTop1;InfractionTop2;InfractionTop3", "query2.csv"),
    QUERY3("query3", "time3.txt", "Issuing Agency;Percentage", "query3.csv"),
    QUERY4("query4", "time4.txt", "County;Plate;Tickets", "query4.csv"),
    QUERY5("query5", "time5.txt", "Group;Infraction A;Infraction B", "query5.csv"),
    ;

    private final String queryName;
    private final String timeOutputFile;
    private final String queryOutputFile;
    private final String resultHeader;

    QueryConfigEnum(String queryName, String timeOutputFileName, String resultHeader, String queryOutputFile) {
        this.queryName = queryName;
        this.timeOutputFile = timeOutputFileName;
        this.resultHeader = resultHeader;
        this.queryOutputFile = queryOutputFile;
    }

}