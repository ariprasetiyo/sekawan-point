package id.sekawan.point.util.mymodel;

public enum ResponseStatus {
    GENERAL_SUCCESS(100, "success"),
    GENERAL_FAILED(200, "failed"),
    GENERAL_NOT_FOUND(201, "not_found"),
    SUBSCRIPTION_HEAD_OFFICE_ID_NOT_FOUND(300, "head_office_id_not_found"),
    SUBSCRIPTION_NAME_ALREADY_EXISTS(301, "name_already_exists"),
    SUBSCRIPTION_HAS_ACTIVE_SUBSCRIBER(302, "has_active_subscriber"),
    SUBSCRIPTION_HEAD_OFFICE_MAIN_BALANCE_NOT_FOUND(303, "head_office_main_balance_not_found"),
    SUBSCRIPTION_INSUFFICIENT_BALANCE(304, "insufficient_balance"),
    SUBSCRIPTION_ALREADY_SUBSCRIBED(305, "already_subscribed"),
    SUBSCRIPTION_CANNOT_UNSUBSCRIBE_BEFORE_MAX_CYCLES(306, "cannot_unsubscribe_before_max_cycles"),
    SUBSCRIPTION_PENALTY_FEE_IS_EXCLUSIVE_TO_DAILY_TYPE(307, "penalty_fee_is_exclusive_to_daily_type");

    private final int code;
    private final String message;

    private ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
