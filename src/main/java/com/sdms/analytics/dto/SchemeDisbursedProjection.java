package com.sdms.analytics.dto;

import java.math.BigDecimal;

public interface SchemeDisbursedProjection {
    Long getSchemeId();
    String getSchemeName();
    BigDecimal getTotalDisbursed();
}