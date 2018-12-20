package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Status
import groovy.transform.ToString
import org.springframework.data.domain.Persistable

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DimCompany")
@ToString(includeNames = true)
class DimCompany  implements Persistable<Integer> {

    @Id
    @Column(name = "SK_CompanyID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "CompanyId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer companyId

    @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    Status status

    @Column(name = "Name", nullable = false, columnDefinition = "VARCHAR(60) DEFAULT ''")
    String name

    @Column(name = "Industry", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String industry

    @Column(name = "SPrating", columnDefinition = "VARCHAR(4) DEFAULT ''")
    String spRating

    @Column(name = "IsLowGrade", columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isLowGrade

    @Column(name = "CEO", nullable = false, columnDefinition = "VARCHAR(100) DEFAULT ''")
    String ceo

    @Column(name = "AddressLine1", columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine1

    @Column(name = "AddressLine2", columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine2

    @Column(name = "PostalCode", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    String postalCode

    @Column(name = "City", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    String city

    @Column(name = "StateProv", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String stateProv


    @Column(name = "Country", columnDefinition = "VARCHAR(24) DEFAULT ''")
    String country

    @Column(name = "Description", nullable = false, columnDefinition = "VARCHAR(150) DEFAULT ''")
    String desc

    @Column(name = "FoundingDate")
    LocalDate foundingDate

    @Column(name = "IsCurrent", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCurrent

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false)
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false)
    LocalDate endDate

    @Override
    boolean isNew() {
        true
    }
}
