package lu.sgbt.dbcompare.config;

public class CompareScenarioConfig {
	private Scenario scenario;
	private SqlQuery query;
	
	public CompareScenarioConfig(Scenario scenario, SqlQuery query) {
		super();
		this.scenario = scenario;
		this.query = query;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public SqlQuery getQuery() {
		return query;
	}
}
