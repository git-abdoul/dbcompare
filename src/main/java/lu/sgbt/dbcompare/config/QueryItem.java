package lu.sgbt.dbcompare.config;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.calypso.tk.core.JDate;
import com.calypso.tk.core.Util;

public class QueryItem {
	private String prefixSchema;
	private String target;
	private String selectDistinct;
	private String select;
	private String from;
	private String where;
	private String orderby;
	private String groupby;
	private Map<String, String> sqlKeys;
	private String[] identifiers;
	private String[] variableNames ;
	private String[] innerJoins;
	private String[] leftJoins;
	private String[] rightJoins;
	private String[] fullJoins;
	private Date valueDate;
	
	public QueryItem(String target, String selectDistinct, String select, String from, String where, String orderby,
			String groupby, Map<String, String> sqlKeys, String[] identifiers, String[] variableNames, Date valueDate) {
		super();		
		this.target = target;
		this.selectDistinct = selectDistinct;
		this.select = select;
		this.from = from;	
		this.where = where;
		this.orderby = orderby;
		this.groupby = groupby;
		this.sqlKeys = sqlKeys;
		this.identifiers = identifiers;
		this.variableNames = variableNames ;
		this.valueDate = valueDate;
	}

	public void setPrefixSchema(String prefixSchema) {
		this.prefixSchema = (prefixSchema==null)?"":prefixSchema+".";
	}

	public String getTarget() {
		return target;
	}	

	public void setTarget(String target) {
		this.target = target;
	}

	public Map<String, String> getSqlKeys() {
		return sqlKeys;
	}

	public List<String> getIdentifiers() {
		return Arrays.asList(identifiers) ;
	}

	public List<String> getVariableNames() {
		return Arrays.asList(variableNames) ;
	}
	
	public void setInnerJoins(String[] innerJoins) {
		this.innerJoins = innerJoins;
	}

	public void setLeftJoins(String[] leftJoins) {
		this.leftJoins = leftJoins;
	}

	public void setRightJoins(String[] rightJoins) {
		this.rightJoins = rightJoins;
	}

	public void setFullJoins(String[] fullJoins) {
		this.fullJoins = fullJoins;
	}
	
	private String processWhereQuery(String whereClause) {
		String ret = whereClause;
		if (!Util.isEmpty(ret)) {
			ret = ret.replaceAll("@@valueDate@@", Util.date2SQLString(this.valueDate == null ? JDate.getNow() : JDate.valueOf(this.valueDate)));	
		}
		return ret;
	}

	public String getSqlQuery() {
		this.where = processWhereQuery(where);
		StringBuilder query = new StringBuilder();
		if (selectDistinct!=null&&selectDistinct.length()>0)
			query.append("select distinct ").append(selectDistinct).append(" ");
		else
			query.append("select ").append(select).append(" ");
		query.append("from ").append(from).append(" ");
		if (innerJoins!=null&&innerJoins.length>0) {
			for (String join : innerJoins) {
				query.append("inner join ").append(join).append(" ");
			}
		}
		if (leftJoins!=null&&leftJoins.length>0) {
			for (String join : leftJoins) {
				query.append("left join ").append(join).append(" ");
			}
		}
		if (rightJoins!=null&&rightJoins.length>0) {
			for (String join : rightJoins) {
				query.append("right join ").append(join).append(" ");
			}
		}
		if (fullJoins!=null&&fullJoins.length>0) {
			for (String join : fullJoins) {
				query.append("full join ").append(join).append(" ");
			}
		}
		if (where!=null&&where.length()>0) 
			query.append("where ").append(where).append(" ");
		if (orderby!=null&&orderby.length()>0)
			query.append("order by ").append(orderby).append(" ");
		if (groupby!=null&&groupby.length()>0)
			query.append("group by ").append(groupby).append(" ");
		return query.toString().replaceAll("@@prefix@@", prefixSchema).toUpperCase();
	}

}
