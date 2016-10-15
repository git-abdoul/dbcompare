package lu.sgbt.dbcompare.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import lu.sgbt.dbcompare.ComparatorUtils;


public class ComparatorResult {
	private int diffRowsCount;
	private String queryName;
//	private Map<String, List<String>> unmatchedRows;
	private int matchedRow;
	private String envName;
	private String key;
	private boolean ok;
	private String title;
	private Map<String, List<MisMatchValue>> mismatchedValues;
	private List<String> identifiers;
	
	private DiffStat targetStat;
	private DiffStat referenceStat;

	public ComparatorResult(String envName, String queryName, String target, String reference) {
		this.envName = envName;
		this.queryName = queryName;
		targetStat = new DiffStat(target);
		referenceStat = new DiffStat(reference);
	}

	public final void processDiff(QueryResultData reference, QueryResultData target, Set<String> identifierResults) {
		ok = true;
		key = reference.getKey();
		identifiers = reference.getQueryItem().getIdentifiers();
		matchedRow = 0;
		diffRowsCount = Math.abs(reference.rowCount - target.rowCount);
		
		mismatchedValues = new HashMap<String, List<MisMatchValue>>();
		
		targetStat.setColHeaderLabel(target.getColHeaderLabels());
		referenceStat.setColHeaderLabel(reference.getColHeaderLabels());
		
		Map<String, String[]> referenceResults = reference.cloneData();
		Map<String, String[]> targetResults = target.cloneData();
		for (String identifier : identifierResults) {
			boolean commonIdentifier = false;
			String[] refRow = referenceResults.get(identifier);
			String[] tarRow = targetResults.get(identifier);
			if (referenceResults.containsKey(identifier) && targetResults.containsKey(identifier)) {
				referenceResults.remove(identifier);
				targetResults.remove(identifier);
				commonIdentifier = true; 
			}
			
			if (commonIdentifier) {
				String refRowStr = ComparatorUtils.getRowAsUniqueString(refRow);
				String tarRowStr = ComparatorUtils.getRowAsUniqueString(tarRow);
				if (refRowStr.equals(tarRowStr)) 
					matchedRow++;
				else {
					List<MisMatchValue> values = new ArrayList<MisMatchValue>();
					mismatchedValues.put(identifier, values);
					String[] headers = reference.getColHeaderLabels();
					String[] refValues = reference.getResults().get(identifier);
					String[] tarValues = target.getResults().get(identifier);
					for (int i=0; i<headers.length; i++) {
						if(!isValueMatched(refValues[i], tarValues[i])) 
							values.add(new MisMatchValue(headers[i], refValues[i], tarValues[i]));
					}
					
				}	
			}
			
		}
		
		referenceStat.setAdditionals(referenceResults.keySet());
		referenceStat.setDuplicates(reference.getDuplicates());
		referenceStat.setRowCount(reference.rowCount);
		
		targetStat.setAdditionals(targetResults.keySet());
		targetStat.setDuplicates(target.getDuplicates());
		targetStat.setRowCount(target.rowCount);
		
		ok &= (diffRowsCount==0);
		ok &= reference.getDuplicates().size()==0;
		ok &= target.getDuplicates().size()==0;
		ok &= referenceResults.size()==0;
		ok &= targetResults.size()==0;
		ok &= mismatchedValues.size()==0;
	}
	
	private boolean isValueMatched(String val1, String val2) {
		boolean ret = false;
		if (val1!=null&&val2!=null)
			return val1.equals(val2);
		if (val1==null && val2==null)
			return true;		
		return ret;
	}
	
	public String getKey() {
		return key;
	}

	public String getQueryName() {
		return queryName;
	}

	public int getDiffRowsCount() {
		return diffRowsCount;
	}

	public int getMatchedRowCount() {
		return matchedRow;
	}

	public DiffStat getTargetStat() {
		return targetStat;
	}

	public DiffStat getReferenceStat() {
		return referenceStat;
	}

	public String getEnvName() {
		return envName;
	}
	
	public String getTitle() {
		if (title==null || title.length()==0)
			title = queryName;
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isOk() {
		return ok;
	}	

	public SortedMap<String, List<MisMatchValue>> getMismatchedValues() {
		
		return new TreeMap<String, List<MisMatchValue>>(mismatchedValues);
	}	
	
	public String getIdentifier() {
		String identifier = "";
		int i = 0;
		for (String field:identifiers) {	
			identifier = identifier + field;
			if (i<identifiers.size()-1)
				identifier = identifier + "/";
			i++;
		}
		return identifier.toLowerCase();
	}
	
	public class MisMatchValue {
		private String name;
		private String refValue;
		private String tarValue;
		
		public MisMatchValue(String name, String refValue, String tarValue) {
			super();
			this.name = name;
			this.refValue = refValue;
			this.tarValue = tarValue;
		}

		public String getName() {
			return name;
		}

		public String getRefValue() {
			return refValue;
		}

		public String getTarValue() {
			return tarValue;
		}
	}
	
	

	public class DiffStat {
		private String name;
		private int rowCount;
		private Set<String> additionals;
		private Set<String> duplicates;
		private String colHeaderLabel[];
		
		
		public DiffStat(String name) {
			this.name = name;
		}
		
		public int getRowCount() {
			return rowCount;
		}

		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}

		public List<String> getAdditionals() {
			List<String> sortedList = new ArrayList<String>(additionals);
			Collections.sort(sortedList);
			return sortedList;
		}

		public void setAdditionals(Set<String> additionals) {
			this.additionals = additionals;
		}

		public List<String> getDuplicates() {
			List<String> sortedList = new ArrayList<String>(duplicates);
			Collections.sort(sortedList);
			return sortedList;
		}

		public void setDuplicates(Set<String> duplicates) {
			this.duplicates = duplicates;
		}

		public String getName() {
			return name;
		}

		public String[] getColHeaderLabel() {
			return colHeaderLabel;
		}

		public void setColHeaderLabel(String[] colHeaderLabel) {
			this.colHeaderLabel = colHeaderLabel;
		}			
	}
	
}
