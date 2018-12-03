package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.TradeType
import org.springframework.data.jpa.repository.JpaRepository

interface TradeTypeRepository extends JpaRepository<TradeType, String> {
}
