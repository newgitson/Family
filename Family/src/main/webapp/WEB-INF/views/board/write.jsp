<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<jsp:useBean id="now" class="java.util.Date" scope="page" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<!-- head -->
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>


<!-- body -->
<body>

<a href="javascript:history.back()">뒤로가기</a>
글쓰기

<!-- form -->
<form id="writeForm" action="${pageContext.request.contextPath}/board/writePro" method="post"  enctype="multipart/form-data">
제목				<input type="text" name="title" required><br>
내용				<input type="text" name="content" required><br>
<input multiple="multiple" type="file" name="file" />
<input type="hidden" name="nickname" value="${sessionScope.nickname}">
<input type="submit" value="작성">
</form>

<!--  <form name="fileForm" action="requestupload2" method="post" enctype="multipart/form-data"> -->
<!--         <input multiple="multiple" type="file" name="file" /> -->
<!--         <input type="text" name="src" /> -->
<!--         <input type="submit" value="전송" /> -->
<!--     </form> -->

</body>
</html>