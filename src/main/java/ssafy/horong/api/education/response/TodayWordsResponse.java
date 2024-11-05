package ssafy.horong.api.education.response;

import ssafy.horong.domain.education.entity.Education;
import ssafy.horong.domain.education.entity.EducationLanguage;

import java.util.List;

public record TodayWordsResponse(
        List<Education> words,
        List<EducationLanguage> translatedWords
) {
}
