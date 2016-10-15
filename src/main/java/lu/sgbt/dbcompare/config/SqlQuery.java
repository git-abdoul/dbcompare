package lu.sgbt.dbcompare.config;

import java.util.Map;


public class SqlQuery {
	private String name;
	private String comparisonType;
	private boolean checkDuplicates;
	private Map<String, QueryItem> queryItems;
	
	public SqlQuery(String name, boolean checkDuplicates, String comparisonType, Map<String, QueryItem> queryItems) {
		super();
		this.name = name;
		this.comparisonType = comparisonType ;
		this.queryItems = queryItems;
		this.checkDuplicates = checkDuplicates;
	}

	public String getName() {
		return name;
	}

	public String getComparisonType() {
		return comparisonType;
	}

	public boolean isCheckDuplicates() {
		return checkDuplicates;
	}

	public Map<String, QueryItem> getQueryItems() {
		return queryItems;
	}
}
