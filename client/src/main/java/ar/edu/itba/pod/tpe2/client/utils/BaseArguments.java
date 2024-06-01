package ar.edu.itba.pod.tpe2.client.utils;

import lombok.Getter;

@Getter
public class BaseArguments {
    private final String addresses;
    private final String city;
    private final String inPath;
    private final String outPath;

    public BaseArguments(String addresses, String city, String inPath, String outPath) {
        this.addresses = addresses;
        this.city = city;
        this.inPath = inPath;
        this.outPath = outPath;
    }

}



