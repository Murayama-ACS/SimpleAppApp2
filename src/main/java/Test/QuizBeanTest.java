package Test;

import bean.QuizBean;

public class QuizBeanTest {

	public static void main(String[] args) {
		QuizBean quizBean = new QuizBean();
		System.out.println(quizBean);

		// 期待値
		String empIdA = "E001";
		String quizA = "母の旧姓は？";
		String answerA = "ヤマダ";

		// 1) no-arg コンストラクタ + setter を使う場合
		QuizBean q0 = new QuizBean();

		// setter が存在する想定。メソッド名が異なれば読み替えてください。
		try {
			q0.setEmp_id(empIdA);
			q0.setQuiz(quizA);
			q0.setAnswer(answerA);
		} catch (NoSuchMethodError | Exception e) {
			// setter が無い場合は同じパッケージにテストクラスを置いて直接フィールドへ代入できます：
			// q0.emp_id = empIdA; q0.quiz = quizA; q0.answer = answerA;
		}

		System.out.println("=== q0 (no-arg + setters) ===");
		// getter が存在する想定。メソッド名が異なれば読み替えてください。
		try {
			System.out.println("emp_id expected: " + empIdA + "  actual: " + q0.getEmp_id());
			System.out.println("quiz expected:   " + quizA   + "  actual: " + q0.getQuiz());
			System.out.println("answer expected: " + answerA + "  actual: " + q0.getAnswer());
		} catch (NoSuchMethodError | Exception e) {
			// getter が無い場合は同じパッケージにテストクラスを置いて直接フィールドを参照できます：
			// System.out.println("emp_id expected: " + empIdA + "  actual: " + q0.emp_id);
			// System.out.println("quiz expected:   " + quizA   + "  actual: " + q0.quiz);
			// System.out.println("answer expected: " + answerA + "  actual: " + q0.answer);
		}

		// 2) コンストラクタ (emp_id, quiz, answer) を使う場合
		QuizBean q1 = new QuizBean(empIdA, quizA, answerA);

		System.out.println("\n=== q1 (3-arg constructor) ===");
		try {
			System.out.println("emp_id expected: " + empIdA + "  actual: " + q1.getEmp_id());
			System.out.println("quiz expected:   " + quizA   + "  actual: " + q1.getQuiz());
			System.out.println("answer expected: " + answerA + "  actual: " + q1.getAnswer());
		} catch (NoSuchMethodError | Exception e) {
			// getter が無い場合（同じパッケージなら直接参照）
			// System.out.println("emp_id expected: " + empIdA + "  actual: " + q1.emp_id);
			// System.out.println("quiz expected:   " + quizA   + "  actual: " + q1.quiz);
			// System.out.println("answer expected: " + answerA + "  actual: " + q1.answer);
		}
	}

}
