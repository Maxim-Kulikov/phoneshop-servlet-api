<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced Search">
    <h1>Advanced Search</h1>
    <form method="get" action="${pageContext.servletContext.contextPath}/advancedSearch">
        <table>
            <tags:advancedSearchRaw name="description" label="Description" errors="${errors}"/>
            <tags:advancedSearchRaw name="minPrice" label="Min price" errors="${errors}"/>
            <tags:advancedSearchRaw name="maxPrice" label="Max price" errors="${errors}"/>
            <td>
                <select name="descriptionSearchStrategy">
                    <c:if test="${not empty param.descriptionSearchStrategy}">
                        <option>${param.descriptionSearchStrategy}</option>
                    </c:if>
                    <c:set var="error" value="${errors['descriptionSearchStrategy']}"/>
                    <c:forEach var="method" items="${descriptionSearchStrategies}">
                        <c:if test="${param.descriptionSearchStrategy != method}">
                            <option>${method}</option>
                        </c:if>
                    </c:forEach>
                </select>
                <c:if test="${not empty error}">
                    <div class="error">
                            ${error}
                    </div>
                </c:if>
            </td>
        </table>
        <button>Search</button>
        <tags:foundProducts foundProducts="${foundProducts}"/>
    </form>
</tags:master>