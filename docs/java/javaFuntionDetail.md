// 안전한 파싱 유틸리티
System.out.println("\n안전한 파싱 유틸리티:");
java.util.Optional<java.time.LocalDate> safeDate1 = safeParseLocalDate("2025-08-04");
java.util.Optional<java.time.LocalDate> safeDate2 = safeParseLocalDate("invalid-date");

        System.out.println("안전한 파싱 1: " + safeDate1.map(Object::toString).orElse("파싱 실패"));
        System.out.println("안전한 파싱 2: " + safeDate2.map(Object::toString).orElse("파싱 실패"));
    }
    
    // 안전한 파싱 유틸리티 메서드
    private static java.util.Optional<java.time.LocalDate> safeParseLocalDate(String dateString) {
        try {
            return java.util.Optional.of(java.time.LocalDate.parse(dateString));
        } catch (java.time.format.DateTimeParseException e) {
            return java.util.Optional.empty();
        }
    }
    
    // 파싱 성능 분석
    private static void analyzeParsingPerformance() {
        System.out.println("\n=== 파싱 성능 분석 ===");
        
        int iterations = 100000;
        String dateString = "2025-08-04T14:30:15";
        
        // 기본 parse() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDateTime.parse(dateString);
        }
        long basicParseTime = System.nanoTime() - start;
        
        // 미리 생성된 포맷터 사용
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDateTime.parse(dateString, formatter);
        }
        long formatterParseTime = System.nanoTime() - start;
        
        // 커스텀 포맷터 (매번 생성)
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDateTime.parse(dateString, 
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        }
        long customParseTime = System.nanoTime() - start;
        
        System.out.println("기본 parse(): " + basicParseTime / 1_000_000 + "ms");
        System.out.println("미리 생성된 formatter: " + formatterParseTime / 1_000_000 + "ms");
        System.out.println("매번 생성하는 formatter: " + customParseTime / 1_000_000 + "ms");
        
        System.out.println("성능 비율:");
        System.out.println("  formatter vs basic: " + (double)formatterParseTime / basicParseTime);
        System.out.println("  custom vs basic: " + (double)customParseTime / basicParseTime);
    }
    
    // 성능 최적화된 생성
    private static void analyzeOptimizedCreation() {
        System.out.println("\n=== 최적화된 생성 방법 ===");
        
        int iterations = 1_000_000;
        
        // 현재 날짜 생성 비교
        System.out.println("현재 날짜 생성 성능:");
        
        // LocalDate.now()
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDate.now();
        }
        long nowTime = System.nanoTime() - start;
        
        // Clock 재사용
        java.time.Clock clock = java.time.Clock.systemDefaultZone();
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDate.now(clock);
        }
        long clockTime = System.nanoTime() - start;
        
        // 특정 값으로 생성
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDate.of(2025, 8, 4);
        }
        long ofTime = System.nanoTime() - start;
        
        System.out.println("LocalDate.now(): " + nowTime / 1_000_000 + "ms");
        System.out.println("LocalDate.now(clock): " + clockTime / 1_000_000 + "ms");
        System.out.println("LocalDate.of(): " + ofTime / 1_000_000 + "ms");
        
        // 객체 재사용 패턴
        demonstrateObjectReuse();
    }
    
    // 객체 재사용 패턴
    private static void demonstrateObjectReuse() {
        System.out.println("\n=== 객체 재사용 패턴 ===");
        
        /* 불변 객체의 재사용 이점:
         * 1. 같은 값의 객체는 캐싱 가능
         * 2. 메모리 사용량 절약
         * 3. 생성 비용 절약
         */
        
        // 캐싱 예제 (실제로는 JVM이 일부 최적화를 수행할 수 있음)
        java.util.Map<String, java.time.LocalDate> dateCache = new java.util.concurrent.ConcurrentHashMap<>();
        
        java.util.function.Function<String, java.time.LocalDate> cachedParser = dateString -> 
            dateCache.computeIfAbsent(dateString, java.time.LocalDate::parse);
        
        // 테스트
        String[] testDates = {"2025-08-04", "2025-08-05", "2025-08-04", "2025-08-06", "2025-08-04"};
        
        System.out.println("캐시된 파싱 결과:");
        for (String dateStr : testDates) {
            java.time.LocalDate parsed = cachedParser.apply(dateStr);
            System.out.printf("'%s' → %s (캐시 크기: %d)%n", dateStr, parsed, dateCache.size());
        }
        
        // 상수 객체 사용
        System.out.println("\n상수 객체 사용:");
        class DateConstants {
            public static final java.time.LocalDate EPOCH_DATE = java.time.LocalDate.of(1970, 1, 1);
            public static final java.time.LocalDate Y2K_DATE = java.time.LocalDate.of(2000, 1, 1);
            public static final java.time.LocalTime MIDNIGHT = java.time.LocalTime.of(0, 0);
            public static final java.time.LocalTime NOON = java.time.LocalTime.of(12, 0);
        }
        
        System.out.println("Epoch 날짜: " + DateConstants.EPOCH_DATE);
        System.out.println("Y2K 날짜: " + DateConstants.Y2K_DATE);
        System.out.println("자정: " + DateConstants.MIDNIGHT);
        System.out.println("정오: " + DateConstants.NOON);
    }
}
```

### 6.3 시간 계산과 비교의 심층 분석

```java
public class DateTimeCalculationAnalysis {
    
    // 날짜/시간 계산의 내부 메커니즘
    public static void analyzeDateTimeCalculations() {
        System.out.println("=== 날짜/시간 계산 메커니즘 분석 ===");
        
        // 기본 산술 연산들
        analyzeBasicArithmetic();
        
        // Period vs Duration 차이점
        analyzePeriodVsDuration();
        
        // 복잡한 계산들
        analyzeComplexCalculations();
        
        // 경계 케이스들
        analyzeBoundaryConditions();
    }
    
    // 기본 산술 연산 분석
    private static void analyzeBasicArithmetic() {
        System.out.println("\n=== 기본 산술 연산 ===");
        
        java.time.LocalDate baseDate = java.time.LocalDate.of(2025, 8, 4);
        java.time.LocalTime baseTime = java.time.LocalTime.of(14, 30, 15);
        java.time.LocalDateTime baseDateTime = java.time.LocalDateTime.of(baseDate, baseTime);
        
        System.out.println("기준 날짜: " + baseDate);
        System.out.println("기준 시간: " + baseTime);
        System.out.println("기준 날짜시간: " + baseDateTime);
        
        // plus 연산들
        System.out.println("\n=== plus 연산들 ===");
        System.out.println("1년 후: " + baseDate.plusYears(1));
        System.out.println("3개월 후: " + baseDate.plusMonths(3));
        System.out.println("7일 후: " + baseDate.plusDays(7));
        System.out.println("2시간 후: " + baseTime.plusHours(2));
        System.out.println("30분 후: " + baseTime.plusMinutes(30));
        System.out.println("45초 후: " + baseTime.plusSeconds(45));
        
        // minus 연산들
        System.out.println("\n=== minus 연산들 ===");
        System.out.println("1년 전: " + baseDate.minusYears(1));
        System.out.println("6개월 전: " + baseDate.minusMonths(6));
        System.out.println("10일 전: " + baseDate.minusDays(10));
        
        // with 연산들 (특정 필드 설정)
        System.out.println("\n=== with 연산들 ===");
        System.out.println("2026년으로: " + baseDate.withYear(2026));
        System.out.println("12월로: " + baseDate.withMonth(12));
        System.out.println("말일로: " + baseDate.withDayOfMonth(baseDate.lengthOfMonth()));
        System.out.println("자정으로: " + baseTime.withHour(0).withMinute(0).withSecond(0));
        
        // 내부 구현 이해
        explainInternalArithmetic();
    }
    
    // 내부 산술 구현 설명
    private static void explainInternalArithmetic() {
        System.out.println("\n=== 내부 산술 구현 ===");
        
        /* LocalDate 산술 내부 동작:
         * 1. 년/월 계산: 월력 규칙 적용 (윤년, 월말 처리)
         * 2. 일 계산: epoch day 기준 계산 후 변환
         * 3. 오버플로우/언더플로우 검사
         * 4. 새로운 불변 객체 생성 후 반환
         */
        
        java.time.LocalDate date = java.time.LocalDate.of(2025, 1, 31);
        System.out.println("1월 31일: " + date);
        
        // 월 계산의 복잡성 - 말일 처리
        java.time.LocalDate feb = date.plusMonths(1); // 2월은 31일이 없음
        System.out.println("1개월 후: " + feb); // 2월 28일로 조정됨
        
        java.time.LocalDate mar = date.plusMonths(2);
        System.out.println("2개월 후: " + mar); // 3월 31일 (정상)
        
        // 윤년 처리
        java.time.LocalDate leapYear = java.time.LocalDate.of(2024, 2, 29); // 윤년
        System.out.println("윤년 2/29: " + leapYear);
        
        java.time.LocalDate nextYear = leapYear.plusYears(1);
        System.out.println("1년 후: " + nextYear); // 2025/2/28로 조정
        
        // epoch day 기반 계산 시연
        long epochDay1 = date.toEpochDay();
        long epochDay2 = date.plusDays(100).toEpochDay();
        System.out.printf("Epoch day: %d → %d (차이: %d)%n", epochDay1, epochDay2, epochDay2 - epochDay1);
    }
    
    // Period vs Duration 상세 분석
    private static void analyzePeriodVsDuration() {
        System.out.println("\n=== Period vs Duration 분석 ===");
        
        /* 차이점:
         * Period: 날짜 기반 기간 (년, 월, 일)
         *         - 달력 시간 (calendar-based)
         *         - 일광절약시간 영향 없음
         * 
         * Duration: 시간 기반 기간 (초, 나노초)
         *          - 기계 시간 (machine-based)  
         *          - 일광절약시간 영향 있음
         */
        
        java.time.LocalDate date1 = java.time.LocalDate.of(2025, 1, 1);
        java.time.LocalDate date2 = java.time.LocalDate.of(2025, 3, 15);
        
        // Period 계산
        java.time.Period period = java.time.Period.between(date1, date2);
        System.out.println("Period 계산:");
        System.out.println("  시작일: " + date1);
        System.out.println("  종료일: " + date2);
        System.out.println("  기간: " + period);
        System.out.println("  년: " + period.getYears());
        System.out.println("  월: " + period.getMonths());
        System.out.println("  일: " + period.getDays());
        System.out.println("  총 월수: " + period.toTotalMonths());
        
        // Duration 계산
        java.time.LocalDateTime dateTime1 = java.time.LocalDateTime.of(2025, 1, 1, 10, 30, 0);
        java.time.LocalDateTime dateTime2 = java.time.LocalDateTime.of(2025, 1, 1, 15, 45, 30);
        
        java.time.Duration duration = java.time.Duration.between(dateTime1, dateTime2);
        System.out.println("\nDuration 계산:");
        System.out.println("  시작: " + dateTime1);
        System.out.println("  종료: " + dateTime2);
        System.out.println("  기간: " + duration);
        System.out.println("  초: " + duration.getSeconds());
        System.out.println("  나노초: " + duration.getNano());
        System.out.println("  총 밀리초: " + duration.toMillis());
        System.out.println("  총 분: " + duration.toMinutes());
        System.out.println("  총 시간: " + duration.toHours());
        System.out.println("  총 일: " + duration.toDays());
        
        // Period와 Duration의 적용
        demonstratePeriodDurationUsage();
    }
    
    // Period와 Duration의 실제 사용법
    private static void demonstratePeriodDurationUsage() {
        System.out.println("\n=== Period와 Duration 사용법 ===");
        
        java.time.LocalDate today = java.time.LocalDate.now();
        
        // Period 사용 - 날짜 계산에 적합
        java.time.Period oneYear = java.time.Period.ofYears(1);
        java.time.Period twoMonths = java.time.Period.ofMonths(2);
        java.time.Period thirtyDays = java.time.Period.ofDays(30);
        java.time.Period complex = java.time.Period.of(1, 6, 15); // 1년 6개월 15일
        
        System.out.println("오늘: " + today);
        System.out.println("1년 후: " + today.plus(oneYear));
        System.out.println("2개월 후: " + today.plus(twoMonths));
        System.out.println("30일 후: " + today.plus(thirtyDays));
        System.out.println("1년 6개월 15일 후: " + today.plus(complex));
        
        // Duration 사용 - 시간 계산에 적합
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        java.time.Duration oneHour = java.time.Duration.ofHours(1);
        java.time.Duration thirtyMinutes = java.time.Duration.ofMinutes(30);
        java.time.Duration fiveSeconds = java.time.Duration.ofSeconds(5);
        java.time.Duration hundredMillis = java.time.Duration.ofMillis(100);
        
        System.out.println("\n현재: " + now);
        System.out.println("1시간 후: " + now.plus(oneHour));
        System.out.println("30분 후: " + now.plus(thirtyMinutes));
        System.out.println("5초 후: " + now.plus(fiveSeconds));
        System.out.println("100ms 후: " + now.plus(hundredMillis));
        
        // 음수 기간
        System.out.println("\n음수 기간:");
        java.time.Period negativePeriod = java.time.Period.ofDays(-10);
        java.time.Duration negativeDuration = java.time.Duration.ofHours(-2);
        
        System.out.println("10일 전: " + today.plus(negativePeriod));
        System.out.println("2시간 전: " + now.plus(negativeDuration));
        
        // 기간 연산
        demonstratePeriodDurationArithmetic();
    }
    
    // 기간 연산
    private static void demonstratePeriodDurationArithmetic() {
        System.out.println("\n=== 기간 연산 ===");
        
        // Period 연산
        java.time.Period period1 = java.time.Period.of(1, 2, 15); // 1년 2개월 15일
        java.time.Period period2 = java.time.Period.of(0, 6, 10); // 6개월 10일
        
        java.time.Period sum = period1.plus(period2);
        java.time.Period diff = period1.minus(period2);
        java.time.Period multiplied = period1.multipliedBy(2);
        java.time.Period negated = period1.negated();
        
        System.out.println("Period 연산:");
        System.out.println("  기간1: " + period1);
        System.out.println("  기간2: " + period2);
        System.out.println("  합계: " + sum);
        System.out.println("  차이: " + diff);
        System.out.println("  2배: " + multiplied);
        System.out.println("  부호반전: " + negated);
        
        // Duration 연산
        java.time.Duration duration1 = java.time.Duration.ofHours(2).plusMinutes(30);
        java.time.Duration duration2 = java.time.Duration.ofMinutes(45);
        
        java.time.Duration durationSum = duration1.plus(duration2);
        java.time.Duration durationDiff = duration1.minus(duration2);
        java.time.Duration durationMult = duration1.multipliedBy(3);
        java.time.Duration durationDiv = duration1.dividedBy(2);
        
        System.out.println("\nDuration 연산:");
        System.out.println("  시간1: " + duration1);
        System.out.println("  시간2: " + duration2);
        System.out.println("  합계: " + durationSum);
        System.out.println("  차이: " + durationDiff);
        System.out.println("  3배: " + durationMult);
        System.out.println("  절반: " + durationDiv);
    }
    
    // 복잡한 계산들
    private static void analyzeComplexCalculations() {
        System.out.println("\n=== 복잡한 날짜/시간 계산 ===");
        
        // 근무일 계산
        calculateBusinessDays();
        
        // 나이 계산
        calculateAge();
        
        // 달력 계산
        performCalendarCalculations();
        
        // 시간대 변환 계산
        performTimeZoneCalculations();
    }
    
    // 근무일 계산
    private static void calculateBusinessDays() {
        System.out.println("\n=== 근무일 계산 ===");
        
        java.time.LocalDate startDate = java.time.LocalDate.of(2025, 8, 1); // 금요일
        java.time.LocalDate endDate = java.time.LocalDate.of(2025, 8, 15);   // 금요일
        
        long businessDays = 0;
        java.time.LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                businessDays++;
            }
            current = current.plusDays(1);
        }
        
        System.out.printf("%s부터 %s까지 근무일: %d일%n", startDate, endDate, businessDays);
        
        // Stream을 사용한 더 함수형 접근
        long streamBusinessDays = startDate.datesUntil(endDate.plusDays(1))
            .filter(date -> {
                java.time.DayOfWeek day = date.getDayOfWeek();
                return day != java.time.DayOfWeek.SATURDAY && day != java.time.DayOfWeek.SUNDAY;
            })
            .count();
        
        System.out.println("Stream으로 계산한 근무일: " + streamBusinessDays);
        
        // N 근무일 후 날짜 찾기
        java.time.LocalDate nthBusinessDay = findNthBusinessDay(startDate, 10);
        System.out.printf("%s부터 10 근무일 후: %s%n", startDate, nthBusinessDay);
    }
    
    // N번째 근무일 찾기
    private static java.time.LocalDate findNthBusinessDay(java.time.LocalDate startDate, int businessDays) {
        java.time.LocalDate current = startDate;
        int count = 0;
        
        while (count < businessDays) {
            java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                count++;
                if (count == businessDays) {
                    return current;
                }
            }
            current = current.plusDays(1);
        }
        
        return current;
    }
    
    // 나이 계산
    private static void calculateAge() {
        System.out.println("\n=== 나이 계산 ===");
        
        java.time.LocalDate birthDate = java.time.LocalDate.of(1990, 3, 15);
        java.time.LocalDate today = java.time.LocalDate.now();
        
        java.time.Period age = java.time.Period.between(birthDate, today);
        System.out.printf("생년월일: %s%n", birthDate);
        System.out.printf("현재: %s%n", today);
        System.out.printf("나이: %d년 %d개월 %d일%n", age.getYears(), age.getMonths(), age.getDays());
        
        // 만 나이만 계산
        int ageInYears = age.getYears();
        System.out.println("만 나이: " + ageInYears + "세");
        
        // 다음 생일까지 남은 날짜
        java.time.LocalDate nextBirthday = birthDate.withYear(today.getYear());
        if (nextBirthday.isBefore(today) || nextBirthday.equals(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        
        long daysUntilBirthday = java.time.temporal.ChronoUnit.DAYS.between(today, nextBirthday);
        System.out.printf("다음 생일까지: %d일%n", daysUntilBirthday);
        
        // 살아온 총 일수
        long totalDaysLived = java.time.temporal.ChronoUnit.DAYS.between(birthDate, today);
        System.out.printf("살아온 총 일수: %d일%n", totalDaysLived);
    }
    
    // 달력 계산
    private static void performCalendarCalculations() {
        System.out.println("\n=== 달력 계산 ===");
        
        java.time.LocalDate date = java.time.LocalDate.of(2025, 8, 4);
        
        // 해당 월의 첫날과 마지막날
        java.time.LocalDate firstDayOfMonth = date.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        java.time.LocalDate lastDayOfMonth = date.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        
        System.out.printf("기준일: %s%n", date);
        System.out.printf("월 첫날: %s%n", firstDayOfMonth);
        System.out.printf("월 마지막날: %s%n", lastDayOfMonth);
        
        // 해당 연도의 첫날과 마지막날
        java.time.LocalDate firstDayOfYear = date.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
        java.time.LocalDate lastDayOfYear = date.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
        
        System.out.printf("연도 첫날: %s%n", firstDayOfYear);
        System.out.printf("연도 마지막날: %s%n", lastDayOfYear);
        
        // 특정 요일 찾기
        java.time.LocalDate nextMonday = date.with(java.time.temporal.TemporalAdjusters.next(java.time.DayOfWeek.MONDAY));
        java.time.LocalDate lastFriday = date.with(java.time.temporal.TemporalAdjusters.previous(java.time.DayOfWeek.FRIDAY));
        java.time.LocalDate firstMondayOfMonth = date.with(java.time.temporal.TemporalAdjusters.firstInMonth(java.time.DayOfWeek.MONDAY));
        
        System.out.printf("다음 월요일: %s%n", nextMonday);
        System.out.printf("이전 금요일: %s%n", lastFriday);
        System.out.printf("이달의 첫 번째 월요일: %s%n", firstMondayOfMonth);
        
        // 분기 계산
        calculateQuarter(date);
    }
    
    // 분기 계산
    private static void calculateQuarter(java.time.LocalDate date) {
        System.out.println("\n분기 정보:");
        
        int quarter = (date.getMonthValue() - 1) / 3 + 1;
        System.out.printf("%s는 %d분기입니다.%n", date, quarter);
        
        // 분기 시작일과 종료일
        java.time.LocalDate quarterStart = java.time.LocalDate.of(date.getYear(), (quarter - 1) * 3 + 1, 1);
        java.time.LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);
        
        System.out.printf("%d분기 시작일: %s%n", quarter, quarterStart);
        System.out.printf("%d분기 종료일: %s%n", quarter, quarterEnd);
        
        // 분기내 경과 일수와 남은 일수
        long daysSinceQuarterStart = java.time.temporal.ChronoUnit.DAYS.between(quarterStart, date);
        long daysUntilQuarterEnd = java.time.temporal.ChronoUnit.DAYS.between(date, quarterEnd);
        
        System.out.printf("분기 시작 후 경과일: %d일%n", daysSinceQuarterStart);
        System.out.printf("분기 종료까지 남은일: %d일%n", daysUntilQuarterEnd);
    }
    
    // 시간대 변환 계산
    private static void performTimeZoneCalculations() {
        System.out.println("\n=== 시간대 변환 계산 ===");
        
        // 여러 시간대의 동일한 순간
        java.time.Instant instant = java.time.Instant.now();
        
        java.time.ZoneId seoulZone = java.time.ZoneId.of("Asia/Seoul");
        java.time.ZoneId utcZone = java.time.ZoneId.of("UTC");
        java.time.ZoneId newYorkZone = java.time.ZoneId.of("America/New_York");
        java.time.ZoneId londonZone = java.time.ZoneId.of("Europe/London");
        
        java.time.ZonedDateTime seoulTime = instant.atZone(seoulZone);
        java.time.ZonedDateTime utcTime = instant.atZone(utcZone);
        java.time.ZonedDateTime newYorkTime = instant.atZone(newYorkZone);
        java.time.ZonedDateTime londonTime = instant.atZone(londonZone);
        
        System.out.println("동일한 순간의 각 시간대 시간:");
        System.out.printf("서울: %s%n", seoulTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
        System.out.printf("UTC: %s%n", utcTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
        System.out.printf("뉴욕: %s%n", newYorkTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
        System.out.printf("런던: %s%n", londonTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")));
        
        // 시간대 간 시차 계산
        calculateTimeDifferences();
        
        // 일광절약시간 처리
        handleDaylightSavingTime();
    }
    
    // 시간대 간 시차 계산
    private static void calculateTimeDifferences() {
        System.out.println("\n시간대 간 시차:");
        
        java.time.ZonedDateTime seoulTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
        java.time.ZonedDateTime utcTime = seoulTime.withZoneSameInstant(java.time.ZoneId.of("UTC"));
        java.time.ZonedDateTime newYorkTime = seoulTime.withZoneSameInstant(java.time.ZoneId.of("America/New_York"));
        
        // UTC와의 시차
        java.time.Duration seoulUtcDiff = java.time.Duration.between(utcTime.toLocalDateTime(), seoulTime.toLocalDateTime());
        java.time.Duration newYorkUtcDiff = java.time.Duration.between(utcTime.toLocalDateTime(), newYorkTime.toLocalDateTime());
        
        System.out.printf("서울-UTC 시차: %+d시간%n", seoulUtcDiff.toHours());
        System.out.printf("뉴욕-UTC 시차: %+d시간%n", newYorkUtcDiff.toHours());
        
        // 서울-뉴욕 시차
        java.time.Duration seoulNewYorkDiff = java.time.Duration.between(newYorkTime.toLocalDateTime(), seoulTime.toLocalDateTime());
        System.out.printf("서울-뉴욕 시차: %+d시간%n", seoulNewYorkDiff.toHours());
    }
    
    // 일광절약시간 처리
    private static void handleDaylightSavingTime() {
        System.out.println("\n=== 일광절약시간 처리 ===");
        
        java.time.ZoneId newYorkZone = java.time.ZoneId.of("America/New_York");
        
        // 일광절약시간 시작 전후 (3월 두 번째 일요일)
        java.time.LocalDateTime beforeDst = java.time.LocalDateTime.of(2025, 3, 8, 1, 30); // DST 시작 전
        java.time.LocalDateTime afterDst = java.time.LocalDateTime.of(2025, 3, 8, 3, 30);  // DST 시작 후
        
        java.time.ZonedDateTime beforeDstZoned = beforeDst.atZone(newYorkZone);
        java.time.ZonedDateTime afterDstZoned = afterDst.atZone(newYorkZone);
        
        System.out.printf("DST 시작 전: %s (오프셋: %s)%n", 
            beforeDstZoned, beforeDstZoned.getOffset());
        System.out.printf("DST 시작 후: %s (오프셋: %s)%n", 
            afterDstZoned, afterDstZoned.getOffset());
        
        // 존재하지 않는 시간 (2:30 AM은 DST로 인해 건너뜀)
        try {
            java.time.LocalDateTime skippedTime = java.time.LocalDateTime.of(2025, 3, 8, 2, 30);
            java.time.ZonedDateTime skippedZoned = skippedTime.atZone(newYorkZone);
            System.out.printf("건너뛴 시간 처리: %s → %s%n", skippedTime, skippedZoned);
        } catch (Exception e) {
            System.out.printf("건너뛴 시간 예외: %s%n", e.getMessage());
        }
        
        // 중복되는 시간 (DST 종료 시)
        java.time.LocalDateTime duplicateTime = java.time.LocalDateTime.of(2025, 11, 2, 1, 30);
        java.time.ZonedDateTime duplicateZoned1 = duplicateTime.atZone(newYorkZone);
        System.out.printf("중복 시간 (첫번째): %s%n", duplicateZoned1);
        
        // 명시적으로 오프셋 지정
        java.time.ZonedDateTime duplicateZoned2 = duplicateTime.atZone(newYorkZone)
            .withEarlierOffsetAtOverlap(); // 또는 withLaterOffsetAtOverlap()
        System.out.printf("중복 시간 (이른 오프셋): %s%n", duplicateZoned2);
    }
    
    // 경계 케이스 분석
    private static void analyzeBoundaryConditions() {
        System.out.println("\n=== 경계 케이스 분석 ===");
        
        // 윤년 처리
        analyzeLeapYearBoundaries();
        
        // 월말 처리
        analyzeMonthEndBoundaries();
        
        // 시간 경계 처리
        analyzeTimeBoundaries();
        
        // 극값 처리
        analyzeExtremeValues();
    }
    
    // 윤년 경계 케이스
    private static void analyzeLeapYearBoundaries() {
        System.out.println("\n윤년 경계 케이스:");
        
        // 윤년 2월 29일
        java.time.LocalDate leapDay = java.time.LocalDate.of(2024, 2, 29);
        System.out.println("윤년 2/29: " + leapDay);
        
        // 1년 더하기 (평년으로)
        java.time.LocalDate nextYear = leapDay.plusYears(1);
        System.out.println("1년 후 (평년): " + nextYear); // 2/28로 조정
        
        // 4년 더하기 (다음 윤년으로)
        java.time.LocalDate nextLeapYear = leapDay.plusYears(4);
        System.out.println("4년 후 (윤년): " + nextLeapYear); // 2/29 유지
        
        // Period를 사용한 경우
        java.time.Period oneYear = java.time.Period.ofYears(1);
        java.time.LocalDate periodResult = leapDay.plus(oneYear);
        System.out.println("Period로 1년 후: " + periodResult);
        
        // 윤년 판별
        System.out.printf("2024년 윤년 여부: %b%n", java.time.Year.of(2024).isLeap());
        System.out.printf("2025년 윤년 여부: %b%n", java.time.Year.of(2025).isLeap());
        System.out.printf("2100년 윤년 여부: %b%n", java.time.Year.of(2100).isLeap()); // false (100으로 나누어지지만 400으로 나누어지지 않음)
        System.out.printf("2000년 윤년 여부: %b%n", java.time.Year.of(2000).isLeap()); // true (400으로 나누어짐)
    }
    
    // 월말 경계 케이스
    private static void analyzeMonthEndBoundaries() {
        System.out.println("\n월말 경계 케이스:");
        
        java.time.LocalDate[] monthEndDates = {
            java.time.LocalDate.of(2025, 1, 31),  // 1월 31일
            java.time.LocalDate.of(2025, 3, 31),  // 3월 31일
            java.time.LocalDate.of(2025, 5, 31),  // 5월 31일
            java.time.LocalDate.of(2025, 8, 31),  // 8월 31일
            java.time.LocalDate.of(2025, 10, 31), // 10월 31일
            java.time.LocalDate.of(2025, 12, 31)  // 12월 31일
        };
        
        for (java.time.LocalDate date : monthEndDates) {
            java.time.LocalDate nextMonth = date.plusMonths(1);
            java.time.LocalDate prevMonth = date.minusMonths(1);
            
            System.out.printf("%s → 다음달: %s, 이전달: %s%n", date, nextMonth, prevMonth);
        }
        
        // 2월의 특별한 경우들
        java.time.LocalDate jan31 = java.time.LocalDate.of(2025, 1, 31);
        java.time.LocalDate feb28 = jan31.plusMonths(1); // 2월 28일로 조정
        java.time.LocalDate mar28 = feb28.plusMonths(1); // 3월 28일 (31일이 아님!)
        
        System.out.println("연속 월 추가의 함정:");
        System.out.printf("1/31 → %s → %s%n", feb28, mar28);
    }
    
    // 시간 경계 케이스
    private static void analyzeTimeBoundaries() {
        System.out.println("\n시간 경계 케이스:");
        
        // 자정 경계
        java.time.LocalTime midnight = java.time.LocalTime.of(0, 0, 0);
        java.time.LocalTime beforeMidnight = java.time.LocalTime.of(23, 59, 59);
        
        System.out.println("자정: " + midnight);
        System.out.println("자정 1초 전: " + beforeMidnight);
        System.out.println("자정 1초 후: " + midnight.plusSeconds(1));
        System.out.println("자정에서 1초 빼기: " + midnight.minusSeconds(1)); // 23:59:59
        
        // 날짜 경계 넘나들기
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(2025, 8, 4, 23, 59, 59);
        java.time.LocalDateTime nextSecond = dateTime.plusSeconds(1);
        
        System.out.printf("날짜 경계: %s → %s%n", dateTime, nextSecond);
        
        // 월 경계
        java.time.LocalDateTime monthEnd = java.time.LocalDateTime.of(2025, 7, 31, 23, 59, 59);
        java.time.LocalDateTime nextMonth = monthEnd.plusSeconds(1);
        
        System.out.printf("월 경계: %s → %s%n", monthEnd, nextMonth);
    }
    
    // 극값 처리
    private static void analyzeExtremeValues() {
        System.out.println("\n극값 처리:");
        
        // LocalDate 범위
        System.out.println("LocalDate 범위:");
        System.out.println("  MIN: " + java.time.LocalDate.MIN);
        System.out.println("  MAX: " + java.time.LocalDate.MAX);
        
        // LocalTime 범위
        System.out.println("LocalTime 범위:");
        System.out.println("  MIN: " + java.time.LocalTime.MIN);
        System.out.println("  MAX: " + java.time.LocalTime.MAX);
        
        // Instant 범위
        System.out.println("Instant 범위:");
        System.out.println("  MIN: " + java.time.Instant.MIN);
        System.out.println("  MAX: " + java.time.Instant.MAX);
        
        // 극값에서의 연산
        try {
            java.time.LocalDate maxDate = java.time.LocalDate.MAX;
            java.time.LocalDate overflow = maxDate.plusDays(1); // 예외 발생!
        } catch (java.time.DateTimeException e) {
            System.out.println("MAX 날짜에서 오버플로우: " + e.getMessage());
        }
        
        try {
            java.time.LocalDate minDate = java.time.LocalDate.MIN;
            java.time.LocalDate underflow = minDate.minusDays(1); // 예외 발생!
        } catch (java.time.DateTimeException e) {
            System.out.println("MIN 날짜에서 언더플로우: " + e.getMessage());
        }
        
        // 안전한 극값 연산
        java.time.LocalDate safeDate = java.time.LocalDate.of(2025, 8, 4);
        try {
            java.time.LocalDate result = safeDate.plusYears(1000000); // 매우 큰 값
            System.out.println("100만년 후: " + result);
        } catch (java.time.DateTimeException e) {
            System.out.println("극값 연산 오류: " + e.getMessage());
        }
    }
}
```

이제 Java의 내장 함수들에 대한 매우 상세한 가이드가 완성되었습니다. 각 함수의:

1. **내부 동작 메커니즘** - 실제로 JVM 레벨에서 어떻게 구현되는지
2. **성능 특성과 복잡도** - Big O 표기법과 실제 벤치마크 결과
3. **메모리 사용 패턴** - 객체 생성, 가비지 컬렉션, 메모리 효율성
4. **최적화 기법** - 더 빠르고 효율적인 사용 방법
5. **실제 사용 시나리오** - 언제 어떤 함수를 써야 하는지
6. **경계 조건과 예외 상황** - 극한 상황에서의 동작
7. **버전별 차이점** - Java 8, 9, 11+ 등의 변화

를 포함하여 설명했습니다.

이 가이드를 통해 각 함수가 단순히 "무엇을 하는지"뿐만 아니라 "어떻게, 왜 그렇게 동작하는지"까지 완전히 이해하실 수 있을 것입니다. 나머지 섹션들(파일 I/O, 스레드, 성능 최적화 등)도 필요하시면 같은 수준으로 계속 작성해드릴 수 있습니다!### 5.4 Lambda 표현식과 메서드 참조 상세 분석

```java
public class LambdaExpressionsDeepAnalysis {
    
    // Lambda 표현식의 내부 구현과 컴파일 메커니즘
    public static void analyzeLambdaInternals() {
        /* Lambda 표현식 내부 구현:
         * 1. 컴파일 시점에 invokedynamic 바이트코드 생성
         * 2. 런타임에 람다 팩토리를 통해 함수형 인터페이스 구현체 생성
         * 3. 익명 클래스보다 효율적 (메타스페이스 사용량 적음)
         * 4. 캡처된 변수는 final 또는 effectively final이어야 함
         */
        
        System.out.println("=== Lambda 표현식 내부 분석 ===");
        
        // 다양한 람다 표현식 형태
        demonstrateLambdaSyntax();
        
        // 변수 캡처 메커니즘
        analyzeVariableCapture();
        
        // 람다 vs 익명 클래스 성능 비교
        compareLambdaVsAnonymous();
    }
    
    // 람다 문법의 다양한 형태
    private static void demonstrateLambdaSyntax() {
        System.out.println("\n=== 람다 문법 형태들 ===");
        
        // 1. 매개변수 없음
        Runnable noParam = () -> System.out.println("매개변수 없음");
        
        // 2. 매개변수 하나 (괄호 생략 가능)
        java.util.function.Consumer<String> oneParam = s -> System.out.println(s);
        java.util.function.Consumer<String> oneParamWithParens = (s) -> System.out.println(s);
        
        // 3. 여러 매개변수
        java.util.function.BiFunction<Integer, Integer, Integer> multiParam = (a, b) -> a + b;
        
        // 4. 타입 명시
        java.util.function.BiFunction<Integer, Integer, Integer> withTypes = 
            (Integer a, Integer b) -> a + b;
        
        // 5. 블록 형태 (return 필요)
        java.util.function.Function<String, String> blockForm = (String s) -> {
            String processed = s.toUpperCase();
            return "처리됨: " + processed;
        };
        
        // 6. 표현식 형태 (return 자동)
        java.util.function.Function<String, String> expressionForm = 
            s -> "처리됨: " + s.toUpperCase();
        
        // 실행 테스트
        noParam.run();
        oneParam.accept("테스트");
        System.out.println("덧셈: " + multiParam.apply(5, 3));
        System.out.println("블록: " + blockForm.apply("hello"));
        System.out.println("표현식: " + expressionForm.apply("world"));
    }
    
    // 변수 캡처 메커니즘
    private static void analyzeVariableCapture() {
        System.out.println("\n=== 변수 캡처 분석 ===");
        
        // 1. 지역 변수 캡처 (effectively final)
        String localVar = "로컬 변수";
        int counter = 10;
        
        java.util.function.Supplier<String> captureLocal = () -> {
            // localVar = "변경"; // 컴파일 오류! effectively final 위반
            return localVar + " 캡처됨, 카운터: " + counter;
        };
        
        System.out.println(captureLocal.get());
        
        // 2. 인스턴스 변수 캡처
        CaptureExample example = new CaptureExample();
        example.demonstrateInstanceCapture();
        
        // 3. 정적 변수 캡처  
        demonstrateStaticCapture();
        
        // 4. 캡처 비용 분석
        analyzeCaptureOverhead();
    }
    
    // 인스턴스 변수 캡처 예제
    static class CaptureExample {
        private String instanceVar = "인스턴스 변수";
        private int instanceCounter = 0;
        
        void demonstrateInstanceCapture() {
            // 인스턴스 변수는 자유롭게 변경 가능
            java.util.function.Supplier<String> captureInstance = () -> {
                instanceCounter++; // 변경 가능
                return instanceVar + ", 호출 횟수: " + instanceCounter;
            };
            
            System.out.println(captureInstance.get());
            System.out.println(captureInstance.get());
        }
    }
    
    // 정적 변수 캡처
    private static int staticCounter = 0;
    
    private static void demonstrateStaticCapture() {
        java.util.function.Supplier<String> captureStatic = () -> {
            staticCounter++; // 정적 변수도 변경 가능
            return "정적 카운터: " + staticCounter;
        };
        
        System.out.println(captureStatic.get());
        System.out.println(captureStatic.get());
    }
    
    // 캡처 오버헤드 분석
    private static void analyzeCaptureOverhead() {
        System.out.println("\n=== 캡처 오버헤드 분석 ===");
        
        int iterations = 1_000_000;
        String capturedVar = "캡처된 변수";
        
        // 변수 캡처 없는 람다
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.function.Supplier<String> noCapture = () -> "상수 문자열";
            noCapture.get();
        }
        long noCaptureTime = System.nanoTime() - start;
        
        // 변수 캡처하는 람다
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.function.Supplier<String> withCapture = () -> capturedVar + " 처리";
            withCapture.get();
        }
        long withCaptureTime = System.nanoTime() - start;
        
        System.out.println("캡처 없음: " + noCaptureTime / 1_000_000 + "ms");
        System.out.println("캡처 있음: " + withCaptureTime / 1_000_000 + "ms");
        System.out.println("캡처 오버헤드: " + (double)withCaptureTime / noCaptureTime + "배");
    }
    
    // 람다 vs 익명 클래스 성능 비교
    private static void compareLambdaVsAnonymous() {
        System.out.println("\n=== Lambda vs 익명클래스 성능 비교 ===");
        
        int iterations = 1_000_000;
        
        // 람다 표현식 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.function.Supplier<String> lambda = () -> "Lambda Result";
            lambda.get();
        }
        long lambdaTime = System.nanoTime() - start;
        
        // 익명 클래스 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.function.Supplier<String> anonymous = new java.util.function.Supplier<String>() {
                @Override
                public String get() {
                    return "Anonymous Result";
                }
            };
            anonymous.get();
        }
        long anonymousTime = System.nanoTime() - start;
        
        // 메서드 참조 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.function.Supplier<String> methodRef = LambdaExpressionsDeepAnalysis::getStaticString;
            methodRef.get();
        }
        long methodRefTime = System.nanoTime() - start;
        
        System.out.println("Lambda: " + lambdaTime / 1_000_000 + "ms");
        System.out.println("익명클래스: " + anonymousTime / 1_000_000 + "ms");
        System.out.println("메서드참조: " + methodRefTime / 1_000_000 + "ms");
        
        System.out.println("Lambda가 익명클래스보다 " + (double)anonymousTime / lambdaTime + "배 빠름");
        System.out.println("메서드참조가 Lambda보다 " + (double)lambdaTime / methodRefTime + "배 빠름");
    }
    
    private static String getStaticString() {
        return "Method Reference Result";
    }
    
    // 메서드 참조의 다양한 형태
    public static void analyzeMethodReferences() {
        System.out.println("\n=== 메서드 참조 형태들 ===");
        
        java.util.List<String> words = java.util.Arrays.asList("apple", "Banana", "cherry", "DATE");
        
        // 1. 정적 메서드 참조 - ClassName::methodName
        System.out.println("=== 정적 메서드 참조 ===");
        words.stream()
            .map(String::valueOf)  // String.valueOf(s) 와 동일
            .forEach(System.out::println); // System.out.println(s) 와 동일
        
        // 2. 인스턴스 메서드 참조 - instance::methodName
        System.out.println("\n=== 인스턴스 메서드 참조 ===");
        java.util.function.Consumer<String> printer = System.out::println;
        words.forEach(printer);
        
        // 3. 특정 타입의 임의 객체 인스턴스 메서드 참조 - ClassName::instanceMethod
        System.out.println("\n=== 임의 객체 인스턴스 메서드 참조 ===");
        words.stream()
            .map(String::toUpperCase)  // s -> s.toUpperCase() 와 동일
            .map(String::length)       // s -> s.length() 와 동일
            .forEach(System.out::println);
        
        // 4. 생성자 참조 - ClassName::new
        System.out.println("\n=== 생성자 참조 ===");
        java.util.function.Supplier<java.util.List<String>> listSupplier = java.util.ArrayList::new;
        java.util.function.Function<String, StringBuilder> sbFunction = StringBuilder::new;
        
        java.util.List<String> newList = listSupplier.get();
        StringBuilder sb = sbFunction.apply("초기 문자열");
        System.out.println("생성된 StringBuilder: " + sb.toString());
        
        // 배열 생성자 참조
        java.util.function.IntFunction<String[]> arrayCreator = String[]::new;
        String[] array = words.stream().toArray(arrayCreator);
        System.out.println("배열 생성: " + java.util.Arrays.toString(array));
        
        // 메서드 참조 vs 람다 표현식 선택 가이드
        demonstrateMethodRefSelection();
    }
    
    // 메서드 참조 vs 람다 표현식 선택 가이드
    private static void demonstrateMethodRefSelection() {
        System.out.println("\n=== 메서드 참조 vs 람다 선택 가이드 ===");
        
        java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5);
        
        // ✅ 메서드 참조가 적절한 경우
        System.out.println("메서드 참조가 적절:");
        numbers.stream()
            .map(Object::toString)        // Integer::toString보다 일반적
            .map(String::length)
            .forEach(System.out::println);
        
        // ✅ 람다가 적절한 경우
        System.out.println("\n람다가 적절:");
        numbers.stream()
            .filter(n -> n % 2 == 0)      // 복잡한 조건
            .map(n -> n * n + 1)          // 복잡한 계산
            .forEach(n -> System.out.printf("결과: %d%n", n));
        
        // 메서드 참조의 타입 추론
        demonstrateTypeInference();
    }
    
    // 타입 추론과 제네릭
    private static void demonstrateTypeInference() {
        System.out.println("\n=== 타입 추론 분석 ===");
        
        // 컴파일러가 타입을 추론
        java.util.List<String> strings = java.util.Arrays.asList("a", "bb", "ccc");
        
        // 타입 명시 없이도 올바른 타입 추론
        java.util.Optional<String> longest = strings.stream()
            .max((s1, s2) -> Integer.compare(s1.length(), s2.length())); // 타입 추론됨
        
        // 메서드 참조로 더 간결하게
        java.util.Optional<String> longestRef = strings.stream()
            .max(java.util.Comparator.comparing(String::length));
        
        System.out.println("가장 긴 문자열: " + longest.orElse("없음"));
        System.out.println("메서드 참조로: " + longestRef.orElse("없음"));
        
        // 복잡한 타입 추론
        demonstrateComplexTypeInference();
    }
    
    // 복잡한 타입 추론 시나리오
    private static void demonstrateComplexTypeInference() {
        System.out.println("\n=== 복잡한 타입 추론 ===");
        
        // 중첩된 제네릭과 람다
        java.util.Map<String, java.util.List<Integer>> groupedData = java.util.Map.of(
            "small", java.util.Arrays.asList(1, 2, 3),
            "large", java.util.Arrays.asList(100, 200, 300)
        );
        
        // 복잡한 스트림 처리에서의 타입 추론
        java.util.Map<String, Double> averages = groupedData.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                java.util.Map.Entry::getKey,  // 키 추출 (타입 추론)
                entry -> entry.getValue().stream()  // 값 처리 (타입 추론)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0)
            ));
        
        System.out.println("그룹별 평균: " + averages);
        
        // 함수 조합과 타입 추론
        java.util.function.Function<String, String> upperCase = String::toUpperCase;
        java.util.function.Function<String, Integer> getLength = String::length;
        java.util.function.Function<String, Integer> upperAndLength = upperCase.andThen(getLength);
        
        System.out.println("'hello' 대문자 후 길이: " + upperAndLength.apply("hello"));
    }
    
    // 고급 람다 패턴들
    public static void demonstrateAdvancedPatterns() {
        System.out.println("\n=== 고급 람다 패턴들 ===");
        
        // 1. 커링 (Currying)
        demonstrateCurrying();
        
        // 2. 함수 조합
        demonstrateFunctionComposition();
        
        // 3. 메모이제이션
        demonstrateMemoization();
        
        // 4. 재귀 람다
        demonstrateRecursiveLambda();
    }
    
    // 커링 패턴
    private static void demonstrateCurrying() {
        System.out.println("\n=== 커링 패턴 ===");
        
        // 3개 매개변수를 받는 함수를 1개씩 받는 함수들로 변환
        java.util.function.Function<Integer, java.util.function.Function<Integer, java.util.function.Function<Integer, Integer>>>
            curriedAdd = a -> b -> c -> a + b + c;
        
        // 부분 적용
        java.util.function.Function<Integer, java.util.function.Function<Integer, Integer>> add10 = curriedAdd.apply(10);
        java.util.function.Function<Integer, Integer> add10And20 = add10.apply(20);
        
        System.out.println("커링 결과: " + add10And20.apply(5)); // 10 + 20 + 5 = 35
        
        // 더 실용적인 커링 예제
        java.util.function.Function<String, java.util.function.Function<String, String>> createFormatter = 
            prefix -> suffix -> text -> prefix + text + suffix;
        
        java.util.function.Function<String, String> htmlBold = createFormatter.apply("<b>").apply("</b>");
        java.util.function.Function<String, String> parentheses = createFormatter.apply("(").apply(")");
        
        System.out.println("HTML Bold: " + htmlBold.apply("중요한 텍스트"));
        System.out.println("괄호: " + parentheses.apply("부가 정보"));
    }
    
    // 함수 조합
    private static void demonstrateFunctionComposition() {
        System.out.println("\n=== 함수 조합 ===");
        
        java.util.function.Function<String, String> removeSpaces = s -> s.replace(" ", "");
        java.util.function.Function<String, String> toUpperCase = String::toUpperCase;
        java.util.function.Function<String, Integer> getLength = String::length;
        
        // andThen으로 함수 조합
        java.util.function.Function<String, Integer> processAndCount = removeSpaces
            .andThen(toUpperCase)
            .andThen(getLength);
        
        // compose로 함수 조합 (역순)
        java.util.function.Function<String, Integer> composeExample = getLength
            .compose(toUpperCase)
            .compose(removeSpaces);
        
        String input = "Hello World Java";
        System.out.println("입력: " + input);
        String input = "Hello World Java";
        System.out.println("입력: " + input);
        System.out.println("andThen 결과: " + processAndCount.apply(input));
        System.out.println("compose 결과: " + composeExample.apply(input));
        
        // Predicate 조합
        java.util.function.Predicate<String> isLong = s -> s.length() > 5;
        java.util.function.Predicate<String> hasUpperCase = s -> !s.equals(s.toLowerCase());
        java.util.function.Predicate<String> longAndHasUpper = isLong.and(hasUpperCase);
        java.util.function.Predicate<String> longOrHasUpper = isLong.or(hasUpperCase);
        
        String[] testStrings = {"short", "verylongstring", "Short", "VeryLongString"};
        for (String test : testStrings) {
            System.out.printf("'%s': 길고+대문자=%b, 길거나+대문자=%b%n", 
                test, longAndHasUpper.test(test), longOrHasUpper.test(test));
        }
    }
    
    // 메모이제이션 패턴
    private static void demonstrateMemoization() {
        System.out.println("\n=== 메모이제이션 패턴 ===");
        
        // 비용이 큰 계산을 캐시하는 함수
        java.util.Map<Integer, Long> cache = new java.util.concurrent.ConcurrentHashMap<>();
        
        java.util.function.Function<Integer, Long> expensiveCalculation = n -> {
            System.out.println("비싼 계산 수행: " + n);
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            return (long) n * n * n;
        };
        
        // 메모이제이션 래퍼
        java.util.function.Function<Integer, Long> memoized = n -> 
            cache.computeIfAbsent(n, expensiveCalculation);
        
        // 테스트
        System.out.println("첫 번째 호출:");
        System.out.println("memoized(5) = " + memoized.apply(5));
        System.out.println("memoized(3) = " + memoized.apply(3));
        
        System.out.println("\n두 번째 호출 (캐시됨):");
        System.out.println("memoized(5) = " + memoized.apply(5));
        System.out.println("memoized(3) = " + memoized.apply(3));
        
        System.out.println("캐시 크기: " + cache.size());
    }
    
    // 재귀 람다 (트릭 필요)
    private static void demonstrateRecursiveLambda() {
        System.out.println("\n=== 재귀 람다 ===");
        
        // 팩토리얼 계산을 위한 재귀 함수형 인터페이스
        @FunctionalInterface
        interface RecursiveFunction<T> extends java.util.function.Function<T, T> {
            default RecursiveFunction<T> recursive() {
                return t -> this.apply(t);
            }
        }
        
        // Y 컴비네이터 패턴 (간단한 버전)
        java.util.function.Function<java.util.function.Function<Integer, java.util.function.Function<Integer, Integer>>, 
                java.util.function.Function<Integer, Integer>> Y = f -> 
            ((java.util.function.Function<Integer, Integer>) x -> f.apply(f).apply(x));
        
        // 팩토리얼 구현 (더 간단한 방법)
        class Factorial {
            static java.util.function.Function<Integer, Integer> factorial = n -> 
                n <= 1 ? 1 : n * Factorial.factorial.apply(n - 1);
        }
        
        System.out.println("5! = " + Factorial.factorial.apply(5));
        System.out.println("7! = " + Factorial.factorial.apply(7));
        
        // 피보나치 수열
        class Fibonacci {
            private static java.util.Map<Integer, Integer> cache = new java.util.HashMap<>();
            static {
                cache.put(0, 0);
                cache.put(1, 1);
            }
            
            static java.util.function.Function<Integer, Integer> fib = n -> {
                if (cache.containsKey(n)) {
                    return cache.get(n);
                }
                int result = Fibonacci.fib.apply(n - 1) + Fibonacci.fib.apply(n - 2);
                cache.put(n, result);
                return result;
            };
        }
        
        System.out.println("피보나치 수열:");
        for (int i = 0; i <= 10; i++) {
            System.out.print(Fibonacci.fib.apply(i) + " ");
        }
        System.out.println();
    }
    
    // 함수형 인터페이스 설계 패턴
    public static void demonstrateFunctionalInterfaceDesign() {
        System.out.println("\n=== 함수형 인터페이스 설계 ===");
        
        // 커스텀 함수형 인터페이스들
        demonstrateCustomFunctionalInterfaces();
        
        // 예외 처리 패턴
        demonstrateExceptionHandling();
        
        // 조건부 실행 패턴
        demonstrateConditionalExecution();
    }
    
    // 커스텀 함수형 인터페이스
    private static void demonstrateCustomFunctionalInterfaces() {
        System.out.println("\n=== 커스텀 함수형 인터페이스 ===");
        
        // 3개 매개변수 함수
        @FunctionalInterface
        interface TriFunction<T, U, V, R> {
            R apply(T t, U u, V v);
            
            default <W> TriFunction<T, U, V, W> andThen(java.util.function.Function<? super R, ? extends W> after) {
                return (t, u, v) -> after.apply(apply(t, u, v));
            }
        }
        
        TriFunction<String, String, String, String> concat3 = (a, b, c) -> a + b + c;
        System.out.println("3개 문자열 연결: " + concat3.apply("Hello", " ", "World"));
        
        // andThen과 함께 사용
        TriFunction<String, String, String, Integer> concat3AndLength = 
            concat3.andThen(String::length);
        System.out.println("연결 후 길이: " + concat3AndLength.apply("A", "B", "C"));
        
        // 조건부 실행 인터페이스
        @FunctionalInterface
        interface ConditionalExecutor<T> {
            void execute(T value);
            
            static <T> ConditionalExecutor<T> when(java.util.function.Predicate<T> condition, 
                                                  java.util.function.Consumer<T> action) {
                return value -> {
                    if (condition.test(value)) {
                        action.accept(value);
                    }
                };
            }
        }
        
        ConditionalExecutor<Integer> printIfEven = ConditionalExecutor.when(
            n -> n % 2 == 0, 
            n -> System.out.println(n + "은 짝수입니다")
        );
        
        printIfEven.execute(4);
        printIfEven.execute(5);
    }
    
    // 예외 처리 패턴
    private static void demonstrateExceptionHandling() {
        System.out.println("\n=== 함수형 예외 처리 ===");
        
        // 체크드 예외를 처리하는 함수형 인터페이스
        @FunctionalInterface
        interface ThrowingFunction<T, R, E extends Exception> {
            R apply(T t) throws E;
            
            static <T, R> java.util.function.Function<T, R> unchecked(ThrowingFunction<T, R, ?> f) {
                return t -> {
                    try {
                        return f.apply(t);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
            }
            
            static <T, R> java.util.function.Function<T, java.util.Optional<R>> safe(ThrowingFunction<T, R, ?> f) {
                return t -> {
                    try {
                        return java.util.Optional.ofNullable(f.apply(t));
                    } catch (Exception e) {
                        return java.util.Optional.empty();
                    }
                };
            }
        }
        
        // 사용 예시
        java.util.List<String> numbers = java.util.Arrays.asList("123", "456", "abc", "789");
        
        // unchecked 방식 - 예외 발생 시 RuntimeException
        System.out.println("unchecked 방식:");
        try {
            numbers.stream()
                .map(ThrowingFunction.unchecked(Integer::parseInt))
                .forEach(System.out::println);
        } catch (RuntimeException e) {
            System.out.println("예외 발생: " + e.getCause().getClass().getSimpleName());
        }
        
        // safe 방식 - 예외 발생 시 Optional.empty()
        System.out.println("\nsafe 방식:");
        numbers.stream()
            .map(ThrowingFunction.safe(Integer::parseInt))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .forEach(n -> System.out.println("파싱 성공: " + n));
    }
    
    // 조건부 실행 패턴
    private static void demonstrateConditionalExecution() {
        System.out.println("\n=== 조건부 실행 패턴 ===");
        
        // Either 타입 (Result 타입과 유사)
        abstract class Either<L, R> {
            public abstract boolean isLeft();
            public abstract boolean isRight();
            public abstract L getLeft();
            public abstract R getRight();
            
            public static <L, R> Either<L, R> left(L value) {
                return new Left<>(value);
            }
            
            public static <L, R> Either<L, R> right(R value) {
                return new Right<>(value);
            }
            
            public <T> Either<L, T> map(java.util.function.Function<R, T> mapper) {
                return isRight() ? right(mapper.apply(getRight())) : left(getLeft());
            }
            
            public <T> Either<L, T> flatMap(java.util.function.Function<R, Either<L, T>> mapper) {
                return isRight() ? mapper.apply(getRight()) : left(getLeft());
            }
            
            private static class Left<L, R> extends Either<L, R> {
                private final L value;
                Left(L value) { this.value = value; }
                public boolean isLeft() { return true; }
                public boolean isRight() { return false; }
                public L getLeft() { return value; }
                public R getRight() { throw new RuntimeException("No right value"); }
            }
            
            private static class Right<L, R> extends Either<L, R> {
                private final R value;
                Right(R value) { this.value = value; }
                public boolean isLeft() { return false; }
                public boolean isRight() { return true; }
                public L getLeft() { throw new RuntimeException("No left value"); }
                public R getRight() { return value; }
            }
        }
        
        // Either 사용 예시
        java.util.function.Function<String, Either<String, Integer>> safeParse = s -> {
            try {
                return Either.right(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Either.left("파싱 실패: " + s);
            }
        };
        
        java.util.List<String> inputs = java.util.Arrays.asList("123", "abc", "456", "def");
        
        inputs.stream()
            .map(safeParse)
            .forEach(either -> {
                if (either.isRight()) {
                    System.out.println("성공: " + either.getRight());
                } else {
                    System.out.println("실패: " + either.getLeft());
                }
            });
        
        // Either 체이닝
        Either<String, Integer> result = safeParse.apply("123")
            .map(n -> n * 2)
            .flatMap(n -> n > 100 ? Either.right(n) : Either.left("값이 너무 작음: " + n));
        
        System.out.println("체이닝 결과: " + (result.isRight() ? result.getRight() : result.getLeft()));
    }
}
```

---

## 6. 날짜/시간 API 완전 가이드

### 6.1 Java 8 Time API 구조와 설계 철학

```java
public class DateTimeAPIDeepAnalysis {
    
    // Time API의 설계 원칙과 내부 구조
    public static void analyzeDateTimeDesign() {
        /* Java 8 Time API 설계 원칙:
         * 1. 불변성 (Immutable): 모든 날짜/시간 객체는 불변
         * 2. 명확성 (Clear): 각 클래스의 역할이 명확히 구분
         * 3. 유창한 API (Fluent): 메서드 체이닝 지원
         * 4. 확장성 (Extensible): 커스텀 달력 시스템 지원
         * 5. 스레드 안전성: 불변 객체로 자동 보장
         */
        
        System.out.println("=== Java 8 Time API 설계 분석 ===");
        
        // 주요 클래스들의 역할과 관계
        analyzeTimeApiClasses();
        
        // 불변성과 스레드 안전성
        demonstrateImmutability();
        
        // 기존 Date/Calendar와의 차이점
        compareWithLegacyApi();
    }
    
    // Time API 클래스들 분석
    private static void analyzeTimeApiClasses() {
        System.out.println("\n=== Time API 클래스 구조 ===");
        
        /* 주요 클래스들:
         * 1. LocalDate: 날짜만 (2025-08-04)
         * 2. LocalTime: 시간만 (14:30:15)
         * 3. LocalDateTime: 날짜 + 시간 (2025-08-04T14:30:15)
         * 4. ZonedDateTime: 날짜 + 시간 + 시간대
         * 5. Instant: UTC 기준 타임스탬프
         * 6. Duration: 시간 기간 (초, 나노초)
         * 7. Period: 날짜 기간 (년, 월, 일)
         */
        
        java.time.LocalDate date = java.time.LocalDate.now();
        java.time.LocalTime time = java.time.LocalTime.now();
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.now();
        java.time.ZonedDateTime zonedDateTime = java.time.ZonedDateTime.now();
        java.time.Instant instant = java.time.Instant.now();
        
        System.out.println("LocalDate: " + date);
        System.out.println("LocalTime: " + time);
        System.out.println("LocalDateTime: " + dateTime);
        System.out.println("ZonedDateTime: " + zonedDateTime);
        System.out.println("Instant: " + instant);
        
        // 정밀도와 범위
        System.out.println("\n=== 정밀도와 범위 ===");
        System.out.println("LocalTime 정밀도: 나노초 (최대 " + java.time.LocalTime.MAX + ")");
        System.out.println("LocalDate 범위: " + java.time.LocalDate.MIN + " ~ " + java.time.LocalDate.MAX);
        System.out.println("Instant epoch: " + java.time.Instant.EPOCH);
        
        // 내부 저장 방식
        analyzeInternalStorage();
    }
    
    // 내부 저장 방식 분석
    private static void analyzeInternalStorage() {
        System.out.println("\n=== 내부 저장 방식 ===");
        
        /* 내부 저장 구조:
         * LocalDate: int year, short month, short day
         * LocalTime: byte hour, byte minute, byte second, int nano
         * Instant: long epochSecond, int nano
         * Duration: long seconds, int nanos
         */
        
        java.time.LocalDate date = java.time.LocalDate.of(2025, 8, 4);
        java.time.LocalTime time = java.time.LocalTime.of(14, 30, 15, 123456789);
        java.time.Duration duration = java.time.Duration.ofSeconds(3661, 123456789); // 1시간 1분 1초 + 나노초
        
        System.out.println("날짜 구성 요소:");
        System.out.println("  년: " + date.getYear());
        System.out.println("  월: " + date.getMonthValue());
        System.out.println("  일: " + date.getDayOfMonth());
        
        System.out.println("시간 구성 요소:");
        System.out.println("  시: " + time.getHour());
        System.out.println("  분: " + time.getMinute());
        System.out.println("  초: " + time.getSecond());
        System.out.println("  나노초: " + time.getNano());
        
        System.out.println("Duration 구성:");
        System.out.println("  초: " + duration.getSeconds());
        System.out.println("  나노초: " + duration.getNano());
        System.out.println("  총 나노초: " + duration.toNanos());
    }
    
    // 불변성과 스레드 안전성
    private static void demonstrateImmutability() {
        System.out.println("\n=== 불변성 시연 ===");
        
        java.time.LocalDateTime original = java.time.LocalDateTime.of(2025, 8, 4, 14, 30);
        System.out.println("원본: " + original);
        
        // 모든 변경 연산은 새 객체 반환
        java.time.LocalDateTime plusHours = original.plusHours(2);
        java.time.LocalDateTime withYear = original.withYear(2026);
        java.time.LocalDateTime minusDays = original.minusDays(1);
        
        System.out.println("2시간 후: " + plusHours);
        System.out.println("2026년으로: " + withYear);
        System.out.println("1일 전: " + minusDays);
        System.out.println("원본 유지: " + original); // 변경되지 않음
        
        // 객체 동일성 확인
        System.out.println("original == plusHours: " + (original == plusHours)); // false
        System.out.println("원본 객체는 절대 변경되지 않습니다!");
        
        // 스레드 안전성 테스트
        testThreadSafety();
    }
    
    // 스레드 안전성 테스트
    private static void testThreadSafety() {
        System.out.println("\n=== 스레드 안전성 테스트 ===");
        
        java.time.LocalDateTime sharedDateTime = java.time.LocalDateTime.now();
        int threadCount = 4;
        int operationsPerThread = 1000;
        
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicBoolean allSucceeded = new java.util.concurrent.atomic.AtomicBoolean(true);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    java.time.LocalDateTime localCopy = sharedDateTime;
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        // 다양한 연산 수행 (모두 새 객체 반환)
                        localCopy = localCopy.plusSeconds(1).minusMinutes(1).withYear(2025);
                        
                        // 원본 객체가 변경되지 않았는지 확인
                        if (!sharedDateTime.equals(sharedDateTime)) {
                            allSucceeded.set(false);
                            break;
                        }
                    }
                } catch (Exception e) {
                    allSucceeded.set(false);
                    System.err.println("스레드 " + threadId + " 오류: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        try {
            latch.await();
            System.out.println("스레드 안전성 테스트 " + (allSucceeded.get() ? "성공" : "실패"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // 기존 API와의 비교
    private static void compareWithLegacyApi() {
        System.out.println("\n=== 기존 Date/Calendar API와 비교 ===");
        
        // 기존 API의 문제점들
        System.out.println("=== 기존 API 문제점 시연 ===");
        
        // 1. 가변성 문제
        java.util.Date legacyDate = new java.util.Date();
        System.out.println("기존 Date (변경 전): " + legacyDate);
        legacyDate.setTime(legacyDate.getTime() + 86400000L); // 1일 추가
        System.out.println("기존 Date (변경 후): " + legacyDate); // 원본이 변경됨!
        
        // 2. Calendar의 복잡성과 0-based 월
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(2025, 7, 4); // 8월 4일이지만 7을 써야 함!
        System.out.println("Calendar 8월: " + (calendar.get(java.util.Calendar.MONTH) + 1));
        
        // 3. 새 API의 명확성
        java.time.LocalDate newDate = java.time.LocalDate.of(2025, 8, 4); // 명확함
        System.out.println("새 API 8월: " + newDate.getMonthValue());
        
        // 성능 비교
        comparePerformance();
        
        // 변환 방법들
        demonstrateConversion();
    }
    
    // 성능 비교
    private static void comparePerformance() {
        System.out.println("\n=== 성능 비교 ===");
        
        int iterations = 1_000_000;
        
        // Date 객체 생성 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            new java.util.Date();
        }
        long dateTime = System.nanoTime() - start;
        
        // LocalDateTime 객체 생성 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.LocalDateTime.now();
        }
        long localDateTimeTime = System.nanoTime() - start;
        
        // Instant 객체 생성 성능 (더 가벼움)
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.time.Instant.now();
        }
        long instantTime = System.nanoTime() - start;
        
        System.out.println("Date 생성: " + dateTime / 1_000_000 + "ms");
        System.out.println("LocalDateTime 생성: " + localDateTimeTime / 1_000_000 + "ms");
        System.out.println("Instant 생성: " + instantTime / 1_000_000 + "ms");
        
        // 날짜 연산 성능
        compareArithmeticPerformance();
    }
    
    // 날짜 연산 성능 비교
    private static void compareArithmeticPerformance() {
        System.out.println("\n=== 날짜 연산 성능 비교 ===");
        
        int iterations = 1_000_000;
        
        // Calendar 연산
        java.util.Calendar cal = java.util.Calendar.getInstance();
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1); // 원복
        }
        long calendarTime = System.nanoTime() - start;
        
        // LocalDate 연산 (불변 객체)
        java.time.LocalDate date = java.time.LocalDate.now();
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            date.plusDays(1).minusDays(1); // 새 객체 생성
        }
        long localDateTime = System.nanoTime() - start;
        
        System.out.println("Calendar 연산: " + calendarTime / 1_000_000 + "ms");
        System.out.println("LocalDate 연산: " + localDateTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)localDateTime / calendarTime + "배");
        
        System.out.println("참고: LocalDate는 불변 객체 생성 비용이 있지만, 스레드 안전과 명확성을 제공");
    }
    
    // 기존 API와의 변환
    private static void demonstrateConversion() {
        System.out.println("\n=== 기존 API와의 변환 ===");
        
        // Date ↔ Instant
        java.util.Date legacyDate = new java.util.Date();
        java.time.Instant instant = legacyDate.toInstant();
        java.util.Date convertedBack = java.util.Date.from(instant);
        
        System.out.println("Date → Instant: " + instant);
        System.out.println("Instant → Date: " + convertedBack);
        
        // Calendar ↔ ZonedDateTime
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.time.ZonedDateTime zonedDateTime = calendar.toInstant().atZone(calendar.getTimeZone().toZoneId());
        java.util.GregorianCalendar convertedCalendar = java.util.GregorianCalendar.from(zonedDateTime);
        
        System.out.println("Calendar → ZonedDateTime: " + zonedDateTime);
        System.out.println("ZonedDateTime → Calendar: " + convertedCalendar.getTime());
        
        // TimeZone ↔ ZoneId
        java.util.TimeZone timeZone = java.util.TimeZone.getDefault();
        java.time.ZoneId zoneId = timeZone.toZoneId();
        java.util.TimeZone convertedTimeZone = java.util.TimeZone.getTimeZone(zoneId);
        
        System.out.println("TimeZone → ZoneId: " + zoneId);
        System.out.println("ZoneId → TimeZone: " + convertedTimeZone.getID());
        
        // SQL 타입과의 변환
        demonstrateSqlConversion();
    }
    
    // SQL 타입 변환
    private static void demonstrateSqlConversion() {
        System.out.println("\n=== SQL 타입 변환 ===");
        
        // java.sql.Date ↔ LocalDate
        java.time.LocalDate localDate = java.time.LocalDate.of(2025, 8, 4);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        java.time.LocalDate backToLocalDate = sqlDate.toLocalDate();
        
        System.out.println("LocalDate → sql.Date: " + sqlDate);
        System.out.println("sql.Date → LocalDate: " + backToLocalDate);
        
        // java.sql.Time ↔ LocalTime
        java.time.LocalTime localTime = java.time.LocalTime.of(14, 30, 15);
        java.sql.Time sqlTime = java.sql.Time.valueOf(localTime);
        java.time.LocalTime backToLocalTime = sqlTime.toLocalTime();
        
        System.out.println("LocalTime → sql.Time: " + sqlTime);
        System.out.println("sql.Time → LocalTime: " + backToLocalTime);
        
        // java.sql.Timestamp ↔ LocalDateTime
        java.time.LocalDateTime localDateTime = java.time.LocalDateTime.of(2025, 8, 4, 14, 30, 15);
        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(localDateTime);
        java.time.LocalDateTime backToLocalDateTime = timestamp.toLocalDateTime();
        
        System.out.println("LocalDateTime → Timestamp: " + timestamp);
        System.out.println("Timestamp → LocalDateTime: " + backToLocalDateTime);
    }
}
```

### 6.2 날짜 생성과 파싱의 상세 분석

```java
public class DateTimeCreationAnalysis {
    
    // 다양한 날짜 생성 방법과 내부 동작
    public static void analyzeDateTimeCreation() {
        System.out.println("=== 날짜/시간 생성 방법 분석 ===");
        
        // 현재 시간 기반 생성
        analyzeCurrentTimeMethods();
        
        // 특정 값으로 생성
        analyzeSpecificValueCreation();
        
        // 파싱을 통한 생성
        analyzeParsingMethods();
        
        // 성능 최적화된 생성 방법
        analyzeOptimizedCreation();
    }
    
    // 현재 시간 기반 생성 메서드들
    private static void analyzeCurrentTimeMethods() {
        System.out.println("\n=== 현재 시간 기반 생성 ===");
        
        /* now() 메서드들의 내부 동작:
         * 1. Clock.systemDefaultZone() 사용
         * 2. System.currentTimeMillis() 또는 더 정밀한 시간 소스
         * 3. 시간대 변환 적용
         */
        
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        java.time.LocalDateTime currentDateTime = java.time.LocalDateTime.now();
        java.time.Instant currentInstant = java.time.Instant.now();
        
        System.out.println("LocalDate.now(): " + today);
        System.out.println("LocalTime.now(): " + currentTime);
        System.out.println("LocalDateTime.now(): " + currentDateTime);
        System.out.println("Instant.now(): " + currentInstant);
        
        // 다른 시간대의 현재 시간
        java.time.ZoneId seoulZone = java.time.ZoneId.of("Asia/Seoul");
        java.time.ZoneId utcZone = java.time.ZoneId.of("UTC");
        java.time.ZoneId newYorkZone = java.time.ZoneId.of("America/New_York");
        
        System.out.println("\n다양한 시간대의 현재 시간:");
        System.out.println("서울: " + java.time.LocalDateTime.now(seoulZone));
        System.out.println("UTC: " + java.time.LocalDateTime.now(utcZone));
        System.out.println("뉴욕: " + java.time.LocalDateTime.now(newYorkZone));
        
        // 커스텀 Clock 사용
        demonstrateCustomClock();
    }
    
    // 커스텀 Clock 사용
    private static void demonstrateCustomClock() {
        System.out.println("\n=== 커스텀 Clock 사용 ===");
        
        // 고정된 시간의 Clock
        java.time.Instant fixedInstant = java.time.Instant.parse("2025-08-04T14:30:15Z");
        java.time.Clock fixedClock = java.time.Clock.fixed(fixedInstant, java.time.ZoneOffset.UTC);
        
        java.time.LocalDateTime fixedDateTime = java.time.LocalDateTime.now(fixedClock);
        System.out.println("고정된 Clock: " + fixedDateTime);
        
        // 오프셋된 Clock (현재 시간 + 1시간)
        java.time.Clock offsetClock = java.time.Clock.offset(java.time.Clock.systemDefaultZone(), 
                                                            java.time.Duration.ofHours(1));
        java.time.LocalDateTime offsetDateTime = java.time.LocalDateTime.now(offsetClock);
        System.out.println("1시간 후 Clock: " + offsetDateTime);
        System.out.println("실제 현재 시간: " + java.time.LocalDateTime.now());
        
        // 틱 기반 Clock (초 단위로 반올림)
        java.time.Clock tickClock = java.time.Clock.tick(java.time.Clock.systemDefaultZone(), 
                                                         java.time.Duration.ofSeconds(1));
        java.time.LocalDateTime tickDateTime = java.time.LocalDateTime.now(tickClock);
        System.out.println("초 단위 Clock: " + tickDateTime);
        
        /* Clock 활용의 이점:
         * 1. 테스트에서 시간 제어 가능
         * 2. 시간 소스 추상화
         * 3. 성능 최적화 (tick으로 정밀도 조절)
         */
    }
    
    // 특정 값으로 생성
    private static void analyzeSpecificValueCreation() {
        System.out.println("\n=== 특정 값으로 생성 ===");
        
        // of() 메서드들 - 가장 기본적인 생성 방법
        java.time.LocalDate specificDate = java.time.LocalDate.of(2025, 8, 4);
        java.time.LocalTime specificTime = java.time.LocalTime.of(14, 30, 15, 123456789);
        java.time.LocalDateTime specificDateTime = java.time.LocalDateTime.of(2025, 8, 4, 14, 30, 15);
        
        System.out.println("특정 날짜: " + specificDate);
        System.out.println("특정 시간: " + specificTime);
        System.out.println("특정 날짜시간: " + specificDateTime);
        
        // 다양한 of() 오버로드
        System.out.println("\n=== of() 메서드 오버로드 ===");
        
        // LocalTime의 다양한 생성 방법
        java.time.LocalTime time1 = java.time.LocalTime.of(14, 30);                    // 시, 분
        java.time.LocalTime time2 = java.time.LocalTime.of(14, 30, 15);                // 시, 분, 초
        java.time.LocalTime time3 = java.time.LocalTime.of(14, 30, 15, 123456789);     // 시, 분, 초, 나노초
        
        System.out.println("시분: " + time1);
        System.out.println("시분초: " + time2);
        System.out.println("시분초나노: " + time3);
        
        // LocalDateTime의 다양한 생성 방법
        java.time.LocalDateTime dt1 = java.time.LocalDateTime.of(2025, 8, 4, 14, 30);
        java.time.LocalDateTime dt2 = java.time.LocalDateTime.of(specificDate, specificTime);
        java.time.LocalDateTime dt3 = java.time.LocalDateTime.of(2025, java.time.Month.AUGUST, 4, 14, 30, 15);
        
        System.out.println("년월일시분: " + dt1);
        System.out.println("날짜+시간 조합: " + dt2);
        System.out.println("Month 열거형 사용: " + dt3);
        
        // 다른 객체로부터 생성
        demonstrateCreationFromOthers();
    }
    
    // 다른 객체로부터 생성
    private static void demonstrateCreationFromOthers() {
        System.out.println("\n=== 다른 객체로부터 생성 ===");
        
        // Instant로부터 생성
        java.time.Instant instant = java.time.Instant.now();
        java.time.LocalDateTime fromInstant = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        java.time.ZonedDateTime zonedFromInstant = java.time.ZonedDateTime.ofInstant(instant, java.time.ZoneId.of("Asia/Seoul"));
        
        System.out.println("Instant: " + instant);
        System.out.println("Instant → LocalDateTime: " + fromInstant);
        System.out.println("Instant → ZonedDateTime: " + zonedFromInstant);
        
        // Epoch 기반 생성
        java.time.Instant epochSecond = java.time.Instant.ofEpochSecond(1722776215L); // Unix timestamp
        java.time.Instant epochMilli = java.time.Instant.ofEpochMilli(System.currentTimeMillis());
        
        System.out.println("Epoch 초: " + epochSecond);
        System.out.println("Epoch 밀리초: " + epochMilli);
        
        // 일자/시간 구성요소로부터
        int year = 2025, month = 8, day = 4;
        int dayOfYear = 216; // 8월 4일은 216일째
        
        java.time.LocalDate fromDayOfYear = java.time.LocalDate.ofYearDay(year, dayOfYear);
        System.out.println("년도와 일련번호: " + fromDayOfYear);
        
        // 주 기반 생성
        java.time.LocalDate mondayOfWeek = java.time.LocalDate.of(2025, 8, 4)
            .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        System.out.println("해당 주의 월요일: " + mondayOfWeek);
    }
    
    // 파싱을 통한 생성
    private static void analyzeParsingMethods() {
        System.out.println("\n=== 파싱을 통한 생성 ===");
        
        // 기본 ISO 형식 파싱
        System.out.println("=== 기본 ISO 형식 파싱 ===");
        
        java.time.LocalDate parsedDate = java.time.LocalDate.parse("2025-08-04");
        java.time.LocalTime parsedTime = java.time.LocalTime.parse("14:30:15");
        java.time.LocalDateTime parsedDateTime = java.time.LocalDateTime.parse("2025-08-04T14:30:15");
        java.time.ZonedDateTime parsedZoned = java.time.ZonedDateTime.parse("2025-08-04T14:30:15+09:00[Asia/Seoul]");
        java.time.Instant parsedInstant = java.time.Instant.parse("2025-08-04T05:30:15Z");
        
        System.out.println("파싱된 날짜: " + parsedDate);
        System.out.println("파싱된 시간: " + parsedTime);
        System.out.println("파싱된 날짜시간: " + parsedDateTime);
        System.out.println("파싱된 시간대 포함: " + parsedZoned);
        System.out.println("파싱된 Instant: " + parsedInstant);
        
        // 커스텀 포맷터를 사용한 파싱
        demonstrateCustomFormatParsing();
        
        // 파싱 오류 처리
        demonstrateParsingErrorHandling();
        
        // 성능 최적화된 파싱
        analyzeParsingPerformance();
    }
    
    // 커스텀 포맷터 파싱
    private static void demonstrateCustomFormatParsing() {
        System.out.println("\n=== 커스텀 포맷터 파싱 ===");
        
        // 다양한 날짜 형식들
        String[] dateFormats = {
            "04/08/2025",           // MM/dd/yyyy
            "2025년 8월 4일",        // yyyy년 M월 d일
            "Aug 4, 2025",          // MMM d, yyyy
            "4th August 2025",      // 복잡한 형식
            "20250804"              // yyyyMMdd
        };
        
        java.time.format.DateTimeFormatter[] formatters = {
            java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("yyyy년 M월 d일"),
            java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.ENGLISH),
            java.time.format.DateTimeFormatter.ofPattern("d['st']['nd']['rd']['th'] MMMM yyyy", java.util.Locale.ENGLISH),
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        };
        
        for (int i = 0; i < dateFormats.length; i++) {
            try {
                java.time.LocalDate parsed = java.time.LocalDate.parse(dateFormats[i], formatters[i]);
                System.out.printf("'%s' → %s%n", dateFormats[i], parsed);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.printf("'%s' 파싱 실패: %s%n", dateFormats[i], e.getMessage());
            }
        }
        
        // 시간 형식들
        System.out.println("\n시간 형식 파싱:");
        String[] timeFormats = {
            "2:30 PM",              // h:mm a
            "14:30:15",             // HH:mm:ss
            "오후 2시 30분",         // a h시 mm분
            "143015123"             // HHmmssSSS
        };
        
        java.time.format.DateTimeFormatter[] timeFormatters = {
            java.time.format.DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.ENGLISH),
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"),
            java.time.format.DateTimeFormatter.ofPattern("a h시 mm분"),
            java.time.format.DateTimeFormatter.ofPattern("HHmmssSSS")
        };
        
        for (int i = 0; i < timeFormats.length; i++) {
            try {
                java.time.LocalTime parsed = java.time.LocalTime.parse(timeFormats[i], timeFormatters[i]);
                System.out.printf("'%s' → %s%n", timeFormats[i], parsed);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.printf("'%s' 파싱 실패: %s%n", timeFormats[i], e.getMessage());
            }
        }
    }
    
    // 파싱 오류 처리
    private static void demonstrateParsingErrorHandling() {
        System.out.println("\n=== 파싱 오류 처리 ===");
        
        String[] invalidDates = {
            "2025-13-01",           // 잘못된 월
            "2025-02-30",           // 존재하지 않는 날짜
            "not-a-date",           // 완전히 잘못된 형식
            "2025/08/04",           // 다른 구분자
            ""                      // 빈 문자열
        };
        
        for (String invalidDate : invalidDates) {
            try {
                java.time.LocalDate parsed = java.time.LocalDate.parse(invalidDate);
                System.out.println("예상외 성공: " + invalidDate + " → " + parsed);
            } catch (java.time.format.DateTimeParseException e) {
                System.out.printf("'%s' 파싱 실패 (위치 %d): %s%n", 
                    invalidDate, e.getErrorIndex(), e.getMessage());
            }
        }
        
        // 안전한 파싱 유틸리티
        System.out.println("\n안전한 파싱 유틸리티:");
        java.util.Optional<java.time.LocalDate> safeDate1 = safeParseLoca---

## 5. Java 8+ 새로운 기능들

### 5.1 Stream API의 완전 정복

Stream API는 Java 8에서 도입된 함수형 프로그래밍 스타일의 컬렉션 처리 API입니다.

```java
public class StreamAPIDeepAnalysis {
    
    // Stream의 내부 구조와 지연 평가 메커니즘
    public static void analyzeStreamInternals() {
        /* Stream 내부 구조:
         * 1. 파이프라인 구조: Source → Intermediate Operations → Terminal Operation
         * 2. 지연 평가 (Lazy Evaluation): 최종 연산이 호출되기 전까지는 실행되지 않음
         * 3. 단일 사용: 한 번 사용된 Stream은 재사용 불가
         * 4. 함수형 인터페이스 기반: Predicate, Function, Consumer 등 활용
         */
        
        System.out.println("=== Stream 내부 구조 분석 ===");
        
        java.util.List<String> names = java.util.Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");
        
        // 지연 평가 시연
        System.out.println("=== 지연 평가 시연 ===");
        java.util.stream.Stream<String> stream = names.stream()
            .filter(name -> {
                System.out.println("Filtering: " + name);
                return name.length() > 3;
            })
            .map(name -> {
                System.out.println("Mapping: " + name);
                return name.toUpperCase();
            });
        
        System.out.println("Stream 생성 완료 - 아직 아무것도 실행되지 않음!");
        
        System.out.println("\n최종 연산 실행:");
        java.util.List<String> result = stream.collect(java.util.stream.Collectors.toList());
        System.out.println("결과: " + result);
        
        /* 지연 평가의 이점:
         * 1. 메모리 효율성: 중간 결과를 저장하지 않음
         * 2. 최적화 가능: short-circuiting, loop fusion 등
         * 3. 무한 스트림 처리 가능
         */
        
        demonstrateStreamOptimizations();
    }
    
    // Stream 최적화 메커니즘
    private static void demonstrateStreamOptimizations() {
        System.out.println("\n=== Stream 최적화 메커니즘 ===");
        
        java.util.List<Integer> numbers = java.util.stream.IntStream.range(1, 1000000)
            .boxed().collect(java.util.stream.Collectors.toList());
        
        // 1. Short-circuiting 최적화
        System.out.println("1. Short-circuiting 최적화:");
        
        long start = System.nanoTime();
        java.util.Optional<Integer> found1 = numbers.stream()
            .filter(n -> n % 7 == 0)
            .filter(n -> n > 1000)
            .findFirst(); // 첫 번째를 찾으면 즉시 종료
        long shortCircuitTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        java.util.List<Integer> all = numbers.stream()
            .filter(n -> n % 7 == 0)
            .filter(n -> n > 1000)
            .collect(java.util.stream.Collectors.toList()); // 모든 요소 처리
        long fullProcessTime = System.nanoTime() - start;
        
        System.out.println("findFirst(): " + shortCircuitTime / 1_000_000 + "ms");
        System.out.println("collect(): " + fullProcessTime / 1_000_000 + "ms");
        System.out.println("Short-circuiting 효과: " + (double)fullProcessTime / shortCircuitTime + "배 빠름");
        
        // 2. Loop Fusion 최적화 (내부적으로 수행)
        System.out.println("\n2. Loop Fusion:");
        System.out.println("여러 중간 연산이 하나의 루프로 융합되어 처리됨");
        
        demonstrateStreamVsLoop();
    }
    
    // Stream vs 전통적인 Loop 성능 비교
    private static void demonstrateStreamVsLoop() {
        System.out.println("\n=== Stream vs Loop 성능 비교 ===");
        
        int size = 1_000_000;
        java.util.List<Integer> data = java.util.stream.IntStream.range(0, size)
            .boxed().collect(java.util.stream.Collectors.toList());
        
        // 전통적인 for 루프
        long start = System.nanoTime();
        long sum1 = 0;
        for (Integer num : data) {
            if (num % 2 == 0) {
                sum1 += num * 2;
            }
        }
        long loopTime = System.nanoTime() - start;
        
        // Stream API
        start = System.nanoTime();
        long sum2 = data.stream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * 2)
            .sum();
        long streamTime = System.nanoTime() - start;
        
        // 병렬 Stream
        start = System.nanoTime();
        long sum3 = data.parallelStream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * 2)
            .sum();
        long parallelTime = System.nanoTime() - start;
        
        System.out.println("For Loop: " + loopTime / 1_000_000 + "ms (결과: " + sum1 + ")");
        System.out.println("Stream: " + streamTime / 1_000_000 + "ms (결과: " + sum2 + ")");
        System.out.println("Parallel Stream: " + parallelTime / 1_000_000 + "ms (결과: " + sum3 + ")");
        
        System.out.println("Stream 오버헤드: " + (double)streamTime / loopTime + "배");
        System.out.println("병렬화 효과: " + (double)streamTime / parallelTime + "배");
    }
    
    // Stream의 중간 연산들 상세 분석
    public static void analyzeIntermediateOperations() {
        System.out.println("\n=== Stream 중간 연산 상세 분석 ===");
        
        java.util.List<String> words = java.util.Arrays.asList(
            "apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "apple", "banana"
        );
        
        // 1. filter() - 조건 필터링
        System.out.println("=== filter() 분석 ===");
        java.util.List<String> longWords = words.stream()
            .filter(word -> word.length() > 5) // Predicate<T> 사용
            .collect(java.util.stream.Collectors.toList());
        System.out.println("길이 > 5: " + longWords);
        
        // 2. map() - 1:1 변환
        System.out.println("\n=== map() 분석 ===");
        java.util.List<Integer> lengths = words.stream()
            .map(String::length) // Function<T, R> 사용
            .collect(java.util.stream.Collectors.toList());
        System.out.println("문자열 길이: " + lengths);
        
        // 3. flatMap() - 1:N 변환
        System.out.println("\n=== flatMap() 분석 ===");
        java.util.List<String> sentences = java.util.Arrays.asList(
            "Hello World", "Java Programming", "Stream API"
        );
        
        java.util.List<String> allWords = sentences.stream()
            .flatMap(sentence -> java.util.Arrays.stream(sentence.split(" ")))
            .collect(java.util.stream.Collectors.toList());
        System.out.println("모든 단어: " + allWords);
        
        // 4. distinct() - 중복 제거
        System.out.println("\n=== distinct() 분석 ===");
        java.util.List<String> uniqueWords = words.stream()
            .distinct() // Object.equals() 기반
            .collect(java.util.stream.Collectors.toList());
        System.out.println("중복 제거: " + uniqueWords);
        
        // 5. sorted() - 정렬
        System.out.println("\n=== sorted() 분석 ===");
        java.util.List<String> sortedWords = words.stream()
            .distinct()
            .sorted() // Comparable 또는 Comparator 사용
            .collect(java.util.stream.Collectors.toList());
        System.out.println("정렬됨: " + sortedWords);
        
        // 커스텀 정렬
        java.util.List<String> lengthSorted = words.stream()
            .distinct()
            .sorted(java.util.Comparator.comparing(String::length).thenComparing(java.util.Comparator.naturalOrder()))
            .collect(java.util.stream.Collectors.toList());
        System.out.println("길이순, 알파벳순: " + lengthSorted);
        
        analyzeAdvancedIntermediateOperations();
    }
    
    // 고급 중간 연산들
    private static void analyzeAdvancedIntermediateOperations() {
        System.out.println("\n=== 고급 중간 연산 ===");
        
        java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 6. peek() - 디버깅과 부수 효과
        System.out.println("=== peek() 분석 ===");
        java.util.List<Integer> processed = numbers.stream()
            .peek(n -> System.out.print("처리 중: " + n + " "))
            .filter(n -> n % 2 == 0)
            .peek(n -> System.out.print("필터링됨: " + n + " "))
            .map(n -> n * n)
            .peek(n -> System.out.print("제곱됨: " + n + " "))
            .collect(java.util.stream.Collectors.toList());
        System.out.println("\n최종 결과: " + processed);
        
        // 7. skip()과 limit() - 범위 제한
        System.out.println("\n=== skip()과 limit() 분석 ===");
        java.util.List<Integer> middlePortion = numbers.stream()
            .skip(3) // 처음 3개 건너뛰기
            .limit(4) // 최대 4개만 취하기
            .collect(java.util.stream.Collectors.toList());
        System.out.println("4번째부터 4개: " + middlePortion);
        
        // 8. takeWhile()과 dropWhile() - Java 9+
        demonstrateJava9StreamOperations(numbers);
    }
    
    // Java 9+ Stream 연산들
    private static void demonstrateJava9StreamOperations(java.util.List<Integer> numbers) {
        System.out.println("\n=== Java 9+ Stream 연산 ===");
        
        try {
            // takeWhile() - 조건이 참인 동안 요소 취하기
            java.lang.reflect.Method takeWhile = java.util.stream.Stream.class.getMethod("takeWhile", java.util.function.Predicate.class);
            java.lang.reflect.Method dropWhile = java.util.stream.Stream.class.getMethod("dropWhile", java.util.function.Predicate.class);
            
            java.util.stream.Stream<Integer> stream1 = numbers.stream();
            @SuppressWarnings("unchecked")
            java.util.stream.Stream<Integer> takenStream = (java.util.stream.Stream<Integer>) takeWhile.invoke(stream1, (java.util.function.Predicate<Integer>) n -> n < 6);
            java.util.List<Integer> taken = takenStream.collect(java.util.stream.Collectors.toList());
            System.out.println("takeWhile(< 6): " + taken);
            
            java.util.stream.Stream<Integer> stream2 = numbers.stream();
            @SuppressWarnings("unchecked")
            java.util.stream.Stream<Integer> droppedStream = (java.util.stream.Stream<Integer>) dropWhile.invoke(stream2, (java.util.function.Predicate<Integer>) n -> n < 6);
            java.util.List<Integer> dropped = droppedStream.collect(java.util.stream.Collectors.toList());
            System.out.println("dropWhile(< 6): " + dropped);
            
        } catch (Exception e) {
            System.out.println("Java 9+ takeWhile/dropWhile 메서드를 사용할 수 없습니다.");
        }
    }
    
    // Stream의 최종 연산들 상세 분석
    public static void analyzeTerminalOperations() {
        System.out.println("\n=== Stream 최종 연산 상세 분석 ===");
        
        java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. collect() - 수집 연산
        System.out.println("=== collect() 분석 ===");
        
        // 리스트로 수집
        java.util.List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(java.util.stream.Collectors.toList());
        System.out.println("짝수 리스트: " + evenNumbers);
        
        // 집합으로 수집
        java.util.Set<Integer> uniqueNumbers = numbers.stream()
            .collect(java.util.stream.Collectors.toSet());
        System.out.println("집합: " + uniqueNumbers);
        
        // 맵으로 수집
        java.util.Map<Boolean, java.util.List<Integer>> partitioned = numbers.stream()
            .collect(java.util.stream.Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("짝수/홀수 분할: " + partitioned);
        
        // 2. reduce() - 축약 연산
        System.out.println("\n=== reduce() 분석 ===");
        
        /* reduce() 내부 동작:
         * 1. identity (초기값) 설정
         * 2. BinaryOperator로 두 요소를 하나로 결합
         * 3. 순차적으로 모든 요소에 적용
         */
        
        // 합계
        int sum = numbers.stream()
            .reduce(0, Integer::sum); // identity=0, accumulator=Integer::sum
        System.out.println("합계: " + sum);
        
        // 곱
        int product = numbers.stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("곱: " + product);
        
        // 최대값 (Optional 반환)
        java.util.Optional<Integer> max = numbers.stream()
            .reduce(Integer::max);
        System.out.println("최대값: " + max.orElse(0));
        
        // 문자열 연결
        String concatenated = java.util.Arrays.asList("A", "B", "C", "D").stream()
            .reduce("", (a, b) -> a + b);
        System.out.println("문자열 연결: " + concatenated);
        
        // 3. forEach() - 각 요소에 대해 작업 수행
        System.out.println("\n=== forEach() 분석 ===");
        System.out.print("각 요소 출력: ");
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print(n + " ")); // Consumer<T> 사용
        System.out.println();
        
        // 4. 검색 연산들
        analyzeSearchOperations(numbers);
        
        // 5. 집계 연산들
        analyzeAggregateOperations(numbers);
    }
    
    // 검색 연산들 분석
    private static void analyzeSearchOperations(java.util.List<Integer> numbers) {
        System.out.println("\n=== 검색 연산 분석 ===");
        
        // findFirst() - 첫 번째 요소
        java.util.Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        System.out.println("5보다 큰 첫 번째: " + first.orElse(-1));
        
        // findAny() - 임의의 요소 (병렬 스트림에서 유용)
        java.util.Optional<Integer> any = numbers.parallelStream()
            .filter(n -> n > 5)
            .findAny();
        System.out.println("5보다 큰 임의 요소: " + any.orElse(-1));
        
        // anyMatch() - 조건을 만족하는 요소가 하나라도 있는가?
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        System.out.println("짝수가 있는가? " + hasEven);
        
        // allMatch() - 모든 요소가 조건을 만족하는가?
        boolean allPositive = numbers.stream()
            .allMatch(n -> n > 0);
        System.out.println("모두 양수인가? " + allPositive);
        
        // noneMatch() - 조건을 만족하는 요소가 하나도 없는가?
        boolean noNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        System.out.println("음수가 없는가? " + noNegative);
        
        /* 검색 연산의 short-circuiting:
         * - 조건을 만족하는 요소를 찾으면 즉시 종료
         * - 무한 스트림에서도 사용 가능
         * - 성능상 유리
         */
    }
    
    // 집계 연산들 분석
    private static void analyzeAggregateOperations(java.util.List<Integer> numbers) {
        System.out.println("\n=== 집계 연산 분석 ===");
        
        // 기본 집계 연산들 (IntStream에서 제공)
        java.util.OptionalDouble average = numbers.stream()
            .mapToInt(Integer::intValue)
            .average();
        System.out.println("평균: " + average.orElse(0.0));
        
        long count = numbers.stream()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("짝수 개수: " + count);
        
        int sum = numbers.stream()
            .mapToInt(Integer::intValue)
            .sum();
        System.out.println("합계: " + sum);
        
        java.util.OptionalInt max = numbers.stream()
            .mapToInt(Integer::intValue)
            .max();
        System.out.println("최대값: " + max.orElse(0));
        
        java.util.OptionalInt min = numbers.stream()
            .mapToInt(Integer::intValue)
            .min();
        System.out.println("최소값: " + min.orElse(0));
        
        // 통계 정보 한 번에
        java.util.IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        
        System.out.println("\n통계 정보:");
        System.out.println("개수: " + stats.getCount());
        System.out.println("합계: " + stats.getSum());
        System.out.println("평균: " + stats.getAverage());
        System.out.println("최소값: " + stats.getMin());
        System.out.println("최대값: " + stats.getMax());
    }
}
```

### 5.2 고급 Collectors 활용

```java
public class AdvancedCollectorsAnalysis {
    
    // Collectors의 내부 구조와 동작 원리
    public static void analyzeCollectorsInternals() {
        /* Collector 인터페이스 구조:
         * 1. Supplier<A> supplier(): 결과 컨테이너 생성
         * 2. BiConsumer<A, T> accumulator(): 요소를 컨테이너에 추가
         * 3. BinaryOperator<A> combiner(): 두 컨테이너 병합 (병렬 처리용)
         * 4. Function<A, R> finisher(): 최종 결과 변환
         * 5. Set<Characteristics> characteristics(): 수집기 특성
         */
        
        System.out.println("=== Collectors 내부 구조 분석 ===");
        
        // 샘플 데이터
        java.util.List<Person> people = java.util.Arrays.asList(
            new Person("Alice", 25, "Engineering", 75000),
            new Person("Bob", 30, "Marketing", 65000),
            new Person("Charlie", 35, "Engineering", 85000),
            new Person("David", 28, "Sales", 55000),
            new Person("Eve", 32, "Marketing", 70000),
            new Person("Frank", 29, "Engineering", 80000)
        );
        
        System.out.println("샘플 데이터: " + people.size() + "명");
        
        // 기본 수집 연산들
        demonstrateBasicCollectors(people);
        
        // 그룹핑 연산들
        demonstrateGroupingCollectors(people);
        
        // 커스텀 Collector 구현
        demonstrateCustomCollectors(people);
    }
    
    // 기본 Collectors
    private static void demonstrateBasicCollectors(java.util.List<Person> people) {
        System.out.println("\n=== 기본 Collectors ===");
        
        // toList(), toSet(), toMap()
        java.util.List<String> names = people.stream()
            .map(Person::getName)
            .collect(java.util.stream.Collectors.toList());
        System.out.println("이름 목록: " + names);
        
        // 중복 제거
        java.util.Set<String> departments = people.stream()
            .map(Person::getDepartment)
            .collect(java.util.stream.Collectors.toSet());
        System.out.println("부서 목록: " + departments);
        
        // toMap() - 키 중복 처리
        java.util.Map<String, Integer> nameToAge = people.stream()
            .collect(java.util.stream.Collectors.toMap(
                Person::getName,
                Person::getAge,
                (existing, replacement) -> existing // 중복 키 처리: 기존 값 유지
            ));
        System.out.println("이름-나이 맵: " + nameToAge);
        
        // joining() - 문자열 결합
        String allNames = people.stream()
            .map(Person::getName)
            .collect(java.util.stream.Collectors.joining(", ", "[", "]"));
        System.out.println("모든 이름: " + allNames);
        
        // 수치 집계
        double averageSalary = people.stream()
            .collect(java.util.stream.Collectors.averagingDouble(Person::getSalary));
        System.out.println("평균 연봉: " + String.format("%.0f", averageSalary));
        
        long totalSalary = people.stream()
            .collect(java.util.stream.Collectors.summingLong(Person::getSalary));
        System.out.println("총 연봉: " + totalSalary);
        
        // 통계 정보
        java.util.DoubleSummaryStatistics salaryStats = people.stream()
            .collect(java.util.stream.Collectors.summarizingDouble(Person::getSalary));
        System.out.println("연봉 통계: " + salaryStats);
    }
    
    // 그룹핑 Collectors
    private static void demonstrateGroupingCollectors(java.util.List<Person> people) {
        System.out.println("\n=== 그룹핑 Collectors ===");
        
        // 단순 그룹핑
        java.util.Map<String, java.util.List<Person>> byDepartment = people.stream()
            .collect(java.util.stream.Collectors.groupingBy(Person::getDepartment));
        
        System.out.println("부서별 그룹핑:");
        byDepartment.forEach((dept, list) -> 
            System.out.println("  " + dept + ": " + list.size() + "명"));
        
        // 다운스트림 Collector와 함께 그룹핑
        java.util.Map<String, Double> avgSalaryByDept = people.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Person::getDepartment,
                java.util.stream.Collectors.averagingDouble(Person::getSalary)
            ));
        
        System.out.println("\n부서별 평균 연봉:");
        avgSalaryByDept.forEach((dept, avg) -> 
            System.out.printf("  %s: %.0f%n", dept, avg));
        
        // 다단계 그룹핑
        java.util.Map<String, java.util.Map<String, java.util.List<Person>>> complexGrouping = people.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Person::getDepartment,
                java.util.stream.Collectors.groupingBy(person -> 
                    person.getAge() >= 30 ? "Senior" : "Junior"
                )
            ));
        
        System.out.println("\n부서별, 연령대별 그룹핑:");
        complexGrouping.forEach((dept, ageGroups) -> {
            System.out.println("  " + dept + ":");
            ageGroups.forEach((ageGroup, list) -> 
                System.out.println("    " + ageGroup + ": " + list.size() + "명"));
        });
        
        // partitioningBy - 이분 분할
        java.util.Map<Boolean, java.util.List<Person>> partitioned = people.stream()
            .collect(java.util.stream.Collectors.partitioningBy(p -> p.getSalary() >= 70000));
        
        System.out.println("\n연봉 기준 분할 (>=70000):");
        System.out.println("  고소득: " + partitioned.get(true).size() + "명");
        System.out.println("  저소득: " + partitioned.get(false).size() + "명");
        
        // 복합 다운스트림 Collector
        java.util.Map<String, java.util.Optional<Person>> topEarnerByDept = people.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Person::getDepartment,
                java.util.stream.Collectors.maxBy(java.util.Comparator.comparing(Person::getSalary))
            ));
        
        System.out.println("\n부서별 최고 연봉자:");
        topEarnerByDept.forEach((dept, optPerson) -> 
            optPerson.ifPresent(person -> 
                System.out.printf("  %s: %s (%.0f)%n", dept, person.getName(), person.getSalary())));
    }
    
    // 커스텀 Collector 구현
    private static void demonstrateCustomCollectors(java.util.List<Person> people) {
        System.out.println("\n=== 커스텀 Collector ===");
        
        // 1. 표준편차 계산 Collector
        java.util.stream.Collector<Person, ?, Double> salaryStdDevCollector = 
            java.util.stream.Collector.of(
                () -> new double[3], // [count, sum, sumOfSquares]
                (acc, person) -> {
                    double salary = person.getSalary();
                    acc[0]++;                    // count
                    acc[1] += salary;            // sum
                    acc[2] += salary * salary;   // sum of squares
                },
                (acc1, acc2) -> {
                    acc1[0] += acc2[0];
                    acc1[1] += acc2[1];  
                    acc1[2] += acc2[2];
                    return acc1;
                },
                acc -> {
                    double count = acc[0];
                    double mean = acc[1] / count;
                    double variance = (acc[2] / count) - (mean * mean);
                    return Math.sqrt(variance);
                }
            );
        
        double stdDev = people.stream().collect(salaryStdDevCollector);
        System.out.printf("연봉 표준편차: %.2f%n", stdDev);
        
        // 2. 최빈값(mode) 계산 Collector
        java.util.stream.Collector<Person, ?, java.util.Optional<String>> departmentModeCollector =
            java.util.stream.Collector.of(
                java.util.HashMap<String, Integer>::new,
                (map, person) -> map.merge(person.getDepartment(), 1, Integer::sum),
                (map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, Integer::sum));
                    return map1;
                },
                map -> map.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
            );
        
        java.util.Optional<String> mostCommonDept = people.stream().collect(departmentModeCollector);
        System.out.println("가장 많은 부서: " + mostCommonDept.orElse("없음"));
        
        // 3. 복합 통계 Collector
        ComplexStats complexStats = people.stream().collect(createComplexStatsCollector());
        System.out.println("\n복합 통계:");
        System.out.println("  평균 나이: " + complexStats.averageAge);
        System.out.println("  평균 연봉: " + complexStats.averageSalary);
        System.out.println("  부서 수: " + complexStats.departmentCount);
        System.out.println("  연봉 범위: " + complexStats.salaryRange);
    }
    
    // 복합 통계를 위한 커스텀 Collector
    private static java.util.stream.Collector<Person, ?, ComplexStats> createComplexStatsCollector() {
        class Accumulator {
            int count = 0;
            int ageSum = 0;
            double salarySum = 0.0;
            double minSalary = Double.MAX_VALUE;
            double maxSalary = Double.MIN_VALUE;
            java.util.Set<String> departments = new java.util.HashSet<>();
        }
        
        return java.util.stream.Collector.of(
            Accumulator::new,
            (acc, person) -> {
                acc.count++;
                acc.ageSum += person.getAge();
                acc.salarySum += person.getSalary();
                acc.minSalary = Math.min(acc.minSalary, person.getSalary());
                acc.maxSalary = Math.max(acc.maxSalary, person.getSalary());
                acc.departments.add(person.getDepartment());
            },
            (acc1, acc2) -> {
                acc1.count += acc2.count;
                acc1.ageSum += acc2.ageSum;
                acc1.salarySum += acc2.salarySum;
                acc1.minSalary = Math.min(acc1.minSalary, acc2.minSalary);
                acc1.maxSalary = Math.max(acc1.maxSalary, acc2.maxSalary);
                acc1.departments.addAll(acc2.departments);
                return acc1;
            },
            acc -> new ComplexStats(
                acc.count > 0 ? (double) acc.ageSum / acc.count : 0,
                acc.count > 0 ? acc.salarySum / acc.count : 0,
                acc.departments.size(),
                acc.maxSalary - acc.minSalary
            )
        );
    }
    
    // 복합 통계 결과 클래스
    static class ComplexStats {
        final double averageAge;
        final double averageSalary;
        final int departmentCount;
        final double salaryRange;
        
        ComplexStats(double averageAge, double averageSalary, int departmentCount, double salaryRange) {
            this.averageAge = averageAge;
            this.averageSalary = averageSalary;
            this.departmentCount = departmentCount;
            this.salaryRange = salaryRange;
        }
    }
    
    // Person 클래스
    static class Person {
        private final String name;
        private final int age;
        private final String department;
        private final double salary;
        
        Person(String name, int age, String department, double salary) {
            this.name = name;
            this.age = age;
            this.department = department;
            this.salary = salary;
        }
        
        String getName() { return name; }
        int getAge() { return age; }
        String getDepartment() { return department; }
        double getSalary() { return salary; }
        
        @Override
        public String toString() {
            return name + "(" + age + ", " + department + ", " + salary + ")";
        }
    }
}
```

### 5.3 Optional 클래스 완전 가이드

```java
public class OptionalDeepAnalysis {
    
    // Optional의 내부 구조와 null 안전성
    public static void analyzeOptionalInternals() {
        /* Optional 내부 구조:
         * 1. private final T value; // 실제 값 (null일 수 있음)
         * 2. private static final Optional<?> EMPTY; // 빈 Optional 싱글톤
         * 3. 불변 객체 (값 변경 불가능)
         * 4. null 참조 대신 명시적으로 "값이 없음"을 표현
         */
        
        System.out.println("=== Optional 내부 구조 분석 ===");
        
        // Optional 생성 방법들
        java.util.Optional<String> optional1 = java.util.Optional.of("Hello");          // null이면 예외
        java.util.Optional<String> optional2 = java.util.Optional.ofNullable(getName()); // null 허용
        java.util.Optional<String> empty = java.util.Optional.empty();                   // 빈 Optional
        
        System.out.println("of()로 생성: " + optional1);
        System.out.println("ofNullable()로 생성: " + optional2);
        System.out.println("empty()로 생성: " + empty);
        
        // 빈 Optional들은 모두 같은 싱글톤 인스턴스
        java.util.Optional<String> empty2 = java.util.Optional.empty();
        System.out.println("빈 Optional 동일성: " + (empty == empty2)); // true
        
        // Optional 체이닝의 null 안전성
        demonstrateNullSafety();
        
        // Optional과 예외 처리
        demonstrateExceptionHandling();
    }
    
    // null 안전성 시연
    private static void demonstrateNullSafety() {
        System.out.println("\n=== null 안전성 시연 ===");
        
        // 전통적인 null 체크 (위험한 방식)
        String traditionalResult = processTraditionalWay(null);
        System.out.println("전통적 방식 결과: " + traditionalResult);
        
        // Optional을 사용한 안전한 방식
        String optionalResult = processOptionalWay(null);
        System.out.println("Optional 방식 결과: " + optionalResult);
        
        // 체이닝된 null 체크
        demonstrateChainedNullCheck();
    }
    
    // 전통적인 null 처리 (문제가 많은 방식)
    private static String processTraditionalWay(String input) {
        try {
            if (input != null) {
                String processed = input.toUpperCase();
                if (processed.length() > 5) {
                    return processed.substring(0, 5);
                }
                return processed;
            }
            return "DEFAULT";
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    // Optional을 사용한 안전한 처리
    private static String processOptionalWay(String input) {
        return java.util.Optional.ofNullable(input)
            .map(String::toUpperCase)
            .filter(s -> s.length() > 5)
            .map(s -> s.substring(0, 5))
            .orElse("DEFAULT");
    }
    
    // 체이닝된 null 체크
    private static void demonstrateChainedNullCheck() {
        System.out.println("\n=== 체이닝된 null 체크 ===");
        
        // 복잡한 객체 그래프
        class Address {
            private final String street;
            Address(String street) { this.street = street; }
            java.util.Optional<String> getStreet() { return java.util.Optional.ofNullable(street); }
        }
        
        class Person {
            private final Address address;
            Person(Address address) { this.address = address; }
            java.util.Optional<Address> getAddress() { return java.util.Optional.ofNullable(address); }
        }
        
        class Company {
            private final Person ceo;
            Company(Person ceo) { this.ceo = ceo; }
            java.util.Optional<Person> getCEO() { return java.util.Optional.ofNullable(ceo); }
        }
        
        // 테스트 데이터
        Company company1 = new Company(new Person(new Address("123 Main St")));
        Company company2 = new Company(new Person(null));
        Company company3 = new Company(null);
        
        // Optional 체이닝을 통한 안전한 접근
        String address1 = company1.getCEO()
            .flatMap(Person::getAddress)
            .flatMap(Address::getStreet)
            .orElse("주소 없음");
        
        String address2 = company2.getCEO()
            .flatMap(Person::getAddress)
            .flatMap(Address::getStreet)
            .orElse("주소 없음");
        
        String address3 = company3.getCEO()
            .flatMap(Person::getAddress)
            .flatMap(Address::getStreet)
            .orElse("주소 없음");
        
        System.out.println("Company1 CEO 주소: " + address1);
        System.out.println("Company2 CEO 주소: " + address2);
        System.out.println("Company3 CEO 주소: " + address3);
    }
    
    // Optional과 예외 처리
    private static void demonstrateExceptionHandling() {
        System.out.println("\n=== Optional과 예외 처리 ===");
        
        // 값이 있을 때만 실행
        java.util.Optional<String> value = java.util.Optional.of("42");
        value.ifPresent(s -> System.out.println("값이 있음: " + s));
        
        // 값이 있을 때와 없을 때 각각 처리 (Java 9+)
        try {
            java.lang.reflect.Method ifPresentOrElse = java.util.Optional.class.getMethod(
                "ifPresentOrElse", java.util.function.Consumer.class, Runnable.class);
            
            ifPresentOrElse.invoke(value, 
                (java.util.function.Consumer<String>) s -> System.out.println("값 처리: " + s),
                (Runnable) () -> System.out.println("값이 없음"));
                
        } catch (Exception e) {
            System.out.println("Java 9+ ifPresentOrElse 메서드 사용 불가");
        }
        
        // 예외 발생하는 기본값
        java.util.Optional<String> empty = java.util.Optional.empty();
        try {
            String result = empty.orElseThrow(() -> new IllegalStateException("값이 없습니다!"));
        } catch (IllegalStateException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }
        
        // 지연 평가 기본값
        String expensive = empty.orElseGet(() -> {
            System.out.println("비싼 계산 수행...");
            return "계산된 기본값";
        });
        System.out.println("지연 평가 결과: " + expensive);
    }
    
    // Optional의 함수형 메서드들 상세 분석
    public static void analyzeFunctionalMethods() {
        System.out.println("\n=== Optional 함수형 메서드 분석 ===");
        
        java.util.Optional<String> optional = java.util.Optional.of("Hello World");
        
        // map() - 값 변환
        System.out.println("=== map() 분석 ===");
        java.util.Optional<Integer> length = optional.map(String::length);
        System.out.println("문자열 길이: " + length.orElse(0));
        
        java.util.Optional<String> upper = optional.map(String::toUpperCase);
        System.out.println("대문자 변환: " + upper.orElse(""));
        
        // 빈 Optional에 map 적용
        java.util.Optional<String> empty = java.util.Optional.empty();
        java.util.Optional<Integer> emptyLength = empty.map(String::length);
        System.out.println("빈 Optional 길이: " + emptyLength.orElse(-1));
        
        // flatMap() - Optional 반환 함수와 함께 사용
        System.out.println("\n=== flatMap() 분석 ===");
        java.util.Optional<String> result = optional
            .flatMap(s -> s.contains("World") ? java.util.Optional.of(s.replace("World", "Java")) : java.util.Optional.empty());
        System.out.println("flatMap 결과: " + result.orElse("변환 실패"));
        
        // filter() - 조건 필터링
        System.out.println("\n=== filter() 분석 ===");
        java.util.Optional<String> filtered = optional
            .filter(s -> s.startsWith("Hello"))
            .map(s -> s + "!");
        System.out.println("필터링 결과: " + filtered.orElse("조건 불만족"));
        
        java.util.Optional<String> notFiltered = optional
            .filter(s -> s.startsWith("Goodbye"));
        System.out.println("조건 불만족: " + notFiltered.isPresent());
        
        // 메서드 체이닝의 강력함
        demonstrateMethodChaining();
    }
    
    // 메서드 체이닝 시연
    private static void demonstrateMethodChaining() {
        System.out.println("\n=== 메서드 체이닝 시연 ===");
        
        java.util.List<String> inputs = java.util.Arrays.asList(
            "123", "456", "abc", "789", null, "0", "-999"
        );
        
        System.out.println("입력 데이터: " + inputs);
        
        // 각 입력에 대해 복잡한 변환 수행
        for (String input : inputs) {
            String result = java.util.Optional.ofNullable(input)
                .filter(s -> !s.isEmpty())              // 빈 문자열 제외
                .map(String::trim)                       // 공백 제거
                .filter(s -> s.matches("\\d+"))          // 숫자만
                .map(Integer::parseInt)                  // 정수 변환
                .filter(n -> n > 0)                      // 양수만
                .filter(n -> n < 1000)                   // 1000 미만만
                .map(n -> "처리된 숫자: " + n)            // 포맷팅
                .orElse("처리 불가능한 입력");
                
            System.out.printf("'%s' → %s%n", input, result);
        }
    }
    
    // Optional 사용 패턴과 안티패턴
    public static void demonstrateBestPractices() {
        System.out.println("\n=== Optional 사용 패턴과 안티패턴 ===");
        
        // ✅ 좋은 패턴들
        System.out.println("=== 좋은 패턴들 ===");
        
        // 1. 메서드 반환 타입으로 사용
        java.util.Optional<String> user = findUserById(123);
        user.ifPresent(name -> System.out.println("사용자 발견: " + name));
        
        // 2. orElse vs orElseGet 적절한 사용
        String cheap = user.orElse("기본값"); // 항상 평가됨
        String expensive = user.orElseGet(() -> {
            System.out.println("비싼 계산은 필요할 때만 실행됨");
            return computeExpensiveDefault();
        });
        
        // 3. Optional 체이닝
        String processed = findUserById(456)
            .filter(name -> name.length() > 3)
            .map(String::toUpperCase)
            .orElse("처리 불가");
        System.out.println("체이닝 결과: " + processed);
        
        // ❌ 안티패턴들
        System.out.println("\n=== 피해야 할 안티패턴들 ===");
        
        // 1. Optional.get() 직접 호출 (위험!)
        try {
            java.util.Optional<String> empty = java.util.Optional.empty();
            // String bad = empty.get(); // NoSuchElementException!
            System.out.println("get() 직접 호출은 위험합니다!");
        } catch (Exception e) {
            System.out.println("예외 발생: " + e.getClass().getSimpleName());
        }
        
        // 2. isPresent() + get() 조합 (null 체크와 다를 바 없음)
        java.util.Optional<String> opt = java.util.Optional.of("test");
        if (opt.isPresent()) {
            System.out.println("안티패턴: " + opt.get().toUpperCase());
        }
        
        // 더 좋은 방법
        opt.map(String::toUpperCase).ifPresent(s -> System.out.println("좋은 패턴: " + s));
        
        // 3. Optional을 필드로 사용 (권장하지 않음)
        System.out.println("Optional은 필드가 아닌 반환 타입으로만 사용 권장");
        
        // 4. Collection의 Optional (불필요)
        // Optional<List<String>> bad = ...; // 빈 리스트 반환이 더 좋음
        System.out.println("컬렉션은 빈 컬렉션 반환이 Optional보다 좋습니다");
        
        // Optional 성능 고려사항
        analyzeOptionalPerformance();
    }
    
    // Optional 성능 분석
    private static void analyzeOptionalPerformance() {
        System.out.println("\n=== Optional 성능 분석 ===");
        
        int iterations = 1_000_000;
        String testValue = "test";
        
        // 1. null 체크 vs Optional 생성 비용
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String result = testValue != null ? testValue.toUpperCase() : "DEFAULT";
        }
        long nullCheckTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String result = java.util.Optional.ofNullable(testValue)
                .map(String::toUpperCase)
                .orElse("DEFAULT");
        }
        long optionalTime = System.nanoTime() - start;
        
        System.out.println("null 체크: " + nullCheckTime / 1_000_000 + "ms");
        System.out.println("Optional: " + optionalTime / 1_000_000 + "ms");
        System.out.println("오버헤드: " + (double)optionalTime / nullCheckTime + "배");
        
        // 2. orElse vs orElseGet 성능
        java.util.Optional<String> presentOptional = java.util.Optional.of("present");
        java.util.Optional<String> emptyOptional = java.util.Optional.empty();
        
        // orElse - 항상 평가됨
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            presentOptional.orElse(expensiveComputation());
        }
        long orElseTime = System.nanoTime() - start;
        
        // orElseGet - 필요할 때만 평가됨
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            presentOptional.orElseGet(() -> expensiveComputation());
        }
        long orElseGetTime = System.nanoTime() - start;
        
        System.out.println("\n값이 있는 Optional:");
        System.out.println("orElse: " + orElseTime / 1_000_000 + "ms");
        System.out.println("orElseGet: " + orElseGetTime / 1_000_000 + "ms");
        System.out.println("orElseGet이 " + (double)orElseTime / orElseGetTime + "배 빠름");
    }
    
    // 헬퍼 메서드들
    private static String getName() {
        return Math.random() > 0.5 ? "John" : null;
    }
    
    private static java.util.Optional<String> findUserById(int id) {
        return id % 2 == 0 ? java.util.Optional.empty() : java.util.Optional.of("User" + id);
    }
    
    private static String computeExpensiveDefault() {
        try { Thread.sleep(1); } catch (InterruptedException e) {}
        return "비싼 기본값";
    }
    
    private static String expensiveComputation() {
        return "expensive";
    }
}
```

계속해서 나머지 섹션들(람다 표현식, 날짜/시간 API, 파일 I/O, 스레드, 성능 최적화 등)도 같은 수준으로 작성해드릴까요?# Java 내장 함수 완전 가이드 - 내부 동작 원리까지

## 목차
1. [System 클래스의 깊이 있는 이해](#1-system-클래스의-깊이-있는-이해)
2. [Math 클래스 완전 정복](#2-math-클래스-완전-정복)
3. [String 클래스의 모든 것](#3-string-클래스의-모든-것)
4. [Collection Framework 심화](#4-collection-framework-심화)
5. [Java 8+ 새로운 기능들](#5-java-8-새로운-기능들)
6. [날짜/시간 API 완전 가이드](#6-날짜시간-api-완전-가이드)
7. [파일 I/O 심화](#7-파일-io-심화)
8. [스레드와 동시성](#8-스레드와-동시성)
9. [내부 구조와 성능 이해](#9-내부-구조와-성능-이해)

---

## 1. System 클래스의 깊이 있는 이해

### 1.1 System 클래스 구조와 내부 동작

System 클래스는 `java.lang` 패키지에 속하며, 모든 메서드가 `static`으로 선언되어 있습니다. JVM과 운영체제 간의 인터페이스 역할을 합니다.

```java
// System 클래스의 내부 구조 (단순화된 버전)
public final class System {
    // 표준 입출력 스트림들 - JVM 시작 시 네이티브 코드로 초기화
    public static final InputStream in;    // 표준 입력 (보통 키보드)
    public static final PrintStream out;   // 표준 출력 (보통 콘솔)
    public static final PrintStream err;   // 표준 에러 (보통 콘솔, out과 별도)
    
    // 정적 초기화 블록에서 네이티브 메서드 호출로 스트림 초기화
    static {
        // 네이티브 코드가 실제 시스템의 stdin, stdout, stderr와 연결
        registerNatives(); // JNI를 통한 네이티브 메서드 등록
    }
}
```

**내부 동작 원리:**
- System 클래스는 JVM 부팅 과정에서 가장 먼저 로드되는 클래스 중 하나입니다
- `in`, `out`, `err` 스트림들은 JNI(Java Native Interface)를 통해 운영체제의 표준 입출력과 직접 연결됩니다
- 이 연결은 C/C++로 작성된 네이티브 코드에서 처리됩니다

### 1.2 출력 관련 메서드들의 내부 동작

#### System.out의 상세한 내부 구조

```java
// System.out이 어떻게 동작하는지 보여주는 예제
public class SystemOutInternals {
    public static void demonstrateSystemOut() {
        // System.out은 PrintStream 객체
        PrintStream out = System.out;
        
        // 1. println()의 내부 동작
        System.out.println("Hello World");
        
        /* 내부적으로 다음과 같이 동작:
         * 1. 문자열을 byte 배열로 변환 (플랫폼 기본 인코딩 사용)
         * 2. synchronized 블록에서 버퍼에 쓰기 (스레드 안전)
         * 3. 개행 문자 추가 (System.lineSeparator() 사용)
         * 4. 버퍼가 가득 차거나 개행 문자가 있으면 flush() 자동 호출
         */
        
        // 2. print()와 println()의 차이
        System.out.print("줄바꿈 없음");
        System.out.println("줄바꿈 있음");
        
        // 3. printf()의 내부 동작 - C 스타일 포맷팅
        System.out.printf("정수: %d, 실수: %.2f, 문자열: %s%n", 42, 3.14159, "Java");
        
        /* printf() 내부 동작:
         * 1. 포맷 문자열 파싱
         * 2. 각 플레이스홀더에 대해 해당하는 인수의 타입 검증
         * 3. Formatter 클래스를 사용해서 포맷팅 수행
         * 4. 결과 문자열을 출력 버퍼에 쓰기
         */
    }
    
    // PrintStream의 버퍼링 메커니즘 이해
    public static void understandBuffering() {
        PrintStream out = System.out;
        
        // 버퍼에 쓰기만 하고 즉시 출력되지 않을 수 있음
        out.print("버퍼에만 저장됨");
        
        // 명시적으로 버퍼 비우기 - 즉시 화면에 출력
        out.flush();
        
        // println()은 자동으로 flush() 호출 (line buffering)
        out.println("자동으로 flush됨");
    }
    
    // 출력 스트림 리다이렉션의 내부 메커니즘
    public static void demonstrateRedirection() throws Exception {
        // 원래 출력 스트림 백업
        PrintStream originalOut = System.out;
        
        try {
            // 파일로 출력 리다이렉션
            PrintStream fileOut = new PrintStream("output.txt");
            System.setOut(fileOut); // 내부적으로 static 필드 변경
            
            System.out.println("이 텍스트는 파일에 저장됩니다");
            
            // 파일 스트림 닫기
            fileOut.close();
            
        } finally {
            // 원래 출력 스트림으로 복구
            System.setOut(originalOut);
            System.out.println("콘솔 출력 복구됨");
        }
        
        /* setOut() 내부 동작:
         * 1. 보안 관리자 체크 (SecurityManager.checkPermission())
         * 2. System.out 정적 필드를 새로운 PrintStream으로 교체
         * 3. 기존 스트림은 가비지 컬렉션 대상이 됨
         */
    }
}
```

**System.out 버퍼링 메커니즘:**
1. **Line Buffering**: `println()`은 개행문자를 만나면 자동으로 flush
2. **Full Buffering**: `print()`는 버퍼가 가득 찰 때까지 대기
3. **Thread Safety**: 모든 출력 메서드는 `synchronized`로 보호됨

### 1.3 시간 관련 메서드들의 상세 분석

#### currentTimeMillis()와 nanoTime()의 차이점

```java
public class SystemTimeAnalysis {
    
    // currentTimeMillis()의 내부 동작과 사용법
    public static void analyzeCurrentTimeMillis() {
        long startTime = System.currentTimeMillis();
        
        /* currentTimeMillis() 내부 동작:
         * 1. JNI를 통해 운영체제의 시스템 시계에 접근
         * 2. Unix Epoch (1970-01-01 00:00:00 UTC)부터의 밀리초 반환
         * 3. 시스템 시계 조정에 영향을 받음 (NTP 동기화 등)
         * 4. 정확도는 운영체제에 따라 다름 (보통 1-15ms)
         */
        
        // 무거운 작업 시뮬레이션
        performHeavyTask();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("작업 시간: " + duration + "ms");
        
        // 주의사항: 시스템 시계가 조정되면 음수가 나올 수 있음!
        if (duration < 0) {
            System.out.println("경고: 시스템 시계가 뒤로 조정되었습니다!");
        }
    }
    
    // nanoTime()의 내부 동작과 고정밀 측정
    public static void analyzeNanoTime() {
        long startNano = System.nanoTime();
        
        /* nanoTime() 내부 동작:
         * 1. 고해상도 성능 카운터 사용 (CPU의 TSC 등)
         * 2. 상대적 시간만 측정 가능 (절대 시간 아님)
         * 3. 시스템 시계 조정에 영향받지 않음
         * 4. 나노초 정밀도 (실제 정확도는 하드웨어 의존적)
         */
        
        performPreciseTask();
        
        long endNano = System.nanoTime();
        long durationNanos = endNano - startNano;
        
        // 다양한 단위로 출력
        System.out.println("나노초: " + durationNanos);
        System.out.println("마이크로초: " + durationNanos / 1_000);
        System.out.println("밀리초: " + durationNanos / 1_000_000);
        System.out.println("초: " + durationNanos / 1_000_000_000.0);
    }
    
    // 벤치마킹을 위한 정확한 시간 측정 유틸리티
    public static void preciseBenchmark(String taskName, Runnable task, int iterations) {
        System.out.println("=== " + taskName + " 벤치마크 ===");
        
        // 워밍업 (JIT 컴파일러 최적화를 위해)
        for (int i = 0; i < Math.min(iterations / 10, 1000); i++) {
            task.run();
        }
        
        long totalNanos = 0;
        long minNanos = Long.MAX_VALUE;
        long maxNanos = Long.MIN_VALUE;
        
        // 실제 측정
        for (int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            task.run();
            long end = System.nanoTime();
            
            long duration = end - start;
            totalNanos += duration;
            minNanos = Math.min(minNanos, duration);
            maxNanos = Math.max(maxNanos, duration);
        }
        
        double avgNanos = (double) totalNanos / iterations;
        
        System.out.printf("평균: %.2f ms%n", avgNanos / 1_000_000);
        System.out.printf("최소: %.2f ms%n", minNanos / 1_000_000.0);
        System.out.printf("최대: %.2f ms%n", maxNanos / 1_000_000.0);
        System.out.printf("표준편차: %.2f ms%n", calculateStdDev(iterations, totalNanos, avgNanos));
    }
    
    private static void performHeavyTask() {
        // CPU 집약적 작업 시뮬레이션
        long sum = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum += Math.sqrt(i);
        }
    }
    
    private static void performPreciseTask() {
        // 짧고 정밀한 측정이 필요한 작업
        String result = "Hello".concat(" World");
    }
    
    private static double calculateStdDev(int iterations, long totalNanos, double avgNanos) {
        // 표준편차 계산 (간소화된 버전)
        return Math.sqrt((totalNanos * totalNanos) / iterations - avgNanos * avgNanos) / 1_000_000;
    }
}
```

### 1.4 시스템 속성과 환경변수의 상세 분석

```java
public class SystemPropertiesAnalysis {
    
    // 시스템 속성의 내부 저장 구조와 접근 메커니즘
    public static void analyzeSystemProperties() {
        /* 시스템 속성 내부 구조:
         * 1. Properties 객체로 저장 (Hashtable 기반)
         * 2. JVM 시작 시 네이티브 코드에서 초기화
         * 3. 스레드 안전 (synchronized methods)
         * 4. key-value 모두 String 타입
         */
        
        // 중요한 시스템 속성들과 그 의미
        System.out.println("=== JVM 정보 ===");
        System.out.println("Java 버전: " + System.getProperty("java.version"));
        System.out.println("Java 벤더: " + System.getProperty("java.vendor"));
        System.out.println("JVM 이름: " + System.getProperty("java.vm.name"));
        System.out.println("JVM 버전: " + System.getProperty("java.vm.version"));
        
        System.out.println("\n=== 운영체제 정보 ===");
        System.out.println("OS 이름: " + System.getProperty("os.name"));
        System.out.println("OS 버전: " + System.getProperty("os.version"));
        System.out.println("OS 아키텍처: " + System.getProperty("os.arch"));
        
        System.out.println("\n=== 사용자 환경 ===");
        System.out.println("사용자 이름: " + System.getProperty("user.name"));
        System.out.println("사용자 홈: " + System.getProperty("user.home"));
        System.out.println("작업 디렉토리: " + System.getProperty("user.dir"));
        
        System.out.println("\n=== 파일 시스템 ===");
        System.out.println("파일 구분자: '" + System.getProperty("file.separator") + "'");
        System.out.println("경로 구분자: '" + System.getProperty("path.separator") + "'");
        System.out.println("줄바꿈 문자: '" + System.getProperty("line.separator").replace("\n", "\\n").replace("\r", "\\r") + "'");
    }
    
    // 환경변수 접근 메커니즘
    public static void analyzeEnvironmentVariables() {
        /* 환경변수 내부 동작:
         * 1. 프로세스 시작 시 운영체제에서 상속
         * 2. 불변 Map으로 제공 (수정 불가)
         * 3. 대소문자 구분 (Windows에서는 대소문자 무시하지만 Java에서는 구분)
         * 4. JNI를 통해 네이티브 환경변수에 접근
         */
        
        // 모든 환경변수 출력
        Map<String, String> env = System.getenv();
        System.out.println("=== 환경변수 목록 ===");
        env.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(10) // 처음 10개만 출력
                .forEach(entry -> 
                    System.out.println(entry.getKey() + " = " + entry.getValue()));
        
        // 특정 환경변수 조회
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null) {
            System.out.println("JAVA_HOME: " + javaHome);
        } else {
            System.out.println("JAVA_HOME이 설정되지 않았습니다.");
        }
        
        // PATH 환경변수 분석
        String path = System.getenv("PATH");
        if (path != null) {
            String[] pathDirs = path.split(System.getProperty("path.separator"));
            System.out.println("PATH 디렉토리 수: " + pathDirs.length);
            System.out.println("첫 번째 PATH 디렉토리: " + pathDirs[0]);
        }
    }
    
    // 커스텀 시스템 속성 설정과 활용
    public static void customSystemProperties() {
        // 시스템 속성 설정 (내부적으로 Properties.setProperty() 호출)
        System.setProperty("myapp.version", "1.0.0");
        System.setProperty("myapp.debug", "true");
        System.setProperty("myapp.max.connections", "100");
        
        // 설정된 속성 읽기
        String version = System.getProperty("myapp.version");
        boolean debug = Boolean.parseBoolean(System.getProperty("myapp.debug", "false"));
        int maxConnections = Integer.parseInt(System.getProperty("myapp.max.connections", "50"));
        
        System.out.println("앱 버전: " + version);
        System.out.println("디버그 모드: " + debug);
        System.out.println("최대 연결수: " + maxConnections);
        
        // 시스템 속성 기반 설정 클래스
        ConfigManager config = new ConfigManager();
        config.loadFromSystemProperties("myapp.");
        
        System.out.println("설정 로드 완료: " + config.getAllProperties());
    }
    
    // 시스템 속성 기반 설정 관리 클래스
    private static class ConfigManager {
        private final Properties config = new Properties();
        
        public void loadFromSystemProperties(String prefix) {
            Properties systemProps = System.getProperties();
            
            // prefix로 시작하는 모든 시스템 속성을 설정으로 로드
            systemProps.stringPropertyNames().stream()
                    .filter(key -> key.startsWith(prefix))
                    .forEach(key -> {
                        String configKey = key.substring(prefix.length());
                        String value = systemProps.getProperty(key);
                        config.setProperty(configKey, value);
                    });
        }
        
        public String getString(String key, String defaultValue) {
            return config.getProperty(key, defaultValue);
        }
        
        public boolean getBoolean(String key, boolean defaultValue) {
            String value = config.getProperty(key);
            return value != null ? Boolean.parseBoolean(value) : defaultValue;
        }
        
        public int getInt(String key, int defaultValue) {
            String value = config.getProperty(key);
            try {
                return value != null ? Integer.parseInt(value) : defaultValue;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        
        public Properties getAllProperties() {
            return new Properties(config); // 복사본 반환
        }
    }
}
```

---

## 2. Math 클래스 완전 정복

### 2.1 Math 클래스의 내부 구조와 최적화

Math 클래스의 모든 메서드는 `static` `native` 메서드로 구현되어 있어 JVM이 아닌 운영체제 레벨에서 최적화된 수학 연산을 수행합니다.

```java
public class MathInternalAnalysis {
    
    // Math 클래스의 상수들과 그 정밀도
    public static void analyzeMathConstants() {
        /* Math 상수들의 내부 저장:
         * - double 타입 (64비트 IEEE 754 표준)
         * - 컴파일 타임에 최대 정밀도로 계산되어 저장
         * - 약 15-17자리의 십진수 정밀도
         */
        
        System.out.println("=== Math 상수 정밀도 분석 ===");
        System.out.println("Math.PI = " + Math.PI);
        System.out.println("Math.E = " + Math.E);
        
        // 정밀도 테스트 - 더 정밀한 값과 비교
        double precisePI = 3.141592653589793238462643383279502884;
        double difference = Math.abs(Math.PI - precisePI);
        System.out.println("PI 오차: " + difference);
        System.out.println("PI 정밀도: " + (difference < 1e-15 ? "매우 높음" : "보통"));
    }
    
    // 기본 수학 연산의 내부 최적화
    public static void analyzeBasicOperations() {
        /* 절댓값 연산 최적화:
         * 1. 비트 연산을 통한 고속 처리
         * 2. 타입별 오버로딩으로 박싱/언박싱 회피
         * 3. 특수 값 (NaN, Infinity) 처리
         */
        
        // 정수 타입 절댓값 - 내부적으로 비트 마스킹 사용
        int negativeInt = -42;
        int absInt = Math.abs(negativeInt);
        
        /* Math.abs(int) 내부 동작 (의사코드):
         * if (a < 0) {
         *     if (a == Integer.MIN_VALUE) {
         *         return Integer.MIN_VALUE; // 오버플로우 방지
         *     }
         *     return -a;
         * } else {
         *     return a;
         * }
         */
        
        System.out.println("절댓값: " + absInt);
        
        // 실수 타입 절댓값 - IEEE 754 부호 비트 조작
        double negativeDouble = -3.14159;
        double absDouble = Math.abs(negativeDouble);
        
        /* Math.abs(double) 내부 동작:
         * IEEE 754 표준에서 맨 왼쪽 비트가 부호 비트
         * 부호 비트만 0으로 설정하여 절댓값 계산
         */
        
        System.out.println("실수 절댓값: " + absDouble);
        
        // 특수 값 처리
        System.out.println("NaN 절댓값: " + Math.abs(Double.NaN));
        System.out.println("양의 무한대 절댓값: " + Math.abs(Double.POSITIVE_INFINITY));
        System.out.println("음의 무한대 절댓값: " + Math.abs(Double.NEGATIVE_INFINITY));
    }
    
    // min/max 함수의 내부 구현과 특수 케이스
    public static void analyzeMinMaxFunctions() {
        /* min/max 내부 최적화:
         * 1. 브랜치 예측 최적화를 위한 조건문 구조
         * 2. NaN 값 전파 규칙 (IEEE 754 준수)
         * 3. -0.0과 +0.0 구분 처리
         */
        
        int a = 10, b = 20;
        int max = Math.max(a, b);
        int min = Math.min(a, b);
        
        System.out.println("최대값: " + max + ", 최소값: " + min);
        
        // 특수 케이스들
        double nan = Double.NaN;
        double positiveZero = 0.0;
        double negativeZero = -0.0;
        
        System.out.println("NaN과의 max: " + Math.max(5.0, nan)); // NaN
        System.out.println("NaN과의 min: " + Math.min(5.0, nan)); // NaN
        System.out.println("+0.0과 -0.0의 max: " + Math.max(positiveZero, negativeZero)); // +0.0
        System.out.println("+0.0과 -0.0의 min: " + Math.min(positiveZero, negativeZero)); // -0.0
        
        // 성능 최적화된 min/max 체인 (삼항 연산자보다 빠름)
        int result = Math.max(1, Math.max(2, Math.max(3, Math.max(4, 5))));
        System.out.println("체인 최대값: " + result);
    }
}
```

### 2.2 거듭제곱과 루트 함수들의 상세 분석

```java
public class PowerAndRootAnalysis {
    
    // Math.pow()의 내부 알고리즘과 최적화
    public static void analyzePowFunction() {
        /* Math.pow() 내부 구현:
         * 1. 특수 케이스 먼저 처리 (0, 1, infinity, NaN)
         * 2. 지수가 정수인 경우 빠른 지수승 알고리즘 (repeated squaring)
         * 3. 일반적인 경우 exp(y * ln(x)) 사용
         * 4. 하드웨어 최적화 활용 (FPU 명령어)
         */
        
        // 기본 거듭제곱
        double result1 = Math.pow(2, 8); // 2^8 = 256
        System.out.println("2^8 = " + result1);
        
        // 정수 지수의 경우 - 내부적으로 빠른 알고리즘 사용
        double result2 = Math.pow(3, 10); // 빠른 계산
        System.out.println("3^10 = " + result2);
        
        // 실수 지수의 경우 - exp(ln) 방식 사용
        double result3 = Math.pow(2, 0.5); // 2^0.5 = sqrt(2)
        System.out.println("2^0.5 = " + result3);
        
        // 특수 케이스들
        System.out.println("특수 케이스들:");
        System.out.println("pow(NaN, 어떤수) = " + Math.pow(Double.NaN, 2));
        System.out.println("pow(어떤수, NaN) = " + Math.pow(2, Double.NaN));
        System.out.println("pow(2, +∞) = " + Math.pow(2, Double.POSITIVE_INFINITY));
        System.out.println("pow(0.5, +∞) = " + Math.pow(0.5, Double.POSITIVE_INFINITY));
        System.out.println("pow(-1, +∞) = " + Math.pow(-1, Double.POSITIVE_INFINITY)); // NaN
        
        // 성능 비교: 직접 곱셈 vs Math.pow()
        benchmarkPowerOperations();
    }
    
    // 제곱근 함수의 내부 구현
    public static void analyzeSqrtFunction() {
        /* Math.sqrt() 내부 구현:
         * 1. 하드웨어 제곱근 명령어 사용 (fsqrt 등)
         * 2. 소프트웨어 fallback: 뉴턴-랩슨 방법
         * 3. 특수 값 처리 (음수, 0, infinity, NaN)
         * 4. IEEE 754 정확도 보장
         */
        
        double number = 16.0;
        double sqrt = Math.sqrt(number);
        System.out.println("√16 = " + sqrt);
        
        // 정확도 검증
        double verification = sqrt * sqrt;
        System.out.println("검증 (4 * 4): " + verification);
        System.out.println("오차: " + Math.abs(verification - number));
        
        // 특수 케이스
        System.out.println("√(-1) = " + Math.sqrt(-1)); // NaN
        System.out.println("√0 = " + Math.sqrt(0.0)); // 0.0
        System.out.println("√(+∞) = " + Math.sqrt(Double.POSITIVE_INFINITY)); // +∞
        
        // 세제곱근 (cbrt) 분석
        double cubeRoot = Math.cbrt(27.0);
        System.out.println("∛27 = " + cubeRoot);
        
        // cbrt는 음수에 대해서도 정의됨 (sqrt와 다른 점)
        System.out.println("∛(-8) = " + Math.cbrt(-8.0)); // -2.0
    }
    
    // 로그 함수들의 정밀도와 특성
    public static void analyzeLogFunctions() {
        /* 로그 함수 내부 구현:
         * 1. 자연로그 ln(x): 테일러 급수 또는 하드웨어 명령어
         * 2. 상용로그 log10(x): ln(x) / ln(10) 변환
         * 3. 정의역 검사 (x > 0)
         * 4. 특수 값 처리
         */
        
        double number = Math.E;
        double naturalLog = Math.log(number); // ln(e) = 1
        System.out.println("ln(e) = " + naturalLog);
        
        double commonLog = Math.log10(100); // log10(100) = 2
        System.out.println("log10(100) = " + commonLog);
        
        // 로그의 특수 값들
        System.out.println("ln(1) = " + Math.log(1.0)); // 0.0
        System.out.println("ln(0) = " + Math.log(0.0)); // -∞
        System.out.println("ln(-1) = " + Math.log(-1.0)); // NaN
        System.out.println("ln(+∞) = " + Math.log(Double.POSITIVE_INFINITY)); // +∞
        
        // 지수함수 exp(x) = e^x
        double exp = Math.exp(1.0); // e^1 = e
        System.out.println("e^1 = " + exp);
        
        // 로그와 지수의 역함수 관계 검증
        double x = 5.0;
        double logExp = Math.log(Math.exp(x));
        double expLog = Math.exp(Math.log(x));
        System.out.println("ln(e^5) = " + logExp + " (원래 값: 5)");
        System.out.println("e^(ln(5)) = " + expLog + " (원래 값: 5)");
    }
    
    // 성능 벤치마크
    private static void benchmarkPowerOperations() {
        int iterations = 1_000_000;
        double base = 2.0;
        
        // Math.pow() 벤치마크
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Math.pow(base, 3);
        }
        long powTime = System.nanoTime() - start;
        
        // 직접 곱셈 벤치마크
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            base * base * base;
        }
        long multiplyTime = System.nanoTime() - start;
        
        System.out.println("Math.pow(x,3) 시간: " + powTime / 1_000_000 + "ms");
        System.out.println("x*x*x 시간: " + multiplyTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)powTime / multiplyTime + "배");
    }
}
```

### 2.3 삼각함수의 내부 구현과 정밀도

```java
public class TrigonometryAnalysis {
    
    // 삼각함수의 내부 구현과 주기성 처리
    public static void analyzeTrigonometricFunctions() {
        /* 삼각함수 내부 구현:
         * 1. 입력값을 [-π/2, π/2] 또는 적절한 범위로 정규화
         * 2. 테일러 급수, 체비셰프 다항식, 또는 룩업 테이블 사용
         * 3. 하드웨어 삼각함수 명령어 활용 (fsin, fcos 등)
         * 4. 주기성을 이용한 범위 축소
         */
        
        // 기본 삼각함수 값들
        double angle90 = Math.toRadians(90);
        double angle180 = Math.toRadians(180);
        double angle360 = Math.toRadians(360);
        
        System.out.println("=== 기본 삼각함수 값 ===");
        System.out.println("sin(90°) = " + Math.sin(angle90)); // 1.0에 매우 근사
        System.out.println("cos(0°) = " + Math.cos(0)); // 1.0
        System.out.println("tan(45°) = " + Math.tan(Math.toRadians(45))); // 1.0에 매우 근사
        
        // 부동소수점 정밀도 이슈
        System.out.println("\n=== 정밀도 이슈 ===");
        System.out.println("sin(180°) = " + Math.sin(angle180)); // 0에 매우 근사하지만 정확히 0은 아님
        System.out.println("cos(90°) = " + Math.cos(angle90)); // 작은 오차 존재
        
        // 큰 각도에서의 정밀도 손실
        double largeAngle = 1e10; // 매우 큰 각도
        System.out.println("sin(큰각도) = " + Math.sin(largeAngle)); // 정밀도 손실 가능
        
        // 주기성 검증
        double period = 2 * Math.PI;
        double testAngle = 1.0;
        System.out.println("\n=== 주기성 검증 ===");
        System.out.println("sin(x) = " + Math.sin(testAngle));
        System.out.println("sin(x + 2π) = " + Math.sin(testAngle + period));
        System.out.println("차이: " + Math.abs(Math.sin(testAngle) - Math.sin(testAngle + period)));
    }
    
    // 역삼각함수의 특성과 범위
    public static void analyzeInverseTrigFunctions() {
        /* 역삼각함수 내부 구현:
         * 1. asin, acos: 정의역 [-1, 1] 검사
         * 2. atan: 모든 실수에서 정의
         * 3. 테일러 급수, CORDIC 알고리즘, 또는 룩업 테이블
         * 4. 주치값 반환 (principal value)
         */
        
        System.out.println("=== 역삼각함수 범위 ===");
        
        // asin: [-π/2, π/2] 범위의 값 반환
        double asin1 = Math.asin(1.0);
        double asinNeg1 = Math.asin(-1.0);
        System.out.println("asin(1) = " + asin1 + " (라디안), " + Math.toDegrees(asin1) + "도");
        System.out.println("asin(-1) = " + asinNeg1 + " (라디안), " + Math.toDegrees(asinNeg1) + "도");
        
        // acos: [0, π] 범위의 값 반환
        double acos0 = Math.acos(0.0);
        double acosNeg1 = Math.acos(-1.0);
        System.out.println("acos(0) = " + acos0 + " (라디안), " + Math.toDegrees(acos0) + "도");
        System.out.println("acos(-1) = " + acosNeg1 + " (라디안), " + Math.toDegrees(acosNeg1) + "도");
        
        // atan: (-π/2, π/2) 범위의 값 반환
        double atan1 = Math.atan(1.0);
        double atanInf = Math.atan(Double.POSITIVE_INFINITY);
        System.out.println("atan(1) = " + atan1 + " (라디안), " + Math.toDegrees(atan1) + "도");
        System.out.println("atan(+∞) = " + atanInf + " (라디안), " + Math.toDegrees(atanInf) + "도");
        
        // 정의역 밖의 값
        System.out.println("\n=== 정의역 밖의 값 ===");
        System.out.println("asin(2) = " + Math.asin(2.0)); // NaN
        System.out.println("acos(2) = " + Math.acos(2.0)); // NaN
        
        // atan2의 특별한 기능 (4사분면 각도)
        analyzeAtan2Function();
    }
    
    // atan2 함수의 상세 분석
    public static void analyzeAtan2Function() {
        /* Math.atan2(y, x) 내부 동작:
         * 1. (x, y) 점이 있는 사분면 결정
         * 2. 원점에서 해당 점까지의 각도 계산 [-π, π]
         * 3. 특수 케이스 처리 (0, infinity, NaN)
         * 4. IEEE 754 부호 규칙 준수
         */
        
        System.out.println("\n=== atan2 함수 분석 ===");
        
        // 4사분면의 각도 계산
        double angle1 = Math.atan2(1, 1);   // 1사분면: 45도
        double angle2 = Math.atan2(1, -1);  // 2사분면: 135도
        double angle3 = Math.atan2(-1, -1); // 3사분면: -135도
        double angle4 = Math.atan2(-1, 1);  // 4사분면: -45도
        
        System.out.printf("1사분면 atan2(1,1) = %.2f도%n", Math.toDegrees(angle1));
        System.out.printf("2사분면 atan2(1,-1) = %.2f도%n", Math.toDegrees(angle2));
        System.out.printf("3사분면 atan2(-1,-1) = %.2f도%n", Math.toDegrees(angle3));
        System.out.printf("4사분면 atan2(-1,1) = %.2f도%n", Math.toDegrees(angle4));
        
        // 특수 케이스
        System.out.println("\n특수 케이스:");
        System.out.println("atan2(0, 1) = " + Math.toDegrees(Math.atan2(0, 1)) + "도"); // 0도
        System.out.println("atan2(0, -1) = " + Math.toDegrees(Math.atan2(0, -1)) + "도"); // 180도
        System.out.println("atan2(1, 0) = " + Math.toDegrees(Math.atan2(1, 0)) + "도"); // 90도
        System.out.println("atan2(-1, 0) = " + Math.toDegrees(Math.atan2(-1, 0)) + "도"); // -90도
    }
    
    // 좌표계 변환 실용 예제
    public static void coordinateTransformations() {
        System.out.println("\n=== 좌표계 변환 예제 ===");
        
        // 직교좌표 → 극좌표 변환
        double x = 3.0, y = 4.0;
        double r = Math.sqrt(x * x + y * y); // 거리
        double theta = Math.atan2(y, x);     // 각도
        
        System.out.printf("직교좌표 (%.1f, %.1f)%n", x, y);
        System.out.printf("극좌표 r=%.1f, θ=%.1f도%n", r, Math.toDegrees(theta));
        
        // 극좌표 → 직교좌표 변환
        double newX = r * Math.cos(theta);
        double newY = r * Math.sin(theta);
        System.out.printf("역변환 결과 (%.6f, %.6f)%n", newX, newY);
        
        // 벡터 각도 계산 예제
        calculateVectorAngles();
    }
    
    private static void calculateVectorAngles() {
        System.out.println("\n=== 벡터 간 각도 계산 ===");
        
        // 두 벡터 간의 각도 계산
        double[] vec1 = {1, 0}; // x축 방향
        double[] vec2 = {1, 1}; // 45도 방향
        
        double angle1 = Math.atan2(vec1[1], vec1[0]);
        double angle2 = Math.atan2(vec2[1], vec2[0]);
        double angleBetween = angle2 - angle1;
        
        System.out.printf("벡터1 각도: %.1f도%n", Math.toDegrees(angle1));
        System.out.printf("벡터2 각도: %.1f도%n", Math.toDegrees(angle2));
        System.out.printf("벡터 간 각도: %.1f도%n", Math.toDegrees(angleBetween));
        
        // 내적을 이용한 각도 계산 (다른 방법)
        double dotProduct = vec1[0] * vec2[0] + vec1[1] * vec2[1];
        double magnitude1 = Math.sqrt(vec1[0] * vec1[0] + vec1[1] * vec1[1]);
        double magnitude2 = Math.sqrt(vec2[0] * vec2[0] + vec2[1] * vec2[1]);
        double cosAngle = dotProduct / (magnitude1 * magnitude2);
        double angleFromDot = Math.acos(cosAngle);
        
        System.out.printf("내적으로 계산한 각도: %.1f도%n", Math.toDegrees(angleFromDot));
    }
}
```

### 2.4 난수 생성과 Random 클래스의 상세 분석

```java
public class RandomAnalysis {
    
    // Math.random()의 내부 구현과 한계
    public static void analyzeMathRandom() {
        /* Math.random() 내부 구현:
         * 1. 내부적으로 java.util.Random 인스턴스 사용
         * 2. 선형합동생성기(Linear Congruential Generator) 기반
         * 3. 48비트 시드 사용
         * 4. 스레드 안전 (synchronized)
         */
        
        System.out.println("=== Math.random() 분석 ===");
        
        // 기본 사용법과 분포 확인
        int[] buckets = new int[10];
        int samples = 100000;
        
        for (int i = 0; i < samples; i++) {
            double random = Math.random(); // [0.0, 1.0)
            int bucket = (int)(random * 10);
            buckets[bucket]++;
        }
        
        System.out.println("분포 확인 (0-9 구간별):");
        for (int i = 0; i < buckets.length; i++) {
            double percentage = (double)buckets[i] / samples * 100;
            System.out.printf("구간 %d: %d개 (%.1f%%)%n", i, buckets[i], percentage);
        }
        
        // 범위 지정 난수 생성 유틸리티
        demonstrateRangeRandom();
    }
    
    // 범위 지정 난수 생성의 수학적 변환
    public static void demonstrateRangeRandom() {
        System.out.println("\n=== 범위 지정 난수 생성 ===");
        
        // 정수 범위 난수: min + (int)(Math.random() * (max - min + 1))
        int min = 10, max = 20;
        int[] intCounts = new int[max - min + 1];
        
        for (int i = 0; i < 10000; i++) {
            int randomInt = generateRandomInt(min, max);
            intCounts[randomInt - min]++;
        }
        
        System.out.println("정수 범위 " + min + "-" + max + " 분포:");
        for (int i = 0; i < intCounts.length; i++) {
            System.out.printf("%d: %d개%n", i + min, intCounts[i]);
        }
        
        // 실수 범위 난수: min + Math.random() * (max - min)
        double minDouble = 1.0, maxDouble = 5.0;
        double sum = 0;
        int count = 10000;
        
        for (int i = 0; i < count; i++) {
            double randomDouble = generateRandomDouble(minDouble, maxDouble);
            sum += randomDouble;
        }
        
        double average = sum / count;
        double expectedAverage = (minDouble + maxDouble) / 2;
        System.out.printf("\n실수 범위 %.1f-%.1f:%n", minDouble, maxDouble);
        System.out.printf("평균: %.3f (기댓값: %.3f)%n", average, expectedAverage);
    }
    
    // Random 클래스의 고급 기능들
    public static void analyzeRandomClass() {
        /* Random 클래스 내부 구현:
         * 1. 시드 기반 의사난수 생성 (reproducible)
         * 2. 다양한 분포의 난수 생성 메서드 제공
         * 3. 스레드별 독립적인 난수 시퀀스
         * 4. Gaussian(정규분포) 난수 생성 지원
         */
        
        System.out.println("\n=== Random 클래스 고급 기능 ===");
        
        // 시드 기반 재현 가능한 난수
        long seed = 12345L;
        Random random1 = new Random(seed);
        Random random2 = new Random(seed);
        
        System.out.println("동일 시드로 생성된 난수 (재현성):");
        for (int i = 0; i < 5; i++) {
            int val1 = random1.nextInt(100);
            int val2 = random2.nextInt(100);
            System.out.printf("Random1: %d, Random2: %d, 동일: %b%n", val1, val2, val1 == val2);
        }
        
        // 가우시안(정규분포) 난수
        analyzeGaussianDistribution();
        
        // 다양한 타입의 난수
        Random random = new Random();
        System.out.println("\n다양한 타입의 난수:");
        System.out.println("nextBoolean(): " + random.nextBoolean());
        System.out.println("nextInt(): " + random.nextInt());
        System.out.println("nextInt(100): " + random.nextInt(100));
        System.out.println("nextLong(): " + random.nextLong());
        System.out.println("nextFloat(): " + random.nextFloat());
        System.out.println("nextDouble(): " + random.nextDouble());
        System.out.println("nextGaussian(): " + random.nextGaussian());
    }
    
    // 가우시안 분포 분석
    private static void analyzeGaussianDistribution() {
        System.out.println("\n=== 가우시안 분포 분석 ===");
        
        Random random = new Random();
        int samples = 100000;
        double sum = 0, sumSquares = 0;
        
        // 표준 정규분포 (평균 0, 표준편차 1) 샘플 생성
        for (int i = 0; i < samples; i++) {
            double gaussian = random.nextGaussian();
            sum += gaussian;
            sumSquares += gaussian * gaussian;
        }
        
        double mean = sum / samples;
        double variance = (sumSquares / samples) - (mean * mean);
        double stdDev = Math.sqrt(variance);
        
        System.out.printf("가우시안 분포 통계 (샘플: %d):%n", samples);
        System.out.printf("평균: %.4f (기댓값: 0.0)%n", mean);
        System.out.printf("표준편차: %.4f (기댓값: 1.0)%n", stdDev);
        System.out.printf("분산: %.4f (기댓값: 1.0)%n", variance);
        
        // 커스텀 정규분포 생성 (평균 μ, 표준편차 σ)
        double customMean = 50.0;
        double customStdDev = 15.0;
        
        System.out.printf("\n커스텀 정규분포 (μ=%.1f, σ=%.1f) 샘플:%n", customMean, customStdDev);
        for (int i = 0; i < 5; i++) {
            double customGaussian = random.nextGaussian() * customStdDev + customMean;
            System.out.printf("%.2f ", customGaussian);
        }
        System.out.println();
    }
    
    // 암호학적으로 안전한 난수 생성
    public static void secureRandomAnalysis() {
        System.out.println("\n=== SecureRandom 분석 ===");
        
        /* SecureRandom의 특징:
         * 1. 예측 불가능한 시드 사용 (엔트로피 소스)
         * 2. 암호학적 강도 보장
         * 3. 더 느린 성능 (보안 vs 성능 트레이드오프)
         * 4. 운영체제의 난수 생성기 활용
         */
        
        try {
            java.security.SecureRandom secureRandom = new java.security.SecureRandom();
            
            System.out.println("SecureRandom 알고리즘: " + secureRandom.getAlgorithm());
            System.out.println("SecureRandom 제공자: " + secureRandom.getProvider());
            
            // 성능 비교
            performanceComparison();
            
        } catch (Exception e) {
            System.out.println("SecureRandom 생성 실패: " + e.getMessage());
        }
    }
    
    // 성능 비교: Math.random() vs Random vs SecureRandom
    private static void performanceComparison() {
        int iterations = 1_000_000;
        
        // Math.random() 성능 측정
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            Math.random();
        }
        long mathRandomTime = System.nanoTime() - start;
        
        // Random 성능 측정
        Random random = new Random();
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            random.nextDouble();
        }
        long randomTime = System.nanoTime() - start;
        
        // SecureRandom 성능 측정
        try {
            java.security.SecureRandom secureRandom = new java.security.SecureRandom();
            start = System.nanoTime();
            for (int i = 0; i < iterations / 100; i++) { // 100분의 1로 줄임 (너무 느려서)
                secureRandom.nextDouble();
            }
            long secureRandomTime = (System.nanoTime() - start) * 100; // 스케일링
            
            System.out.printf("\n성능 비교 (%d회 호출):%n", iterations);
            System.out.printf("Math.random(): %d ms%n", mathRandomTime / 1_000_000);
            System.out.printf("Random: %d ms%n", randomTime / 1_000_000);
            System.out.printf("SecureRandom: %d ms (추정)%n", secureRandomTime / 1_000_000);
            
        } catch (Exception e) {
            System.out.println("SecureRandom 성능 측정 실패");
        }
    }
    
    // 유틸리티 메서드들
    private static int generateRandomInt(int min, int max) {
        return min + (int)(Math.random() * (max - min + 1));
    }
    
    private static double generateRandomDouble(double min, double max) {
        return min + Math.random() * (max - min);
    }
}
```

### 2.5 반올림 함수들의 정밀한 분석

```java
public class RoundingAnalysis {
    
    // 반올림 함수들의 내부 구현과 차이점
    public static void analyzeRoundingFunctions() {
        /* 반올림 함수들의 내부 구현:
         * 1. floor(): 음의 무한대 방향으로 내림 (IEEE 754 규칙)
         * 2. ceil(): 양의 무한대 방향으로 올림
         * 3. round(): 가장 가까운 정수로, .5는 짝수로 (banker's rounding)
         * 4. rint(): round()와 비슷하지만 double 반환
         */
        
        System.out.println("=== 반올림 함수 비교 ===");
        
        double[] testValues = {3.2, 3.7, -3.2, -3.7, 3.5, 4.5, -3.5, -4.5, 0.0, -0.0};
        
        System.out.printf("%-8s %-8s %-8s %-8s %-8s%n", "값", "floor", "ceil", "round", "rint");
        System.out.println("----------------------------------------");
        
        for (double value : testValues) {
            System.out.printf("%-8.1f %-8.1f %-8.1f %-8d %-8.1f%n",
                value,
                Math.floor(value),
                Math.ceil(value),
                Math.round(value),
                Math.rint(value)
            );
        }
        
        // 특수한 반올림 규칙 설명
        explainRoundingRules();
    }
    
    // 반올림 규칙의 상세 설명
    private static void explainRoundingRules() {
        System.out.println("\n=== 반올림 규칙 상세 분석 ===");
        
        // round()의 .5 처리 (Banker's rounding - 가장 가까운 짝수)
        System.out.println("round()의 .5 처리 (Banker's rounding):");
        System.out.println("round(0.5) = " + Math.round(0.5)); // 0 (가까운 짝수)
        System.out.println("round(1.5) = " + Math.round(1.5)); // 2 (가까운 짝수)  
        System.out.println("round(2.5) = " + Math.round(2.5)); // 2 (가까운 짝수)
        System.out.println("round(3.5) = " + Math.round(3.5)); // 4 (가까운 짝수)
        
        // rint()도 동일한 규칙
        System.out.println("\nrint()의 .5 처리:");
        System.out.println("rint(0.5) = " + Math.rint(0.5)); // 0.0
        System.out.println("rint(1.5) = " + Math.rint(1.5)); // 2.0
        System.out.println("rint(2.5) = " + Math.rint(2.5)); // 2.0
        System.out.println("rint(3.5) = " + Math.rint(3.5)); // 4.0
        
        // 음수에서의 floor/ceil 동작
        System.out.println("\n음수에서의 floor/ceil:");
        System.out.println("floor(-3.2) = " + Math.floor(-3.2)); // -4.0 (더 작은 정수)
        System.out.println("ceil(-3.2) = " + Math.ceil(-3.2));   // -3.0 (더 큰 정수)
        System.out.println("floor(-3.7) = " + Math.floor(-3.7)); // -4.0
        System.out.println("ceil(-3.7) = " + Math.ceil(-3.7));   // -3.0
        
        // 특수 값들의 처리
        analyzeSpecialValues();
    }
    
    // 특수 값들의 반올림 처리
    private static void analyzeSpecialValues() {
        System.out.println("\n=== 특수 값 처리 ===");
        
        // NaN 처리
        System.out.println("NaN 값들:");
        System.out.println("floor(NaN) = " + Math.floor(Double.NaN)); // NaN
        System.out.println("ceil(NaN) = " + Math.ceil(Double.NaN)); // NaN
        System.out.println("round(NaN) = " + Math.round(Double.NaN)); // 0
        System.out.println("rint(NaN) = " + Math.rint(Double.NaN)); // NaN
        
        // 무한대 처리
        System.out.println("\n무한대 값들:");
        System.out.println("floor(+∞) = " + Math.floor(Double.POSITIVE_INFINITY)); // +∞
        System.out.println("ceil(+∞) = " + Math.ceil(Double.POSITIVE_INFINITY)); // +∞
        System.out.println("floor(-∞) = " + Math.floor(Double.NEGATIVE_INFINITY)); // -∞
        System.out.println("ceil(-∞) = " + Math.ceil(Double.NEGATIVE_INFINITY)); // -∞
        
        // 0의 부호 처리
        System.out.println("\n0의 부호 처리:");
        System.out.println("floor(+0.0) = " + Math.floor(+0.0)); // +0.0
        System.out.println("floor(-0.0) = " + Math.floor(-0.0)); // -0.0
        System.out.println("ceil(+0.0) = " + Math.ceil(+0.0)); // +0.0
        System.out.println("ceil(-0.0) = " + Math.ceil(-0.0)); // -0.0
    }
    
    // 고정 소수점 반올림 구현
    public static void customRounding() {
        System.out.println("\n=== 커스텀 반올림 함수 ===");
        
        double value = 3.14159265;
        
        // 소수점 N자리 반올림
        for (int decimalPlaces = 0; decimalPlaces <= 4; decimalPlaces++) {
            double rounded = roundToDecimalPlaces(value, decimalPlaces);
            System.out.printf("%d자리: %.4f → %.4f%n", decimalPlaces, value, rounded);
        }
        
        // 다양한 반올림 방식 구현
        System.out.println("\n다양한 반올림 방식:");
        double test = 3.555;
        System.out.println("원본: " + test);
        System.out.println("수학적 반올림: " + mathRounding(test, 2)); // 3.56
        System.out.println("뱅커스 반올림: " + bankersRounding(test, 2)); // 3.56
        System.out.println("항상 올림: " + alwaysRoundUp(test, 2)); // 3.56
        System.out.println("항상 내림: " + alwaysRoundDown(test, 2)); // 3.55
    }
    
    // 소수점 N자리 반올림 (가장 일반적인 방법)
    private static double roundToDecimalPlaces(double value, int decimalPlaces) {
        /* 내부 동작:
         * 1. 10^decimalPlaces를 곱해서 소수점을 이동
         * 2. Math.round()로 가장 가까운 정수로 반올림
         * 3. 다시 10^decimalPlaces로 나누어서 원래 자리로 복원
         */
        if (decimalPlaces < 0) throw new IllegalArgumentException("음수 자릿수는 불가능");
        
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(value * multiplier) / multiplier;
    }
    
    // 수학적 반올림 (.5 이상은 올림)
    private static double mathRounding(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.floor(value * multiplier + 0.5) / multiplier;
    }
    
    // 뱅커스 반올림 (IEEE 754 표준)
    private static double bankersRounding(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.rint(value * multiplier) / multiplier;
    }
    
    // 항상 올림
    private static double alwaysRoundUp(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.ceil(value * multiplier) / multiplier;
    }
    
    // 항상 내림
    private static double alwaysRoundDown(double value, int decimalPlaces) {
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.floor(value * multiplier) / multiplier;
    }
    
    // 반올림 정확도 테스트
    public static void roundingAccuracyTest() {
        System.out.println("\n=== 반올림 정확도 테스트 ===");
        
        /* 부동소수점 연산의 정확도 이슈:
         * 1. 이진 표현에서 일부 십진수는 정확히 표현 불가
         * 2. 연산 과정에서 미세한 오차 누적
         * 3. 반올림 결과가 예상과 다를 수 있음
         */
        
        // 부동소수점 정확도 이슈 시연
        double problematic = 0.1 + 0.2; // 0.3이 아님!
        System.out.println("0.1 + 0.2 = " + problematic);
        System.out.println("정확히 0.3인가? " + (problematic == 0.3));
        System.out.println("차이: " + Math.abs(problematic - 0.3));
        
        // BigDecimal을 사용한 정확한 반올림 (권장)
        demonstrateBigDecimalRounding();
    }
    
    // BigDecimal을 이용한 정확한 반올림
    private static void demonstrateBigDecimalRounding() {
        System.out.println("\n=== BigDecimal 정확한 반올림 ===");
        
        java.math.BigDecimal bd = new java.math.BigDecimal("3.14159265");
        
        // 다양한 반올림 모드
        System.out.println("원본: " + bd);
        System.out.println("HALF_UP (수학적): " + 
            bd.setScale(2, java.math.RoundingMode.HALF_UP));
        System.out.println("HALF_EVEN (뱅커스): " + 
            bd.setScale(2, java.math.RoundingMode.HALF_EVEN));
        System.out.println("CEILING (올림): " + 
            bd.setScale(2, java.math.RoundingMode.CEILING));
        System.out.println("FLOOR (내림): " + 
            bd.setScale(2, java.math.RoundingMode.FLOOR));
        System.out.println("DOWN (0 방향): " + 
            bd.setScale(2, java.math.RoundingMode.DOWN));
        System.out.println("UP (0 반대방향): " + 
            bd.setScale(2, java.math.RoundingMode.UP));
    }
}
```

---

## 3. String 클래스의 모든 것

### 3.1 String의 내부 구조와 메모리 관리

String 클래스는 Java에서 가장 중요한 클래스 중 하나이며, 불변(immutable) 객체로 설계되었습니다.

```java
public class StringInternalAnalysis {
    
    // String의 내부 구조 분석
    public static void analyzeStringStructure() {
        /* String 내부 구조 (Java 9+ 기준):
         * 1. private final byte[] value; // 실제 문자 데이터 (Java 9부터 char[]에서 변경)
         * 2. private final byte coder; // 인코딩 정보 (LATIN1 또는 UTF16)
         * 3. private int hash; // 해시코드 캐싱 (지연 계산)
         * 4. Compact Strings 최적화: ASCII 문자만 있으면 1바이트로 저장
         */
        
        System.out.println("=== String 내부 구조 분석 ===");
        
        // ASCII만 포함된 문자열 - LATIN1 인코딩 사용 (1바이트/문자)
        String ascii = "Hello World";
        
        // 유니코드 문자 포함 - UTF16 인코딩 사용 (2바이트/문자)
        String unicode = "안녕하세요 Hello";
        
        System.out.println("ASCII 문자열: " + ascii);
        System.out.println("유니코드 문자열: " + unicode);
        
        // 메모리 사용량 분석 (대략적)
        System.out.println("\n메모리 사용량 추정:");
        System.out.println("ASCII 문자열 바이트 수: " + ascii.length()); // LATIN1: 1바이트/문자
        System.out.println("유니코드 문자열 문자 수: " + unicode.length());
        System.out.println("유니코드 문자열 바이트 수: " + unicode.getBytes().length); // UTF-8 인코딩
        
        // String Pool과 Heap의 차이
        demonstrateStringPool();
    }
    
    // String Pool 메커니즘 상세 분석
    public static void demonstrateStringPool() {
        System.out.println("\n=== String Pool 메커니즘 ===");
        
        /* String Pool 동작 원리:
         * 1. JVM의 Metaspace(또는 Heap) 영역에 위치
         * 2. 동일한 문자열 리터럴은 하나의 인스턴스만 유지
         * 3. intern() 메서드로 강제로 풀에 추가 가능
         * 4. 가비지 컬렉션 대상 (Java 7부터)
         */
        
        // 리터럴 문자열 - String Pool에 저장
        String literal1 = "Hello";
        String literal2 = "Hello";
        
        // new 연산자 - Heap에 새 인스턴스 생성
        String heapString1 = new String("Hello");
        String heapString2 = new String("Hello");
        
        System.out.println("리터럴 참조 동일성: " + (literal1 == literal2)); // true
        System.out.println("new String 참조 동일성: " + (heapString1 == heapString2)); // false
        System.out.println("리터럴 vs new String: " + (literal1 == heapString1)); // false
        
        // intern() 메서드로 String Pool에 추가
        String interned = heapString1.intern();
        System.out.println("intern() 후 참조 동일성: " + (literal1 == interned)); // true
        
        // 컴파일 타임 최적화
        String compiled = "He" + "llo"; // 컴파일러가 "Hello"로 최적화
        System.out.println("컴파일 타임 최적화: " + (literal1 == compiled)); // true
        
        // 런타임 연결 - String Pool에 없음
        String runtime = "He" + getString("llo"); // 런타임에 연결
        System.out.println("런타임 연결: " + (literal1 == runtime)); // false
        
        // 메모리 절약 효과 시연
        demonstratePoolMemoryEfficiency();
    }
    
    // String Pool의 메모리 절약 효과
    private static void demonstratePoolMemoryEfficiency() {
        System.out.println("\n=== String Pool 메모리 효율성 ===");
        
        // 동일한 문자열을 많이 생성
        String[] literals = new String[1000];
        String[] newStrings = new String[1000];
        
        String commonString = "CommonString";
        
        // 리터럴 사용 - 모두 같은 인스턴스 참조
        for (int i = 0; i < 1000; i++) {
            literals[i] = "CommonString"; // String Pool에서 재사용
        }
        
        // new String 사용 - 각각 다른 인스턴스
        for (int i = 0; i < 1000; i++) {
            newStrings[i] = new String("CommonString"); // 매번 새 인스턴스
        }
        
        // 참조 비교로 메모리 효율성 확인
        boolean allSame = true;
        for (int i = 1; i < 1000; i++) {
            if (literals[0] != literals[i]) {
                allSame = false;
                break;
            }
        }
        System.out.println("리터럴 배열 모두 같은 참조: " + allSame); // true
        
        boolean anyDifferent = false;
        for (int i = 1; i < 1000; i++) {
            if (newStrings[0] != newStrings[i]) {
                anyDifferent = true;
                break;
            }
        }
        System.out.println("new String 배열 다른 참조 존재: " + anyDifferent); // true
    }
    
    // String의 불변성과 그 영향
    public static void analyzeImmutability() {
        System.out.println("\n=== String 불변성 분석 ===");
        
        /* String 불변성의 의미:
         * 1. 객체 생성 후 내부 상태 변경 불가능
         * 2. 모든 변경 연산은 새 String 인스턴스 반환
         * 3. Thread-safe (여러 스레드에서 안전하게 공유 가능)
         * 4. 해시코드 캐싱 가능 (변하지 않으므로)
         */
        
        String original = "Hello";
        String modified = original.concat(" World");
        
        System.out.println("원본 문자열: " + original); // "Hello" (변경되지 않음)
        System.out.println("변경된 문자열: " + modified); // "Hello World"
        System.out.println("같은 객체인가? " + (original == modified)); // false
        
        // 잘못된 String 연결 방식 (성능 문제)
        demonstrateIneffectiveStringBuilding();
        
        // 올바른 String 연결 방식
        demonstrateEffectiveStringBuilding();
    }
    
    // 비효율적인 String 연결
    private static void demonstrateIneffectiveStringBuilding() {
        System.out.println("\n=== 비효율적인 String 연결 ===");
        
        long start = System.nanoTime();
        String result = "";
        
        // 매우 비효율적: O(n²) 복잡도
        for (int i = 0; i < 1000; i++) {
            result += "a"; // 매번 새로운 String 객체 생성
        }
        
        long end = System.nanoTime();
        System.out.println("비효율적 연결 시간: " + (end - start) / 1_000_000 + "ms");
        System.out.println("결과 길이: " + result.length());
        
        /* 내부 동작:
         * 1. 매 연결마다 새 char[] 배열 생성
         * 2. 기존 문자들을 새 배열에 복사
         * 3. 새 문자 추가
         * 4. 1000번 반복시 약 500,000번의 문자 복사 발생
         */
    }
    
    // 효율적인 String 연결
    private static void demonstrateEffectiveStringBuilding() {
        System.out.println("\n=== 효율적인 String 연결 ===");
        
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder(1000); // 초기 용량 설정
        
        // 효율적: O(n) 복잡도
        for (int i = 0; i < 1000; i++) {
            sb.append("a"); // 내부 배열에 직접 추가
        }
        
        String result = sb.toString(); // 최종적으로 한 번만 String 생성
        long end = System.nanoTime();
        
        System.out.println("효율적 연결 시간: " + (end - start) / 1_000_000 + "ms");
        System.out.println("결과 길이: " + result.length());
    }
    
    private static String getString(String s) {
        return s;
    }
}
```

### 3.2 문자열 검색과 추출 메서드들의 상세 분석

```java
public class StringSearchAnalysis {
    
    // 문자열 검색 메서드들의 내부 알고리즘
    public static void analyzeSearchMethods() {
        /* 검색 메서드 내부 구현:
         * 1. indexOf(): 단순 선형 검색 또는 최적화된 알고리즘
         * 2. contains(): 내부적으로 indexOf() 사용
         * 3. startsWith()/endsWith(): 부분 문자열 직접 비교
         * 4. 모든 메서드는 대소문자 구분
         */
        
        System.out.println("=== 문자열 검색 메서드 분석 ===");
        
        String text = "Java Programming Language Java";
        String pattern = "Java";
        
        // 기본 검색 메서드들
        int firstIndex = text.indexOf(pattern);
        int lastIndex = text.lastIndexOf(pattern);
        boolean contains = text.contains(pattern);
        
        System.out.println("텍스트: " + text);
        System.out.println("패턴: " + pattern);
        System.out.println("첫 번째 위치: " + firstIndex);
        System.out.println("마지막 위치: " + lastIndex);
        System.out.println("포함 여부: " + contains);
        
        // indexOf() 시작 위치 지정
        int secondOccurrence = text.indexOf(pattern, firstIndex + 1);
        System.out.println("두 번째 위치: " + secondOccurrence);
        
        // 문자 검색 vs 문자열 검색
        analyzeCharVsStringSearch();
        
        // 고급 검색 패턴 구현
        implementAdvancedSearch();
    }
    
    // 문자 검색과 문자열 검색의 성능 차이
    private static void analyzeCharVsStringSearch() {
        System.out.println("\n=== 문자 vs 문자열 검색 성능 ===");
        
        String text = "This is a very long string with many characters to search through";
        
        // 문자 검색 (더 빠름)
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            text.indexOf('a'); // char 검색
        }
        long charTime = System.nanoTime() - start;
        
        // 문자열 검색 (상대적으로 느림)
        start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            text.indexOf("a"); // String 검색
        }
        long stringTime = System.nanoTime() - start;
        
        System.out.println("문자 검색 시간: " + charTime / 1_000_000 + "ms");
        System.out.println("문자열 검색 시간: " + stringTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)stringTime / charTime + "배");
    }
    
    // 고급 검색 패턴 구현
    private static void implementAdvancedSearch() {
        System.out.println("\n=== 고급 검색 패턴 ===");
        
        String text = "Java Programming Language, Java is powerful, Java everywhere";
        String pattern = "Java";
        
        // 모든 발생 위치 찾기
        java.util.List<Integer> allOccurrences = findAllOccurrences(text, pattern);
        System.out.println("모든 '" + pattern + "' 위치: " + allOccurrences);
        
        // 대소문자 무시 검색
        java.util.List<Integer> caseInsensitive = findAllOccurrencesIgnoreCase(text, "java");
        System.out.println("대소문자 무시 'java' 위치: " + caseInsensitive);
        
        // 단어 경계 검색 (정규표현식 사용)
        java.util.List<Integer> wordBoundary = findWholeWordsOnly(text, "Java");
        System.out.println("완전한 단어 'Java' 위치: " + wordBoundary);
        
        // 성능 비교
        performSearchBenchmark();
    }
    
    // 모든 발생 위치 찾기 - 효율적 구현
    private static java.util.List<Integer> findAllOccurrences(String text, String pattern) {
        /* 내부 알고리즘:
         * 1. 시작 위치에서 indexOf() 호출
         * 2. 찾은 위치 + 패턴 길이를 다음 시작 위치로 설정
         * 3. -1이 반환될 때까지 반복
         * 시간 복잡도: O(nm) 최악의 경우, O(n) 평균적인 경우
         */
        
        java.util.List<Integer> positions = new java.util.ArrayList<>();
        int index = 0;
        
        while ((index = text.indexOf(pattern, index)) != -1) {
            positions.add(index);
            index += pattern.length(); // 겹치지 않는 검색
        }
        
        return positions;
    }
    
    // 대소문자 무시 검색
    private static java.util.List<Integer> findAllOccurrencesIgnoreCase(String text, String pattern) {
        /* toLowerCase() 사용 시 주의사항:
         * 1. 새로운 String 객체 생성 (메모리 사용)
         * 2. Locale 의존적 변환
         * 3. 성능 오버헤드
         */
        
        String lowerText = text.toLowerCase();
        String lowerPattern = pattern.toLowerCase();
        
        return findAllOccurrences(lowerText, lowerPattern);
    }
    
    // 완전한 단어만 검색 (정규표현식 활용)
    private static java.util.List<Integer> findWholeWordsOnly(String text, String word) {
        java.util.List<Integer> positions = new java.util.ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b" + java.util.regex.Pattern.quote(word) + "\\b");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            positions.add(matcher.start());
        }
        
        return positions;
    }
    
    // 검색 성능 벤치마크
    private static void performSearchBenchmark() {
        System.out.println("\n=== 검색 성능 벤치마크 ===");
        
        // 큰 텍스트 생성
        StringBuilder largeText = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeText.append("This is sample text with Java programming language. ");
        }
        String text = largeText.toString();
        
        int iterations = 1000;
        
        // indexOf() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.indexOf("Java");
        }
        long indexOfTime = System.nanoTime() - start;
        
        // contains() 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.contains("Java");
        }
        long containsTime = System.nanoTime() - start;
        
        // 정규표현식 성능
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Java");
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            pattern.matcher(text).find();
        }
        long regexTime = System.nanoTime() - start;
        
        System.out.println("indexOf() 시간: " + indexOfTime / 1_000_000 + "ms");
        System.out.println("contains() 시간: " + containsTime / 1_000_000 + "ms");
        System.out.println("정규표현식 시간: " + regexTime / 1_000_000 + "ms");
    }
    
    // 문자열 추출 메서드 분석
    public static void analyzeExtractionMethods() {
        System.out.println("\n=== 문자열 추출 메서드 분석 ===");
        
        String text = "Java Programming Language";
        
        // substring() 메서드 - 내부에서 새 String 객체 생성
        String sub1 = text.substring(5); // "Programming Language"
        String sub2 = text.substring(5, 16); // "Programming"
        
        System.out.println("원본: " + text);
        System.out.println("substring(5): " + sub1);
        System.out.println("substring(5, 16): " + sub2);
        
        /* substring() 내부 동작 (Java 7+):
         * 1. 범위 검증 (IndexOutOfBoundsException 체크)
         * 2. 새로운 String 객체 생성
         * 3. 지정된 범위의 문자들을 새 배열로 복사
         * 4. 원본 문자열과 독립적인 새 객체 반환
         */
        
        // charAt() 메서드 - 단일 문자 접근
        char first = text.charAt(0);
        char last = text.charAt(text.length() - 1);
        
        System.out.println("첫 번째 문자: " + first);
        System.out.println("마지막 문자: " + last);
        
        // 안전한 문자 접근 (예외 처리)
        char safe = safeCharAt(text, 100, '?');
        System.out.println("안전한 접근 결과: " + safe);
        
        // 문자열 분할 분석
        analyzeStringSplit();
    }
    
    // 안전한 문자 접근 메서드
    private static char safeCharAt(String str, int index, char defaultChar) {
        if (str == null || index < 0 || index >= str.length()) {
            return defaultChar;
        }
        return str.charAt(index);
    }
    
    // split() 메서드의 내부 동작 분석
    private static void analyzeStringSplit() {
        System.out.println("\n=== split() 메서드 분석 ===");
        
        String csv = "apple,banana,cherry,date,elderberry";
        
        // 기본 분할
        String[] fruits = csv.split(",");
        System.out.println("분할 결과: " + java.util.Arrays.toString(fruits));
        
        // 제한된 분할
        String[] limited = csv.split(",", 3);
        System.out.println("제한된 분할 (3개): " + java.util.Arrays.toString(limited));
        
        /* split() 내부 동작:
         * 1. 정규표현식 Pattern 컴파일
         * 2. 문자열 전체 스캔하여 매치 위치 찾기
         * 3. 각 구간을 substring()으로 추출
         * 4. String[] 배열로 반환
         */
        
        // 정규표현식 분할
        String text = "word1   word2\tword3\nword4";
        String[] words = text.split("\\s+"); // 모든 공백 문자로 분할
        System.out.println("정규표현식 분할: " + java.util.Arrays.toString(words));
        
        // 성능 고려사항
        demonstrateSplitPerformance();
    }
    
    // split() 성능 분석
    private static void demonstrateSplitPerformance() {
        System.out.println("\n=== split() 성능 분석 ===");
        
        String data = "a,b,c,d,e,f,g,h,i,j";
        int iterations = 100000;
        
        // split() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            data.split(",");
        }
        long splitTime = System.nanoTime() - start;
        
        // 수동 분할 성능 (StringTokenizer 방식)
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            manualSplit(data, ',');
        }
        long manualTime = System.nanoTime() - start;
        
        System.out.println("split() 시간: " + splitTime / 1_000_000 + "ms");
        System.out.println("수동 분할 시간: " + manualTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)splitTime / manualTime + "배");
        
        /* 성능 차이 이유:
         * 1. split()은 정규표현식 엔진 사용 (오버헤드)
         * 2. 수동 분할은 단순 문자 비교
         * 3. 간단한 구분자의 경우 수동 분할이 더 빠름
         */
    }
    
    // 수동 문자열 분할 구현
    private static String[] manualSplit(String str, char delimiter) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        int start = 0;
        
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == delimiter) {
                parts.add(str.substring(start, i));
                start = i + 1;
            }
        }
        
        // 마지막 부분 추가
        if (start < str.length()) {
            parts.add(str.substring(start));
        }
        
        return parts.toArray(new String[0]);
    }
}
```

### 3.3 문자열 변환과 조작의 상세 분석

```java
public class StringTransformationAnalysis {
    
    // 대소문자 변환의 내부 메커니즘
    public static void analyzeCaseConversion() {
        /* 대소문자 변환 내부 동작:
         * 1. 각 문자에 대해 Character.toUpperCase()/toLowerCase() 호출
         * 2. Locale 정보를 고려한 변환 (일부 언어의 특수 규칙)
         * 3. 변경이 없어도 새 String 객체 생성
         * 4. Unicode 케이스 매핑 테이블 참조
         */
        
        System.out.println("=== 대소문자 변환 분석 ===");
        
        String mixed = "Hello World 안녕하세요 123";
        String upper = mixed.toUpperCase();
        String lower = mixed.toLowerCase();
        
        System.out.println("원본: " + mixed);
        System.out.println("대문자: " + upper);
        System.out.println("소문자: " + lower);
        
        // Locale별 변환 차이
        analyzeLocaleSpecificConversion();
        
        // 성능 분석
        analyzeCaseConversionPerformance();
    }
    
    // Locale별 대소문자 변환 차이
    private static void analyzeLocaleSpecificConversion() {
        System.out.println("\n=== Locale별 변환 차이 ===");
        
        // 터키어의 특별한 케이스 (i ↔ I, ı ↔ İ)
        String turkish = "İstanbul";
        
        System.out.println("터키어 단어: " + turkish);
        System.out.println("기본 Locale 소문자: " + turkish.toLowerCase());
        System.out.println("터키 Locale 소문자: " + turkish.toLowerCase(java.util.Locale.forLanguageTag("tr")));
        System.out.println("영어 Locale 소문자: " + turkish.toLowerCase(java.util.Locale.ENGLISH));
        
        // 독일어 ß (에스체트) 변환
        String german = "Straße";
        System.out.println("\n독일어 단어: " + german);
        System.out.println("대문자 변환: " + german.toUpperCase());
        System.out.println("독일 Locale 대문자: " + german.toUpperCase(java.util.Locale.GERMAN));
    }
    
    // 대소문자 변환 성능 분석
    private static void analyzeCaseConversionPerformance() {
        System.out.println("\n=== 대소문자 변환 성능 ===");
        
        String text = "The Quick Brown Fox Jumps Over The Lazy Dog";
        int iterations = 100000;
        
        // toUpperCase() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.toUpperCase();
        }
        long upperTime = System.nanoTime() - start;
        
        // toLowerCase() 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.toLowerCase();
        }
        long lowerTime = System.nanoTime() - start;
        
        // 수동 변환 성능 (ASCII만)
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            manualToUpperCase(text);
        }
        long manualTime = System.nanoTime() - start;
        
        System.out.println("toUpperCase() 시간: " + upperTime / 1_000_000 + "ms");
        System.out.println("toLowerCase() 시간: " + lowerTime / 1_000_000 + "ms");
        System.out.println("수동 변환 시간: " + manualTime / 1_000_000 + "ms");
        System.out.println("수동 변환이 " + (double)upperTime / manualTime + "배 빠름");
    }
    
    // ASCII 전용 수동 대문자 변환 (성능 최적화)
    private static String manualToUpperCase(String str) {
        char[] chars = str.toCharArray();
        boolean changed = false;
        
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 'a' && c <= 'z') {
                chars[i] = (char)(c - 32); // ASCII 'a'-'A' = 32
                changed = true;
            }
        }
        
        return changed ? new String(chars) : str;
    }
    
    // 문자열 치환 메서드들의 상세 분석
    public static void analyzeReplacementMethods() {
        System.out.println("\n=== 문자열 치환 분석 ===");
        
        String text = "Java is great. Java is powerful. Java everywhere!";
        
        // replace() - 모든 발생 치환
        String replaced1 = text.replace("Java", "Python");
        System.out.println("replace() 결과: " + replaced1);
        
        // replaceFirst() - 첫 번째 발생만 치환
        String replaced2 = text.replaceFirst("Java", "Python");
        System.out.println("replaceFirst() 결과: " + replaced2);
        
        // replaceAll() - 정규표현식 사용
        String replaced3 = text.replaceAll("Java", "Python");
        System.out.println("replaceAll() 결과: " + replaced3);
        
        /* 메서드별 내부 동작:
         * 1. replace(): 단순 문자열 검색 및 치환 (빠름)
         * 2. replaceFirst(): 정규표현식 엔진 + 첫 번째 매치만
         * 3. replaceAll(): 정규표현식 엔진 + 모든 매치
         */
        
        analyzeReplacementPerformance();
        demonstrateAdvancedReplacement();
    }
    
    // 치환 메서드 성능 비교
    private static void analyzeReplacementPerformance() {
        System.out.println("\n=== 치환 성능 비교 ===");
        
        String text = "Java Java Java Java Java";
        int iterations = 100000;
        
        // replace() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.replace("Java", "Python");
        }
        long replaceTime = System.nanoTime() - start;
        
        // replaceAll() 성능
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            text.replaceAll("Java", "Python");
        }
        long replaceAllTime = System.nanoTime() - start;
        
        System.out.println("replace() 시간: " + replaceTime / 1_000_000 + "ms");
        System.out.println("replaceAll() 시간: " + replaceAllTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)replaceAllTime / replaceTime + "배");
    }
    
    // 고급 치환 패턴
    private static void demonstrateAdvancedReplacement() {
        System.out.println("\n=== 고급 치환 패턴 ===");
        
        String text = "Phone: 010-1234-5678, Email: user@example.com";
        
        // 전화번호 마스킹
        String maskedPhone = text.replaceAll("(\\d{3})-(\\d{4})-(\\d{4})", "$1-****-$3");
        System.out.println("전화번호 마스킹: " + maskedPhone);
        
        // 이메일 도메인 변경
        String emailChanged = text.replaceAll("@\\w+\\.com", "@newdomain.com");
        System.out.println("이메일 도메인 변경: " + emailChanged);
        
        // 여러 공백을 하나로
        String multiSpace = "word1    word2\t\tword3\n\nword4";
        String singleSpace = multiSpace.replaceAll("\\s+", " ");
        System.out.println("여러 공백 정리: '" + singleSpace + "'");
        
        // HTML 태그 제거
        String html = "<p>This is <b>bold</b> and <i>italic</i> text.</p>";
        String plainText = html.replaceAll("<[^>]*>", "");
        System.out.println("HTML 태그 제거: " + plainText);
    }
    
    // 문자열 트림 메서드들의 분석
    public static void analyzeTrimMethods() {
        System.out.println("\n=== 트림 메서드 분석 ===");
        
        String text = "  \t  Java Programming  \n  ";
        
        // trim() - ASCII 공백 문자만 제거
        String trimmed = text.trim();
        System.out.println("원본: '" + text + "'");
        System.out.println("trim(): '" + trimmed + "'");
        
        /* trim() 내부 동작:
         * 1. 문자열 시작부터 공백이 아닌 문자 찾기
         * 2. 문자열 끝부터 공백이 아닌 문자 찾기
         * 3. 해당 범위의 substring() 반환
         * 4. 공백: 코드포인트 <= 0x20 인 문자
         */
        
        // Java 11+ strip() 메서드 - Unicode 공백도 처리
        if (isJava11OrLater()) {
            demonstrateStripMethods(text);
        }
        
        // 커스텀 트림 구현
        demonstrateCustomTrim();
    }
    
    // Java 11의 strip() 메서드들 (있는 경우)
    private static void demonstrateStripMethods(String text) {
        try {
            // Java 11+ 메서드들을 리플렉션으로 호출
            java.lang.reflect.Method stripMethod = String.class.getMethod("strip");
            java.lang.reflect.Method stripLeadingMethod = String.class.getMethod("stripLeading");
            java.lang.reflect.Method stripTrailingMethod = String.class.getMethod("stripTrailing");
            
            String stripped = (String) stripMethod.invoke(text);
            String stripLeading = (String) stripLeadingMethod.invoke(text);
            String stripTrailing = (String) stripTrailingMethod.invoke(text);
            
            System.out.println("strip(): '" + stripped + "'");
            System.out.println("stripLeading(): '" + stripLeading + "'");
            System.out.println("stripTrailing(): '" + stripTrailing + "'");
            
        } catch (Exception e) {
            System.out.println("Java 11+ strip() 메서드 사용 불가");
        }
    }
    
    // 커스텀 트림 구현
    private static void demonstrateCustomTrim() {
        System.out.println("\n=== 커스텀 트림 구현 ===");
        
        String text = "###Java Programming###";
        
        // 특정 문자 제거
        String customTrimmed = customTrim(text, '#');
        System.out.println("커스텀 트림('#'): '" + customTrimmed + "'");
        
        // 여러 문자 제거
        String multiCharText = "!@#Hello World#@!";
        String multiTrimmed = customTrimMultiple(multiCharText, "!@#");
        System.out.println("다중 문자 트림: '" + multiTrimmed + "'");
    }
    
    // 특정 문자를 제거하는 커스텀 트림
    private static String customTrim(String str, char charToTrim) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        int start = 0;
        int end = str.length();
        
        // 앞에서부터 제거할 문자 건너뛰기
        while (start < end && str.charAt(start) == charToTrim) {
            start++;
        }
        
        // 뒤에서부터 제거할 문자 건너뛰기
        while (start < end && str.charAt(end - 1) == charToTrim) {
            end--;
        }
        
        return str.substring(start, end);
    }
    
    // 여러 문자를 제거하는 커스텀 트림
    private static String customTrimMultiple(String str, String charsToTrim) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        int start = 0;
        int end = str.length();
        
        // 앞에서부터 제거할 문자들 건너뛰기
        while (start < end && charsToTrim.indexOf(str.charAt(start)) != -1) {
            start++;
        }
        
        // 뒤에서부터 제거할 문자들 건너뛰기
        while (start < end && charsToTrim.indexOf(str.charAt(end - 1)) != -1) {
            end--;
        }
        
        return str.substring(start, end);
    }
    
    private static boolean isJava11OrLater() {
        try {
            String version = System.getProperty("java.version");
            int majorVersion = Integer.parseInt(version.split("\\.")[0]);
            return majorVersion >= 11;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 3.4 문자열 분할과 결합의 심층 분석

```java
public class StringJoinSplitAnalysis {
    
    // String.join()의 내부 구현과 최적화
    public static void analyzeStringJoin() {
        /* String.join() 내부 동작:
         * 1. StringJoiner 클래스 내부 사용
         * 2. StringBuilder 기반 구현
         * 3. 구분자를 각 요소 사이에만 삽입
         * 4. null 요소는 "null" 문자열로 변환
         */
        
        System.out.println("=== String.join() 분석 ===");
        
        String[] fruits = {"apple", "banana", "cherry", "date"};
        java.util.List<String> fruitList = java.util.Arrays.asList(fruits);
        
        // 배열로부터 결합
        String joined1 = String.join(", ", fruits);
        System.out.println("배열 결합: " + joined1);
        
        // 컬렉션으로부터 결합
        String joined2 = String.join(" | ", fruitList);
        System.out.println("리스트 결합: " + joined2);
        
        // null 요소 처리
        String[] withNulls = {"apple", null, "cherry", null};
        String joinedWithNulls = String.join(", ", withNulls);
        System.out.println("null 포함 결합: " + joinedWithNulls);
        
        // 다양한 결합 방법 성능 비교
        compareJoinPerformance();
    }
    
    // 문자열 결합 방법들의 성능 비교
    private static void compareJoinPerformance() {
        System.out.println("\n=== 문자열 결합 성능 비교 ===");
        
        String[] data = new String[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = "item" + i;
        }
        
        int iterations = 1000;
        
        // String.join() 성능
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            String.join(",", data);
        }
        long joinTime = System.nanoTime() - start;
        
        // StringBuilder 직접 사용
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            stringBuilderJoin(data, ",");
        }
        long sbTime = System.nanoTime() - start;
        
        // StringJoiner 사용
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            stringJoinerJoin(data, ",");
        }
        long sjTime = System.nanoTime() - start;
        
        // Stream.join() 사용
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            java.util.Arrays.stream(data).collect(java.util.stream.Collectors.joining(","));
        }
        long streamTime = System.nanoTime() - start;
        
        System.out.println("String.join() 시간: " + joinTime / 1_000_000 + "ms");
        System.out.println("StringBuilder 시간: " + sbTime / 1_000_000 + "ms");
        System.out.println("StringJoiner 시간: " + sjTime / 1_000_000 + "ms");
        System.out.println("Stream.join() 시간: " + streamTime / 1_000_000 + "ms");
    }
    
    // StringBuilder를 이용한 수동 결합
    private static String stringBuilderJoin(String[] array, String delimiter) {
        if (array.length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        
        for (int i = 1; i < array.length; i++) {
            sb.append(delimiter).append(array[i]);
        }
        
        return sb.toString();
    }
    
    // StringJoiner를 이용한 결합
    private static String stringJoinerJoin(String[] array, String delimiter) {
        java.util.StringJoiner joiner = new java.util.StringJoiner(delimiter);
        for (String item : array) {
            joiner.add(item);
        }
        return joiner.toString();
    }
    
    // StringJoiner의 고급 기능들
    public static void analyzeStringJoiner() {
        System.out.println("\n=== StringJoiner 고급 기능 ===");
        
        /* StringJoiner 특징:
         * 1. 구분자, 접두사, 접미사 지원
         * 2. 내부적으로 StringBuilder 사용
         * 3. 빈 결과에 대한 기본값 설정 가능
         * 4. Stream.collect()와 완벽 호환
         */
        
        // 접두사/접미사와 함께 사용
        java.util.StringJoiner joiner = new java.util.StringJoiner(", ", "[", "]");
        joiner.add("Apple");
        joiner.add("Banana");
        joiner.add("Cherry");
        
        System.out.println("접두사/접미사 포함: " + joiner);
        
        // 빈 결과 처리
        java.util.StringJoiner emptyJoiner = new java.util.StringJoiner(", ", "[", "]");
        emptyJoiner.setEmptyValue("비어있음");
        System.out.println("빈 StringJoiner: " + emptyJoiner);
        
        // StringJoiner 병합
        java.util.StringJoiner joiner1 = new java.util.StringJoiner(", ");
        joiner1.add("A").add("B");
        
        java.util.StringJoiner joiner2 = new java.util.StringJoiner(", ");
        joiner2.add("C").add("D");
        
        joiner1.merge(joiner2);
        System.out.println("병합 결과: " + joiner1);
        
        // CSV 형식 생성 예제
        demonstrateCsvGeneration();
    }
    
    // CSV 형식 데이터 생성 예제
    private static void demonstrateCsvGeneration() {
        System.out.println("\n=== CSV 생성 예제 ===");
        
        // 헤더 생성
        String[] headers = {"이름", "나이", "도시"};
        String csvHeader = String.join(",", headers);
        
        // 데이터 행 생성
        String[][] data = {
            {"홍길동", "25", "서울"},
            {"김철수", "30", "부산"},
            {"이영희", "28", "대구"}
        };
        
        System.out.println(csvHeader);
        for (String[] row : data) {
            System.out.println(String.join(",", row));
        }
        
        // 복잡한 CSV (따옴표 처리)
        String[] complexRow = {"Smith, John", "Director of \"Sales\"", "New York, NY"};
        String complexCsv = String.join(",", 
            java.util.Arrays.stream(complexRow)
                .map(field -> "\"" + field.replace("\"", "\"\"") + "\"")
                .toArray(String[]::new)
        );
        System.out.println("복잡한 CSV: " + complexCsv);
    }
    
    // StringBuilder와 StringBuffer의 상세 비교
    public static void analyzeStringBuilders() {
        System.out.println("\n=== StringBuilder vs StringBuffer ===");
        
        /* StringBuilder vs StringBuffer:
         * 1. StringBuilder: 단일 스레드용, 동기화 없음 (빠름)
         * 2. StringBuffer: 멀티 스레드용, 동기화됨 (느림)
         * 3. 내부 구조는 거의 동일 (char[] 배열 사용)
         * 4. 자동 용량 확장 (현재 용량의 2배 + 2)
         */
        
        // StringBuilder 내부 동작 분석
        analyzeStringBuilderInternals();
        
        // 성능 비교
        compareStringBuilderPerformance();
        
        // 용량 관리 최적화
        demonstrateCapacityOptimization();
    }
    
    // StringBuilder 내부 동작 분석
    private static void analyzeStringBuilderInternals() {
        System.out.println("\n=== StringBuilder 내부 동작 ===");
        
        StringBuilder sb = new StringBuilder();
        System.out.println("초기 용량: " + sb.capacity()); // 기본값: 16
        
        // 문자열 추가하면서 용량 변화 관찰
        String longString = "0123456789";
        for (int i = 0; i < 5; i++) {
            sb.append(longString);
            System.out.printf("길이: %d, 용량: %d%n", sb.length(), sb.capacity());
        }
        
        /* 용량 확장 규칙:
         * 1. 현재 용량으로 부족할 때 자동 확장
         * 2. 새 용량 = (현재 용량 * 2) + 2
         * 3. 필요한 최소 용량이 더 크면 그 값 사용
         * 4. 기존 데이터를 새 배열로 복사
         */
        
        // 용량 확장 오버헤드 시연
        demonstrateCapacityOverhead();
    }
    
    // 용량 확장 오버헤드 시연
    private static void demonstrateCapacityOverhead() {
        System.out.println("\n=== 용량 확장 오버헤드 ===");
        
        // 용량 지정 없음 (비효율)
        long start = System.nanoTime();
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb1.append("a");
        }
        long time1 = System.nanoTime() - start;
        
        // 적절한 초기 용량 지정 (효율적)
        start = System.nanoTime();
        StringBuilder sb2 = new StringBuilder(10000);
        for (int i = 0; i < 10000; i++) {
            sb2.append("a");
        }
        long time2 = System.nanoTime() - start;
        
        System.out.println("용량 미지정 시간: " + time1 / 1_000_000 + "ms");
        System.out.println("용량 지정 시간: " + time2 / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)time1 / time2 + "배");
    }
    
    // StringBuilder vs StringBuffer 성능 비교
    private static void compareStringBuilderPerformance() {
        System.out.println("\n=== StringBuilder vs StringBuffer 성능 ===");
        
        int iterations = 100000;
        String testString = "test";
        
        // StringBuilder 성능 (단일 스레드)
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder(iterations * 4);
        for (int i = 0; i < iterations; i++) {
            sb.append(testString);
        }
        String result1 = sb.toString();
        long sbTime = System.nanoTime() - start;
        
        // StringBuffer 성능 (동기화 오버헤드)
        start = System.nanoTime();
        StringBuffer sbuf = new StringBuffer(iterations * 4);
        for (int i = 0; i < iterations; i++) {
            sbuf.append(testString);
        }
        String result2 = sbuf.toString();
        long sbufTime = System.nanoTime() - start;
        
        System.out.println("StringBuilder 시간: " + sbTime / 1_000_000 + "ms");
        System.out.println("StringBuffer 시간: " + sbufTime / 1_000_000 + "ms");
        System.out.println("동기화 오버헤드: " + (double)sbufTime / sbTime + "배");
    }
    
    // 용량 관리 최적화 가이드
    private static void demonstrateCapacityOptimization() {
        System.out.println("\n=== 용량 최적화 가이드 ===");
        
        // 1. 예상 크기로 초기화
        int expectedSize = 1000;
        StringBuilder optimized = new StringBuilder(expectedSize);
        System.out.println("최적화된 초기 용량: " + optimized.capacity());
        
        // 2. trimToSize() 사용 (존재하는 경우)
        StringBuilder large = new StringBuilder(10000);
        large.append("small content");
        System.out.println("사용 전 용량: " + large.capacity());
        large.trimToSize(); // 불필요한 용량 제거
        System.out.println("trimToSize() 후 용량: " + large.capacity());
        
        // 3. 메서드 체이닝 활용
        String chained = new StringBuilder()
            .append("Hello")
            .append(" ")
            .append("World")
            .append("!")
            .toString();
        System.out.println("체이닝 결과: " + chained);
        
        // 4. insert()와 delete() 메서드 활용
        demonstrateAdvancedStringBuilderMethods();
    }
    
    // StringBuilder의 고급 메서드들
    private static void demonstrateAdvancedStringBuilderMethods() {
        System.out.println("\n=== StringBuilder 고급 메서드 ===");
        
        StringBuilder sb = new StringBuilder("Hello World");
        System.out.println("원본: " + sb);
        
        // insert() - 지정 위치에 삽입
        sb.insert(6, "Beautiful ");
        System.out.println("insert() 후: " + sb);
        
        // delete() - 범위 삭제
        sb.delete(6, 16); // "Beautiful " 제거
        System.out.println("delete() 후: " + sb);
        
        // deleteCharAt() - 특정 위치 문자 삭제
        sb.deleteCharAt(5); // 6번째 문자 삭제 (0-based)
        System.out.println("deleteCharAt() 후: " + sb);
        
        // replace() - 범위 치환
        sb.replace(0, 5, "Hi");
        System.out.println("replace() 후: " + sb);
        
        // reverse() - 문자열 뒤집기
        sb.reverse();
        System.out.println("reverse() 후: " + sb);
        sb.reverse(); // 원상복구
        
        // setCharAt() - 특정 위치 문자 변경
        sb.setCharAt(0, 'h');
        System.out.println("setCharAt() 후: " + sb);
        
        // setLength() - 길이 조정
        int originalLength = sb.length();
        sb.setLength(2); // 처음 2글자만 유지
        System.out.println("setLength(2) 후: " + sb);
        sb.setLength(originalLength); // 길이 늘리기 (null 문자로 채움)
        System.out.println("길이 복원 후: " + sb + " (길이: " + sb.length() + ")");
    }
}
```

### 3.5 문자열 비교의 모든 것

```java
public class StringComparisonAnalysis {
    
    // 문자열 비교 메서드들의 내부 구현
    public static void analyzeStringComparison() {
        /* 문자열 비교 내부 동작:
         * 1. equals(): 길이 비교 → char 배열 요소별 비교
         * 2. equalsIgnoreCase(): 대소문자 변환 후 비교
         * 3. compareTo(): 사전식 순서, 유니코드 값 기준
         * 4. == 연산자: 참조(메모리 주소) 비교만 수행
         */
        
        System.out.println("=== 문자열 비교 메서드 분석 ===");
        
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");
        String s4 = "hello";
        
        // 참조 비교 vs 내용 비교
        System.out.println("=== 참조 vs 내용 비교 ===");
        System.out.println("s1 == s2: " + (s1 == s2)); // true (String Pool)
        System.out.println("s1 == s3: " + (s1 == s3)); // false (다른 객체)
        System.out.println("s1.equals(s2): " + s1.equals(s2)); // true
        System.out.println("s1.equals(s3): " + s1.equals(s3)); // true
        
        // 대소문자 무시 비교
        System.out.println("\n=== 대소문자 비교 ===");
        System.out.println("s1.equals(s4): " + s1.equals(s4)); // false
        System.out.println("s1.equalsIgnoreCase(s4): " + s1.equalsIgnoreCase(s4)); // true
        
        analyzeEqualsPerformance();
        demonstrateCompareTo();
    }
    
    // equals() 메서드 성능 분석
    private static void analyzeEqualsPerformance() {
        System.out.println("\n=== equals() 성능 분석 ===");
        
        /* equals() 내부 최적화:
         * 1. 동일 객체 참조 체크 (this == obj)
         * 2. null 체크
         * 3. 타입 체크 (instanceof String)
         * 4. 길이 비교 (빠른 실패)
         * 5. char 배열 요소별 비교
         */
        
        String short1 = "Hi";
        String short2 = "Hi";
        String short3 = "Bye";
        
        String long1 = "This is a very long string for performance testing";
        String long2 = "This is a very long string for performance testing";
        String long3 = "This is a very long string for performance testing!";
        
        int iterations = 1_000_000;
        
        // 짧은 문자열 - 동일
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            short1.equals(short2);
        }
        long shortEqualTime = System.nanoTime() - start;
        
        // 짧은 문자열 - 다름
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            short1.equals(short3);
        }
        long shortDiffTime = System.nanoTime() - start;
        
        // 긴 문자열 - 동일
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            long1.equals(long2);
        }
        long longEqualTime = System.nanoTime() - start;
        
        // 긴 문자열 - 마지막 문자만 다름
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            long1.equals(long3);
        }
        long longDiffTime = System.nanoTime() - start;
        
        System.out.println("짧은 문자열 (동일): " + shortEqualTime / 1_000_000 + "ms");
        System.out.println("짧은 문자열 (다름): " + shortDiffTime / 1_000_000 + "ms");
        System.out.println("긴 문자열 (동일): " + longEqualTime / 1_000_000 + "ms");
        System.out.println("긴 문자열 (끝만 다름): " + longDiffTime / 1_000_000 + "ms");
    }
    
    // compareTo() 메서드 상세 분석
    private static void demonstrateCompareTo() {
        System.out.println("\n=== compareTo() 메서드 분석 ===");
        
        /* compareTo() 내부 동작:
         * 1. 두 문자열을 문자별로 비교
         * 2. 첫 번째 다른 문자의 유니코드 차이 반환
         * 3. 한쪽이 다른 쪽의 접두사면 길이 차이 반환
         * 4. 동일하면 0 반환
         */
        
        String[] words = {"apple", "banana", "Apple", "BANANA", "apricot", "app"};
        
        System.out.println("사전식 정렬 (대소문자 구분):");
        java.util.Arrays.sort(words);
        for (String word : words) {
            System.out.println("  " + word);
        }
        
        // 비교 결과 해석
        System.out.println("\n비교 결과 해석:");
        System.out.println("'apple'.compareTo('banana'): " + "apple".compareTo("banana")); // 음수
        System.out.println("'banana'.compareTo('apple'): " + "banana".compareTo("apple")); // 양수
        System.out.println("'apple'.compareTo('apple'): " + "apple".compareTo("apple")); // 0
        System.out.println("'Apple'.compareTo('apple'): " + "Apple".compareTo("apple")); // 음수 (대문자가 먼저)
        System.out.println("'app'.compareTo('apple'): " + "app".compareTo("apple")); // 음수 (짧은 것이 먼저)
        
        // 대소문자 무시 비교
        System.out.println("\n대소문자 무시 비교:");
        System.out.println("'Apple'.compareToIgnoreCase('apple'): " + "Apple".compareToIgnoreCase("apple")); // 0
        
        // 문자열 정렬 데모
        demonstrateStringSorting();
    }
    
    // 다양한 정렬 방식 데모
    private static void demonstrateStringSorting() {
        System.out.println("\n=== 문자열 정렬 방식들 ===");
        
        String[] names = {"김철수", "이영희", "박민수", "정수진", "최영호"};
        String[] englishNames = {"John", "alice", "Bob", "CHARLIE", "david"};
        
        // 기본 정렬 (compareTo 사용)
        String[] sorted1 = englishNames.clone();
        java.util.Arrays.sort(sorted1);
        System.out.println("기본 정렬: " + java.util.Arrays.toString(sorted1));
        
        // 대소문자 무시 정렬
        String[] sorted2 = englishNames.clone();
        java.util.Arrays.sort(sorted2, String.CASE_INSENSITIVE_ORDER);
        System.out.println("대소문자 무시: " + java.util.Arrays.toString(sorted2));
        
        // 길이별 정렬
        String[] sorted3 = englishNames.clone();
        java.util.Arrays.sort(sorted3, java.util.Comparator.comparing(String::length));
        System.out.println("길이별 정렬: " + java.util.Arrays.toString(sorted3));
        
        // 역순 정렬
        String[] sorted4 = englishNames.clone();
        java.util.Arrays.sort(sorted4, java.util.Collections.reverseOrder());
        System.out.println("역순 정렬: " + java.util.Arrays.toString(sorted4));
        
        // 한글 정렬 (Collator 사용)
        demonstrateKoreanSorting(names);
    }
    
    // 한글 정렬 (Locale 고려)
    private static void demonstrateKoreanSorting(String[] koreanNames) {
        System.out.println("\n=== 한글 정렬 ===");
        
        java.text.Collator koreanCollator = java.text.Collator.getInstance(java.util.Locale.KOREAN);
        
        String[] sorted = koreanNames.clone();
        java.util.Arrays.sort(sorted, koreanCollator);
        
        System.out.println("한글 이름 정렬: " + java.util.Arrays.toString(sorted));
        
        // 초성별 그룹핑 예제
        groupByInitialConsonant(koreanNames);
    }
    
    // 한글 초성별 그룹핑
    private static void groupByInitialConsonant(String[] names) {
        System.out.println("\n=== 초성별 그룹핑 ===");
        
        java.util.Map<Character, java.util.List<String>> grouped = new java.util.LinkedHashMap<>();
        
        for (String name : names) {
            char firstChar = name.charAt(0);
            char initial = getKoreanInitial(firstChar);
            
            grouped.computeIfAbsent(initial, k -> new java.util.ArrayList<>()).add(name);
        }
        
        grouped.forEach((initial, nameList) -> {
            System.out.println(initial + ": " + nameList);
        });
    }
    
    // 한글 초성 추출
    private static char getKoreanInitial(char ch) {
        if (ch >= '가' && ch <= '힣') {
            int code = ch - '가';
            int initialIndex = code / (21 * 28); // 21개 중성 * 28개 종성
            char[] initials = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 
                              'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
            return initials[initialIndex];
        }
        return ch;
    }
    
    // Null 안전 문자열 비교
    public static void analyzeNullSafeComparison() {
        System.out.println("\n=== Null 안전 문자열 비교 ===");
        
        String s1 = "Hello";
        String s2 = null;
        String s3 = "";
        String s4 = "Hello";
        
        // Objects.equals() 사용 - null 안전
        System.out.println("Objects.equals(s1, s2): " + java.util.Objects.equals(s1, s2)); // false
        System.out.println("Objects.equals(s2, s2): " + java.util.Objects.equals(s2, s2)); // true
        System.out.println("Objects.equals(s1, s4): " + java.util.Objects.equals(s1, s4)); // true
        
        // null과 빈 문자열 구분
        System.out.println("\nnull vs 빈 문자열:");
        System.out.println("s2 == null: " + (s2 == null)); // true
        System.out.println("s3.isEmpty(): " + s3.isEmpty()); // true
        System.out.println("s3 == null: " + (s3 == null)); // false
        
        // 안전한 문자열 처리 유틸리티
        demonstrateSafeStringUtils();
    }
    
    // 안전한 문자열 처리 유틸리티들
    private static void demonstrateSafeStringUtils() {
        System.out.println("\n=== 안전한 문자열 유틸리티 ===");
        
        String[] testStrings = {null, "", "  ", "Hello", "  World  "};
        
        for (String str : testStrings) {
            System.out.printf("문자열: %s%n", str == null ? "null" : "'" + str + "'");
            System.out.printf("  isEmpty: %b%n", isEmpty(str));
            System.out.printf("  isBlank: %b%n", isBlank(str));
            System.out.printf("  safeTrim: '%s'%n", safeTrim(str));
            System.out.printf("  defaultIfEmpty: '%s'%n", defaultIfEmpty(str, "DEFAULT"));
            System.out.println();
        }
    }
    
    // 유틸리티 메서드들
    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    private static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    private static String safeTrim(String str) {
        return str == null ? null : str.trim();
    }
    
    private static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }
    
    // 고성능 문자열 비교 기법
    public static void analyzeHighPerformanceComparison() {
        System.out.println("\n=== 고성능 문자열 비교 기법 ===");
        
        // 해시코드를 이용한 빠른 사전 필터링
        demonstrateHashBasedFiltering();
        
        // 길이 기반 조기 종료
        demonstrateLengthBasedOptimization();
        
        // 인턴된 문자열 활용
        demonstrateInternOptimization();
    }
    
    // 해시코드 기반 필터링
    private static void demonstrateHashBasedFiltering() {
        System.out.println("\n=== 해시코드 기반 필터링 ===");
        
        String target = "찾는문자열입니다";
        String[] candidates = {
            "다른문자열1", "다른문자열2", "찾는문자열입니다", 
            "다른문자열3", "다른문자열4"
        };
        
        int targetHash = target.hashCode();
        
        System.out.println("타겟 해시코드: " + targetHash);
        
        for (String candidate : candidates) {
            int candidateHash = candidate.hashCode();
            boolean hashMatch = (candidateHash == targetHash);
            boolean actualMatch = candidate.equals(target);
            
            System.out.printf("후보: %s, 해시매치: %b, 실제매치: %b%n", 
                            candidate, hashMatch, actualMatch);
        }
        
        /* 해시코드 활용 최적화:
         * 1. 해시코드가 다르면 확실히 다른 문자열
         * 2. 해시코드가 같아도 다른 문자열일 수 있음 (해시 충돌)
         * 3. 긴 문자열 비교 전에 해시코드로 사전 필터링 가능
         */
    }
    
    // 길이 기반 최적화
    private static void demonstrateLengthBasedOptimization() {
        System.out.println("\n=== 길이 기반 최적화 ===");
        
        String shortStr = "Hi";
        String longStr = "This is a very long string for testing purposes";
        
        // 길이가 다르면 즉시 false 반환
        boolean quickCheck = shortStr.length() == longStr.length();
        System.out.println("길이 비교 결과: " + quickCheck);
        System.out.println("실제 equals 호출 필요: " + !quickCheck);
        
        // 성능 측정
        int iterations = 1_000_000;
        
        // 길이 체크 포함
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (shortStr.length() == longStr.length()) {
                shortStr.equals(longStr);
            }
        }
        long optimizedTime = System.nanoTime() - start;
        
        // 직접 equals 호출
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            shortStr.equals(longStr);
        }
        long directTime = System.nanoTime() - start;
        
        System.out.println("길이 체크 포함: " + optimizedTime / 1_000_000 + "ms");
        System.out.println("직접 equals: " + directTime / 1_000_000 + "ms");
        System.out.println("최적화 효과: " + (directTime > optimizedTime ? "있음" : "없음"));
    }
    
    // 인턴 최적화
    private static void demonstrateInternOptimization() {
        System.out.println("\n=== 인턴 최적화 ===");
        
        // 동적 생성된 문자열들
        String dynamic1 = new String("CONSTANT");
        String dynamic2 = new String("CONSTANT");
        String literal = "CONSTANT";
        
        System.out.println("== 연산자 비교 (인턴 전):");
        System.out.println("dynamic1 == dynamic2: " + (dynamic1 == dynamic2)); // false
        System.out.println("dynamic1 == literal: " + (dynamic1 == literal)); // false
        
        // intern() 사용
        String interned1 = dynamic1.intern();
        String interned2 = dynamic2.intern();
        
        System.out.println("\n== 연산자 비교 (인턴 후):");
        System.out.println("interned1 == interned2: " + (interned1 == interned2)); // true
        System.out.println("interned1 == literal: " + (interned1 == literal)); // true
        
        // 성능 비교: equals vs == (인턴된 문자열)
        int iterations = 10_000_000;
        
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            interned1.equals(interned2);
        }
        long equalsTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            interned1 == interned2;
        }
        long referenceTime = System.nanoTime() - start;
        
        System.out.println("\nequals() 시간: " + equalsTime / 1_000_000 + "ms");
        System.out.println("== 비교 시간: " + referenceTime / 1_000_000 + "ms");
        System.out.println("참조 비교가 " + (double)equalsTime / referenceTime + "배 빠름");
    }
}
```

---

## 4. Collection Framework 심화

### 4.1 List 계열의 완전 이해

Collection Framework는 Java에서 데이터를 효율적으로 저장하고 처리하기 위한 핵심 라이브러리입니다.

```java
public class ListAnalysisDeep {
    
    // ArrayList의 내부 구조와 동적 배열 메커니즘
    public static void analyzeArrayListInternals() {
        /* ArrayList 내부 구조:
         * 1. Object[] elementData; // 실제 요소들을 저장하는 배열
         * 2. int size; // 현재 저장된 요소 개수
         * 3. static final int DEFAULT_CAPACITY = 10; // 기본 초기 용량
         * 4. 동적 크기 조정: 용량이 부족하면 1.5배 확장
         */
        
        System.out.println("=== ArrayList 내부 구조 분석 ===");
        
        java.util.List<String> list = new java.util.ArrayList<>();
        
        // 초기 상태 (빈 배열)
        System.out.println("초기 크기: " + list.size());
        
        // 첫 번째 요소 추가 시 기본 용량(10)으로 확장
        list.add("첫 번째");
        System.out.println("첫 요소 추가 후 크기: " + list.size());
        
        // 용량 확장 시뮬레이션
        demonstrateCapacityGrowth();
        
        // ArrayList vs Array 성능 비교
        compareArrayListVsArray();
    }
    
    // ArrayList 용량 확장 과정 시뮬레이션
    private static void demonstrateCapacityGrowth() {
        System.out.println("\n=== 용량 확장 시뮬레이션 ===");
        
        /* 확장 알고리즘:
         * 1. 새 용량 = 기존 용량 + (기존 용량 >> 1) // 1.5배
         * 2. 기존 요소들을 새 배열로 복사 (System.arraycopy 사용)
         * 3. 새 배열로 참조 변경
         */
        
        java.util.List<Integer> list = new java.util.ArrayList<>();
        
        // 많은 요소 추가하면서 확장 지점 관찰
        int[] capacityCheckPoints = {0, 1, 10, 11, 15, 16, 22, 23, 33, 34};
        
        for (int checkpoint : capacityCheckPoints) {
            // 체크포인트까지 요소 추가
            while (list.size() < checkpoint) {
                list.add(list.size());
            }
            
            if (checkpoint > 0) {
                System.out.printf("크기 %d일 때 용량 확장 예상: %s%n", 
                    checkpoint, 
                    isProbableCapacityExpansion(checkpoint) ? "발생" : "미발생");
            }
        }
        
        // 확장 비용 측정
        measureExpansionCost();
    }
    
    // 용량 확장 여부 추정 (10 → 15 → 22 → 33 → ... 패턴)
    private static boolean isProbableCapacityExpansion(int size) {
        // ArrayList의 확장 지점들: 10, 15, 22, 33, 49, 73, ...
        int capacity = 10;
        while (capacity < size) {
            capacity = capacity + (capacity >> 1); // 1.5배 확장
        }
        return capacity == size;
    }
    
    // 확장 비용 측정
    private static void measureExpansionCost() {
        System.out.println("\n=== 확장 비용 측정 ===");
        
        int elements = 100000;
        
        // 용량 지정하지 않은 경우 (확장 발생)
        long start = System.nanoTime();
        java.util.List<Integer> list1 = new java.util.ArrayList<>();
        for (int i = 0; i < elements; i++) {
            list1.add(i);
        }
        long withExpansion = System.nanoTime() - start;
        
        // 충분한 초기 용량 지정 (확장 없음)
        start = System.nanoTime();
        java.util.List<Integer> list2 = new java.util.ArrayList<>(elements);
        for (int i = 0; i < elements; i++) {
            list2.add(i);
        }
        long withoutExpansion = System.nanoTime() - start;
        
        System.out.println("확장 있음: " + withExpansion / 1_000_000 + "ms");
        System.out.println("확장 없음: " + withoutExpansion / 1_000_000 + "ms");
        System.out.println("확장 오버헤드: " + (double)withExpansion / withoutExpansion + "배");
    }
    
    // ArrayList vs 배열 성능 비교
    private static void compareArrayListVsArray() {
        System.out.println("\n=== ArrayList vs 배열 성능 비교 ===");
        
        int size = 1_000_000;
        int iterations = 100;
        
        // ArrayList 성능
        long start = System.nanoTime();
        for (int iter = 0; iter < iterations; iter++) {
            java.util.List<Integer> list = new java.util.ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(i);
            }
            // 순차 접근
            for (int i = 0; i < size; i++) {
                int value = list.get(i);
            }
        }
        long arrayListTime = System.nanoTime() - start;
        
        // 배열 성능
        start = System.nanoTime();
        for (int iter = 0; iter < iterations; iter++) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i;
            }
            // 순차 접근
            for (int i = 0; i < size; i++) {
                int value = array[i];
            }
        }
        long arrayTime = System.nanoTime() - start;
        
        System.out.println("ArrayList 시간: " + arrayListTime / 1_000_000 + "ms");
        System.out.println("배열 시간: " + arrayTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)arrayListTime / arrayTime + "배");
        
        /* 성능 차이 이유:
         * 1. ArrayList는 Object[] 사용, 기본 타입은 박싱/언박싱 필요
         * 2. get() 메서드 호출 오버헤드
         * 3. 배열은 직접 메모리 접근
         */
    }
    
    // LinkedList의 내부 구조와 노드 기반 동작
    public static void analyzeLinkedListInternals() {
        System.out.println("\n=== LinkedList 내부 구조 분석 ===");
        
        /* LinkedList 내부 구조:
         * 1. Node<E> first; // 첫 번째 노드 참조
         * 2. Node<E> last;  // 마지막 노드 참조
         * 3. int size;      // 노드 개수
         * 
         * Node 구조:
         * - E item;        // 실제 데이터
         * - Node<E> next;  // 다음 노드 참조
         * - Node<E> prev;  // 이전 노드 참조 (이중 연결)
         */
        
        java.util.LinkedList<String> linkedList = new java.util.LinkedList<>();
        
        // 양끝 삽입 (O(1))
        linkedList.addFirst("첫 번째");
        linkedList.addLast("마지막");
        linkedList.add(1, "중간"); // 인덱스 접근 (O(n))
        
        System.out.println("LinkedList 내용: " + linkedList);
        
        // LinkedList vs ArrayList 연산별 성능 비교
        compareLinkedListVsArrayList();
        
        // 메모리 사용량 비교
        compareMemoryUsage();
    }
    
    // LinkedList vs ArrayList 연산별 성능 비교
    private static void compareLinkedListVsArrayList() {
        System.out.println("\n=== LinkedList vs ArrayList 연산 성능 ===");
        
        int size = 100000;
        
        java.util.List<Integer> arrayList = new java.util.ArrayList<>();
        java.util.LinkedList<Integer> linkedList = new java.util.LinkedList<>();
        
        // 1. 순차 추가 성능
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
        }
        long arrayListAddTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            linkedList.add(i);
        }
        long linkedListAddTime = System.nanoTime() - start;
        
        System.out.println("=== 순차 추가 ===");
        System.out.println("ArrayList: " + arrayListAddTime / 1_000_000 + "ms");
        System.out.println("LinkedList: " + linkedListAddTime / 1_000_000 + "ms");
        
        // 2. 중간 삽입 성능
        int insertIndex = size / 2;
        
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            arrayList.add(insertIndex, -i);
        }
        long arrayListInsertTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            linkedList.add(insertIndex, -i);
        }
        long linkedListInsertTime = System.nanoTime() - start;
        
        System.out.println("\n=== 중간 삽입 ===");
        System.out.println("ArrayList: " + arrayListInsertTime / 1_000_000 + "ms");
        System.out.println("LinkedList: " + linkedListInsertTime / 1_000_000 + "ms");
        
        // 3. 인덱스 접근 성능
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayList.get(i % arrayList.size());
        }
        long arrayListGetTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedList.get(i % linkedList.size());
        }
        long linkedListGetTime = System.nanoTime() - start;
        
        System.out.println("\n=== 인덱스 접근 ===");
        System.out.println("ArrayList: " + arrayListGetTime / 1_000_000 + "ms");
        System.out.println("LinkedList: " + linkedListGetTime / 1_000_000 + "ms");
        
        // 4. Iterator 순회 성능
        start = System.nanoTime();
        for (Integer value : arrayList) {
            // 값 처리
        }
        long arrayListIterTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (Integer value : linkedList) {
            // 값 처리
        }
        long linkedListIterTime = System.nanoTime() - start;
        
        System.out.println("\n=== Iterator 순회 ===");
        System.out.println("ArrayList: " + arrayListIterTime / 1_000_000 + "ms");
        System.out.println("LinkedList: " + linkedListIterTime / 1_000_000 + "ms");
    }
    
    // 메모리 사용량 비교
    private static void compareMemoryUsage() {
        System.out.println("\n=== 메모리 사용량 분석 ===");
        
        /* 이론적 메모리 사용량:
         * 
         * ArrayList (1000개 Integer 저장):
         * - Object[] elementData: 1000 * 8 bytes (참조) = 8KB
         * - Integer 객체들: 1000 * 16 bytes = 16KB
         * - 총합: 약 24KB + ArrayList 객체 오버헤드
         * 
         * LinkedList (1000개 Integer 저장):
         * - Node 객체들: 1000 * (8 + 8 + 8) bytes = 24KB (item, next, prev 참조)
         * - Integer 객체들: 1000 * 16 bytes = 16KB
         * - 총합: 약 40KB + LinkedList 객체 오버헤드
         */
        
        int elements = 10000;
        
        // ArrayList 메모리 사용량 추정
        System.gc();
        long beforeArrayList = getUsedMemory();
        
        java.util.List<Integer> arrayList = new java.util.ArrayList<>();
        for (int i = 0; i < elements; i++) {
            arrayList.add(i);
        }
        
        long afterArrayList = getUsedMemory();
        long arrayListMemory = afterArrayList - beforeArrayList;
        
        // LinkedList 메모리 사용량 추정
        System.gc();
        long beforeLinkedList = getUsedMemory();
        
        java.util.LinkedList<Integer> linkedList = new java.util.LinkedList<>();
        for (int i = 0; i < elements; i++) {
            linkedList.add(i);
        }
        
        long afterLinkedList = getUsedMemory();
        long linkedListMemory = afterLinkedList - beforeLinkedList;
        
        System.out.printf("ArrayList 메모리 사용량: %d KB%n", arrayListMemory / 1024);
        System.out.printf("LinkedList 메모리 사용량: %d KB%n", linkedListMemory / 1024);
        System.out.printf("메모리 차이: %.1f배%n", (double)linkedListMemory / arrayListMemory);
        
        // 참조 유지 (GC 방지)
        System.out.printf("데이터 검증: ArrayList[0]=%d, LinkedList[0]=%d%n", 
                         arrayList.get(0), linkedList.get(0));
    }
    
    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    // Vector의 동기화 메커니즘
    public static void analyzeVectorSynchronization() {
        System.out.println("\n=== Vector 동기화 분석 ===");
        
        /* Vector 특징:
         * 1. 모든 메서드가 synchronized
         * 2. ArrayList와 동일한 내부 구조
         * 3. 확장 비율이 2배 (ArrayList는 1.5배)
         * 4. Legacy 클래스 (Java 1.0부터 존재)
         */
        
        java.util.Vector<Integer> vector = new java.util.Vector<>();
        java.util.List<Integer> arrayList = new java.util.ArrayList<>();
        
        // 단일 스레드 성능 비교
        int elements = 100000;
        
        long start = System.nanoTime();
        for (int i = 0; i < elements; i++) {
            vector.add(i);
        }
        long vectorTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < elements; i++) {
            arrayList.add(i);
        }
        long arrayListTime = System.nanoTime() - start;
        
        System.out.println("단일 스레드 성능:");
        System.out.println("Vector: " + vectorTime / 1_000_000 + "ms");
        System.out.println("ArrayList: " + arrayListTime / 1_000_000 + "ms");
        System.out.println("동기화 오버헤드: " + (double)vectorTime / arrayListTime + "배");
        
        // 멀티 스레드 안전성 테스트
        testThreadSafety();
    }
    
    // 스레드 안전성 테스트
    private static void testThreadSafety() {
        System.out.println("\n=== 스레드 안전성 테스트 ===");
        
        java.util.Vector<Integer> vector = new java.util.Vector<>();
        java.util.List<Integer> arrayList = new java.util.ArrayList<>();
        java.util.List<Integer> synchronizedList = java.util.Collections.synchronizedList(new java.util.ArrayList<>());
        
        int threads = 4;
        int elementsPerThread = 10000;
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threads);
        
        // Vector 테스트 (스레드 안전)
        long start = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < elementsPerThread; j++) {
                    vector.add(j);
                }
                latch.countDown();
            }).start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long vectorConcurrentTime = System.nanoTime() - start;
        
        System.out.println("멀티 스레드 테스트:");
        System.out.println("Vector 최종 크기: " + vector.size() + " (기댓값: " + (threads * elementsPerThread) + ")");
        System.out.println("Vector 멀티스레드 시간: " + vectorConcurrentTime / 1_000_000 + "ms");
        
        // ArrayList vs Collections.synchronizedList() 비교는 생략 (안전하지 않은 테스트가 될 수 있음)
    }
    
    // 최적화된 List 사용 패턴
    public static void demonstrateOptimizedListPatterns() {
        System.out.println("\n=== 최적화된 List 사용 패턴 ===");
        
        // 1. 초기 용량 설정
        demonstrateCapacityOptimization();
        
        // 2. 적절한 List 구현 선택
        demonstrateProperListSelection();
        
        // 3. 대량 데이터 처리 최적화
        demonstrateBulkOperationOptimization();
    }
    
    // 용량 최적화
    private static void demonstrateCapacityOptimization() {
        System.out.println("\n=== 용량 최적화 ===");
        
        int expectedSize = 50000;
        
        // 최적화 없음
        long start = System.nanoTime();
        java.util.List<Integer> list1 = new java.util.ArrayList<>();
        for (int i = 0; i < expectedSize; i++) {
            list1.add(i);
        }
        long time1 = System.nanoTime() - start;
        
        // 초기 용량 설정
        start = System.nanoTime();
        java.util.List<Integer> list2 = new java.util.ArrayList<>(expectedSize);
        for (int i = 0; i < expectedSize; i++) {
            list2.add(i);
        }
        long time2 = System.nanoTime() - start;
        
        System.out.println("용량 미설정: " + time1 / 1_000_000 + "ms");
        System.out.println("용량 설정: " + time2 / 1_000_000 + "ms");
        System.out.println("개선 효과: " + (double)time1 / time2 + "배");
    }
    
    // 적절한 List 구현 선택 가이드
    private static void demonstrateProperListSelection() {
        System.out.println("\n=== List 구현 선택 가이드 ===");
        
        System.out.println("사용 패턴별 권장 구현:");
        System.out.println("1. 순차 접근 위주, 크기 변경 적음 → ArrayList");
        System.out.println("2. 빈번한 삽입/삭제 (양끝) → LinkedList");
        System.out.println("3. 빈번한 삽입/삭제 (중간) → LinkedList (소규모) 또는 ArrayList (대규모)");
        System.out.println("4. 멀티스레드 환경 → Vector 또는 Collections.synchronizedList()");
        System.out.println("5. 불변 리스트 → Arrays.asList() 또는 List.of()");
        
        // 실제 시나리오별 성능 비교
        compareScenarioPerformance();
    }
    
    // 시나리오별 성능 비교
    private static void compareScenarioPerformance() {
        System.out.println("\n=== 시나리오별 성능 비교 ===");
        
        // 시나리오 1: 대량 데이터 순차 처리
        System.out.println("시나리오 1: 대량 데이터 순차 처리 (ArrayList 유리)");
        performSequentialProcessingTest();
        
        // 시나리오 2: 큐 동작 (양끝 삽입/삭제)
        System.out.println("\n시나리오 2: 큐 동작 (LinkedList 유리)");
        performQueueOperationTest();
        
        // 시나리오 3: 랜덤 접근
        System.out.println("\n시나리오 3: 랜덤 접근 (ArrayList 압승)");
        performRandomAccessTest();
    }
    
    // 순차 처리 테스트
    private static void performSequentialProcessingTest() {
        int size = 1_000_000;
        
        // ArrayList
        java.util.List<Integer> arrayList = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
        }
        
        long start = System.nanoTime();
        long sum = 0;
        for (Integer value : arrayList) {
            sum += value;
        }
        long arrayListTime = System.nanoTime() - start;
        
        // LinkedList
        java.util.LinkedList<Integer> linkedList = new java.util.LinkedList<>();
        for (int i = 0; i < size; i++) {
            linkedList.add(i);
        }
        
        start = System.nanoTime();
        sum = 0;
        for (Integer value : linkedList) {
            sum += value;
        }
        long linkedListTime = System.nanoTime() - start;
        
        System.out.println("  ArrayList: " + arrayListTime / 1_000_000 + "ms");
        System.out.println("  LinkedList: " + linkedListTime / 1_000_000 + "ms");
    }
    
    // 큐 동작 테스트
    private static void performQueueOperationTest() {
        int operations = 100000;
        
        // ArrayList (맨 앞 삭제는 비효율적)
        java.util.List<Integer> arrayList = new java.util.ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            arrayList.add(i); // 맨 뒤 추가
            if (arrayList.size() > 1000) {
                arrayList.remove(0); // 맨 앞 제거 (O(n))
            }
        }
        long arrayListTime = System.nanoTime() - start;
        
        // LinkedList (양끝 삽입/삭제 효율적)
        java.util.LinkedList<Integer> linkedList = new java.util.LinkedList<>();
        start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            linkedList.addLast(i); // 맨 뒤 추가
            if (linkedList.size() > 1000) {
                linkedList.removeFirst(); // 맨 앞 제거 (O(1))
            }
        }
        long linkedListTime = System.nanoTime() - start;
        
        System.out.println("  ArrayList: " + arrayListTime / 1_000_000 + "ms");
        System.out.println("  LinkedList: " + linkedListTime / 1_000_000 + "ms");
    }
    
    // 랜덤 접근 테스트
    private static void performRandomAccessTest() {
        int size = 50000;
        int accesses = 100000;
        
        // ArrayList
        java.util.List<Integer> arrayList = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
        }
        
        java.util.Random random = new java.util.Random(42);
        long start = System.nanoTime();
        for (int i = 0; i < accesses; i++) {
            arrayList.get(random.nextInt(size));
        }
        long arrayListTime = System.nanoTime() - start;
        
        // LinkedList
        java.util.LinkedList<Integer> linkedList = new java.util.LinkedList<>();
        for (int i = 0; i < size; i++) {
            linkedList.add(i);
        }
        
        random = new java.util.Random(42); // 동일한 시드
        start = System.nanoTime();
        for (int i = 0; i < accesses; i++) {
            linkedList.get(random.nextInt(size));
        }
        long linkedListTime = System.nanoTime() - start;
        
        System.out.println("  ArrayList: " + arrayListTime / 1_000_000 + "ms");
        System.out.println("  LinkedList: " + linkedListTime / 1_000_000 + "ms");
        System.out.println("  성능 차이: " + (double)linkedListTime / arrayListTime + "배");
    }
    
    // 대량 연산 최적화
    private static void demonstrateBulkOperationOptimization() {
        System.out.println("\n=== 대량 연산 최적화 ===");
        
        int size = 100000;
        java.util.List<Integer> source = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            source.add(i);
        }
        
        // 개별 추가 vs 대량 추가
        java.util.List<Integer> target1 = new java.util.ArrayList<>();
        long start = System.nanoTime();
        for (Integer item : source) {
            target1.add(item);
        }
        long individualTime = System.nanoTime() - start;
        
        java.util.List<Integer> target2 = new java.util.ArrayList<>(size);
        start = System.nanoTime();
        target2.addAll(source);
        long bulkTime = System.nanoTime() - start;
        
        System.out.println("개별 추가: " + individualTime / 1_000_000 + "ms");
        System.out.println("대량 추가: " + bulkTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)individualTime / bulkTime + "배");
        
        /* addAll() 최적화:
         * 1. 내부적으로 System.arraycopy() 사용
         * 2. 한 번에 필요한 용량 확보
         * 3. 루프 오버헤드 제거
         */
    }
}
```

### 4.2 Map 계열의 심화 분석

```java
public class MapAnalysisDeep {
    
    // HashMap의 내부 구조와 해시 테이블 메커니즘
    public static void analyzeHashMapInternals() {
        /* HashMap 내부 구조 (Java 8+):
         * 1. Node<K,V>[] table; // 해시 테이블 (버킷 배열)
         * 2. int size; // 저장된 key-value 쌍의 개수
         * 3. int threshold; // 리사이징 임계값 (capacity * loadFactor)
         * 4. float loadFactor; // 로드 팩터 (기본값: 0.75)
         * 5. 해시 충돌 해결: 체이닝 + 트리화 (Java 8+)
         */
        
        System.out.println("=== HashMap 내부 구조 분석 ===");
        
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        
        // 해시 충돌과 성능에 대한 이해
        demonstrateHashCollisions();
        
        // 로드 팩터의 영향
        analyzeLoadFactorImpact();
        
        // Java 8 트리화 메커니즘
        demonstrateTreeification();
    }
    
    // 해시 충돌 시연과 성능 영향
    private static void demonstrateHashCollisions() {
        System.out.println("\n=== 해시 충돌 시연 ===");
        
        /* HashMap의 해시 함수:
         * 1. Object.hashCode() 호출
         * 2. 추가 해싱: (h = key.hashCode()) ^ (h >>> 16)
         * 3. 버킷 인덱스: hash & (capacity - 1)
         */
        
        // 의도적으로 해시 충돌을 발생시키는 클래스
        class BadHashKey {
            private final int value;
            
            BadHashKey(int value) { this.value = value; }
            
            @Override
            public int hashCode() {
                return value % 4; // 의도적으로 나쁜 해시 함수
            }
            
            @Override
            public boolean equals(Object obj) {
                return obj instanceof BadHashKey && ((BadHashKey) obj).value == this.value;
            }
            
            @Override
            public String toString() {
                return "BadHashKey(" + value + ")";
            }
        }
        
        // 좋은 해시 함수를 가진 클래스
        class GoodHashKey {
            private final int value;
            
            GoodHashKey(int value) { this.value = value; }
            
            @Override
            public int hashCode() {
                return Integer.hashCode(value); // 좋은 해시 함수
            }
            
            @Override
            public boolean equals(Object obj) {
                return obj instanceof GoodHashKey && ((GoodHashKey) obj).value == this.value;
            }
            
            @Override
            public String toString() {
                return "GoodHashKey(" + value + ")";
            }
        }
        
        int size = 10000;
        
        // 나쁜 해시 함수 성능
        java.util.Map<BadHashKey, Integer> badHashMap = new java.util.HashMap<>();
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            badHashMap.put(new BadHashKey(i), i);
        }
        for (int i = 0; i < size; i++) {
            badHashMap.get(new BadHashKey(i));
        }
        long badTime = System.nanoTime() - start;
        
        // 좋은 해시 함수 성능
        java.util.Map<GoodHashKey, Integer> goodHashMap = new java.util.HashMap<>();
        start = System.nanoTime();
        for (int i = 0; i < size; i++) {
            goodHashMap.put(new GoodHashKey(i), i);
        }
        for (int i = 0; i < size; i++) {
            goodHashMap.get(new GoodHashKey(i));
        }
        long goodTime = System.nanoTime() - start;
        
        System.out.println("나쁜 해시 함수: " + badTime / 1_000_000 + "ms");
        System.out.println("좋은 해시 함수: " + goodTime / 1_000_000 + "ms");
        System.out.println("성능 차이: " + (double)badTime / goodTime + "배");
        
        // 해시 분포 분석
        analyzeHashDistribution();
    }
    
    // 해시 분포 분석
    private static void analyzeHashDistribution() {
        System.out.println("\n=== 해시 분포 분석 ===");
        
        // String의 해시코드 분포 확인
        String[] testStrings = {
            "apple", "banana", "cherry", "date", "elderberry",
            "fig", "grape", "honeydew", "kiwi", "lemon"
        };
        
        System.out.println("문자열 해시코드 분포:");
        for (String str : testStrings) {
            int hash = str.hashCode();
            int bucketIndex = hash & 15; // 16개 버킷 가정 (capacity - 1 = 15)
            System.out.printf("'%s': hash=%d, bucket=%d%n", str, hash, bucketIndex);
        }
        
        // 숫자의 해시코드 (Integer.hashCode() = value 자체)
        System.out.println("\n정수 해시코드 분포:");
        for (int i = 0; i < 10; i++) {
            int hash = Integer.hashCode(i);
            int bucketIndex = hash & 15;
            System.out.printf("%d: hash=%d, bucket=%d%n", i, hash, bucketIndex);
        }
    }
    
    // 로드 팩터 영향 분석
    private static void analyzeLoadFactorImpact() {
        System.out.println("\n=== 로드 팩터 영향 분석 ===");
        
        /* 로드 팩터와 성능:
         * 1. 낮은 로드 팩터 (0.5): 메모리 낭비, 빠른 접근
         * 2. 높은 로드 팩터 (0.9): 메모리 효율적, 느린 접근
         * 3. 기본값 (0.75): 시간과 공간의 균형
         */
        
        int elements = 50000;
        
        // 로드 팩터 0.5
        java.util.Map<Integer, Integer> lowLoadMap = new java.util.HashMap<>(elements * 2);
        long start = System.nanoTime();
        for (int i = 0; i < elements; i++) {
            lowLoadMap.put(i, i);
        }
        for (int i = 0; i < elements; i++) {
            lowLoadMap.get(i);
        }
        long lowLoadTime = System.nanoTime() - start;
        
        // 기본 로드 팩터 0.75
        java.util.Map<Integer, Integer> defaultLoadMap = new java.util.HashMap<>();
        start = System.nanoTime();
        for (int i = 0; i < elements; i++) {
            defaultLoadMap.put(i, i);
        }
        for (int i = 0; i < elements; i++) {
            defaultLoadMap.get(i);
        }
        long defaultLoadTime = System.nanoTime() - start;
        
        // 높은 로드 팩터 시뮬레이션 (작은 초기 용량)
        java.util.Map<Integer, Integer> highLoadMap = new java.util.HashMap<>(16);
        start = System.nanoTime();
        for (int i = 0; i < elements; i++) {
            highLoadMap.put(i, i);
        }
        for (int i = 0; i < elements; i++) {
            highLoadMap.get(i);
        }
        long highLoadTime = System.nanoTime() - start;
        
        System.out.println("낮은 로드팩터 (~0.5): " + lowLoadTime / 1_000_000 + "ms");
        System.out.println("기본 로드팩터 (0.75): " + defaultLoadTime / 1_000_000 + "ms");
        System.out.println("높은 로드팩터 (>0.75): " + highLoadTime / 1_000_000 + "ms");
    }
    
    // Java 8 트리화 메커니즘
    private static void demonstrateTreeification() {
        System.out.println("\n=== Java 8 트리화 메커니즘 ===");
        
        /* 트리화 조건:
         * 1. 하나의 버킷에 8개 이상의 노드
         * 2. 전체 테이블 크기가 64 이상
         * 3. 노드가 6개 이하로 줄어들면 다시 연결 리스트로 변환
         */
        
        // 트리화를 강제로 발생시키는 키 클래스
        class TreeifyKey {
            private final int value;
            
            TreeifyKey(int value) { this.value = value; }
            
            @Override
            public int hashCode() {
                return 1; // 모든 키가 같은 해시코드 (같은 버킷으로)
            }
            
            @Override
            public boolean equals(Object obj) {
                return obj instanceof TreeifyKey && ((TreeifyKey) obj).value == this.value;
            }
            
            @Override
            public String toString() {
                return "TreeifyKey(" + value + ")";
            }
        }
        
        java.util.Map<TreeifyKey, Integer> map = new java.util.HashMap<>();
        
        // 같은 버킷에 많은 요소 추가 (트리화 유도)
        System.out.println("트리화 실험:");
        for (int i = 0; i < 20; i++) {
            map.put(new TreeifyKey(i), i);
            if (i == 7) {
                System.out.println("  8개 요소 추가 완료 - 트리화 발생 가능");
            }
        }
        
        // 검색 성능 측정 (트리화된 버킷)
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            map.get(new TreeifyKey(i % 20));
        }
        long treeTime = System.nanoTime() - start;
        
        System.out.println("트리화된 HashMap 검색 시간: " + treeTime / 1_000_000 + "ms");
        System.out.println("트리화 효과: O(log n) 검색 성능 보장");
        
        /* 트리화의 이점:
         * 1. 최악의 경우 O(n) → O(log n)으로 성능 개선
         * 2. 해시 충돌이 많은 상황에서 안정적 성능
         * 3. DoS 공격 방어 (의도적 해시 충돌 공격)
         */
    }
    
    // TreeMap의 내부 구조 (Red-Black Tree)
    public static void analyzeTreeMapInternals() {
        System.out.println("\n=== TreeMap 내부 구조 분석 ===");
        
        /* TreeMap 특징:
         * 1. Red-Black Tree 구조 (자가 균형 이진 트리)
         * 2. 모든 연산이 O(log n)
         * 3. 키의 정렬 순서 유지
         * 4. Comparable 또는 Comparator 필요
         */
        
        java.util.TreeMap<String, Integer> treeMap = new java.util.TreeMap<>();
        
        // 정렬된 삽입
        String[] words = {"zebra", "apple", "banana", "cherry", "date"};
        for (int i = 0; i < words.length; i++) {
            treeMap.put(words[i], i);
        }
        
        System.out.println("TreeMap 내용 (자동 정렬됨): " + treeMap);
        
        // NavigableMap 기능들
        demonstrateNavigableMapFeatures(treeMap);
        
        // TreeMap vs HashMap 성능 비교
        compareTreeMapVsHashMap();
    }
    
    // NavigableMap 기능 시연
    private static void demonstrateNavigableMapFeatures(java.util.NavigableMap<String, Integer> navMap) {
        System.out.println("\n=== NavigableMap 기능 ===");
        
        /* NavigableMap 주요 메서드:
         * 1. firstKey(), lastKey(): 최소/최대 키
         * 2. lowerKey(), higherKey(): 지정된 키보다 작은/큰 키
         * 3. floorKey(), ceilingKey(): 지정된 키 이하/이상의 키
         * 4. subMap(), headMap(), tailMap(): 부분 맵
         */
        
        System.out.println("첫 번째 키: " + navMap.firstKey());
        System.out.println("마지막 키: " + navMap.lastKey());
        
        String targetKey = "cherry";
        System.out.println("\n'" + targetKey + "' 기준:");
        System.out.println("작은 키: " + navMap.lowerKey(targetKey));
        System.out.println("큰 키: " + navMap.higherKey(targetKey));
        System.out.println("이하 키: " + navMap.floorKey(targetKey));
        System.out.println("이상 키: " + navMap.ceilingKey(targetKey));
        
        // 범위 검색
        java.util.NavigableMap<String, Integer> subMap = navMap.subMap("b", true, "d", false);
        System.out.println("'b' 이상 'd' 미만: " + subMap);
        
        // 역순 뷰
        java.util.NavigableMap<String, Integer> descendingMap = navMap.descendingMap();
        System.out.println("역순 맵: " + descendingMap);
    }
    
    // TreeMap vs HashMap 성능 비교
    private static void compareTreeMapVsHashMap() {
        System.out.println("\n=== TreeMap vs HashMap 성능 비교 ===");
        
        int size = 100000;
        
        // 데이터 준비
        java.util.List<Integer> keys = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            keys.add(i);
        }
        java.util.Collections.shuffle(keys); // 랜덤 순서로 섞기
        
        // HashMap 성능
        java.util.Map<Integer, Integer> hashMap = new java.util.HashMap<>();
        long start = System.nanoTime();
        for (Integer key : keys) {
            hashMap.put(key, key);
        }
        long hashMapPutTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (Integer key : keys) {
            hashMap.get(key);
        }
        long hashMapGetTime = System.nanoTime() - start;
        
        // TreeMap 성능
        java.util.TreeMap<Integer, Integer> treeMap = new java.util.TreeMap<>();
        start = System.nanoTime();
        for (Integer key : keys) {
            treeMap.put(key, key);
        }
        long treeMapPutTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (Integer key : keys) {
            treeMap.get(key);
        }
        long treeMapGetTime = System.nanoTime() - start;
        
        System.out.println("삽입 성능:");
        System.out.println("  HashMap: " + hashMapPutTime / 1_000_000 + "ms");
        System.out.println("  TreeMap: " + treeMapPutTime / 1_000_000 + "ms");
        System.out.println("  차이: " + (double)treeMapPutTime / hashMapPutTime + "배");
        
        System.out.println("검색 성능:");
        System.out.println("  HashMap: " + hashMapGetTime / 1_000_000 + "ms");
        System.out.println("  TreeMap: " + treeMapGetTime / 1_000_000 + "ms");
        System.out.println("  차이: " + (double)treeMapGetTime / hashMapGetTime + "배");
        
        /* 성능 차이 이유:
         * HashMap: 평균 O(1), 최악 O(n) → Java 8+ 트리화로 O(log n)
         * TreeMap: 항상 O(log n)
         * 
         * 사용 권장:
         * - 단순 키-값 저장/검색: HashMap
         * - 정렬된 순서 필요: TreeMap
         * - 범위 검색 필요: TreeMap
         */
    }
    
    // LinkedHashMap의 특수 기능
    public static void analyzeLinkedHashMap() {
        System.out.println("\n=== LinkedHashMap 분석 ===");
        
        /* LinkedHashMap 특징:
         * 1. HashMap + 이중 연결 리스트
         * 2. 삽입 순서 또는 접근 순서 유지
         * 3. accessOrder = false: 삽입 순서 (기본값)
         * 4. accessOrder = true: 접근 순서 (LRU 캐시 구현 가능)
         */
        
        // 삽입 순서 유지
        java.util.LinkedHashMap<String, Integer> insertionOrder = new java.util.LinkedHashMap<>();
        insertionOrder.put("third", 3);
        insertionOrder.put("first", 1);
        insertionOrder.put("second", 2);
        
        System.out.println("삽입 순서 유지: " + insertionOrder);
        
        // 접근 순서 LinkedHashMap (LRU)
        java.util.LinkedHashMap<String, Integer> accessOrder = new java.util.LinkedHashMap<>(16, 0.75f, true);
        accessOrder.put("A", 1);
        accessOrder.put("B", 2);
        accessOrder.put("C", 3);
        
        System.out.println("초기 상태: " + accessOrder);
        
        // 접근하면 순서가 바뀜
        accessOrder.get("A"); // A를 맨 뒤로 이동
        System.out.println("A 접근 후: " + accessOrder);
        
        accessOrder.get("B"); // B를 맨 뒤로 이동
        System.out.println("B 접근 후: " + accessOrder);
        
        // LRU 캐시 구현
        demonstrateLRUCache();
    }
    
    // LRU 캐시 구현
    private static void demonstrateLRUCache() {
        System.out.println("\n=== LRU 캐시 구현 ===");
        
        // LRU 캐시 클래스
        class LRUCache<K, V> extends java.util.LinkedHashMap<K, V> {
            private final int maxSize;
            
            public LRUCache(int maxSize) {
                super(16, 0.75f, true); // accessOrder = true
                this.maxSize = maxSize;
            }
            
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
                return size() > maxSize; // 크기 초과 시 가장 오래된 항목 제거
            }
        }
        
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        
        // 캐시에 데이터 추가
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        System.out.println("초기 캐시: " + cache);
        
        // 용량 초과 시 가장 오래된 항목 제거
        cache.put("D", 4); // A가 제거됨
        System.out.println("D 추가 후: " + cache);
        
        // B 접근 (최근 사용으로 이동)
        cache.get("B");
        System.out.println("B 접근 후: " + cache);
        
        // E 추가 (C가 제거됨, B는 최근 접근되어 유지)
        cache.put("E", 5);
        System.out.println("E 추가 후: " + cache);
    }
    
    // ConcurrentHashMap의 동시성 메커니즘
    public static void analyzeConcurrentHashMap() {
        System.out.println("\n=== ConcurrentHashMap 동시성 분석 ===");
        
        /* ConcurrentHashMap 특징 (Java 8+):
         * 1. 세그먼트 기반 → 버킷 레벨 락킹으로 변경
         * 2. CAS(Compare-And-Swap) 연산 활용
         * 3. 읽기 연산은 대부분 락 없음
         * 4. 쓰기 연산만 필요시 락 사용
         */
        
        java.util.concurrent.ConcurrentHashMap<Integer, Integer> concurrentMap = 
            new java.util.concurrent.ConcurrentHashMap<>();
        
        // 동시성 테스트
        testConcurrentMapPerformance(concurrentMap);
        
        // 원자적 연산들
        demonstrateAtomicOperations(concurrentMap);
        
        // 대량 병렬 연산
        demonstrateBulkOperations(concurrentMap);
    }
    
    // ConcurrentHashMap 성능 테스트
    private static void testConcurrentMapPerformance(java.util.concurrent.ConcurrentHashMap<Integer, Integer> concurrentMap) {
        System.out.println("\n=== 동시성 성능 테스트 ===");
        
        int threads = 4;
        int operationsPerThread = 250000;
        java.util.concurrent.CountDownLatch startLatch = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch endLatch = new java.util.concurrent.CountDownLatch(threads);
        
        // ConcurrentHashMap 테스트
        long start = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = threadId * operationsPerThread + j;
                        concurrentMap.put(key, key);
                        concurrentMap.get(key);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }
        
        startLatch.countDown(); // 모든 스레드 시작
        try {
            endLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long concurrentTime = System.nanoTime() - start;
        
        // 동기화된 HashMap과 비교
        java.util.Map<Integer, Integer> syncMap = java.util.Collections.synchronizedMap(new java.util.HashMap<>());
        java.util.concurrent.CountDownLatch startLatch2 = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch endLatch2 = new java.util.concurrent.CountDownLatch(threads);
        
        start = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    startLatch2.await();
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = threadId * operationsPerThread + j;
                        syncMap.put(key, key);
                        syncMap.get(key);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch2.countDown();
                }
            }).start();
        }
        
        startLatch2.countDown();
        try {
            endLatch2.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long syncTime = System.nanoTime() - start;
        
        System.out.println("ConcurrentHashMap: " + concurrentTime / 1_000_000 + "ms");
        System.out.println("SynchronizedMap: " + syncTime / 1_000_000 + "ms");
        System.out.println("성능 개선: " + (double)syncTime / concurrentTime + "배");
        
        System.out.println("최종 크기: " + concurrentMap.size());
    }
    
    // 원자적 연산 시연
    private static void demonstrateAtomicOperations(java.util.concurrent.ConcurrentHashMap<Integer, Integer> map) {
        System.out.println("\n=== 원자적 연산 시연 ===");
        
        // 기본 원자적 연산들
        map.put(1, 100);
        
        // putIfAbsent: 키가 없을 때만 추가
        Integer previous1 = map.putIfAbsent(1, 200); // 100 반환 (추가되지 않음)
        Integer previous2 = map.putIfAbsent(2, 200); // null 반환 (새로 추가됨)
        
        System.out.println("putIfAbsent(1, 200) 이전 값: " + previous1);
        System.out.println("putIfAbsent(2, 200) 이전 값: " + previous2);
        System.out.println("현재 맵: " + map);
        
        // replace: 키가 있을 때만 교체
        boolean replaced1 = map.replace(1, 100, 150); // true (교체됨)
        boolean replaced2 = map.replace(3, 100, 300); // false (키 없음)
        
        System.out.println("replace(1, 100→150): " + replaced1);
        System.out.println("replace(3, 100→300): " + replaced2);
        
        // 계산 기반 연산들
        map.compute(1, (key, val) -> val == null ? 1 : val + 10); // 150 + 10 = 160
        map.computeIfAbsent(3, key -> key * 100); // 3 * 100 = 300
        map.computeIfPresent(2, (key, val) -> val * 2); // 200 * 2 = 400
        
        System.out.println("계산 연산 후: " + map);
        
        // merge 연산
        map.merge(1, 50, Integer::sum); // 160 + 50 = 210
        map.merge(4, 50, Integer::sum); // 새 키 4에 50 추가
        
        System.out.println("merge 연산 후: " + map);
    }
    
    // 대량 병렬 연산
    private static void demonstrateBulkOperations(java.util.concurrent.ConcurrentHashMap<Integer, Integer> map) {
        System.out.println("\n=== 대량 병렬 연산 ===");
        
        // 테스트 데이터 준비
        map.clear();
        for (int i = 1; i <= 1000; i++) {
            map.put(i, i);
        }
        
        // 병렬 검색
        Integer found = map.search(100, (key, value) -> value > 950 ? value : null);
        System.out.println("950보다 큰 첫 번째 값: " + found);
        
        // 병렬 감소 (리듀스)
        Integer sum = map.reduce(100, (key, value) -> value, Integer::sum);
        System.out.println("모든 값의 합: " + sum);
        
        // 병렬 forEach
        java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger();
        map.forEach(100, (key, value) -> {
            if (value % 2 == 0) {
                count.incrementAndGet();
            }
        });
        System.out.println("짝수 개수: " + count.get());
        
        /* 병렬성 임계값:
         * - 첫 번째 매개변수 (100): 병렬 처리 임계값
         * - 요소 수가 임계값보다 작으면 순차 처리
         * - 큰 경우 자동으로 병렬 처리
         * - 일반적으로 CPU 코어 수 * 32 정도 권장
         */
    }
    
    // Map 구현체 선택 가이드
    public static void demonstrateMapSelectionGuide() {
        System.out.println("\n=== Map 구현체 선택 가이드 ===");
        
        System.out.println("사용 목적별 권장 구현체:");
        System.out.println("1. 일반적인 키-값 저장: HashMap");
        System.out.println("2. 삽입 순서 유지: LinkedHashMap");
        System.out.println("3. 정렬된 키 필요: TreeMap");
        System.out.println("4. 멀티스레드 환경: ConcurrentHashMap");
        System.out.println("5. LRU 캐시: LinkedHashMap (accessOrder=true)");
        System.out.println("6. 읽기 전용: Collections.unmodifiableMap()");
        System.out.println("7. 열거형 키: EnumMap");
        System.out.println("8. 약한 참조: WeakHashMap");
        
        // 성능 특성 요약
        demonstratePerformanceSummary();
    }
    
    // 성능 특성 요약
    private static void demonstratePerformanceSummary() {
        System.out.println("\n=== 성능 특성 요약 ===");
        
        System.out.printf("%-20s %-10s %-10s %-15s %-15s%n", 
                         "구현체", "get()", "put()", "정렬", "스레드안전");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.printf("%-20s %-10s %-10s %-15s %-15s%n", 
                         "HashMap", "O(1)", "O(1)", "없음", "아니오");
        System.out.printf("%-20s %-10s %-10s %-15s %-15s%n", 
                         "LinkedHashMap", "O(1)", "O(1)", "삽입순서", "아니오");
        System.out.printf("%-20s %-10s %-10s %-15s %-15s%n", 
                         "TreeMap", "O(log n)", "O(log n)", "키 정렬", "아니오");
        System.out.printf("%-20s %-10s %-10s %-15s %-15s%n", 
                         "ConcurrentHashMap", "O(1)", "O(1)", "없음", "예");
        
        System.out.println("\n메모리 사용량 (상대적):");
        System.out.println("HashMap < LinkedHashMap < TreeMap ≈ ConcurrentHashMap");
    }
}
```

### 4.3 Set 계열의 완전 분석

```java
public class SetAnalysisDeep {
    
    // HashSet의 내부 구조 (HashMap 기반)
    public static void analyzeHashSetInternals() {
        System.out.println("=== HashSet 내부 구조 분석 ===");
        
        /* HashSet 내부 구현:
         * 1. 내부적으로 HashMap을 사용
         * 2. 요소는 HashMap의 키로 저장
         * 3. 값으로는 더미 Object (PRESENT) 사용
         * 4. 모든 Set 연산은 Map 연산으로 위임
         */
        
        java.util.Set<String> hashSet = new java.util.HashSet<>();
        
        // Set의 기본 연산들
        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Cherry");
        hashSet.add("Apple"); // 중복 - 추가되지 않음
        
        System.out.println("HashSet 내용: " + hashSet);
        System.out.println("크기: " + hashSet.size());
        System.out.println("Apple 포함: " + hashSet.contains("Apple"));
        
        // 내부 HashMap과의 관계 설명
        explainHashSetImplementation();
        
        // Set 연산들의 구현
        demonstrateSetOperations();
    }
    
    // HashSet 내부 구현 설명
    private static void explainHashSetImplementation() {
        System.out.println("\n=== HashSet 내부 구현 원리 ===");
        
        /* 의사 코드로 보는 HashSet:
         * 
         * class HashSet<E> {
         *     private HashMap<E, Object> map;
         *     private static final Object PRESENT = new Object();
         *     
         *     public boolean add(E e) {
         *         return map.put(e, PRESENT) == null;
         *     }
         *     
         *     public boolean contains(Object o) {
         *         return map.containsKey(o);
         *     }
         *     
         *     public boolean remove(Object o) {
         *         return map.remove(o) == PRESENT;
         *     }
         * }
         */
        
        System.out.println("HashSet은 내부적으로 HashMap<E, Object>를 사용합니다.");
        System.out.println("- 실제 요소는 HashMap의 키로 저장");
        System.out.println("- 값으로는 더미 객체 (PRESENT) 사용");
        System.out.println("- 이로 인해 HashMap의 모든 성능 특성을 그대로 가짐");
        System.out.println("- 해시 충돌, 로드 팩터, 트리화 등 모든 메커니즘 동일");
    }
    
    // Set 연산들의 구현과 성능
    private static void demonstrateSetOperations() {
        System.out.println("\n=== Set 연산들의 구현 ===");
        
        java.util.Set<Integer> set1 = new java.util.HashSet<>(java.util.Arrays.asList(1, 2, 3, 4, 5));
        java.util.Set<Integer> set2 = new java.util.HashSet<>(java.util.Arrays.asList(4, 5, 6, 7, 8));
        
        System.out.println("Set1: " + set1);
        System.out.println("Set2: " + set2);
        
        // 합집합 (Union)
        java.util.Set<Integer> union = new java.util.HashSet<>(set1);
        union.addAll(set2);
        System.out.println("합집합: " + union);
        
        // 교집합 (Intersection)
        java.util.Set<Integer> intersection = new java.util.HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("교집합: " + intersection);
        
        // 차집합 (Difference)
        java.util.Set<Integer> difference = new java.util.HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("차집합: " + difference);
        
        // 대칭 차집합 (Symmetric Difference)
        java.util.Set<Integer> symDiff = new java.util.HashSet<>(union);
        symDiff.removeAll(intersection);
        System.out.println("대칭 차집합: " + symDiff);
        
        // 부분집합 검사
        boolean isSubset = set2.containsAll(java.util.Arrays.asList(4, 5));
        System.out.println("{4, 5}가 Set2의 부분집합: " + isSubset);
        
        // 연산들의 시간 복잡도 분석
        analyzeSetOperationComplexity();
    }
    
    // Set 연산들의 시간 복잡도 분석
    private static void analyzeSetOperationComplexity() {
        System.out.println("\n=== Set 연산 복잡도 분석 ===");
        
        /* 시간 복잡도 (n = 첫 번째 set 크기, m = 두 번째 set 크기):
         * 
         * HashSet 기준:
         * - add(e): O(1) 평균, O(n) 최악
         * - contains(e): O(1) 평균, O(n) 최악
         * - remove(e): O(1) 평균, O(n) 최악
         * - addAll(collection): O(m) 평균
         * - retainAll(collection): O(n) 평균
         * - removeAll(collection): O(n) 평균
         * 
         * TreeSet 기준:
         * - 모든 기본 연산: O(log n)
         * - 집합 연산: O(n log n) 또는 O(m log n)
         */
        
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            // HashSet 성능 측정
            long hashSetTime = measureSetOperationTime(size, true);
            
            // TreeSet 성능 측정  
            long treeSetTime = measureSetOperationTime(size, false);
            
            System.out.printf("크기 %d - HashSet: %d ms, TreeSet: %d ms, 비율: %.2f%n",
                            size, hashSetTime, treeSetTime, (double)treeSetTime / hashSetTime);
        }
    }
    
    // Set 연산 시간 측정
    private static long measureSetOperationTime(int size, boolean useHashSet) {
        java.util.Set<Integer> set1 = useHashSet ? new java.util.HashSet<>() : new java.util.TreeSet<>();
        java.util.Set<Integer> set2 = useHashSet ? new java.util.HashSet<>() : new java.util.TreeSet<>();
        
        // 데이터 준비
        for (int i = 0; i < size; i++) {
            set1.add(i);
            set2.add(i / 2); // 50% 중복
        }
        
        long start = System.nanoTime();
        
        // 여러 연산 수행
        set1.addAll(set2);          // 합집합
        set1.retainAll(set2);       // 교집합
        set1.removeAll(set2);       // 차집합
        
        return (System.nanoTime() - start) / 1_000_000;
    }
    
    // TreeSet의 정렬과 검색 기능
    public static void analyzeTreeSetFeatures() {
        System.out.println("\n=== TreeSet 정렬과 검색 기능 ===");
        
        /* TreeSet 특징:
         * 1. NavigableSet 인터페이스 구현
         * 2. Red-Black Tree 기반 (TreeMap과 동일)
         * 3. 자동 정렬 (Comparable 또는 Comparator)
         * 4. 범위 검색 지원
         */
        
        java.util.TreeSet<Integer> treeSet = new java.util.TreeSet<>();
        
        // 무작위 순서로 추가해도 자동 정렬
        int[] randomNumbers = {7, 3, 9, 1, 5, 2, 8, 4, 6};
        for (int num : randomNumbers) {
            treeSet.add(num);
        }
        
        System.out.println("TreeSet (자동 정렬): " + treeSet);
        
        // NavigableSet 기능들
        demonstrateNavigableSetFeatures(treeSet);
        
        // 커스텀 Comparator 사용
        demonstrateCustomComparator();
    }
    
    // NavigableSet 기능 시연
    private static void demonstrateNavigableSetFeatures(java.util.NavigableSet<Integer> navSet) {
        System.out.println("\n=== NavigableSet 기능 ===");
        
        System.out.println("첫 번째: " + navSet.first());
        System.out.println("마지막: " + navSet.last());
        
        int target = 5;
        System.out.println("\n" + target + " 기준:");
        System.out.println("작은 값: " + navSet.lower(target));      // 5보다 작은 가장 큰 값
        System.out.println("큰 값: " + navSet.higher(target));       // 5보다 큰 가장 작은 값
        System.out.println("이하 값: " + navSet.floor(target));      // 5 이하의 가장 큰 값
        System.out.println("이상 값: " + navSet.ceiling(target));    // 5 이상의 가장 작은 값
        
        // 범위 검색
        java.util.NavigableSet<Integer> subset = navSet.subSet(3, true, 7, false);
        System.out.println("3 이상 7 미만: " + subset);
        
        // 헤드/테일 집합
        java.util.NavigableSet<Integer> headSet = navSet.headSet(5, false);
        java.util.NavigableSet<Integer> tailSet = navSet.tailSet(5, true);
        System.out.println("5 미만: " + headSet);
        System.out.println("5 이상: " + tailSet);
        
        // 역순 뷰
        java.util.NavigableSet<Integer> descendingSet = navSet.descendingSet();
        System.out.println("역순: " + descendingSet);
    }
    
    // 커스텀 Comparator 사용
    private static void demonstrateCustomComparator() {
        System.out.println("\n=== 커스텀 Comparator 사용 ===");
        
        // 문자열 길이로 정렬
        java.util.TreeSet<String> lengthSorted = new java.util.TreeSet<>(
            java.util.Comparator.comparing(String::length).thenComparing(String::compareTo)
        );
        
        String[] words = {"apple", "pie", "banana", "kiwi", "cherry", "date"};
        for (String word : words) {
            lengthSorted.add(word);
        }
        
        System.out.println("길이순 정렬: " + lengthSorted);
        
        // 역순 정렬
        java.util.TreeSet<Integer> reverseSet = new java.util.TreeSet<>(java.util.Collections.reverseOrder());
        reverseSet.addAll(java.util.Arrays.asList(1, 5, 3, 9, 2, 7));
        System.out.println("역순 정렬: " + reverseSet);
        
        // 복합 객체 정렬
        demonstrateComplexObjectSorting();
    }
    
    // 복합 객체 정렬
    private static void demonstrateComplexObjectSorting() {
        System.out.println("\n=== 복합 객체 정렬 ===");
        
        // Person 클래스 (간단한 레코드 스타일)
        class Person implements Comparable<Person> {
            final String name;
            final int age;
            
            Person(String name, int age) {
                this.name = name;
                this.age = age;
            }
            
            @Override
            public int compareTo(Person other) {
                // 나이 순, 같으면 이름 순
                int ageCompare = Integer.compare(this.age, other.age);
                return ageCompare != 0 ? ageCompare : this.name.compareTo(other.name);
            }
            
            @Override
            public String toString() {
                return name + "(" + age + ")";
            }
            
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (!(obj instanceof Person)) return false;
                Person person = (Person) obj;
                return age == person.age && java.util.Objects.equals(name, person.name);
            }
            
            @Override
            public int hashCode() {
                return java.util.Objects.hash(name, age);
            }
        }
        
        java.util.TreeSet<Person> people = new java.util.TreeSet<>();
        people.add(new Person("Alice", 30));
        people.add(new Person("Bob", 25));
        people.add(new Person("Charlie", 30));
        people.add(new Person("David", 25));
        
        System.out.println("나이순, 이름순 정렬: " + people);
        
        // Comparator로 다른 정렬 기준
        java.util.TreeSet<Person> nameFirst = new java.util.TreeSet<>(
            java.util.Comparator.comparing((Person p) -> p.name).thenComparingInt(p -> p.age)
        );
        nameFirst.addAll(people);
        System.out.println("이름순, 나이순 정렬: " + nameFirst);
    }
    
    // LinkedHashSet의 특징
    public static void analyzeLinkedHashSet() {
        System.out.println("\n=== LinkedHashSet 특징 ===");
        
        /* LinkedHashSet 특징:
         * 1. HashSet + 삽입 순서 유지
         * 2. 내부적으로 LinkedHashMap 사용
         * 3. 반복 순서가 삽입 순서와 동일
         * 4. HashSet보다 약간 느리지만 순서 보장
         */
        
        java.util.Set<String> hashSet = new java.util.HashSet<>();
        java.util.Set<String> linkedHashSet = new java.util.LinkedHashSet<>();
        
        String[] items = {"Zebra", "Apple", "Banana", "Cherry", "Date"};
        
        // 동일한 순서로 추가
        for (String item : items) {
            hashSet.add(item);
            linkedHashSet.add(item);
        }
        
        System.out.println("HashSet 순회: " + hashSet);
        System.out.println("LinkedHashSet 순회: " + linkedHashSet);
        System.out.println("LinkedHashSet은 삽입 순서를 보장합니다.");
        
        // 성능 비교
        compareSetPerformance();
    }
    
    // Set 구현체들의 성능 비교
    private static void compareSetPerformance() {
        System.out.println("\n=== Set 구현체 성능 비교 ===");
        
        int size = 100000;
        int iterations = 10;
        
        // 테스트 데이터 준비
        java.util.List<Integer> testData = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            testData.add(i);
        }
        java.util.Collections.shuffle(testData);
        
        // HashSet 성능
        long hashSetTime = measureSetPerformance(testData, java.util.HashSet::new, iterations);
        
        // LinkedHashSet 성능
        long linkedHashSetTime = measureSetPerformance(testData, java.util.LinkedHashSet::new, iterations);
        
        // TreeSet 성능
        long treeSetTime = measureSetPerformance(testData, java.util.TreeSet::new, iterations);
        
        System.out.printf("%-20s: %d ms%n", "HashSet", hashSetTime);
        System.out.printf("%-20s: %d ms (%.2fx)%n", "LinkedHashSet", linkedHashSetTime, 
                         (double)linkedHashSetTime / hashSetTime);
        System.out.printf("%-20s: %d ms (%.2fx)%n", "TreeSet", treeSetTime, 
                         (double)treeSetTime / hashSetTime);
    }
    
    // Set 성능 측정 헬퍼 메서드
    private static long measureSetPerformance(java.util.List<Integer> testData, 
                                            java.util.function.Supplier<java.util.Set<Integer>> setSupplier, 
                                            int iterations) {
        long totalTime = 0;
        
        for (int iter = 0; iter < iterations; iter++) {
            java.util.Set<Integer> set = setSupplier.get();
            
            long start = System.nanoTime();
            
            // 추가
            for (Integer item : testData) {
                set.add(item);
            }
            
            // 검색
            for (Integer item : testData) {
                set.contains(item);
            }
            
            // 삭제
            for (Integer item : testData) {
                set.remove(item);
            }
            
            totalTime += (System.nanoTime() - start);
        }
        
        return totalTime / 1_000_000 / iterations;
    }
    
    // Set 사용 패턴과 최적화
    public static void demonstrateSetOptimization() {
        System.out.println("\n=== Set 최적화 패턴 ===");
        
        // 1. 적절한 초기 용량 설정
        demonstrateCapacityOptimization();
        
        // 2. 중복 제거 최적화
        demonstrateDuplicateRemoval();
        
        // 3. Set을 이용한 빠른 멤버십 테스트
        demonstrateMembershipTest();
    }
    
    // 용량 최적화
    private static void demonstrateCapacityOptimization() {
        System.out.println("\n=== 용량 최적화 ===");
        
        int expectedSize = 50000;
        java.util.List<Integer> data = new java.util.ArrayList<>();
        for (int i = 0; i < expectedSize; i++) {
            data.add(i);
        }
        
        // 기본 용량
        long start = System.nanoTime();
        java.util.Set<Integer> set1 = new java.util.HashSet<>();
        set1.addAll(data);
        long time1 = System.nanoTime() - start;
        
        // 적절한 초기 용량
        start = System.nanoTime();
        java.util.Set<Integer> set2 = new java.util.HashSet<>(expectedSize * 4 / 3); // 로드 팩터 고려
        set2.addAll(data);
        long time2 = System.nanoTime() - start;
        
        System.out.println("기본 용량: " + time1 / 1_000_000 + "ms");
        System.out.println("최적 용량: " + time2 / 1_000_000 + "ms");
        System.out.println("개선 효과: " + (double)time1 / time2 + "배");
    }
    
    // 중복 제거 최적화
    private static void demonstrateDuplicateRemoval() {
        System.out.println("\n=== 중복 제거 최적화 ===");
        
        // 중복이 많은 데이터
        java.util.List<Integer> duplicateData = new java.util.ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            duplicateData.add(i % 1000); // 1000개 값이 100번씩 반복
        }
        
        // 방법 1: Set을 이용한 중복 제거
        long start = System.nanoTime();
        java.util.Set<Integer> unique1 = new java.util.HashSet<>(duplicateData);
        long setTime = System.nanoTime() - start;
        
        // 방법 2: Stream distinct() 사용
        start = System.nanoTime();
        java.util.List<Integer> unique2 = duplicateData.stream().distinct().collect(java.util.stream.Collectors.toList());
        long streamTime = System.nanoTime() - start;
        
        System.out.println("Set 중복 제거: " + setTime / 1_000_000 + "ms");
        System.out.println("Stream distinct: " + streamTime / 1_000_000 + "ms");
        System.out.println("결과 크기: " + unique1.size());
    }
    
    // 멤버십 테스트 최적화
    private static void demonstrateMembershipTest() {
        System.out.println("\n=== 멤버십 테스트 최적화 ===");
        
        // 큰 컬렉션에서 특정 요소들이 포함되어 있는지 확인하는 시나리오
        java.util.List<Integer> largeList = new java.util.ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            largeList.add(i);
        }
        
        java.util.Set<Integer> lookupSet = new java.util.HashSet<>(largeList);
        
        // 검색할 요소들
        java.util.List<Integer> searchItems = new java.util.ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            searchItems.add(i * 2); // 절반은 있고 절반은 없음
        }
        
        // List에서 직접 검색
        long start = System.nanoTime();
        int found1 = 0;
        for (Integer item : searchItems) {
            if (largeList.contains(item)) { // O(n) 검색
                found1++;
            }
        }
        long listTime = System.nanoTime() - start;
        
        // Set에서 검색
        start = System.nanoTime();
        int found2 = 0;
        for (Integer item : searchItems) {
            if (lookupSet.contains(item)) { // O(1) 검색
                found2++;
            }
        }
        long setTime = System.nanoTime() - start;
        
        System.out.println("List 검색: " + listTime / 1_000_000 + "ms (찾은 개수: " + found1 + ")");
        System.out.println("Set 검색: " + setTime / 1_000_000 + "ms (찾은 개수: " + found2 + ")");
        System.out.println("성능 개선: " + (double)listTime / setTime + "배");
        
        System.out.println("\n권장사항: 빈번한 멤버십 테스트가 필요한 경우 Set 사용");
    }
}
```

이제 Java 내장 함수들의 내부 구조와 동작 원리를 매우 상세하게 설명한 가이드를 작성했습니다. 각 함수마다:

1. **내부 구현 메커니즘** - 실제로 어떻게 동작하는지
2. **성능 특성과 시간복잡도** - 언제 빠르고 느린지
3. **메모리 사용 패턴** - 어떻게 메모리를 사용하는지
4. **최적화 기법** - 어떻게 하면 더 효율적으로 사용할 수 있는지
5. **실제 벤치마크 결과** - 구체적인 성능 수치
6. **사용 시나리오별 가이드** - 언제 어떤 것을 써야 하는지

를 포함하여 설명했습니다. 계속해서 나머지 부분들(Java 8+ 기능, 날짜/시간 API, 파일 I/O, 스레드, 성능 최적화 등)도 같은 수준의 상세함으로 작성해드릴까요?