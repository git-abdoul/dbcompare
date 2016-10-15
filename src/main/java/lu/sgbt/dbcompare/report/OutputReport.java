package lu.sgbt.dbcompare.report;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import lu.sgbt.dbcompare.result.ComparatorResult;
import lu.sgbt.dbcompare.result.ComparatorResult.DiffStat;
import lu.sgbt.dbcompare.result.ComparatorResult.MisMatchValue;

import org.apache.log4j.Logger;

public class OutputReport {
	
	private static final String COLUMN_STYLE = "style='border: 1px solid black;'";
	private static final String HEADER_COLUMN_STYLE = "style='background-color:gray;text-align:center;border: 1px solid black;'";
	private static final String TABLE_HEADER_STYLE = "style='table-layout: fixed; border: 1; word-wrap: break-word; overflow: hidden; width: 100%; border-collapse: collapse;'";
	
	public static void writeTo(StringBuilder feed, String outputFilename) throws IOException {
		Logger.getLogger(OutputReport.class).info("Generating Output Report File. ["+outputFilename+"]") ;
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(outputFilename));
			out.write(feed.toString());						
		} catch (Throwable e) {
        	Logger.getLogger(OutputReport.class).error("Unexpected error while writing output.", e) ;
		} finally {
			if (out!=null) {
				out.close();
			}
		}
	}
	
//	public static void writeToPdf(StringBuilder feed, String outputFilename) throws IOException {
//		writeTo(feed, outputFilename) ;
//		String pdfFileName = outputFilename.substring(0, outputFilename.lastIndexOf(".")) + ".pdf";
//		Logger.getLogger(OutputReport.class).info("Converting to PDF File. ["+pdfFileName+"]") ;
//		Document pdfDocument = new Document();
//        Reader htmlreader = new BufferedReader(new InputStreamReader(new FileInputStream(outputFilename)));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//			PdfWriter.getInstance(pdfDocument, baos);
//			pdfDocument.open();
//	        StyleSheet styles = new StyleSheet();
//	        styles.loadTagStyle("body", "font", "Bitstream Vera Sans");
//	        ArrayList<?> arrayElementList = HTMLWorker.parseToList(htmlreader, styles);
//	        for (int i = 0; i < arrayElementList.size(); ++i) {
//	            Element e = (Element) arrayElementList.get(i);
//	            pdfDocument.add(e);
//	        }
//	        pdfDocument.close();
//		} catch (DocumentException e1) {
//        	Logger.getLogger(OutputReport.class).error("Failed to get PDF Writer or to add Text element.", e1) ;
//		}
//        
//        byte[] bs = baos.toByteArray();
//        String pdfBase64 = Base64.encodeBytes(bs); //output
//        File pdfFile = new File(pdfFileName);
//        FileOutputStream fout = new FileOutputStream(pdfFile);
//        fout.write(bs);
//        fout.close();
//	}
	
	public static StringBuilder outputHtml(List<ComparatorResult> comparatorResults, String title) {		
		Logger.getLogger(OutputReport.class).info("Generating HTML Outputs.") ;
		String header = "<html>" + 
						"<style>" + 
						"BODY {font-weight: normal;font-family: verdana;font-size: 9pt;}\n" +
						"TH {font-weight: bold;font-family: verdana;font-size: 9pt;}\n" +
						"TD {font-weight: normal;font-family: verdana;font-size: 9pt;}\n" +
						"table {table-layout:fixed;overflow:hidden;word-wrap:break-word;}\n" +
						"</style>" + 
						"<body>";			
		StringBuilder result = new StringBuilder();
		result.append(header);
		result.append("<br/>");
		result.append("<center><h1><b><a name='top'>" + title + " </a></b></h1></center>");
		result.append("<br/>");
		result.append("<table "+ TABLE_HEADER_STYLE+">");
		result.append("<tr>");
		result.append("<th width='250px' "+ HEADER_COLUMN_STYLE +">Name</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">Configs</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">Diffs</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">Matched</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">Mismatched</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">Unmatched</th>");
		result.append("<th "+ HEADER_COLUMN_STYLE +">duplicates</th>");
		result.append("</tr>");
		for (ComparatorResult comparatorResult : comparatorResults) {
			String queryName = comparatorResult.getTitle();
			String resultColor = (comparatorResult.isOk())? "green" : "red";
			DiffStat refStat = comparatorResult.getReferenceStat();
			DiffStat tarStat = comparatorResult.getTargetStat();
			result.append("<tr>");
			result.append("<td align='left' style='border: 1px solid black;background-color:"+resultColor+";font-weight:bold;table-layout:fixed;'><a href='#"+queryName+"'>"+queryName+"</a></td>");
			result.append("<td "+ COLUMN_STYLE +"> [" + refStat.getName() + ":" + refStat.getRowCount()+"][" + tarStat.getName() + ":" + tarStat.getRowCount() + "]</td>");
			result.append("<td "+ COLUMN_STYLE +">" + comparatorResult.getDiffRowsCount() + "</td>");
			result.append("<td "+ COLUMN_STYLE +">" + comparatorResult.getMatchedRowCount() + "</td>");
			result.append("<td "+ COLUMN_STYLE +">" + comparatorResult.getMismatchedValues().size() + "</td>");
			result.append("<td "+ COLUMN_STYLE +"> [" + refStat.getName() + ":" + refStat.getAdditionals().size()+"][" + tarStat.getName() + ":" + tarStat.getAdditionals().size() + "]</td>");
			result.append("<td "+ COLUMN_STYLE +"> [" + refStat.getName() + ":" + refStat.getDuplicates().size()+"][" + tarStat.getName() + ":" + tarStat.getDuplicates().size() + "]</td>");
			result.append("</tr>");
		}
		result.append("</table>");
		result.append("<br/><br/>");
		
		for (ComparatorResult comparatorResult : comparatorResults) {
			result.append("<table "+ TABLE_HEADER_STYLE+">");
			result.append("<tr>");
			result.append("<td style='border: 1px solid black;'>");
			result.append(outputHtmlUnit(comparatorResult));
			result.append("<a href='#top'>Back to the top</a>");
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");			
			result.append("<br/><br/>");
		}
		result.append("</body></html>");
		return result;
	}
	
	private static StringBuilder outputHtmlUnit(ComparatorResult comparatorResult) {
		DiffStat refStat = comparatorResult.getReferenceStat();
		DiffStat tarStat = comparatorResult.getTargetStat();
		StringBuilder result = new StringBuilder();
		result.append("<div style='text-align: left;'><b><a name='"+comparatorResult.getTitle()+"'>"+ comparatorResult.getTitle() +"</a></b> Summary:</div>");
		result.append("<div style='text-align: left;'>"+ refStat.getName() +" Config Count : "+ String.valueOf(refStat.getRowCount()) +"</div>");
		result.append("<div style='text-align: left;'>"+ tarStat.getName() +" Config Count : "+ String.valueOf(tarStat.getRowCount()) +"</div>");
		result.append("<div style='text-align: left;'>Config Diff :"+ String.valueOf(comparatorResult.getDiffRowsCount()) +"</div>");
		result.append("<div style='text-align: left;'>Matched Config :"+ String.valueOf(comparatorResult.getMatchedRowCount()) +"</div>");
		result.append("<div style='text-align: left;'>Mismatched Config :"+ String.valueOf(comparatorResult.getMismatchedValues().size()) +"</div>");
		result.append("<div style='text-align: left;'>"+ refStat.getName() +" contains "+ String.valueOf(refStat.getDuplicates().size()) +" Duplicated Config(s)</div>");
		result.append("<div style='text-align: left;'>"+ tarStat.getName() +" contains "+ String.valueOf(tarStat.getDuplicates().size()) +" Duplicated Config(s)</div>");
		result.append("<div style='text-align: left;'>"+ String.valueOf(refStat.getAdditionals().size()) +" Config(s) present in "+ refStat.getName() +" and not in "+ tarStat.getName() +"</div>");
		result.append("<div style='text-align: left;'>"+ String.valueOf(tarStat.getAdditionals().size()) +" Config(s) present in "+ tarStat.getName() +" and not in "+ refStat.getName() +"</div>");
		String nameHeader = "Name <br/>" + "(" + comparatorResult.getIdentifier() + ")";
		if (comparatorResult.getMismatchedValues().size() > 0) {
			result.append("<br/>");
			result.append("<div style='text-align: left;'><b><u>Mismatched Configs between "+refStat.getName()+" and " +tarStat.getName()+ "</u></b></div>").append("<br/>");
			result.append("<table "+ TABLE_HEADER_STYLE+">");
			result.append("<tr>");						
			result.append("<th width='55%' "+ HEADER_COLUMN_STYLE +">"+nameHeader+"</th>");
			result.append("<th width='15%' "+ HEADER_COLUMN_STYLE +">Fields</th>");
			result.append("<th width='15%' "+ HEADER_COLUMN_STYLE +">" + refStat.getName() + "</th>");
			result.append("<th width='15%' "+ HEADER_COLUMN_STYLE +">" + tarStat.getName() + "</th>");
			result.append("</tr>");
			Map<String, List<MisMatchValue>> misMatchValues = comparatorResult.getMismatchedValues();			
			for (String identifer : misMatchValues.keySet()) {
				int i = 0;
				List<MisMatchValue> values = misMatchValues.get(identifer);				
				for (MisMatchValue value : values) {
					result.append("<tr>");
					if (i==0)
						result.append("<td align='left' rowspan='"+values.size()+"' "+ COLUMN_STYLE +">" + identifer + "</td>");
					result.append("<td "+ COLUMN_STYLE +">" + value.getName() + "</td>");
					result.append("<td "+ COLUMN_STYLE +">" + value.getRefValue() + "</td>");
					result.append("<td "+ COLUMN_STYLE +">" + value.getTarValue() + "</td>");
					result.append("</tr>");
					i++;
				}
			}			
			result.append("</table>");
			result.append("<br/>");
		}
		
		result.append("<br/>");
		
		if (refStat.getAdditionals().size()>0||tarStat.getAdditionals().size()>0) {			
			result.append("<table "+ TABLE_HEADER_STYLE+">");
			result.append("<tr valign='top'>");
			result.append("<td>");
			result.append("<div style='text-align: left;'><b><u>"+refStat.getAdditionals().size()+" Configs present in "+refStat.getName()+" and not in " +tarStat.getName()+ "</u></b></div>").append("<br/>");
			if (refStat.getAdditionals().size()>0) {
				result.append("<table "+ TABLE_HEADER_STYLE+">");
				result.append("<tr>");
				result.append("<th "+ HEADER_COLUMN_STYLE +">"+nameHeader+"</th>");
				result.append("</tr>");
				for (String identifer : refStat.getAdditionals()) {
					result.append("<tr>");
					result.append("<td align='left' "+ COLUMN_STYLE +">" + identifer + "</td>");
					result.append("</tr>");
				}			
				result.append("</table>");
			}
			result.append("</td>");
			result.append("<td>");
			result.append("<div style='text-align: left;'><b><u>"+tarStat.getAdditionals().size()+" Configs present in "+tarStat.getName()+" and not in " +refStat.getName()+ "</u></b></div>").append("<br/>");
			if (tarStat.getAdditionals().size()>0) {
				result.append("<table "+ TABLE_HEADER_STYLE+">");
				result.append("<tr>");
				result.append("<th "+ HEADER_COLUMN_STYLE +">"+nameHeader+"</th>");
				result.append("</tr>");
				for (String identifer : tarStat.getAdditionals()) {
					result.append("<tr>");
					result.append("<td align='left' "+ COLUMN_STYLE +">" + identifer + "</td>");
					result.append("</tr>");
				}			
				result.append("</table>");
			}
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");	
			result.append("<br/>");
		}
		
		result.append("<br/>");
		
		if (refStat.getDuplicates().size()>0||tarStat.getDuplicates().size()>0) {			
			result.append("<table "+ TABLE_HEADER_STYLE+">");
			result.append("<tr valign='top'>");
			result.append("<td>");
			result.append("<div style='text-align: left;'><b><u>"+refStat.getDuplicates().size()+" duplicates present in "+refStat.getName()+ "</u></b></div>").append("<br/>");
			if (refStat.getDuplicates().size()>0) {
				result.append("<table "+ TABLE_HEADER_STYLE+">");
				result.append("<tr>");
				result.append("<th "+ HEADER_COLUMN_STYLE +">"+nameHeader+"</th>");
				result.append("</tr>");
				for (String identifer : refStat.getDuplicates()) {
					result.append("<tr>");
					result.append("<td align='left' "+ COLUMN_STYLE +">" + identifer + "</td>");
					result.append("</tr>");
				}			
				result.append("</table>");
			}
			result.append("</td>");
			result.append("<td>");
			result.append("<div style='text-align: left;'><b><u>"+tarStat.getDuplicates().size()+" duplicates present in "+tarStat.getName() + "</u></b></div>").append("<br/>");
			if (tarStat.getDuplicates().size()>0) {
				result.append("<table "+ TABLE_HEADER_STYLE+">");
				result.append("<tr>");
				result.append("<th "+ HEADER_COLUMN_STYLE +">"+nameHeader+"</th>");
				result.append("</tr>");
				for (String identifer : tarStat.getDuplicates()) {
					result.append("<tr>");
					result.append("<td align='left' "+ COLUMN_STYLE +">" + identifer + "</td>");
					result.append("</tr>");
				}			
				result.append("</table>");
			}
			result.append("</td>");
			result.append("</tr>");
			result.append("</table>");	
		}
		return result;
	}
}
