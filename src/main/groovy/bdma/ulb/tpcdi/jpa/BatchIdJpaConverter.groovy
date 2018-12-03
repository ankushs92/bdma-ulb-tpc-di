package bdma.ulb.tpcdi.jpa

import bdma.ulb.tpcdi.domain.enums.BatchId

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class BatchIdJpaConverter implements AttributeConverter<BatchId, Integer> {

    @Override
    Integer convertToDatabaseColumn(BatchId attribute) {
        if(attribute) {
            attribute.code
        }
    }

    @Override
    BatchId convertToEntityAttribute(Integer dbData) {
        if(Objects.nonNull(dbData)) {
            BatchId.from(dbData)
        }
    }

}
