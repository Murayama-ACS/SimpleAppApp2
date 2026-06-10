package bean;

public class QuizBean {
	public QuizBean(){}
	public QuizBean(String emp_id, String quiz, String answer){
		this.emp_id = emp_id;
		this.quiz = quiz;
		this.answer = answer;
	}
	String emp_id;
	String quiz;
	String answer;
	
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getQuiz() {
		return quiz;
	}
	public void setQuiz(String quiz) {
		this.quiz = quiz;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
}
