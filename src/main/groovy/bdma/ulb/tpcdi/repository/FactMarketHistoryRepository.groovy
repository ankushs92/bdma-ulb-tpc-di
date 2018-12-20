package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.FactMarketHistory
import org.springframework.data.jpa.repository.JpaRepository

interface FactMarketHistoryRepository extends JpaRepository<FactMarketHistory, Integer> {
}
