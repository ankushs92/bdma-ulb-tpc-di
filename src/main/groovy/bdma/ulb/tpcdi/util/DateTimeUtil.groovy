package bdma.ulb.tpcdi.util

import java.time.LocalDate
import java.time.LocalDateTime
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

    static LocalDate parseFinWireDate(String date) {
        Assert.notEmptyString(date, "Date cannot be null or empty")
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))
    }

    static LocalDateTime parseFinWireDateAndTime(String dateTime) {
        Assert.notEmptyString(dateTime, "dateTime cannot be null or empty")
        LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
    }

    static LocalDateTime parseIso(String iso) {
        Assert.notEmptyString(iso ,"iso cannot be null or empty")
        try {
            LocalDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME)
        }
        catch(ex) {
            LocalDateTime.parse(iso, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        }
    }

    public static void main(String[] args) {
        def s = "2007-07-07T04:28:56"
        println LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
    }

}
