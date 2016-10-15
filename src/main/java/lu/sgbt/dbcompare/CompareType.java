package lu.sgbt.dbcompare;

public enum CompareType {
	SETUP("setup"), PROCESS("process");
	
	private String type;
	
	private CompareType(String type) {
		this.type = type;
	}
	
	public String getValue() {
		return type;
	}
	
}
