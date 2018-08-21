package com.letv.autoapk.boss;

public class VideoChargeInfo {
	private String createTime;// 2016-03-17 00:00:00创建日期
	private String albumName;// 芈月传,专辑名称
	private String chargeId;// 28,价格id
	private String albumId;// 123,专辑id
	private String updateTime;// "2016-04-22 19:20:28",更新日期
	private int status;// 3,状态3是发布，1是未发布
	private int isCharge;// 1,是否付费，0：免费，1：付费
	private String chargePlatform;// "104001,104002,104003,104004",<br/>付费平台
									// PC（WEB,M） 104001<br/> 移动设备 104002
									// <br/>pad 104003
									// <br/>TV 104004
	private String pic;// "http://i3.letvimg.com/lc07_pay/201603/25/18/07/1458900422344.png",套餐图片url
	private int tryLookTime;// 360,试看时间，秒
	private String tenantId;// 1111111532,租户id
	private String price;// 2.5,价格
	private String videoId;// 456,视频id
	private int chargeType;// 1 付费类型 1：点播，2：点播或包月，3：包月

	private String catgory;// 100011
	private int validTime;// 有效期，天数

	public String getCatgory() {
		return catgory;
	}

	public int getValidTime() {
		return validTime;
	}

	public void setValidTime(int validTime) {
		this.validTime = validTime;
	}

	public void setCatgory(String catgory) {
		this.catgory = catgory;
	}

	public String getCustomCatgoryId() {
		return customCatgoryId;
	}

	public void setCustomCatgoryId(String customCatgoryId) {
		this.customCatgoryId = customCatgoryId;
	}

	public String getFixedTime() {
		return fixedTime;
	}

	public void setFixedTime(String fixedTime) {
		this.fixedTime = fixedTime;
	}

	private String customCatgoryId;// 0
	private String fixedTime;// 2016-03-31 00:00:00"

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(int isCharge) {
		this.isCharge = isCharge;
	}

	public String getChargePlatform() {
		return chargePlatform;
	}

	public void setChargePlatform(String chargePlatform) {
		this.chargePlatform = chargePlatform;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getVideoId() {
		return videoId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTryLookTime() {
		return tryLookTime;
	}

	public void setTryLookTime(int tryLookTime) {
		this.tryLookTime = tryLookTime;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getChargeType() {
		return chargeType;
	}

	public void setChargeType(int chargeType) {
		this.chargeType = chargeType;
	}

}
