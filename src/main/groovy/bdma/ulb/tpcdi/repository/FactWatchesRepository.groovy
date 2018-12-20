package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.FactWatches
import org.springframework.data.jpa.repository.JpaRepository

interface FactWatchesRepository extends JpaRepository<FactWatches, Integer> {
}
