package com.dev.library_api.dto;

import com.dev.library_api.model.CopyStatus;

public record ReturnRequest(Long loanId, CopyStatus condition) {}