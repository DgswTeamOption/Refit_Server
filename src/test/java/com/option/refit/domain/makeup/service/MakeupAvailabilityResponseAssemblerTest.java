package com.option.refit.domain.makeup.service;

import com.option.refit.domain.makeup.dto.MakeupAvailabilityResponse;
import com.option.refit.domain.makeup.entity.Makeup;
import com.option.refit.domain.makeup.entity.MakeupAvailability;
import com.option.refit.domain.makeup.repository.MakeupAvailabilityRepository;
import com.option.refit.domain.makeup.repository.MakeupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(MakeupAvailabilityResponseAssembler.class)
class MakeupAvailabilityResponseAssemblerTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2030, 1, 1, 14, 0);

    @Autowired
    private MakeupAvailabilityResponseAssembler assembler;
    @Autowired
    private MakeupAvailabilityRepository availabilityRepository;
    @Autowired
    private MakeupRepository makeupRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private MakeupAvailability availability(long scheduleId, int capacity) {
        return availabilityRepository.save(MakeupAvailability.builder()
                .classId(1L).teacherId(2L).scheduleId(scheduleId).makeupTime(TIME).capacity(capacity).build());
    }

    private Makeup apply(long studentId, long scheduleId, boolean approve, boolean reject, boolean cancel) {
        Makeup m = makeupRepository.save(Makeup.builder().studentId(studentId).teacherId(2L)
                .classId(1L).scheduleId(scheduleId).makeupTime(TIME).build());
        if (approve) m.approve();
        if (reject) m.reject();
        if (cancel) m.cancel();
        return m;
    }

    @Test
    void countsOnlyPendingAndApprovedAndComputesFlags() {
        MakeupAvailability busy = availability(10L, 3);
        MakeupAvailability empty = availability(11L, 2);
        apply(100L, 10L, false, false, false); // 대기
        apply(101L, 10L, true, false, false);  // 승인
        apply(102L, 10L, false, true, false);  // 거절 (제외)
        apply(103L, 10L, false, false, true);  // 취소 (제외)
        entityManager.flush();
        entityManager.clear();

        List<MakeupAvailabilityResponse> result = assembler.toResponses(List.of(busy, empty));

        MakeupAvailabilityResponse b = result.get(0);
        assertThat(b.getPendingCount()).isEqualTo(1);
        assertThat(b.getApprovedCount()).isEqualTo(1);
        assertThat(b.getAppliedCount()).isEqualTo(2);
        assertThat(b.getRemainingCapacity()).isEqualTo(1);
        assertThat(b.isFull()).isFalse();
        assertThat(b.getMinCapacity()).isEqualTo(2);
        assertThat(b.isCanChangeTime()).isFalse();
        assertThat(b.isCanDelete()).isFalse();
        assertThat(b.getLockReason()).isNotNull();

        MakeupAvailabilityResponse e = result.get(1);
        assertThat(e.getAppliedCount()).isZero();
        assertThat(e.getRemainingCapacity()).isEqualTo(2);
        assertThat(e.isCanChangeTime()).isTrue();
        assertThat(e.isCanDelete()).isTrue();
        assertThat(e.getLockReason()).isNull();
    }
}
