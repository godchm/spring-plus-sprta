package org.example.expert.domain.user;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.concurrent.ThreadLocalRandom;

@JdbcTest
@ActiveProfiles("localtest")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class BulkUserInsertTest {

    private static final Logger log = LoggerFactory.getLogger(BulkUserInsertTest.class);

    private static final int TOTAL_COUNT = 5_000_000;
    private static final int BATCH_SIZE = 10_000;
    private static final String RUN_PREFIX = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 36);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 유저_500만건을_JDBC_배치로_생성한다() {
        createUsersTableIfNotExists();

        String sql = """
                INSERT INTO users (id, email, nickname)
                VALUES (?, ?, ?)
                """;

        long startedAt = System.currentTimeMillis();

        for (int start = 0; start < TOTAL_COUNT; start += BATCH_SIZE) {
            int batchStart = start;
            int batchEnd = Math.min(start + BATCH_SIZE, TOTAL_COUNT);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int index) throws java.sql.SQLException {
                    int sequence = batchStart + index;
                    String uniqueSuffix = Integer.toString(sequence, 36);
                    String email = "bulk-user-" + RUN_PREFIX + "-" + uniqueSuffix + "@example.com";
                    String nickname = "user_" + RUN_PREFIX + "_" + uniqueSuffix;

                    ps.setNull(1, Types.BIGINT);
                    ps.setString(2, email);
                    ps.setString(3, nickname);
                }

                @Override
                public int getBatchSize() {
                    return batchEnd - batchStart;
                }
            });

            if (batchEnd % 100_000 == 0 || batchEnd == TOTAL_COUNT) {
                log.info("bulk insert progress: {} / {}", batchEnd, TOTAL_COUNT);
            }
        }

        long elapsed = System.currentTimeMillis() - startedAt;
        log.info("bulk insert completed: totalCount={}, elapsedMs={}", TOTAL_COUNT, elapsed);
    }

    private void createUsersTableIfNotExists() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    email VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NULL,
                    user_role VARCHAR(50) NULL,
                    profile_image_key VARCHAR(255) NULL,
                    nickname VARCHAR(255) NULL,
                    created_at DATETIME(6) NULL,
                    modified_at DATETIME(6) NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY uk_users_email (email)
                )
                """);
    }
}
