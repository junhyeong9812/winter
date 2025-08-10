package winter.controller;

import winter.annotation.Controller;
import winter.annotation.RequestMapping;
import winter.annotation.RequestParam;
import winter.http.HttpRequest;
import winter.http.HttpResponse;
import winter.http.HttpSession;
import winter.view.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 25단계: 세션 관리 기능을 테스트하기 위한 컨트롤러
 * 세션 생성, 속성 설정/조회, 세션 정보 확인, 로그인/로그아웃 시뮬레이션 등의 기능을 제공합니다.
 */
@Controller
public class SessionController {

    /**
     * 세션 테스트 메인 페이지
     */
    @RequestMapping(value = "/session", method = "GET")
    public ModelAndView sessionHome(HttpRequest request) {
        HttpSession session = request.getSession();
        ModelAndView mv = new ModelAndView("session/home");

        mv.addAttribute("sessionId", session.getId());
        mv.addAttribute("isNew", session.isNew());
        mv.addAttribute("creationTime", session.getCreationTime());
        mv.addAttribute("lastAccessedTime", session.getLastAccessedTime());
        mv.addAttribute("maxInactiveInterval", session.getMaxInactiveInterval());

        // 세션에서 사용자 정보 조회
        String username = (String) session.getAttribute("username");
        Integer visitCount = (Integer) session.getAttribute("visitCount");

        mv.addAttribute("username", username != null ? username : "guest");
        mv.addAttribute("visitCount", visitCount != null ? visitCount : 0);

        return mv;
    }

    /**
     * 세션에 속성 설정
     */
    @RequestMapping(value = "/session/set", method = "POST")
    public ModelAndView setSessionAttribute(
            HttpRequest request,
            @RequestParam("key") String key,
            @RequestParam("value") String value) {

        HttpSession session = request.getSession();
        session.setAttribute(key, value);

        ModelAndView mv = new ModelAndView("session/result");
        mv.addAttribute("message", "세션 속성 설정 완료: " + key + " = " + value);
        mv.addAttribute("sessionId", session.getId());

        return mv;
    }

    /**
     * 세션에서 속성 조회
     */
    @RequestMapping(value = "/session/get", method = "GET")
    public ModelAndView getSessionAttribute(
            HttpRequest request,
            @RequestParam("key") String key) {

        HttpSession session = request.getSession(false);
        ModelAndView mv = new ModelAndView("session/result");

        if (session != null) {
            Object value = session.getAttribute(key);
            mv.addAttribute("key", key);
            mv.addAttribute("value", value != null ? value.toString() : "null");
            mv.addAttribute("sessionId", session.getId());
        } else {
            mv.addAttribute("error", "세션이 존재하지 않습니다.");
        }

        return mv;
    }

    /**
     * 로그인 시뮬레이션
     */
    @RequestMapping(value = "/session/login", method = "POST")
    public ModelAndView login(
            HttpRequest request,
            @RequestParam("username") String username,
            @RequestParam(value = "password", defaultValue = "") String password) {

        // 간단한 로그인 검증 (실제로는 데이터베이스 검증)
        if (isValidUser(username, password)) {
            HttpSession session = request.getSession();

            // 세션에 사용자 정보 저장
            session.setAttribute("username", username);
            session.setAttribute("loginTime", System.currentTimeMillis());
            session.setAttribute("role", getUserRole(username));

            // 방문 횟수 증가
            Integer visitCount = (Integer) session.getAttribute("visitCount");
            session.setAttribute("visitCount", visitCount != null ? visitCount + 1 : 1);

            ModelAndView mv = new ModelAndView("session/login-success");
            mv.addAttribute("message", "로그인 성공: " + username);
            mv.addAttribute("sessionId", session.getId());
            mv.addAttribute("username", username);
            mv.addAttribute("role", getUserRole(username));

            return mv;
        } else {
            ModelAndView mv = new ModelAndView("session/login-failed");
            mv.addAttribute("error", "로그인 실패: 사용자명 또는 비밀번호가 올바르지 않습니다.");
            return mv;
        }
    }

    /**
     * 로그아웃
     */
    @RequestMapping(value = "/session/logout", method = "POST")
    public ModelAndView logout(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String username = (String) session.getAttribute("username");
            session.invalidate(); // 세션 무효화

            // 세션 쿠키 삭제
            response.deleteSessionCookie();

            ModelAndView mv = new ModelAndView("session/logout");
            mv.addAttribute("message", "로그아웃 완료" + (username != null ? ": " + username : ""));
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("session/logout");
            mv.addAttribute("message", "로그아웃할 세션이 없습니다.");
            return mv;
        }
    }

    /**
     * 세션 상세 정보 조회
     */
    @RequestMapping(value = "/session/info", method = "GET")
    public ModelAndView sessionInfo(HttpRequest request) {
        HttpSession session = request.getSession(false);
        ModelAndView mv = new ModelAndView("session/info");

        if (session != null) {
            mv.addAttribute("sessionId", session.getId());
            mv.addAttribute("isNew", session.isNew());
            mv.addAttribute("creationTime", session.getCreationTime());
            mv.addAttribute("lastAccessedTime", session.getLastAccessedTime());
            mv.addAttribute("maxInactiveInterval", session.getMaxInactiveInterval());

            // 모든 세션 속성 수집
            Map<String, Object> attributes = new HashMap<>();
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                attributes.put(name, session.getAttribute(name));
            }
            mv.addAttribute("attributes", attributes);

            // 세션 활성 시간 계산
            long activeTime = (System.currentTimeMillis() - session.getCreationTime()) / 1000;
            long inactiveTime = (System.currentTimeMillis() - session.getLastAccessedTime()) / 1000;

            mv.addAttribute("activeTime", activeTime);
            mv.addAttribute("inactiveTime", inactiveTime);

        } else {
            mv.addAttribute("error", "세션이 존재하지 않습니다.");
        }

        return mv;
    }

    /**
     * 쇼핑카트 시뮬레이션 - 상품 추가
     */
    @RequestMapping(value = "/session/cart/add", method = "POST")
    public ModelAndView addToCart(
            HttpRequest request,
            @RequestParam("productId") String productId,
            @RequestParam("productName") String productName,
            @RequestParam(value = "price", defaultValue = "0") String priceStr) {

        HttpSession session = request.getSession();

        // 쇼핑카트 가져오기 (없으면 새로 생성)
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        // 상품 추가/수량 증가
        CartItem existingItem = cart.get(productId);
        if (existingItem != null) {
            existingItem.quantity++;
        } else {
            int price = 0;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                // 기본값 0 사용
            }
            cart.put(productId, new CartItem(productId, productName, price, 1));
        }

        ModelAndView mv = new ModelAndView("session/cart-updated");
        mv.addAttribute("message", "상품이 장바구니에 추가되었습니다: " + productName);
        mv.addAttribute("cartSize", cart.size());
        mv.addAttribute("totalAmount", calculateTotalAmount(cart));

        return mv;
    }

    /**
     * 쇼핑카트 조회
     */
    @RequestMapping(value = "/session/cart", method = "GET")
    public ModelAndView viewCart(HttpRequest request) {
        HttpSession session = request.getSession(false);
        ModelAndView mv = new ModelAndView("session/cart");

        if (session != null) {
            @SuppressWarnings("unchecked")
            Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");

            if (cart != null && !cart.isEmpty()) {
                mv.addAttribute("cart", cart.values());
                mv.addAttribute("totalAmount", calculateTotalAmount(cart));
                mv.addAttribute("itemCount", cart.size());
            } else {
                mv.addAttribute("message", "장바구니가 비어있습니다.");
            }
        } else {
            mv.addAttribute("error", "세션이 없습니다.");
        }

        return mv;
    }

    /**
     * 쇼핑카트 비우기
     */
    @RequestMapping(value = "/session/cart/clear", method = "POST")
    public ModelAndView clearCart(HttpRequest request) {
        HttpSession session = request.getSession(false);
        ModelAndView mv = new ModelAndView("session/cart-cleared");

        if (session != null) {
            session.removeAttribute("cart");
            mv.addAttribute("message", "장바구니가 비워졌습니다.");
        } else {
            mv.addAttribute("error", "세션이 없습니다.");
        }

        return mv;
    }

    /**
     * 세션 설정 변경 (타임아웃 변경 등)
     */
    @RequestMapping(value = "/session/config", method = "POST")
    public ModelAndView configureSession(
            HttpRequest request,
            @RequestParam(value = "maxInactiveInterval", defaultValue = "1800") String intervalStr) {

        HttpSession session = request.getSession();

        try {
            int interval = Integer.parseInt(intervalStr);
            session.setMaxInactiveInterval(interval);

            ModelAndView mv = new ModelAndView("session/config-updated");
            mv.addAttribute("message", "세션 타임아웃이 변경되었습니다: " + interval + "초");
            mv.addAttribute("sessionId", session.getId());
            mv.addAttribute("newTimeout", interval);

            return mv;
        } catch (NumberFormatException e) {
            ModelAndView mv = new ModelAndView("session/error");
            mv.addAttribute("error", "올바르지 않은 숫자 형식입니다: " + intervalStr);
            return mv;
        }
    }

    // === 유틸리티 메서드들 ===

    /**
     * 간단한 사용자 검증
     */
    private boolean isValidUser(String username, String password) {
        // 실제로는 데이터베이스에서 검증해야 함
        Map<String, String> users = Map.of(
                "admin", "admin123",
                "user", "user123",
                "guest", "guest",
                "winter", "framework"
        );

        return users.containsKey(username) && users.get(username).equals(password);
    }

    /**
     * 사용자 역할 조회
     */
    private String getUserRole(String username) {
        switch (username) {
            case "admin": return "ADMIN";
            case "user": return "USER";
            case "winter": return "DEVELOPER";
            default: return "GUEST";
        }
    }

    /**
     * 장바구니 총 금액 계산
     */
    private int calculateTotalAmount(Map<String, CartItem> cart) {
        return cart.values().stream()
                .mapToInt(item -> item.price * item.quantity)
                .sum();
    }

    /**
     * 장바구니 아이템 클래스
     */
    public static class CartItem {
        public String productId;
        public String productName;
        public int price;
        public int quantity;

        public CartItem(String productId, String productName, int price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return String.format("CartItem{id='%s', name='%s', price=%d, quantity=%d}",
                    productId, productName, price, quantity);
        }
    }
}