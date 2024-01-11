package com.calendlygui.constant;

public class ConstantValue {
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String NAME_PATTERN = "[\\p{L} ]+";
    public static final String NON_PRINTABLE_CHARACTER = "[^\\p{Print}\\p{Space}]";

    public static final String TIME_FORMAT = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    public static final String HOST_ADDRESS = "127.0.0.1";
    public static final String COMMAND_DELIMITER = ";";
    public static final String FIELD_DELIMITER = "~";
    public static final String LINE_BREAK = "<>";
    public static final String DOUBLE_LINE_BREAK = "<><>";

    //MESSAGES
    public static final int PORT = 3000;

    //COMMANDS
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String TEACHER_CREATE_MEETING = "TEACHER_CREATE_MEETING";
    public static final String TEACHER_EDIT_MEETING = "TEACHER_EDIT_MEETING";
    public static final String TEACHER_VIEW_MEETING_BY_DATE = "TEACHER_VIEW_MEETING_BY_DATE";
    public static final String TEACHER_ENTER_CONTENT = "TEACHER_ENTER_CONTENT";
    public static final String TEACHER_VIEW_HISTORY = "TEACHER_VIEW_HISTORY";
    public static final String STUDENT_VIEW_TIMESLOT = "STUDENT_VIEW_TIMESLOT";
    public static final String STUDENT_SCHEDULE_MEETING = "STUDENT_SCHEDULE_MEETING";
    public static final String STUDENT_VIEW_MEETING_BY_WEEK = "STUDENT_VIEW_MEETING_BY_WEEK";
    public static final String STUDENT_CANCEL_MEETING = "STUDENT_CANCEL_MEETING";
    public static final String QUIT = "QUIT";

    //FIELDS
    //Meeting
    public static final String MEETING = "meeting";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String MEETING_TEACHER_ID = "teacher_id";
    public static final String MEETING_OCCUR = "occur";
    public static final String MEETING_FINISH = "finish";
    public static final String STATUS = "status";
    public static final String PENDING = "pending";
    public static final String ACCEPT = "ready";
    public static final String GROUP = "group";
    public static final String INDIVIDUAL = "individual";
    public static final String CLASSIFICATION = "classification";
    public static final String SELECTED_CLASSIFICATION = "selected_classification";
    public static final String ESTABLISH_DATETIME = "establish_datetime";
    public static final String PARTICIPATE_DATETIME = "participate_datetime";
    public static final String TEACHER_NAME = "teacher_name";

    //Minute
    public static final String MINUTE = "minute";
    public static final String CONTENT = "content";
    public static final String MEETING_ID = "meeting_id";

    //Participate
    public static final String PARTICIPATE = "participate";
    public static final String STUDENT_ID = "student_id";

    //Users
    public static final String USERS = "Users";
    public static final String EMAIL = "email";
    public static final String GENDER = "gender";
    public static final String REGISTER_DATETIME = "register_datetime";
    public static final String IS_TEACHER = "is_teacher";


    //Code
    public static final int CREATE_SUCCESS = 10;
    public static final int QUERY_SUCCESS = 11;
    public static final int UPDATE_SUCCESS = 12;
    public static final int AUTHENTICATE_SUCCESS = 13;
    public static final int CLIENT_MISSING_INFO = 41;
    public static final int INCORRECT_FORMAT = 42;
    public static final int ACCOUNT_NOT_EXIST = 43;
    public static final int INVALID_PASSWORD = 44;
    public static final int ACCOUNT_EXIST = 45;
    public static final int UNDEFINED_ERROR = 46;

    public static final int IO_ERROR = 51;
    public static final int PARSE_ERROR = 52;
    public static final int NULL_ERROR = 53;
    public static final int NOT_UP_TO_DATE = 54;
    public static final int DUPLICATE_SCHEDULE = 55;

    public static final int SQL_ERROR = 61;


    //Message types
    public static final String SQL_EXCEPTION = "SQL Exception";
    public static final String UPDATE_DONE = "Update successfully";

    ////
    public static final int MAX_TIME_WAITING = 14;

    public static final int TIMESLOT_MIN_DURATION = 15;
    public static final int TIMESLOT_MAX_DURATION = 30;
}