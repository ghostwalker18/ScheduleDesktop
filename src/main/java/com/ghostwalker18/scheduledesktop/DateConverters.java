package com.ghostwalker18.scheduledesktop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
public class DateConverters {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    static public String toString(Calendar date){
        return date == null ? null : dateFormat.format(date.getTime());
    }

    static public Calendar fromString(String date){
        if(date == null){
            return null;
        }
        else{
            try{
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(date));
                return cal;
            }
            catch (java.text.ParseException e){
                return null;
            }
        }
    }
}
