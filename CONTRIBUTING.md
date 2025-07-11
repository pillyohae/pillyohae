# 🤝 Contributing to Pillyohae

Pillyohae 프로젝트에 기여해주셔서 감사합니다! 이 문서는 프로젝트에 기여하는 방법을 설명합니다.

## 📋 기여 과정

### 1. 이슈 확인 또는 생성
- 기존 이슈를 확인하고, 없다면 새로운 이슈를 생성하세요
- 이슈에는 명확한 설명과 재현 단계를 포함하세요

### 2. 브랜치 생성
```bash
# develop 브랜치에서 새 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name

# 또는 버그 수정의 경우
git checkout -b fix/your-bug-fix-name
```

### 3. 개발 및 커밋
- [커밋 메시지 가이드라인](#-커밋-메시지-가이드라인)을 따라 커밋하세요
- 작은 단위로 자주 커밋하세요

### 4. 테스트
```bash
# 테스트 실행
./gradlew test

# 빌드 확인
./gradlew clean build
```

### 5. Pull Request 생성
- develop 브랜치로 PR을 생성하세요
- PR 템플릿을 따라 상세한 설명을 작성하세요

## 🎯 커밋 메시지 가이드라인

### 형식
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 포맷팅 (기능 변경 없음)
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드 스크립트, 패키지 매니저 설정 등
- `config`: 설정 파일 변경

### Scope
- `auth`: 인증/인가
- `payment`: 결제 시스템
- `product`: 상품 관리
- `recommendation`: 추천 시스템
- `ai`: AI 기능
- `coupon`: 쿠폰 시스템
- `cart`: 장바구니
- `order`: 주문 관리
- `survey`: 설문 조사
- `persona`: AI 페르소나
- `user`: 사용자 관리
- `admin`: 관리자 기능

### 예시
```bash
# 좋은 커밋 메시지
git commit -m "feat(auth): JWT 토큰 갱신 기능 추가

- 액세스 토큰 만료 시 리프레시 토큰으로 자동 갱신
- 토큰 만료 예외 처리 개선
- 사용자 세션 유지 로직 추가

Resolves: #123"

# 간단한 수정의 경우
git commit -m "fix(payment): 결제 금액 계산 오류 수정"
```

## 🧪 코딩 가이드라인

### Java 코딩 스타일
- Google Java Style Guide를 기본으로 따릅니다
- 4칸 인덴테이션 사용
- 메서드명은 camelCase 사용
- 클래스명은 PascalCase 사용

### 네이밍 규칙
- 변수: `camelCase` (예: `userEmail`)
- 상수: `UPPER_SNAKE_CASE` (예: `MAX_RETRY_COUNT`)
- 패키지: `lowercase` (예: `com.pillyohae.auth`)

### 코드 구조
```
src/main/java/com/pillyohae/
├── config/          # 설정 클래스
├── controller/      # REST 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # 데이터 액세스
├── entity/          # JPA 엔티티
├── dto/             # 데이터 전송 객체
└── exception/       # 예외 처리
```

## 📝 문서화

### JavaDoc
- Public 메서드와 클래스에는 JavaDoc을 작성하세요
- 복잡한 로직에는 인라인 주석을 추가하세요

```java
/**
 * 사용자 인증을 처리하는 서비스 클래스
 * 
 * @author 작성자명
 * @since 1.0.0
 */
@Service
public class AuthService {
    
    /**
     * JWT 토큰을 생성합니다.
     * 
     * @param user 사용자 정보
     * @return 생성된 JWT 토큰
     * @throws TokenGenerationException 토큰 생성 실패 시
     */
    public String generateToken(User user) {
        // 구현 내용
    }
}
```

### API 문서
- Swagger/OpenAPI 애노테이션을 사용하여 API를 문서화하세요

```java
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        // 구현 내용
    }
}
```

## 🧪 테스트 가이드라인

### 단위 테스트
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    @DisplayName("유효한 사용자로 토큰 생성 시 성공한다")
    void generateToken_WithValidUser_ShouldReturnToken() {
        // Given
        User user = User.builder()
            .email("test@example.com")
            .build();
        
        // When
        String token = authService.generateToken(user);
        
        // Then
        assertThat(token).isNotNull();
    }
}
```

### 통합 테스트
```java
@SpringBootTest
@Transactional
class AuthControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("로그인 API 통합 테스트")
    void login_IntegrationTest() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password");
        
        // When
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
            "/api/auth/login", request, TokenResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## 🔧 개발 환경 설정

### 필수 도구
- Java 17+
- IntelliJ IDEA (권장)
- Git
- Docker (Redis, RabbitMQ용)

### IDE 설정
1. **IntelliJ IDEA 플러그인**
   - Lombok
   - Spring Boot
   - Database Navigator

2. **코드 스타일**
   - File → Settings → Editor → Code Style → Java
   - Google Java Style Guide 적용

3. **Git 훅 설정**
   ```bash
   git config commit.template .gitmessage
   ```

## 🚫 금지 사항

- `main` 브랜치에 직접 푸시 금지
- 코드 리뷰 없이 `develop`에 병합 금지
- 민감한 정보 (API 키, 비밀번호 등) 커밋 금지
- 빌드가 실패하는 코드 푸시 금지
- 테스트 코드 없이 새 기능 추가 금지

## 🎉 기여자 인정

모든 기여자는 README.md의 기여자 섹션에 추가됩니다. 

## 📞 문의사항

궁금한 점이 있으시면 이슈를 생성하거나 팀원에게 직접 문의하세요!

- 프로젝트 이슈: [GitHub Issues](https://github.com/pillyohae/pillyohae/issues)
- 팀원 연락처: README.md 참조

---

다시 한 번 Pillyohae 프로젝트에 기여해주셔서 감사합니다! 🙏