package com.example.apenadetect.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class Esp32Response {
    public Esp32Response(Double nhipTho){
        this.nhipTho = nhipTho;
    }
    private double nhipTho;
}
