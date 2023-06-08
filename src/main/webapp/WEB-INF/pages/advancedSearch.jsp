<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced Search">
    <h1>Advanced Search</h1>
    <form method="post" action="${pageContext.servletContext.contextPath}/advancedSearch">
        <table>
            <tags:advancedSearchRaw name="description" label="Description" errors="${errors}" descriptionSearchStrategies="${descriptionSearchStrategies}"/>
            <tags:advancedSearchRaw name="minPrice" label="Min price" errors="${errors}"/>
            <tags:advancedSearchRaw name="maxPrice" label="Max price" errors="${errors}"/>
        </table>
        <p></p>
        <button>Search</button>
        <p></p>
        <tags:foundProducts foundProducts="${foundProducts}"/>
    </form>
</tags:master>