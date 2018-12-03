package bdma.ulb.tpcdi.jpa

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class BooleanJpaConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    Integer convertToDatabaseColumn(Boolean attribute) {
        if(Objects.nonNull(attribute)) {
            if(attribute) {
                return 1
            }
            else {
                return 0
            }
        }
    }

    @Override
    Boolean convertToEntityAttribute(Integer dbData) {
        if(Objects.nonNull(dbData)) {
            if(dbData == 1) {
                return true
            }
            if(dbData == 0) {
                return false
            }
        }
    }

}
