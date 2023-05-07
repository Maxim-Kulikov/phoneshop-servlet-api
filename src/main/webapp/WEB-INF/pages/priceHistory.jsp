<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product">
    <h1>Price history</h1>
    <h2>${product.description}</h2>
    <table>
        <tr>
            <th>Start Date</th>
            <th>Price</th>
        </tr>
        <tr>
            <c:forEach var="price" items="${product.priceInfoList}">
        <tr>
            <th>${price.date}</th>
            <th class="price">
                <fmt:formatNumber value="${price.price}" type="currency"
                                  currencySymbol="${product.currency.symbol}"/>
            </th>
        </tr>
        </c:forEach>
        </tr>
    </table>
    <p>(c) Expert-Soft</p>
</tags:master>