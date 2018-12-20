package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "FactMarketHistory")
@ToString(includeNames = true)
class FactMarketHistory {

    @Id
    @Column(name = "SK_FactMarketHistoryId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "SK_SecurityID", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skSecurityId

    @Column(name = "SK_CompanyId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCompanyId

    @Column(name = "SK_DateId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skDateId

    @Column(name = "PERatio", columnDefinition = "DECIMAL(12, 2) UNSIGNED")
    Double peRatio

    @Column(name = "Yield", columnDefinition = "DECIMAL(5, 2) UNSIGNED")
    Double yield

    @Column(name = "FiftyTwoWeekHigh", columnDefinition = "DECIMAL(8, 2) UNSIGNED")
    Double fiftyTwoWeekHigh

    @Column(name = "SK_FiftyTwoWeekHighDate", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skFiftyTwoWeekHighDateId

    @Column(name = "FiftyTwoWeekLow", columnDefinition = "DECIMAL(8, 2) UNSIGNED")
    Double fiftyTwoWeekLow

    @Column(name = "SK_FiftyTwoWeekLowDate", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skFiftyTwoWeekLowDateId

    @Column(name = "ClosePrice", columnDefinition = "DECIMAL(8, 2) UNSIGNED")
    Double closePrice

    @Column(name = "DayHigh", columnDefinition = "DECIMAL(8, 2) UNSIGNED")
    Double dayHigh

    @Column(name = "DayLow", columnDefinition = "DECIMAL(8, 2) UNSIGNED")
    Double dayLow

    @Column(name = "Volume", nullable = false, columnDefinition = "INT(12) UNSIGNED")
    Integer volume

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

}
