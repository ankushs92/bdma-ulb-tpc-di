package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.domain.enums.Status
import groovy.transform.ToString
import org.springframework.data.domain.Persistable

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "DimCustomer")
@ToString(includeNames = true)
class DimCustomer implements Persistable<Integer> {

    @Id
    @Column(name = "SK_CustomerID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "CustomerID", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer customerId

    @Column(name = "TaxID", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String taxId

    @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    Status status

    @Column(name = "FirstName", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
    String firstName

    @Column(name = "LastName", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
    String lastName


    @Column(name = "MiddleInitial", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
    String middleInitial

    @Column(name = "Gender", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
    Gender gender

    @Column(name = "Tier", columnDefinition = "TINYINT(1) UNSIGNED")
    Integer tier

    @Column(name = "DOB", columnDefinition = "DATE")
    LocalDate dob

    @Column(name = "AddressLine1", nullable = false, columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine1

    @Column(name = "AddressLine2", columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine2

    @Column(name = "PostalCode", nullable = false, columnDefinition = "VARCHAR(12) DEFAULT ''")
    String postalCode

    @Column(name = "City", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    String city

    @Column(name = "Country", columnDefinition = "VARCHAR(25) DEFAULT ''")
    String country


    @Column(name = "StateProv", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String stateProv

    @Column(name = "Phone1", columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone1

    @Column(name = "Phone2", columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone2

    @Column(name = "Phone3", columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone3

    @Column(name = "Email1", columnDefinition = "VARCHAR(50) DEFAULT ''")
    String email1

    @Column(name = "Email2", columnDefinition = "VARCHAR(50) DEFAULT ''")
    String email2

    @Column(name = "NationalTaxRateDesc", columnDefinition = "VARCHAR(50) DEFAULT ''")
    String nationalTaxRateDesc

    @Column(name = "NationalTaxRate", columnDefinition = "DECIMAL(6,5) UNSIGNED")
    Double nationalTaxRate

    @Column(name = "LocalTaxRateDesc", columnDefinition = "VARCHAR(50) DEFAULT ''")
    String localTaxRateDesc

    @Column(name = "LocalTaxRate", columnDefinition = "DECIMAL(6,5) UNSIGNED")
    Double localTaxRate


    @Column(name = "AgencyID", columnDefinition = "VARCHAR(30) DEFAULT ''")
    String agencyId

    @Column(name = "CreditRating", columnDefinition = "INT(5) UNSIGNED")
    Integer creditRating

    @Column(name = "NetWorth", columnDefinition = "DECIMAL (10,2) UNSIGNED")
    Double netWorth

    @Column(name = "MarketingNameplate", columnDefinition = "VARCHAR(100) DEFAULT ''")
    String marketingNameplate

    @Column(name = "IsCurrent", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCurrent

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false, columnDefinition = "DATE")
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false, columnDefinition = "DATE")
    LocalDate endDate

    @org.springframework.data.annotation.Transient
    String actionType

    @Override
    boolean isNew() {
        true
    }
}
