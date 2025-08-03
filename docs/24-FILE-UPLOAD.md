# 24단계: 파일 업로드 (File Upload)

## 📋 단계 개요

24단계에서는 Multipart 형태의 파일 업로드 기능을 구현합니다. HTTP Multipart 요청을 파싱하고, 업로드된 파일을 서버에 저장하며, 컨트롤러에서 간편하게 파일을 처리할 수 있는 시스템을 완성합니다.

## 🎯 주요 목표

- **Multipart 요청 파싱** - `multipart/form-data` 형태의 HTTP 요청 처리
- **파일 업로드 처리** - 업로드된 파일을 서버 파일시스템에 저장
- **@RequestParam MultipartFile** - 어노테이션을 통한 파일 파라미터 바인딩
- **파일 메타데이터 관리** - 원본 파일명, 크기, 타입 등 정보 추출
- **업로드 제한 설정** - 파일 크기, 확장자 제한 등 보안 기능

## 🔧 주요 구현 내용

### 1. Multipart 요청 처리

#### MultipartFile 인터페이스
```java
public interface MultipartFile {
    String getName();                    // 폼 필드명
    String getOriginalFilename();        // 원본 파일명
    String getContentType();             // MIME 타입
    boolean isEmpty();                   // 빈 파일 여부
    long getSize();                      // 파일 크기
    byte[] getBytes();                   // 파일 내용
    InputStream getInputStream();        // 파일 스트림
    void transferTo(File dest);          // 파일 저장
}
```

#### StandardMultipartFile 구현체
```java
public class StandardMultipartFile implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;
    
    // 파일 내용을 메모리에서 관리
    // 방어적 복사로 데이터 안전성 보장
    // transferTo()로 효율적인 파일 저장
}
```

### 2. MultipartRequest 확장 클래스

기존 HttpRequest를 확장하여 파일 업로드 기능을 추가:

```java
public class MultipartRequest extends HttpRequest {
    private final Map<String, List<MultipartFile>> files;
    
    public MultipartFile getFile(String name);
    public List<MultipartFile> getFiles(String name);
    public Map<String, List<MultipartFile>> getFileMap();
    public boolean hasFiles();
    public Set<String> getFileNames();
    public int getFileCount();
    public long getTotalFileSize();
}
```

### 3. MultipartParser - 요청 파싱

RFC 2388 표준에 따른 Multipart 요청 파싱:

```java
public class MultipartParser {
    public static MultipartRequest parseRequest(HttpRequest request) {
        // 1. Content-Type에서 boundary 추출
        // 2. 요청 본문을 boundary로 분리
        // 3. 각 part의 헤더와 바디 파싱
        // 4. 파일과 일반 파라미터 구분
        // 5. MultipartFile 객체 생성
        // 6. MultipartRequest 반환
    }
}
```

### 4. Dispatcher 통합

Dispatcher에서 Multipart 요청 자동 감지 및 처리:

```java
public class Dispatcher {
    public void dispatch(HttpRequest request, HttpResponse response) {
        // 0. Multipart 요청 감지 및 파싱 (새로 추가)
        if (isMultipartRequest(request)) {
            request = MultipartParser.parseRequest(request);
        }
        
        // 기존 처리 흐름 유지...
    }
    
    private boolean isMultipartRequest(HttpRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) &&
               request.getHeader("Content-Type") != null &&
               request.getHeader("Content-Type").toLowerCase()
                      .startsWith("multipart/form-data");
    }
}
```

### 5. ParameterResolver 확장

MultipartFile 파라미터 바인딩 지원 추가:

```java
public class ParameterResolver {
    public Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response) {
        Class<?> paramType = parameter.getType();
        
        // MultipartFile 단일 파일 처리
        if (paramType.equals(MultipartFile.class)) {
            return resolveMultipartFile(parameter, request);
        }
        
        // MultipartFile[] 다중 파일 처리
        if (paramType.equals(MultipartFile[].class)) {
            return resolveMultipartFileArray(parameter, request);
        }
        
        // 기존 처리 로직...
    }
}
```

### 6. 파일 업로드 설정 관리

#### UploadConfig 클래스
```java
public class UploadConfig {
    private String uploadDir = "./uploads";
    private long maxFileSize = 10 * 1024 * 1024;  // 10MB
    private long maxRequestSize = 50 * 1024 * 1024; // 50MB
    private String[] allowedExtensions = {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp",
        ".pdf", ".doc", ".docx", ".txt", ".rtf"
    };
    private boolean createUploadDir = true;
    private boolean overwriteExisting = true;
    
    // 체이닝 방식의 설정 메서드들
    public UploadConfig setMaxFileSizeMB(int mb);
    public UploadConfig allowImagesOnly();
    public UploadConfig allowDocumentsOnly();
}
```

#### FileUploadUtil 유틸리티
```java
public class FileUploadUtil {
    // 고유 파일명 생성
    public static String generateUniqueFileName(String originalFilename);
    
    // 파일 안전성 검증
    public static boolean isSafeFilename(String filename);
    public static boolean isAllowedExtension(String filename, String[] allowed);
    public static boolean isValidFileSize(long size, long maxSize);
    
    // 파일 정보 유틸리티
    public static String formatFileSize(long bytes);
    public static boolean isImageFile(String contentType);
    public static boolean isTextFile(String contentType);
    
    // 디렉토리 및 경로 관리
    public static void ensureDirectoryExists(String dirPath);
    public static String createSafeFilePath(String uploadDir, String filename);
}
```

## 📝 사용 예시

### 1. 단일 파일 업로드

```java
@Controller
public class FileUploadController {
    
    @RequestMapping(value = "/upload", method = "POST")
    public ModelAndView uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("description") String description
    ) {
        if (!file.isEmpty()) {
            // 파일 검증
            if (!FileUploadUtil.isValidFileSize(file.getSize(), uploadConfig.getMaxFileSize())) {
                return new ModelAndView("upload-error");
            }
            
            if (!FileUploadUtil.isAllowedExtension(file.getOriginalFilename(), 
                                                   uploadConfig.getAllowedExtensions())) {
                return new ModelAndView("upload-error");
            }
            
            // 파일 저장
            String savedPath = saveFile(file);
            
            ModelAndView mav = new ModelAndView("upload-success");
            mav.addAttribute("filename", file.getOriginalFilename());
            mav.addAttribute("savedPath", savedPath);
            mav.addAttribute("fileSize", FileUploadUtil.formatFileSize(file.getSize()));
            return mav;
        }
        
        return new ModelAndView("upload-error");
    }
}
```

### 2. 다중 파일 업로드

```java
@RequestMapping(value = "/upload/multiple", method = "POST")
public ModelAndView uploadMultipleFiles(
    @RequestParam("files") MultipartFile[] files,
    @RequestParam("category") String category
) {
    List<UploadResult> results = new ArrayList<>();
    long totalSize = 0;
    
    for (MultipartFile file : files) {
        if (!file.isEmpty()) {
            totalSize += file.getSize();
            
            // 전체 요청 크기 검증
            if (totalSize > uploadConfig.getMaxRequestSize()) {
                // 오류 처리
            }
            
            // 개별 파일 검증 및 저장
            String savedPath = saveFile(file);
            results.add(new UploadResult(file, savedPath, true, null));
        }
    }
    
    ModelAndView mav = new ModelAndView("upload-multiple-success");
    mav.addAttribute("results", results);
    mav.addAttribute("category", category);
    return mav;
}
```

### 3. 프로필 + 아바타 업로드 (파일 + 폼 데이터 혼합)

```java
@RequestMapping(value = "/upload/profile", method = "POST")
public ModelAndView uploadProfile(
    @ModelAttribute UserProfile profile,
    @RequestParam(value = "avatar", required = false) MultipartFile avatar
) {
    // 프로필 정보 검증
    if (profile.getName() == null || profile.getName().trim().isEmpty()) {
        return new ModelAndView("upload-error");
    }
    
    // 아바타 이미지 처리
    if (avatar != null && !avatar.isEmpty()) {
        // 이미지 파일 검증
        if (!FileUploadUtil.isImageFile(avatar.getContentType())) {
            return new ModelAndView("upload-error");
        }
        
        // 아바타 저장
        String avatarPath = saveFile(avatar);
        profile.setAvatarPath(avatarPath);
    }
    
    // 프로필 저장
    ModelAndView mav = new ModelAndView("profile-updated");
    mav.addAttribute("profile", profile);
    mav.addAttribute("hasAvatar", avatar != null && !avatar.isEmpty());
    return mav;
}
```

### 4. 파일 정보 조회 (AJAX용)

```java
@RequestMapping(value = "/upload/info", method = "POST")
public ModelAndView getFileInfo(@RequestParam("file") MultipartFile file) {
    ModelAndView mav = new ModelAndView("json");
    
    if (file == null || file.isEmpty()) {
        mav.addAttribute("success", false);
        mav.addAttribute("error", "파일이 없습니다.");
        return mav;
    }
    
    // 파일 정보 수집
    FileInfo info = new FileInfo();
    info.setName(file.getOriginalFilename());
    info.setSize(file.getSize());
    info.setFormattedSize(FileUploadUtil.formatFileSize(file.getSize()));
    info.setContentType(file.getContentType());
    info.setIsImage(FileUploadUtil.isImageFile(file.getContentType()));
    info.setIsSafe(FileUploadUtil.isSafeFilename(file.getOriginalFilename()));
    
    mav.addAttribute("success", true);
    mav.addAttribute("fileInfo", info);
    return mav;
}
```

## 🛠 기술적 특징

### 1. 보안 중심 설계
- **확장자 화이트리스트** 검증으로 실행 파일 업로드 차단
- **파일명 안전성** 검사로 경로 조작 공격 방지
- **파일 크기 제한**으로 DoS 공격 방지
- **Content-Type 검증**으로 MIME 타입 위조 방지

### 2. 메모리 효율성
- **스트림 기반** 파일 처리로 대용량 파일 지원
- **방어적 복사**로 데이터 안전성 보장
- **자동 리소스 해제**로 메모리 누수 방지

### 3. 개발자 친화적 API
- **어노테이션 기반** 파라미터 바인딩
- **체이닝 방식** 설정으로 직관적인 구성
- **상세한 오류 메시지**로 디버깅 지원
- **템플릿 기반** 응답으로 사용자 친화적 UI

### 4. 확장성과 호환성
- **기존 HttpRequest 완전 호환**
- **다양한 파라미터 타입** 지원 (단일/다중 파일, 폼 데이터 혼합)
- **플러그인 방식** 확장으로 새로운 검증 규칙 추가 가능

## 📊 구현 클래스 상세

### 새로 추가된 클래스들

1. **MultipartFile** (인터페이스) - 업로드된 파일 추상화
2. **StandardMultipartFile** (구현체) - 메모리 기반 MultipartFile 구현
3. **MultipartRequest** (클래스) - HttpRequest 확장으로 파일 업로드 지원
4. **MultipartParser** (유틸) - RFC 2388 표준 Multipart 파싱
5. **UploadConfig** (설정) - 업로드 관련 중앙 설정 관리
6. **FileUploadUtil** (유틸) - 파일 처리 관련 종합 유틸리티
7. **FileUploadController** (컨트롤러) - 다양한 업로드 시나리오 테스트

### 수정된 클래스들

1. **Dispatcher** - Multipart 요청 자동 감지 및 파싱 통합
2. **ParameterResolver** - MultipartFile/MultipartFile[] 파라미터 바인딩 지원

## 🎯 테스트 시나리오

FileUploadController에서 제공하는 8가지 업로드 시나리오:

1. **단일 파일 업로드** (`/upload`)
    - 파일 + 설명 텍스트 업로드
    - 파일 크기, 확장자, 안전성 검증
    - 상세한 업로드 결과 표시

2. **다중 파일 업로드** (`/upload/multiple`)
    - 여러 파일 동시 업로드
    - 개별 파일별 성공/실패 결과
    - 전체 요청 크기 제한 검증

3. **프로필 + 아바타 업로드** (`/upload/profile`)
    - 폼 데이터와 파일의 혼합 처리
    - 이미지 파일 전용 검증
    - 객체 바인딩과 파일 업로드 연동

4. **파일 정보 조회** (`/upload/info`)
    - AJAX 방식 파일 정보 분석
    - JSON 응답으로 파일 메타데이터 제공

5. **업로드 폼 페이지** (`/upload/form`)
    - 다양한 업로드 시나리오 테스트 UI
    - 현재 설정 정보 표시

6. **오류 처리 검증**
    - 파일 크기 초과
    - 허용되지 않은 확장자
    - 빈 파일 업로드
    - 안전하지 않은 파일명

7. **템플릿 기반 응답**
    - `upload-success.html` - 성공 시 상세 정보
    - `upload-error.html` - 실패 시 오류 정보 및 해결 방법
    - `upload-multiple-success.html` - 다중 파일 업로드 결과

## 🎉 24단계 완성 효과

### 개발 편의성 향상
- **어노테이션 기반** 파일 처리로 코드 간소화
- **자동 파라미터 바인딩**으로 보일러플레이트 코드 제거
- **상세한 검증과 오류 메시지**로 디버깅 시간 단축

### 보안 강화
- **다층 보안 검증** (확장자, 파일명, 크기, Content-Type)
- **경로 조작 공격 방지**로 서버 보안 강화
- **실행 파일 차단**으로 악성코드 업로드 방지

### 성능 최적화
- **스트림 기반 처리**로 메모리 효율성 극대화
- **파일 크기 제한**으로 서버 부하 방지
- **고유 파일명 생성**으로 파일 충돌 방지

### 확장성 확보
- **플러그인 방식** 설정으로 다양한 요구사항 대응
- **기존 코드 완전 호환**으로 점진적 마이그레이션 가능
- **표준 준수**로 다른 시스템과의 연동성 확보

24단계를 통해 Winter Framework는 **엔터프라이즈급 파일 업로드 시스템**을 완전히 지원하게 되었습니다! 이제 실제 프로덕션 환경에서 요구되는 모든 파일 업로드 시나리오를 안전하고 효율적으로 처리할 수 있습니다.

## 🚀 다음 단계 예고

25단계에서는 **Session Management**를 구현하여 사용자 세션과 쿠키 관리 기능을 추가할 예정입니다.