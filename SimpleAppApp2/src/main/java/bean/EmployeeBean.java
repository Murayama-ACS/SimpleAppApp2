package bean;

import java.io.Serializable;

/**
 * 画面表示用の社員情報と部署名を保持するオブジェクト
 */
public class EmployeeBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String empName; // 社員名
    private String dptName; // 部署名

    public EmployeeBean() {}

    public EmployeeBean(String empName, String dptName) {
        this.empName = empName;
        this.dptName = dptName;
    }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getDptName() { return dptName; }
    public void setDptName(String dptName) { this.dptName = dptName; }
}