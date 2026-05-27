package com.actuary.ibps.common.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class IbpsConstants {

    private IbpsConstants() {
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final int MAX_QUERY_DAYS = 7;
    public static final int DEFAULT_CHUNK_SIZE = 1000;
    public static final long SLOW_THRESHOLD_MS = 800L;

    public static final String STATUS_SIGNED_IN = "SIGNED_IN";
    public static final String STATUS_SIGNED_OUT = "SIGNED_OUT";
    public static final String STATUS_ABNORMAL = "ABNORMAL";

    public static final String DIRECTION_IN = "IN";
    public static final String DIRECTION_OUT = "OUT";

    public static final String SEND_STATUS_PENDING = "PENDING";
    public static final String SEND_STATUS_SENT = "SENT";
    public static final String SEND_STATUS_FAILED = "FAILED";

    public static final String CERT_TYPE_CC00 = "CC00";
    public static final String CERT_TYPE_CC02 = "CC02";

    public static final String IMPORT_STATUS_PARSING = "PARSING";
    public static final String IMPORT_STATUS_INSERTING = "INSERTING";
    public static final String IMPORT_STATUS_DONE = "DONE";
    public static final String IMPORT_STATUS_FAILED = "FAILED";

    public static final String STEP_STATUS_SUCCESS = "SUCCESS";
    public static final String STEP_STATUS_FAIL = "FAIL";
    public static final String STEP_STATUS_TIMEOUT = "TIMEOUT";

    public static final List<String> STEP_RECV_LIST = Collections.unmodifiableList(
            Arrays.asList("RECV_INIT", "RECV_ANSW_601", "RECV_ANSW_900", "RECV_CANCEL",
                    "RECV_CANCEL_ANSW", "RECV_QUERY_ANSW", "ADJUST_INIT", "ASYN_TASK_INIT",
                    "TASK_INIT", "MANUAL_ENTRY_INIT"));

    public static final List<String> STEP_CHECK_LIST = Collections.unmodifiableList(
            Arrays.asList("CHECK_INIT", "PRE_ACCT_CHECK_SUCCESS", "PRE_ACCT_CHECK_FAIL",
                    "BUSI_CHECK_SUCCESS", "BUSI_CHECK_FAIL"));

    public static final List<String> STEP_ACCT_LIST = Collections.unmodifiableList(
            Arrays.asList("ACCT_INIT", "ACCT_SUCCESS", "ACCT_FAIL", "ACCT_TIMEOUT"));

    public static final List<String> STEP_SEND_LIST = Collections.unmodifiableList(
            Arrays.asList("SEND_INIT", "SEND_SUCCESS", "SEND_FAIL", "SEND_TIMEOUT"));

    public static final List<String> STEP_ROLLBACK_LIST = Collections.unmodifiableList(
            Arrays.asList("ROLLBACK_INIT", "ROLLBACK_SUCCESS", "ROLLBACK_FAIL", "ROLLBACK_TIMEOUT"));
}
