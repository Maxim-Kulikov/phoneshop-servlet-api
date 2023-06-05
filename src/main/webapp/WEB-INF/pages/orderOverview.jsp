<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="quantity">Quantity</td>
            <td class="price">Price</td>
        </tr>
        </thead>
        <c:forEach var="item" items="${order.items}" varStatus="status">
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
                        ${item.quantity}
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/price-history?id=${item.product.id}">
                            <fmt:formatNumber value="${item.product.price}"
                                              type="currency"
                                              currencySymbol="${item.product.currency.symbol}"
                            />
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td></td>
            <td>Subtotal cost</td>
            <td><fmt:formatNumber value="${order.subtotal}"
                                  type="currency"
                                  currencySymbol="USD"/>
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>Delivery cost</td>
            <td><fmt:formatNumber value="${order.deliveryCost}"
                                  type="currency"
                                  currencySymbol="USD"/></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>Total cost</td>
            <td><fmt:formatNumber value="${order.totalCost}"
                                  type="currency"
                                  currencySymbol="USD"/></td>
        </tr>
    </table>
    <p></p>
    <h2>Your details</h2>
    <table>
        <tags:orderOverviewRaw name="firstName" order="${order}" label="First name"/>
        <tags:orderOverviewRaw name="lastName" order="${order}" label="Last name"/>
        <tags:orderOverviewRaw name="phone" order="${order}" label="Phone"/>
        <tags:orderOverviewRaw name="deliveryDate" order="${order}" label="Delivery date"/>
        <tags:orderOverviewRaw name="deliveryAddress" order="${order}" label="Delivery address"/>
        <tags:orderOverviewRaw name="paymentMethod" order="${order}" label="Payment method"/>
    </table>
</tags:master>