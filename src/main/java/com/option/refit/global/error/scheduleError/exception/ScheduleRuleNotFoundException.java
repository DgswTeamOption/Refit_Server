package com.option.refit.global.error.scheduleError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.scheduleError.ScheduleErrorCode;

public class ScheduleRuleNotFoundException extends CustomException {
    public ScheduleRuleNotFoundException() {
        super(ScheduleErrorCode.SCHEDULE_RULE_NOT_FOUND);
    }
}
