<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="session"/>
<tags:master pageTitle="Cart">
    <form method="post" action="${pageContext.servletContext.contextPath}/cart">
        <p>
            Welcome to Expert-Soft training!
        </p>
        <c:if test="${not empty errors}">
            <div class="error">There were some errors updating cars</div>
        </c:if>
        <c:if test="${not empty param.message and empty errors}">
            <div class="success">
                    ${param.message}
            </div>
        </c:if>
        <p>Total quantity: ${cart.totalQuantity}</p>
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="quantity">Quantity</td>
                <td class="price">Price</td>
                <td></td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}" varStatus="status">
                <tr>
                    <td>
                        <img class="product-tile" src="${item.product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                                ${item.product.description}
                    </td>
                    <td class="quantity">
                        <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                        <c:set var="error" value="${errors[item.product.id]}"/>
                        <input name="quantity"
                               value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}"
                               class="quantity"/>
                        <c:if test="${not empty error}">
                            <div class="error">
                                    ${errors[item.product.id]}
                            </div>
                        </c:if>
                        <input type="hidden" name="productId" value="${item.product.id}"/>
                    </td>
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/products/price-history?id=${item.product.id}">
                                <fmt:formatNumber value="${item.product.price}"
                                                  type="currency"
                                                  currencySymbol="${item.product.currency.symbol}"
                                />
                    </td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td>Total cost</td>
                <td>${cart.totalCost}</td>
            </tr>
        </table>
        <p></p>
        <button>Update</button>
    </form>
    <form action="${pageContext.servletContext.contextPath}/checkout" method="get">
        <button>Checkout</button>
    </form>
    <form id="deleteCartItem" method="post"></form>
</tags:master>