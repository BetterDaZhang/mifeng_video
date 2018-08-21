package com.letv.autoapk.ui.search;

enum ChannelType {
	
	MOVIE("1"), 
	TV_PLAY("2"), 
	VARIETY_SHOW("3"),
	ENTERTAINMENT("4"),
	MUSIC("5"), 
	ANIMATION("6"), 
	GAME("7"), 
	INFORMATION("8"), 
	FASHION("9"), 
	DOCUMENTARY("10"), 
	AUTO("11"), 
	SPORTS("12"),
	CHILDREN("13"),
	LIFE("14"),
	FINANCE("15"),
	EDUCATION("16"),
	HEALTH("17"),
	TOUR("18"),
	QUYI("19"),
	PETS("20"),
	BUINESS("21"),
	MILITARY("22"),
	TECHNOLOGY("23"),
	FUNNY("24"),
	COMMONWEAL("25"),
	OTHER("26");

	private String channcelId;

	private ChannelType(String id) {
		channcelId = id;
	}

	public String getChanncelId() {
		return channcelId;
	}


	
}
