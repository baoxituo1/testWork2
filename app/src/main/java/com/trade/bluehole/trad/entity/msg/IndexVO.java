package com.trade.bluehole.trad.entity.msg;

import java.util.List;


/**
 * 首页用参数
 * @author 95
 *
 */
public class IndexVO {

	/**
	 * 系统消息
	 */
	private Integer totalNoticeNum;
	
	/**
	 * 新闻公告
	 */
	private Integer totalLetterNum;
	
	/**
	 * 店铺商品数
	 */
	private Integer totalProNum;
	
	/**
	 * 店铺活动数
	 */
	private Integer totalActivityNum;
	
	/**
	 * 店铺热销数
	 */
	private Integer totalHotNum;
	
	/**
	 * 自定义分类数
	 */
	private Integer totalCoverTypeNum;
	
	/**
	 * 标签总数
	 */
	private Integer totalLabelNum;
	
	/**
	 * 评论数
	 */
	private Integer totalDiscussNum;
	/**
	 * 店铺被访问数
	 */
	private Integer accessNum;
	/**
	 * 店铺被收藏数
	 */
	private Integer shopcollectNum;
	/**
	 * 商品被访问数
	 */
	private Integer accessShopNum;
	/**
	 * 商品被收藏数
	 */
	private Integer allshopcollectNum;
	/**
	 * 评论
	 */
	private List<IndexProCommentVO> talkAbout;
	/**
	 * 站内信
	 */
	private List<IndexProCommentVO> messAge;

	public List<IndexProCommentVO> getMessAge() {
		return messAge;
	}

	public void setMessAge(List<IndexProCommentVO> messAge) {
		this.messAge = messAge;
	}

	public Integer getAllshopcollectNum() {
		return allshopcollectNum;
	}

	public void setAllshopcollectNum(Integer allshopcollectNum) {
		this.allshopcollectNum = allshopcollectNum;
	}

	public Integer getAccessShopNum() {
		return accessShopNum;
	}

	public void setAccessShopNum(Integer accessShopNum) {
		this.accessShopNum = accessShopNum;
	}

	public Integer getShopcollectNum() {
		return shopcollectNum;
	}

	public void setShopcollectNum(Integer shopcollectNum) {
		this.shopcollectNum = shopcollectNum;
	}

	public Integer getAccessNum() {
		return accessNum;
	}

	public void setAccessNum(Integer accessNum) {
		this.accessNum = accessNum;
	}

	public Integer getTotalNoticeNum() {
		return totalNoticeNum;
	}

	public void setTotalNoticeNum(Integer totalNoticeNum) {
		this.totalNoticeNum = totalNoticeNum;
	}

	public Integer getTotalLetterNum() {
		return totalLetterNum;
	}

	public void setTotalLetterNum(Integer totalLetterNum) {
		this.totalLetterNum = totalLetterNum;
	}

	public Integer getTotalProNum() {
		return totalProNum;
	}

	public void setTotalProNum(Integer totalProNum) {
		this.totalProNum = totalProNum;
	}

	public Integer getTotalActivityNum() {
		return totalActivityNum;
	}

	public void setTotalActivityNum(Integer totalActivityNum) {
		this.totalActivityNum = totalActivityNum;
	}

	public Integer getTotalHotNum() {
		return totalHotNum;
	}

	public void setTotalHotNum(Integer totalHotNum) {
		this.totalHotNum = totalHotNum;
	}

	public Integer getTotalCoverTypeNum() {
		return totalCoverTypeNum;
	}

	public void setTotalCoverTypeNum(Integer totalCoverTypeNum) {
		this.totalCoverTypeNum = totalCoverTypeNum;
	}

	public Integer getTotalLabelNum() {
		return totalLabelNum;
	}

	public void setTotalLabelNum(Integer totalLabelNum) {
		this.totalLabelNum = totalLabelNum;
	}

	public Integer getTotalDiscussNum() {
		return totalDiscussNum;
	}

	public void setTotalDiscussNum(Integer totalDiscussNum) {
		this.totalDiscussNum = totalDiscussNum;
	}

	public List<IndexProCommentVO> getTalkAbout() {
		return talkAbout;
	}

	public void setTalkAbout(List<IndexProCommentVO> talkAbout) {
		this.talkAbout = talkAbout;
	}
	
	
	
}
