package com.option.refit.domain.classroom.service.impl;

import com.option.refit.domain.absence.repository.AbsenceRepository;
import com.option.refit.domain.attendance.repository.AttendanceRepository;
import com.option.refit.domain.classroom.entity.ClassRoomEntity;
import com.option.refit.domain.classroom.repository.ClassRoomMemberRepository;
import com.option.refit.domain.classroom.repository.ClassRoomRepository;
import com.option.refit.domain.classroom.service.ClassRoomDeleteService;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.nfc.repository.NfcTagRepository;
import com.option.refit.domain.notice.repository.NoticeRepository;
import com.option.refit.domain.schedule.repository.ScheduleRepository;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.domain.user.entity.User;
import com.option.refit.global.error.classError.exception.ClassAccessDeniedException;
import com.option.refit.global.error.classError.exception.ClassNotFoundException;
import com.option.refit.global.error.classError.exception.ClassPermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 수업 행은 삭제하고(참여 코드 unique·FK 때문에), 연관 데이터는 각 엔티티의 기존 삭제 방식을 따른다.
// - 소프트 삭제 엔티티(회차·결석 신청·공지): @SQLDelete 로 deleted_at 기록
// - 그 외(가입 학생·보강 신청·보강 가능 시간·출석 기록): 삭제
// - NFC 태그: 태그는 선생님 소유 자산이므로 삭제하지 않고 수업 연결만 해제 (다른 수업에 재사용 가능)

@Service
@RequiredArgsConstructor
public class ClassRoomDeleteServiceImpl implements ClassRoomDeleteService {

    private final ClassRoomRepository classroomRepository;
    private final ClassRoomMemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final AbsenceRepository absenceRepository;
    private final MakeupRepository makeupRepository;
    private final MakeupAvailabilityRepository makeupAvailabilityRepository;
    private final AttendanceRepository attendanceRepository;
    private final NoticeRepository noticeRepository;
    private final NfcTagRepository nfcTagRepository;

    @Override
    @Transactional
    public void deleteClassroom(Long classroomId, User user) {

        // 교사만 수업을 삭제할 수 있다.
        if (user.getRole() != Role.TEACHER) {
            throw new ClassPermissionDeniedException();
        }

        // 수업이 존재하는지 확인
        ClassRoomEntity classroom = classroomRepository
                .findById(classroomId)
                .orElseThrow(ClassNotFoundException::new);

        // 해당 수업의 담당 교사만 삭제할 수 있다.
        if (!classroom.getTeacher().getId().equals(user.getId())) {
            throw new ClassAccessDeniedException();
        }

        // NFC 태그는 삭제하지 않고 수업 연결만 해제 (FK 해소)
        nfcTagRepository.findByClassId(classroomId)
                .ifPresent(tag -> tag.unlink());
        nfcTagRepository.flush();

        // 연관 데이터 정리
        attendanceRepository.deleteAllByClassId(classroomId);
        makeupRepository.deleteAllByClassId(classroomId);
        makeupAvailabilityRepository.deleteAllByClassId(classroomId);
        absenceRepository.deleteAllByClassId(classroomId);
        noticeRepository.deleteAllByClassId(classroomId);
        scheduleRepository.deleteAllByClassId(classroomId);
        memberRepository.deleteAllByClassroomId(classroomId);

        classroomRepository.delete(classroom);
    }
}
