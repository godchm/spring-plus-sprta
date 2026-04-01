package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;


import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.springframework.boot.origin.Origin.from;

@RequiredArgsConstructor
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory queryFactory;

    // 엔티티 기반 쿼리 dsl
//    @Override
//    public Optional<Todo> findByIdWithUser(Long todoId) {
//        QTodo todo = QTodo.todo;
//        QUser user = QUser.user;
//
//        Todo result = queryFactory
//                .selectFrom(todo)
//                .leftJoin(todo.user, user).fetchJoin()
//                .where(todo.id.eq(todoId))
//                .fetchOne();
//
//        return Optional.ofNullable(result);
//    }


    // List vs Optional
    // Optional로 하는 이유는 이 조회가 “단건 조회”이기 때문
    //
    // List<TodoResponse>를 사용하면
    // 실제 의미는 0개 또는 1개인데
    // 타입만 보면 여러 개 나올 수 있는 것처럼 보임
    // 그래서 단건 조회 의도가 퇴색된다.
    @Override
    public Optional<TodoResponse>findTodoResponseById(Long todoId){
        TodoResponse result = queryFactory
                .select(Projections.constructor(
                        TodoResponse.class,
                        todo.id,
                        todo.title,
                        todo.contents,
                        todo.weather,
                        Projections.constructor(
                                UserResponse.class,
                                user.id,
                                user.email
                        ),
                        todo.createdAt,
                        todo.modifiedAt
                ))
                 .from(todo)
                .leftJoin(todo.user, user)
                .where(todo.id.eq(todoId))
                .fetchOne();


        // result가 있으면 → Optional.of(result)
        // result가 null이면 → Optional.empty()
        // 즉 “값이 없을 수도 있는 단건 결과”를 표현.
        return Optional.ofNullable(result);
    }


}
