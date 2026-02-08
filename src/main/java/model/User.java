package model;

public class User {

    // Login Information
    private String hospitalId;
    private String password;

    // Shared Information
    private String gender;
	private int age;
    private String role;

    // Getter and Setters
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
