package com.letv.autoapk.ui.search;
@Deprecated
enum CopyOfChannelType {
	
	MOVIE("1", "电影"), 
	TV_PLAY("2", "电视剧"), 
	VARIETY_SHOW("3","综艺"),
	ENTERTAINMENT("4","娱乐"),
	MUSIC("5", "音乐"), 
	ANIMATION("6", "动漫"), 
	GAME("7", "游戏"), 
	INFORMATION("8", "资讯"), 
	FASHION("9", "风尚"), 
	DOCUMENTARY("10", "纪录片"), 
	AUTO("11", "汽车"), 
	SPORTS("12", "体育"),
	CHILDREN("13", "少儿（亲子+母婴）"),
	LIFE("14", "生活"),
	FINANCE("15", "财经"),
	EDUCATION("16", "教育"),
	HEALTH("17", "健康"),
	TOUR("18", "旅游"),
	QUYI("19", "曲艺"),
	PETS("20", "宠物"),
	BUINESS("21", "商业"),
	MILITARY("22", "军事"),
	TECHNOLOGY("23", "科技"),
	FUNNY("24", "搞笑"),
	COMMONWEAL("25", "公益"),
	OTHER("26", "其他");

	private String channcelName;
	private String channcelId;

	private CopyOfChannelType(String id, String name) {
		channcelId = id;
		channcelName = name;
	}

	public String getChanncelId() {
		return channcelId;
	}

	public String getChanncelName() {
		return channcelName;
	}

	public static String getChanncelName(String id) {
		for (CopyOfChannelType type : CopyOfChannelType.values()) {
			if (type.getChanncelId().equals(id)) {
				return type.getChanncelName();
			}
		}
		return "";
	}
	
	public static String getChannelId(String name){
		for (CopyOfChannelType type : CopyOfChannelType.values()) {
			if (type.getChanncelName().equals(name)) {
				return type.getChanncelId();
			}
		}
		return "";
	}
	
}
