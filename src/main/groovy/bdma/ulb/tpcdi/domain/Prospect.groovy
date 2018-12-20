package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Gender

import javax.persistence.*

@Entity
@Table(name = "Prospect")
class Prospect {

    @Id
    @Column(name = "SK_ProspectId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "SK_RecordDateId", nullable = false, columnDefinition = "INT(12) UNSIGNED")
    Integer skRecordDateId

    @Column(name = "SK_UpdateDateId", nullable = false, columnDefinition = "INT(12) UNSIGNED")
    Integer skUpdateDateId

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(1) UNSIGNED")
    BatchId batchId

    @Column(name = "IsCustomer", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCustomer

    @Column(name = "FirstName", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String firstName

    @Column(name = "LastName", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT ''")
    String lastName

    @Column(name = "Phone", columnDefinition = "VARCHAR(30)")
    String phone

    @Column(name = "Employer", columnDefinition = "VARCHAR(30)")
    String employer


    @Column(name = "MiddleInitial", columnDefinition = "VARCHAR(1) ")
    String middleInitial

    @Column(name = "Gender", columnDefinition = "VARCHAR(1)")
    Gender gender

    @Column(name = "AddressLine1", columnDefinition = "VARCHAR(80)")
    String addressLine1

    @Column(name = "AddressLine2", columnDefinition = "VARCHAR(80)")
    String addressLine2

    @Column(name = "PostalCode", columnDefinition = "VARCHAR(12)")
    String postalCode

    @Column(name = "City", nullable = false, columnDefinition = "VARCHAR(25) DEFAULT ''")
    String city

    @Column(name = "Country",columnDefinition = "VARCHAR(25)")
    String country

    @Column(name = "State", nullable = false, columnDefinition = "VARCHAR(20)")
    String state

    @Column(name = "Income", columnDefinition = "INT(9) UNSIGNED")
    Integer income

    @Column(name = "NumberCars", columnDefinition = "INT(2) UNSIGNED")
    Integer numberOfCars

    @Column(name = "NumberChildren", columnDefinition = "INT(2) UNSIGNED")
    Integer numberOfChildren

    @Column(name = "MaritalStatus",  columnDefinition = "VARCHAR(1)")
    String maritalStatus

    @Column(name = "Age", columnDefinition = "INT(3) UNSIGNED")
    Integer age

    @Column(name = "CreditRating", columnDefinition = "INT(4) UNSIGNED")
    Integer creditRating

    @Column(name = "OwnOrRentFlag", columnDefinition = "VARCHAR(1)")
    String ownOrRentFlag

    @Column(name = "NumberCreditCards", columnDefinition = "INT(2) UNSIGNED")
    Integer numberOfCreditCards

    @Column(name = "NetWorth", columnDefinition = "INT(12) UNSIGNED")
    Integer netWorth

    @Column(name = "MarketingNameplate", columnDefinition = "VARCHAR(100)")
    String marketingNameplate


}
