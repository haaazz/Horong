package ssafy.horong.domain.shortForm.command;

public record ModifyIsSavedCommand(
        Long shortFormId,
        Boolean isSaved
) {
}
