package lu.sgbt.dbcompare;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import lu.sgbt.dbcompare.config.ComparatorConfig;
import lu.sgbt.dbcompare.config.CompareScenarioConfig;
import lu.sgbt.dbcompare.config.DbConnectionCfg;
import lu.sgbt.dbcompare.generator.DbInfo;
import lu.sgbt.dbcompare.job.EnvQueryJob;
import lu.sgbt.dbcompare.job.KeyQueryJob;
import lu.sgbt.dbcompare.job.QueryJob;
import lu.sgbt.dbcompare.job.VariableQueryJob;
import lu.sgbt.dbcompare.report.OutputReport;
import lu.sgbt.dbcompare.result.ComparatorResult;

import org.apache.log4j.Logger;

import com.calypso.tk.core.Util;


public class StartComparator {
	
	private static final  DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmm");
	private static final String HTML_EXTENSION = ".html";
	
	private String configFileName;
	private String resultDir;
	private ComparatorConfig configs;
	
	private FileConfigurationGenerator configGenerator;
	
	private Map<String , Connection> connections;
	private Map<String , String> prefixSchemas;
	
	private String comparisonTypes;
	private Set<String> comparisonTypeSet;
	
	private Set<String> environnments;

	/**
	 * Set of parameters of type variables<BR>
	 * e.g.<BR>
	 * <code>-Comparator:Variable:<i>variableName</i>:<i>variableValue1</i>,<i>variableValue2</i></code>
	 */
	private Set<String> variableSet;
	private Map<String, List<String>> variablesMap;
	
	private void init() throws Exception {
		configGenerator = new FileConfigurationGenerator();
		List<DbInfo> dbInfos = configGenerator.buildEnvDbInfoList();
		if (!Util.isEmpty(dbInfos)) {
			environnments = new HashSet<String>();
			for (DbInfo info : dbInfos) {
				environnments.add(info.getDbName());
			}
		}
		else
			throw new Exception("No db information found in " + FileConfigurationGenerator.DB_INFO_FILE);
	}
	
	private void initDbCompare(File xmlFileConfig, String configFileName, String resultDir, String comparisonTypes, Set<String> variableSet, Date valueDate) {
		this.configFileName = configFileName;
		this.resultDir = resultDir;
		this.comparisonTypes = comparisonTypes;
		this.variableSet = variableSet ;
		this.configs = ComparatorUtils.getConfig(xmlFileConfig, valueDate);
		initDbConnection();
		initComparisonTypes();
		initVariables();
	}
	
	private void initDbConnection() {
		if (configs!=null) {
			connections = new HashMap<String, Connection>();
			prefixSchemas = new HashMap<String, String>();
			for (DbConnectionCfg cfg : configs.getConnections()) {
				String url = cfg.getUrl();
				String user = cfg.getUser();
				String password = cfg.getPassword();
				String driver = cfg.getDriver();
				if (cfg.isActive()) {
					try {
			            Class.forName(driver != null ? driver : "oracle.jdbc.OracleDriver");
			        } catch(ClassNotFoundException e){
			        	Logger.getLogger(StartComparator.class).error("Driver Name is unknown.", e) ;
			        }
			        Connection con = null;
			        try {
			            con = DriverManager.getConnection(url, user, password);
			            connections.put(cfg.getName(), con);
			            prefixSchemas.put(cfg.getName(), cfg.getPrefixSchema());
			        } catch(SQLException e){
			        	Logger.getLogger(StartComparator.class).error("Error while getting SQL Connection.", e) ;
			        }
				}
			}
		}
	}
	
	private void initComparisonTypes() {
		comparisonTypeSet = stringToHashSet(comparisonTypes, ",") ;
		Logger.getLogger(StartComparator.class).info("COMPARISON TYPES : "+ comparisonTypeSet) ;
	}

	/**
	 * Loops around all <code>-Comparator:Variable:<i>key</i>:<i>values</i></code> key:values defined as app parameters<BR>
	 * Extract variables keys and values and put them in a Map<BR>
	 */
	private void initVariables() {
		if (variableSet != null && variableSet.size()>0) {
			for (String currentVariables : variableSet) {
				if (currentVariables.matches("[^:]+:[^:]+")) {
					// Variables has only one ':' character and must have at least one character around it
					int idx = currentVariables.indexOf(':');
					String key = currentVariables.substring(0, idx) ;
					String values = currentVariables.substring(idx + 1, currentVariables.length()) ;
					List<String> currentValueSet = stringToList(values, ",") ;
					// Initialize Variables Map if it is not yet created
					if (variablesMap == null) {
						variablesMap = new HashMap<String, List<String>>() ;
					}
					// Check if there are already some values for this key
					List<String> oldValueSet = variablesMap.get(key) ;
					if (oldValueSet == null) {
						// Add the new Set
						variablesMap.put(key, currentValueSet) ;
					} else {
						// Update existing referenced Set
						oldValueSet.addAll(currentValueSet) ;
					}
				} else {
					Logger.getLogger(StartComparator.class).warn("Ignore current Argument because it has a bad format ["+ currentVariables +"]");
				}
			}
		}
		Logger.getLogger(StartComparator.class).info("VARIABLES MAP : "+ variablesMap) ;
	}
	
	/**
	 * Parse an input string using given delimiters and generate a Set
	 * @param inputString the input string to parse
	 * @param delimiter the delimiters
	 * @return the build set or null if input string is empty
	 */
	private Set<String> stringToHashSet(String inputString, String delimiter) {
		Set<String> result = null ;
		if (inputString != null && inputString.trim().length() > 0) {
			result = new HashSet<String>() ;
			StringTokenizer tokenizer = new StringTokenizer(inputString.trim(), delimiter) ; 
			while (tokenizer.hasMoreTokens()) {
				result.add(tokenizer.nextToken()) ;
			}
		}
		return result ;
	}
	
	/**
	 * Parse an input string using given delimiters and generate a List
	 * @param inputString the input string to parse
	 * @param delimiter the delimiters
	 * @return the build List or null if input string is empty
	 */
	private List<String> stringToList(String inputString, String delimiter) {
		List<String> result = null ;
		if (inputString != null && inputString.trim().length() > 0) {
			result = new ArrayList<String>() ;
			StringTokenizer tokenizer = new StringTokenizer(inputString.trim(), delimiter) ; 
			while (tokenizer.hasMoreTokens()) {
				result.add(tokenizer.nextToken()) ;
			}
		}
		return result ;
	}
	
	private void executeScenario() throws Exception {	
		Logger.getLogger(StartComparator.class).info("Starting Scenario Execution.") ;
		try {		
			String resDir = (!Util.isEmpty(resultDir))?resultDir:System.getProperty("home")+ File.separator + "results";
			File directory = new File(resDir);
			if (!directory.exists())
				directory.mkdirs();
			String filename = resDir + File.separator + configFileName;
			List<ComparatorResult> results = executeSqlJobs();
			StringBuilder buffer = OutputReport.outputHtml(results, configs.getTitle());
			OutputReport.writeTo(buffer, filename + "_" + DATE_FORMAT.format(new Date()) + HTML_EXTENSION);
			//OutputReport.writeToPdf(buffer, filename + "_" + DATE_FORMAT.format(new Date()) + HTML_EXTENSION);
		} finally {
			for (Connection con : connections.values()) {
				con.close();
				con = null;
			}
		}
		Logger.getLogger(StartComparator.class).info("Scenario Execution Completed.") ;
	}
	
	private List<ComparatorResult> executeSqlJobs() {
		Logger.getLogger(StartComparator.class).info("Executing SQL Jobs.") ;
		List<ComparatorResult> results = new ArrayList<ComparatorResult>();
		int i=0 ;
		int nbCompare = configs.getCompareSet().size();
		for (CompareScenarioConfig scenarioCfg : configs.getCompareSet()) {
			// Check if the current SQL Query is required or not
			// If ComparisonTypeSet is null, we allow all types
			if (comparisonTypeSet == null || comparisonTypeSet.contains(scenarioCfg.getQuery().getComparisonType())) {
				if (scenarioCfg.getScenario()==null) {
					scenarioCfg.setScenario(configs.getDefaultScenario());
				}
				QueryJob job = getJobInstance(scenarioCfg);
				try {
					Logger.getLogger(StartComparator.class).info("[" + ++i + "/" + nbCompare + "] - "+ scenarioCfg.getQuery().getName() +" - Starting Job") ;
					List<ComparatorResult> tmp = job.executeJob();
					Logger.getLogger(StartComparator.class).info("[" + i + "/" + nbCompare + "] - "+ scenarioCfg.getQuery().getName() +" - End of Job") ;
					results.addAll(tmp);
				} catch (Throwable e) {
					Logger.getLogger(StartComparator.class).error("Unexpected error while executing QueryJob.", e) ;
				}
			}
		}				
		return results;
	}
	
	private QueryJob getJobInstance(CompareScenarioConfig scenarioCfg) {
		QueryJob job = null;
		String type = scenarioCfg.getScenario().getType();
		if ("env".equalsIgnoreCase(type)) {
			job = new EnvQueryJob(scenarioCfg, connections, prefixSchemas);
		} else if ("key".equalsIgnoreCase(type)) {
			job = new KeyQueryJob(scenarioCfg, connections,prefixSchemas);
		} else if ("variable".equalsIgnoreCase(type)) {
			job = new VariableQueryJob(scenarioCfg, connections, variablesMap,prefixSchemas);
		}
		return job;
	}
	
	private List<String> getEnvToCompare() {
		List<String> envsToCompare = new ArrayList<String>();	
		System.out.print("REF environment? available : " + environnments.toString());
        Scanner keyBoardScanner = new Scanner(System.in);        
        String response = keyBoardScanner.nextLine();
        if (!Util.isDir(response))
        	response = response.toUpperCase();
        if(environnments.contains(response)) {
        	envsToCompare.add(response);
        	environnments.remove(response);
        }
        else {
        	System.out.println(response + " doesn't exist");
        	System.exit(0);
        }          
        
        System.out.print("Target environment? available : " + environnments.toString());
        keyBoardScanner = new Scanner(System.in);        
        response = keyBoardScanner.nextLine();
        if (!Util.isDir(response))
        	response = response.toUpperCase();
        if(environnments.contains(response)) {
        	envsToCompare.add(response);
        	environnments.remove(response);
        }
        else {
        	System.out.println(response + " doesn't exist");
        	System.out.println("System Exit");
        	System.exit(0);
        } 
        
        return envsToCompare;
	}
	
	private void compare(String appHome, CompareType type) {
		List<String> envsToCompare = getEnvToCompare();	
		
		Date valueDate = null;
	    if (type == CompareType.PROCESS) {
	    	System.out.print("Value date (format dd/mm/yyyy)?  : ");
	    	Scanner keyBoardScanner = new Scanner(System.in);
	    	String response = keyBoardScanner.nextLine();
	    	try {
	    		valueDate = new SimpleDateFormat("dd/MM/yyyy").parse(response);
	    	} catch (ParseException e) {
	    		System.err.println("Error occured while parsing the value date :" + e.getMessage());
	    		e.printStackTrace();
	    	}
	    }
		
		String appConfig = getCompareFile(appHome, type, envsToCompare);
		if (!Util.isEmpty(appConfig)) {
			ComparatorLogger.configureLogger(appHome, (String)envsToCompare.get(0) + "_" + (String)envsToCompare.get(1));
		    Logger.getLogger(StartComparator.class).info("********** Starting Comparator **********");
		    Logger.getLogger(StartComparator.class).info("APP HOME   : " + appHome);
		    Logger.getLogger(StartComparator.class).info("********** Starting Comparator " + appConfig + " **********");
		    String configFullPathname = appHome + File.separator + "conf" + File.separator + appConfig;
		      
		    Logger.getLogger(StartComparator.class).info("APP CONFIG : " + configFullPathname);
		    try {
		    	initDbCompare(new File(configFullPathname), appConfig, null, null, new HashSet<String>(), valueDate);
		        executeScenario();
		        Logger.getLogger(StartComparator.class).info("********** Comparator " + appConfig + " finished **********");
		    } catch (Exception e) {
		    	Logger.getLogger(StartComparator.class).error("********** Stopping Comparator " + appConfig + " **********");
		        Logger.getLogger(StartComparator.class).error("Unexpected error while running Scenario " + appConfig, e);
		    }
		      
		    Logger.getLogger(StartComparator.class).info("********** Comparator Completed **********");
        }
        else {
        	System.out.println("Can't find comparison file associated to " + envsToCompare.toString());
        	System.exit(0);
        }
	}
	
	private String getCompareFile(String appHome, CompareType type, List<String> envsToCompare) {
		String res = "";
		File confDir = new File(appHome + File.separator + "conf");
		List<String> envCombinations = new ArrayList<String>();
		envCombinations.add(envsToCompare.get(0)+"_"+envsToCompare.get(1));
		envCombinations.add(envsToCompare.get(1)+"_"+envsToCompare.get(0));		
		if(confDir.exists()) {
			List<String> filenames = Arrays.asList(confDir.list());
			for (String name : filenames) {
				for (String envs : envCombinations) {
					if (name.contains(envs)&&name.contains(type.getValue())) {
						res = name;
			            break;
					}
				}
			}
		}			
		return res;
	}
	
	private void generateConfig() {
		try {
			configGenerator.process();
		} catch (IOException e) {
			Logger.getLogger(StartComparator.class).error("Failed to generate files.", e) ;
		}
	}
	
	private void processAction(String appHome, CompareType type) {		
    	long start = System.currentTimeMillis();
    	compare(appHome, type);
        long duration = (System.currentTimeMillis() - start)/1000;
        System.out.println("Compare completed in " + duration + " seconds");
	}
	
	static public String getOption(String args[], String opt) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals(opt)
                    && (i < args.length - 1))
                return args[i + 1];
        return null;
    }	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// home property check
		List<String> errMsgs = new ArrayList<String>();
		String appHome = System.getProperty("home");
		if (appHome==null || appHome.length()==0) {
			errMsgs.add("No Application Home has been defined, set the jvm arg -Dhome");
		}
		
		if (errMsgs.size()>0) {
			for (String msg : errMsgs) {
				System.out.println(msg);
			}
			System.exit(0);
		}
		
		StartComparator comparator = new StartComparator();
		try {
			comparator.init();
			comparator.processAction(appHome, CompareType.valueOf(args[0]));			
		} catch (Exception e) {
			System.err.println("Error occured while initializing DbCompare : " + e.getMessage());
		}
	}

}
