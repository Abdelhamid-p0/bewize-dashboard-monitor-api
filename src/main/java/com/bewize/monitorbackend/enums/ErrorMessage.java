package com.bewize.monitorbackend.enums;

public enum ErrorMessage {

    // Auth 1000
    AUTH_BAD_CREDENTIALS("auth.emailpassword.incorrect", 1000),
    AUTH_DISABLED_USER("auth.disabled.user", 1008),
    REGISTER_CNE_EXISTS("register.cne.exists", 1004),
    REGISTER_EMAIL_EXISTS("register.email.exists", 1001),
    SAVE_EMAIL_EXIST("save.email.exists", 1002),
    SAVE_CNE_EXIST("save.cne.exists", 1005),
    SAVE_CNE_BACK_USER_EXISTS("save.cne.back.exists", 1007),
    USER_NOT_FOUND("none.existing.user", 1003),
    CHANGE_OLD_PASSWORD_INVALID("changePassword.oldPassword.invalid", 1006),
    // Resource
    SUBJECT_NOT_FOUND("none.existing.subject", 1100),
    COURSE_NOT_FOUND("none.existing.course", 1101),
    QUIZ_NOT_FOUND("none.existing.quiz", 1102),
    LEVEL_NOT_FOUND("none.existing.level",1103),
    SCHOOL_NOT_FOUND("none.existing.school",1104),
    CNE_BACK_USER_NOT_FOUND("save.cne.back.not.found", 1105),

    // Session
    SESSION_NOT_FINISHED("session.not.finished", 1200),
    SESSION_IS_FINISHED("session.already.finished", 1201),
    SESSION_NOT_FOUND("session.not.found", 1202),
    CANT_START_SESSION("quiz.not.found", 1203),
    NOT_STUDENT_SESSION("not.right.student", 1204),
    SESSION_RESPONSE_NOT_VALID("session.response.invalid", 1205),
    SESSION_QUIZID_NOT_VALID("session.quizid.invalid", 1206),

    // permission
    PERMISSION_DENIED("permission.denied", 4001),

    //Excel File
	NOT_EXCEL_FILE("not.excel.file", 3000),
	FAIL_PARSE_EXCEL_FILE("fail.parse.excel.file", 3001),
	INADEQUATE_TEMPLATE_EXCEL_FILE("inadequate.template.excel.file", 3002),
    INVALID_DATES_RANGE("invalid.dates.range", 3003),

    //Student
    STUDENT_NOT_FOUND("none.existing.student", 3004),

    STUDENT_NOT_FOUND_IN_SCHOOL("none.existing.student.in.school", 3005),
    CLASSROOM_NOT_FOUND("none.existing.classroom", 3006),
    INVALID_PAYLOAD("invalid.payload",3007 ),
    INVALID_PASSWORD( "invalid.password", 3008),
    INVALID_APPLE_PUBLIC_KEYS("invalid.apple.public.key", 3009),
    INVALID_TOKEN("invalid.token", 3010),
    UNABLE_TO_GENERATE_PUBLIC_KEY("unable.to.generate.public.key", 3011),
    UNABLE_TO_FETCH_USER_INFO_FROM_PROVIDER("unable.to.fetch.user.info.from.provider", 3012),
    PAYMENT_FAILED("payment.failed",3013),
    ORDER_NOT_FOUND("order.not.found",3014),
    SUBSCRIPTION_NOT_FOUND("subscription.not.found",3015),
    CLASS_ALREADY_EXIST("class.already.exist", 3016),

    //Notification 
    SEND_NOTIFICATION_FAILED("send.notification.failed", 4000);

    final private String message;
    final private Integer code;

    ErrorMessage(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
