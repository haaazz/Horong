package ssafy.horong.domain.education.service;

import ssafy.horong.api.education.response.*;
import ssafy.horong.domain.education.command.SaveEduciatonRecordCommand;
import ssafy.horong.domain.education.entity.EducationRecord;

import java.time.LocalDate;
import java.util.List;

public interface EducationService {
    TodayWordsResponse getTodayWords();
    GetAllEducationRecordResponse getAllEducationRecord();
    EducationRecordResponse saveEducationRecord(SaveEduciatonRecordCommand command);
    List<LocalDate> getStampDates();
    GetEducationRecordByWordResponse getEducationRecordDetail(Long wordId);
}
