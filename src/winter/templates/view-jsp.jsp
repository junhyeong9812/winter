<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= pageTitle %></title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff8e7;
        }
        .header {
            background: linear-gradient(135deg, #fd79a8 0%, #fdcb6e 100%);
            color: white;
            padding: 30px;
            border-radius: 12px;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 6px 20px rgba(253, 121, 168, 0.3);
        }
        .content {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }
        .jsp-info {
            background: linear-gradient(45deg, #6c5ce7, #a29bfe);
            color: white;
            padding: 25px;
            border-radius: 10px;
            margin: 20px 0;
        }
        .server-info {
            background: #e17055;
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }
        .admin-card {
            background: #00b894;
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }
        .calculation {
            background: #0984e3;
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            text-align: center;
        }
        .scriptlet-demo {
            background: #2d3436;
            color: #ddd;
            padding: 20px;
            border-radius: 8px;
            font-family: 'Courier New', monospace;
            margin: 20px 0;
        }
        .highlight {
            background: #fdcb6e;
            color: #2d3436;
            padding: 10px;
            border-radius: 5px;
            display: inline-block;
            margin: 5px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1><%= pageTitle %> ☕</h1>
        <p>MockJSP 엔진을 사용한 템플릿 렌더링</p>
        <p><%= welcomeMessage %></p>
    </div>

    <div class="content">
        <!-- JSP 표현식 테스트 -->
        <div class="jsp-info">
            <h2>📋 JSP 기본 정보</h2>
            <p><strong>현재 시간:</strong> <%= new java.util.Date() %></p>
            <p><strong>요청 메서드:</strong> <%= request.getMethod() %></p>
            <p><strong>서버 정보:</strong> <%= serverInfo %></p>
            <p><strong>컨텍스트 경로:</strong> <%= contextPath %></p>
        </div>

        <!-- 관리자 정보 -->
        <div class="admin-card">
            <h3>👨‍💼 관리자 정보</h3>
            <p><strong>이름:</strong> <%= admin.name %></p>
            <p><strong>나이:</strong> <%= admin.age %>세</p>
            <p><strong>이메일:</strong> <%= admin.email %></p>
            <p><strong>주소:</strong> <%= admin.address.city %> (<%= admin.address.zipcode %>)</p>
        </div>

        <!-- 스크립틀릿을 사용한 조건부 렌더링 -->
        <% if (admin.name != null) { %>
        <div class="highlight">
            ✅ 관리자가 로그인되어 있습니다: <%= admin.name %>
        </div>
        <% } %>

        <!-- 계산 예제 -->
        <div class="calculation">
            <h3>💰 주문 계산 예제</h3>
            <p>상품 개수: <%= itemCount %>개</p>
            <p>개당 가격: <%= pricePerItem %>원</p>
            <p><strong>총 금액: <%= itemCount * pricePerItem %>원</strong></p>

            <%-- JSP 주석: 할인 계산 --%>
            <%
                int totalPrice = itemCount * pricePerItem;
                int discount = totalPrice > 10000 ? 1000 : 0;
                int finalPrice = totalPrice - discount;
            %>
            <% if (discount > 0) { %>
            <p style="color: #00b894;">🎉 할인 적용: -<%= discount %>원</p>
            <% } %>
            <p><strong>최종 금액: <%= finalPrice %>원</strong></p>
        </div>

        <!-- JSP 스크립틀릿 데모 -->
        <div class="scriptlet-demo">
            <h4>⚙️ JSP 스크립틀릿 데모</h4>
            <p>1부터 5까지의 숫자:</p>
            <% for (int i = 1; i <= 5; i++) { %>
            <span class="highlight">숫자 <%= i %></span>
            <% } %>

            <p style="margin-top: 15px;">조건부 메시지:</p>
            <%
                String message;
                if (itemCount > 10) {
                    message = "대량 주문입니다!";
                } else {
                    message = "일반 주문입니다.";
                }
            %>
            <div style="background: #fdcb6e; color: #2d3436; padding: 10px; border-radius: 5px; margin-top: 10px;">
                <%= message %>
            </div>
        </div>

        <!-- JSP 구문 설명 -->
        <div style="background: #74b9ff; color: white; padding: 20px; border-radius: 10px; margin: 20px 0;">
            <h4>☕ JSP 구문 예제</h4>
            <ul>
                <li><strong>표현식:</strong> &lt;%= expression %&gt; - 값을 출력</li>
                <li><strong>스크립틀릿:</strong> &lt;% code %&gt; - Java 코드 실행</li>
                <li><strong>선언:</strong> &lt;%! declaration %&gt; - 메서드/변수 선언</li>
                <li><strong>지시자:</strong> &lt;%@ directive %&gt; - 페이지 설정</li>
                <li><strong>주석:</strong> &lt;%-- comment --%&gt; - JSP 주석</li>
            </ul>
        </div>

        <div style="background: #00b894; color: white; padding: 20px; border-radius: 10px; margin: 20px 0;">
            <h4>🔧 MockJSP 엔진 특징</h4>
            <ul>
                <li><strong>표현식 평가:</strong> &lt;%= %&gt; 구문 처리</li>
                <li><strong>스크립틀릿:</strong> &lt;% %&gt; Java 코드 시뮬레이션</li>
                <li><strong>내장 객체:</strong> request, response 등 접근</li>
                <li><strong>조건문/반복문:</strong> if, for 등 제어 구조</li>
                <li><strong>산술 연산:</strong> 기본적인 계산 처리</li>
                <li><strong>중첩 객체:</strong> admin.address.city 지원</li>
            </ul>
        </div>

        <p style="text-align: center; margin-top: 30px;">
            <a href="/view/simple" style="margin: 0 10px;">Simple 테스트</a> |
            <a href="/view/thymeleaf" style="margin: 0 10px;">Thymeleaf 테스트</a> |
            <a href="/view/mustache" style="margin: 0 10px;">Mustache 테스트</a>
        </p>
    </div>
</body>
</html>

