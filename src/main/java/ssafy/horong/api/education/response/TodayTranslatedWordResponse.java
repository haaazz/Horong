package ssafy.horong.api.education.response;

import ssafy.horong.domain.member.common.Language;

public record TodayTranslatedWordResponse(
        Long id,
        Long educationId, // 연관된 Education 엔티티의 ID만 포함
        Language language,
        String transWord,
        String transDefinition,
        String transExample1,
        String transExample2,
        String audio,
        boolean isSlang,
        String word,
        String pronunciation
) {}