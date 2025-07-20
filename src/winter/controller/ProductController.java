package winter.controller;

import winter.annotation.Controller;
import winter.annotation.RequestMapping;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.view.ModelAndView;

/**
 * 어노테이션 기반 컨트롤러 예시
 *
 * 기존 Controller 인터페이스를 구현하지 않고,
 * @Controller와 @RequestMapping 어노테이션만으로 컨트롤러를 정의합니다.
 *
 * 특징:
 * - 클래스에 @Controller 어노테이션
 * - 메서드에 @RequestMapping으로 URL과 HTTP 메서드 매핑
 * - 하나의 컨트롤러 클래스에 여러 엔드포인트 정의 가능
 * - 메서드별로 다른 파라미터 구성 지원
 */
@Controller
public class ProductController {

    /**
     * 모든 상품 목록 조회
     * GET /products
     *
     * 파라미터 없는 메서드 예시
     */
    @RequestMapping(value = "/products", method = "GET")
    public ModelAndView getAllProducts() {
        ModelAndView mv = new ModelAndView("products");
        mv.addAttribute("title", "Product List");
        mv.addAttribute("productCount", 5);
        mv.addAttribute("message", "Welcome to our product catalog!");

        return mv;
    }

    /**
     * 상품 상세 정보 조회
     * GET /product/detail
     *
     * HttpRequest 파라미터를 받는 메서드 예시
     */
    @RequestMapping(value = "/product/detail", method = "GET")
    public ModelAndView getProductDetail(HttpRequest request) {
        String productId = request.getParameter("id");
        String productName = request.getParameter("name");

        ModelAndView mv = new ModelAndView("product-detail");
        mv.addAttribute("productId", productId != null ? productId : "unknown");
        mv.addAttribute("productName", productName != null ? productName : "Unknown Product");
        mv.addAttribute("price", 29900);
        mv.addAttribute("description", "This is a sample product description.");

        return mv;
    }

    /**
     * 상품 생성
     * POST /products
     *
     * HttpRequest와 HttpResponse를 모두 받는 메서드 예시
     */
    @RequestMapping(value = "/products", method = "POST")
    public ModelAndView createProduct(HttpRequest request, HttpResponse response) {
        String productName = request.getParameter("name");
        String price = request.getParameter("price");

        // 입력값 검증
        if (productName == null || productName.trim().isEmpty()) {
            response.setStatus(400); // Bad Request
            ModelAndView mv = new ModelAndView("error");
            mv.addAttribute("errorMessage", "Product name is required");
            return mv;
        }

        ModelAndView mv = new ModelAndView("product-created");
        mv.addAttribute("productName", productName);
        mv.addAttribute("price", price != null ? price : "0");
        mv.addAttribute("message", "Product created successfully!");
        mv.addAttribute("timestamp", System.currentTimeMillis());

        return mv;
    }

    /**
     * HTTP 메서드를 지정하지 않은 경우 (모든 메서드 허용)
     * /product/info
     */
    @RequestMapping("/product/info")
    public ModelAndView getProductInfo(HttpRequest request) {
        String httpMethod = request.getMethod();

        ModelAndView mv = new ModelAndView("product-info");
        mv.addAttribute("httpMethod", httpMethod);
        mv.addAttribute("message", "This endpoint accepts all HTTP methods");
        mv.addAttribute("timestamp", System.currentTimeMillis());

        return mv;
    }
}