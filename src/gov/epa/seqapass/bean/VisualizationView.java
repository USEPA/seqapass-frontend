package gov.epa.seqapass.bean;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Base64.Decoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
//import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.google.gson.Gson;

import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;
import gov.epa.seqapass.common.SpeciesNameType;
import gov.epa.seqapass.common.TaxEcos;
import gov.epa.seqapass.controller.ReportController;
import gov.epa.seqapass.model.BoxPlotSettings;
import gov.epa.seqapass.model.HeatMapSettings;
import gov.epa.seqapass.model.RiskAssessorLevel2Group;

@ManagedBean
@SessionScoped
public class VisualizationView {

	private ReportView reportView;
	private String levelOneVizPage;
	private String levelOneVizPageHeader;
	private String levelTwoVizPage;
	private String levelTwoVizPageHeader;
	private String levelThreeVizPage;
	private String levelThreeVizPageHeader;

	// level 1 box plot variables
	private List<String> boxPlotTaxGroups;
	private List<String> selectedBoxPlotTaxGroups;

	private Map<Integer, String> boxPlotSpecies;
	private List<String> boxPlotSpeciesCommonNames;
	private List<String> boxPlotSpeciesScientificNames;
	private List<String> boxPlotSpeciesNames;
	private List<String> selectedBoxPlotSpecies;

	private boxPlotSpeciesNameType boxPlotSpeciesOption1;
	private boxPlotSpeciesNameType boxPlotSpeciesOption2;
	
	private HeatmapReportType hmReportType;

	private boolean boxPlotKeepCommonSymbols1;
	private boolean boxPlotKeepCommonSymbols2;

	private boolean levelOneBoxPlotOrtholog;
	private boolean levelOneBoxPlotEndangered;
	private boolean levelOneBoxPlotThreatened;
	private boolean levelOneBoxPlotModelOrganisms;
	private String levelOneBoxPlotJSON;
	private String levelOneBoxPlotTaxGroupsJSON;

	// level 2 box plot variables
	private List<String> boxPlotTaxGroups2;
	private List<String> selectedBoxPlotTaxGroups2;

	private Map<Integer, String> boxPlotSpecies2;
	private List<String> boxPlotSpeciesCommonNames2;
	private List<String> boxPlotSpeciesScientificNames2;
	private List<String> boxPlotSpeciesNames2;
	private List<String> selectedBoxPlotSpecies2;

	private boolean levelTwoBoxPlotOrtholog;
	private boolean levelTwoBoxPlotEndangered;
	private boolean levelTwoBoxPlotThreatened;
	private boolean levelTwoBoxPlotModelOrganisms;
	private String levelTwoBoxPlotJSON;
	private String levelTwoBoxPlotTaxGroupsJSON;

	private List<TaxEcos> endangeredSpecies; // holds endangered species taxids
	private List<TaxEcos> threatenedSpecies; // holds threatened species taxids
	private List<Integer> modelOrganisms;
	private String endangeredSpeciesJSON;
	private String threatenedSpeciesJSON;
	private String modelOrganismsJSON;

	private String dataTableTaxGroup1;
	private Double taxMean1;
	private Double taxMedian1;
	private String taxSusceptible1;
	private String dataTableTaxGroup2;
	private Double taxMean2;
	private Double taxMedian2;
	private String taxSusceptible2;

	private List<LevelOneReportRow> taxTable1 = new ArrayList<LevelOneReportRow>();
	private List<LevelTwoReportRow> taxTable2 = new ArrayList<LevelTwoReportRow>();

	private boolean orthologsAvail1;
	private boolean orthologsAvail2;
	private boolean endangeredsAvail1;
	private boolean endangeredsAvail2;
	private boolean modelsAvail1;
	private boolean modelsAvail2;
	private boolean threatenedsAvail1;
	private boolean threatenedsAvail2;

	private boolean zoomPanOn1;
	private boolean zoomPanOn2;
	private int boxWidth1;
	private int boxWidth2;
	private int zoomVal1;
	private int zoomVal2;

	private int boxWidthDefault1;
	private int boxWidthDefault2;
	private int zoomValDefault;

	private Integer imageWidth1;
	private Integer imageHeight1;
	private double imageAspectRatio1; // width/height*18
	private Integer imageWidth2;
	private Integer imageHeight2;
	private double imageAspectRatio2;
	private Integer imageWidth3;
	private Integer imageHeight3;
	private double imageAspectRatio3;

	private String svgSource1;
	private String svgSource2;
	private String svgSource3;
	private StreamedContent svgBinarySource3;

	private ImageTypeEnum imageType1;
	private ImageTypeEnum imageType2;
	private ImageTypeEnum imageType3;
	
	private boolean level1SVGPushWarning;
	private boolean level1SVGDiffers;
	private boolean level2SVGPushWarning;
	private boolean level2SVGDiffers;
	private boolean level3SVGPushWarning;
	private boolean level3SVGDiffers;
	
	private DualListModel<String> levelThreeHeatmapPickList;
	private SpeciesNameType heatmapSpeciesNameType;
	private boolean levelThreeBoxPlotOrtholog;
	private boolean levelThreeBoxPlotThreatened;
	private boolean levelThreeBoxPlotEndangered;
	private boolean levelThreeBoxPlotModelOrganisms;
	private boolean orthologsAvail3;
	private boolean endangeredsAvail3;
	private boolean modelsAvail3;
	private boolean threatenedsAvail3;
	private boolean levelThreeHeatmapSusceptibility;
	private boolean levelThreeHeatmapSusceptibilityText;
	private boolean levelThreeHeatmapAlignPrediction;
	private boolean levelThreeHeatmapAminoAcid;
	private boolean levelThreeHeatMapPosition;
	private String levelThreeHeatMapJSON;
	private String levelThreeHeatMapLevel1ReportJSON;
	

	private boolean resetBoxPlot1 = false;
	private boolean resetBoxPlot2 = false;
	
	private String level3OptionalSelection;

	public enum ImageTypeEnum {

		SVG(0), PNG(1), JPG(2);
		private int value;

		public int getValue() {
			return value;
		}

		private ImageTypeEnum(int value) {
			this.value = value;
		}

	}

	public enum VizTypeEnum {

		INFO(0), BOXPLOT(1), HEATMAP(2);
		private int value;

		public int getValue() {
			return value;
		}

		private VizTypeEnum(int value) {
			this.value = value;
		}

	}

	public enum boxPlotSpeciesNameType {

		COMMON(0), SCIENTIFIC(1);
		private int value;

		public int getValue() {
			return value;
		}

		private boxPlotSpeciesNameType(int value) {
			this.value = value;
		}

	}
	
	public enum HeatmapReportType {
		SIMPLE(0), FULL(1);
		private int value;

		public int getValue() {
			return value;
		}

		private HeatmapReportType(int value) {
			this.value = value;
		}
	}

	@PostConstruct
	public void init() {
		System.out.println("Inside VisualizationView.init");
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		this.reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver()
				.getValue(elContext, null, "reportView");

		Gson gson = new Gson();

		endangeredSpecies = ReportController.getEndangered();
		this.endangeredSpeciesJSON = gson.toJson(endangeredSpecies);

		threatenedSpecies = ReportController.getThreatened();
		this.threatenedSpeciesJSON = gson.toJson(threatenedSpecies);

		modelOrganisms = ReportController.getModelOrganisms();
		this.modelOrganismsJSON = gson.toJson(modelOrganisms);

		orthologsAvail1 = true;
		endangeredsAvail1 = true;
		modelsAvail1 = true;
		orthologsAvail2 = true;
		endangeredsAvail2 = true;
		modelsAvail2 = true;
		threatenedsAvail1 = true;
		threatenedsAvail2 = true;
		orthologsAvail3 = true;
		endangeredsAvail3 = true;
		modelsAvail3 = true;
		threatenedsAvail3 = true;
		
		levelThreeHeatmapSusceptibility = true;
		levelThreeHeatmapSusceptibilityText = true;
		levelThreeHeatmapAlignPrediction = true;
		levelThreeHeatmapAminoAcid = true;
		levelThreeHeatMapPosition = true;
		
		level3OptionalSelection = "";

		boxWidthDefault1 = 0;
		boxWidthDefault2 = 0;
		zoomValDefault = 100;

		boxPlotSpeciesOption1 = boxPlotSpeciesNameType.COMMON;
		boxPlotSpeciesOption2 = boxPlotSpeciesNameType.COMMON;
		heatmapSpeciesNameType = SpeciesNameType.COMMON;
		hmReportType = HeatmapReportType.SIMPLE;

		boxPlotKeepCommonSymbols1 = false;
		boxPlotKeepCommonSymbols2 = false;
		
		level1SVGPushWarning = false;
		level1SVGDiffers = false;
		
		level2SVGPushWarning = false;
		level2SVGDiffers = true;
		
		level3SVGPushWarning = false;
		level3SVGDiffers = true;

		// imageHeight = 900;
		// imageWidth = 1600;
		setImageType1(ImageTypeEnum.SVG);
		setImageType2(ImageTypeEnum.SVG);
		setImageType3(ImageTypeEnum.SVG);

		// *******************
		// Level One Viz
		// *******************/
		resetLevelOneViz();

		// *******************
		// Level Two Viz
		// *******************/
		resetLevelTwoViz();
		
		// *******************
		// Level Two Viz
		// *******************/
		resetLevelThreeViz();

	}

	public void changeLevelOneVizPage(VizTypeEnum vizType) {
		System.out.println("calling changeVizPage");
		if (vizType == VizTypeEnum.INFO) {
			levelOneVizPage = "vizinfo12.xhtml";
			levelOneVizPageHeader = "Info";
		} else if (vizType == VizTypeEnum.BOXPLOT) {
//			if (resetBoxPlot1) {
//				resetBoxPlotJS(1);
//				resetBoxPlot1 = false;
//			}
			levelOneVizPage = "boxplot.xhtml";
			levelOneVizPageHeader = "BoxPlot";
			// testJSFunctionFromBean();
			retrieveBoxPlotTaxonomicGroups(1);
			retrieveBoxPlotSpecies(1);
			if (resetBoxPlot1) {
//				selectedBoxPlotTaxGroups = boxPlotTaxGroups;
				selectedBoxPlotTaxGroups = new ArrayList<String>(boxPlotTaxGroups);
				resetBoxPlot1 = false;
			}
			updateReportJSON(1);
			updateBoxPlotTaxGroupsJSON(1);
			checkDiffLevel1BoxPlotRAReport();
			// resetBoxPlot(1);
		} else {
			System.out.println("Could not find specified viz page.  Returning to info page.");
			levelOneVizPage = "vizinfo12.xhtml";
		}
		System.out.println("VizPage1 changed to: " + levelOneVizPage);
	}

	public void changeLevelTwoVizPage(VizTypeEnum vizType) {
		System.out.println("calling changeVizPage");
		if (vizType == VizTypeEnum.INFO) {
			levelTwoVizPage = "vizinfo12.xhtml";
			levelTwoVizPageHeader = "Info";
		} else if (vizType == VizTypeEnum.BOXPLOT) {
			System.out.println("boxplot button clicked for level 2");
//			if (resetBoxPlot2) {
//				resetBoxPlotJS(2);
//				resetBoxPlot2 = false;
//			}
			levelTwoVizPage = "boxplot2.xhtml";
			levelTwoVizPageHeader = "BoxPlot";
			retrieveBoxPlotTaxonomicGroups(2);
			retrieveBoxPlotSpecies(2);
			if (resetBoxPlot2) {
//				selectedBoxPlotTaxGroups2 = boxPlotTaxGroups2;
				selectedBoxPlotTaxGroups2 = new ArrayList<String>(boxPlotTaxGroups2);
				resetBoxPlot2 = false;
			}
			updateReportJSON(2);
			updateBoxPlotTaxGroupsJSON(2);
			checkDiffLevel2BoxPlotRAReport();
		} else {
			System.out.println("Could not find specified level two viz page.  Returning to info page.");
			levelTwoVizPage = "vizinfo12.xhtml";
		}
		System.out.println("VizPage changed to: " + levelTwoVizPage);
	}
	
	public void changeLevelThreeVizPage(VizTypeEnum vizType) {
		System.out.println("calling changeVizPage");
		if (vizType == VizTypeEnum.INFO) {
			levelThreeVizPage = "vizinfo3.xhtml";
			levelThreeVizPageHeader = "Info";
		} else if (vizType == VizTypeEnum.HEATMAP) {
			System.out.println("heatmap button clicked for level 3");
			levelThreeVizPage = "heatmap3.xhtml";
			levelThreeVizPageHeader = "Heat Map";
			updateReportJSON(3);
			checkDiffLevel3HeatmapRAReport();
		} else {
			System.out.println("Could not find specified level three viz page.  Returning to info page.");
			levelThreeVizPage = "vizinfo3.xhtml";
		}
		System.out.println("VizPage changed to: " + levelThreeVizPage);
	}

	public void reloadLevelOneVizData(boolean reset) {
		if (selectedBoxPlotSpecies != null) {
			selectedBoxPlotSpecies.clear();
			// System.out.println("selectedBoxPlotSpecies size: " +
			// selectedBoxPlotSpecies.size());
			// } else {
			// System.out.println("selectedBoxPlotSpecies is null");
		}

		// needs to be clicked twice to update Species for Legend properly.
//		if (reset) resetBoxPlotJS(1);
		resetBoxPlot1 = reset;
		clickBoxPlotButtonViaJavaScript(1, reset);
//		if (reset) resetBoxPlotJS(1);
		clickBoxPlotButtonViaJavaScript(1, reset);
	}
	
//	public void resetBoxPlotJS(int levelNumber) {
////		StringBuilder sb = new StringBuilder();
////		if (levelNumber == 1) {
////			sb.append("if (vizNamespace.viz1Window !== undefined){");
////			sb.append("console.log('DEBUG: using vizNamespace.viz1Window.sqg.resetBoxPlot1')");
////			sb.append(" vizNamespace.viz1Window.sqg.resetBoxPlot1 = true; ");
////			sb.append("}else{");
////			sb.append("console.log('DEBUG: using sqg.resetBoxPlot1')");
////			sb.append("sqg.resetBoxPlot1 = true;");
////			sb.append("};");
////		} else if (levelNumber == 2) {
////			sb.append("if (vizNamespace.viz2Window !== undefined){");
////			sb.append(" vizNamespace.viz2Window.sqg.resetBoxPlot2 = true; ");
////			sb.append("}else{");
////			sb.append("sqg.resetBoxPlot2 = true;");
////			sb.append("};");
////		}
////		PrimeFaces.current().executeScript(sb.toString());
//		
//	}


	public void reloadLevelTwoVizData(boolean reset) {
		if (selectedBoxPlotSpecies2 != null) {
			selectedBoxPlotSpecies2.clear();
			// System.out.println("selectedBoxPlotSpecies2 size: " +
			// selectedBoxPlotSpecies2.size());
			// } else {
			// System.out.println("selectedBoxPlotSpecies2 is null");
		}
//		if (reset) resetBoxPlotJS(2);
		resetBoxPlot2 = reset;
		clickBoxPlotButtonViaJavaScript(2, reset);
//		if (reset) resetBoxPlotJS(2);
		clickBoxPlotButtonViaJavaScript(2, reset);
	}
	
	public void reloadLevelThreeVizData() {
		clickBoxPlotButtonViaJavaScript(3, true);
		clickBoxPlotButtonViaJavaScript(3, true);
	}
	
//	public void firstLoadLevelOneViz() {
////		resetBoxPlot1 = true;
//		resetLevelOneViz();
//	}
//	
//	public void firstLoadLevelTwoViz() {
//		resetBoxPlot2 = true;
//		resetLevelTwoViz();
//	}

	public void resetLevelOneViz() {
		levelOneVizPage = "vizinfo12.xhtml";
		levelOneVizPageHeader = "Info";		
		// Default to all checked
		resetBoxPlot(1);
		for (LevelOneReportRow row : reportView.levelOneReport) {
			row.setEndangered(checkSpeciesStatus(row.getSpeciesTaxId(), "endangered"));
			row.setThreatened(checkSpeciesStatus(row.getSpeciesTaxId(), "threatened"));
			row.setModel(checkSpeciesStatus(row.getSpeciesTaxId(), "model"));
		}

		for (LevelOneReportRow row : reportView.levelOnePrimaryReport) {
			row.setEndangered(checkSpeciesStatus(row.getSpeciesTaxId(), "endangered"));
			row.setThreatened(checkSpeciesStatus(row.getSpeciesTaxId(), "threatened"));
			row.setModel(checkSpeciesStatus(row.getSpeciesTaxId(), "model"));
		}

		resetBoxPlotWidth(1);
		resetBoxPlotZoom(1);
		
	}

	public void resetLevelTwoViz() {
		levelTwoVizPage = "vizinfo12.xhtml";
		levelTwoVizPageHeader = "Info";
		boxPlotSpeciesOption2 = boxPlotSpeciesNameType.COMMON;
		resetBoxPlot2 = true;
		
		// Default to all checked
		resetBoxPlot(2);
		for (LevelTwoReportRow row : reportView.levelTwoReport) {
			row.setEndangered(checkSpeciesStatus(row.getSpeciesTaxId(), "endangered"));
			row.setThreatened(checkSpeciesStatus(row.getSpeciesTaxId(), "threatened"));
			row.setModel(checkSpeciesStatus(row.getSpeciesTaxId(), "model"));
		}

		for (LevelTwoReportRow row : reportView.levelTwoPrimaryReport) {
			row.setEndangered(checkSpeciesStatus(row.getSpeciesTaxId(), "endangered"));
			row.setThreatened(checkSpeciesStatus(row.getSpeciesTaxId(), "threatened"));
			row.setModel(checkSpeciesStatus(row.getSpeciesTaxId(), "model"));
		}

		resetBoxPlotWidth(2);
		resetBoxPlotZoom(2);
	}
	
	public void resetLevelThreeViz() {
		
		levelThreeVizPage = "vizinfo3.xhtml";
		levelThreeVizPageHeader = "Info";
		
		
		for (LevelThreeReportRow row: reportView.levelThreeReport){
			row.setEndangered(checkSpeciesStatus(row.getSpeciesTaxId(), "endangered"));
			row.setThreatened(checkSpeciesStatus(row.getSpeciesTaxId(), "threatened"));
			row.setModel(checkSpeciesStatus(row.getSpeciesTaxId(), "model"));
		}

		
		List<String> pickListSource = new ArrayList<String>();
		for (LevelThreeReportRow row: reportView.levelThreeReport){
			if (!pickListSource.contains(row.getTaxonomyName())){
				pickListSource.add(row.getTaxonomyName());
			}
		}
		
		levelThreeHeatmapSusceptibility = true;
		levelThreeHeatmapSusceptibilityText = true;
		levelThreeHeatmapAlignPrediction = true;
		levelThreeHeatmapAminoAcid = true;
		levelThreeHeatMapPosition = true;
		
		level3OptionalSelection = "";
		
		hmReportType = HeatmapReportType.SIMPLE;
		heatmapSpeciesNameType = SpeciesNameType.COMMON;
		
		
		List<String> pickListTarget = new ArrayList<String>();
		
		setLevelThreeHeatmapPickList(new DualListModel<String>(pickListSource, pickListTarget));
	}

	public void resetBoxPlot(int level) {
		if (level == 1) {
			boxPlotSpeciesOption1 = boxPlotSpeciesNameType.COMMON;
			retrieveBoxPlotTaxonomicGroups(level);
			retrieveBoxPlotSpecies(level);
			// this.selectedBoxPlotTaxGroups = boxPlotTaxGroups;
			if (selectedBoxPlotTaxGroups != null) {
				selectedBoxPlotTaxGroups.clear();
				selectedBoxPlotTaxGroups.addAll(boxPlotTaxGroups);
			}
			if (selectedBoxPlotSpecies != null) {
				selectedBoxPlotSpecies.clear();
			}
			this.levelOneBoxPlotOrtholog = false;
			this.levelOneBoxPlotEndangered = false;
			this.levelOneBoxPlotThreatened = false;
			this.levelOneBoxPlotModelOrganisms = false;
			updateReportJSON(1);
			updateBoxPlotTaxGroupsJSON(1);
		} else if (level == 2) {
			boxPlotSpeciesOption2 = boxPlotSpeciesNameType.COMMON;
			retrieveBoxPlotTaxonomicGroups(level);
			retrieveBoxPlotSpecies(level);
			// this.selectedBoxPlotTaxGroups2 = boxPlotTaxGroups2;
			if (selectedBoxPlotTaxGroups2 != null) {
				selectedBoxPlotTaxGroups2.clear();
			}
			if (selectedBoxPlotSpecies2 != null) {
				selectedBoxPlotSpecies2.clear();
			}
			this.levelTwoBoxPlotOrtholog = false;
			this.levelTwoBoxPlotEndangered = false;
			this.levelTwoBoxPlotThreatened = false;
			this.levelTwoBoxPlotModelOrganisms = false;
			updateReportJSON(2);
			updateBoxPlotTaxGroupsJSON(2);
		} else {
			System.out.println("Error in resetBoxPlot: invalid level");
		}

//		RequestContext context = RequestContext.getCurrentInstance();
//		context.execute("console.log('finished resetBoxPlot');");
		PrimeFaces.current().executeScript("console.log('finished resetBoxPlot');");
		
	}
	
	public void resetHeatMap() {
		
			//boxPlotSpeciesOption1 = boxPlotSpeciesNameType.COMMON;
			//retrieveBoxPlotTaxonomicGroups(level);
			//retrieveBoxPlotSpecies(level);

//			this.levelThreeBoxPlotOrtholog = false;
//			this.levelThreeBoxPlotEndangered = false;
//			this.levelThreeBoxPlotThreatened = false;
//			this.levelThreeBoxPlotModelOrganisms = false;
			updateReportJSON(3);
		
	}

	public void updateReportJSON(int level) {
		Gson gson = new Gson();
		if (level == 1) {
			if (reportView.getLevelOneReportType() == ReportTypeEnum.Primary) {
				this.levelOneBoxPlotJSON = gson.toJson(reportView.getLevelOnePrimaryReport());
			} else {
				this.levelOneBoxPlotJSON = gson.toJson(reportView.getLevelOneReport());
			}
		} else if (level == 2) {
			if (reportView.getLevelTwoReportType() == ReportTypeEnum.Primary) {
				this.levelTwoBoxPlotJSON = gson.toJson(reportView.getLevelTwoPrimaryReport());
			} else {
				this.levelTwoBoxPlotJSON = gson.toJson(reportView.getLevelTwoReport());
			}
		} else if (level == 3){
			this.levelThreeHeatMapJSON = gson.toJson(reportView.getLevelThreeReport());
			//level 3 needs the level 1 full report to match orthologs
			this.levelThreeHeatMapLevel1ReportJSON = gson.toJson(reportView.getLevelOneReport());
		} else {
			System.out.println("Error: invalid level in updateReportJSON");
		}
	}

	public void updateBoxPlotTaxGroupsJSON(int level) {
		Gson gson = new Gson();
		if (level == 1) {
			this.levelOneBoxPlotTaxGroupsJSON = gson.toJson(boxPlotTaxGroups);
		} else if (level == 2) {
			this.levelTwoBoxPlotTaxGroupsJSON = gson.toJson(boxPlotTaxGroups2);
		} else {
			System.out.println("Error: invalid level in updateBoxPlotTaxGroupsJSON");
		}
	}

	public void clickBoxPlotButtonViaJavaScript(int level, boolean fullReset) {
		StringBuilder sb = new StringBuilder();
		if (level == 1) {
			sb.append("if (vizNamespace.viz1Window !== undefined){");
			sb.append(" console.log('vizNamespace.viz1Window exists!!!');");
			sb.append(
					" var boxButton = vizNamespace.viz1Window.document.getElementById('levOneVizForm:boxPlotButton'); ");
			sb.append("console.log('b4 viz1Window resetBoxPlot1 = ' + vizNamespace.viz1Window.sqg.resetBoxPlot1);");
			if (fullReset) {
				sb.append("vizNamespace.viz1Window.sqg.resetBoxPlot1 = true;");
			} else {
				sb.append("vizNamespace.viz1Window.sqg.resetBoxPlot1 = false;");
			}
			sb.append("console.log('after viz1Window resetBoxPlot1 = ' + vizNamespace.viz1Window.sqg.resetBoxPlot1);");
			sb.append("if(boxButton !== null){ boxButton.click();};");
			sb.append("}else{");
			sb.append(" console.log('vizNamespace.viz1Window DNE!!!');");
			sb.append("var boxButton = document.getElementById('levOneVizForm:boxPlotButton');");
			sb.append("console.log('b4 sqg resetBoxPlot1 = ' + sqg.resetBoxPlot1);");
			if (fullReset) {
				sb.append("sqg.resetBoxPlot1 = true;");
			} else {
				sb.append("sqg.resetBoxPlot1 = false;");
			}
			sb.append("console.log('after sqg resetBoxPlot1 = ' + sqg.resetBoxPlot1);");
			sb.append("if(boxButton !== null){boxButton.click();};");
			sb.append("};");
		} else if (level == 2) {
			sb.append("if (vizNamespace.viz2Window !== undefined){");
			sb.append(" console.log('vizNamespace.viz2Window exists!!!');");
			sb.append(
					" var boxButton = vizNamespace.viz2Window.document.getElementById('levTwoVizForm:boxPlotButton2'); ");
			if (fullReset) {
				sb.append("vizNamespace.viz2Window.sqg.resetBoxPlot2 = true;");
			} else {
				sb.append("vizNamespace.viz2Window.sqg.resetBoxPlot2 = false;");
			}
			sb.append("if(boxButton !== null){ boxButton.click();};");
			sb.append("}else{");
			sb.append(" console.log('vizNamespace.viz2Window DNE!!!');");
			sb.append("var boxButton = document.getElementById('levTwoVizForm:boxPlotButton2');");
			if (fullReset) {
				sb.append("sqg.resetBoxPlot2 = true;");
			} else {
				sb.append("sqg.resetBoxPlot2 = false;");
			}
			sb.append("if(boxButton !== null){boxButton.click();};");
			sb.append("};");
		} else if (level == 3) {
			sb.append("if (vizNamespace.viz3Window !== undefined){");
			sb.append(" console.log('vizNamespace.viz3Window exists!!!');");
			sb.append(
					" var hmButton = vizNamespace.viz3Window.document.getElementById('levThreeVizForm:heatMapButton3'); ");
			sb.append("if(hmButton !== null){ hmButton.click();};");
			sb.append("}else{");
			sb.append(" console.log('vizNamespace.viz3Window DNE!!!');");
			sb.append("var hmButton = document.getElementById('levThreeVizForm:heatMapButton3');");
			sb.append("if(hmButton !== null){hmButton.click();};");
			sb.append("};");
		} else {
			System.out.println("Error: invalid level in clickBoxPlotButtonViaJavaScript");
			return;
		}

//		RequestContext context = RequestContext.getCurrentInstance();
//		context.execute(sb.toString());
		PrimeFaces.current().executeScript(sb.toString());
	}

	private void retrieveBoxPlotTaxonomicGroups(int level) {
		if (level == 1) {
			boxPlotTaxGroups = new ArrayList<String>(reportView.getTaxonomyGroupByPercSim());
		} else if (level == 2) {
			boxPlotTaxGroups2 = new ArrayList<String>(reportView.getTaxonomyGroup2ByPercSim());
		} else {
			System.out.println("Error in retrieveBoxPlotTaxonomicGroups: invalid level");
		}
	}

	private void retrieveBoxPlotSpecies(int level) {
		if (level == 1) {
			boxPlotSpecies = new HashMap<Integer, String>();
			boxPlotSpeciesCommonNames = new LinkedList<String>();
			boxPlotSpeciesScientificNames = new LinkedList<String>();

			List<LevelOneReportRow> theReport;
			if (reportView.getLevelOneReportType() == ReportTypeEnum.Primary) {
				theReport = new ArrayList<LevelOneReportRow>(reportView.levelOnePrimaryReport);
			} else {
				theReport = new ArrayList<LevelOneReportRow>(reportView.levelOneReport);
			}

			for (LevelOneReportRow row : theReport) {
				boxPlotSpecies.put(row.getSpeciesTaxId(), row.getCommonName() + " (" + row.getScientificName() + ")");
				if (!boxPlotSpeciesCommonNames.contains(row.getCommonName())) {
					boxPlotSpeciesCommonNames.add(row.getCommonName());
				}
				if (!boxPlotSpeciesScientificNames.contains(row.getScientificName())) {
					boxPlotSpeciesScientificNames.add(row.getScientificName());
				}
			}
			Collections.sort(boxPlotSpeciesCommonNames);
			Collections.sort(boxPlotSpeciesScientificNames);

			if (boxPlotSpeciesOption1.equals(boxPlotSpeciesNameType.COMMON)) {
				boxPlotSpeciesNames = boxPlotSpeciesCommonNames;
			} else {
				boxPlotSpeciesNames = boxPlotSpeciesScientificNames;
			}

			// What's the purpose of this null sort?
			// boxPlotSpeciesCommonNames.sort(null);
			// boxPlotSpeciesScientificNames.sort(null);
		} else if (level == 2) {
			boxPlotSpecies2 = new HashMap<Integer, String>();
			boxPlotSpeciesCommonNames2 = new LinkedList<String>();
			boxPlotSpeciesScientificNames2 = new LinkedList<String>();

			List<LevelTwoReportRow> theReport;
			if (reportView.getLevelTwoReportType() == ReportTypeEnum.Primary) {
				theReport = new ArrayList<LevelTwoReportRow>(reportView.levelTwoPrimaryReport);
			} else {
				theReport = new ArrayList<LevelTwoReportRow>(reportView.levelTwoReport);
			}

			for (LevelTwoReportRow row : theReport) {
				boxPlotSpecies2.put(row.getSpeciesTaxId(), row.getCommonName() + " (" + row.getScientificName() + ")");
				if (!boxPlotSpeciesCommonNames2.contains(row.getCommonName())) {
					boxPlotSpeciesCommonNames2.add(row.getCommonName());
				}
				if (!boxPlotSpeciesScientificNames2.contains(row.getScientificName())) {
					boxPlotSpeciesScientificNames2.add(row.getScientificName());
				}
			}
			Collections.sort(boxPlotSpeciesCommonNames2);
			Collections.sort(boxPlotSpeciesScientificNames2);

			if (boxPlotSpeciesOption2.equals(boxPlotSpeciesNameType.COMMON)) {
				boxPlotSpeciesNames2 = boxPlotSpeciesCommonNames2;
			} else {
				boxPlotSpeciesNames2 = boxPlotSpeciesScientificNames2;
			}

		} else {
			System.out.println("Error in retrieveBoxPlotSpecies: invalid level");
		}

	}

	public void levelOneBoxPlotSelectCheckbox(String checked) {
		System.out.println("inside levelOneBoxPlotSelectCheckbox");
		if (checked.equals("Ortholog")) {
			levelOneBoxPlotEndangered = false;
			levelOneBoxPlotThreatened = false;
			levelOneBoxPlotModelOrganisms = false;
		} else if (checked.equals("Endangered")) {
			levelOneBoxPlotOrtholog = false;
			levelOneBoxPlotThreatened = false;
			levelOneBoxPlotModelOrganisms = false;
		} else if (checked.equals("ModelOrganisms")) {
			levelOneBoxPlotOrtholog = false;
			levelOneBoxPlotEndangered = false;
			levelOneBoxPlotThreatened = false;
		} else if (checked.equals("Threatened")) {
			levelOneBoxPlotOrtholog = false;
			levelOneBoxPlotEndangered = false;
			levelOneBoxPlotModelOrganisms = false;
		} else {
			System.out.println("Unknown option in boxPlotSelectCheckbox");
		}
	}

	public void levelTwoBoxPlotSelectCheckbox(String checked) {
		System.out.println("inside levelTwoBoxPlotSelectCheckbox");
		if (checked.equals("Ortholog")) {
			levelTwoBoxPlotEndangered = false;
			levelTwoBoxPlotThreatened = false;
			levelTwoBoxPlotModelOrganisms = false;
		} else if (checked.equals("Endangered")) {
			levelTwoBoxPlotOrtholog = false;
			levelTwoBoxPlotThreatened = false;
			levelTwoBoxPlotModelOrganisms = false;
		} else if (checked.equals("ModelOrganisms")) {
			levelTwoBoxPlotOrtholog = false;
			levelTwoBoxPlotEndangered = false;
			levelTwoBoxPlotThreatened = false;
		} else if (checked.equals("Threatened")) {
			levelTwoBoxPlotOrtholog = false;
			levelTwoBoxPlotEndangered = false;
			levelTwoBoxPlotModelOrganisms = false;
		} else {
			System.out.println("Unknown option in boxPlotSelectCheckbox2");
		}
	}
	
	public void levelThreeOptionalSelectionCheckbox(String checked) {
		System.out.println("inside levelThreeOptionalSelectionCheckbox");
//		if (checked.equals("Ortholog")) {
//			levelThreeBoxPlotOrtholog = false;
//		} else if (checked.equals("Endangered")) {
//			levelThreeBoxPlotEndangered = false;
//		} else if (checked.equals("ModelOrganisms")) {
//			levelThreeBoxPlotModelOrganisms = false;
//		} else if (checked.equals("Threatened")) {
//			levelThreeBoxPlotThreatened = false;
//		} else {
//			System.out.println("Unknown option in levelThreeOptionalSelectionCheckbox");
//		}
//		if (checked.equals("Ortholog")) {
//			levelThreeBoxPlotEndangered = false;
//			levelThreeBoxPlotThreatened = false;
//			levelThreeBoxPlotModelOrganisms = false;
//		} else if (checked.equals("Endangered")) {
//			levelThreeBoxPlotOrtholog = false;
//			levelThreeBoxPlotThreatened = false;
//			levelThreeBoxPlotModelOrganisms = false;
//		} else if (checked.equals("ModelOrganisms")) {
//			levelThreeBoxPlotOrtholog = false;
//			levelThreeBoxPlotEndangered = false;
//			levelThreeBoxPlotThreatened = false;
//		} else if (checked.equals("Threatened")) {
//			levelThreeBoxPlotOrtholog = false;
//			levelThreeBoxPlotEndangered = false;
//			levelThreeBoxPlotModelOrganisms = false;
//		} else {
//			System.out.println("Unknown option in boxPlotSelectCheckbox3");
//		}
	}

	public void showTaxTable(int level) {
//		RequestContext context = RequestContext.getCurrentInstance();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = facesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		if (level == 1) {
			dataTableTaxGroup1 = parameterMap.get("dataTaxon");
			taxMean1 = Double.parseDouble(parameterMap.get("mean"));
			taxMedian1 = Double.parseDouble(parameterMap.get("median"));
			// System.out.println("taxName: " + dataTableTaxGroup1);
			// System.out.println("taxMean: " + taxMean1);
			// System.out.println("taxMedian1: " + taxMedian1);
			taxTable1.clear();
			List<LevelOneReportRow> allRows;
			if (reportView.getLevelOneReportType() == ReportTypeEnum.Primary) {
				allRows = reportView.getLevelOnePrimaryReport();
			} else {
				allRows = reportView.getLevelOneReport();
			}
			for (LevelOneReportRow row : allRows) {
				if (row.getTaxonomyName().equals(dataTableTaxGroup1)) {
					taxTable1.add(row);
				}
			}

			taxSusceptible1 = taxTable1.get(0).getSusceptible();

			System.out.println("taxTable1 has " + taxTable1.size() + " rows");
//			context.execute("PF('taxTableWidget').show();");
			PrimeFaces.current().executeScript("PF('taxTableWidget').show();");
		} else if (level == 2) {
			System.out.println("Inside showTaxTable for level 2");
			dataTableTaxGroup2 = parameterMap.get("dataTaxon");
			taxMean2 = Double.parseDouble(parameterMap.get("mean"));
			taxMedian2 = Double.parseDouble(parameterMap.get("median"));
			taxTable2.clear();
			List<LevelTwoReportRow> allRows;
			if (reportView.getLevelTwoReportType() == ReportTypeEnum.Primary) {
				allRows = reportView.getLevelTwoPrimaryReport();
			} else {
				allRows = reportView.getLevelTwoReport();
			}
			for (LevelTwoReportRow row : allRows) {
				if (row.getTaxonomyName().equals(dataTableTaxGroup2)) {
					taxTable2.add(row);
				}
			}
			taxSusceptible2 = taxTable2.get(0).getSusceptible();

			System.out.println("taxTable2 has " + taxTable2.size() + " rows");
//			context.execute("PF('taxTableWidget2').show();");
			PrimeFaces.current().executeScript("PF('taxTableWidget2').show();");
		} else {
			System.out.println("invalid level in showTaxTable");
		}

	}

	public void disableBoxPlotCheckBox(int level) {

//		RequestContext context = RequestContext.getCurrentInstance();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = facesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String box = parameterMap.get("type");
		String avail = parameterMap.get("avail");

		switch (box) {
		case "ortho":
			if (level == 1) {
				orthologsAvail1 = Boolean.valueOf(avail);
			} else if (level == 2) {
				orthologsAvail2 = Boolean.valueOf(avail);
			} else if (level == 3){
				orthologsAvail3 = Boolean.valueOf(avail);
			}
			break;
		case "endangered":
			if (level == 1) {
				endangeredsAvail1 = Boolean.valueOf(avail);
			} else if (level == 2) {
				endangeredsAvail2 = Boolean.valueOf(avail);
			} else if (level == 3){
				endangeredsAvail3 = Boolean.valueOf(avail);
			}
			break;
		case "threatened":
			if (level == 1) {
				threatenedsAvail1 = Boolean.valueOf(avail);
			} else if (level == 2) {
				threatenedsAvail2 = Boolean.valueOf(avail);
			} else if (level == 3){
				threatenedsAvail3 = Boolean.valueOf(avail);
			}
			break;
		case "model":
			if (level == 1) {
				modelsAvail1 = Boolean.valueOf(avail);
			} else if (level == 2) {
				modelsAvail2 = Boolean.valueOf(avail);
			} else if (level == 3){
				modelsAvail3 = Boolean.valueOf(avail);
			}
			break;
		default:
			break;
		}

	}
	
//	public void retrieveSVGnew(int level){
//	
//	}
	
	//sets size of heatmap in bean variables
	public void setHeatMapSize(int level){
		System.out.println("inside setHeatMapSize");
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		float width = Float.parseFloat(parameterMap.get("width"));
		float height = Float.parseFloat(parameterMap.get("height"));
		
		imageWidth3 = (int) Math.round(width);
		imageHeight3 = (int) Math.round(height);
		
		imageAspectRatio3 = ((double) imageWidth3 / imageHeight3);
		
	}
	
	
	public void pushHeatmapToRA(){
		System.out.println("inside testPushHeatmapToRA");
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String theSVG = parameterMap.get("theSVG");
		theSVG = theSVG.replace("data:octet/stream;base64,", "");
		
		System.out.println("latest theSVG base64: " + theSVG);
		
	
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
					.getELResolver().getValue(elContext, null, "riskAssessorView");
		raReportView.getRaReport().setLev3Heatmap(org.apache.commons.codec.binary.Base64.decodeBase64(theSVG));
		
		reportView.pushToRA("heatmap3");  //to push heatmap settings
	}
	
	
	
	//get svg source including all css/javascript/data
	public void retrieveHeatMapSVG(int level){
		//currently disregard level number.  Assume level = 3
		
		System.out.println("inside retrieveHeatMapSVG for level:" + level);

//		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String theSVG = parameterMap.get("theSVG");
		String imageType = parameterMap.get("imageType").toLowerCase();
		
		InputStream stream = null;
		
//		if (imageType3 != ImageTypeEnum.SVG){
		if (!imageType.equals("svg")){
			//convert base64 string to streamed content;
			Decoder decoder = Base64.getDecoder();
			byte[] imgByteArray = decoder.decode(theSVG.split(",")[1]);
			stream = new ByteArrayInputStream(imgByteArray);
		}else {
			stream = IOUtils.toInputStream(theSVG);
//			stream = new ByteArrayInputStream(theSVG.getBytes());
		}
		
		InputStream stream2 = stream;
//		switch (imageType3) {
		switch (imageType){
//		case SVG:
		case "svg":
			//svgBinarySource3 = downloadSVG(3);
			//svgBinarySource3 = new DefaultStreamedContent(stream, "image/svg", "theSVG.svg");
			svgBinarySource3 = DefaultStreamedContent.builder().contentType("image/svg").name("theSVG.svg").stream(()->stream2).build();
			break;
//		case PNG:
		case "png":
			//svgBinarySource3 = new DefaultStreamedContent(stream, "image/png", "thePNG.png");
			svgBinarySource3 = DefaultStreamedContent.builder().contentType("image/png").name("thePNG.png").stream(()->stream2).build();
			break;
//		case JPG:
		case "jpeg":
			//svgBinarySource3 = downloadJPG(3);
			//svgBinarySource3 = new DefaultStreamedContent(stream, "image/jpg", "theJPG.jpg");
			svgBinarySource3 = DefaultStreamedContent.builder().contentType("image/jpg").name("theJPG.jpg").stream(()->stream2).build();
			break;

		default:
		
			break;
		}
		
		try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	


	public void retrieveSVG(int level) {
		System.out.println("inside retrieveSVG for level:" + level);

//		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String theSVG = parameterMap.get("theSVG");
		// System.out.println("theSVG");
		// System.out.println(theSVG);
		String theCSS = parameterMap.get("theCSS");
		// System.out.println("theCSS");
		// System.out.println(theCSS);
		String theJS = parameterMap.get("theJS");
		// System.out.println("theJS");
		// System.out.println(theJS);
		String theSQG = parameterMap.get("theSQG");

		// the following removes tags/styles that batik cannot handle
		theSVG = theSVG.replaceAll("<div.*\\/div>", "");
		theSVG = theSVG.replaceAll("cursor:\\s*context-menu;", "");
		theSVG = theSVG.replaceAll("popupG", "removethis");

		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		
		//DEBUG test svg
		//theSVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100\" width=\"100\"><circle cx=\"50\" cy=\"50\" r=\"40\" stroke=\"black\" stroke-width=\"3\" fill=\"red\"/></svg>";
		//theSVG="<svg id=\"heatMapSVG\" xmlns=\"http://www.w3.org/2000/svg\"></svg>";
		//theSVG="<svg id=\"heatMapSVG\" xmlns=\"http://www.w3.org/2000/svg\"><g transform=\"translate(25,25)\"><rect id=\"svgBorder\" x=\"-25\" y=\"-25\" style=\"stroke: black; fill: none; stroke-width: 1;\" width=\"718\" height=\"409\"></rect><foreignObject id=\"heatMapForObj\" width=\"678px\" height=\"369px\"></foreignObject></g></svg>";
		
		//theSVG="<svg id=\"heatMapSVG\" xmlns=\"http://www.w3.org/2000/svg\"><g transform=\"translate(25,25)\"><rect id=\"svgBorder\" x=\"-25\" y=\"-25\" style=\"stroke: black; fill: none; stroke-width: 1;\" width=\"718\" height=\"409\"></rect><foreignObject id=\"heatMapForObj\" width=\"678px\" height=\"369px\"><body><table><thead><tr><th>Common Name</th></tr></thead><tbody><tr><td>Amaranth</td></tbody></table></body></foreignObject></g></svg>";
		
		
		
		//failed
		//theSVG="<svg id=\"heatMapSVG\" xmlns=\"http://www.w3.org/2000/svg\"><g transform=\"translate(25,25)\"><rect id=\"svgBorder\" x=\"-25\" y=\"-25\" style=\"stroke: black; fill: none; stroke-width: 1;\" width=\"718\" height=\"409\"></rect><foreignObject id=\"heatMapForObj\" width=\"678px\" height=\"369px\"><body><table id=\"heatMapTable\"><thead><tr id=\"headerRow\"><th>Common Name</th><th>Similar Susceptibility</th><th>Amino Acid 1</th><th>Amino Acid 2</th><th>Amino Acid 3</th></tr></thead><tbody id=\"heatMapBody\"><tr id=\"3567\"><td style=\"text-align: center; background-color: rgb(240, 240, 240);\">Amaranth</td><td style=\"text-align: center; background-color: rgb(46, 184, 46);\">Y</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">5P</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">29Y</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">54Y</td></tr><tr id=\"29760\"><td style=\"text-align: center; background-color: rgb(240, 240, 240);\">Wine grape</td><td style=\"text-align: center; background-color: rgb(255, 0, 0);\">N</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">4P</td><td style=\"text-align: center; background-color: rgb(152, 175, 210);\">28H</td><td style=\"text-align: center; background-color: rgb(250, 206, 0);\">53G</td></tr><tr id=\"1590841\"><td style=\"text-align: center; background-color: rgb(240, 240, 240);\">Chinese gooseberry family</td><td style=\"text-align: center; background-color: rgb(255, 0, 0);\">N</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">4P</td><td style=\"text-align: center; background-color: rgb(152, 175, 210);\">28H</td><td style=\"text-align: center; background-color: rgb(250, 206, 0);\">53G</td></tr><tr id=\"3649\"><td style=\"text-align: center; background-color: rgb(240, 240, 240);\">Papaya</td><td style=\"text-align: center; background-color: rgb(255, 0, 0);\">N</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">4P</td><td style=\"text-align: center; background-color: rgb(152, 175, 210);\">28H</td><td style=\"text-align: center; background-color: rgb(250, 206, 0);\">--</td></tr><tr id=\"102107\"><td style=\"text-align: center; background-color: rgb(240, 240, 240);\">Japanese apricot</td><td style=\"text-align: center; background-color: rgb(255, 0, 0);\">N</td><td style=\"text-align: center; background-color: rgb(52, 93, 150);\">6P</td><td style=\"text-align: center; background-color: rgb(152, 175, 210);\">30H</td><td style=\"text-align: center; background-color: rgb(250, 206, 0);\">55G</td></tr></tbody></table></body></foreignObject></g></svg>";
		//theSVG="<svg id=\"heatMapSVG\" xmlns=\"http://www.w3.org/2000/svg\"><g transform=\"translate(25,25)\"><rect id=\"svgBorder\" x=\"-25\" y=\"-25\" style=\"stroke: black; fill: none; stroke-width: 1;\" width=\"718\" height=\"409\"></rect><foreignObject id=\"heatMapForObj\" width=\"678px\" height=\"369px\"><body><table id=\"heatMapTable\"><thead><tr id=\"headerRow\"><th>Common Name</th><th>Similar Susceptibility</th><th>Amino Acid 1</th><th>Amino Acid 2</th><th>Amino Acid 3</th></tr></thead><tbody id=\"heatMapBody\"><tr id=\"3567\"><td >Amaranth</td><td>Y</td><td >5P</td><td >29Y</td><td>54Y</td></tr></tbody></table></body></foreignObject></g></svg>";
				
		StringReader reader = new StringReader(theSVG);
		
		//debug reader //causes rest to fail
//		try {
//			String readerStr = IOUtils.toString(reader);
//			System.out.println(readerStr.substring(0,20));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		String uri = "svgURI";
		Document doc = null;
		try {
			doc = f.createDocument(uri, reader);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		

		SVGDocument svgDoc = (SVGDocument) doc;
		Element svgRoot = svgDoc.getDocumentElement();

		// add CSS
		Element defElem = doc.createElementNS("http://www.w3.org/2000/svg", "defs");
		defElem.setAttribute("id", "cssDefs");
		Element styleElem = doc.createElement("style");
		CDATASection cssData = doc.createCDATASection(theCSS);
		styleElem.appendChild(cssData);
		defElem.appendChild(styleElem);
		svgRoot.appendChild(defElem);

		// add JS
		Element scriptElem = doc.createElementNS("http://www.w3.org/2000/svg", "script");
		scriptElem.setAttribute("type", "text/javascript");
		CDATASection jsData = doc.createCDATASection(theJS);
		scriptElem.appendChild(jsData);
		svgRoot.appendChild(scriptElem);
		
		// add SQG
		Element scriptElemSQG = doc.createElementNS("http://www.w3.org/2000/svg", "script");
		scriptElemSQG.setAttribute("type", "text/javascript");
		CDATASection sqgData = doc.createCDATASection(theSQG);
		scriptElemSQG.appendChild(jsData);
		svgRoot.appendChild(scriptElemSQG);

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(svgDoc), new StreamResult(writer));
			if (level == 1) {
				svgSource1 = writer.toString();
			} else if (level == 2) {
				svgSource2 = writer.toString();
			} 
//			else if (level == 3) {
//				svgSource3 = null;  //level 3 is implemented elsewhere
//			}
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getImageSize(level);
		// System.out.println(svgSource);
		// System.out.println("Source for svgDoc");
		// if (level == 1) {
		// System.out.println(svgSource1);
		// } else if (level == 2) {
		// System.out.println(svgSource2);
		// }

	}

	public void getImageSize(int level) {

		// parses viewBox = " x y width height"
		Pattern pattern = Pattern.compile("viewBox *= *\"(.*?)\"");
		Matcher matcher = null;
		if (level == 1) {
			matcher = pattern.matcher(svgSource1);
		} else if (level == 2) {
			matcher = pattern.matcher(svgSource2);
		} else if (level == 3) {
			return;
		} 
		while (matcher.find()) {
			String found = matcher.group(1);
			String[] res = found.split(" ");
			if (level == 1) {
				imageWidth1 = (int) Math.rint(Double.parseDouble(res[2]));
				imageHeight1 = (int) Math.rint(Double.parseDouble(res[3]));
			} else if (level == 2) {
				imageWidth2 = (int) Math.rint(Double.parseDouble(res[2]));
				imageHeight2 = (int) Math.rint(Double.parseDouble(res[3]));
			} 
//				else if (level == 3) {
//				imageWidth3 = (int) Math.rint(Double.parseDouble(res[2]));
//				imageHeight3 = (int) Math.rint(Double.parseDouble(res[3]));
//			}
		}
		if (level == 1) {
			imageAspectRatio1 = ((double) imageWidth1 / imageHeight1);
		} else if (level == 2) {
			imageAspectRatio2 = ((double) imageWidth2 / imageHeight2);
		} 
//		else if (level == 3) {
//			imageAspectRatio3 = ((double) imageWidth3 / imageHeight3);
//		}
	}

	// public void setImageSize() {
	// // replace this with regex to change viewbox
	// System.out.println("width: " + imageWidth1);
	// System.out.println("height: " + imageHeight1);
	// }

	public StreamedContent downloadImage(int level) {
		// setImageSize();
		StreamedContent theImage = null;
		ImageTypeEnum imageType = null;
		if (level == 1) {
			imageType = imageType1;
		} else if (level == 2) {
			imageType = imageType2;
		} else if (level == 3) {
			imageType = imageType3;
		}
		switch (imageType) {
		case SVG:
			theImage = downloadSVG(level);
			break;
		case PNG:
			theImage = downloadPNG(level);
			break;
		case JPG:
			theImage = downloadJPG(level);
			break;

		default:
			break;
		}
		return theImage;

	}

	public StreamedContent downloadSVG(int level) {
		try {
			InputStream stream = null;
			if (level == 1) {
				stream = new ByteArrayInputStream(svgSource1.getBytes(StandardCharsets.UTF_8.name()));
			} else if (level == 2) {
				stream = new ByteArrayInputStream(svgSource2.getBytes(StandardCharsets.UTF_8.name()));
			} else if (level == 3) {
				stream = new ByteArrayInputStream(svgSource3.getBytes(StandardCharsets.UTF_8.name()));
			}
			InputStream stream2 = stream;
			//StreamedContent SVG = new DefaultStreamedContent(stream, "image/svg", "theSVG.svg");
			StreamedContent SVG = DefaultStreamedContent.builder().contentType("image/SVG").name("theSVG.svg").stream(()->stream2).build();
			return SVG;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public StreamedContent downloadPNG(int level) {
		float imageHeight = 0;
		float imageWidth = 0;
		String svgSource = null;
		if (level == 1) {
			imageHeight = imageHeight1;
			imageWidth = imageWidth1;
			svgSource = svgSource1;
		} else if (level == 2) {
			imageHeight = imageHeight2;
			imageWidth = imageWidth2;
			svgSource = svgSource2;
		} else if (level == 3) {
			return null;  //level 3 is handled elsewhere
		}
		PNGTranscoder pngt = new PNGTranscoder();
		pngt.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(imageHeight));
		pngt.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(imageWidth));
		pngt.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);

		ByteArrayOutputStream ostream = null;
		StreamedContent PNG = null;

		if (svgSource != null) {
			TranscoderInput input = new TranscoderInput(new StringReader(svgSource));
			ostream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(ostream);

			try {
				// t.transcode(input, output);
				pngt.transcode(input, output);
				//PNG = new DefaultStreamedContent(new ByteArrayInputStream(ostream.toByteArray()), "image/png",
				//		"thePNG.png");
				final ByteArrayOutputStream ostream2 = ostream;
				PNG = DefaultStreamedContent.builder().contentType("image/png").name("thePNG.png").stream(()->new ByteArrayInputStream(ostream2.toByteArray())).build();
				ostream.flush();
				ostream.close();

				return PNG;
			} catch (TranscoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	public StreamedContent downloadJPG(int level) {
		float imageHeight = 0;
		float imageWidth = 0;
		String svgSource = null;
		if (level == 1) {
			imageHeight = imageHeight1;
			imageWidth = imageWidth1;
			svgSource = svgSource1;
		} else if (level == 2) {
			imageHeight = imageHeight2;
			imageWidth = imageWidth2;
			svgSource = svgSource2;
		} else if (level == 3) {
			imageHeight = imageHeight3;
			imageWidth = imageWidth3;
			svgSource = svgSource3;
		}
		JPEGTranscoder jpgt = new JPEGTranscoder();
		jpgt.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(imageHeight));
		jpgt.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(imageWidth));
		jpgt.addTranscodingHint(JPEGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);

		ByteArrayOutputStream ostream = null;
		StreamedContent JPG = null;

		if (svgSource != null) {
			TranscoderInput input = new TranscoderInput(new StringReader(svgSource));
			ostream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(ostream);

			try {
				// t.transcode(input, output);
				jpgt.transcode(input, output);
				//JPG = new DefaultStreamedContent(new ByteArrayInputStream(ostream.toByteArray()), "image/jpeg",
				//		"theJPEG.jpg");
				final ByteArrayOutputStream ostream2 = ostream;
				JPG = DefaultStreamedContent.builder().contentType("image/jpg").name("theJPG.jpg").stream(()->new ByteArrayInputStream(ostream2.toByteArray())).build();
				ostream.flush();
				ostream.close();

				return JPG;
			} catch (TranscoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	// Insure 16x9 aspect ratio
	public void constrainAspectRatio(int level, String dimension) {

		switch (dimension) {
		case "width":
			if (level == 1) {
				imageHeight1 = (int) (imageWidth1 / imageAspectRatio1);
			} else if (level == 2) {
				imageHeight2 = (int) (imageWidth2 / imageAspectRatio2);
			} else if (level == 3) {
				imageHeight3 = (int) (imageWidth3 / imageAspectRatio3);
			}
			break;

		case "height":

			if (level == 1) {
				imageWidth1 = (int) (imageHeight1 * imageAspectRatio1);
			} else if (level == 2) {
				imageWidth2 = (int) (imageHeight2 * imageAspectRatio2);
			} else if (level == 3) {
				imageWidth3 = (int) (imageHeight3 * imageAspectRatio3);
			}
			break;

		default:
			break;
		}

	}

	public void updateSpeciesMenu(int level) {
		System.out.println("inside updateSpeciesMenu");
//		RequestContext context = RequestContext.getCurrentInstance();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = facesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String action = parameterMap.get("action");
		String tempItems = parameterMap.get("items");

		// System.out.println("action: " + action);
		// System.out.println("tempItems: " + tempItems);
		List<String> items = Arrays.asList(tempItems.split("\t"));
		if (level == 1) {
			if (action.equals("remove")) {
				selectedBoxPlotSpecies.removeAll(items);
			} else if (action.equals("restore")) {
				for (String item : items) {
					System.out.println("Item to restore to level 1: " + item);
					if (boxPlotSpeciesOption1.equals(boxPlotSpeciesNameType.COMMON)) {
						if (boxPlotSpeciesCommonNames.indexOf(item) > -1
								&& selectedBoxPlotSpecies.indexOf(item) == -1) {
							// System.out.println(" ... success!");
							selectedBoxPlotSpecies.add(item);
						}
					} else {
						if (boxPlotSpeciesScientificNames.indexOf(item) > -1
								&& selectedBoxPlotSpecies.indexOf(item) == -1) {
							// System.out.println(" ... success!");
							selectedBoxPlotSpecies.add(item);
						}
					}
				}
				// selectedBoxPlotSpecies.addAll(items);
			}
		} else if (level == 2) {
			if (action.equals("remove")) {
				selectedBoxPlotSpecies2.removeAll(items);
			} else if (action.equals("restore")) {
				for (String item : items) {
					if (boxPlotSpeciesOption2.equals(boxPlotSpeciesNameType.COMMON)) {
						if (boxPlotSpeciesCommonNames2.indexOf(item) > -1
								&& selectedBoxPlotSpecies2.indexOf(item) == -1) {
							selectedBoxPlotSpecies2.add(item);
						}
					} else {
						if (boxPlotSpeciesScientificNames2.indexOf(item) > -1
								&& selectedBoxPlotSpecies2.indexOf(item) == -1) {
							selectedBoxPlotSpecies2.add(item);
						}
					}
				}
				// selectedBoxPlotSpecies2.addAll(items);
			}
		}
		// System.out.println("selectedBoxPlotSpecies items are:");
		// for (String thing : selectedBoxPlotSpecies) {
		// System.out.println(thing);
		// }

	}

	public void resetBoxPlotZoom(int levelNumber) {
		System.out.println("inside resetBoxPlotZoom: " + levelNumber);
		if (levelNumber == 1) {
			zoomVal1 = zoomValDefault;
		} else if (levelNumber == 2) {
			zoomVal2 = zoomValDefault;
		}
	}

	public void resetBoxPlotWidth(int levelNumber) {
		System.out.println("inside resetBoxPlotWidth: " + levelNumber);
		if (levelNumber == 1) {
			boxWidth1 = boxWidthDefault1;
		} else if (levelNumber == 2) {
			boxWidth2 = boxWidthDefault2;
		}
	}

	public void resetBoxPlotAll(int levelNumber) {
		resetBoxPlotWidth(levelNumber);
		resetBoxPlotZoom(levelNumber);
	}

	public void updateBoxPlotControls(int levelNumber) {
		System.out.println("inside updateBoxPlotControls");

//		RequestContext context = RequestContext.getCurrentInstance();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map<String, String> parameterMap = facesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		// set boxWidth
		int bwVal = Integer.valueOf(parameterMap.get("boxWidth"));
		System.out.println("got bwWidth of: " + bwVal);

		if (levelNumber == 1) {
			boxWidthDefault1 = bwVal;
			boxWidth1 = boxWidthDefault1;
		} else if (levelNumber == 2) {
			boxWidthDefault2 = bwVal;
			boxWidth2 = boxWidthDefault2;
		}
	}

	public void zoomPanOff(int levelNumber) {
		if (levelNumber == 1) {
			zoomPanOn1 = false;
		} else if (levelNumber == 2) {
			zoomPanOn2 = false;
		}
	}

	public void changeBoxPlotSpeciesNameType(int levelNumber) {
		retrieveBoxPlotSpecies(levelNumber);

		if (levelNumber == 1) {
			selectedBoxPlotSpecies.clear();
		} else {
			selectedBoxPlotSpecies2.clear();
		}

	}

	// checks whether species is threatened/endangered/common model
	public boolean checkSpeciesStatus(int taxid, String category) {

		List<Integer> categoryTaxIds = new ArrayList<Integer>();
		if (category.equals("threatened")) {
			for (TaxEcos entry : threatenedSpecies) {
				categoryTaxIds.add(entry.getTaxid());
			}
		} else if (category.equals("endangered")) {
			for (TaxEcos entry : endangeredSpecies) {
				categoryTaxIds.add(entry.getTaxid());
			}
		} else if (category.equals("model")) {
			categoryTaxIds = modelOrganisms;
		}

		// if(categoryTaxIds.contains(taxid)){
		// System.out.println(category + ": taxid: "+ taxid);
		// }

		return categoryTaxIds.contains(taxid);

	}
	
	// compares current level 1 report to pushed RA level 1 report
	public void checkDiffLevel1BoxPlotRAReport() {
		System.out.println("Inside checkDiffLevel1BoxPlotRAReport");
		boolean noChanges = true;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
					.getELResolver().getValue(elContext, null, "riskAssessorView");
		
		
		BoxPlotSettings boxplotSettings = raReportView.getRaReport().getLev1BoxPlotSettings();
		
//		//check if no level 1 boxplot has been pushed yet
		if (raReportView.getRaReport().getLev1Boxplot() == null) {
			level1SVGPushWarning = false;
			level1SVGDiffers = true;
			return;
		}
		
		noChanges = noChanges && selectedBoxPlotTaxGroups.equals(boxplotSettings.getSelectedTaxGroups());
		noChanges = noChanges && selectedBoxPlotSpecies.equals(boxplotSettings.getSelectedSpecies());	
		noChanges = noChanges && (boxPlotSpeciesOption1 == boxplotSettings.getSpeciesOption());
		noChanges = noChanges && (boxPlotKeepCommonSymbols1 == boxplotSettings.isGroupByCommonName());
		noChanges = noChanges && (levelOneBoxPlotOrtholog == boxplotSettings.isOrtholog());
		noChanges = noChanges && (levelOneBoxPlotThreatened == boxplotSettings.isThreatened());
		noChanges = noChanges && (levelOneBoxPlotEndangered == boxplotSettings.isEndangered());
		noChanges = noChanges && (levelOneBoxPlotModelOrganisms == boxplotSettings.isModelOrganisms());
		
		//changes based on level 1 report
		noChanges = noChanges && reportView.compareLevel1ReportSettings(boxplotSettings.getReportSettings());
		
		if (noChanges) {
			level1SVGPushWarning = false;
			level1SVGDiffers = false;
		} else {
			level1SVGPushWarning = true;
			level1SVGDiffers = true;
		}

		
	}
	
	
	// compares current level 2 report to pushed RA level 2 report
	public void checkDiffLevel2BoxPlotRAReport() {
		System.out.println("Inside checkDiffLevel2BoxPlotRAReport");
		boolean noChanges = true;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
					.getELResolver().getValue(elContext, null, "riskAssessorView");
		
		ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "reportView");
		
		
//		BoxPlotSettings boxplotSettings = raReportView.getRaReport().getLev1BoxPlotSettings();
		
		//first find corresponding level 2 report in levelTwoReportColl
		List<RiskAssessorLevel2Group> lev2RAReportGroups = raReportView.getLevel2Groups();
		RiskAssessorLevel2Group lev2Grp = null;
		for (RiskAssessorLevel2Group grp : lev2RAReportGroups){
			boolean foundMatch = true;
			foundMatch = grp.getInfo().getRunId() == reportView.getLoadedCompletedDomain().getRunId();
			foundMatch = foundMatch && grp.getInfo().getDomainNumber() == reportView.getLoadedCompletedDomain().getDomainNumber();
			foundMatch = foundMatch && grp.getInfo().getStartPosition() == reportView.getLoadedCompletedDomain().getStartPosition();
			if (foundMatch) lev2Grp = grp;
		}
		if (lev2Grp != null){
			BoxPlotSettings boxplotSettings = lev2Grp.getBoxPlotSettings();  
			
//			//check if no level 2 boxplot has been pushed yet
			if (lev2Grp.getBoxPlot() == null) {
				level2SVGPushWarning = false;
				level2SVGDiffers = true;
				return;
			}
			
			noChanges = noChanges && selectedBoxPlotTaxGroups2.equals(boxplotSettings.getSelectedTaxGroups());
			noChanges = noChanges && selectedBoxPlotSpecies2.equals(boxplotSettings.getSelectedSpecies());	
			noChanges = noChanges && (boxPlotSpeciesOption2 == boxplotSettings.getSpeciesOption());
			noChanges = noChanges && (boxPlotKeepCommonSymbols2 == boxplotSettings.isGroupByCommonName());
			noChanges = noChanges && (levelTwoBoxPlotOrtholog == boxplotSettings.isOrtholog());
			noChanges = noChanges && (levelTwoBoxPlotThreatened == boxplotSettings.isThreatened());
			noChanges = noChanges && (levelTwoBoxPlotEndangered == boxplotSettings.isEndangered());
			noChanges = noChanges && (levelTwoBoxPlotModelOrganisms == boxplotSettings.isModelOrganisms());
			
			
			//changes based on level 2 report
			noChanges = noChanges && reportView.compareLevel2ReportSettings(boxplotSettings.getReportSettings());
			
			if (noChanges) {
				level2SVGPushWarning = false;
				level2SVGDiffers = false;
			} else {
				level2SVGPushWarning = true;
				level2SVGDiffers = true;
			}
		} else {
			level2SVGPushWarning = false;
			level2SVGDiffers = true;
		}
		
		

		
	}
	
	
	
	// compares current level 2 report to pushed RA level 2 report
		public void checkDiffLevel3HeatmapRAReport() {
			System.out.println("Inside checkDiffLevel3HeatmapRAReport");
			boolean noChanges = true;

			ELContext elContext = FacesContext.getCurrentInstance().getELContext();
			RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
						.getELResolver().getValue(elContext, null, "riskAssessorView");
			
			ReportView reportView = (ReportView) FacesContext.getCurrentInstance().getApplication()
					.getELResolver().getValue(elContext, null, "reportView");
			
			HeatMapSettings heatMapSettings = raReportView.getRaReport().getLev3HeatMapSettings();
////			BoxPlotSettings boxplotSettings = raReportView.getRaReport().getLev1BoxPlotSettings();
			
			//check if no level 3 heatmap has been pushed yet
			if (raReportView.getRaReport().getLev3Heatmap() == null) {
				level3SVGPushWarning = false;
				level3SVGDiffers = true;
				return;
			}
			
			noChanges = noChanges && levelThreeHeatmapPickList.getTarget().equals(heatMapSettings.getSelectedTaxGroups());
			noChanges = noChanges && (hmReportType == heatMapSettings.getReportType());
			noChanges = noChanges && (heatmapSpeciesNameType == heatMapSettings.getSpeciesOption());
			noChanges = noChanges && (level3OptionalSelection.equals(heatMapSettings.getOptionalSelection()));		
			noChanges = noChanges && (levelThreeHeatmapSusceptibility == heatMapSettings.isSPHeatmap());
			noChanges = noChanges && (levelThreeHeatmapSusceptibilityText == heatMapSettings.isSPText());
			noChanges = noChanges && (levelThreeHeatmapAlignPrediction == heatMapSettings.isAPHeatmap());
			noChanges = noChanges && (levelThreeHeatmapAminoAcid == heatMapSettings.isAA());
			noChanges = noChanges && (levelThreeHeatMapPosition == heatMapSettings.isAAPos());
			noChanges = noChanges && (reportView.getChosenQueryResidues().equals(heatMapSettings.getSelectedPositions()));
			
			if (noChanges) {
				level3SVGPushWarning = false;
				level3SVGDiffers = false;
			} else {
				level3SVGPushWarning = true;
				level3SVGDiffers = true;
			}

		}
	
	
	
	//Heatmap methods
	
	public void updateHeatMapSize(int levelNumber) {
		System.out.println("inside updateHeatMapSize");

		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		// set boxWidth
		int width = Integer.valueOf(parameterMap.get("width"));
		int height = Integer.valueOf(parameterMap.get("height"));
		System.out.println("got width of: " + width + " and height of : " + height);

		imageHeight3 = height;
		imageWidth3 = width;
	}
	
	
	public void handleSusceptibleChkBox(String chkBox){
		
		//if checked susceptible then default susceptibleText to true
		if (chkBox.equals("susceptible") && levelThreeHeatmapSusceptibility){
			levelThreeHeatmapSusceptibilityText = true;
		}
		
		//if susceptible is false then set susceptibleText to false
		if (!levelThreeHeatmapSusceptibility){
			levelThreeHeatmapSusceptibilityText = false;
		}
	}
	

	// Getters and Setters
	public ReportView getReportView() {
		return reportView;
	}

	public void setReportView(ReportView reportView) {
		this.reportView = reportView;
	}

	public String getLevelOneVizPage() {
		System.out.println("Calling getLevelOneVizPage: " + levelOneVizPage);
		return levelOneVizPage;
	}

	public void setLevelOneVizPage(String levelOneVizPage) {
		this.levelOneVizPage = levelOneVizPage;
	}

	public List<String> getBoxPlotTaxGroups() {
		return boxPlotTaxGroups;
	}

	public void setBoxPlotTaxGroups(List<String> boxPlotTaxGroups) {
		this.boxPlotTaxGroups = boxPlotTaxGroups;
	}

	public List<String> getSelectedBoxPlotTaxGroups() {
		return selectedBoxPlotTaxGroups;
	}

	public void setSelectedBoxPlotTaxGroups(List<String> selectedBoxPlotTaxGroups) {
		this.selectedBoxPlotTaxGroups = selectedBoxPlotTaxGroups;
	}

	public Map<Integer, String> getBoxPlotSpecies() {
		return boxPlotSpecies;
	}

	public void setBoxPlotSpecies(Map<Integer, String> boxPlotSpecies) {
		this.boxPlotSpecies = boxPlotSpecies;
	}

	public List<String> getSelectedBoxPlotSpecies() {
		return selectedBoxPlotSpecies;
	}

	public void setSelectedBoxPlotSpecies(List<String> selectedBoxPlotSpecies) {
		this.selectedBoxPlotSpecies = selectedBoxPlotSpecies;
	}

	public List<String> getBoxPlotSpeciesCommonNames() {
		return boxPlotSpeciesCommonNames;
	}

	public void setBoxPlotSpeciesCommonNames(List<String> boxPlotSpeciesCommonNames) {
		this.boxPlotSpeciesCommonNames = boxPlotSpeciesCommonNames;
	}

	public boolean isLevelOneBoxPlotOrtholog() {
		return levelOneBoxPlotOrtholog;
	}

	public void setLevelOneBoxPlotOrtholog(boolean levelOneBoxPlotOrtholog) {
		this.levelOneBoxPlotOrtholog = levelOneBoxPlotOrtholog;
	}

	public boolean isLevelOneBoxPlotEndangered() {
		return levelOneBoxPlotEndangered;
	}

	public void setLevelOneBoxPlotEndangered(boolean levelOneBoxPlotEndangered) {
		this.levelOneBoxPlotEndangered = levelOneBoxPlotEndangered;
	}

	public String getLevelOneVizPageHeader() {
		return levelOneVizPageHeader;
	}

	public void setLevelOneVizPageHeader(String levelOneVizPageHeader) {
		this.levelOneVizPageHeader = levelOneVizPageHeader;
	}

	public String getLevelTwoVizPage() {
		return levelTwoVizPage;
	}

	public void setLevelTwoVizPage(String levelTwoVizPage) {
		this.levelTwoVizPage = levelTwoVizPage;
	}

	public String getLevelTwoVizPageHeader() {
		return levelTwoVizPageHeader;
	}

	public void setLevelTwoVizPageHeader(String levelTwoVizPageHeader) {
		this.levelTwoVizPageHeader = levelTwoVizPageHeader;
	}

	public String getLevelThreeVizPage() {
		return levelThreeVizPage;
	}

	public void setLevelThreeVizPage(String levelThreeVizPage) {
		this.levelThreeVizPage = levelThreeVizPage;
	}

	public String getLevelThreeVizPageHeader() {
		return levelThreeVizPageHeader;
	}

	public void setLevelThreeVizPageHeader(String levelThreeVizPageHeader) {
		this.levelThreeVizPageHeader = levelThreeVizPageHeader;
	}

	public List<String> getBoxPlotTaxGroups2() {
		return boxPlotTaxGroups2;
	}

	public void setBoxPlotTaxGroups2(List<String> boxPlotTaxGroups2) {
		this.boxPlotTaxGroups2 = boxPlotTaxGroups2;
	}

	public List<String> getSelectedBoxPlotTaxGroups2() {
		return selectedBoxPlotTaxGroups2;
	}

	public void setSelectedBoxPlotTaxGroups2(List<String> selectedBoxPlotTaxGroups2) {
		this.selectedBoxPlotTaxGroups2 = selectedBoxPlotTaxGroups2;
	}

	public boolean isLevelTwoBoxPlotOrtholog() {
		return levelTwoBoxPlotOrtholog;
	}

	public void setLevelTwoBoxPlotOrtholog(boolean levelTwoBoxPlotOrtholog) {
		this.levelTwoBoxPlotOrtholog = levelTwoBoxPlotOrtholog;
	}

	public boolean isLevelTwoBoxPlotEndangered() {
		return levelTwoBoxPlotEndangered;
	}

	public void setLevelTwoBoxPlotEndangered(boolean levelTwoBoxPlotEndangered) {
		this.levelTwoBoxPlotEndangered = levelTwoBoxPlotEndangered;
	}

	public boolean isLevelTwoBoxPlotModelOrganisms() {
		return levelTwoBoxPlotModelOrganisms;
	}

	public void setLevelTwoBoxPlotModelOrganisms(boolean levelTwoBoxPlotModelOrganisms) {
		this.levelTwoBoxPlotModelOrganisms = levelTwoBoxPlotModelOrganisms;
	}

	public boolean isLevelOneBoxPlotModelOrganisms() {
		return levelOneBoxPlotModelOrganisms;
	}

	public void setLevelOneBoxPlotModelOrganisms(boolean levelOneBoxPlotModelOrganisms) {
		this.levelOneBoxPlotModelOrganisms = levelOneBoxPlotModelOrganisms;
	}

	public String getLevelOneBoxPlotJSON() {
		return levelOneBoxPlotJSON;
	}

	public void setLevelOneBoxPlotJSON(String levelOneBoxPlotJSON) {
		this.levelOneBoxPlotJSON = levelOneBoxPlotJSON;
	}

	public String getLevelTwoBoxPlotJSON() {
		return levelTwoBoxPlotJSON;
	}

	public void setLevelTwoBoxPlotJSON(String levelTwoBoxPlotJSON) {
		this.levelTwoBoxPlotJSON = levelTwoBoxPlotJSON;
	}

	public Map<Integer, String> getBoxPlotSpecies2() {
		return boxPlotSpecies2;
	}

	public void setBoxPlotSpecies2(Map<Integer, String> boxPlotSpecies2) {
		this.boxPlotSpecies2 = boxPlotSpecies2;
	}

	public List<String> getBoxPlotSpeciesCommonNames2() {
		return boxPlotSpeciesCommonNames2;
	}

	public void setBoxPlotSpeciesCommonNames2(List<String> boxPlotSpeciesCommonNames2) {
		this.boxPlotSpeciesCommonNames2 = boxPlotSpeciesCommonNames2;
	}

	public List<String> getSelectedBoxPlotSpecies2() {
		return selectedBoxPlotSpecies2;
	}

	public void setSelectedBoxPlotSpecies2(List<String> selectedBoxPlotSpecies2) {
		this.selectedBoxPlotSpecies2 = selectedBoxPlotSpecies2;
	}

	public String getLevelOneBoxPlotTaxGroupsJSON() {
		return levelOneBoxPlotTaxGroupsJSON;
	}

	public void setLevelOneBoxPlotTaxGroupsJSON(String levelOneBoxPlotTaxGroupsJSON) {
		this.levelOneBoxPlotTaxGroupsJSON = levelOneBoxPlotTaxGroupsJSON;
	}

	public String getLevelTwoBoxPlotTaxGroupsJSON() {
		return levelTwoBoxPlotTaxGroupsJSON;
	}

	public void setLevelTwoBoxPlotTaxGroupsJSON(String levelTwoBoxPlotTaxGroupsJSON) {
		this.levelTwoBoxPlotTaxGroupsJSON = levelTwoBoxPlotTaxGroupsJSON;
	}

	public String getEndangeredSpeciesJSON() {
		return endangeredSpeciesJSON;
	}

	public void setEndangeredSpeciesJSON(String endangeredSpeciesJSON) {
		this.endangeredSpeciesJSON = endangeredSpeciesJSON;
	}

	public String getModelOrganismsJSON() {
		return modelOrganismsJSON;
	}

	public void setModelOrganismsJSON(String modelOrganismsJSON) {
		this.modelOrganismsJSON = modelOrganismsJSON;
	}

	public List<TaxEcos> getEndangeredSpecies() {
		return endangeredSpecies;
	}

	public void setEndangeredSpecies(List<TaxEcos> endangeredSpecies) {
		this.endangeredSpecies = endangeredSpecies;
	}

	public List<Integer> getModelOrganisms() {
		return modelOrganisms;
	}

	public void setModelOrganisms(List<Integer> modelOrganisms) {
		this.modelOrganisms = modelOrganisms;
	}

	public String getDataTableTaxGroup1() {
		return dataTableTaxGroup1;
	}

	public void setDataTableTaxGroup1(String dataTableTaxGroup1) {
		this.dataTableTaxGroup1 = dataTableTaxGroup1;
	}

	public List<LevelOneReportRow> getTaxTable1() {
		return taxTable1;
	}

	public void setTaxTable1(List<LevelOneReportRow> taxTable1) {
		this.taxTable1 = taxTable1;
	}

	public String getDataTableTaxGroup2() {
		return dataTableTaxGroup2;
	}

	public void setDataTableTaxGroup2(String dataTableTaxGroup2) {
		this.dataTableTaxGroup2 = dataTableTaxGroup2;
	}

	public List<LevelTwoReportRow> getTaxTable2() {
		return taxTable2;
	}

	public void setTaxTable2(List<LevelTwoReportRow> taxTable2) {
		this.taxTable2 = taxTable2;
	}

	public Double getTaxMean1() {
		return taxMean1;
	}

	public void setTaxMean1(Double taxMean1) {
		this.taxMean1 = taxMean1;
	}

	public Double getTaxMedian1() {
		return taxMedian1;
	}

	public void setTaxMedian1(Double taxMedian1) {
		this.taxMedian1 = taxMedian1;
	}

	public String getTaxSusceptible1() {
		return taxSusceptible1;
	}

	public void setTaxSusceptible1(String taxSusceptible1) {
		this.taxSusceptible1 = taxSusceptible1;
	}

	public Double getTaxMean2() {
		return taxMean2;
	}

	public void setTaxMean2(Double taxMean2) {
		this.taxMean2 = taxMean2;
	}

	public Double getTaxMedian2() {
		return taxMedian2;
	}

	public void setTaxMedian2(Double taxMedian2) {
		this.taxMedian2 = taxMedian2;
	}

	public String getTaxSusceptible2() {
		return taxSusceptible2;
	}

	public void setTaxSusceptible2(String taxSusceptible2) {
		this.taxSusceptible2 = taxSusceptible2;
	}

	public boolean isOrthologsAvail1() {
		return orthologsAvail1;
	}

	public void setOrthologsAvail1(boolean orthologsAvail1) {
		this.orthologsAvail1 = orthologsAvail1;
	}

	public boolean isOrthologsAvail2() {
		return orthologsAvail2;
	}

	public void setOrthologsAvail2(boolean orthologsAvail2) {
		this.orthologsAvail2 = orthologsAvail2;
	}

	public boolean isEndangeredsAvail1() {
		return endangeredsAvail1;
	}

	public void setEndangeredsAvail1(boolean endangeredsAvail1) {
		this.endangeredsAvail1 = endangeredsAvail1;
	}

	public boolean isEndangeredsAvail2() {
		return endangeredsAvail2;
	}

	public void setEndangeredsAvail2(boolean endangeredsAvail2) {
		this.endangeredsAvail2 = endangeredsAvail2;
	}

	public boolean isModelsAvail1() {
		return modelsAvail1;
	}

	public void setModelsAvail1(boolean modelsAvail1) {
		this.modelsAvail1 = modelsAvail1;
	}

	public boolean isModelsAvail2() {
		return modelsAvail2;
	}

	public void setModelsAvail2(boolean modelsAvail2) {
		this.modelsAvail2 = modelsAvail2;
	}

	public String getSvgSource1() {
		return svgSource1;
	}

	public void setSvgSource1(String svgSource1) {
		this.svgSource1 = svgSource1;
	}

	public Integer getImageWidth1() {
		return imageWidth1;
	}

	public void setImageWidth1(Integer imageWidth1) {
		this.imageWidth1 = imageWidth1;
	}

	public Integer getImageHeight1() {
		return imageHeight1;
	}

	public void setImageHeight1(Integer imageHeight1) {
		this.imageHeight1 = imageHeight1;
	}

	public ImageTypeEnum getImageType1() {
		return imageType1;
	}

	public void setImageType1(ImageTypeEnum imageType1) {
		this.imageType1 = imageType1;
	}

	public double getImageAspectRatio1() {
		return imageAspectRatio1;
	}

	public void setImageAspectRatio1(double imageAspectRatio1) {
		this.imageAspectRatio1 = imageAspectRatio1;
	}

	public Integer getImageWidth2() {
		return imageWidth2;
	}

	public void setImageWidth2(Integer imageWidth2) {
		this.imageWidth2 = imageWidth2;
	}

	public Integer getImageHeight2() {
		return imageHeight2;
	}

	public void setImageHeight2(Integer imageHeight2) {
		this.imageHeight2 = imageHeight2;
	}

	public double getImageAspectRatio2() {
		return imageAspectRatio2;
	}

	public void setImageAspectRatio2(double imageAspectRatio2) {
		this.imageAspectRatio2 = imageAspectRatio2;
	}

	public String getSvgSource2() {
		return svgSource2;
	}

	public void setSvgSource2(String svgSource2) {
		this.svgSource2 = svgSource2;
	}

	public ImageTypeEnum getImageType2() {
		return imageType2;
	}

	public void setImageType2(ImageTypeEnum imageType2) {
		this.imageType2 = imageType2;
	}

	public boolean isLevelOneBoxPlotThreatened() {
		return levelOneBoxPlotThreatened;
	}

	public void setLevelOneBoxPlotThreatened(boolean levelOneBoxPlotThreatened) {
		this.levelOneBoxPlotThreatened = levelOneBoxPlotThreatened;
	}

	public boolean isLevelTwoBoxPlotThreatened() {
		return levelTwoBoxPlotThreatened;
	}

	public void setLevelTwoBoxPlotThreatened(boolean levelTwoBoxPlotThreatened) {
		this.levelTwoBoxPlotThreatened = levelTwoBoxPlotThreatened;
	}

	public boolean isThreatenedsAvail1() {
		return threatenedsAvail1;
	}

	public void setThreatenedsAvail1(boolean threatenedsAvail1) {
		this.threatenedsAvail1 = threatenedsAvail1;
	}

	public boolean isThreatenedsAvail2() {
		return threatenedsAvail2;
	}

	public void setThreatenedsAvail2(boolean threatenedsAvail2) {
		this.threatenedsAvail2 = threatenedsAvail2;
	}

	public String getThreatenedSpeciesJSON() {
		return threatenedSpeciesJSON;
	}

	public void setThreatenedSpeciesJSON(String threatenedSpeciesJSON) {
		this.threatenedSpeciesJSON = threatenedSpeciesJSON;
	}

	public List<TaxEcos> getThreatenedSpecies() {
		return threatenedSpecies;
	}

	public void setThreatenedSpecies(List<TaxEcos> threatenedSpecies) {
		this.threatenedSpecies = threatenedSpecies;
	}

	public boolean isZoomPanOn1() {
		return zoomPanOn1;
	}

	public void setZoomPanOn1(boolean zoomPanOn1) {
		this.zoomPanOn1 = zoomPanOn1;
	}

	public boolean isZoomPanOn2() {
		return zoomPanOn2;
	}

	public void setZoomPanOn2(boolean zoomPanOn2) {
		this.zoomPanOn2 = zoomPanOn2;
	}

	public int getBoxWidth1() {
		return boxWidth1;
	}

	public void setBoxWidth1(int boxWidth1) {
		this.boxWidth1 = boxWidth1;
	}

	public int getBoxWidth2() {
		return boxWidth2;
	}

	public void setBoxWidth2(int boxWidth2) {
		this.boxWidth2 = boxWidth2;
	}

	public int getZoomVal1() {
		return zoomVal1;
	}

	public void setZoomVal1(int zoomVal1) {
		this.zoomVal1 = zoomVal1;
	}

	public int getZoomVal2() {
		return zoomVal2;
	}

	public void setZoomVal2(int zoomVal2) {
		this.zoomVal2 = zoomVal2;
	}

	public int getZoomValDefault() {
		return zoomValDefault;
	}

	public void setZoomValDefault(int zoomValDefault) {
		this.zoomValDefault = zoomValDefault;
	}

	public int getBoxWidthDefault1() {
		return boxWidthDefault1;
	}

	public void setBoxWidthDefault1(int boxWidthDefault1) {
		this.boxWidthDefault1 = boxWidthDefault1;
	}

	public int getBoxWidthDefault2() {
		return boxWidthDefault2;
	}

	public void setBoxWidthDefault2(int boxWidthDefault2) {
		this.boxWidthDefault2 = boxWidthDefault2;
	}

	public List<String> getBoxPlotSpeciesScientificNames() {
		return boxPlotSpeciesScientificNames;
	}

	public void setBoxPlotSpeciesScientificNames(List<String> boxPlotSpeciesScientificNames) {
		this.boxPlotSpeciesScientificNames = boxPlotSpeciesScientificNames;
	}

	public List<String> getBoxPlotSpeciesScientificNames2() {
		return boxPlotSpeciesScientificNames2;
	}

	public void setBoxPlotSpeciesScientificNames2(List<String> boxPlotSpeciesScientificNames2) {
		this.boxPlotSpeciesScientificNames2 = boxPlotSpeciesScientificNames2;
	}

	public List<String> getBoxPlotSpeciesNames() {
		return boxPlotSpeciesNames;
	}

	public void setBoxPlotSpeciesNames(List<String> boxPlotSpeciesNames) {
		this.boxPlotSpeciesNames = boxPlotSpeciesNames;
	}

	public List<String> getBoxPlotSpeciesNames2() {
		return boxPlotSpeciesNames2;
	}

	public void setBoxPlotSpeciesNames2(List<String> boxPlotSpeciesNames2) {
		this.boxPlotSpeciesNames2 = boxPlotSpeciesNames2;
	}

	public boxPlotSpeciesNameType getBoxPlotSpeciesOption1() {
		return boxPlotSpeciesOption1;
	}

	public void setBoxPlotSpeciesOption1(boxPlotSpeciesNameType boxPlotSpeciesOption1) {
		this.boxPlotSpeciesOption1 = boxPlotSpeciesOption1;
	}

	public boxPlotSpeciesNameType getBoxPlotSpeciesOption2() {
		return boxPlotSpeciesOption2;
	}

	public void setBoxPlotSpeciesOption2(boxPlotSpeciesNameType boxPlotSpeciesOption2) {
		this.boxPlotSpeciesOption2 = boxPlotSpeciesOption2;
	}

	public boolean isBoxPlotKeepCommonSymbols1() {
		return boxPlotKeepCommonSymbols1;
	}

	public void setBoxPlotKeepCommonSymbols1(boolean boxPlotKeepCommonSymbols1) {
		this.boxPlotKeepCommonSymbols1 = boxPlotKeepCommonSymbols1;
	}

	public boolean isBoxPlotKeepCommonSymbols2() {
		return boxPlotKeepCommonSymbols2;
	}

	public void setBoxPlotKeepCommonSymbols2(boolean boxPlotKeepCommonSymbols2) {
		this.boxPlotKeepCommonSymbols2 = boxPlotKeepCommonSymbols2;
	}

	public boolean isLevel1SVGPushWarning() {
		return level1SVGPushWarning;
	}

	public void setLevel1SVGPushWarning(boolean level1svgPushWarning) {
		level1SVGPushWarning = level1svgPushWarning;
	}

	public boolean isLevel1SVGDiffers() {
		return level1SVGDiffers;
	}

	public void setLevel1SVGDiffers(boolean level1svgDiffers) {
		level1SVGDiffers = level1svgDiffers;
	}

	public boolean isLevel2SVGPushWarning() {
		return level2SVGPushWarning;
	}

	public void setLevel2SVGPushWarning(boolean level2svgPushWarning) {
		level2SVGPushWarning = level2svgPushWarning;
	}

	public boolean isLevel2SVGDiffers() {
		return level2SVGDiffers;
	}

	public void setLevel2SVGDiffers(boolean level2svgDiffers) {
		level2SVGDiffers = level2svgDiffers;
	}

	public DualListModel<String> getLevelThreeHeatmapPickList() {
		return levelThreeHeatmapPickList;
	}

	public void setLevelThreeHeatmapPickList(DualListModel<String> levelThreeHeatmapPickList) {
		this.levelThreeHeatmapPickList = levelThreeHeatmapPickList;
	}

	public SpeciesNameType getHeatmapSpeciesNameType() {
		return heatmapSpeciesNameType;
	}

	public void setHeatmapSpeciesNameType(SpeciesNameType heatmapSpeciesNameType) {
		this.heatmapSpeciesNameType = heatmapSpeciesNameType;
	}

//	public boolean isLevelThreeBoxPlotOrtholog() {
//		return levelThreeBoxPlotOrtholog;
//	}
//
//	public void setLevelThreeBoxPlotOrtholog(boolean levelThreeBoxPlotOrtholog) {
//		this.levelThreeBoxPlotOrtholog = levelThreeBoxPlotOrtholog;
//	}
//
//	public boolean isLevelThreeBoxPlotThreatened() {
//		return levelThreeBoxPlotThreatened;
//	}
//
//	public void setLevelThreeBoxPlotThreatened(boolean levelThreeBoxPlotThreatened) {
//		this.levelThreeBoxPlotThreatened = levelThreeBoxPlotThreatened;
//	}
//
//	public boolean isLevelThreeBoxPlotEndangered() {
//		return levelThreeBoxPlotEndangered;
//	}
//
//	public void setLevelThreeBoxPlotEndangered(boolean levelThreeBoxPlotEndangered) {
//		this.levelThreeBoxPlotEndangered = levelThreeBoxPlotEndangered;
//	}
//
//	public boolean isLevelThreeBoxPlotModelOrganisms() {
//		return levelThreeBoxPlotModelOrganisms;
//	}
//
//	public void setLevelThreeBoxPlotModelOrganisms(boolean levelThreeBoxPlotModelOrganisms) {
//		this.levelThreeBoxPlotModelOrganisms = levelThreeBoxPlotModelOrganisms;
//	}

	public boolean isOrthologsAvail3() {
		return orthologsAvail3;
	}

	public void setOrthologsAvail3(boolean orthologsAvail3) {
		this.orthologsAvail3 = orthologsAvail3;
	}

	public boolean isEndangeredsAvail3() {
		return endangeredsAvail3;
	}

	public void setEndangeredsAvail3(boolean endangeredsAvail3) {
		this.endangeredsAvail3 = endangeredsAvail3;
	}

	public boolean isModelsAvail3() {
		return modelsAvail3;
	}

	public void setModelsAvail3(boolean modelsAvail3) {
		this.modelsAvail3 = modelsAvail3;
	}

	public boolean isThreatenedsAvail3() {
		return threatenedsAvail3;
	}

	public void setThreatenedsAvail3(boolean threatenedsAvail3) {
		this.threatenedsAvail3 = threatenedsAvail3;
	}

	public boolean isLevelThreeHeatmapSusceptibility() {
		return levelThreeHeatmapSusceptibility;
	}

	public void setLevelThreeHeatmapSusceptibility(boolean levelThreeHeatmapSusceptibility) {
		this.levelThreeHeatmapSusceptibility = levelThreeHeatmapSusceptibility;
	}

	public boolean isLevelThreeHeatmapAlignPrediction() {
		return levelThreeHeatmapAlignPrediction;
	}

	public void setLevelThreeHeatmapAlignPrediction(boolean levelThreeHeatmapAlignPrediction) {
		this.levelThreeHeatmapAlignPrediction = levelThreeHeatmapAlignPrediction;
	}

	public boolean isLevelThreeHeatmapAminoAcid() {
		return levelThreeHeatmapAminoAcid;
	}

	public void setLevelThreeHeatmapAminoAcid(boolean levelThreeHeatmapAminoAcid) {
		this.levelThreeHeatmapAminoAcid = levelThreeHeatmapAminoAcid;
	}

	public boolean isLevelThreeHeatMapPosition() {
		return levelThreeHeatMapPosition;
	}

	public void setLevelThreeHeatMapPosition(boolean levelThreeHeatMapPosition) {
		this.levelThreeHeatMapPosition = levelThreeHeatMapPosition;
	}

	public String getLevelThreeHeatMapJSON() {
		return levelThreeHeatMapJSON;
	}

	public void setLevelThreeHeatMapJSON(String levelThreeHeatMapJSON) {
		this.levelThreeHeatMapJSON = levelThreeHeatMapJSON;
	}

	public HeatmapReportType getHmReportType() {
		return hmReportType;
	}

	public void setHmReportType(HeatmapReportType hmReportType) {
		this.hmReportType = hmReportType;
	}

	public String getLevelThreeHeatMapLevel1ReportJSON() {
		return levelThreeHeatMapLevel1ReportJSON;
	}

	public void setLevelThreeHeatMapLevel1ReportJSON(String levelThreeHeatMapLevel1ReportJSON) {
		this.levelThreeHeatMapLevel1ReportJSON = levelThreeHeatMapLevel1ReportJSON;
	}

	public boolean isLevel3SVGPushWarning() {
		return level3SVGPushWarning;
	}

	public void setLevel3SVGPushWarning(boolean level3svgPushWarning) {
		level3SVGPushWarning = level3svgPushWarning;
	}

	public boolean isLevel3SVGDiffers() {
		return level3SVGDiffers;
	}

	public void setLevel3SVGDiffers(boolean level3svgDiffers) {
		level3SVGDiffers = level3svgDiffers;
	}

	public Integer getImageWidth3() {
		return imageWidth3;
	}

	public void setImageWidth3(Integer imageWidth3) {
		this.imageWidth3 = imageWidth3;
	}

	public Integer getImageHeight3() {
		return imageHeight3;
	}

	public void setImageHeight3(Integer imageHeight3) {
		this.imageHeight3 = imageHeight3;
	}

	public double getImageAspectRatio3() {
		return imageAspectRatio3;
	}

	public void setImageAspectRatio3(double imageAspectRatio3) {
		this.imageAspectRatio3 = imageAspectRatio3;
	}

	public ImageTypeEnum getImageType3() {
		return imageType3;
	}

	public void setImageType3(ImageTypeEnum imageType3) {
		this.imageType3 = imageType3;
	}

	public String getSvgSource3() {
		return svgSource3;
	}

	public void setSvgSource3(String svgSource3) {
		this.svgSource3 = svgSource3;
	}

	public StreamedContent getSvgBinarySource3() {
		return svgBinarySource3;
	}

	public void setSvgBinarySource3(StreamedContent svgBinarySource3) {
		this.svgBinarySource3 = svgBinarySource3;
	}

	public boolean isLevelThreeHeatmapSusceptibilityText() {
		return levelThreeHeatmapSusceptibilityText;
	}

	public void setLevelThreeHeatmapSusceptibilityText(boolean levelThreeHeatmapSusceptibilityText) {
		this.levelThreeHeatmapSusceptibilityText = levelThreeHeatmapSusceptibilityText;
	}

	public String getLevel3OptionalSelection() {
		return level3OptionalSelection;
	}

	public void setLevel3OptionalSelection(String level3OptionalSelection) {
		this.level3OptionalSelection = level3OptionalSelection;
	}

	public boolean isLevelThreeBoxPlotOrtholog() {
		return levelThreeBoxPlotOrtholog;
	}

	public void setLevelThreeBoxPlotOrtholog(boolean levelThreeBoxPlotOrtholog) {
		this.levelThreeBoxPlotOrtholog = levelThreeBoxPlotOrtholog;
	}

	public boolean isLevelThreeBoxPlotThreatened() {
		return levelThreeBoxPlotThreatened;
	}

	public void setLevelThreeBoxPlotThreatened(boolean levelThreeBoxPlotThreatened) {
		this.levelThreeBoxPlotThreatened = levelThreeBoxPlotThreatened;
	}

	public boolean isLevelThreeBoxPlotEndangered() {
		return levelThreeBoxPlotEndangered;
	}

	public void setLevelThreeBoxPlotEndangered(boolean levelThreeBoxPlotEndangered) {
		this.levelThreeBoxPlotEndangered = levelThreeBoxPlotEndangered;
	}

	public boolean isLevelThreeBoxPlotModelOrganisms() {
		return levelThreeBoxPlotModelOrganisms;
	}

	public void setLevelThreeBoxPlotModelOrganisms(boolean levelThreeBoxPlotModelOrganisms) {
		this.levelThreeBoxPlotModelOrganisms = levelThreeBoxPlotModelOrganisms;
	}

	public boolean isResetBoxPlot1() {
		return resetBoxPlot1;
	}

	public void setResetBoxPlot1(boolean resetBoxPlot1) {
		this.resetBoxPlot1 = resetBoxPlot1;
	}

	public boolean isResetBoxPlot2() {
		return resetBoxPlot2;
	}

	public void setResetBoxPlot2(boolean resetBoxPlot2) {
		this.resetBoxPlot2 = resetBoxPlot2;
	}

}

