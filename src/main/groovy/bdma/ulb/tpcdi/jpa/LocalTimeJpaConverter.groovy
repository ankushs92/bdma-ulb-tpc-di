package bdma.ulb.tpcdi.jpa

import bdma.ulb.tpcdi.util.DateTimeUtil
import bdma.ulb.tpcdi.util.Strings

import javax.persistence.AttributeConverter
import javax.persistence.Converter
import java.time.LocalTime

@Converter(autoApply = true)
class LocalTimeJpaConverter implements AttributeConverter<LocalTime, String> {

    @Override
    String convertToDatabaseColumn(LocalTime time) {
        if(time) {
            DateTimeUtil.readableRepresentation(time)
        }
    }

    @Override
    LocalTime convertToEntityAttribute(String dbData) {
        if(Strings.hasText(dbData)) {
            DateTimeUtil.asLocalTime(dbData)
        }
    }
}
