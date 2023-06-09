package DAO;

import java.sql.*;
import java.util.ArrayList;

import DTO.Board;

public class BoardDAO {
	final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
	
	//db연결 메소드
	public Connection open() {
		Connection conn = null; //데이터베이스 연결 담당
		try {
			Class.forName(JDBC_DRIVER); //드라이버 로드
			conn = DriverManager.getConnection(JDBC_URL, "test", "test1234"); //DB연결
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return conn;
	}
	
	//게시판 리스트 가져오기
	public ArrayList<Board> getList() throws Exception {
		Connection conn = open();
		
		//Board객체를 저장할 ArrayList
		ArrayList<Board> boardList = new ArrayList<>();
		
		String sql = "SELECT BOARD_NO, TITLE, USER_ID, TO_CHAR (REG_DATE, 'YYYY.MM.DD') REG_DATE2, VIEWS FROM BOARD";
		PreparedStatement pstmt = conn.prepareStatement(sql); //쿼리문등록
		ResultSet rs = pstmt.executeQuery(); //쿼리문 실행
		
		try (conn; pstmt; rs) {
			while(rs.next()) { //1라인씩 데이터를 읽어온다.
				Board b = new Board();
				b.setBoard_no(rs.getInt("BOARD_NO"));
				b.setTitle(rs.getString("TITLE"));
				b.setUser_id(rs.getString("USER_ID"));
				b.setReg_date(rs.getString("REG_DATE"));
				b.setViews(rs.getInt("VIEWS"));
				
				boardList.add(b);
			}
			return boardList;
		}
		
		
	}
	
	//게시물 내용 가져오기
	public Board getView(int board_no) throws Exception {
		Connection conn = open();
		Board b = new Board();
		
		String sql = "SELECT BOARD_NO, TITLE, USER_ID, TO_CHAR (REG_DATE, 'YYYY.MM.DD') REG_DATE, VIEWS, CONTENT, IMG FROM BOARD WHERE BOARD_NO = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, board_no);
		ResultSet rs = pstmt.executeQuery();
		
		try(conn; pstmt; rs) {
			while(rs.next()) {
				b.setBoard_no(rs.getInt("BOARD_NO"));
				b.setTitle(rs.getString("TITLE"));
				b.setUser_id(rs.getString("USER_ID"));
				b.setReg_date(rs.getString("REG_DATE"));
				b.setViews(rs.getInt("VIEWS"));
				b.setContent(rs.getString("CONTENT"));
				b.setImg(rs.getString("IMG"));
			}
			
			return b;
		}
		
	 }
	
	//조회수 증가
	public void updateViews(int board_no) throws Exception {
		Connection conn = open();
		String sql = "UPDATE BOARD SET VIEWS = (VIEWS + 1) WHERE BOARD_NO = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt) {
			pstmt.setInt(1,  board_no);
			pstmt.executeUpdate();
		}
	 }
	
	//게시글 등록
	public void insertBoard(Board b) throws Exception {
		Connection conn = open();
		String sql = "INSERT INTO BOARD(BOARD_NO, USER_ID, TITLE, CONTENT, REG_DATE, VIEWS, IMG)" 
				+ " VALUES (BOARD_SEQ.NEXTVAL, ?, ?, ?, SYSDATE, 0, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try (conn; pstmt) {
			pstmt.setString(1, b.getUser_id());
			pstmt.setString(2, b.getTitle());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			pstmt.executeUpdate();
		}
	 }
	
	//게시글 수정화면 보여주기
	public Board getViewForEdit(int board_no) throws Exception {
		Connection conn = open();
		Board b = new Board();
		String sql = "SELECT BOARD_NO, TITLE, USER_ID, TO_CHAR (REG_DATE, 'YYYY.MM.DD') REG_DATE, VIEWS, CONTENT, IMG FROM BOARD WHERE BOARD_NO = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, board_no);
		ResultSet rs = pstmt.executeQuery();
		
		try(conn; pstmt; rs) {
			while(rs.next()) {
				b.setBoard_no(rs.getInt("BOARD_NO"));
				b.setTitle(rs.getString("TITLE"));
				b.setUser_id(rs.getString("USER_ID"));
				b.setReg_date(rs.getString("REG_DATE"));
				b.setViews(rs.getInt("VIEWS"));
				b.setContent(rs.getString("CONTENT"));
				b.setImg(rs.getString("IMG"));
			}
			
			return b;
		}
	}
	
	//게시글 수정하기
	public void updateBoard(Board b) throws Exception {
		Connection conn = open();
		String sql = "UPDATE BOARD SET TITLE = ?, USER_ID = ?, CONTENT = ?, IMG = ? " + "WHERE BOARD_NO = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt) {
			pstmt.setString(1, b.getTitle());
			pstmt.setString(2, b.getUser_id());
			pstmt.setString(3, b.getContent());
			pstmt.setString(4, b.getImg());
			pstmt.setInt(5, b.getBoard_no());
			
			//수정된 글이 없을 경우
			if(pstmt.executeUpdate() != 1) {
				throw new Exception("수정된 글이 없습니다.");
			}
		}
	}
	
	//게시글 삭제
	public void deleteBoard(int board_no) throws Exception {
		Connection conn = open();
		String sql = "DELETE FROM BOARD WHERE BOARD_NO = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		try(conn; pstmt) {
			pstmt.setInt(1, board_no);
			
			if (pstmt.executeUpdate() != 1) {
				throw new Exception("삭제된 글이 없습니다.");
			}
		}
	}
	
}
