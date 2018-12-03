package bdma.ulb.tpcdi.repository

import bdma.ulb.tpcdi.domain.Industry
import org.springframework.data.jpa.repository.JpaRepository

interface IndustryRepository extends JpaRepository<Industry, String> {
}
