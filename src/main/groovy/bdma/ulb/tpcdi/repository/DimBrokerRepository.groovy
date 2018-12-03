package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.DimBroker
import org.springframework.data.jpa.repository.JpaRepository

interface DimBrokerRepository extends JpaRepository<DimBroker, Integer>{
}
