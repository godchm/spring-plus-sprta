# SPRING PLUS

## AWS 활용
---

## Health Check
![img.png](img.png)

---

## EC2
![img_1.png](img_1.png)
![img_2.png](img_2.png)
![img_3.png](img_3.png)

---

## RDS
![img_4.png](img_4.png)
![img_5.png](img_5.png)

---

## S3
![img_6.png](img_6.png)
![img_7.png](img_7.png)

---

## IAM / Parameter Store
![img_8.png](img_8.png)
![img_9.png](img_9.png)

---

## 대용량 데이터 처리 

### 1. 테스트 데이터 생성 
![img_10.png](img_10.png)
![img_11.png](img_11.png)

### 2. 인덱스 튜닝 활용

#### (1). 그냥 조회 
![img_12.png](img_12.png)

#### (2). 단일 인덱스 조회
![img_13.png](img_13.png)

#### (3). 복합 인덱스 조회
![img_14.png](img_14.png)

---

### 3. 조회 성능 비교 
#### (1). 조회 성능 비교

| 구분 | 실행 시간 |
|---|---:|
| 인덱스 없음 | 1801 ms |
| 단일 인덱스 적용 | 0.73 ms |
| 복합 인덱스 적용 | 0.03 ms |

#### (2). 성능 개선 비율

| 구분 | 기준 대비 개선 |
|---|---:|
| 단일 인덱스 | 약 2,467배 |
| 복합 인덱스 | 약 60,033배 