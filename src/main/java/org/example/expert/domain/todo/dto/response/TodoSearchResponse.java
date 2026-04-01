package org.example.expert.domain.todo.dto.response;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final LocalDateTime createdAt;
    private final Long managerCount;
    private final Long commentCount;


    public TodoSearchResponse(String title, LocalDateTime createdAt, Long managerCount, Long commentCount) {
        this.title = title;
        this.createdAt = createdAt;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
