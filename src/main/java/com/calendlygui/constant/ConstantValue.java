package com.calendlygui.constant;

public class ConstantValue {
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String NAME_PATTERN = "[\\p{L} ]+";
    public static final String HOST_ADDRESS = "127.0.0.1";
    public static final String COMMAND_DELIMITER = ";";
    public static final String FIELD_DELIMITER = "~";
    public static final String LINE_BREAK = "<>";
    public static final String DOUBLE_LINE_BREAK = "<><>";

    //MESSAGES
    public static final String SQL_ERROR = "SQL Connection Error";
    public static final String CLIENTSIDE_ERROR = "Client-side Error";
    public static final String SERVERSIDE_ERROR = "Server-side Error";
    public static final int PORT = 3000;
    public static final String LOGIN_EMAIL_NOT_EXIST = "Email does not exist";
    public static final String LOGIN_SERVER_WRONG = "Something fails at server";
    public static final String LOGIN_PASSWORD_NOT_MATCH = "Password does not match with email";
    public static final String LOGIN_REQUIRED_FIELD = "This field is required";

    public static final String CREATE_SUCCESS = "Create successfully";
    public static final String LOGIN_SUCCESS = "Login successfully";
    public static final String MISSING_INFO = "Missing information";
    public static final String INCORRECT_FORMAT = "Incorrect message format";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String DATA_FOUND = "Data found";

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
    public static final String ACCEPT = "accept";
    public static final String GROUP = "group";
    public static final String INDIVIDUAL = "individual";
    public static final String CLASSIFICATION = "classification";
    public static final String SELECTED_CLASSIFICATION = "selected_classification";
    public static final String ESTABLISH_DATETIME = "establish_datetime";
    public static final String PARTICIPATE_DATETIME = "participate_datetime";

    //Minute
    public static final String MINUTE = "minute";
    public static final String CONTENT = "content";
    public static final String MEETING_ID = "meeting_id";

    //Participate
    public static final String PARTICIPATE = "participate";
    public static final String STUDENT_ID = "student_id";

    //Users
    public static final String EMAIL = "email";
    public static final String GENDER = "gender";
    public static final String REGISTER_DATETIME = "register_datetime";
    public static final String IS_TEACHER = "is_teacher";


    //Message types
    public static final String SQL_EXCEPTION = "SQL Exception";

}