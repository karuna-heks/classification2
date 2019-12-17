package com.company;

import java.time.LocalDateTime;

public class TestTimer {

    private LocalDateTime start;
    private LocalDateTime stop;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private boolean start_flag = false;
    private boolean stop_flag = false;

    public void startTimer() {

        start_flag = true;
        stop_flag = false;
        start = LocalDateTime.now();
    }

    public void stopTimer() {

        if (start_flag == false) {
            System.err.println("Таймер не был запущен");
        }
        stop = LocalDateTime.now();
        stop_flag = true;
    }

    public String getDiffence() {

        if (stop_flag == false) {
            System.err.println("Таймер не был остановлен");
        }



        days = stop.getDayOfMonth() - start.getDayOfMonth();
        hours = stop.getHour() - start.getHour();
        minutes = stop.getMinute() - start.getMinute();
        seconds = stop.getSecond() - start.getSecond();

        if (hours < 0) {
            days--;
            hours += 24;
        }

        if (minutes < 0) {
            hours--;
            minutes += 60;
        }

        if (seconds < 0) {
            minutes--;
            seconds += 60;
        }

        String str;
        if (days > 0) {
            str = days + " д., " + hours + " ч., " + minutes + " мин., " + seconds + " сек.";
                    // 15 д., 8 ч., 11 мин., 10 сек.
        } else if (hours > 0) {
            str = hours + " ч., " + minutes + " мин., " + seconds + " сек.";
            // 8 ч., 11 мин., 10 сек.
        } else if (minutes > 0) {
            str = minutes + " мин., " + seconds + " сек.";
        } else {
            str = seconds + " сек.";
        }

        return str;
    }

    public String getStartTime() {

        return start.toString();
    }

    public String getStartTimeSimple() {

        int year = start.getYear();
        int month = start.getMonthValue();
        int day = start.getDayOfMonth();
        int hour = start.getHour();
        int minute = start.getMinute();
        String str = year+"-"+month+"-"+day+"_"+hour+"-"+minute;
        return str;
    }

    public String getStopTime() {

        return stop.toString();
    }

    public String getStopTimeSimple() {

        int year = stop.getYear();
        int month = stop.getMonthValue();
        int day = stop.getDayOfMonth();
        int hour = stop.getHour();
        int minute = stop.getMinute();
        String str = year+"-"+month+"-"+day+"_"+hour+"-"+minute;
        return str;
    }

}
