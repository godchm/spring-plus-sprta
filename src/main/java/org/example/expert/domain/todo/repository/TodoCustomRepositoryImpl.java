package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
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

//     1. BooleanBuilder
    @Override
    public Page<TodoSearchResponse> getSearch(
            String title,
            LocalDateTime createdAt,
            String managerNickname,
            Pageable pageable
    ){


        BooleanBuilder builder = new BooleanBuilder();

        if (title != null && !title.isBlank()) {
            builder.and(todo.title.contains(title));
        }

        if (createdAt != null) {
            builder.and(todo.createdAt.goe(createdAt));
        }

        if (managerNickname != null && !managerNickname.isBlank()) {
            builder.and(user.nickname.contains(managerNickname));
        }

        // 단건 조회가 아니라 여러 개 결과를 반환하는 목록 조회를 하므로 List 선언.
        List<TodoSearchResponse> result=queryFactory
                .select(Projections.constructor(
                        TodoSearchResponse.class,
                        todo.title,
                        todo.createdAt,
                        manager.id.countDistinct(),
                        comment.id.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(builder)
                // 같은 Todo를 기준으로 묶어서, 담당자 수 댓글 수를 계산하겠다.
                .groupBy(todo.id, todo.title, todo.createdAt)
                // 생성일 최신순으로 하기위해 정렬한다. 이때 desc이면 내림차순, asc는 올림차순.
                .orderBy(todo.createdAt.desc())
                // 몇 번째 데이터부터 가져올지 결정
                .offset(pageable.getOffset())
                // 한 페이지에 몇개 가져올지 결정.
                .limit(pageable.getPageSize())
                .fetch();


        // 페이징 처리를 하려면 전체 개수도 구한다.
        Long total = queryFactory
                .select(todo.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(builder)
                .fetchOne();

      // PageImpl은 함수가 아니라 Spring Data에서 제공하는 Page 구현 클래스이다.
        return new PageImpl<>(result, pageable, total == null ? 0 : total);
    }



//    // 2. BooleanExpression. 메서드화해서 활용한다.
//    @Override
//    public Page<TodoSearchResponse> getSearch(
//            String title,
//            LocalDateTime createdAt,
//            String managerNickname,
//            Pageable pageable
//    )
//         // 단건 조회가 아니라 여러 개 결과를 반환하는 목록 조회를 하므로 List 선언.
//        List<TodoSearchResponse> result = queryFactory
//                .select(Projections.constructor(
//                        TodoSearchResponse.class,
//                        todo.title,
//                        todo.createdAt,
//                        manager.id.countDistinct(),
//                        comment.id.countDistinct()
//                ))
//                .from(todo)
//                .leftJoin(todo.managers, manager)
//                .leftJoin(manager.user, user)
//                .leftJoin(todo.comments, comment)
//                .where(
//                        titleContains(title),
//                        createdAtGoe(createdAt),
//                        managerNicknameContains(managerNickname)
//                )
//                .groupBy(todo.id, todo.title, todo.createdAt)
//                .orderBy(todo.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        Long total = queryFactory
//                .select(todo.countDistinct())
//                .from(todo)
//                .leftJoin(todo.managers, manager)
//                .leftJoin(manager.user, user)
//                .where(
//                        titleContains(title),
//                        createdAtGoe(createdAt),
//                        managerNicknameContains(managerNickname)
//                )
//                .fetchOne();
//
//        return new PageImpl<>(result, pageable, total == null ? 0 : total);
//    }
//    private BooleanExpression titleContains(String title) {
//        return (title == null || title.isBlank()) ? null : todo.title.contains(title);
//    }
//
//    private BooleanExpression createdAtGoe(LocalDateTime createdAt) {
//        return createdAt == null ? null : todo.createdAt.goe(createdAt);
//    }
//
//    private BooleanExpression managerNicknameContains(String managerNickname) {
//        return (managerNickname == null || managerNickname.isBlank())
//                ? null
//                : user.nickname.contains(managerNickname);
//    }


}
