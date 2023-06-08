<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="foundProducts" required="true" type="java.util.ArrayList" %>

<body>
<table>
    <c:if test="${not empty foundProducts}">
        <c:forEach var="product" items="${foundProducts}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>${product.description}</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}"
                                      type="currency"
                                      currencySymbol="${product.currency.symbol}"
                    />
                </td>
            </tr>
        </c:forEach>
    </c:if>
</table>
</body>