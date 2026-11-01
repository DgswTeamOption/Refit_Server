package com.option.refit.calendar.service.impl;

import com.option.refit.calendar.dto.CalendarRequest;
import com.option.refit.calendar.dto.CalendarResponse;
import com.option.refit.calendar.entity.Memo;
import com.option.refit.calendar.repository.CalendarRepository;
import com.option.refit.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarServiceimpl implements CalendarService {

    private final CalendarRepository memoRepository;

    @Override
    @Transactional
    public CalendarResponse createMemo(Long userId, CalendarRequest request){
        Memo memo = Memo.builder()
                .userId(userId)
                .scheduleId(request.getScheduleId() != null ? request.getScheduleId() : -99L)
                .memoName(request.getMemoName())
                .content(request.getContent())
                .build();
        Memo savedMemo = memoRepository.save(memo);
        return new CalendarResponse(savedMemo);
    }


        public List<CalendarResponse> getMemosByUserId(Long userId) {
        return memoRepository.findByUserId(userId).stream()
                .map(CalendarResponse::new)
                .collect(Collectors.toList());
    }


    public List<CalendarResponse> getMemoByScheduleId(Long userId, Long scheduleId) {
        return memoRepository.findByUserIdAndScheduleId(userId, scheduleId).stream()
                .map(CalendarResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CalendarResponse updateMemo(Long userId, Long memoId, CalendarRequest request){
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 존재하지 않습니다. id=" + memoId));

        if (!memo.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 메모만 수정할 수 있습니다.");
        }

        memo.updateMemo(request.getMemoName(), request.getContent());
        return new CalendarResponse(memo);
    }

    @Transactional
    public void deleteMemo(Long userId, Long memoId){
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메모가 존재하지 않습니다. id=" + memoId));
        if (!memo.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 메모만 삭제할 수 있습니다.");
        }
        memoRepository.delete(memo);
    }


}
