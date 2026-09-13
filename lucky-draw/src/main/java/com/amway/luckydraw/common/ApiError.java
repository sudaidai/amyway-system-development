package com.amway.luckydraw.common;

import java.time.Instant;

public record ApiError(ErrorCode code, String message, Instant timestamp) {}
