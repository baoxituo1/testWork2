package com.trade.bluehole.trad.entity;




public class User  implements
		java.io.Serializable {

	// Fields

	private Integer id;
	private String userCode;
	private String account;
	private String password;
	private String email;
	private String mobile;
	private Integer userType;
	private String nickName;
	private String shopCode;

	// Constructors

	/** default constructor */
	public User() {
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /** full constructor */
	public User(String userCode, String account, String password, String email,
			String mobile, Integer userType, String nickName) {
		this.userCode = userCode;
		this.account = account;
		this.password = password;
		this.email = email;
		this.mobile = mobile;
		this.userType = userType;

		this.nickName = nickName;
	}

	// Property accessors
	


}