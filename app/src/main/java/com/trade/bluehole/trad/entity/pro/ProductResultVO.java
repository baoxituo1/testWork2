package com.trade.bluehole.trad.entity.pro;

import com.trade.bluehole.trad.entity.Product;
import com.trade.bluehole.trad.entity.ProductBase;

import java.util.List;



public class ProductResultVO implements java.io.Serializable{

	private Product pro;
	private ProductBase proBase;
	private List<ProductImage> images;
	private boolean isHot;
	/**
	 * 商品关联的类别
	 */
	private List<ProductCoverRelVO> myCovers;
	/**
	 * 商品关联的标签
	 */
	private List<ProductLabelRelVO> muLabels;
	/**
	 * 全部类别
	 */
	private List<ShopCoverType> covers;
	/**
	 * 全部标签
	 */
	private List<ProductLabel> labels;

	public Product getPro() {
		return pro;
	}

	public void setPro(Product pro) {
		this.pro = pro;
	}

	public ProductBase getProBase() {
		return proBase;
	}

	public void setProBase(ProductBase proBase) {
		this.proBase = proBase;
	}

	public List<ProductImage> getImages() {
		return images;
	}

	public void setImages(List<ProductImage> images) {
		this.images = images;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	public List<ProductCoverRelVO> getMyCovers() {
		return myCovers;
	}

	public void setMyCovers(List<ProductCoverRelVO> myCovers) {
		this.myCovers = myCovers;
	}

	public List<ProductLabelRelVO> getMuLabels() {
		return muLabels;
	}

	public void setMuLabels(List<ProductLabelRelVO> muLabels) {
		this.muLabels = muLabels;
	}

	public List<ShopCoverType> getCovers() {
		return covers;
	}

	public void setCovers(List<ShopCoverType> covers) {
		this.covers = covers;
	}

	public List<ProductLabel> getLabels() {
		return labels;
	}

	public void setLabels(List<ProductLabel> labels) {
		this.labels = labels;
	}

}
