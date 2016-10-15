package lu.sgbt.dbcompare.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.QueryItem;
import lu.sgbt.dbcompare.result.ComparatorResult;
import lu.sgbt.dbcompare.result.ComparatorResultManager;


public class KeyQueryJob extends QueryJob {
	
	public KeyQueryJob(CompareScenarioConfig compareScenario, Map<String, Connection> connections,Map<String, String> prefixes) {
		super(compareScenario, connections, prefixes);
	}

	public synchronized List<ComparatorResult> executeJob() throws Throwable {
		// Aziz Enhancement
		// For KeyQueryJob, do not uuse only DEFAULT queryItem
		// First check if there are some defined target and references, else retrieve DEFAULT
		// QueryItem queryItem = query.getQueryItems().get("DEFAULT");
		Map<String, QueryItem> queryItems = query.getQueryItems();
		QueryItem targetQueryItem = null;
		QueryItem referenceQueryItem = null;
		if (queryItems.size()>1) {
			targetQueryItem = queryItems.get(target);
			referenceQueryItem = queryItems.get(reference);
		} else {
			targetQueryItem = queryItems.get("DEFAULT");
			referenceQueryItem = queryItems.get("DEFAULT");
		}
		
		
		 List<ComparatorResult> results = new ArrayList<ComparatorResult>();
		if (targetQueryItem!=null && referenceQueryItem!=null) {
			ComparatorResultManager comparatorMgr = new ComparatorResultManager();
			comparatorMgr.setQueryName(query.getName());
			comparatorMgr.setEnv(env);
			Connection connection = connections.get(scenario.getConnection());
			comparatorMgr.setReferenceResultData(processQuery(reference, referenceQueryItem, reference, connection));
			comparatorMgr.setTargetResultData(processQuery(target, targetQueryItem, target, connection));
			results.add(comparatorMgr.getResult());     
		} 
        return results;
    }

	@Override
	protected void setEnv() {
		env = scenario.getConnection();	
	}    
}
