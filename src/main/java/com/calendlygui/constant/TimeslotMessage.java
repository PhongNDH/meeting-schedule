package com.calendlygui.constant;

import com.calendlygui.constant.ConstantValue;

public class TimeslotMessage {
    public static final String TIMESLOT_DATETIME_PAST  = "The meeting time should be after the current date and time";
    public static final String TIMESLOT_DATETIME_SURPASS  = "The meeting time surpass a period of "+ConstantValue.MAX_TIME_WAITING+" days from now" ;
    public static final String TIMESLOT_TIME_WRONG_FORMAT = "Format for time is hh:mm";
    public static final String TIMESLOT_TIME_CONFLICT = "Time for this meeting coincides with one of your others";
    public static final String TIMESLOT_SUCCESS = "Meeting was successfully created";
    public static final String TIMESLOT_DURATION_WRONG = "Duration for meeting must be between "+ConstantValue.TIMESLOT_MIN_DURATION+" and "+ConstantValue.TIMESLOT_MAX_DURATION;
}
