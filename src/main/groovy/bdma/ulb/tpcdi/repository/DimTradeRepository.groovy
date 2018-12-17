package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimTrade
import org.springframework.data.jpa.repository.JpaRepository

interface DimTradeRepository extends JpaRepository<DimTrade, Integer> {
}
