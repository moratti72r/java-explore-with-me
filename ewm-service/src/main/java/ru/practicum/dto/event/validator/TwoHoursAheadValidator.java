package ru.practicum.dto.event.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class TwoHoursAheadValidator implements ConstraintValidator<TwoHoursAhead, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime target, ConstraintValidatorContext context) {
        return target == null || target.isAfter(LocalDateTime.now());
    }
}
