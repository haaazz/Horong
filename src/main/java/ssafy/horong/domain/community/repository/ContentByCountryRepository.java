package ssafy.horong.domain.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.community.entity.ContentByLanguage;

public interface ContentByCountryRepository extends JpaRepository<ContentByLanguage, Long> {
}
