<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- JSTLタグライブラリの宣言（c:条件分岐やループなど） --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>新規社員登録 - AppApp システム</title>
    <%-- 共通ナビゲーションCSS、新規登録用CSS、およびポップアップ用ライブラリ（SweetAlert2）の読み込み --%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/user_signup.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/navbar.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> 
</head>
<body>
    <%-- 通知の未読カウントなどの機能を含む共通ヘッダーをインクルード --%>
    <%@ include file="/WEB-INF/jsp/header.jsp" %>

    <div class="container"> 
        <div class="form-container">
            
            <h2 style="margin-top: 0; margin-bottom: 30px; border-bottom: 2px solid #00796b; padding-bottom: 12px; color: #004d40;">
                新規社員登録
            </h2>
            
            <%-- メッセージ表示エリア --%>
            <%-- 登録成功メッセージ（Success Message） --%>
            <c:if test="${not empty sMsg}">
                <div style="background:#d4edda; color:#155724; padding:15px; border-radius:4px; margin-bottom:25px; border: 1px solid #c3e6cb; font-weight: bold;">
                    ✅ ${sMsg}
                </div>
            </c:if>
            
            <%-- 一般的なエラーメッセージ（Error Message） --%>
            <c:if test="${not empty eMsg}">
                <div style="background:#f8d7da; color:#721c24; padding:15px; border-radius:4px; margin-bottom:10px; border: 1px solid #f5c6cb; font-weight: bold;">
                    ⚠️ ${eMsg}
                </div>
            </c:if>
            
            <%-- CSV一括登録時の「部分エラー」リスト表示 --%>
            <%-- （例：100件中3件だけID重複などでエラーになった場合、その詳細行を表示する） --%>
            <c:if test="${not empty errorList}">
                <div class="error-list-container">
                    <strong>エラー詳細（以下の行は登録されていません）:</strong>
                    <ul>
                        <c:forEach var="err" items="${errorList}">
                            <li>${err}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <%--  パターンA：1件ずつの「手動」個別登録フォーム --%>
            <form action="${pageContext.request.contextPath}/EmployeeAdd" method="post" id="manualForm">
                <%-- サーブレット側で手動登録(manual)かCSVか(csv)を判別するための隠しフィールド --%>
                <input type="hidden" name="mode" value="manual">

                <div class="form-group">
                    <label for="emp_id">社員ID <span class="required-mark">※必須</span></label>
                    <input type="text" id="emp_id" name="emp_id" required placeholder="例：A00000001 (一意のID)">
                </div>
                
                <div class="form-group">
                    <label for="emp_name">氏名 <span class="required-mark">※必須</span></label>
                    <input type="text" id="emp_name" name="emp_name" required placeholder="例：山田 太郎">
                </div>
                
                <div class="form-group">
                    <label for="emp_furigana">ふりがな <span class="required-mark">※必須</span></label>
                    <input type="text" id="emp_furigana" name="emp_furigana" required placeholder="例：やまだ たろう">
                </div>
                
                <div class="form-group">
                    <label for="email">メールアドレス <span class="required-mark">※必須</span></label>
                    <input type="email" id="email" name="email" required placeholder="例：yamada@example.com">
                </div>
                
                <%-- 部署マスター(dptList)を展開してプルダウンを作成 --%>
                <div class="form-group">
                    <label for="dpt_id">所属部署 <span class="required-mark">※必須</span></label>
                    <select id="dpt_id" name="dpt_id" required>
                        <option value="" selected disabled>部署を選択してください</option>
                        <c:forEach var="dpt" items="${dptList}">
                            <option value="${dpt.dpt_id}">${dpt.dpt_name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <%-- 役職マスター(posList)を展開してプルダウンを作成 --%>
                <div class="form-group">
                    <label for="pos_id">役職 <span class="required-mark">※必須</span></label>
                    <select id="pos_id" name="pos_id" required>
                        <option value="" selected disabled>役職を選択してください</option>
                        <c:forEach var="pos" items="${posList}">
                            <option value="${pos.pos_id}">${pos.pos_name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <button type="submit" class="btn-update">確認画面へ進む</button>
            </form>

            <div class="section-divider">または</div>
           
            <%-- パターンB：CSVファイルによる「一括登録」フォーム --%>            
            <%-- ファイルを送信するためには必ず enctype="multipart/form-data" が必要です！ --%>
            <form action="${pageContext.request.contextPath}/EmployeeAdd" method="post" enctype="multipart/form-data" id="csvForm">
                <%-- サーブレット側で処理を分岐させるための隠しフィールド --%>
                <input type="hidden" name="mode" value="csv">
                
                <div class="csv-upload-box">
                    <h3 style="margin-top: 0; color: #004d40;">📁 CSVファイルで一括登録</h3>
                    <p style="font-size: 13px; color: #546e7a;">
                        フォーマット: 社員ID, 氏名, ふりがな, メールアドレス, 部署ID, 役職ID
                    </p>
                    <%-- .csvファイルのみを選択できるように制限 (accept=".csv") --%>
                    <input type="file" id="csvFile" name="csvFile" accept=".csv" required>
                    <br>
                    <button type="submit" class="btn-csv">⬆ CSVをアップロードして登録</button>
                </div>
            </form>
            
            <div class="back-link-container">
                <a href="${pageContext.request.contextPath}/EmployeeInfo" class="back-link">一覧に戻る</a>
            </div>
            
        </div> 
    </div>

    <%-- フロントエンドのJavaScript処理 --%>
    <script>
        // 【個別登録】の送信ボタンが押された時の処理
        document.getElementById('manualForm').addEventListener('submit', function(event) {
            // 個別登録の場合、Servlet側で入力内容を受け取って確認画面（user_confirm.jsp）に遷移する仕様。
            // 遷移中のユーザーの連打（二重送信）を防ぐため、ローディングアニメーションだけを表示してそのまま送信させる。
            Swal.fire({
                title: '確認画面へ移動中...',
                allowOutsideClick: false,
                didOpen: () => { Swal.showLoading(); }
            });
        });

        // CSVアップロードの送信ボタンが押された時の処理
        document.getElementById('csvForm').addEventListener('submit', function(event) {
            // 1. ブラウザ標準の送信を一旦ストップする
            event.preventDefault(); 
            
            // 2. 選択されたファイル名を取得
            const fileInput = document.getElementById('csvFile');
            if(fileInput.files.length === 0) return;
            const fileName = fileInput.files[0].name;

            // 3. SweetAlert2を使って最終確認ポップアップを表示
            Swal.fire({
                title: 'CSV一括登録を開始しますか？',
                text: "選択されたファイル: " + fileName,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#00796b',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'アップロード実行',
                cancelButtonText: 'キャンセル'
            }).then((result) => {
                // ユーザーが「実行」を押した場合
                if (result.isConfirmed) {
                    // 処理中（ローディング）のポップアップに切り替え、多重送信を防止
                    Swal.fire({
                        title: '処理中...',
                        text: 'データベースに登録しています',
                        allowOutsideClick: false,
                        didOpen: () => { Swal.showLoading(); }
                    });
                    // 実際にフォームを送信する
                    event.target.submit();
                }
            });
        });
    </script>
</body>
</html>