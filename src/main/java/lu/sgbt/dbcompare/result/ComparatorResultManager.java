package lu.sgbt.dbcompare.result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ComparatorResultManager {	
	private String queryName;
	private String env;
	private Map<String, QueryResultData> resultsData;
		
	public ComparatorResultManager() {
		resultsData = new HashMap<String, QueryResultData>();
	}

	public synchronized void setTargetResultData(QueryResultData result) {
		resultsData.put("TARGET", result);
	}
	
	public synchronized void setReferenceResultData(QueryResultData result) {
		resultsData.put("REFERENCE", result);
	}

	public synchronized QueryResultData getTargetResultData(){
		return resultsData.get("TARGET");
	}
	
	public synchronized QueryResultData getReferenceResultData(){
		return resultsData.get("REFERENCE");
	}

	public synchronized ComparatorResult getResult() throws Throwable {
		ComparatorResult result = null;
		result = compareRow();
		return result;
	}
	
	private synchronized ComparatorResult compareRow() {
		QueryResultData referenceData = resultsData.get("REFERENCE");
		QueryResultData targetData = resultsData.get("TARGET");
		Set<String> identifierResults = new HashSet<String>();
		identifierResults.addAll(getIdentifiers(referenceData));
		identifierResults.addAll(getIdentifiers(targetData));
		ComparatorResult result = new ComparatorResult(env, queryName, targetData.getId(), referenceData.getId());
		result.processDiff(referenceData, targetData, identifierResults);
		return result;
	}
	
	private Set<String> getIdentifiers(QueryResultData resultData) {
		Set<String> res = new HashSet<String>();
		for (String ident : resultData.getResults().keySet()) {
			res.add(ident);
		}
		return res;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	
	public void setEnv(String env) {
		this.env = env;
	}
}
