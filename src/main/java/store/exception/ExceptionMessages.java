package store.exception;

public enum ExceptionMessages {

    ERROR_PREFIX("[ERROR] "),
    WRONG_INPUT("잘못된 입력입니다. 다시 입력해 주세요."),
    NO_INPUT("아무것도 입력하지 않았습니다."),
    INVALID_FILE_FORMAT("파일 형식이 잘못되었습니다."),
    INVALID_FORMAT("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    NOT_NULL_ARGUMENT("인자 값은 null일 수 없습니다."),
    NOT_NULL_BLANK("인자 값은 null이거나 공백일 수 없습니다"),
    NOT_BLANK("인자 값은 비어있거나 공백일 수 없습니다.");

    private String content;

    ExceptionMessages(final java.lang.String content) {
        this.content = content;
    }

    public String getErrorMessage() {
        return ERROR_PREFIX.content + this.content;
    }
}
