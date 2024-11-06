package ssafy.horong.domain.education.service;

import ssafy.horong.api.education.response.EducationRecordResponse;
import ssafy.horong.api.education.response.GetEducationRecordResponse;
import ssafy.horong.api.education.response.TodayWordsResponse;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;

import java.util.List;

public interface EducationService {
    TodayWordsResponse getTodayWords();
    List<GetEducationRecordResponse> getAllEducationRecord();
    EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command);
}
