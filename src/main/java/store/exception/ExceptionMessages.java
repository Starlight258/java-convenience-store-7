package store.exception;

public enum ExceptionMessages {

    ERROR_PREFIX("[ERROR] "),
    WRONG_INPUT("잘못된 입력입니다. 다시 입력해 주세요."),
    INVALID_FILE_FORMAT("파일 형식이 잘못되었습니다."),
    INVALID_FORMAT("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    NOT_NULL_ARGUMENT("인자 값은 null일 수 없습니다."),
    NOT_NULL_BLANK("인자 값은 null이거나 공백일 수 없습니다"),
    NOT_NULL_EMPTY("인자 값은 null이거나 비어있을 수 없습니다"),
    NOT_BLANK("인자 값은 비어있거나 공백일 수 없습니다."),
    NOT_EXIST_PRODUCT("존재하지 않는 상품입니다. 다시 입력해 주세요."),
    OUT_OF_STOCK("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."),
    NO_PROMOTION_PRODUCT("프로모션이 없는 상품을 찾을 수 없습니다."),
    CANNOT_BUY_PRODUCT("상품을 구매할 수 없습니다.");

    private final String content;

    ExceptionMessages(final java.lang.String content) {
        this.content = content;
    }

    public String getMessageWithPrefix() {
        return ERROR_PREFIX.content + this.content;
    }

    public String getMessage() {
        return content;
    }
}
