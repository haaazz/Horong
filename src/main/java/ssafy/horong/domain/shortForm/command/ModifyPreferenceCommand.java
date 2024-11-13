package ssafy.horong.domain.shortForm.command;

public record ModifyPreferenceCommand(
        Long shortFormId,
        Integer preference
) {
}
