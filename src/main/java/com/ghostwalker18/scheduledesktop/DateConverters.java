package com.ghostwalker18.scheduledesktop;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
@Converter
public class DateConverters implements AttributeConverter<Calendar, String> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String convertToDatabaseColumn(Calendar attribute) {
        return attribute == null ? null : dateFormat.format(attribute.getTime());
    }

    @Override
    public Calendar convertToEntityAttribute(String dbData) {
        if(dbData == null){
            return null;
        }
        else{
            try{
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(dbData));
                return cal;
            }
            catch (java.text.ParseException e){
                return null;
            }
        }
    }
}
