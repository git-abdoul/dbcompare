package lu.sgbt.dbcompare.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.sql.BLOB;

import lu.sgbt.dbcompare.config.QueryItem;


public class QueryResultData {
	private String id;
	private String key;
    protected int rowCount;
    protected int diffRowCount;
    protected int colCount;
    private Set<String> duplicates;
    protected Map<String, String[]> results;
    protected String colHeaderClass[];
    protected String colHeader[];
    protected String colHeaderLabel[];
    protected SQLException eSQL;
    private String queryName;
    private boolean checkDuplicates;
    private QueryItem query;
    
    public QueryResultData(String queryName, boolean checkDuplicates) {
		super();
		this.queryName = queryName;
		this.checkDuplicates = checkDuplicates;
	}

	public final synchronized int getColCount(){
        return colCount;
    }

    public final synchronized SQLException getESQL(){
        return eSQL;
    }

    public final int getRowCount(){
        return rowCount;
    }
    
    public final int getDiffRowCount(){
        return diffRowCount;
    }

    public final synchronized boolean checkData() {
        return results != null && colCount > 0 && rowCount >= 0 && colHeader != null && colHeaderClass != null && colHeaderLabel != null;
    }

    public final synchronized String getColumnLabel(int col){
        if(!checkData())
            return null;
        if(col < 0 || col >= colCount)
            return null;
        else
            return colHeaderLabel[col];
    }

    public final synchronized String getColumnName(int col){
        if(!checkData())
            return null;
        if(col < 0 || col >= colCount)
            return null;
        else
            return colHeader[col];
    }

    public final synchronized String getColumnClass(int col){
        if(!checkData())
            return null;
        if(col < 0 || col >= colCount)
            return null;
        else
            return colHeaderClass[col];
    }

    public synchronized void reset(){
        colCount = 0;
        rowCount = 0;
        results = null;
        colHeader = null;
        colHeaderLabel = null;
        colHeaderClass = null;
        eSQL = null;
    }

    public synchronized boolean fillFromResultSet(ResultSet rs){
    	results = new HashMap<String, String[]>();
    	duplicates = new HashSet<String>();
        if(rs == null)
            return false;
        boolean status = true;
        ResultSetMetaData rsData = null;
        try {
            rsData = rs.getMetaData();
            if(rsData == null || rsData.getColumnCount() <= 0){
                status = false;
            } else {
            	List<String> identifiers = query.getIdentifiers();
                int columns = rsData.getColumnCount();
                String headers[] = new String[columns];
                String classes[] = new String[columns];
                int sz = columns-identifiers.size();
                String headersLabels[] = new String[sz>0?sz:columns];
                String headersClasses[] = new String[sz>0?sz:columns];
                int resCol = 0;
                for(int col = 0; col < columns; col++){
                	String columnName = rsData.getColumnName(col + 1).toUpperCase();
                	String columnClassName = rsData.getColumnClassName(col + 1);
                    headers[col] = columnName;   
                    classes[col] = columnClassName;
                    if (!identifiers.contains(columnName)) {
                    	headersLabels[resCol]=columnName;
                    	headersClasses[resCol]=columnClassName;
                    	resCol++;
                    }
                }
                
                if (sz==0) {
                	headersLabels = headers;
                	headersClasses = classes;
                }
                
                Map<String, List<String>> resultsTemp = new HashMap<String, List<String>>();
                while(rs.next()){
                    String rowStrings[] = new String[headersLabels.length];
                    for(int col = 0; col < headersLabels.length; col++){
                    	String columnName = headersLabels[col];
                    	String className = headersClasses[col];
                    	String value = null;
                    	if ("oracle.sql.BLOB".equals(className)) {
                    		BLOB blob = (BLOB)rs.getObject(columnName);
                    		if (blob!=null)
                    			value = new String(blob.getBytes(1, (int) blob.length()));
                    	}
                    	else                    		
                    		value = rs.getString(columnName);
                    	rowStrings[col] = value;
                    }
                    
                    String identifer = "";
                    for (String columnName : identifiers) 
                    	identifer += rs.getString(columnName.trim())+ "/";                    
                    
                    identifer = identifer.substring(0, identifer.length()-1);
                    List<String> datas = resultsTemp.get(identifer);
                    if (checkDuplicates) {                    	
	                    if (datas==null) {
	                    	datas = new ArrayList<String>();
	                    	datas.addAll(Arrays.asList(rowStrings));
	                    	resultsTemp.put(identifer, datas);
	                    }
	                    else {
	                    	duplicates.add(identifer);
	                    }
                    }
                    else {
                    	if (datas==null) {
	                    	datas = new ArrayList<String>();
	                    	datas.addAll(Arrays.asList(rowStrings));
	                    	resultsTemp.put(identifer, datas);
	                    }
                    	else {
                    		List<String> tmp = new ArrayList<String>();
                    		for (int i=0; i<datas.size(); i++) {
                    			String res = datas.get(i)+","+rowStrings[i];
                    			tmp.add(i, res);
                    		}
                    		resultsTemp.put(identifer, tmp);
                    	}                    	
                    	
                    }
                }
                
                for (String identifier : resultsTemp.keySet()) {
                	List<String> tmp = resultsTemp.get(identifier);
                	results.put(identifier, tmp.toArray(new String[tmp.size()]));
                }
                
                synchronized(this){
                    colCount = columns;
                    rowCount = results.size();                   
                    colHeader = headers;
                    colHeaderLabel = headersLabels;
                    colHeaderClass = headersClasses;
                    eSQL = null;
                }
            }
        }
        catch(SQLException e){
            eSQL = e;
            status = false;
        }
        if(!status)
            reset();
        return status;
    }

    public String getQueryName() {
		return queryName;
	}

	public void setQueryItem(QueryItem query) {
		this.query = query;
	}
	
	public QueryItem getQueryItem() {
		return query;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Set<String> getDuplicates() {
		return duplicates;
	}

	public Map<String, String[]> getResults() {
		return results;
	}

	public String[] getColHeaderLabels() {
		return colHeaderLabel;
	}

	public Map<String, String[]> cloneData() {
		Map<String, String[]> cloneDatas = new HashMap<String, String[]>();
		for (String key : results.keySet()) {
			cloneDatas.put(key, results.get(key));
		}
		return cloneDatas;
	}
}
