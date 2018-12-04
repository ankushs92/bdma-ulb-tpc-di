package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender
import bdma.ulb.tpcdi.domain.enums.Status

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "DimCustomer")
class DimCustomer {

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

    @Column(name = "MiddleInitial", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
    String middleInitial

    @Column(name = "Gender", nullable = false, columnDefinition = "VARCHAR(1) DEFAULT ''")
    Gender gender

    @Column(name = "Tier", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    Integer tier

    @Column(name = "DOB", nullable = false, columnDefinition = "DATE")
    LocalDate dob

    @Column(name = "AddressLine1", nullable = false, columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine1


    @Column(name = "AddressLine2", columnDefinition = "VARCHAR(80) DEFAULT ''")
    String addressLine2

    @Column(name = "PostalCode", nullable = false, columnDefinition = "VARCHAR(12) DEFAULT ''")
    String postalCode

    @Column(name = "City", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    String city

    @Column(name = "StateProv", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    String stateProv

    @Column(name = "Phone1", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone1

    @Column(name = "Phone2", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone2

    @Column(name = "Phone3", nullable = false, columnDefinition = "VARCHAR(30) DEFAULT ''")
    String phone3

    @Column(name = "Email1", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String email1

    @Column(name = "Email2", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String email2

    @Column(name = "NationalTaxRateDesc", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String nationalTaxRateDesc

    @Column(name = "NationalTaxRate", nullable = false, columnDefinition = "DECIMAL(6,5) UNSIGNED")
    Double nationalTaxRate

    @Column(name = "LocalTaxRateDesc", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String localTaxRateDesc

    @Column(name = "LocalTaxRate", nullable = false, columnDefinition = "DECIMAL(6,5) UNSIGNED")
    Double localTaxRate


    @Column(name = "AgencyID", columnDefinition = "VARCHAR(30) DEFAULT ''")
    String agencyId

    @Column(name = "CreditRating", columnDefinition = "INT(5) UNSIGNED")
    Integer creditRating

    @Column(name = "NetWorth", columnDefinition = "INT(10)")
    Integer netWorth

    @Column(name = "MarketingNameplate", columnDefinition = "VARCHAR(100) DEFAULT ''")
    String marketingNameplate

    @Column(name = "IsCurrent", columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCurrent

    @Column(name = "BatchId", columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false, columnDefinition = "DATE")
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false, columnDefinition = "DATE")
    LocalDate endDate

}
