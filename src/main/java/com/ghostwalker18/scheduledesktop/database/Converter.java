package com.ghostwalker18.scheduledesktop.database;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {
    Class<? extends QueryArgConverter> converter();
}
