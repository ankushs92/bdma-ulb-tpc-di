package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId
import bdma.ulb.tpcdi.domain.enums.Status
import org.jcp.xml.dsig.internal.dom.DOMUtils

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

@Entity
@Table(name = "DimSecurity")
class DimSecurity {


    @Id
    @Column(name = "SK_SecurityID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "Symbol", nullable = false, columnDefinition = "VARCHAR(15) DEFAULT ''")
    String symbol

    @Column(name = "Name", nullable = false, columnDefinition = "VARCHAR(70) DEFAULT ''")
    String name

    @Column(name = "Issue", nullable = false, columnDefinition = "VARCHAR(6) DEFAULT ''")
    String issue

    @Column(name = "Status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT ''")
    Status status

    @Column(name = "Security", nullable = false, columnDefinition = "VARCHAR(70) DEFAULT ''")
    String security

    @Column(name = "ExchageID", nullable = false, columnDefinition = "VARCHAR(6) DEFAULT ''")
    String exchangeId

    @Column(name = "SK_CompanyID", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCompanyId

    @Column(name = "SharesOutstanding", nullable = false, columnDefinition = "INT(12) UNSIGNED")
    Integer sharesOutstanding

    @Column(name = "FirstTrade", nullable = false)
    LocalDate firstDate

    @Column(name = "FirstTradeOnExchange", nullable = false)
    LocalDate firstTradeOnExchange

    @Column(name = "Dividend", nullable = false, columnDefinition = "DECIMAL(10,2)")
    Double dividend

    @Column(name = "IsCurrent", nullable = false, columnDefinition = "TINYINT(1) UNSIGNED")
    boolean isCurrent

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId

    @Column(name = "EffectiveDate", nullable = false, columnDefinition = "DATE")
    LocalDate effectiveDate

    @Column(name = "EndDate", nullable = false,  columnDefinition = "DATE")
    LocalDate endDate

}
