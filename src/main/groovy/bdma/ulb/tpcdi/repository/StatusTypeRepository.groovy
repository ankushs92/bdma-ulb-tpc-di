package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.StatusType
import org.springframework.data.jpa.repository.JpaRepository

interface StatusTypeRepository extends JpaRepository<StatusType, String> {
}
