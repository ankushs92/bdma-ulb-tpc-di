package bdma.ulb.tpcdi.jpa

import javax.persistence.AttributeConverter
import javax.persistence.Converter
import java.sql.Date
import java.time.LocalDate

@Converter(autoApply = true)
class LocalDateJpaConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    Date convertToDatabaseColumn(LocalDate attribute) {
        if(attribute) {
            Date.valueOf(attribute)
        }
    }

    @Override
    LocalDate convertToEntityAttribute(Date dbData) {
        if(dbData){
            dbData.toLocalDate()
        }
    }

}
