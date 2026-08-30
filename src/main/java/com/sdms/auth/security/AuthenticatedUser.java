package com.sdms.auth.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticatedUser {
    private Long userId;
    private Long beneficiaryId; // nullable, only set for BENEFICIARY-role users
}