package bdma.ulb.tpcdi.domain.keys

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ["firstName", "lastName", "postalCode", "addressline1", "addressline2"])
@ToString(includeNames = true)
class ProspectRecordKeys {

    final String firstName
    final String lastName
    final String postalCode
    final String addressline1
    final String addressline2

    ProspectRecordKeys(Map fields) {
        this.firstName = fields['firstName'] as String
        this.lastName = fields['lastName'] as String
        this.postalCode = fields['postalCode'] as String
        this.addressline1 = fields['addressline1'] as String
        this.addressline2 = fields['addressline2'] as String
    }


}
