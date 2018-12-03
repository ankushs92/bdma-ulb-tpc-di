package bdma.ulb.tpcdi.jpa

import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.util.Strings

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class GenderJpaConverter implements AttributeConverter<Gender, String> {

    @Override
    String convertToDatabaseColumn(Gender attribute) {
        if(attribute) {
            attribute.name()
        }
        else {
            "U"
        }
    }

    @Override
    Gender convertToEntityAttribute(String dbData) {
        def result = Gender.U
        if(Strings.hasText(dbData)) {
            result = Gender.from(dbData)
        }
        result
    }
}
