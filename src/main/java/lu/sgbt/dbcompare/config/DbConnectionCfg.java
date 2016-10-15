package lu.sgbt.dbcompare.config;

public class DbConnectionCfg {
	private String name;
	private String url;
	private String user;
	private String password;
	private String driver;
	private String prefixSchema;
	private boolean active;	

	public DbConnectionCfg(String name, String url, String user,
			String password, String driver, String prefixSchema, boolean active) {
		super();
		this.name = name;
		this.url = url;
		this.user = user;
		this.password = password;
		this.driver = driver;
		this.prefixSchema = prefixSchema;
		this.active = active;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPrefixSchema() {
		return prefixSchema;
	}

	public void setPrefixSchema(String prefixSchema) {
		this.prefixSchema = prefixSchema;
	}
}
