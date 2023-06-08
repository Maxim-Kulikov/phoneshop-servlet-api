<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>
<%@ attribute name="descriptionSearchStrategies" required="false" type="java.util.List" %>

<tr>
    <td>${label}</td>
    <td>
        <c:set var="error" value="${errors[name]}"/>
        <input name="${name}" value="${param[name]}"/>
        <c:if test="${not empty error}">
            <div class="error">
                    ${error}
            </div>
        </c:if>
    </td>
    <c:if test="${name eq 'description'}">
        <td>
            <select name="descriptionSearchStrategy">
                <c:if test="${not empty param.descriptionSearchStrategy}">
                    <option>${param.descriptionSearchStrategy}</option>
                </c:if>
                <c:forEach var="method" items="${descriptionSearchStrategies}">
                    <c:if test="${param.descriptionSearchStrategy != method}">
                        <option>${method}</option>
                    </c:if>
                </c:forEach>
            </select>
        </td>
    </c:if>
</tr>