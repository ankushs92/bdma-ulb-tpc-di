package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimDate
import org.springframework.data.jpa.repository.JpaRepository

interface DimDateRepository extends JpaRepository<DimDate, Integer> {
}
