package controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.beanutils.BeanUtils;

import DAO.BoardDAO;
import DTO.Board;

@WebServlet("/")
@MultipartConfig(maxFileSize=1024*1024*2, location="c:/Temp/img")
public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private BoardDAO dao; //model
	private ServletContext ctx; //페이지이동, forward를 위해 사용한다.
	
       
    public BoardController() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = new BoardDAO();
		ctx = getServletContext(); //ServletContext: 웹 어플리케이션 자원관리
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //한글깨짐 방지
		String command = request.getServletPath(); //경로를 가지고 온다.
		String site = null;
		
		System.out.println("command: " + command);
		
		//1.경로를 정해준다(라우팅) 
		switch (command) {
			case "/list": site = getList(request); break;
			case "/view": site = getView(request); break;
			case "/write": site = "write.jsp"; break; //글쓰기 화면을 보여줌
			case "/insert": site = insertBoard(request); break; //글등록
			case "/edit": site = getViewForEdit(request); break; //수정 화면 보여줌
			case "/update": site = updateBoard(request); break; //글수정
			case "/delete": site = deleteBoard(request); break; //글삭제
		}
		
		/*
		 -둘다 새로운 페이지로 이동
		 redirect: 데이터(response, request 객체)를 가지고 이동하지ㄴㄴ, 주소가변함
		 *DB에 변화가 생기는 요청(글쓰기, 회원가입...)
		 *insert, update, delete...
		 *
		 forward: 데이터(response, request 객체)를 가지고 이동, 주소가 변하지않흠
		 *단순조회(상세페이지 보기, 리스트보기, 검색..)
		 *select...
		 */
		
		if(site.startsWith("redirect:/")) { //redirect
			
			//redirect 경로만 잘라온다.
			String rview = site.substring("redirect:/".length());
			System.out.println("rview:" + rview); //list
			
			response.sendRedirect(rview); //페이지 이동
		} else { //forward
		ctx.getRequestDispatcher("/" + site).forward(request, response);
		}
	}
	
	public String getList(HttpServletRequest request) {
		ArrayList<Board> list;
		
		try {
			list = dao.getList();
			request.setAttribute("boardList", list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "index.jsp";
	}
	
	public String getView(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		
		try {
			dao.updateViews(board_no); //조회수 증가
			Board b = dao.getView(board_no);
			request.setAttribute("board", b);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return "view.jsp";
	}
	
	public String insertBoard(HttpServletRequest request) {
		Board b = new Board();
		
		try {
			BeanUtils.populate(b, request.getParameterMap());
			//1. 이미지 파일 자체를 서버 컴퓨터에 저장
			Part part = request.getPart("file"); //이미지파일 받기
			String fileName = getFilename(part); //파일 이름 구하기
			
			if(fileName != null && !fileName.isEmpty()) {
				part.write(fileName); //파일을 컴퓨터에 저장한다
			}
			
			//2. 이미지 파일 이름에 "/img/ 경로를 붙여서 board 객체에 저장"
			b.setImg("/img/" + fileName);
			
			dao.insertBoard(b);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "redirect:/list";
	}
	
	public String getViewForEdit(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		try {
			Board b = dao.getViewForEdit(board_no);
			request.setAttribute("board", b);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "edit.jsp";
	}
	
	public String updateBoard(HttpServletRequest request) {
		Board b = new Board();
		try {
			BeanUtils.populate(b, request.getParameterMap());
			
			//1. 이미지 파일 자체를 서버 컴퓨터에 저장
			Part part = request.getPart("file"); //이미지파일 받기
			String fileName = getFilename(part); //파일 이름 구하기
			
			if(fileName != null && !fileName.isEmpty()) {
				part.write(fileName); //파일을 컴퓨터에 저장한다
			}
			
			//2. 이미지 파일 이름에 "/img/ 경로를 붙여서 board 객체에 저장"
			b.setImg("/img/" + fileName);
			
			dao.updateBoard(b);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "redirect:/view?board_no=" + b.getBoard_no();
	}
	
	public String deleteBoard(HttpServletRequest request) {
		int board_no = Integer.parseInt(request.getParameter("board_no"));
		
		try {
			dao.deleteBoard(board_no);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/list";
	}
	
	//파일 이름 추출
	private String getFilename(Part part) {
		String fileName = null;
		String header = part.getHeader("content-disposition");
		System.out.println("header => " + header);
		
		int start = header.indexOf("filename=");
		fileName = header.substring(start + 10, header.length() - 1);
		System.out.println("파일명: " + fileName);
		
		return fileName;
	}

}
