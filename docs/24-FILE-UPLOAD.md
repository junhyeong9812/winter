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

#### MultipartRequest 구현
```java
public class MultipartRequest extends HttpRequest {
    private Map<String, List<MultipartFile>> files;
    
    // Multipart 요청 파싱
    public MultipartFile getFile(String name);
    public List<MultipartFile> getFiles(String name);
    public Map<String, List<MultipartFile>> getFileMap();
}
```

### 2. Multipart 파서 (MultipartParser)

HTTP 요청에서 Multipart 데이터를 추출하는 파서:

```java
public class MultipartParser {
    private static final String BOUNDARY_PREFIX = "--";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String CONTENT_TYPE = "Content-Type";
    
    public static MultipartRequest parseRequest(HttpRequest request) {
        // Content-Type에서 boundary 추출
        // 각 part별로 헤더와 바디 분리
        // 파일과 일반 파라미터 구분
        // MultipartFile 객체 생성
    }
}
```

### 3. 파일 업로드 구성

#### UploadConfig 설정
```java
public class UploadConfig {
    private String uploadDir = "./uploads";      // 업로드 디렉토리
    private long maxFileSize = 10 * 1024 * 1024; // 최대 파일 크기 (10MB)
    private long maxRequestSize = 50 * 1024 * 1024; // 최대 요청 크기 (50MB)
    private String[] allowedExtensions = {".jpg", ".png", ".pdf", ".txt"}; // 허용 확장자
    
    // getter/setter 메서드들
}
```

#### FileUploadUtil 유틸리티
```java
public class FileUploadUtil {
    public static String generateUniqueFileName(String originalFilename);
    public static boolean isAllowedExtension(String filename, String[] allowedExtensions);
    public static void ensureDirectoryExists(String dirPath);
    public static String getFileExtension(String filename);
    public static boolean isValidFileSize(long size, long maxSize);
}
```

### 4. 파라미터 리졸버 확장

기존 ParameterResolver에 MultipartFile 바인딩 지원 추가:

```java
public class ParameterResolver {
    public Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response) {
        // 기존 로직...
        
        // MultipartFile 파라미터 처리
        if (parameter.getType() == MultipartFile.class) {
            if (request instanceof MultipartRequest) {
                MultipartRequest multipartRequest = (MultipartRequest) request;
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramName = getParameterName(requestParam, parameter);
                return multipartRequest.getFile(paramName);
            }
        }
        
        // MultipartFile[] 배열 처리
        if (parameter.getType() == MultipartFile[].class) {
            // 여러 파일 처리 로직
        }
    }
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
            // 파일 저장 로직
            String savedPath = saveFile(file);
            
            ModelAndView mav = new ModelAndView("upload-success");
            mav.addObject("filename", file.getOriginalFilename());
            mav.addObject("savedPath", savedPath);
            mav.addObject("description", description);
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
    List<String> savedFiles = new ArrayList<>();
    
    for (MultipartFile file : files) {
        if (!file.isEmpty()) {
            String savedPath = saveFile(file);
            savedFiles.add(savedPath);
        }
    }
    
    ModelAndView mav = new ModelAndView("upload-multiple-success");
    mav.addObject("uploadedFiles", savedFiles);
    mav.addObject("category", category);
    return mav;
}
```

### 3. 파일과 폼 데이터 혼합

```java
@RequestMapping(value = "/upload/profile", method = "POST")
public ModelAndView uploadProfile(
    @ModelAttribute UserProfile profile,
    @RequestParam("avatar") MultipartFile avatar
) {
    // 프로필 정보 처리
    if (!avatar.isEmpty()) {
        // 아바타 이미지 저장
        profile.setAvatarPath(saveFile(avatar));
    }
    
    // 데이터베이스 저장 로직...
    
    ModelAndView mav = new ModelAndView("profile-updated");
    mav.addObject("profile", profile);
    return mav;
}
```

## 🛠 기술적 특징

### 1. Multipart 파싱
- RFC 2388 Multipart 표준 구현
- Boundary 기반 데이터 분리
- 메모리 효율적인 스트림 처리

### 2. 파일 보안
- 확장자 화이트리스트 검증
- 파일 크기 제한
- 업로드 경로 제한

### 3. 파일명 관리
- 고유한 파일명 생성 (UUID 기반)
- 원본 파일명 보존
- 경로 조작 공격 방지

### 4. 메모리 관리
- 대용량 파일 스트림 처리
- 임시 파일 활용
- 자동 리소스 해제

## 📊 구현 클래스

### 새로 추가되는 클래스들

1. **MultipartFile** (인터페이스) - 업로드된 파일 추상화
2. **StandardMultipartFile** (구현체) - MultipartFile 구현
3. **MultipartRequest** (클래스) - Multipart 요청 확장
4. **MultipartParser** (유틸) - Multipart 데이터 파싱
5. **UploadConfig** (설정) - 업로드 관련 설정
6. **FileUploadUtil** (유틸) - 파일 업로드 유틸리티
7. **FileUploadController** (컨트롤러) - 테스트용 컨트롤러

### 수정되는 클래스들

1. **ParameterResolver** - MultipartFile 파라미터 바인딩 지원
2. **Dispatcher** - Multipart 요청 감지 및 파싱
3. **HttpRequest** - Multipart 지원을 위한 확장

## 🎯 테스트 시나리오

WinterMain에서 8가지 파일 업로드 시나리오를 테스트:

1. **단일 파일 업로드** - 기본 파일 업로드 테스트
2. **다중 파일 업로드** - 여러 파일 동시 업로드
3. **파일 + 폼 데이터** - 파일과 일반 파라미터 혼합
4. **빈 파일 처리** - 빈 파일 업로드 시 처리
5. **파일 크기 제한** - 크기 초과 파일 처리
6. **확장자 제한** - 허용되지 않은 확장자 처리
7. **프로필 업로드** - 사용자 프로필 + 아바타 업로드
8. **파일 정보 조회** - 업로드된 파일 메타데이터 확인

## 🔗 연관된 컴포넌트

- **Dispatcher**: Multipart 요청 감지 및 파싱 통합
- **ParameterResolver**: MultipartFile 파라미터 바인딩
- **FileUploadController**: 테스트용 업로드 컨트롤러
- **UploadConfig**: 업로드 설정 관리
- **FileUploadUtil**: 파일 처리 유틸리티

## 🎉 24단계 완성 효과

- **파일 업로드 지원**: 웹 애플리케이션의 핵심 기능 구현
- **보안 강화**: 파일 업로드 관련 보안 검증
- **개발 편의성**: 어노테이션 기반 파일 처리
- **확장성**: 다양한 파일 타입과 크기 지원
- **안정성**: 메모리 효율적인 대용량 파일 처리

24단계를 통해 Winter Framework는 실제 웹 애플리케이션에서 필수적인 파일 업로드 기능을 완전히 지원하게 되었습니다!

## 🚀 다음 단계 예고

25단계에서는 **Session Management**를 구현하여 사용자 세션과 쿠키 관리 기능을 추가할 예정입니다.