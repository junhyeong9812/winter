package winter.util;

import java.util.Map;

/**
 * 25단계 세션 테스트를 위한 간단한 HTML 템플릿 생성 유틸리티
 * 실제 템플릿 엔진 대신 사용하는 간단한 HTML 생성기
 */
public class SessionHtmlGenerator {

    /**
     * 세션 홈 페이지 HTML 생성
     */
    public static String generateSessionHome(Map<String, Object> model) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>Winter Session Test</title></head><body>");
        html.append("<h1>Winter Framework - 세션 관리 테스트</h1>");
        html.append("<div style='border: 1px solid #ccc; padding: 20px; margin: 10px;'>");
        html.append("<h2>세션 정보</h2>");
        html.append("<p><strong>세션 ID:</strong> ").append(model.get("sessionId")).append("</p>");
        html.append("<p><strong>새 세션:</strong> ").append(model.get("isNew")).append("</p>");
        html.append("<p><strong>생성 시간:</strong> ").append(model.get("creationTime")).append("</p>");
        html.append("<p><strong>마지막 접근:</strong> ").append(model.get("lastAccessedTime")).append("</p>");
        html.append("<p><strong>타임아웃:</strong> ").append(model.get("maxInactiveInterval")).append("초</p>");
        html.append("<p><strong>사용자:</strong> ").append(model.get("username")).append("</p>");
        html.append("<p><strong>방문 횟수:</strong> ").append(model.get("visitCount")).append("</p>");
        html.append("</div>");

        html.append("<div style='margin: 20px;'>");
        html.append("<h3>세션 테스트 메뉴</h3>");
        html.append("<ul>");
        html.append("<li><a href='/session/info'>세션 상세 정보</a></li>");
        html.append("<li><a href='/session/cart'>장바구니 보기</a></li>");
        html.append("<li>로그인: <form style='display:inline;' method='post' action='/session/login'>");
        html.append("사용자명: <input name='username' value='winter'> ");
        html.append("비밀번호: <input name='password' value='framework'> ");
        html.append("<input type='submit' value='로그인'></form></li>");
        html.append("<li>속성 설정: <form style='display:inline;' method='post' action='/session/set'>");
        html.append("키: <input name='key' value='testKey'> ");
        html.append("값: <input name='value' value='testValue'> ");
        html.append("<input type='submit' value='설정'></form></li>");
        html.append("</ul>");
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 세션 결과 페이지 HTML 생성
     */
    public static String generateSessionResult(Map<String, Object> model) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>세션 결과</title></head><body>");
        html.append("<h1>세션 작업 결과</h1>");

        if (model.containsKey("message")) {
            html.append("<div style='color: green; border: 1px solid green; padding: 10px;'>");
            html.append("<strong>성공:</strong> ").append(model.get("message"));
            html.append("</div>");
        }

        if (model.containsKey("error")) {
            html.append("<div style='color: red; border: 1px solid red; padding: 10px;'>");
            html.append("<strong>오류:</strong> ").append(model.get("error"));
            html.append("</div>");
        }

        if (model.containsKey("sessionId")) {
            html.append("<p>세션 ID: ").append(model.get("sessionId")).append("</p>");
        }

        if (model.containsKey("key") && model.containsKey("value")) {
            html.append("<p>조회된 속성: ").append(model.get("key")).append(" = ").append(model.get("value")).append("</p>");
        }

        html.append("<p><a href='/session'>세션 홈으로 돌아가기</a></p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 로그인 성공 페이지 HTML 생성
     */
    public static String generateLoginSuccess(Map<String, Object> model) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>로그인 성공</title></head><body>");
        html.append("<h1>로그인 성공!</h1>");
        html.append("<div style='color: green; border: 1px solid green; padding: 20px;'>");
        html.append("<h2>환영합니다, ").append(model.get("username")).append("님!</h2>");
        html.append("<p><strong>메시지:</strong> ").append(model.get("message")).append("</p>");
        html.append("<p><strong>세션 ID:</strong> ").append(model.get("sessionId")).append("</p>");
        html.append("<p><strong>역할:</strong> ").append(model.get("role")).append("</p>");
        html.append("</div>");

        html.append("<div style='margin: 20px;'>");
        html.append("<h3>사용 가능한 기능</h3>");
        html.append("<ul>");
        html.append("<li><a href='/session/cart'>장바구니 관리</a></li>");
        html.append("<li><a href='/session/info'>세션 정보 확인</a></li>");
        html.append("<li><form style='display:inline;' method='post' action='/session/logout'>");
        html.append("<input type='submit' value='로그아웃'></form></li>");
        html.append("</ul>");
        html.append("</div>");

        html.append("<p><a href='/session'>세션 홈으로 돌아가기</a></p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 장바구니 페이지 HTML 생성
     */
    public static String generateCart(Map<String, Object> model) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>장바구니</title></head><body>");
        html.append("<h1>쇼핑 장바구니</h1>");

        if (model.containsKey("cart")) {
            @SuppressWarnings("unchecked")
            java.util.Collection<Object> cartItems = (java.util.Collection<Object>) model.get("cart");

            html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            html.append("<tr><th>상품ID</th><th>상품명</th><th>가격</th><th>수량</th><th>소계</th></tr>");

            for (Object item : cartItems) {
                // CartItem의 속성들을 직접 접근 (toString 사용)
                String itemStr = item.toString();
                html.append("<tr><td colspan='5'>").append(itemStr).append("</td></tr>");
            }

            html.append("</table>");
            html.append("<p><strong>총 금액:</strong> ").append(model.get("totalAmount")).append("원</p>");
            html.append("<p><strong>상품 수:</strong> ").append(model.get("itemCount")).append("개</p>");

            html.append("<form method='post' action='/session/cart/clear'>");
            html.append("<input type='submit' value='장바구니 비우기' style='background-color: red; color: white;'>");
            html.append("</form>");
        } else if (model.containsKey("message")) {
            html.append("<p>").append(model.get("message")).append("</p>");
        } else if (model.containsKey("error")) {
            html.append("<p style='color: red;'>").append(model.get("error")).append("</p>");
        }

        html.append("<div style='margin: 20px;'>");
        html.append("<h3>상품 추가</h3>");
        html.append("<form method='post' action='/session/cart/add'>");
        html.append("상품ID: <input name='productId' value='prod001'><br>");
        html.append("상품명: <input name='productName' value='테스트 상품'><br>");
        html.append("가격: <input name='price' value='10000'><br>");
        html.append("<input type='submit' value='장바구니에 추가'>");
        html.append("</form>");
        html.append("</div>");

        html.append("<p><a href='/session'>세션 홈으로 돌아가기</a></p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 세션 상세 정보 페이지 HTML 생성
     */
    public static String generateSessionInfo(Map<String, Object> model) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>세션 상세 정보</title></head><body>");
        html.append("<h1>세션 상세 정보</h1>");

        if (model.containsKey("error")) {
            html.append("<p style='color: red;'>").append(model.get("error")).append("</p>");
        } else {
            html.append("<table border='1' style='border-collapse: collapse;'>");
            html.append("<tr><th>속성</th><th>값</th></tr>");
            html.append("<tr><td>세션 ID</td><td>").append(model.get("sessionId")).append("</td></tr>");
            html.append("<tr><td>새 세션</td><td>").append(model.get("isNew")).append("</td></tr>");
            html.append("<tr><td>생성 시간</td><td>").append(model.get("creationTime")).append("</td></tr>");
            html.append("<tr><td>마지막 접근</td><td>").append(model.get("lastAccessedTime")).append("</td></tr>");
            html.append("<tr><td>최대 비활성 시간</td><td>").append(model.get("maxInactiveInterval")).append("초</td></tr>");
            html.append("<tr><td>활성 시간</td><td>").append(model.get("activeTime")).append("초</td></tr>");
            html.append("<tr><td>비활성 시간</td><td>").append(model.get("inactiveTime")).append("초</td></tr>");
            html.append("</table>");

            if (model.containsKey("attributes")) {
                html.append("<h3>세션 속성</h3>");
                @SuppressWarnings("unchecked")
                Map<String, Object> attributes = (Map<String, Object>) model.get("attributes");

                if (attributes.isEmpty()) {
                    html.append("<p>저장된 속성이 없습니다.</p>");
                } else {
                    html.append("<table border='1' style='border-collapse: collapse;'>");
                    html.append("<tr><th>키</th><th>값</th></tr>");
                    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                        html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>");
                    }
                    html.append("</table>");
                }
            }
        }

        html.append("<div style='margin: 20px;'>");
        html.append("<h3>세션 설정 변경</h3>");
        html.append("<form method='post' action='/session/config'>");
        html.append("타임아웃 (초): <input name='maxInactiveInterval' value='3600'> ");
        html.append("<input type='submit' value='변경'>");
        html.append("</form>");
        html.append("</div>");

        html.append("<p><a href='/session'>세션 홈으로 돌아가기</a></p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * 일반 메시지 페이지 HTML 생성
     */
    public static String generateMessagePage(String title, String message, boolean isError) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>").append(title).append("</title></head><body>");
        html.append("<h1>").append(title).append("</h1>");

        String style = isError ? "color: red; border: 1px solid red;" : "color: green; border: 1px solid green;";
        html.append("<div style='").append(style).append(" padding: 10px;'>");
        html.append("<p>").append(message).append("</p>");
        html.append("</div>");

        html.append("<p><a href='/session'>세션 홈으로 돌아가기</a></p>");
        html.append("</body></html>");
        return html.toString();
    }
}