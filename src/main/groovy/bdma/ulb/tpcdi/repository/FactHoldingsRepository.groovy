package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.FactHoldings
import org.springframework.data.jpa.repository.JpaRepository

interface FactHoldingsRepository extends JpaRepository<FactHoldings, Integer> {
}
