package ssafy.horong.domain.member.common;

public enum Language {
    KOREAN("한국어"),
    ENGLISH("영어"),
    CHINESE("중국어"),
    JAPANESE("일본어");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
