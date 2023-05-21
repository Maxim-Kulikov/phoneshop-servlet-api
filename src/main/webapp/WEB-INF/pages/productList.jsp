<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>
    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortLink sort="description" order="asc"/>
                <tags:sortLink sort="description" order="desc"/>
            </td>
            <td class="price">Price
                    <tags:sortLink sort="price" order="asc"/>
                    <tags:sortLink sort="price" order="desc"/>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/price-history?id=${product.id}">
                            <fmt:formatNumber value="${product.price}"
                                              type="currency"
                                              currencySymbol="${product.currency.symbol}"
                            />
                </td>
            </tr>
        </c:forEach>
    </table>
<%--    <c:if test="${not empty viewedProducts}">--%>
<%--        <h3>Recently viewed</h3>--%>
<%--    </c:if>--%>
<%--    <table >--%>
<%--        <c:forEach var="viewedProduct" items="${viewedProducts}">--%>
<%--            <td>--%>
<%--                <img class="product-tile" src="${viewedProduct.imageUrl}">--%>
<%--                <p></p>--%>
<%--                <a href="${pageContext.servletContext.contextPath}/products/${viewedProduct.id}">--%>
<%--                        ${viewedProduct.description}--%>
<%--                </a>--%>
<%--                    <p><fmt:formatNumber value="${viewedProduct.price}"--%>
<%--                                         type="currency"--%>
<%--                                         currencySymbol="${viewedProduct.currency.symbol}"></fmt:formatNumber>--%>
<%--                    </p>--%>
<%--            </td>--%>
<%--        </c:forEach>--%>
<%--    </table>--%>
    <tags:viewedProducts viewedProducts="${viewedProducts}"/>
</tags:master>