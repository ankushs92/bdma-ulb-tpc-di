package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimTime
import org.springframework.data.jpa.repository.JpaRepository

interface DimTimeRepository extends JpaRepository<DimTime, Integer> {
}
