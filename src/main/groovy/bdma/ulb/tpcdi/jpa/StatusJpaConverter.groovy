package bdma.ulb.tpcdi.jpa

import bdma.ulb.tpcdi.domain.enums.Status
import bdma.ulb.tpcdi.util.Strings

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class StatusJpaConverter implements AttributeConverter<Status, String> {

    @Override
    String convertToDatabaseColumn(Status attribute) {
        if(attribute) {
            attribute.code
        }
    }

    @Override
    Status convertToEntityAttribute(String dbData) {
        if(Strings.hasText(dbData)) {
            Status.from(dbData)
        }
    }

}
