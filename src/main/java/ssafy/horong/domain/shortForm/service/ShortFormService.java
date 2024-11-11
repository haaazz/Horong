package ssafy.horong.domain.shortForm.service;

import ssafy.horong.api.shortForm.response.ShortFromListResponse;

import java.util.List;

public interface ShortFormService{
    List<ShortFromListResponse> getShortFormList();
}
