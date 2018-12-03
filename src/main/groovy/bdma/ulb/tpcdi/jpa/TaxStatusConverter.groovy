package bdma.ulb.tpcdi.jpa

import bdma.ulb.tpcdi.domain.enums.TaxStatus

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class TaxStatusConverter implements AttributeConverter<TaxStatus, Integer> {

    @Override
    Integer convertToDatabaseColumn(TaxStatus attribute) {
        if(attribute) {
            attribute.code
        }
    }

    @Override
    TaxStatus convertToEntityAttribute(Integer dbData) {
        if(Objects.nonNull(dbData)) {
            TaxStatus.from(dbData)
        }
    }

}
