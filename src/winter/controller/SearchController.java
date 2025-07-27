package winter.controller;

import winter.annotation.Controller;
import winter.annotation.ModelAttribute;
import winter.annotation.RequestMapping;
import winter.annotation.RequestParam;
import winter.form.SearchForm;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/**
 * 23단계 파라미터 바인딩을 테스트하기 위한 컨트롤러
 *
 * 다양한 파라미터 바인딩 시나리오를 제공합니다:
 * 1. @RequestParam을 사용한 개별 파라미터 바인딩
 * 2. @ModelAttribute를 사용한 객체 바인딩
 * 3. 기본값과 필수값 처리
 * 4. 여러 파라미터 조합
 * 5. 타입 변환 테스트
 */
@Controller
public class SearchController {

    /**
     * 기본 검색 - @RequestParam 사용
     * GET /search?keyword=spring&page=1
     */
    @RequestMapping(value = "/search", method = "GET")
    public ModelAndView search(@RequestParam("keyword") String keyword,
                               @RequestParam(value = "page", defaultValue = "1") int page) {

        ModelAndView mv = new ModelAndView("search-results");
        mv.addAttribute("keyword", keyword);
        mv.addAttribute("page", page);
        mv.addAttribute("message", "Search completed successfully!");

        // 검색 결과 시뮬레이션
        mv.addAttribute("resultCount", keyword.length() * 10);  // 키워드 길이 * 10
        mv.addAttribute("totalPages", (page <= 3) ? 5 : page + 2);

        return mv;
    }

    /**
     * 고급 검색 - 여러 @RequestParam 조합
     * GET /search/advanced?keyword=spring&category=tech&minPrice=1000&maxPrice=5000&sortBy=date
     */
    @RequestMapping(value = "/search/advanced", method = "GET")
    public ModelAndView advancedSearch(@RequestParam("keyword") String keyword,
                                       @RequestParam(value = "category", defaultValue = "all") String category,
                                       @RequestParam(value = "minPrice", required = false) Integer minPrice,
                                       @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                                       @RequestParam(value = "sortBy", defaultValue = "relevance") String sortBy) {

        ModelAndView mv = new ModelAndView("advanced-search-results");
        mv.addAttribute("keyword", keyword);
        mv.addAttribute("category", category);
        mv.addAttribute("minPrice", minPrice);
        mv.addAttribute("maxPrice", maxPrice);
        mv.addAttribute("sortBy", sortBy);

        // 검색 조건 요약
        StringBuilder summary = new StringBuilder();
        summary.append("Searching for '").append(keyword).append("'");
        summary.append(" in category '").append(category).append("'");
        if (minPrice != null || maxPrice != null) {
            summary.append(" with price range ");
            summary.append(minPrice != null ? minPrice : "0");
            summary.append(" - ");
            summary.append(maxPrice != null ? maxPrice : "∞");
        }
        summary.append(", sorted by ").append(sortBy);

        mv.addAttribute("searchSummary", summary.toString());
        mv.addAttribute("resultCount", 42);  // 고정값

        return mv;
    }

    /**
     * 검색 폼 제출 - @ModelAttribute 사용
     * POST /search/form
     */
    @RequestMapping(value = "/search/form", method = "POST")
    public ModelAndView searchForm(@ModelAttribute SearchForm searchForm, HttpResponse response) {

        // 검증
        if (searchForm.getKeyword() == null || searchForm.getKeyword().trim().isEmpty()) {
            response.setStatus(400);

            ModelAndView mv = new ModelAndView("error");
            mv.addAttribute("errorMessage", "Keyword is required for search");
            return mv;
        }

        ModelAndView mv = new ModelAndView("form-search-results");
        mv.addAttribute("searchForm", searchForm);
        mv.addAttribute("message", "Form search completed!");

        // 검색 결과 시뮬레이션
        int resultCount = searchForm.getKeyword().length() * searchForm.getPage() * 5;
        mv.addAttribute("resultCount", resultCount);

        return mv;
    }

    /**
     * 혼합 파라미터 - @RequestParam과 @ModelAttribute 조합
     * POST /search/mixed?userId=123&debug=true
     * Body: keyword=spring&category=tech&page=2
     */
    @RequestMapping(value = "/search/mixed", method = "POST")
    public ModelAndView mixedSearch(@RequestParam("userId") long userId,
                                    @RequestParam(value = "debug", defaultValue = "false") boolean debug,
                                    @ModelAttribute SearchForm searchForm,
                                    HttpRequest request) {

        ModelAndView mv = new ModelAndView("mixed-search-results");
        mv.addAttribute("userId", userId);
        mv.addAttribute("debug", debug);
        mv.addAttribute("searchForm", searchForm);
        mv.addAttribute("userAgent", request.getHeader("User-Agent"));

        // 디버그 정보 추가
        if (debug) {
            mv.addAttribute("debugInfo", String.format(
                    "User %d searched for '%s' in category '%s' on page %d",
                    userId, searchForm.getKeyword(), searchForm.getCategory(), searchForm.getPage()
            ));
        }

        mv.addAttribute("message", "Mixed parameter search completed!");

        return mv;
    }

    /**
     * 배열 파라미터 테스트
     * GET /search/tags?tags=java,spring,mvc&includeAll=true
     */
    @RequestMapping(value = "/search/tags", method = "GET")
    public ModelAndView searchByTags(@RequestParam("tags") String[] tags,
                                     @RequestParam(value = "includeAll", defaultValue = "false") boolean includeAll) {

        ModelAndView mv = new ModelAndView("tag-search-results");
        mv.addAttribute("tags", tags);
        mv.addAttribute("includeAll", includeAll);

        // 태그 검색 시뮬레이션
        int totalResults = 0;
        for (String tag : tags) {
            totalResults += tag.length() * 3;  // 각 태그 길이 * 3
        }

        if (!includeAll) {
            totalResults *= 2;  // OR 검색이면 결과가 더 많음
        }

        mv.addAttribute("resultCount", totalResults);
        mv.addAttribute("message", String.format(
                "Found %d results for %s search with tags: %s",
                totalResults,
                includeAll ? "AND" : "OR",
                String.join(", ", tags)
        ));

        return mv;
    }

    /**
     * 타입 변환 테스트
     * GET /search/filter?minRating=4.5&publishedAfter=2023-01-01&isAvailable=true
     */
    @RequestMapping(value = "/search/filter", method = "GET")
    public ModelAndView searchWithFilter(@RequestParam("minRating") double minRating,
                                         @RequestParam(value = "publishedAfter", required = false) String publishedAfter,
                                         @RequestParam(value = "isAvailable", defaultValue = "true") boolean isAvailable) {

        ModelAndView mv = new ModelAndView("filter-search-results");
        mv.addAttribute("minRating", minRating);
        mv.addAttribute("publishedAfter", publishedAfter);
        mv.addAttribute("isAvailable", isAvailable);

        // 필터 조건 요약
        StringBuilder filterSummary = new StringBuilder();
        filterSummary.append("Rating ≥ ").append(minRating);
        if (publishedAfter != null) {
            filterSummary.append(", Published after ").append(publishedAfter);
        }
        filterSummary.append(", Available: ").append(isAvailable ? "Yes" : "No");

        mv.addAttribute("filterSummary", filterSummary.toString());
        mv.addAttribute("resultCount", (int)(minRating * 20));

        return mv;
    }
}