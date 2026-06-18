<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLコアタグの宣言（c:条件分岐やループなど） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>社員情報編集 - AppApp システム</title>
    <%-- 社員情報編集用の専用CSS、共通ナビゲーションCSS、およびポップアップ用ライブラリ（SweetAlert2）の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_update.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
    
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="form-container">
            
            <h2 style="margin-top: 0; border-bottom: 2px solid #343a40; padding-bottom: 10px; color: #343a40; margin-bottom: 30px;">
                社員情報編集
            </h2>
            
            <%-- エラーメッセージ表示領域 --%>
            <%-- 更新処理でバリデーションエラーやDBエラーが発生した場合に表示される --%>
            <c:if test="${not empty eMsg}">
                <div style="background:#f8d7da; color:#721c24; padding:15px; border-radius:4px; margin-bottom:25px; border: 1px solid #f5c6cb; font-weight: bold;">
                    ⚠️ ${eMsg}
                </div>
            </c:if>

            <%-- 社員情報更新フォーム --%>
            <form action="${pageContext.request.contextPath}/EmployeeUpdate" method="post" id="updateForm">
                <%-- サーブレット側で「更新処理の実行」であることを判定するための隠しパラメータ --%>
                <input type="hidden" name="action" value="update">

                <div class="form-group">
                    <%-- 社員IDはシステム上の主キー（一意の識別子）であるため、変更不可（readonly）として画面に表示のみ行う --%>
                    <label>社員ID <span style="font-size: 12px; color: #6c757d; font-weight: normal;">※変更不可</span></label>
                    <input type="text" value="${updateEmpBean.emp_id}" readonly>
                </div>
                
                <div class="form-group">
                    <label for="emp_name">氏名 <span class="required-mark">※必須</span></label>
                    <%-- value属性に現在の登録情報（updateEmpBean.emp_name）をセットして初期表示 --%>
                    <input type="text" id="emp_name" name="emp_name" value="${updateEmpBean.emp_name}" required placeholder="例：山田 太郎">
                </div>
                
                <div class="form-group">
                    <label for="emp_furigana">ふりがな <span class="required-mark">※必須</span></label>
                    <input type="text" id="emp_furigana" name="furigana" value="${updateEmpBean.furigana}" required placeholder="例：やまだ たろう">
                </div>
                
                <div class="form-group">
                    <label for="email">メールアドレス <span class="required-mark">※必須</span></label>
                    <input type="email" id="email" name="email" value="${updateEmpBean.email}" required placeholder="例：yamada@example.com">
                </div>
                
                <%-- 所属部署のプルダウン --%>
                <div class="form-group">
                    <label for="dpt_id">所属部署 <span class="required-mark">※必須</span></label>
                    <select id="dpt_id" name="dpt_id" required>
                        <option value="" disabled>部署を選択してください</option>
                        <%-- 部署マスターリスト（dptList）をループで展開 --%>
                        <c:forEach var="dpt" items="${dptList}">
                            <%-- 現在登録されている部署IDと一致する場合は 'selected' を付与して初期選択状態にする --%>
                            <option value="${dpt.dpt_id}" ${updateEmpBean.dpt_id == dpt.dpt_id ? 'selected' : ''}>
                                ${dpt.dpt_name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <%-- 役職のプルダウン --%>
                <div class="form-group">
                    <label for="pos_id">役職 <span class="required-mark">※必須</span></label>
                    <select id="pos_id" name="pos_id" required>
                        <option value="" disabled>役職を選択してください</option>
                        <%-- 役職マスターリスト（posList）をループで展開 --%>
                        <c:forEach var="pos" items="${posList}">
                            <%-- 現在登録されている役職IDと一致する場合は 'selected' を付与して初期選択状態にする --%>
                            <option value="${pos.pos_id}" ${updateEmpBean.pos_id == pos.pos_id ? 'selected' : ''}>
                                ${pos.pos_name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <%-- 送信ボタン（クリックすると下のJSイベントが発火する） --%>
                <button type="submit" class="btn-update">この内容で更新する</button>
            </form>
            
            <div class="back-link-container">
                <%-- 賢い戻るボタン：セッションに直前の社員一覧URL（検索条件維持用）があればそこに戻り、なければデフォルトの一覧へ戻る --%>
                <a href="${not empty sessionScope.lastListUrl ? sessionScope.lastListUrl : pageContext.request.contextPath += '/EmployeeInfo'}" class="back-link">キャンセルして一覧に戻る</a>
            </div>
            
        </div> 
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // フォームの送信（submit）イベントをフック
        document.getElementById('updateForm').addEventListener('submit', function(event) {
            // 1. ブラウザ標準の送信処理を一旦ストップさせる
            event.preventDefault(); 
            
            // 2. 確認ポップアップに表示するため、入力された氏名と選択された部署名を取得
            const empName = document.getElementById('emp_name').value;
            const dptSelect = document.getElementById('dpt_id');
            const dptName = dptSelect.options[dptSelect.selectedIndex].text;
            
            // 3. SweetAlert2を使ってリッチな確認ダイアログを表示
            Swal.fire({
                title: '社員情報を更新しますか？',
                // JSP内でJSのテンプレートリテラル(${})を使うため、先頭にバックスラッシュ(\)をつけてエスケープ処理
                html: `<strong>\${empName}</strong> さんの情報を<br><strong>\${dptName}</strong> 所属として更新します。`,
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#28a745', // 更新・安全を意味する緑色
                cancelButtonColor: '#6c757d',
                confirmButtonText: '更新する',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「更新する」をクリックした場合
                if (result.isConfirmed) {
                    // 4. 多重送信を防止するため、ローディングアニメーションを表示
                    Swal.fire({
                        title: '更新中...',
                        text: 'しばらくお待ちください',
                        allowOutsideClick: false,
                        didOpen: () => { Swal.showLoading(); }
                    });
                    
                    // 5. 実際のフォーム送信処理を実行
                    event.target.submit();
                }
            });
        });
    </script>
</body>
</html>