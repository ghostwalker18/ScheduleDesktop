/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
