<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="viewedProducts" required="true" type="java.util.ArrayList" %>

<body>
<c:if test="${not empty viewedProducts}">
    <h3>Recently viewed</h3>
</c:if>
<table>
    <c:forEach var="viewedProduct" items="${viewedProducts}">
        <td>
            <img class="product-tile" src="${viewedProduct.imageUrl}">
            <p></p>
            <a href="${pageContext.servletContext.contextPath}/products/${viewedProduct.id}">
                    ${viewedProduct.description}
            </a>
            <p><fmt:formatNumber value="${viewedProduct.price}"
                                 type="currency"
                                 currencySymbol="${viewedProduct.currency.symbol}"/>
            </p>
        </td>
    </c:forEach>
</table>
</body>