package com.example.demo.exception

enum class BoardErrorCode(
    override val message: String,
) : ErrorCode {
    INVALID_CATEGORY_NAME("유효하지 않은 카테고리 이름입니다."),
    INVALID_POST_TITLE("유효하지 않은 게시물 제목입니다."),
    INVALID_POST_CONTENT("유효하지 않는 게시물 본문입니다."),
    INVALID_POST_CATEGORY("유효하지 않은 게시물의 카테고리입니다."),
    INVALID_POST_SORT_PROPERTY("유효하지 않은 게시물 정렬 속성입니다."),
    NOT_POST_OWNER("게시물의 작성자가 아닙니다."),
    NOT_FOUND_CATEGORY("카테고리를 찾을 수 없습니다."),
    NOT_FOUND_POST("게시물을 찾을 수 없습니다.");
}