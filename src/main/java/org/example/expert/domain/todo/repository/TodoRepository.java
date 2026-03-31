package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 모두 검색
    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);



    // weather 검색
    // :weather은 매개변수가 저 자리에 들어가서 조건절을 수행한다.
    //  t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.weather = :weather
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchByWeather(@Param("weather") String weather, Pageable pageable);


    // startDate만 검색
    // :startDate은 매개변수가 저 자리에 들어가서 조건절을 수행한다.
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.modifiedAt >= :startDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchFrom(@Param("startDate") LocalDateTime startDate, Pageable pageable);




    // endDate만 검색
    // :endDate 매개변수가 저 자리에 들어가서 조건절을 수행한다.
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.modifiedAt <= :endDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchTo(@Param("endDate") LocalDateTime endDate, Pageable pageable);


    // startDate, endDate
    // 조건절 startDate이상 endDate이하만
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.modifiedAt BETWEEN :startDate AND :endDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchPeriod(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate,
                            Pageable pageable);


    // weather 포함, modifiedAt이 startDate보다 같거나 이후인 데이터만 조회.
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.weather = :weather
      AND t.modifiedAt >= :startDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchWeatherFrom(@Param("weather") String weather,
                                 @Param("startDate") LocalDateTime startDate,
                                 Pageable pageable);


    // weather 포함, modifiedAt이 endDate보다 같거나 이전인 데이터만 조회
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.weather = :weather
      AND t.modifiedAt <= :endDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchWeatherTo(@Param("weather") String weather,
                               @Param("endDate") LocalDateTime endDate,
                               Pageable pageable);


    // (weather 포함) && (startDate<=modifiedAt<=endDate)
    // t,u를 조인하겟고, 이것을 t user id을 기준으로 user id와 같은것을 조인 하겟다
    @Query("""
    SELECT t FROM Todo t
    LEFT JOIN t.user u ON t.user.id = u.id
    WHERE t.weather = :weather
      AND t.modifiedAt BETWEEN :startDate AND :endDate
    ORDER BY t.modifiedAt DESC
""")
    Page<Todo> searchWeatherPeriod(@Param("weather") String weather,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);
}
