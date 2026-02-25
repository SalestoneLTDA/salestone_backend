package com.salestonetech.salestone.infrastructure.repository.projection;

import java.time.LocalDate;

public interface DateCountProjection {
    LocalDate getDate();
    Long getCount();
}
