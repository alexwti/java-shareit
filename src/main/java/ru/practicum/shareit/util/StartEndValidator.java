package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.model.Booking;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartEndValidator implements ConstraintValidator<StartEnd, Booking> {

    @Override
    public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
        return !booking.getEnd().isBefore(booking.getStart());
    }
}
