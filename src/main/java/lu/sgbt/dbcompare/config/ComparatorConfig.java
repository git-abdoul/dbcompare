package lu.sgbt.dbcompare.config;

import java.util.List;

public class ComparatorConfig {
	private String title;
	private Scenario defaultScenario;
	private List<DbConnectionCfg> connections;
	private List<CompareScenarioConfig> compareSet;
	
	public ComparatorConfig(String title, Scenario defaultScenario, List<DbConnectionCfg> connections, List<CompareScenarioConfig> compareSet) {
		super();
		this.title = title;
		this.defaultScenario = defaultScenario;
		this.connections = connections;
		this.compareSet = compareSet;
	}
	
	public String getTitle() {
		return title;
	}

	public Scenario getDefaultScenario() {
		return defaultScenario;
	}

	public List<DbConnectionCfg> getConnections() {
		return connections;
	}

	public List<CompareScenarioConfig> getCompareSet() {
		return compareSet;
	}
}
