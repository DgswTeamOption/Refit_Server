package com.option.refit.global.error.statsError.exception;

import com.option.refit.global.error.CustomException;
import com.option.refit.global.error.statsError.StatsErrorCode;

public class StatsNotFoundException extends CustomException {
    public StatsNotFoundException() {
        super(StatsErrorCode.STATS_NOT_FOUND);
    }
}
