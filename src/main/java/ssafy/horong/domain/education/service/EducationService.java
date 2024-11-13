package ssafy.horong.domain.education.service;

import ssafy.horong.api.education.response.EducationRecordResponse;
import ssafy.horong.api.education.response.GetAllEducationRecordResponse;
import ssafy.horong.api.education.response.GetEducationRecordResponse;
import ssafy.horong.api.education.response.TodayWordsResponse;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;
import ssafy.horong.domain.education.entity.EducationRecord;

import java.time.LocalDate;
import java.util.List;

public interface EducationService {
    TodayWordsResponse getTodayWords();
    GetAllEducationRecordResponse getAllEducationRecord();
    EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command);
    List<LocalDate> getStampDates();
    List<EducationRecordResponse> getEducationRecordDetail(Long wordId);
}
