package bdma.ulb.tpcdi.domain.keys

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ["hourId", "minuteId", "secondId"])
class DimTimeKeys {

    final Integer hourId
    final Integer minuteId
    final Integer secondId

    DimTimeKeys(Map fields) {
        this.hourId = fields['hourId'] as Integer
        this.minuteId = fields['minuteId'] as Integer
        this.secondId = fields['secondId'] as Integer

    }
}
