package lu.sgbt.dbcompare;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lu.sgbt.dbcompare.config.ComparatorConfig;
import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.DbConnectionCfg;
import lu.sgbt.dbcompare.config.QueryItem;
import lu.sgbt.dbcompare.config.Scenario;
import lu.sgbt.dbcompare.config.SqlQuery;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ComparatorUtils {

	public static String getRowAsUniqueString(String rowDatas[]) {
		String result = "";
		if (rowDatas != null) {
			for (String str : rowDatas) {
				result += str + " ";
			}
		}
		return result;
	}

	public static String getRowAsUniqueString(int row, Vector<String[]> data) {
		String result = "";
		String rowDatas[] = data.get(row);
		if (rowDatas != null) {
			for (String str : rowDatas) {
				result += str + " ";
			}
		}
		return result;
	}

	public static ComparatorConfig getConfig(File xmlFile, Date valueDate) {
		String title = "";
		Scenario defaultScenario = null;
		List<DbConnectionCfg> connections = new ArrayList<DbConnectionCfg>();
		List<CompareScenarioConfig> compareSet = new ArrayList<CompareScenarioConfig>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			title = getTagValue("title", doc.getDocumentElement());
			
			NodeList databaseNode = doc.getElementsByTagName("database");	
			for (int temp = 0; temp < databaseNode.getLength(); temp++) {
				Node nNode = databaseNode.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String name = getTagValue("name", eElement);
					String url = getTagValue("url", eElement);
					String user = getTagValue("user", eElement);
					String pwd = getTagValue("pwd", eElement);
					String driver = getTagValue("driver", eElement);
					String prefixSchemaStr = getTagValue("prefix-schema", eElement);
					String[] prefixes = null;
					if(prefixSchemaStr!=null)
						prefixes = prefixSchemaStr.split("=");
					String active = getTagValue("active", eElement);
					connections.add(new DbConnectionCfg(name, url, user, pwd, driver, (prefixes!=null&&prefixes.length==2)?prefixes[1]:null, Boolean.valueOf(active)));
				}
			}
			
			NodeList defaultScenarioNode = doc.getElementsByTagName("default-scenario");
			Element element = (Element) defaultScenarioNode.item(0);
			String defaultType = getTagValue("type", element);
			String defaultReference = getTagValue("reference", element);
			String defaultTarget = getTagValue("target", element);
			String defaultConnection = getTagValue("connection", element);
			defaultScenario = new Scenario(defaultType, defaultReference, defaultTarget, defaultConnection);
			
			NodeList compareScenarioNode = doc.getElementsByTagName("compare");
			for (int temp = 0; temp < compareScenarioNode.getLength(); temp++) {
				Node primaryNode = compareScenarioNode.item(temp);
				NodeList scenarioNode = ((Element)primaryNode).getElementsByTagName("scenario");
				Scenario scenario = null;
				if (scenarioNode!=null) {
					Element scenarioElement = (Element) scenarioNode.item(0);
					if (scenarioElement!=null) {
						String type = getTagValue("type", scenarioElement);
						String reference = getTagValue("reference", scenarioElement);
						String target = getTagValue("target", scenarioElement);
						String connection = getTagValue("connection", scenarioElement);
						scenario = new Scenario(type, reference, target, connection);
					}
				}
				
				NodeList queryNode = ((Element)primaryNode).getElementsByTagName("query");
				Element queryElement = (Element) queryNode.item(0);
				String name = getTagValue("name", queryElement);
				String comparisonTypeStr = getTagValue("comparison-type", queryElement);
				String comparisonType = comparisonTypeStr != null && comparisonTypeStr.trim().length()>0 ? comparisonTypeStr.trim() : "none" ;
				String checkDuplicatesStr = getTagValue("check-duplicates", queryElement);
				boolean checkDuplicates = (checkDuplicatesStr!=null&&checkDuplicatesStr.length()>0)?Boolean.valueOf(checkDuplicatesStr):true;
				NodeList itemNode = queryElement.getElementsByTagName("item");
				Map<String, QueryItem> queryItems = new HashMap<String, QueryItem>();
				for (int j = 0; j < itemNode.getLength(); j++) {
					Node itemPrimaryNode = itemNode.item(j);
					if (itemPrimaryNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) itemPrimaryNode;
						String target = eElement.getAttribute("target");
						if (target==null || target.length()==0)
							target = "DEFAULT";
						String selectDistinct = getTagValue("select-distinct", eElement);
						String select = getTagValue("select", eElement);
						String from = getTagValue("from", eElement);
						String where = getTagValue("where", eElement);
						String[] innerJoins = getTagValues("inner-join", eElement);
						String[] leftJoins = getTagValues("left-join", eElement);
						String[] rightJoins = getTagValues("right-join", eElement);
						String[] fullJoins = getTagValues("full-join", eElement);
						String orderby = getTagValue("orderby", eElement);	
						String groupby = getTagValue("groupby", eElement);
						Map<String, String> sqlKeys = getTagValues("sql_keys", "id", eElement);
						String[] variableNames = getTagValues("variable-name", eElement);
					
						String identifierStr = getTagValue("identifiers", eElement);
						String[] identifiers = null;
						if (identifierStr!=null&&identifierStr.length()>0) {
							identifierStr = identifierStr.toUpperCase();
							// Spliting and Cleaning identifiers by removing leading and trailing spaces
							identifiers = splitAndTrim(identifierStr);
						}
						QueryItem queryItem = new QueryItem(target, selectDistinct, select, from, where, orderby, groupby, sqlKeys, identifiers, variableNames, valueDate);
						queryItem.setInnerJoins(innerJoins);
						queryItem.setLeftJoins(leftJoins);
						queryItem.setFullJoins(fullJoins);
						queryItem.setRightJoins(rightJoins);
						queryItems.put(target, queryItem);
					}
				}
				SqlQuery sqlQuery = new SqlQuery(name, checkDuplicates, comparisonType, queryItems);
				compareSet.add(new CompareScenarioConfig(scenario, sqlQuery));
			}
			
			return new ComparatorConfig(title, defaultScenario, connections, compareSet);
		} catch (Exception e) {
			Logger.getLogger(ComparatorUtils.class).error("Error while loading XML Config.", e) ;
		}

		return null;
	}

	/**
	 * This method split and trim identifiers input string<BR>
	 * To do : use a better method than using replaceAll spaces<BR>
	 * @param identifierStr the input String
	 * @return an array of String trimmed and split
	 */
	private static String[] splitAndTrim(String identifierStr) {
		return identifierStr.replaceAll(" ", "").split(",");
	}

	private static String getTagValue(String sTag, Element eElement) {
		String value = null;
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
			Node nValue = (Node) nlList.item(0);
			value = nValue.getNodeValue();
		}
		catch (NullPointerException e) {}
		return value;
	}
	
	private static String[] getTagValues(String sTag, Element eElement) {
		String[] values = null;
		try {
			NodeList plList = eElement.getElementsByTagName(sTag);
			if (plList.getLength()>0) {
				values = new String[plList.getLength()];
				for (int idx=0; idx<plList.getLength(); idx++) {
					NodeList slList = plList.item(idx).getChildNodes();
					Node nValue = (Node) slList.item(0);
					values[idx] = nValue.getNodeValue();;
				}
			}
		}
		catch (NullPointerException e) {}
		return values;
	}
	
	private static Map<String, String> getTagValues(String sTag, String attr, Element eElement) {
		Map<String, String> values = null;
		try {
			NodeList plList = eElement.getElementsByTagName(sTag);
			if (plList.getLength()>0) {
				values = new HashMap<String, String>();
				for (int idx=0; idx<plList.getLength(); idx++) {
					Node node = plList.item(idx);
					NodeList slList = plList.item(idx).getChildNodes();
					Node nValue = (Node) slList.item(0);
					Element elt = (Element)node;
					String id = elt.getAttribute("id");
					if (id==null||id.length()==0)
						id = nValue.getNodeValue();
					values.put(id, nValue.getNodeValue());
				}
			}
		}
		catch (NullPointerException e) {}
		return values;
	}

}
