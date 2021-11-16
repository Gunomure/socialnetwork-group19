package ru.skillbox.diplom.util;

import org.mapstruct.Named;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

//@Service
@Named("Utils")
public class MapperUtils {

    @Named("zonedDateTimeToLong")
    public static Long map(ZonedDateTime time){
        return TimeUtil.zonedDateTimeToLong(time);
    }

    @Named("listToIntSize")
    public static int listToSize(List<?> source) {
        return source.size();
    }

    @Named("dateToLong")
    public static Long map(Date date){
        return date.getTime();
    }
}
