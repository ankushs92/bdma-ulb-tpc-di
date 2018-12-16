package bdma.ulb.tpcdi.domain.keys

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ["actionType", "customerId"])
class CustomerXmlKeys {

    final String actionType
    final Integer customerId

    CustomerXmlKeys(Map fields) {
        this.actionType = fields['actionType'] as String
        this.customerId = fields['customerId'] as Integer

    }
}
