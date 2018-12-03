package bdma.ulb.tpcdi.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DateTimeUtil {


    static String readableRepresentation(LocalTime time) {
        DateTimeFormatter.ofPattern("HH:mm:ss").format(time)
    }

    static LocalTime asLocalTime(String time) {
        Assert.notEmptyString(time, "time cannot be null or empty")
        LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    static LocalDate parse(String date) {
        Assert.notEmptyString(date, "Date cannot be null or empty")
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

}
