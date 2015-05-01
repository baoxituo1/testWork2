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
     * 手机端-活动-头部-900*450-无水印
     */
    public final static String activity_logo_img="@!p-a-h-w900-h450";



    /**
     * 更新服务器地址
     */
    public final  static String UPDATE_SERVER_HOST="http://miss77.vicp.net/";

    /**
     * 服务器地址
     */
    //public final  static String SERVER_HOST="http://192.168.1.161:8080/qqt_up/";
    public final  static String SERVER_HOST="http://miss77.vicp.net/qqt_up/";
    //public final  static String SERVER_HOST="http://192.168.1.108:8080/qqt_up/";
    /**
     * 图片服务器地址
     */
    public final  static String IMAGE_HOST="http://ossimg.178tb.com/";
    /**
     * 用户登录
     */
    public final  static String user_login="shopjson/userLogin.do";
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
    /**
     * 注册用户和商铺
     */
    public final  static String add_user_shop="shopjson/registerUser.do";
    /**
     * 读取店铺数据
     */
    public final  static String load_user_shop_info="shopjson/loadShopCommonInfo.do";
    /**
     * 获取手机注册验证码
     */
    public final  static String send_phone_yzm="shopjson/getPhoneAuthCode.do";
    /**
     * 获取手机验证码倒计时
     */
    public final  static String load_send_yzm_time="shopjson/checkCodeTime.do";
    /**
     * 查询 店铺统计信息
     */
    public final  static String load_shop_statistical_info="shopjson/loadIndexInfo.do";
    /**
     * 保存更新活动信息
     */
    public final  static String save_or_update_activity="shopjson/saveActivity.do";
    /**
     * 读取活动列表信息
     */
    public final  static String load_shop_activity="shopjson/loadShopActivity.do";
    /**
     * 读取活动详细信息
     */
    public final  static String load_activity_detail="shopjson/loadActivityInfo.do";
    /**
     * 更新活动
     */
    public final  static String update_activity_state="shopjson/updateActivityStatus.do";
    /**
     * 删除活动
     */
    public final  static String delete_activity="shopjson/deleteActivity.do";
    /**
     * 读取打折促销列表
     */
    public final  static String load_dynamic_sale_detail="shopjson/loadShopDynamic.do";
    /**
     * 读取所有商品的打折状态列表
     */
    public final  static String load_product_sale_detail="shopjson/loadShopProductSale.do";
    /**
     * 更新打折信息
     */
    public final  static String update_product_sale_detail="shopjson/updateProductForSale.do";
    /**
     * 读取用户基础信息
     */
    public final  static String load_user_base="shopjson/loadUserBase.do";
    /**
     * 更新用户基础信息
     */
    public final  static String update_user_base="shopjson/updateUserBase.do";
    /**
     *读取新闻信息通过webview
     */
    public final  static String load_notice_for_web_view="shopjson/showNotice.do?newCode=";
    /**
     *读取通知信息通过webview
     */
    public final  static String load_letter_for_web_view="shopjson/showLetter.do?letterCode=";
    /**
     *读取新闻列表
     */
    public final  static String load_news_all_list="shopjson/loadNoticeList.do";
    /**
     *读取通知详细列表
     */
    public final  static String load_letter_all_list="shopjson/loadLetterList.do";
    /**
     *预览商品
     */
    public final  static String show_view_pro_web="mobile/detailProduct.htm";
    /**
     *预览店铺
     */
    public final  static String show_view_shop_web="Mshop/showMshop.htm";
    /**
     *预览商品
     */
    public final  static String edit_shop="shopjson/editShop.do";
}
