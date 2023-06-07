package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DAO.BoardDAO;
import DTO.Board;

@WebServlet("/")
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
		}
		
		ctx.getRequestDispatcher("/" + site).forward(request, response);
		
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

}
