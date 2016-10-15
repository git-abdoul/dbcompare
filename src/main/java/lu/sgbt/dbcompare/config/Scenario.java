package lu.sgbt.dbcompare.config;

public class Scenario {
	private String type;
	private String reference;
	private String target;
	private String connection;
	
	public Scenario(String type, String reference, String target,
			String connection) {
		super();
		this.type = type;
		this.reference = reference;
		this.target = target;
		this.connection = connection;
	}

	public String getType() {
		return type;
	}

	public String getReference() {
		return reference;
	}

	public String getTarget() {
		return target;
	}

	public String getConnection() {
		return connection;
	}
}
