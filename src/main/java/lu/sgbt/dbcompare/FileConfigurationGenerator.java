package lu.sgbt.dbcompare;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import lu.sgbt.dbcompare.generator.AbstractParameter;
import lu.sgbt.dbcompare.generator.BufferedReaderProcessor;
import lu.sgbt.dbcompare.generator.DbInfo;
import lu.sgbt.dbcompare.generator.EnvLineParser;
import lu.sgbt.dbcompare.generator.InOutInterface;

import org.apache.log4j.Logger;

/**
 * Generate Comparator Configuration Files from a Template
 * 
 * 
 */
public class FileConfigurationGenerator {
	public static final String DB_INFO_FILE = "/lu/sgbt/dbcompare/environments.txt";
	/** Output File Name Pattern */
	private static final String SETUP_FILE_NAME_PATTERN = "%s_%s_comparison_setup.xml";
	/** Output File Name Pattern */
	private static final String PROCESS_FILE_NAME_PATTERN = "%s_%s_comparison_process.xml";
	/** Comparison XML Template for Env XXX VS YYY */
	private static final String ENVXXX_ENVYYY_COMPARISON_SETUP_XML = "/lu/sgbt/dbcompare/ENVXXX_ENVYYY_comparison_setup.xml";
	/** Comparison XML Template for Env XXX VS YYY */
	private static final String ENVXXX_ENVYYY_COMPARISON_PROCESS_XML = "/lu/sgbt/dbcompare/ENVXXX_ENVYYY_comparison_process.xml";	

	/** Local Logger */
	private static final Logger LOG = Logger.getLogger(FileConfigurationGenerator.class) ;

	/**
	 * Processing : Generating Configuration Files<BR>
	 * This method can only be called by a Starter in the same <BR>
	 * @throws IOException if any error occurs
	 */
	public void process() throws IOException {
		// Load Environnement Names
		List<DbInfo> dbInfoList = buildEnvDbInfoList();
		generateConfigs(ENVXXX_ENVYYY_COMPARISON_SETUP_XML, dbInfoList, SETUP_FILE_NAME_PATTERN);
		generateConfigs(ENVXXX_ENVYYY_COMPARISON_PROCESS_XML, dbInfoList, PROCESS_FILE_NAME_PATTERN);
	}
	
	private void generateConfigs(String filePath, List<DbInfo> dbInfoList, String filePattern) throws IOException {
		// Loading XXX VS YYY Template
		String xxxYyy = InOutInterface.getInstance().readResource(filePath) ;
		
		// For each defined Env
		for (int i= 0 ; i < dbInfoList.size(); i++) {
			for (int j=i+1 ; j < dbInfoList.size() ; j++) {
				// Retrieves DB Info
				DbInfo infoXxx = dbInfoList.get(i) ;
				DbInfo infoYyy = dbInfoList.get(j) ;
				// Generate File from Template
				String result = generateConfiguration(xxxYyy, infoXxx, infoYyy);
				// Generate File Name 
				String fileName =  String.format(filePattern, infoXxx.getDbName(), infoYyy.getDbName());
				// Write output file
				InOutInterface.getInstance().writeFile(fileName, result);
				LOG.info(String.format("File [%s] writed !", fileName));
			}
		}
	}

	/**
	 * Build Environnement DataBase Properties List
	 * @return
	 * @throws IOException
	 */
	public List<DbInfo> buildEnvDbInfoList() throws IOException {
		InOutInterface inOutInterface = InOutInterface.getInstance() ;
		BufferedReader bufferedReader = inOutInterface.buildBufferedReader(DB_INFO_FILE);
		EnvLineParser lineParser = new EnvLineParser();
		AbstractParameter parameter = BufferedReaderProcessor.processBufferedReader(bufferedReader, lineParser) ;
		bufferedReader.close() ;
		return lineParser.transformOutput(parameter).getDbInfoList();
	}

	/**
	 * @param xxxProd Env XXX vs PROD Template
	 * @param xxxYyy Env XXX vs YYY Template
	 * @param envXxx DB Info for Env XXX
	 * @param envYyy DB Info for Env YYY
	 * @return 
	 */
	private String generateConfiguration(String xxxYyy, DbInfo envXxx, DbInfo envYyy) {
		LOG.info(String.format("Generating %s VS %s", envXxx.getDbName(), envYyy.getDbName()) ) ;
		// Choose Template to use
		String result = populateXxxYyy(xxxYyy, envXxx, envYyy);
		return result;
	}

	/**
	 * Populate XXX VS YYY Env template
	 * @param xxxYyy
	 * @param env1
	 * @param env2
	 * @return
	 */
	private String populateXxxYyy(String xxxYyy, DbInfo env1, DbInfo env2) {
		String result;
		String tempTemplate = xxxYyy; 
		tempTemplate = tempTemplate.replaceAll("@@ENVXXX@@", env1.getDbName()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVXXX.DBURL@@", env1.getDbUrl()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVXXX.DBUSER@@", env1.getDbUser()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVXXX.DBPWD@@",  env1.getDbPwd()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVXXX.DBPREFIXSCHEMA@@",  env1.getDbPrefixSchema()) ;
		// Env2 Update
		tempTemplate = tempTemplate.replaceAll("@@ENVYYY@@", env2.getDbName()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVYYY.DBURL@@", env2.getDbUrl()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVYYY.DBUSER@@", env2.getDbUser()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVYYY.DBPWD@@",  env2.getDbPwd()) ;
		tempTemplate = tempTemplate.replaceAll("@@ENVYYY.DBPREFIXSCHEMA@@",  env2.getDbPrefixSchema()) ;
		result = tempTemplate;
		return result;
	}

}
