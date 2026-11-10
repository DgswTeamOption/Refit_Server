package com.option.refit.domain.makeup.service.impl;

import com.option.refit.domain.makeup.dto.MakeupApproveRequest;
import com.option.refit.domain.makeup.dto.MakeupApproveResponse;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupApprovalStatus;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import com.option.refit.domain.makeup.service.MakeupApproveService;
import com.option.refit.domain.user.entity.Role;
import com.option.refit.global.error.makeupError.exception.AlreadyProcessedMakeupException;
import com.option.refit.global.error.makeupError.exception.InvalidApprovalStatusException;
import com.option.refit.global.error.makeupError.exception.MakeupNotFoundException;
import com.option.refit.global.error.securityError.exception.RoleAccessDeniedException;
import com.option.refit.global.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MakeupApproveServiceImpl implements MakeupApproveService {

    private final MakeupRepository makeupRepository;

    // TODO: 수업 도메인 머지 후 "본인이 담당하는 수업의 신청 건만" 처리하도록 검사 추가
    @Override
    @Transactional
    public MakeupApproveResponse processMakeup(JwtPrincipal principal, Long makeupId, MakeupApproveRequest request) {
        // 선생님만 처리 가능 (403)
        if (principal.role() != Role.TEACHER) {
            throw new RoleAccessDeniedException();
        }

        // 승인(1) 또는 거절(2)만 허용, 대기(0)로 되돌리기나 범위 밖 값은 불가 (400)
        MakeupApprovalStatus target = toTargetStatus(request.getApprovalStatus());

        // 존재하지 않는 보강 신청 (404)
        Makeup makeup = makeupRepository.findById(makeupId)
                .orElseThrow(MakeupNotFoundException::new);

        // 이미 승인/거절 처리된 신청 (409)
        if (!makeup.isPending()) {
            throw new AlreadyProcessedMakeupException();
        }

        if (target == MakeupApprovalStatus.APPROVED) {
            makeup.approve();
        } else {
            makeup.reject();
        }
        return new MakeupApproveResponse(makeup);
    }

    private MakeupApprovalStatus toTargetStatus(int approvalStatus) {
        if (approvalStatus == MakeupApprovalStatus.APPROVED.ordinal()) {
            return MakeupApprovalStatus.APPROVED;
        }
        if (approvalStatus == MakeupApprovalStatus.REJECTED.ordinal()) {
            return MakeupApprovalStatus.REJECTED;
        }
        throw new InvalidApprovalStatusException();
    }
}
