package com.github.MaierFlorian.testsmelldetection.data;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    // Singleton

    private static Statistics instance = new Statistics();

    public static Statistics getInstance(){
        return instance;
    }


    private List<Long> timesReadingFiles = new ArrayList<>();
    private List<Long> timesToCountStatementsPerMethod = new ArrayList<>();
    private List<Long> timeToDetectRGT = new ArrayList<>();
    private List<Long> timeToDetectCTL = new ArrayList<>();
    private List<Long> timesToCountAssertions = new ArrayList<>();
    private List<Long> timeToDetectAT = new ArrayList<>();

    // methods

    public long getAverageTimeReadingFiles(){
        long sum = 0;
        for(long l : timesReadingFiles){
            sum += l;
        }
        return sum / timesReadingFiles.size();
    }

    public long getAverageTimeToCountStatementsPerMethod(){
        long sum = 0;
        for(long l : timesToCountStatementsPerMethod){
            sum += l;
        }
        return sum / timesToCountStatementsPerMethod.size();
    }

    public long getAverageTimeToDetectRGT(){
        long sum = 0;
        for(long l : timeToDetectRGT){
            sum += l;
        }
        return sum / timeToDetectRGT.size();
    }

    public long getAverageTimeToDetectCTL(){
        long sum = 0;
        for(long l : timeToDetectCTL){
            sum += l;
        }
        return sum / timeToDetectCTL.size();
    }

    public long getAverageTimesToCountAssertions(){
        long sum = 0;
        for(long l : timesToCountAssertions){
            sum += l;
        }
        return sum / timesToCountAssertions.size();
    }

    public long getAverageTimeToDetectAT(){
        long sum = 0;
        for(long l : timeToDetectAT){
            sum += l;
        }
        return sum / timeToDetectAT.size();
    }

    // Adding values

    public void addTimeReadingFiles(long time){
        timesReadingFiles.add(time);
    }

    public void addTimeToCountStatementsPerMethod(long time){
        timesToCountStatementsPerMethod.add(time);
    }
    public void addTimeToDetectRGT(long time){
        timeToDetectRGT.add(time);
    }
    public void addTimeToDetectCTL(long time){
        timeToDetectCTL.add(time);
    }
    public void addTimeToCountAssertions(long time){
        timesToCountAssertions.add(time);
    }
    public void addTimeToDetectAT(long time){
        timeToDetectAT.add(time);
    }
}
