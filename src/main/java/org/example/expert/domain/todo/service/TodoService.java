package org.example.expert.domain.todo.service;


import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }


    // 페이징 조회
    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(String weather, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos;

        if (weather == null && startDate == null && endDate == null) {
            todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        } else if (weather != null && startDate == null && endDate == null) {
            todos = todoRepository.searchByWeather(weather, pageable);
        } else if (weather == null && startDate != null && endDate == null) {
            todos = todoRepository.searchFrom(startDate, pageable);
        } else if (weather == null && startDate == null && endDate != null) {
            todos = todoRepository.searchTo(endDate, pageable);
        } else if (weather == null && startDate != null && endDate != null) {
            todos = todoRepository.searchPeriod(startDate, endDate, pageable);
        } else if (weather != null && startDate != null && endDate == null) {
            todos = todoRepository.searchWeatherFrom(weather, startDate, pageable);
        } else if (weather != null && startDate == null && endDate != null) {
            todos = todoRepository.searchWeatherTo(weather, endDate, pageable);
        } else {
            todos = todoRepository.searchWeatherPeriod(weather, startDate, endDate, pageable);
        }

        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {

        return todoRepository.findTodoResponseById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));
//        Todo todo = todoRepository.findTodoResponseById(todoId)
//                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

//        User user = todo.getUser();
//
//        return new TodoResponse(
//                todo.getId(),
//                todo.getTitle(),
//                todo.getContents(),
//                todo.getWeather(),
//                new UserResponse(user.getId(), user.getEmail()),
//                todo.getCreatedAt(),
//                todo.getModifiedAt()
//        )
    }
}
