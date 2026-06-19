package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // 【追加】
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // 【追加】

import bean.EmployeeBean;
import dao.EmployeeDAO; // 【追加】

/**
 * Servlet implementation class InsertUser
 */
@WebServlet("/EmployeeAdd")
/* ==========================================================================
 * 【追加】multipart/form-data（ファイルアップロード）を受け付けるためのアノテーション
 * ========================================================================== */
@MultipartConfig
public class EmployeeAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			dao.DepartmentDAO deptDao = new dao.DepartmentDAO();
			request.setAttribute("dptList", deptDao.findAll());
			
			dao.PositionDAO posDao = new dao.PositionDAO();
			request.setAttribute("posList", posDao.findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String url = "WEB-INF/jsp/user_signup.jsp";
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. ログインチェック
		String loginUrl = "/index.jsp";
		HttpSession session = request.getSession();
		EmployeeBean employee = (EmployeeBean) session.getAttribute("empBean"); 

		if (employee == null) {
			response.sendRedirect(request.getContextPath() + loginUrl);
			return;
		}

		// 2. 権限チェック
		String ldptId = employee.getDpt_id();
		if (!ldptId.matches("^D4.*$")) {
			request.setAttribute("eMsg", "アクセス権限がありません。");
			request.getRequestDispatcher(loginUrl).forward(request, response);
			return;
		}
		String url = "WEB-INF/jsp/user_signup.jsp";

		/* ==========================================================================
		 * 【追加】リクエストが「画面入力（manual）」か「CSV」かを判別するロジック
		 * ========================================================================== */
		String mode = request.getParameter("mode");
		
		// multipart/form-dataのときは通常の方法でパラメータが取れない場合があるためPartから補正
		if (mode == null && request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
			Part modePart = request.getPart("mode");
			if (modePart != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(modePart.getInputStream(), StandardCharsets.UTF_8))) {
					mode = reader.readLine();
				}
			}
		}

		// 分岐処理の開始
		if ("csv".equals(mode)) {
			/* ==========================================================================
			 * 【追加】CSV一括登録処理のロジック一式
			 * ========================================================================== */
			Part filePart = request.getPart("csvFile");
			if (filePart == null || filePart.getSize() == 0) {
				request.setAttribute("eMsg", "ファイルが選択されていないか、空のファイルです。");
			} else {
				List<String> errorList = new ArrayList<>();
				int successCount = 0;
				int failureCount = 0;
				EmployeeDAO empDAO = new EmployeeDAO();

				try (BufferedReader br = new BufferedReader(new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
					String line;
					int lineNumber = 0;

					while ((line = br.readLine()) != null) {
						lineNumber++;
						// ヘッダー行のスキップ判定
						if (lineNumber == 1 && line.contains("社員ID")) {
							continue; 
						}
						if (line.trim().isEmpty()) {
							continue;
						}

						// カンマ分割
						String[] data = line.split(",", -1);
						if (data.length < 6) {
							errorList.add(lineNumber + "行目: 列数が足りません。必須6項目を入力してください。");
							failureCount++;
							continue;
						}

						String empId = data[0].trim();
						String empName = data[1].trim();
						String emp_furigana = data[2].trim();
						String email = data[3].trim();
						String dptId = data[4].trim();
						String posId = data[5].trim();

						// バリデーション
						if (empId.isEmpty() || empName.isEmpty() || emp_furigana.isEmpty() || email.isEmpty() || dptId.isEmpty() || posId.isEmpty()) {
							errorList.add(lineNumber + "行目: 未入力の項目があります。");
							failureCount++;
							continue;
						}

						// インサート実行
						EmployeeBean insertEmpBean = new EmployeeBean(empId, empName, emp_furigana, email, dptId, posId);
						int result = empDAO.insertEmployee(insertEmpBean);

						if (result == 1) {
							successCount++;
						} else if (result == -1) {
							errorList.add(lineNumber + "行目: 社員ID「" + empId + "」またはメールアドレスが重複しています。");
							failureCount++;
						} else {
							errorList.add(lineNumber + "行目: 登録に失敗しました。");
							failureCount++;
						}
					}
					
					// 処理結果のセット
					if (failureCount == 0) {
						request.setAttribute("sMsg", successCount + "件のCSV登録が完了しました。");
					} else {
						request.setAttribute("sMsg", successCount + "件の登録が完了しました。");
						request.setAttribute("eMsg", failureCount + "件の処理でエラーが発生しました。");
						request.setAttribute("errorList", errorList);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("eMsg", "CSVの解析中に予期せぬエラーが発生しました。");
				}
			}
			/* ========================================================================== */

		} else {
			/* ==========================================================================
			 * 【変更】既存の個別入力処理（インデントのみ下げてelse句の中に閉じ込めました）
			 * ========================================================================== */
			String emp_id = request.getParameter("emp_id");
			String emp_name = request.getParameter("emp_name");
			String emp_furigana = request.getParameter("emp_furigana");
			String email = request.getParameter("email");
			String dpt_id = request.getParameter("dpt_id");
			String pos_id = request.getParameter("pos_id");

			System.out.println("emp_id:" + emp_id);
			System.out.println("emp_name:" + emp_name);
			System.out.println("emp_furigana:" + emp_furigana);
			System.out.println("email:" + email);
			System.out.println("dpt_id:" + dpt_id);
			System.out.println("pos_id:" + pos_id);

			// ... (约162行)
			if(emp_id.isEmpty() || emp_name.isEmpty() || emp_furigana.isEmpty() || email.isEmpty() || dpt_id == null || pos_id == null) {
			    request.setAttribute("eMsg", "社員ID、名前、ふりがな、Email、部署、役職のいずれかが入力されていません。");
			}else {
			    EmployeeBean insertEmpBean = new EmployeeBean(emp_id, emp_name, emp_furigana, email, dpt_id, pos_id);
			    session.setAttribute("insertEmpBean", insertEmpBean);

			    /* ==========================================================================
			     * 【追加】确认画面で名前を表示するために、DAOからリストを取得してrequestにセットする
			     * ========================================================================== */
			    try {
			        dao.DepartmentDAO deptDao = new dao.DepartmentDAO();
			        request.setAttribute("dptList", deptDao.findAll());
			        
			        dao.PositionDAO posDao = new dao.PositionDAO();
			        request.setAttribute("posList", posDao.findAll());
			    } catch (Exception e) {
			        // 万が一DB取得に失敗した場合の処理
			        e.printStackTrace();
			        request.setAttribute("eMsg", "部署・役職データの取得に失敗しました。");
			        // 取得失敗してもIDはBeanにあるので、urlは変えずそのまま進めます。
			        // JSP側でListがない場合は空欄になります。
			    }
			    /* ========================================================================== */

			    url = "WEB-INF/jsp/user_confirm.jsp";
			}
		}
		RequestDispatcher rd = request.getRequestDispatcher(url);
		rd.forward(request, response);
	}
}
	