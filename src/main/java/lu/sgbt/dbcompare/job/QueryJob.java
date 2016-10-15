package lu.sgbt.dbcompare.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.QueryItem;
import lu.sgbt.dbcompare.config.Scenario;
import lu.sgbt.dbcompare.config.SqlQuery;
import lu.sgbt.dbcompare.result.ComparatorResult;
import lu.sgbt.dbcompare.result.QueryResultData;

import org.apache.log4j.Logger;



public abstract class QueryJob {
	
	protected Scenario scenario;
	protected String target;
	protected String reference;
	protected Map<String , Connection> connections;
	protected Map<String , String> prefixes;
	protected SqlQuery query;
	protected String env;	
	
	public QueryJob(CompareScenarioConfig compareScenario, Map<String , Connection> connections, Map<String, String> prefixes) {
		this.connections = connections;
		this.prefixes = prefixes;
		this.scenario = compareScenario.getScenario();
		this.reference = scenario.getReference();
		this.target = scenario.getTarget();
		this.query = compareScenario.getQuery();
		
		setEnv();
	}
	
	private QueryResultData executeJobPerKey(QueryResultData res, String sqlText, List<String> keyList, Connection connection) {
    	PreparedStatement statement = null;
        java.sql.ResultSet result = null;        
        try {
        	statement = connection.prepareStatement(sqlText);
        	if (keyList!=null && keyList.size()>0) {
        		try {
        			for (int i=1 ; i <= keyList.size(); i++) {
        				statement.setString(i, keyList.get(i-1));
        			}
        		} catch (SQLException e) {
        			String msgFormat = "Failed to set variable to Prepared Statement [%s]. If error is raised later, check if SQL Query is correct.";
					Logger.getLogger(QueryJob.class).warn(String.format(msgFormat, e.getMessage())) ;
        		}
        	}
        	Logger.getLogger(QueryJob.class).info("Executing : " + sqlText);
        	result = statement.executeQuery();
        	Logger.getLogger(QueryJob.class).info("Executed !");
            res.fillFromResultSet(result);
        } catch(SQLException e) {
        	Logger.getLogger(QueryJob.class).error("Failed to execute Query.", e) ;
        }
        
        if (statement != null) {
            try {
                statement.close();
            } catch(SQLException e){
            	Logger.getLogger(QueryJob.class).error("Failed to close statement.", e) ;
            }
        }
        return res;
    }
	
	protected QueryResultData processQuery(String key, QueryItem queryItem, String target, Connection connection) throws Throwable {
		if (queryItem!=null)
			queryItem.setPrefixSchema(prefixes.get(target));
		QueryResultData resultData = new QueryResultData(query.getName(), query.isCheckDuplicates());
		resultData.setId(target);
		resultData.setQueryItem(queryItem);
		resultData.setKey(key);
		List<String> keyList = null ;
		if (key != null) {
			keyList = new ArrayList<String>() ;
			keyList.add(key) ;
		}
    	executeJobPerKey(resultData, queryItem == null ? null : queryItem.getSqlQuery(), keyList, connection);        
        return resultData;
	}
	
	protected QueryResultData processQuery(List<String> keyList, QueryItem queryItem, String target, Connection connection) throws Throwable {
		if (queryItem!=null)
			queryItem.setPrefixSchema(prefixes.get(target));
		QueryResultData resultData = new QueryResultData(query.getName(), query.isCheckDuplicates());
		resultData.setId(target);
		resultData.setQueryItem(queryItem);
		resultData.setKey(keyList!=null ? keyList.toString() : "");
    	executeJobPerKey(resultData, queryItem == null ? null :  queryItem.getSqlQuery(), keyList, connection);        
        return resultData;
	}
	
	public abstract List<ComparatorResult> executeJob() throws Throwable;
	protected abstract void setEnv();

}
