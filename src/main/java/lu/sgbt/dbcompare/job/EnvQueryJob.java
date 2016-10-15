package lu.sgbt.dbcompare.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.QueryItem;
import lu.sgbt.dbcompare.result.ComparatorResult;
import lu.sgbt.dbcompare.result.ComparatorResultManager;


public class EnvQueryJob extends QueryJob {
	
	public EnvQueryJob(CompareScenarioConfig compareScenario,Map<String, Connection> connections, Map<String, String> prefixes) {
		super(compareScenario, connections, prefixes);
	}

	public List<ComparatorResult> executeJob() throws Throwable {
		List<ComparatorResult> results = new ArrayList<ComparatorResult>();
		Map<String, QueryItem> queryItems = query.getQueryItems();
		QueryItem targetQueryItem = null;
		QueryItem referenceQueryItem = null;
		if (queryItems.size()>1) {
			targetQueryItem = queryItems.get(target);
			referenceQueryItem = queryItems.get(reference);
		}
		else {
			targetQueryItem = queryItems.get("DEFAULT");
			referenceQueryItem = queryItems.get("DEFAULT");
		}
		
		if (targetQueryItem==null || referenceQueryItem==null)
			throw new Exception("reference or target query cannot be found ...");
		
		Map<String, String> targetSqlKeys = targetQueryItem.getSqlKeys();
		Map<String, String> referenceSqlKeys = referenceQueryItem.getSqlKeys();
		
		if (targetSqlKeys==null && referenceSqlKeys==null) {
			ComparatorResultManager comparatorMgr = new ComparatorResultManager();
			comparatorMgr.setQueryName(query.getName());
			comparatorMgr.setEnv(env);
			comparatorMgr.setReferenceResultData(processQuery((String) null, referenceQueryItem, reference, connections.get(reference)));
			comparatorMgr.setTargetResultData(processQuery((String) null, targetQueryItem, target, connections.get(target)));
			results.add(comparatorMgr.getResult());
		}
		else if (targetSqlKeys!=null && referenceSqlKeys!=null) {
			for (String id : targetSqlKeys.keySet()) {				
				String targetKey = targetSqlKeys.get(id);
				String referenceKey = referenceSqlKeys.get(id);
				if (targetKey!=null&&referenceKey!=null) {
					ComparatorResultManager comparatorMgr = new ComparatorResultManager();
					comparatorMgr.setQueryName(query.getName());
					comparatorMgr.setEnv(env);
					comparatorMgr.setReferenceResultData(processQuery(referenceKey, referenceQueryItem, reference, connections.get(reference)));
					comparatorMgr.setTargetResultData(processQuery(targetKey, targetQueryItem, target, connections.get(target)));
					ComparatorResult result = comparatorMgr.getResult();
					result.setTitle(query.getName() + " " + id);
					results.add(result);
				}
			}
		}
        return results;
	}	

	@Override
	protected void setEnv() {
		env = reference + "_" + target;		
	}
}
