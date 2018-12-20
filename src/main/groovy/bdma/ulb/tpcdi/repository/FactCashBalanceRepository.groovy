package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.FactCashBalance
import org.springframework.data.jpa.repository.JpaRepository

interface FactCashBalanceRepository extends JpaRepository<FactCashBalance, Integer> {
}
