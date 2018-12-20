package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.Prospect
import org.springframework.data.jpa.repository.JpaRepository

interface ProspectRepository extends JpaRepository<Prospect, Integer> {
}
