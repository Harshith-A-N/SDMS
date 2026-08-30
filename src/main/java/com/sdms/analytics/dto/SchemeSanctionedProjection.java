package com.sdms.analytics.dto;

import java.math.BigDecimal;

public interface SchemeSanctionedProjection {
    Long getSchemeId();
    String getSchemeName();
    Long getSanctionedCount();
    BigDecimal getGrantAmount();
}