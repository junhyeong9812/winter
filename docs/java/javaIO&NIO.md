# Java I/O 및 NIO 라이브러리 완전 가이드

## 📚 Java I/O (java.io) vs NIO (java.nio)

### 기본 개념 비교

| 구분 | I/O (java.io) | NIO (java.nio) |
|------|---------------|----------------|
| **도입 버전** | JDK 1.0 | JDK 1.4 |
| **처리 방식** | 블로킹 (Blocking) | 논블로킹 (Non-blocking) |
| **데이터 단위** | 스트림 기반 (Stream) | 버퍼 기반 (Buffer) |
| **방향성** | 단방향 (읽기 또는 쓰기) | 양방향 (읽기 및 쓰기) |
| **성능** | 단순한 I/O에 적합 | 대용량, 다중 연결에 적합 |

## 🔧 주요 I/O 클래스들

### 1. InputStream 계열

```java
// 추상 클래스 InputStream의 핵심 메서드들
public abstract class InputStream {
    // 1바이트 읽기 (추상 메서드)
    public abstract int read() throws IOException;
    
    // 바이트 배열로 읽기
    public int read(byte[] b) throws IOException;
    
    // 바이트 배열의 특정 구간에 읽기
    public int read(byte[] b, int off, int len) throws IOException;
    
    // 건너뛰기
    public long skip(long n) throws IOException;
    
    // 사용 가능한 바이트 수
    public int available() throws IOException;
    
    // 스트림 닫기
    public void close() throws IOException;
}
```

**주요 구현체들:**
- `FileInputStream`: 파일에서 바이트 읽기
- `ByteArrayInputStream`: 바이트 배열에서 읽기
- `BufferedInputStream`: 버퍼링된 읽기 (성능 향상)

### 2. Reader 계열 (문자 스트림)

```java
// 추상 클래스 Reader의 핵심 메서드들
public abstract class Reader {
    // 문자 하나 읽기
    public int read() throws IOException;
    
    // 문자 배열로 읽기
    public int read(char[] cbuf) throws IOException;
    
    // 문자 배열의 특정 구간에 읽기
    public abstract int read(char[] cbuf, int off, int len) throws IOException;
    
    // 한 줄 읽기 (BufferedReader에서 제공)
    public String readLine() throws IOException; // BufferedReader에서만
}
```

**주요 구현체들:**
- `FileReader`: 파일에서 문자 읽기
- `StringReader`: 문자열에서 읽기
- `BufferedReader`: 버퍼링된 문자 읽기 (줄 단위 읽기 지원)

### 3. ByteArrayInputStream 상세 분석

```java
public class ByteArrayInputStream extends InputStream {
    protected byte buf[];        // 데이터를 저장하는 바이트 배열
    protected int pos;          // 현재 읽기 위치
    protected int mark = 0;     // 마크된 위치
    protected int count;        // 유효한 바이트 수
    
    // 생성자: 바이트 배열 전체를 입력으로 사용
    public ByteArrayInputStream(byte buf[]) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }
    
    // 생성자: 바이트 배열의 특정 부분만 사용
    public ByteArrayInputStream(byte buf[], int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.mark = offset;
    }
    
    // 1바이트 읽기
    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }
    
    // 바이트 배열로 읽기
    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        
        if (pos >= count) {
            return -1;  // EOF
        }
        
        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        
        System.arraycopy(buf, pos, b, off, len);  // 핵심: 배열 복사
        pos += len;
        return len;
    }
}
```

## 🚀 NIO의 핵심 컴포넌트들

### 1. Buffer 클래스

```java
public abstract class Buffer {
    // Buffer의 4가지 핵심 속성
    private int mark = -1;      // 마크된 위치
    private int position = 0;   // 현재 위치
    private int limit;          // 제한 위치
    private int capacity;       // 전체 용량
    
    // 위치 관계: 0 <= mark <= position <= limit <= capacity
    
    // 핵심 메서드들
    public final Buffer flip() {        // 읽기 모드로 전환
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
    
    public final Buffer clear() {       // 쓰기 모드로 초기화
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
    
    public final Buffer rewind() {      // 처음부터 다시 읽기
        position = 0;
        mark = -1;
        return this;
    }
}
```

### 2. Channel 인터페이스

```java
public interface Channel extends Closeable {
    public boolean isOpen();    // 채널이 열려있는지 확인
    public void close() throws IOException;  // 채널 닫기
}

// 읽기/쓰기 가능한 채널
public interface ReadableByteChannel extends Channel {
    public int read(ByteBuffer dst) throws IOException;
}

public interface WritableByteChannel extends Channel {
    public int write(ByteBuffer src) throws IOException;
}
```

## 📁 Files 클래스 (NIO.2 - JDK 7+)

### 핵심 메서드들

```java
public final class Files {
    // 파일 복사 (우리가 사용하는 핵심 메서드)
    public static long copy(InputStream in, Path target, CopyOption... options)
            throws IOException {
        // 내부적으로 버퍼를 사용해 효율적으로 복사
        // StandardCopyOption.REPLACE_EXISTING 옵션 지원
    }
    
    // 파일 생성
    public static Path createFile(Path path, FileAttribute<?>... attrs)
            throws IOException;
    
    // 디렉토리 생성
    public static Path createDirectories(Path dir, FileAttribute<?>... attrs)
            throws IOException;
    
    // 파일 존재 확인
    public static boolean exists(Path path, LinkOption... options);
    
    // 디렉토리 확인
    public static boolean isDirectory(Path path, LinkOption... options);
    
    // 파일 크기
    public static long size(Path path) throws IOException;
}
```

## 🔄 clone() 메서드와 Arrays 유틸리티

### 1. byte[] 배열의 clone()

```java
public class ArrayCloneExample {
    public static void main(String[] args) {
        byte[] original = {1, 2, 3, 4, 5};
        
        // 1. clone() 사용 - 얕은 복사
        byte[] cloned = original.clone();
        
        // 2. System.arraycopy() 사용
        byte[] copied = new byte[original.length];
        System.arraycopy(original, 0, copied, 0, original.length);
        
        // 3. Arrays.copyOf() 사용
        byte[] arraysCopied = Arrays.copyOf(original, original.length);
        
        // 배열의 clone()은 새로운 배열 객체를 생성하지만
        // 원시 타입 배열이므로 완전한 독립 복사본이 됨
        cloned[0] = 99;
        System.out.println(original[0]);  // 1 (변경되지 않음)
        System.out.println(cloned[0]);    // 99
    }
}
```

### 2. clone() 메서드의 동작 원리

```java
// Object 클래스의 clone() 메서드
protected native Object clone() throws CloneNotSupportedException;

// 배열에서 clone() 재정의 (JVM 레벨에서 구현)
public class Array {
    public Object clone() {
        // 새로운 배열 객체 생성 후 모든 요소 복사
        // 원시 타입: 값 복사 (deep copy 효과)
        // 참조 타입: 참조 복사 (shallow copy)
    }
}
```

## 🎯 우리 코드에서의 활용

### StandardMultipartFile에서의 I/O 사용

```java
// 1. 방어적 복사 - clone() 사용
@Override
public byte[] getBytes() throws IOException {
    return content.clone();  // 원본 데이터 보호
}

// 2. ByteArrayInputStream 생성
@Override
public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(content);
}

// 3. NIO Files.copy() 사용
@Override
public void transferTo(File dest) throws IOException {
    try (InputStream is = getInputStream()) {
        Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
```

## 📊 성능 비교

### I/O vs NIO 벤치마크

| 작업 | I/O (java.io) | NIO (java.nio) |
|------|---------------|----------------|
| **작은 파일 (< 1MB)** | 빠름 | 비슷하거나 약간 느림 |
| **큰 파일 (> 10MB)** | 보통 | 빠름 |
| **다중 파일 처리** | 느림 | 매우 빠름 |
| **네트워크 I/O** | 블로킹으로 비효율적 | 논블로킹으로 효율적 |
| **메모리 사용량** | 높음 | 낮음 (버퍼 재사용) |

## 🛠 실제 사용 시나리오

### 1. 파일 업로드 처리

```java
// I/O 방식 (우리가 사용하는 방식)
public void saveFileIO(MultipartFile file, String path) throws IOException {
    try (InputStream is = file.getInputStream();
         FileOutputStream fos = new FileOutputStream(path)) {
        
        byte[] buffer = new byte[8192];  // 8KB 버퍼
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
    }
}

// NIO 방식 (더 효율적)
public void saveFileNIO(MultipartFile file, String path) throws IOException {
    try (InputStream is = file.getInputStream()) {
        Files.copy(is, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
    }
}
```

### 2. 대용량 파일 처리

```java
// NIO의 메모리 매핑 사용 (매우 큰 파일용)
public void processLargeFile(String filePath) throws IOException {
    try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
         FileChannel channel = file.getChannel()) {
        
        // 파일을 메모리에 매핑 (실제로는 가상 메모리 사용)
        MappedByteBuffer buffer = channel.map(
            FileChannel.MapMode.READ_ONLY, 0, channel.size());
        
        // 매우 효율적인 파일 처리 가능
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            // 처리...
        }
    }
}
```

## 💡 핵심 포인트

1. **I/O는 단순함**: 작은 파일이나 순차 처리에 적합
2. **NIO는 성능**: 대용량 데이터나 동시 처리에 적합
3. **Files.copy()**: 내부적으로 최적화된 복사 알고리즘 사용
4. **ByteArrayInputStream**: 메모리 상의 데이터를 스트림으로 처리
5. **Buffer 패턴**: NIO의 핵심은 버퍼 기반 처리

우리의 파일 업로드 구현에서는 **단순함과 호환성**을 위해 I/O를 사용하되, **성능이 중요한 transferTo()에서는 NIO의 Files.copy()를 활용**하는 하이브리드 접근 방식을 사용합니다.