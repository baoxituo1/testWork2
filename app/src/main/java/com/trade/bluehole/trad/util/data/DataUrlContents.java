package com.trade.bluehole.trad.util.data;

/**
 * Created by Administrator on 2015-04-02.
 */
public class DataUrlContents {
    /**
     * 列表头部图片
     */
    public final static String img_list_head_img="@!p-l-h-e-280";
    /**
     * logo 300*300 无水印
     */
    public final static String img_logo_img="@!p-l-h-e-280-n";

    /**
     * 服务器地址
     */
    public final  static String SERVER_HOST="http://192.168.1.161:8080/qqt_up/";
    /**
     * 图片服务器地址
     */
    public final  static String IMAGE_HOST="http://ossimg.178tb.com/";
    /**
     * 读取店铺自定义分类商品数量集合
     */
    public final  static String load_pro_covers_number_list="shopjson/searchProCovers.do";
    /**
     * 读取店铺自定义分类商品数量集合
     */
    public final  static String load_pro_all_list="shopjson/findUserProList.do";
    /**
     * 读取店铺商品信息
     */
    public final  static String load_pro_info="shopjson/loadProductJson.do";
    /**
     * 读取店铺信息
     */
    public final  static String load_shop_info="shopjson/shopjson.do";
    /**
     * 保存新建分类
     */
    public final  static String save_shop_cover="shopjson/saveCoverType.do";
    /**
     * 保存店铺信息
     */
    public final  static String save_all_cshop="shopjson/saveProductJson.do";
    /**
     * 更新店铺信息
     */
    public final  static String update_all_cshop="shopjson/updateProductJson.do";
    /**
     * 更改店铺设置
     */
    public final  static String update_shop_config="shopjson/editShop.do";

    /**
     * 商品上下架操作
     */
    public final  static String update_product_state="shopjson/saleOutProduct.do";
    /**
     * 商品删除操作
     */
    public final  static String del_product_bycode="shopjson/deleteProduct.do";
}
