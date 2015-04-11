package com.trade.bluehole.trad.entity.msg;

import java.sql.Timestamp;
import java.util.List;

public class IndexProCommentVO {
	
	private String productCode;
	
	private String productName;
	
	private Integer proCommentNum;
	
	private String proCommentContent;
	
	private String commentCode;
	
	private String shopCode;
	
	private Timestamp createDate;
	
	private String userCode;
	
	private String messAgeCode;
	
	
	
	public String getMessAgeCode() {
		return messAgeCode;
	}

	public void setMessAgeCode(String messAgeCode) {
		this.messAgeCode = messAgeCode;
	}

	private String letterCode;	
	public String getLetterCode() {
		return letterCode;
	}

	public void setLetterCode(String letterCode) {
		this.letterCode = letterCode;
	}

	public String getLetterTitle() {
		return letterTitle;
	}

	public void setLetterTitle(String letterTitle) {
		this.letterTitle = letterTitle;
	}

	public String getLetterContent() {
		return letterContent;
	}

	public void setLetterContent(String letterContent) {
		this.letterContent = letterContent;
	}

	public int getLetterState() {
		return letterState;
	}

	public void setLetterState(int letterState) {
		this.letterState = letterState;
	}

	private String letterTitle;
	private String letterContent;
	private int letterState;
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String title;
	private String state;
	private String content;
	private String createTime;
	
	
	



	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	private String nickName;
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getProNum() {
		return proNum;
	}

	public void setProNum(int proNum) {
		this.proNum = proNum;
	}

	public String getUserCode() {
		return userCode;
	}

	private int proNum;
	
	
	
	
	
	
	
	
	
	
	
	

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public int getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(int delFlag) {
		this.delFlag = delFlag;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	private int delFlag;
	private String commentContent;
	
	private List<IndexProCommentVO> talkAbout;
	
	
	

	

	public List<IndexProCommentVO> getTalkAbout() {
		return talkAbout;
	}

	public void setTalkAbout(List<IndexProCommentVO> talkAbout) {
		this.talkAbout = talkAbout;
	}

	public String getShopCode() {
		return shopCode;
	}

	public void setShopCode(String shopCode) {
		this.shopCode = shopCode;
	}

	

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProCommentNum() {
		return proCommentNum;
	}

	public void setProCommentNum(Integer proCommentNum) {
		this.proCommentNum = proCommentNum;
	}

	public String getProCommentContent() {
		return proCommentContent;
	}

	public void setProCommentContent(String proCommentContent) {
		this.proCommentContent = proCommentContent;
	}

	public String getCommentCode() {
		return commentCode;
	}

	public void setCommentCode(String commentCode) {
		this.commentCode = commentCode;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	
	
}
