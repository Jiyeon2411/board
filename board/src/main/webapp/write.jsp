<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>Insert title here</title>
<link rel="stylesheet" href="./css/style.css" />
</head>
<body>
 	<div class="board_wrap">
      <div class="board_title">
        <strong>자유게시판</strong>
        <p>자유게시판 입니다.</p>
      </div>
      <div class="board_write_wrap">
       <form name="frm" method="post" action="insert" enctype="multipart/form-data">
        <div class="board_write">
          <div class="title">
            <dl>
              <dt>제목</dt>
              <dd><input type="text" name="title" maxlength="50" placeholder="제목 입력" /></dd>
            </dl>
          </div>
          <div class="info">
            <dl>
              <dt>글쓴이</dt>
              <dd><input type="text" name="user_id" maxlength="10" placeholder="글쓴이 입력" /></dd>
            </dl>
          </div>
          <div class="cont">
            <textarea name="content" placeholder="내용 입력"></textarea>
          </div>
          <div style="padding-top:10px">
              <label style="font-size:1.4rem; padding-right: 20px;" for="file">이미지 업로드</label>
              <input type="file" name="file" id="file"/>
          </div>
        </div>
       </form>
        <div class="bt_wrap">
          <a onclick="chkForm(); return false;" class="on">등록</a>
          <a href="list">취소</a>
        </div>
      </div>
    </div>
    <script type="text/javascript" src="./script.js"></script>
</body>
</html>