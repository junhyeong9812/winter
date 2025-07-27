package winter.form;

/**
 * 검색 폼을 위한 데이터 전송 객체 (DTO)
 *
 * @ModelAttribute로 바인딩될 때 사용되는 폼 객체입니다.
 * HTTP 요청 파라미터들이 이 객체의 setter 메서드를 통해 자동으로 바인딩됩니다.
 *
 * 예시 요청: keyword=spring&category=tech&page=2&size=10
 * → SearchForm 객체의 각 필드에 자동 매핑
 */
public class SearchForm {

    private String keyword;      // 검색 키워드
    private String category;     // 카테고리
    private int page = 1;        // 페이지 번호 (기본값: 1)
    private int size = 20;       // 페이지 크기 (기본값: 20)
    private String sortBy;       // 정렬 기준
    private String sortOrder;    // 정렬 순서 (asc, desc)

    /**
     * 기본 생성자 - 필수
     * ModelAttributeBinder가 객체를 생성할 때 사용됩니다.
     */
    public SearchForm() {}

    /**
     * 편의를 위한 생성자
     */
    public SearchForm(String keyword) {
        this.keyword = keyword;
    }

    // Getter 메서드들
    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    // Setter 메서드들 - ModelAttributeBinder가 사용
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * 폼 데이터 유효성 검사
     *
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValid() {
        return keyword != null && !keyword.trim().isEmpty() &&
                page > 0 && size > 0 && size <= 100;
    }

    /**
     * 검색 조건 요약 문자열 생성
     *
     * @return 검색 조건 요약
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Keyword: ").append(keyword != null ? keyword : "none");

        if (category != null && !category.isEmpty()) {
            summary.append(", Category: ").append(category);
        }

        summary.append(", Page: ").append(page);
        summary.append(", Size: ").append(size);

        if (sortBy != null && !sortBy.isEmpty()) {
            summary.append(", Sort: ").append(sortBy);
            if (sortOrder != null && !sortOrder.isEmpty()) {
                summary.append(" ").append(sortOrder);
            }
        }

        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("SearchForm{keyword='%s', category='%s', page=%d, size=%d, sortBy='%s', sortOrder='%s'}",
                keyword, category, page, size, sortBy, sortOrder);
    }
}