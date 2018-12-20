package bdma.ulb.tpcdi.domain

import bdma.ulb.tpcdi.domain.enums.BatchId

import javax.persistence.*

@Entity
@Table(name = "FactHoldings")
class FactHoldings {

    @Id
    @Column(name = "SK_FactCashBalanceId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column(name = "TradeId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer tradeId

    @Column(name = "CurrentTradeId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer currentTradeId

    @Column(name = "SK_CustomerId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCustomerId

    @Column(name = "SK_AccountId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skAccountId

    @Column(name = "SK_SecurityId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skSecurityId

    @Column(name = "SK_CompanyId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skCompanyId

    @Column(name = "SK_DateId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skDateId

    @Column(name = "SK_TimeId", nullable = false, columnDefinition = "INT(11) UNSIGNED")
    Integer skTimeId

    @Column(name = "CurrentPrice", columnDefinition = "DECIMAL(11, 0) UNSIGNED")
    Double currentPrice

    @Column(name = "CurrentHolding", columnDefinition = "INT(6)")
    Integer currentHolding

    @Column(name = "BatchId", nullable = false, columnDefinition = "INT(5) UNSIGNED")
    BatchId batchId
}
