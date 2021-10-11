package com.example.zzmedble;

public enum DeviceType {
    //	这里添加J750,key：J750L 防止有的时候机器扫描的时候并不是以 BLEsmart_00000332 开头，而是直接J750L
	//HGM_124T("BLEsmart_00090006",1),
	//HGM_125T("BLEsmart_00090007",1),
	//HGM_126T("BLEsmart_00090008",1),
	BLOOD_9031C("HBP-903X",2),
	J750L("BLEsmart_00000332",2),
	BLOOD_9200X("BLEsmart_00000116",2),
	BLOOD_9200T("BLEsmart",2);


	/**
	 * the prefix used to filter during scanning
	 */
	private String prefix;
	/**
	 * type =1  glucose, =2 blood
	 */
	private int type;
	
	DeviceType(String prefix, int type){
		this.prefix = prefix;
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getType() {
		return type;
	}

}
