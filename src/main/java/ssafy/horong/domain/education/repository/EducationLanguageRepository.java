package ssafy.horong.domain.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ssafy.horong.domain.education.entity.EducationLanguage;
import ssafy.horong.domain.member.common.Language;

import java.util.List;

public interface EducationLanguageRepository extends JpaRepository<EducationLanguage, Long> {
    List<EducationLanguage> findByEducationIdAndLanguage(Long educationId, Language language);
}

