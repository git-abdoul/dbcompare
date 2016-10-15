/*
 * La Banque Postale. Tous droits réservés
 * 
 * La Banque Postale
 * Siège Social - 115 rue de Sèvres
 * 75006 PARIS
 * 
 * Ce logiciel est la propriété exclusive de La Banque Postale.
 */
package lu.sgbt.dbcompare.job;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.QueryItem;
import lu.sgbt.dbcompare.result.ComparatorResult;
import lu.sgbt.dbcompare.result.ComparatorResultManager;


/**
 * Variable Query Job
 * 
 */
public class VariableQueryJob extends QueryJob {

	private Map<String, List<String>> variables ;
	
	/**
	 * @param compareScenario
	 * @param connections
	 */
	public VariableQueryJob(CompareScenarioConfig compareScenario, Map<String, Connection> connections, Map<String, List<String>> variables, Map<String, String> prefixes) {
		super(compareScenario, connections, prefixes);
		this.variables = variables ;
	}

	/* (non-Javadoc)
	 * @see com.lbp.tools.comparator.job.QueryJob#executeJob()
	 */
	@Override
	public synchronized List<ComparatorResult> executeJob() throws Throwable {
		List<ComparatorResult> results = new ArrayList<ComparatorResult>();
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
		
		if (targetQueryItem==null || referenceQueryItem==null) {
			throw new Exception("reference or target query cannot be found ...");
		}
		
		// We must retrieve the name of the variable to use in the query
		List<String> targetSqlVariables = targetQueryItem.getVariableNames();
		List<String> referenceSqlVariables = referenceQueryItem.getVariableNames();
		if (targetSqlVariables != null && referenceSqlVariables != null) {
			// Here we will use Variables from the Map and map with variable name
			// First get the number of loop to perform (max size of map)
			int maxTarget = computeMax(targetSqlVariables);
			int maxReference = computeMax(referenceSqlVariables);
			// Check that list of variables is the same on both side
			if (maxTarget != maxReference) {
				throw new Exception("Reference and Target variables does not have the same size...");
			} else {
				// Then Build a List of variables to use
				// Target
				List<List<String>> targetValuesList = new ArrayList<List<String>>();
				for (int i= 0 ; i < maxTarget ; i++) {
					List<String> innerList = new ArrayList<String>() ;
					for (String currentVarName : referenceSqlVariables) {
						List<String> currentVarValues = this.variables.get(currentVarName) ;
						String currentValue = i < currentVarValues.size() ? currentVarValues.get(i) : "" ;
						innerList.add(currentValue) ;
					}
					targetValuesList.add(innerList) ;
				}
				// Reference
				List<List<String>> referenceValuesList = new ArrayList<List<String>>();
				for (int i= 0 ; i < maxReference ; i++) {
					List<String> innerList = new ArrayList<String>() ;
					for (String currentVarName : targetSqlVariables) {
						List<String> currentVarValues = this.variables.get(currentVarName) ;
						String currentValue = i < currentVarValues.size() ? currentVarValues.get(i) : "" ;
						innerList.add(currentValue) ;
					}
					referenceValuesList.add(innerList) ;
				}

				for (int i= 0 ; i < maxTarget ; i++) {	
					List<String> targetVariables = targetValuesList.get(i) ;
					List<String> referenceVariables = referenceValuesList.get(i) ;
					if (targetVariables != null && referenceVariables != null) {
						ComparatorResultManager comparatorMgr = new ComparatorResultManager();
						comparatorMgr.setQueryName(query.getName());
						comparatorMgr.setEnv(env);
						comparatorMgr.setReferenceResultData(processQuery(referenceVariables, referenceQueryItem, reference, connections.get(reference)));
						comparatorMgr.setTargetResultData(processQuery(targetVariables, targetQueryItem, target, connections.get(target)));
						ComparatorResult result = comparatorMgr.getResult();
						result.setTitle(query.getName() + " " + referenceVariables.toString() + " " + targetVariables.toString());
						results.add(result);
					}
				}
			}
		} 
        return results;
	}

	private int computeMax(List<String> targetSqlVariables) {
		int max = 0 ;
		for (String currentSqlVarName : targetSqlVariables) {
			List<String> inputValues = this.variables.get(currentSqlVarName) ;
			if (inputValues != null && inputValues.size()>max) {
				max = inputValues.size() ;
			}
		}
		return max;
	}

	/* (non-Javadoc)
	 * @see com.lbp.tools.comparator.job.QueryJob#setEnv()
	 */
	@Override
	protected void setEnv() {
		env = reference + "_" + target + "_" + "<variable>";		
	}

}
