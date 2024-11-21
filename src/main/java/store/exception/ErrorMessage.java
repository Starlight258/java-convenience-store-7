package store.exception;

public enum ErrorMessage {

    ERROR_PREFIX("[ERROR] "),

    INVALID_ORDER_FORMAT("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    INVALID_FILE_FORMAT("파일 형식이 잘못되었습니다."),
    INVALID_DATE_FORMAT("유효한 날짜형식이 아닙니다."),
    INVALID_ANSWER_FORMAT("Y/N만 응답 가능합니다."),
    INVALID_INPUT("잘못된 입력입니다. 다시 입력해 주세요."),

    NULL("인자 값은 null일 수 없습니다."),
    BLANK("인자 값은 비어있거나 공백일 수 없습니다."),
    NULL_OR_BLANK("인자 값은 null이거나 공백일 수 없습니다"),
    NULL_OR_EMPTY("인자 값은 null이거나 비어있을 수 없습니다"),

    OUT_OF_STOCK("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");

    private final String content;

    ErrorMessage(final String content) {
        this.content = content;
    }

    public String getMessage() {
        return content;
    }
}
