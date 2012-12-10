package com.teemtok.yamba;

public class LomoCredentials {

	private String Username;
	private String Password;
	private String Company;

	public LomoCredentials() {
		this.Username = null;
		this.Password = null;
		this.Company = null;
	}

	public LomoCredentials(String username, String password, String company) {
		this.Username = username;
		this.Password = password;
		this.Company = company;
	}

	public void setUsername(String Username) {
		this.Username = Username;
	}

	public String getUsername() {
		return Username;
	}

	public void setPassword(String Password) {
		this.Password = Password;
	}

	public String getPassword() {
		return Password;
	}

	public void setCpmpany(String Company) {
		this.Company = Company;
	}

	public String getCompany() {
		return Company;
	}

}
