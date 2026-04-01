package org.example.expert.domain.todo.repository;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoCustomRepository {


//    Optional<Todo>findByIdWithUser(Long todoId);


    //
    Optional<TodoResponse>findTodoResponseById(Long todoId);


    Page<TodoSearchResponse> getSearch(
            String title,
            LocalDateTime createdAt,
            String managerNickname,
            Pageable pageable
    );

}
