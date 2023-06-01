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
            <td>Quantity</td>
            <td class="price">Price
                    <tags:sortLink sort="price" order="asc"/>
                    <tags:sortLink sort="price" order="desc"/>
            <td></td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}" varStatus="status">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                </td>
                <td class="quantity">
                    <form id="addCartItem${product.id}" action="${pageContext.servletContext.contextPath}/products"
                          method="post">
                        <fmt:formatNumber value="1" var="quantity"/>
                        <input name="quantity"
                               value="${not empty error and (param.productId eq product.id)? param.quantity : 1}"
                               class="quantity"/>
                        <c:if test="${not empty error and (param.productId eq product.id)}">
                            <div class="error">
                                    ${error}
                            </div>
                        </c:if>
                        <c:if test="${not empty param.message and (param.productId eq product.id)}">
                            <div class="success">
                                    ${param.message}
                            </div>
                        </c:if>
                        <input name="productId" value="${product.id}" type="hidden"/>
                    </form>
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/price-history?id=${product.id}">
                            <fmt:formatNumber value="${product.price}"
                                              type="currency"
                                              currencySymbol="${product.currency.symbol}"
                            />
                </td>
                <td>
                    <button form="addCartItem${product.id}">
                        Add to cart
                    </button>
                </td>
            </tr>
        </c:forEach>
    </table>
    <form id="addCartItem" method="post"></form>
    <tags:viewedProducts viewedProducts="${viewedProducts}"/>
</tags:master>