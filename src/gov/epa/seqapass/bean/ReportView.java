package gov.epa.seqapass.bean;

//import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
//import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.apache.commons.codec.binary.Base64;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.util.IOUtils;
//import org.apache.poi.util.SystemOutLogger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIColumn;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.util.ComponentUtils;

import com.google.common.collect.Lists;

import gov.epa.seqapass.common.AminoAcid;
import gov.epa.seqapass.common.Chemical;
import gov.epa.seqapass.common.CutoffData;
import gov.epa.seqapass.common.DensityRow;
import gov.epa.seqapass.common.LevelFourAccessionRow;
import gov.epa.seqapass.common.LevelFourRequestableRow;
import gov.epa.seqapass.common.LevelFourResultRow;
import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.LevelThreeRequestableRow;
import gov.epa.seqapass.common.LevelThreeResidueResult;
import gov.epa.seqapass.common.LevelThreeViewRequest;
import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.LevelTwoRequestableRow;
import gov.epa.seqapass.common.Link;
import gov.epa.seqapass.common.Protein;
import gov.epa.seqapass.common.ReportChoiceEnum;
import gov.epa.seqapass.common.ReportInfo;
import gov.epa.seqapass.common.ReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;
import gov.epa.seqapass.common.SpeciesTaxGrouping;
import gov.epa.seqapass.common.TaxGroup;
import gov.epa.seqapass.common.UniprotMap;
import gov.epa.seqapass.common.ZipRequestable;
import gov.epa.seqapass.controller.LoginController;
import gov.epa.seqapass.controller.ReportController;
import gov.epa.seqapass.controller.RequestRunController;
import gov.epa.seqapass.model.BoxPlotSettings;
import gov.epa.seqapass.model.HeatMapSettings;
import gov.epa.seqapass.model.ReportSettings;
import gov.epa.seqapass.model.RiskAssessorLevel2Group;
import gov.epa.seqapass.model.RiskAssessorReport;
import gov.epa.seqapass.model.RiskAssessorTaxGroup;
import gov.epa.seqapass.model.SummaryReportRow;
import gov.epa.seqapass.model.User;

@ManagedBean
@SessionScoped
public class ReportView {

	// private boolean resetLevOneToPrimary;
	// private boolean resetLevTwoToPrimary;

	private int residueLimit = 50;

	private static final double defaultLevelOneEvalue = 0.01;
	private static final double defaultLevelTwoEvalue = 10;
	private static final int defaultCommonDomains = 1;
	private static final double levelThreeSizeTolerance = 30.0;

	private double primaryLevOneEvalueLimit;
	private double primaryLevTwoEvalueLimit;
	private int primaryLevOneCommonDomainLimit;
	private int selectedSpeciesCount;

	private ReportInfo latestUpdateInfo;
	private ReportInfo currentReportInfo;
	private String levelTwoRequestTip;
	private boolean showLevelTwoRequestTip;
	private String levelThreeRequestTip;
	private boolean showLevelThreeRequestTip;
	private String levelFourRequestTip;
	private boolean showLevelFourRequestTip;

	private int runId;
	private int accessionRunId;
	private String accession;
	private String topHitAccession;
	private int ortholog_count;
	private int level2_ortholog_count;
	private int full_ortholog_count;
	private int primary_ortholog_count;
	private int full_level2_ortholog_count;
	private int primary_level2_ortholog_count;
	private Date ncbiDate;
	private String blastVersion;
	private String itasserVersion;
	private String tmalignVersion;
	private Date uniprotDate;
	private Date cddDate;
	private Date cobaltDate;
	private String cobaltVersion;
	private String seqapassVersion;
	private Date levelOneReportDate;
	private String querySpecies;
	private String queryProtein;

	private String templateSpecies;
	private String templateProtein;
	
	private String sourceChoice;
	private String prevSelectedAlphaAcc;
	
	private String alphaFoldAccInput;
	private String alphaFoldProtInput;
	private String alphaFoldTaxIdInput;
	private String alphaFoldTaxGrpInput;
	private String alphaFoldSciNameInput;
	private String alphaFoldCommonNameInput;
	private String alphaFoldPDBInput;
	private String userInputL4Restraint;

	public String getPrevSelectedAlphaAcc() {
		return prevSelectedAlphaAcc;
	}

	public void setPrevSelectedAlphaAcc(String prevSelectedAlphaAcc) {
		this.prevSelectedAlphaAcc = prevSelectedAlphaAcc;
	}

	private String selectedAction;
	private ReportChoiceEnum chosenMainReportOption;
	private LevelTwoRequestableRow selectedDomain;
	private String selectedTemplate;
	private String selectedTaxGroup;
	private String levelOneFilterString; // may not be used
	private String taxFilterVal; // may not be used
	private LevelTwoRequestableRow selectedCompletedDomain; // used for domain
															// menu on level 1
															// report page
	private LevelTwoRequestableRow loadedCompletedDomain; // used once domain
															// has been
															// requested to view
	private LevelThreeRequestableRow selectedCompletedLevel3;
	private LevelThreeRequestableRow selectedLevel3Info;
	private String selectedLevel1Action;
	private String selectedLevel2Action;
	private String selectedCompletedDomainId;
	private String selectedCompletedDomainName;
	private String selectedCompletedDomainLoc;
	private String selectedCompletedDomainInfo;
	private String selectedLevel3Action;
	private String[] selectedLevel1Highlights; // may not be used
	private String queryString;

	private String chosenLevel2Cutoff;

	private boolean renderLevelButtons;
	private boolean renderLevelOne;
	private boolean renderLevelTwo;
	private boolean renderLevelThree;
	private boolean renderLevelFour;
	private boolean mainReportButtonDisabled;
	private String selectedPageURL;
	private ReportTypeEnum levelOneReportType;
	private String levelOneReportPage;
	private ReportTypeEnum levelTwoReportType;
	private String levelTwoReportPage;
	private ReportTypeEnum levelThreeReportType;
	private String levelThreeReportPage;
	private String reportOptionPage;
	private String mainReportButtonText;

	private boolean highlightPartials;
	private boolean highlightPercSim;
	private boolean highlightZeroOrtho;
	// private boolean eukaryotesOnly;
	private boolean eukaryotesOnly1;
	private boolean eukaryotesOnly2;
	private boolean disableRequestDomainButton;
	private boolean disableRequestResidueButton;
	private boolean disableRequestLevel4Buttons;
	private int isDup;

	private String infoText = "Default";
	private String infoHeaderText = "Header";

	private String isDupMessage = "This row is a duplicate";

	// Cutoff variables

	// Density Models are used for cutoff.xhtml page to display interactive
	// graph
	private LineChartModel levelOnePrimaryDensityModel;
	private LineChartModel levelOneFullDensityModel;
	private LineChartModel levelOneDensityModel;
	private LineChartModel levelTwoDensityModel;
	private LineChartModel levelTwoPrimaryDensityModel;
	private LineChartModel levelTwoFullDensityModel;
	private LineChartModel levelFourDensityModel;

	private ChartPanel levelOnePrimaryChartPanel;
	private ChartPanel levelOneFullChartPanel;
	private ChartPanel levelTwoChartPanel;
	private ChartPanel levelTwoPrimaryChartPanel;
	private ChartPanel levelTwoFullChartPanel;
	private ChartPanel levelFourChartPanel;

	// List of cutoffs available for each model
	private List<Double> levelOnePrimaryCutoffs;
	private List<Double> levelOneFullCutoffs;
	private List<Double> levelOneCutoffs;
	private List<Double> levelTwoCutoffs;
	private List<Double> levelTwoPrimaryCutoffs;
	private List<Double> levelTwoFullCutoffs;
	private List<Double> levelFourCutoffs;

	// CutoffData objects that contain all needed cutoff info
	// calculated from DensityRow lists
	private CutoffData levelOnePrimaryCutData;
	private CutoffData levelOneFullCutData;
	private CutoffData levelOneCutData;
	private CutoffData levelTwoCutData;
	private CutoffData levelTwoPrimaryCutData;
	private CutoffData levelTwoFullCutData;
	private CutoffData levelFourCutData;
	private List<UniprotMap> uniprotMap = new ArrayList<UniprotMap>();

	// current cutoff values
	private int chosenLevelOneCutoffOption;
	private double userDefinedLevOneCut;
	private double levelOnePrimaryCutValue;
	private double levelOneFullCutValue;
	private double levelOneCutValue;
	private int chosenLevelTwoCutoffOption;
	private double userDefinedLevTwoCut;
	private double levelTwoCutValue;
	private double levelTwoPrimaryCutValue;
	private double levelTwoFullCutValue;
	private double levelFourCutValue;

	// Monitor used for synchronizing zip download and reset of data table
	private static Object monitor = new Object();

	private String residuePositionText;
	private int levelThreeTaxGroup;
	private String level3JobName;
	private String level4JobName;
	private String displayedLevel4JobName;
	private boolean level4JobExists;
	private boolean level4JobSubmitted;
	private String templateText;
	private String additionalComparisonsText;
	private String queryResiduesText;
	private List<String> selectedAccessionIdStrings = new ArrayList<String>();

	private String levelOneHeaderText;
	private String levelTwoHeaderText;
	private String levelThreeHeaderText;
	private String levelThreeRunName;

	private LevelOneReportRow levelOneFullFirstRow;
	private LevelTwoReportRow levelTwoFullFirstRow;

	private LevelOneReportRow levelOnePrimaryFirstRow;
	private LevelTwoReportRow levelTwoPrimaryFirstRow;

	private boolean levelOneSpeciesReadAcross;
	private boolean levelTwoSpeciesReadAcross;

	private List<AminoAcid> AminoAcidInfo;
	private String positionBoxList;

	private String refExplorerAddName;
	private List<String> proteinXplorerList = new ArrayList<String>();
	private String xplorerSelectedProtein;
	private String scholarString;

	private String selectedAssessorTemplate;
	private List<String> availableTemplates = new ArrayList<String>();
	private List<LevelThreeRequestableRow> selectedAssessorJobs = new ArrayList<LevelThreeRequestableRow>();
	private List<LevelThreeRequestableRow> availableJobs = new ArrayList<LevelThreeRequestableRow>();
	private boolean combinedLevel3Report;
	private List<String> chosenQueryResidues = new ArrayList<String>(); // These
																		// are
																		// the
																		// position(s)/amino
																		// acid(s)
																		// from
																		// the
																		// picklist
																		// that
																		// generate
																		// the
																		// level
																		// 3
																		// report

	private boolean disableRAReport;
	private boolean level1RAReportDiffers;
	private boolean level2RAReportDiffers;
	private boolean level3RAReportDiffers;
	private boolean level1RABoxplotDiffers;
	private boolean level2RABoxplotDiffers;

	private boolean level1RAPushWarning;
	private boolean level2RAPushWarning;
	private boolean level3RAPushWarning;

	private StreamedContent lev1SettingsFile;
	private StreamedContent lev2SettingsFile;
	private StreamedContent lev3SettingsFile;

	private StreamedContent lev4FastaFile;
	private StreamedContent lev4PDBFile;
	private StreamedContent tmalignPDBFile;
	
	private String icn3dPage;

	public enum taxRanking {
		CLASS("class"), SUBCLASS("subclass"), SUPERORDER("superorder"), ORDER("order"), SUBORDER("suborder"),
		SUPERFAMILY("superfamily"), FAMILY("family"), SUBFAMILY("subfamily"), GENUS("genus");

		private String label;

		private taxRanking(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	}

	public taxRanking[] getTaxRankings() {
		return taxRanking.values();
	}

	private String levOnePrimaryTaxGroup;
	private String levTwoPrimaryTaxGroup;
	private SpeciesTaxGrouping levelOneReportTaxGrouping;
	private SpeciesTaxGrouping levelTwoReportTaxGrouping;

	List<LevelOneReportRow> defaultLevelOneReport = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> levelOneReport = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> levelOnePrimaryReport = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> filteredLevelOneReport = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> filteredLevelOnePrimaryReport = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> selectedLevelOneRows = new ArrayList<LevelOneReportRow>();
	List<LevelOneReportRow> selectedLevelOnePrimaryRows = new ArrayList<LevelOneReportRow>();
	List<LevelTwoReportRow> defaultLevelTwoReport = new ArrayList<LevelTwoReportRow>();
	List<LevelTwoReportRow> levelTwoReport = new ArrayList<LevelTwoReportRow>();
	List<LevelTwoReportRow> levelTwoPrimaryReport = new ArrayList<LevelTwoReportRow>();
	List<LevelTwoReportRow> filteredLevelTwoReport = new ArrayList<LevelTwoReportRow>();
	List<LevelTwoReportRow> filteredLevelTwoPrimaryReport = new ArrayList<LevelTwoReportRow>();

	List<LevelThreeReportRow> levelThreeReport = new ArrayList<LevelThreeReportRow>();
	List<LevelThreeReportRow> filteredLevelThreeReport = new ArrayList<LevelThreeReportRow>();

	List<LevelFourAccessionRow> levelFourAccessions = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> selectedLevelFourAccessions = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> filteredLevelFourAccessions = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> prevSelectedLevelFourAccessions = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> levelFourFASTAs = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> selectedLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> prevSelectedLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> filteredLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> levelFourReport = new ArrayList<LevelFourAccessionRow>();
	List<LevelFourAccessionRow> selectedLevelFourReportRows = new ArrayList<LevelFourAccessionRow>();
	String levelFourTemplate;
	String levelFourTemplateName;
//	LevelFourRequestableRow loadedCompletedL4Run = new LevelFourRequestableRow();
	LevelFourAccessionRow selectedPDBAccessionRow = new LevelFourAccessionRow();
	List<LevelFourResultRow> levelFourTMAlignReport = new ArrayList<LevelFourResultRow>();
	List<LevelFourResultRow> levelFourTMAlignSelectionReport = new ArrayList<LevelFourResultRow>();
	List<LevelFourResultRow> selectedTMAlignSelectionReportRows = new ArrayList<LevelFourResultRow>();
	List<LevelFourRequestableRow> tmAlignQueryAccs = new ArrayList<LevelFourRequestableRow>();
	LevelFourRequestableRow selectedTMAlignQueryAcc;
	List<LevelFourRequestableRow> tmalignReportChoices = new ArrayList<LevelFourRequestableRow>();
	LevelFourRequestableRow selectedTMAlignReportChoice = new LevelFourRequestableRow();
	List<LevelFourAccessionRow> levelFourAlphaFoldReport = new ArrayList<LevelFourAccessionRow>();
	LevelFourAccessionRow selectedAlphaFoldRow = new LevelFourAccessionRow();
	
	String chosenLevelFourLevel;
	String chosenLevelFourViewLevel;
	private LevelTwoRequestableRow selectedLevelFourDomain = new LevelTwoRequestableRow();
	private boolean levelFourDomainVisible;
	private boolean disableL4PrioritizeBtn;
	private List<LevelTwoReportRow> itasserLevelTwoReport = new ArrayList<LevelTwoReportRow>();


	boolean updatePriorities;
	boolean updateFilteredFASTAs;

	List<ReportRow> reportList = new ArrayList<ReportRow>();
	List<ReportRow> filteredReportList = new ArrayList<ReportRow>();
	ReportRow selectedReport = new ReportRow();

	List<ReportRow> selectedReports = new ArrayList<ReportRow>();
	List<ReportRow> filteredDownloadList = new ArrayList<ReportRow>();

	List<SummaryReportRow> levelOneSummaryReport = new ArrayList<SummaryReportRow>();
	List<SummaryReportRow> levelTwoSummaryReport = new ArrayList<SummaryReportRow>();
	List<SummaryReportRow> levelThreeSummaryReport = new ArrayList<SummaryReportRow>();

	List<LevelTwoRequestableRow> levelTwoDomains = new ArrayList<LevelTwoRequestableRow>();
	List<LevelTwoRequestableRow> completedLevelTwoDomains = new ArrayList<LevelTwoRequestableRow>();

	List<LevelThreeRequestableRow> completedLevelThreeRuns = new ArrayList<LevelThreeRequestableRow>();

	List<LevelFourRequestableRow> createdLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	LevelFourRequestableRow selectedCreatedLevelFourRun;
	List<LevelFourRequestableRow> startedLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	List<LevelFourRequestableRow> filteredStartedLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	LevelFourRequestableRow selectedStartedLevelFourRun;
	List<LevelFourRequestableRow> selectedCombineLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	List<LevelFourRequestableRow> selectedLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	List<LevelFourRequestableRow> loadedLevelFourRuns = new ArrayList<LevelFourRequestableRow>();
	int levelFourSourceLevel;
	String icn3dPDB;
	String itasserBackground;
    private String[] selectedIcn3dChains;
    private List<String> icn3dChains = new ArrayList<String>();
    

	private List<String> residueHeaders = new ArrayList<String>();
	private List<String> primaryResidueHeaders = new ArrayList<String>();
	private List<String> summaryResidueHeaders = new ArrayList<String>();
	List<String> taxonomyGroup = new ArrayList<String>();
	List<String> taxonomyGroup2 = new ArrayList<String>();

	List<String> taxonomyGroupByPercSim = new ArrayList<String>();
	List<String> taxonomyGroup2ByPercSim = new ArrayList<String>();

	// ecotox variables
//	private Map<String, Boolean> ecotoxTaxMap = new LinkedHashMap<String, Boolean>();
	private List<RiskAssessorTaxGroup> ecotoxTaxGroups = new ArrayList<RiskAssessorTaxGroup>();
	// private List<String> ecotoxTaxGroups = new ArrayList<String>();
	// private List<Boolean> ecotoxTaxGroupsDisable = new ArrayList<Boolean>();
	private List<RiskAssessorTaxGroup> ecotoxSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
	// private List<String> ecotoxSelectedTaxGroups = new ArrayList<String>();
	private List<RiskAssessorTaxGroup> ecotoxSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>();
//	private List<String> ecotoxPrevSelectedTaxGroups = new ArrayList<String>();
	private List<RiskAssessorTaxGroup> ecotoxPrevSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>();
	private List<RiskAssessorTaxGroup> ecotoxSelectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
	private RiskAssessorTaxGroup ecotoxQuerySpecies;
	private Integer ecotoxSpeciesNameType;
	private String ecotoxSortTaxGroup;
	private boolean showEcotoxSpeciesPanel;
	private Chemical ecotoxSearchChemical;
	private List<Chemical> ecotoxChemicalList;
	private List<Chemical> ecotoxPossibleChemicalList;
	private List<Chemical> ecotoxSelectedChemicalList;
	private String ecotoxURL;
	private int icn3dPopupSize;

	private boolean updateLevOneBox;
	private boolean updateLevTwoBox;

	// Map<Integer, Integer> sequenceMap = new LinkedHashMap<Integer,
	// Integer>();
	List<Map<Integer, Integer>> sequenceMaps = new ArrayList<Map<Integer, Integer>>();

	private DualListModel<String> levelThreePickList;

	NumberFormat scientificFormatter = new DecimalFormat("0.000E0");
	DecimalFormat oneDecimalDigitFormatter = new DecimalFormat("#.0");
	DecimalFormat twoDecimalDigitFormatter = new DecimalFormat("#.00");

	@PostConstruct
	public void init() {

		setReportList(ReportController.getMainReportForUser());

		filteredReportList.clear();
		for (int i = 0; i < reportList.size(); i++) {
			filteredReportList.add(reportList.get(i));
		}

		filteredDownloadList.clear();
		for (int i = 0; i < reportList.size(); i++) {
			filteredDownloadList.add(reportList.get(i));
		}

		setDisableRAReport(true);

		setChosenMainReportOption(ReportChoiceEnum.View);
		changeMainReportOption();
		setSelectedAction("");
		setChosenLevel2Cutoff("0");
		setSelectedTemplate("");
		setLevelOneReportType(ReportTypeEnum.Primary);
		setRenderLevelButtons(false);
		setRenderLevelOne(false);
		setRenderLevelTwo(false);
		setRenderLevelThree(false);
		setRenderLevelFour(false);
		setSelectedPageURL("main_report.xhtml");
		setLevelOneReportPage("levelOnePrimaryReport.xhtml");
		setLevelTwoReportType(ReportTypeEnum.Primary);
		setLevelTwoReportPage("levelTwoPrimaryReport.xhtml");
		setLevelThreeReportType(ReportTypeEnum.Primary);
		setLevelThreeReportPage("levelThreePrimaryReport.xhtml");
		setRunId(-1); // initialize runId, accession, accessionRunId, &
						// selectedDomainId as unspecified
		setAccession(null);
		setAccessionRunId(-1);
		setHighlightPartials(true);
		setHighlightPercSim(true);
		setHighlightZeroOrtho(true);
		// setEukaryotesOnly(true);
		setEukaryotesOnly1(true);
		setEukaryotesOnly2(true);
		setChosenLevelOneCutoffOption(1);
		setChosenLevelTwoCutoffOption(1);
		setQueryResiduesText("No Residues Selected");
		setLevelOneHeaderText("Level 1 Data - Primary");
		setLevelTwoHeaderText("Level 2 Data - Primary");
		setLevelThreeHeaderText("Level 3 Data - Primary");
		setDisableRequestDomainButton(true);

		setNcbiDate(new Date(ReportController.getNcbiDate()));

		// setResetLevOneToPrimary(true);
		// setResetLevTwoToPrimary(true);
		setLevelOneDefaults();
		setLevelTwoDefaults();
		setMainReportButtonDisabled(true);

		setAminoAcidInfo(ReportController.getAminoAcidInfo());

		initRAReportButtonVars();

		ecotoxSpeciesNameType = 0;
		showEcotoxSpeciesPanel = true;
		ecotoxPossibleChemicalList = new ArrayList<Chemical>();
		ecotoxChemicalList = new ArrayList<Chemical>();
		ecotoxSelectedChemicalList = new ArrayList<Chemical>();

		// level1RAReportDiffers = true;
		// level2RAReportDiffers = true;
		// level3RAReportDiffers = true;
		//
		// level1RABoxplotDiffers = true;
		// level2RABoxplotDiffers = true;
		//
		// level1RAPushWarning = false;
		// level2RAPushWarning = false;

		// loadRefXplorer();
		setLevel4JobExists(false); // TODO update to check
		setLevel4JobSubmitted(false);

		updatePriorities = false;
		updateFilteredFASTAs = false;

		updateLevOneBox = true;
		updateLevTwoBox = true;
		
		levelFourDomainVisible = false;
		disableL4PrioritizeBtn = false;
		chosenLevelFourLevel = "Level1";
		chosenLevelFourViewLevel = "Level1";
		icn3dPDB = "";
		
	}

	public void initRAReportButtonVars() {
		level1RAReportDiffers = true;
		level2RAReportDiffers = true;
		level3RAReportDiffers = true;

		level1RABoxplotDiffers = true;
		level2RABoxplotDiffers = true;

		level1RAPushWarning = false;
		level2RAPushWarning = false;
	}

	/**
	 * submits a level 2 run. It expects the selectedDomain object to be populated.
	 */
	public void requestLevel2Button() {

		// get the latest update info
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		RequestRunView requestRunView = (RequestRunView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "requestRunView");

		if (requestRunView.displaySubmitMessage()) {

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			User theUser = (User) session.getAttribute("UserInfo");

			String status = RequestRunController.requestLevelTwoRun(selectedDomain.getRunId(), selectedDomain.getKey(),
					selectedDomain.getStartPosition(), theUser.getUserid());

			context.addMessage("growl", new FacesMessage("Level 2 Run Requested", "Status " + status));
		} else {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Submissions Disabled", "Please try again later."));
		}
	}

	public void setL3DefaultPriority() {

		List<LevelOneReportRow> report = new ArrayList<LevelOneReportRow>();
		List<LevelOneReportRow> selectedReport = new ArrayList<LevelOneReportRow>();
		List<LevelOneReportRow> newSelections = new ArrayList<LevelOneReportRow>();

		switch (levelOneReportType) {
		case Primary:
			report = filteredLevelOnePrimaryReport;
			selectedReport = selectedLevelOnePrimaryRows;
			break;
		case Full:
			report = filteredLevelOneReport;
			selectedReport = selectedLevelOneRows;
			break;
		default:
			return;
		}

		// add all selected rows that are not currently part of filtered report
		// this is needed so that this subroutine only modifies rows that are part of
		// filtered
		// report, leaving all other rows (and their selections) unchanged.
		for (LevelOneReportRow row : selectedReport) {
			if (!report.contains(row)) {
				newSelections.add(row);
			}
		}

		// add new selections based on default prioritization algorithm
		for (LevelOneReportRow row : report) {
			String status = determineAccessionPriority(row.getAccession(), row.getProteinName(), row.getCommonName(),
					true);
			// System.out.println(row.getAccession() + " with status: " + status);
			if (status.toLowerCase().equals("high")) {
				newSelections.add(row);
			}
		}

		// update selected rows for table
		switch (levelOneReportType) {
		case Primary:
			selectedLevelOnePrimaryRows = new ArrayList<LevelOneReportRow>(newSelections);
			break;
		case Full:
			selectedLevelOneRows = new ArrayList<LevelOneReportRow>(newSelections);
			break;
		default:
			return;
		}

		// update selectedSpeciesCount
		checkBoxListener();

	}

	/**
	 * submits a level 3 run
	 */
	public void requestLevel3Button() {
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		RequestRunView requestRunView = (RequestRunView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "requestRunView");

		boolean validated = true;

		if (requestRunView.displaySubmitMessage()) {

			if (templateText.isEmpty() || level3JobName.isEmpty()) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"You must specify a Template Sequence and Level 3 Run Name"));
				return;
			} else if (selectedSpeciesCount == 0 && StringUtils.isBlank(additionalComparisonsText)) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"You must select sequences from the Level 1 Data table or enter Additional Comparisons to request a Level 3 Run"));
				return;
			}

			// Validation
			Pattern jobNamePattern = Pattern.compile("[^a-zA-Z0-9_ -]");
			Pattern templateHeaderPattern = Pattern.compile("\t");
			// if
			// (!gapsRemoved.matches("^[^\\n]+\\n[ACDEFGHIKLMNPQRSTUVWXY\\n]+$"))
			// {

			// Pattern templateBodyPattern =
			// Pattern.compile("[^aAc-iC-Ik-nK-Np-tP-Tv-yV-Y-]");
			Pattern templateBodyPattern = Pattern.compile("[^aAc-iC-Ik-nK-Np-yP-Y-]");
			Pattern additionalBodyPattern = Pattern.compile("[^a-zA-Z0-9_ -\\.]");
			String templateLines[] = templateText.replaceAll("\\r", "").split("\\n");
			for (int i = 0; i < templateLines.length; i++) {
				if (i == 0) {
					Matcher m = templateHeaderPattern.matcher(templateLines[0]);
					if (m.find()) {
						validated = false;
						context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
								"No tabs allowed in Template Sequence."));
					}
				} else {
					Matcher m = templateBodyPattern.matcher(templateLines[i]);
					if (m.find()) {
						validated = false;
						context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
								"Only valid amino acids (not including B,J,O,Z) are allowed in Template Sequence Body.  No spaces"));
					}
				}
			}

			// Trim leading and trailing spaces for better filenames
			level3JobName = level3JobName.trim();
			Matcher jobNameMatcher = jobNamePattern.matcher(level3JobName);
			if (level3JobName.length() > 50) {
				validated = false;
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Level 3 Run Name must be less than 50 characters."));
			}
			if (jobNameMatcher.find()) {
				validated = false;
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Only alphanumeric and _ - <space> allowed in Level 3 Run Name"));
			}

			// Add validation for additionalComparisons here
			String additionalLines[] = additionalComparisonsText.replaceAll("\\r", "").split("\\n");
			for (int i = 0; i < additionalLines.length; i++) {
				// use templateHeaderPattern for comment line of fasta
				if (additionalLines[i].startsWith(">")) {
					Matcher m = templateHeaderPattern.matcher(additionalLines[i]);
					if (m.find()) {
						validated = false;
						context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
								"No tabs allowed in Template Sequence."));
						System.out.println("Header Error in line " + i);
					}
				}
				// assume either fasta sequence or accession
				else {
					Matcher m = additionalBodyPattern.matcher(additionalLines[i]);
					if (m.find()) {
						validated = false;
						context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
								"Only alphanumeric and _ - . <space> allowed in Level 3 Additional Comparisons"));
						System.out.println("Body Error in line " + i);
					}
				}
			}

			if (!validated) {
				return;
			}

			selectedAccessionIdStrings.clear();
			if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
				for (LevelOneReportRow row : selectedLevelOnePrimaryRows) {
					selectedAccessionIdStrings.add(row.getAccession());
				}
			} else {
				for (LevelOneReportRow row : selectedLevelOneRows) {
					selectedAccessionIdStrings.add(row.getAccession());
				}
			}
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			User theUser = (User) session.getAttribute("UserInfo");
			LevelThreeRequestableRow request = new LevelThreeRequestableRow(accessionRunId, theUser.getUserid(),
					level3JobName, templateText, selectedAccessionIdStrings, additionalComparisonsText);

			String status = RequestRunController.requestLevelThreeRun(request);
			clearLevelThreePanel();
			updateFilter();
			if (status.toLowerCase().equals("this run name is already selected")) {
				context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", status));
			} else {
				context.addMessage("growl", new FacesMessage("Level 3 Run Requested", "Status " + status));
			}
		} else {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Submissions Disabled", "Please try again later."));
		}
	}

	/**
	 * Retrieves level one report via two cases:
	 * <p>
	 * <ul>
	 * <li>case 0: downloading a single report for displaying via the SeqAPASS GUI
	 * <li>case 1: downloading multiple reports in zip format
	 * </ul>
	 */
	public void requestSelectedButton() {
		switch (chosenMainReportOption) {
		// View Report
		case View:
			if (selectedReport != null) {
				setLevelOneDefaults();
				setLevelTwoDefaults();
				setRenderLevelButtons(true);
				setRenderLevelOne(true);
				setRenderLevelTwo(false);
				setRenderLevelThree(false);
				setRenderLevelFour(false);
				setHighlightPartials(true);
				setHighlightPercSim(true);
				setHighlightZeroOrtho(true);
				// setEukaryotesOnly(true);
				setRunId(selectedReport.getRunId());
				setAccessionRunId(selectedReport.getAccessionRunId());
				setAccession(selectedReport.getAccession());
				setQuerySpecies(selectedReport.getQuerySpeciesName());
				setQueryProtein(selectedReport.getQueryProtein());

				setDefaultLevelOneReport(ReportController.getLevelOneReportForUser(accessionRunId));

				// setEukaryotesOnly(defaultLevelOneReport.get(0).isEukaryote());
				setEukaryotesOnly1(defaultLevelOneReport.get(0).isEukaryote());
				setEukaryotesOnly2(isEukaryotesOnly1());

				// setLevelOneReport(ReportController.getLevelOneReportForUser(accessionRunId));

				// loadLevelOneReportsWithEukaryoteChoice();
				setLevelOneReport(filterLevOneEukaryote(getDefaultLevelOneReport()));
				filteredLevelOneReport.clear();
				for (int i = 0; i < levelOneReport.size(); i++) {
					filteredLevelOneReport.add(levelOneReport.get(i));
				}
				System.out.println("default report size: " + getDefaultLevelOneReport().size());
				System.out.println("full report size: " + getLevelOneReport().size());

				updateLevOneReport();

				setCurrentReportInfo(ReportController.getReportInfo(accessionRunId, -1, -1, -1));
				setNcbiDate(new Date(getCurrentReportInfo().getNcbiDate()));
				setBlastVersion(getCurrentReportInfo().getBlastVersion());
				setSeqapassVersion(getCurrentReportInfo().getSeqapassVersion());

				// get the latest update info
				ELContext elContext = FacesContext.getCurrentInstance().getELContext();
				UserLoginView userLoginView = (UserLoginView) FacesContext.getCurrentInstance().getApplication()
						.getELResolver().getValue(elContext, null, "userLoginView");
				userLoginView.setUpdateInfo(LoginController.getUpdateInfo());
				setLatestUpdateInfo(userLoginView.getUpdateInfo().get(0));

				if (levelOneReport.size() > 0) {
					setLevelOneFullFirstRow(levelOneReport.get(0));
					setLevelOnePrimaryFirstRow(levelOneReport.get(0));
					setTopHitAccession(levelOneReport.get(0).getAccession());
				}

				updateLevOneOrthologCount();
				setOrtholog_count(getPrimary_ortholog_count());

				changeToLevelOnePage(true);
				setLevelOnePrimaryDensityModel(null);
				setSelectedTaxGroup("");

				// init cutoff objects
				setLevelOnePrimaryCutoffs(null);
				setLevelOnePrimaryCutData(null);
				setChosenLevelOneCutoffOption(1);
				setChosenLevelTwoCutoffOption(1);
				// setUserDefinedLevOneCut(0);

				// init level 2 and 3 objects
				reloadLevelTwoAndThreeResults();
				clearLevelThreePanel();

				// init level 4 objects
				reloadLevelFourResults();

				// init level one cutoff
				try {
					genLevelOnePrimaryCutoff(accessionRunId, defaultLevelOneEvalue, defaultCommonDomains);
					genLevelOneFullCutoff(accessionRunId);
					// always set to primary report on load
					if (levelOnePrimaryCutoffs != null) {
						setUserDefinedLevOneCut(
								Double.parseDouble(twoDecimalDigitFormatter.format(levelOnePrimaryCutoffs.get(0))));
					} else {
						setUserDefinedLevOneCut(100.0);
					}
					System.out.println("User defined level one cutoff = " + getUserDefinedLevOneCut());
					updateLevOneCutoff();
				} catch (Exception e) {
					System.out.println("Error in generating level one cutoff");
				}

				positionBoxList = "";

				// reset variables for visualization bean
				VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
						.getELResolver().getValue(elContext, null, "visualizationView");
//				vizView.setResetBoxPlot1(true);
				vizView.init();

				// System.out.println("In requestSelected");
				// System.out.println("LevelOnePrimaryReport.get(0) cutoff: " +
				// levelOnePrimaryReport.get(0).getCutoff());
				// System.out.println("LevelOnePrimaryFirstRow cutoff: " +
				// levelOnePrimaryFirstRow.getCutoff());

				// reset RA report
				disableRAReport = true;
				RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
						.getELResolver().getValue(elContext, null, "riskAssessorView");
				raReportView.resetRAReport();
				initRAReportButtonVars();

			}

			break;
		// Download Report(s)
		case Save:

			// call webservice to download and zip appropriate files

			// create necessary directory/file structure for downloads
			String zipFile = "seqapass.zip";

			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			User theUser = (User) session.getAttribute("UserInfo");

			List<ZipRequestable> zipRequests = new ArrayList<ZipRequestable>();
			// iterate through download choices
			for (ReportRow row : selectedReports) {
//				zipRequests.add(new ZipRequestable(theUser.getUserid(), row.getAccessionRunId(), row.getAccession(),
//						row.isLev1ChkBox(), row.isLev2ChkBox(), row.isLev3ChkBox()));
				zipRequests.add(new ZipRequestable(theUser.getUserid(), row.getAccessionRunId(), row.getAccession(),
						true, true, true));
			}

			byte[] files = null;

			if (zipRequests.size() != 0) {
				files = ReportController.requestZippedReports(zipRequests);
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

			response.reset();
			response.setContentType("application/zip");
			response.setHeader("Content-disposition", "attachment; filename=" + zipFile);

			if (files != null) {
				ServletOutputStream output;
				try {
					output = response.getOutputStream();
					output.write(files);
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			facesContext.responseComplete();
			selectedReports.clear();
			synchronized (monitor) {
				monitor.notifyAll();
			}

			break;
		default:
			System.out.println("ERROR: Unknown option chosen:" + chosenMainReportOption);
		}

	}

	/**
	 * displays growl message and clears checkboxes after multi-report download
	 */
	public void displayMultiReportSaveMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage("growl", new FacesMessage("Download", "Your download will begin momentarily"));

		while (!selectedReports.isEmpty()) {
			System.out.println("Waiting for zip operation to complete....");
			synchronized (monitor) {
				try {
					monitor.wait();

				} catch (Exception e) {

				}
			}
		}

		resetSelectedReports();
	}

	/**
	 * Clears all report row checkboxes for main report page
	 */
	public void resetSelectedReports() {
		selectedReports.clear();
		for (ReportRow row : reportList) {
			row.setLev1ChkBox(false);
			row.setLev2ChkBox(false);
			row.setLev3ChkBox(false);
		}
		mainReportButtonDisabled = true;
	}

	/**
	 * Displays the level 2 report page
	 */
	public void viewLevel2Button() {

		if (selectedCompletedDomain == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select domain from drop-down"));
			return;
		} else if (selectedCompletedDomain.getLevel2RunId() > 0) {
			setLevelTwoDefaults();

			loadedCompletedDomain = LevelTwoRequestableRow.newInstance(selectedCompletedDomain);

			setDefaultLevelTwoReport(ReportController.getLevelTwoReportForUser(selectedCompletedDomain.getRunId(),
					selectedCompletedDomain.getLevel2RunId()));

			setLevelTwoReport(filterLevTwoEukaryote(getDefaultLevelTwoReport()));
			filteredLevelTwoReport.clear();
			for (int i = 0; i < levelTwoReport.size(); i++) {
				filteredLevelTwoReport.add(levelTwoReport.get(i));
			}
			updateLevTwoReport();

			updateLevTwoOrthologCount();
			updateLevTwoCutoff();

			if (levelTwoReport.size() > 0) {
				setLevelTwoFullFirstRow(levelTwoReport.get(0));
				setLevelTwoPrimaryFirstRow(levelTwoReport.get(0));
			}

			String displayText = selectedCompletedDomain.getDisplayText();
			String[] domainParts = displayText.split(",", 3);
			setSelectedCompletedDomainId(domainParts[0].split("\\) ", 2)[1]);
			setSelectedCompletedDomainName(domainParts[1]);
			setChosenLevelTwoCutoffOption(1);

			setSelectedCompletedDomainLoc(selectedCompletedDomain.getDisplayText().split(selectedCompletedDomainId)[0]);
			// setSelectedCompletedDomainInfo(
			// selectedCompletedDomain.getDisplayText().split(selectedCompletedDomainName)[1]);
			selectedCompletedDomainInfo = selectedCompletedDomain.getDisplayText()
					.split(selectedCompletedDomainName)[1];
			if (selectedCompletedDomainInfo.endsWith(",")) {
				selectedCompletedDomainInfo += "...";
			}

			updateLevTwoOrthologCount();
			setLevel2_ortholog_count(getPrimary_level2_ortholog_count());

			ReportInfo info = ReportController.getReportInfo(selectedReport.getAccessionRunId(),
					selectedCompletedDomain.getLevel2RunId(), -1, -1);
			setCddDate(new Date(info.getCddDate()));

			try {
				genLevelTwoPrimaryCutoff(defaultLevelTwoEvalue);
				genLevelTwoFullCutoff();
				if (levelTwoPrimaryCutoffs != null) {
					// always set to primary report on load
					setUserDefinedLevTwoCut(
							Double.parseDouble(twoDecimalDigitFormatter.format(levelTwoPrimaryCutoffs.get(0))));
				} else {
					System.out.println("levelTwoPrimaryCutofss is null!!!!");
					setUserDefinedLevTwoCut(100.0);
				}
				updateLevTwoCutoff();
			} catch (Exception e) {
				System.out.println("error in generating level 2 cutoff" + e.getMessage());
			}

			setRenderLevelTwo(true);
			changeToLevelTwoPage(true);
		}

		// reset variables for visualization bean
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		// vizView.init();
		vizView.setResetBoxPlot2(true);
		vizView.resetLevelTwoViz();

		// check RA report for previously loaded domain
		checkDiffLevel2RAReport();

	}

	/**
	 * Displays the level 3 report page
	 */
	public void viewLevel3Button() {

		System.out.println("inside viewLevel3Button");
		combinedLevel3Report = false;

		if (selectedCompletedLevel3 == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select level 3 run from drop-down"));
			return;
		}

		// store info in a different object so control can be reset
		selectedLevel3Info = selectedCompletedLevel3;
		levelThreeRunName = selectedLevel3Info.getJobName();
		if (selectedLevel3Info.getTemplate() != null && !selectedLevel3Info.getTemplate().isEmpty()) {
			try {
				Protein protein = ReportController.getProtein(selectedLevel3Info.getTemplate());
				templateProtein = protein.getDisplayName();
				templateSpecies = ReportController.getSciNameAtTaxId(selectedLevel3Info.getTemplate(), "species");
			} catch (Exception e) {
				templateProtein = selectedLevel3Info.getTemplate();
				templateSpecies = "NA";
			}
		} else {
			templateProtein = "User Defined";
			templateSpecies = "NA";
		}

		// Get template sequence, split to list while adding position and left
		// padding to strings
		String sequence = ReportController.requestLevelThreeSequence(selectedLevel3Info.getLevel3RunId());

		// 1st integer represents location with all (-) removed, 2nd integer
		// represents actual location in sequence returned from cobalt,
		// sequenceMap.clear();
		sequenceMaps.clear();
		Map<Integer, Integer> sequenceMap = new LinkedHashMap<Integer, Integer>();

		List<String> pickListSource = new ArrayList<String>();
		int longest = String.valueOf(sequence.length()).length() + 1;
		String formatStatement = "%" + longest + "s";
		int loc = 0;
		System.out.println("Generating picklist");
		for (int i = 0; i < sequence.length(); i++) {
			if (!Character.toString(sequence.charAt(i)).equals("-")) {
				loc++;
				String newString = String.valueOf(loc) + sequence.charAt(i);
				newString = String.format(formatStatement, newString);
				System.out.println("Adding: " + String.format(formatStatement, newString));
				pickListSource.add(String.format(formatStatement, newString));
				sequenceMap.put(loc, i + 1);
			}
		}
		sequenceMaps.add(sequenceMap);
		List<String> pickListTarget = new ArrayList<String>();
		setLevelThreePickList(new DualListModel<String>(pickListSource, pickListTarget));

		List<Integer> posList = new ArrayList<Integer>();
		LevelThreeViewRequest request = new LevelThreeViewRequest(selectedLevel3Info.getLevel3RunId(), posList);

		System.out.println(request.toString());
		setLevelThreeReport(ReportController.getLevelThreeReportForUser(request));

		for (LevelOneReportRow oneRow : levelOneReport) {
			for (LevelThreeReportRow threeRow : levelThreeReport) {
				if (threeRow.getAccession().equals(oneRow.getAccession())) {
					threeRow.setSpeciesTaxId(oneRow.getSpeciesTaxId());
					threeRow.setProteinCount(oneRow.getProteinCount());
					threeRow.setTaxonomyName(oneRow.getTaxonomyName());
					threeRow.setScientificName(oneRow.getScientificName());
					threeRow.setCommonName(oneRow.getCommonName());
					threeRow.setProteinName(oneRow.getProteinName());
				}
			}
		}

		filteredLevelThreeReport.clear();
		for (int i = 0; i < levelThreeReport.size(); i++) {
			filteredLevelThreeReport.add(levelThreeReport.get(i));
		}

		ReportInfo info = ReportController.getReportInfo(selectedLevel3Info.getAccessionRunId(), -1,
				selectedLevel3Info.getLevel3RunId(), -1);
		setCobaltDate(new Date(info.getCobaltDate()));
		setCobaltVersion(info.getCobaltVersion());

		residueHeaders.clear();
		primaryResidueHeaders.clear();
		queryResiduesText = "No Residues Selected";
		setRenderLevelThree(true);
		changeToLevelThreePage();

		System.out.println("finished viewLevel3Button");
	}

	public void viewLevel4Button() {
		System.out.println("Inside viewLevel4Button");

		if (selectedStartedLevelFourRun == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select L4 run from drop-down"));
			return;
		} else if (selectedStartedLevelFourRun.getLevel4RunId() > 0) {

			LevelFourRequestableRow loadedCompletedL4Run = LevelFourRequestableRow
					.newInstance(selectedStartedLevelFourRun);
			selectedCombineLevelFourRuns.clear();
			selectedCombineLevelFourRuns.add(loadedCompletedL4Run);

			viewCombinedLevel4Button();

		}

	}

	public List<LevelFourResultRow> combineLevel4DataAndResults(List<LevelFourAccessionRow> data,
			List<LevelFourResultRow> results) {

		List<LevelFourResultRow> newList = new ArrayList<LevelFourResultRow>();

		for (LevelFourResultRow l4res : results) {
			if (l4res.getPdbSource() == null || l4res.getPdbSource().equals("I-TASSER")) {
				for (LevelFourAccessionRow l4data : data) {
					// null pdbSource means I-TASSER
					if (l4data.getNcbiAccession().equals(l4res.getAcc2())
							&& Objects.equals(l4data.getTemplate(), l4res.getTemplate())) {

						LevelFourResultRow newRow = new LevelFourResultRow();
						newRow = new LevelFourResultRow(l4res, l4data);
						newList.add(newRow);
						break;
					}
				}
			} else {
				LevelFourResultRow newRow = LevelFourResultRow.newInstance(l4res);
				newRow.setQuality("-");
				newRow.setCscore(-9999);
				newRow.setTm_score(-9999);
				newRow.setRmsd(-9999);
				newRow.setDensity(-9999);
				newRow.setPdb(l4res.getPdb());
				newList.add(newRow);
			}
		}

		return newList;
		
	}

	public void viewCombinedLevel4Button() {
		
		levelFourCutValue = 0.0;
		
		String displayName = "";
		
		loadedLevelFourRuns = selectedCombineLevelFourRuns;
		
		//Check that all runs belong to same level
		levelFourSourceLevel = loadedLevelFourRuns.get(0).getSourceLevel();
		for (LevelFourRequestableRow requestRow : loadedLevelFourRuns) {
			if (requestRow.getSourceLevel() != levelFourSourceLevel) {
				System.out.println("Error in viewCombinedLevel4Button: all level 4 runs should be the same source level");
				return;
			}
		}
		
		if (selectedCombineLevelFourRuns.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select L4 run from drop-down"));
			return;
		} else {

			Set<Triple<String, String, String>> queryTemplateStatusList = new HashSet<Triple<String, String, String>>();

			List<LevelFourAccessionRow> finalCombineLevelFourRuns = new ArrayList<LevelFourAccessionRow>();
			int count = 1;
			for (LevelFourRequestableRow requestRow : selectedCombineLevelFourRuns) {
				displayName += requestRow.getDisplayName();
				if (count != selectedCombineLevelFourRuns.size())
					displayName += ",";
				LevelFourRequestableRow newRow = LevelFourRequestableRow.newInstance(requestRow);
				List<LevelFourAccessionRow> dataRows = ReportController.getLevel4Data(newRow.getLevel4RunId());
				for (LevelFourAccessionRow dataRow : dataRows) {
					// add row to final if unique set of queryAcc, template, and status
					// goal is to get unique queryAcc/template combinations but allowing for
					// complete and incomplete runs
					if (queryTemplateStatusList
							.add(Triple.of(dataRow.getNcbiAccession(), dataRow.getTemplate(), dataRow.getStatus()))) {
						finalCombineLevelFourRuns.add(dataRow);
					}

				}
				count++;
			}

			Comparator<LevelFourAccessionRow> compareByCScore = Comparator.comparing(LevelFourAccessionRow::getCscore)
					.reversed();
			List<LevelFourAccessionRow> sortedfinal = finalCombineLevelFourRuns.stream().sorted(compareByCScore)
					.collect(Collectors.toList());

			setLevelFourReport(sortedfinal);

			if (levelFourSourceLevel == 1) {
				levelFourReport = combineLevelOneLevel4(levelOneReport, levelFourReport);
			} else if (levelFourSourceLevel == 2) {
				//Currently assumes all level4 runs use the same level2 run as source
				LevelTwoRequestableRow level2Domain = completedLevelTwoDomains.stream().filter(completedLevelTwoDomains -> selectedCombineLevelFourRuns.get(0).getLevel2RunId() == completedLevelTwoDomains.getLevel2RunId()).findAny().orElse(null);
				String displayText = level2Domain.getDisplayText();
				String[] domainParts = displayText.split(",",3);
				String domainId = domainParts[0].split("\\) ", 2)[1];
				String domainName = domainParts[1];
				List<LevelTwoReportRow> tmpList = ReportController.getLevelTwoReportForUser(loadedLevelFourRuns.get(0).getAccessionRunId(),
						selectedCombineLevelFourRuns.get(0).getLevel2RunId());
				itasserLevelTwoReport = filterLevTwoPrimaryReport(tmpList);
				for (LevelTwoReportRow row : itasserLevelTwoReport) {
					row.setDomainId(domainId);
					row.setDomainName(domainName);					
				}
				//List<LevelTwoReportRow> localTwoReport = itasserLevelTwoReport;
				CutoffData cutData = ReportController.getCutoffData(2, ReportTypeEnum.Primary, level2Domain.getLevel2RunId(), defaultLevelTwoEvalue, -1, true);
				updateLevTwoSusceptibility(itasserLevelTwoReport, cutData.getCutoffValues().get(0), false);
				levelFourReport = combineLevelTwoLevel4(itasserLevelTwoReport, levelFourReport);
			} else {
				System.out.println("Error in viewCombinedLevel4Button");
				System.out.println("   levelFourSourceLevel = " + levelFourSourceLevel);
				return;
			}
			// remove row if status is "FASTA created" since these are not run in I-Tasser
			levelFourReport.removeIf(row -> row.getStatus().equals("FASTA created"));

			parseLevelFourStatus(levelFourReport);

			// setup any other calculations needed

			// calculate absolute Length (abs %diff between query and hit length)
			int queryLength = 0;
			if (levelFourSourceLevel == 1) {
				queryLength = levelOneReport.get(0).getHitLength();
			} else if (levelFourSourceLevel == 2) {
				queryLength = itasserLevelTwoReport.get(0).getHitLength();
			}
			for (LevelFourAccessionRow row : levelFourReport) {
				row.setAbsLength(Math.abs(queryLength - row.getHitLength()) / queryLength);
			}

			try {
				genLevelFourCutoff();
				levelFourCutoffs = levelFourCutData.getCutoffValues();
				levelFourCutValue = levelFourCutoffs.get(0);
			} catch (Exception e) {
				System.out.println("error in generating level 4 cutoff" + e.getMessage());
			}

			for (LevelFourAccessionRow row : levelFourReport) {
				// determine quality (based on cscore alone)
				// changed on 7/6/23 meeting to base on cscore and/or tm-score
				if ((row.getCscore() < -5 || row.getCscore() > 2) || row.getTm_score()<0.5) {
					row.setQuality("Low");
				} else {
					row.setQuality("High");
				}
				// determine length cutoff
				if (levelFourCutValue == 0 || row.getAbsLength()*100 <= levelFourCutValue) {
					row.setLengthCutOff("Okay");
				} else {
					row.setLengthCutOff("Check");
				}
			}

			//selectedLevelFourReportRows.clear();
			selectedLevelFourReportRows = new ArrayList<LevelFourAccessionRow>();
			for (LevelFourAccessionRow row : levelFourReport) {
				if (Objects.equals(row.getQuality(), "High")) {
					selectedLevelFourReportRows.add(row);
				}
			}

			// TODO This currently assumes all reports are from same level 4 version. This
			// needs to be enforced.
			ReportInfo info = ReportController.getReportInfo(selectedCombineLevelFourRuns.get(0).getAccessionRunId(),
					-1, -1, selectedCombineLevelFourRuns.get(0).getLevel4RunId());
			setItasserVersion(info.getItasserVersion());
			setTmalignVersion(info.getTmalignVersion());
			Long tmpUniprotDate = info.getUniprotDate();
			if (tmpUniprotDate != null ) setUniprotDate(new Date(info.getUniprotDate()));

			displayedLevel4JobName = displayName;

			tmAlignQueryAccs = new ArrayList<LevelFourRequestableRow>();
			levelFourTMAlignSelectionReport.clear();
			selectedTMAlignSelectionReportRows.clear();

			
			
			selectedLevelFourRuns.clear();
			selectedLevelFourRuns.addAll(selectedCombineLevelFourRuns);
			selectedCombineLevelFourRuns.clear();
			selectedStartedLevelFourRun = null;
			
			changeLev4JobChoices();

			
			//Completed TM-Align Reports
			getAvailableTMAlignReports();
			
			System.out.println("after getAvailableTMAlignReports");
			
			//reset AlphaFold report
			selectedAlphaFoldRow = null;
			levelFourAlphaFoldReport.clear();
			
			//reset TM-align reports
			selectedTMAlignReportChoice = null;
			levelFourTMAlignReport.clear();
			
			itasserBackground = "black";
			icn3dPDB = "";
			icn3dPage = "icn3d.html";
			icn3dPopupSize = 0;
			
			PrimeFaces.current().executeScript("onLoadL4();");

			
			setRenderLevelFour(true);
			changeToLevelFourPage();

		}

	}
	
	public void retrieveUniprotMappings() {		
		//get list of all L1 accessions
		System.out.println("inside retrieveUniprotMappings");
		List<String> accs = levelOneReport.stream().map(LevelOneReportRow::getAccession).collect(Collectors.toList());
		System.out.println("accs.size() = " + accs.size());
		for (int i=0; i<accs.size(); i++) {
			System.out.println("i:" + i + ", " + accs.get(i));
		}
//		
//		System.out.println("Original accs size: " + accs.size());
//		accs.subList(2001, accs.size()).clear();
//		System.out.println("Reduced to: " + accs.size());
		int batchSize = 500;
		List<List<String>> batch = Lists.partition(accs, batchSize);
		
		System.out.println("calling ReportController.getUniprotMappings");
//		uniprotMap = ReportController.getUniprotMappingsByAccId(accessionRunId, eukaryotesOnly1);
		int count = 1;
		for (List<String> list : batch) {
			System.out.println("Running FE batch " + count + " of " + batch.size());
			uniprotMap.addAll(ReportController.getUniprotMappings(list));
			System.out.println("finished FE batch # " + count);
			count++;
		}
		System.out.println("after ReportController.getUniprotMappings");
		
		for (LevelOneReportRow row1: levelOneReport) {
			UniprotMap map = uniprotMap.stream().filter(row -> row1.getAccession().equals(row.getNcbiAcc())).findAny().orElse(null);
			if (map != null) {
				row1.setUniprotAcc(map.getUniprotAccs());
			} 
		}
	}
	
	public void requestUniprotButton() {
		retrieveUniprotMappings();

		//AlphaFold Report
		levelFourAlphaFoldReport.clear();
		selectedAlphaFoldRow = new LevelFourAccessionRow();
		for (LevelOneReportRow row: levelOneReport) {
			for (int i=0; i<row.getUniprotAcc().size(); i++) {
				levelFourAlphaFoldReport.add(new LevelFourAccessionRow(row, null, null, currentReportInfo.getUpdateVersion(), null, null, row.getUniprotAcc().get(i)));
			}
		}
	}

	public void getAvailableTMAlignReports() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");

		List<Integer> levelFourRunIds = selectedLevelFourRuns.stream()
				.map(LevelFourRequestableRow::getLevel4RunId).collect(Collectors.toList());

//		List<LevelFourRequestableRow> res = ReportController.getAvailableTmalignReports(theUser.getUserid(), levelFourRunIds);

		tmalignReportChoices = ReportController.getAvailableTmalignReports(theUser.getUserid(), levelFourRunIds);
		// use list of tmalign_run_ids to get
		System.out.println("tmalignReportChoices size:" + tmalignReportChoices.size());

	}

	public void viewTMAlign() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");

		System.out.println("Selected to view: " + selectedTMAlignReportChoice);

		levelFourTMAlignReport = ReportController.getTmalignReport(theUser.getUserid(),
				selectedTMAlignReportChoice.getTmAlignRunId());
		
		//ensure query row has correct template
		LevelFourResultRow queryRow = levelFourTMAlignReport.get(0);
		//ensure that 1st row is query Row
		if (queryRow.getAcc1().equals(queryRow.getAcc2())) {
			if (queryRow.getPdbSource().equals("I-TASSER")) {
				LevelFourAccessionRow matchRow = levelFourReport.stream().filter(row -> queryRow.getAcc2().equals(row.getNcbiAccession())).findFirst().orElse(null);
				queryRow.setTemplate(matchRow.getTemplate());
			}
		}

		
		levelFourTMAlignReport = combineLevel4DataAndResults(levelFourReport, levelFourTMAlignReport);

		//set values for queryRow
		LevelFourResultRow queryRow1 = levelFourTMAlignReport.get(0);
		//ensure that 1st row is query Row just in case it is somehow dropped in combineLevel4DataAndResults
		if (queryRow1.getAcc1().equals(queryRow1.getAcc2()))
			queryRow1.setAvgVal(1.0);
		
		//Set values for AlphaFold, RCSB, other sources
		int queryLength = levelOneReport.get(0).getHitLength();
		for(LevelFourResultRow row : levelFourTMAlignReport) {
			if (!row.getPdbSource().equals("I-TASSER")) {
				row.setSusceptible("Y");
				row.setDomainId(null);			}
		}
		
		//sort
		Collections.sort(levelFourTMAlignReport, Comparator.comparing(LevelFourResultRow::getAvgVal).reversed());
		
		

	}

	/**
	 * Risk Assessor Report methods
	 */
	public void combineLevel3Button() {

		combinedLevel3Report = true;

		selectedAssessorTemplate = null;
		selectedAssessorJobs.clear();

		availableTemplates.clear();
		for (LevelThreeRequestableRow request : completedLevelThreeRuns) {
			if (!availableTemplates.contains(request.getTemplate())) {
				availableTemplates.add(request.getTemplate());
			}
		}
	}

	public String onFlowProcess(FlowEvent event) {

//		System.out.println(event.getNewStep());
		PrimeFaces pf = PrimeFaces.current();

		switch (event.getNewStep()) {
		// View Report
		case "Lev3JobChoice":
			// //validate previous step
			if (selectedAssessorTemplate == null || selectedAssessorTemplate.isEmpty()) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must select a template.");
				FacesContext.getCurrentInstance().addMessage("tabView:reportForm:validateGrowl", msg);
				// request.update("tabView:reportForm:validateGrowl");
				pf.ajax().update("tabView:reportForm:validateGrowl");

				return event.getOldStep();
			}
			// get jobs for chosen template
			availableJobs.clear();
			for (LevelThreeRequestableRow row : completedLevelThreeRuns) {
				if (row.getTemplate().equals(selectedAssessorTemplate)) {
					availableJobs.add(row);
				}
			}
			break;
		case "Lev3JobOrder":
			if (selectedAssessorJobs.size() == 0) {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
						"Must select at least one job.");
				FacesContext.getCurrentInstance().addMessage("tabView:reportForm:validateGrowl", msg);
				// request.update("tabView:reportForm:validateGrowl");
				pf.ajax().update("tabView:reportForm:validateGrowl");
				return event.getOldStep();
			}
			break;
		default:
			break;
		}

		return event.getNewStep();
	}

	public void viewCombinedLevel3Report() {

		selectedLevel3Info = selectedAssessorJobs.get(0);
		if (selectedLevel3Info.getTemplate() != null && !selectedLevel3Info.getTemplate().isEmpty()) {
			try {
				Protein protein = ReportController.getProtein(selectedLevel3Info.getTemplate());
				templateProtein = protein.getDisplayName();
				templateSpecies = ReportController.getSciNameAtTaxId(selectedLevel3Info.getTemplate(), "species");
			} catch (Exception e) {
				templateProtein = selectedLevel3Info.getTemplate();
				templateSpecies = "NA";
			}
		} else {
			templateProtein = "User Defined";
			templateSpecies = "NA";
		}

		// 1st integer represents location with all (-) removed, 2nd integer
		// represents actual location in sequence returned from cobalt,
		// sequenceMap.clear();
		// Map<Integer, Integer> sequenceMap = new LinkedHashMap<Integer,
		// Integer>();
		sequenceMaps.clear();

		List<String> pickListSource = new ArrayList<String>();


		for (int j = 0; j < selectedAssessorJobs.size(); j++) {

			Map<Integer, Integer> sequenceMap = new LinkedHashMap<Integer, Integer>();

			LevelThreeRequestableRow selectedJob = selectedAssessorJobs.get(j);
			// Get template sequence, split to list while adding position and
			// left
			// padding to strings
			String sequence = ReportController.requestLevelThreeSequence(selectedJob.getLevel3RunId());

			int loc = 0;
			for (int i = 0; i < sequence.length(); i++) {
				if (!Character.toString(sequence.charAt(i)).equals("-")) {
					loc++;
					sequenceMap.put(loc, i + 1);
					if (j == 0) {
						int longest = String.valueOf(sequence.length()).length() + 1;
						String formatStatement = "%" + longest + "s";
						String newString = String.valueOf(loc) + sequence.charAt(i);
						newString = String.format(formatStatement, newString);
						pickListSource.add(String.format(formatStatement, newString));
					}
				}
			}
			sequenceMaps.add(sequenceMap);
		}

		List<String> pickListTarget = new ArrayList<String>();
		setLevelThreePickList(new DualListModel<String>(pickListSource, pickListTarget));

		List<Integer> positionList = new ArrayList<Integer>();
		// Do next lines for each selectedAssessorJobs and combine
		LevelThreeViewRequest request = new LevelThreeViewRequest(selectedLevel3Info.getLevel3RunId(), positionList);
		setLevelThreeReport(ReportController.getLevelThreeReportForUser(request));

		List<LevelThreeReportRow> combinedReport = new ArrayList<LevelThreeReportRow>();
		combinedReport.add(levelThreeReport.get(0));

		// overwrite jobName for selectedLevel3Info to combine all job names
		List<String> jobNameList = new ArrayList<String>();

		for (int i = 0; i < selectedAssessorJobs.size(); i++) {
			jobNameList.add(selectedAssessorJobs.get(i).getJobName());
			LevelThreeViewRequest newRequest = new LevelThreeViewRequest(selectedAssessorJobs.get(i).getLevel3RunId(),
					positionList);
			List<LevelThreeReportRow> newReport = ReportController.getLevelThreeReportForUser(newRequest);
			for (int j = 1; j < newReport.size(); j++) {
				LevelThreeReportRow row = newReport.get(j);
				combinedReport.add(row);
			}

			// Why won't this work?
			//// newReport.remove(0); //remove first row (template row)
			//// levelThreeReport.addAll(newReport);

		}
		levelThreeReport = combinedReport;

		levelThreeRunName = "Combined: " + String.join(", ", jobNameList);

		for (LevelOneReportRow oneRow : levelOneReport) {
			for (LevelThreeReportRow threeRow : levelThreeReport) {
				if (threeRow.getAccession().equals(oneRow.getAccession())) {
					threeRow.setSpeciesTaxId(oneRow.getSpeciesTaxId());
					threeRow.setProteinCount(oneRow.getProteinCount());
					threeRow.setTaxonomyName(oneRow.getTaxonomyName());
					threeRow.setScientificName(oneRow.getScientificName());
					threeRow.setCommonName(oneRow.getCommonName());
					threeRow.setProteinName(oneRow.getProteinName());
				}
			}
		}

		filteredLevelThreeReport.clear();
		for (int i = 0; i < levelThreeReport.size(); i++) {
			filteredLevelThreeReport.add(levelThreeReport.get(i));
		}

		ReportInfo info = ReportController.getReportInfo(selectedLevel3Info.getAccessionRunId(), -1,
				selectedLevel3Info.getLevel3RunId(), -1);
		setCobaltDate(new Date(info.getCobaltDate()));
		setCobaltVersion(info.getCobaltVersion());

		residueHeaders.clear();
		primaryResidueHeaders.clear();
		queryResiduesText = "No Residues Selected";
		setRenderLevelThree(true);
		changeToLevelThreePage();
	}

	// Handle changing between levels of report pages

	public void changeToMainPage() {
		setSelectedPageURL("main_report.xhtml");
	}

	public void changeToLevelOnePage(boolean resetToPrimary) {
		if (resetToPrimary) {
			setLevelOneReportType(ReportTypeEnum.Primary); // reset to primary
															// report
		}
		changeLevelOneReportPage();
		setSelectedPageURL("levelOneReport.xhtml");
		setSelectedDomain(null);
		setSelectedCompletedDomain(null);
		setSelectedCompletedLevel3(null);
		setSelectedTaxGroup(null);
		updateFilter();
		selectedLevelOneRows.clear();
		selectedLevelOnePrimaryRows.clear();
		setSelectedSpeciesCount(0);
		reloadLevelTwoAndThreeResults();
	}

	public void changeToLevelTwoPage(boolean resetToPrimary) {
		if (resetToPrimary) {
			setLevelTwoReportType(ReportTypeEnum.Primary); // reset to primary
															// report
		}
		changeLevelTwoReportPage();
		setSelectedPageURL("levelTwoReport.xhtml");
	}

	public void changeToLevelThreePage() {
		setSelectedPageURL("levelThreeReport.xhtml");
	}

	public void changeToLevelFourPage() {
		getAvailableTMAlignReports();
		setSelectedPageURL("levelFourReport.xhtml");
	}

	public void changeToRAReportPage() {
		setSelectedPageURL("riskAssessorReport.xhtml");
	}

	/**************************************************************
	 * Methods that handle changing report types (primary/full or view/save)
	 ************************************************************************/

	/**
	 * Handles changing between view/save main reports
	 */
	public void changeMainReportOption() {
		switch (chosenMainReportOption) {
		case View:
			setMainReportButtonText("Request Selected Report");
			setReportOptionPage("main_report_view.xhtml");
			if (selectedReport != null) {
				setMainReportButtonDisabled(false);
			} else {
				setMainReportButtonDisabled(true);
			}

			break;
		case Save:
			setMainReportButtonText("Save Selected Report(s)");
			setReportOptionPage("main_report_save.xhtml");
			resetToMain();
			if (selectedReports.size() == 0) {
				setMainReportButtonDisabled(true);
			} else {
				setMainReportButtonDisabled(false);
			}
			// RequestContext context = RequestContext.getCurrentInstance();
			// context.execute("PF('mainSaveReportTable').filter();");
			PrimeFaces.current().executeScript("PF('mainSaveReportTable').filter();");
			break;
		default:
			System.out.println("ERROR: Unknown chosenReportOption " + chosenMainReportOption);
		}
	}

	/**
	 * Handles changing between primary/full level one reports
	 */
	public String changeLevelOneReportPage() {
		switch (levelOneReportType) {
		case Primary:
			setLevelOneReportPage("levelOnePrimaryReport.xhtml");
			setLevelOneHeaderText("Level 1 Data - Primary");
			setOrtholog_count(getPrimary_ortholog_count());
			updateTaxGroupChoices();
			setSelectedTaxGroup("");
			updateCheckedRowsByReportType();
			updateLevOneCutoff();
			break;
		case Full:
			setLevelOneReportPage("levelOneFullReport.xhtml");
			setLevelOneHeaderText("Level 1 Data - Full");
			setOrtholog_count(getFull_ortholog_count());
			updateTaxGroupChoices();
			setSelectedTaxGroup("");
			updateCheckedRowsByReportType();
			updateLevOneCutoff();
			break;
		default:
			System.out.println("ERROR: Unknown Level 1 Report Type " + levelOneReportType);
			return null;
		}

		checkDiffLevel1RAReport();

		return levelOneReportPage;

	}

	/**
	 * this method changes between primary and full level two report pages
	 * 
	 * @return
	 */
	public String changeLevelTwoReportPage() {
		switch (levelTwoReportType) {
		case Primary:
			setLevelTwoReportPage("levelTwoPrimaryReport.xhtml");
			setLevelTwoHeaderText("Level 2 Data - Primary");
			setLevel2_ortholog_count(getPrimary_level2_ortholog_count());
			updateTaxGroup2Choices();
			// updateCheckedRowsByReportType();
			setSelectedTaxGroup("");
			updateLevTwoCutoff();
			break;
		case Full:
			setLevelTwoReportPage("levelTwoFullReport.xhtml");
			setLevelTwoHeaderText("Level 2 Data - Full");
			setLevel2_ortholog_count(getFull_level2_ortholog_count());
			updateTaxGroup2Choices();
			// updateCheckedRowsByReportType();
			setSelectedTaxGroup("");
			updateLevTwoCutoff();
			break;
		default:
			System.out.println("ERROR: Unknown Level 2 Report Type " + levelTwoReportType);
			return null;
		}

		checkDiffLevel2RAReport();

		return levelTwoReportPage;
	}

	/**
	 * this method changes between primary and full level three report pages
	 * 
	 * @return
	 */
	public String changeLevelThreeReportPage() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		switch (levelThreeReportType) {
		case Primary:
			setLevelThreeReportPage("levelThreePrimaryReport.xhtml");
			setLevelThreeHeaderText("Level 3 Data - Primary");
			// vizView.reloadLevelThreeVizData();
			// vizView.checkDiffLevel3HeatmapRAReport();
			break;
		case Full:
			setLevelThreeReportPage("levelThreeFullReport.xhtml");
			setLevelThreeHeaderText("Level 3 Data - Full");
			// vizView.reloadLevelThreeVizData();
			// vizView.checkDiffLevel3HeatmapRAReport();
			break;
		default:
			System.out.println("ERROR: Unknown Level 3 Report Type " + levelThreeReportType);
			return null;
		}
		return levelThreeReportPage;
	}

	/****************************************************************
	 * Methods that refresh/update reports or report info
	 ***************************************************/

	/**
	 * Updates all main reports
	 */
	public void refreshButton() {
		setReportList(ReportController.getMainReportForUser());

		filteredReportList.clear();
		for (int i = 0; i < reportList.size(); i++) {
			filteredReportList.add(reportList.get(i));
		}
		filteredDownloadList.clear();
		for (int i = 0; i < reportList.size(); i++) {
			filteredDownloadList.add(reportList.get(i));
		}

		getSelectedReports().clear();
	}

	/**
	 * Updates/reloads level two and three run info
	 */
	public void reloadLevelTwoAndThreeResults() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");
		// level 2 results
		setLevelTwoDomains(ReportController.getLevelTwoDomainsNew(getAccessionRunId(), theUser.getUserid()));
		completedLevelTwoDomains.clear();
		for (LevelTwoRequestableRow row : levelTwoDomains) {
			if (row.getLevel2RunId() > 0) {
				completedLevelTwoDomains.add(row);
			}
		}

		// level 3 results
		setCompletedLevelThreeRuns(ReportController.getCompletedLevelThree(getAccessionRunId(), theUser.getUserid()));
		updateTaxGroupChoices();
		updateTaxGroup2Choices();
		setDisableRequestDomainButton(true);

		// Handles whether to allow submission of level 3 job based on data
		// version
		// Shows tooltips for level 2 and level 3 submissions
		// (level 2 submission enabling/disabling is handled in
		// changeSelectedDomain on selection event)
		if (getCurrentReportInfo() != null
				&& latestUpdateInfo.getUpdateVersion() > getCurrentReportInfo().getUpdateVersion()) {
			setDisableRequestResidueButton(true);
			setLevelThreeRequestTip(
					"This was run with a previous data version.  Please re-submit the Level 1 accession to enable Level 3 runs.");
			setShowLevelThreeRequestTip(true);
			setLevelTwoRequestTip(
					"This was run with a previous data version.  Please re-submit the Level 1 accession to enable Level 2 runs.");
			setShowLevelTwoRequestTip(true);
		} else {
			setDisableRequestResidueButton(false);
			setShowLevelThreeRequestTip(false);
			setShowLevelTwoRequestTip(false);
		}

	}

	/*****************************************************
	 * Methods that format output
	 ****************************************************/

	/**
	 * this method formats a timestamp(long) as a string in "yyyy MM dd HH:mm:ss"
	 * format
	 * 
	 * @param time (long)
	 * @return the formatted date as a string
	 */
	public String convertUnixTimeToDate(long time) {
		if (time != 0) {
			time *= 1000; // Unix_timestamp give seconds, we need milliseconds
			Date date = new Date(time);
			Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
			return format.format(date);
		} else
			return "Not Finished";
	}

	public String convertUnixTimeToDate2(long time) {
		if (time != 0) {
			time *= 1000; // Unix_timestamp give seconds, we need milliseconds
			Date date = new Date(time);
			Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
			return format.format(date);
		} else
			return "Not Started";
	}

	/**
	 * this method formats a double in scientific notation
	 * 
	 * @param num (double)
	 * @return the formatted value as a string
	 */
	public String formatScientific(double num) {
		return scientificFormatter.format(num);
	}

	/********************************************************
	 * Methods that handle cutoff calculations and graphs
	 *******************************************************/

	/**
	 * Resets previously failed validation of cutoffInput, so that the selectOneMenu
	 * can update it's value correctly
	 */
	public void setLevOneCutoffField() {
		if (chosenLevelOneCutoffOption != 3) {
			setUserDefinedLevOneCut(Double
					.parseDouble(twoDecimalDigitFormatter.format(levelOneCutoffs.get(chosenLevelOneCutoffOption - 1))));
			UIInput field = (UIInput) FacesContext.getCurrentInstance().getViewRoot()
					.findComponent("cutoffForm:cutoffInput");
			field.resetValue();
		}

	}

	/**
	 * Resets previously failed validation of cutoffInput, so that the selectOneMenu
	 * can update it's value correctly
	 */
	public void setLevTwoCutoffField() {
		if (chosenLevelTwoCutoffOption != 3) {
			setUserDefinedLevTwoCut(Double
					.parseDouble(twoDecimalDigitFormatter.format(levelTwoCutoffs.get(chosenLevelTwoCutoffOption - 1))));
			UIInput field = (UIInput) FacesContext.getCurrentInstance().getViewRoot()
					.findComponent("cutoffForm2:cutoffInput");
			field.resetValue();
		}

	}

	/**
	 * 
	 * @param level      - cutoff level 1 or 2
	 * @param id         - accessionRunId for level 1, lev2RunId for level 2
	 * @param reportType - 0 for full report, 1 for primary report
	 * @param saveToView - boolean specifying whether reportView cutoff variables
	 *                   should be updated
	 * @return ChartPanel object containing cutoff graph and sets level cutoff and
	 *         density variables
	 */
	public ChartPanel createCutoffChartAndDensityModel(int level, int id, ReportTypeEnum reportType, double eValue,
			int commonDomains) {

		// CutoffData cutData = ReportController.getCutoffData(level,
		// reportType, id, eValue, commonDomains,
		// eukaryotesOnly);

		boolean eukOnly = true;
		if (level == 1) {
			eukOnly = eukaryotesOnly1;
		} else if (level == 2) {
			eukOnly = eukaryotesOnly2;
		}
		CutoffData cutData = ReportController.getCutoffData(level, reportType, id, eValue, commonDomains, eukOnly);
		LineChartModel densityModel = null;
		ChartPanel chartPanel = null;
		if (cutData != null) {
			densityModel = cutData.genDensityModel();
			chartPanel = cutData.genCutoffChart(level);
		}

		if (level == 1) {
			if (reportType.equals(ReportTypeEnum.Primary)) {
				setLevelOnePrimaryCutData(cutData);
				setLevelOnePrimaryCutoffs(cutData.getCutoffValues());
				setLevelOnePrimaryDensityModel(densityModel);
				setLevelOnePrimaryChartPanel(chartPanel);
			} else {
				setLevelOneFullCutData(cutData);
				setLevelOneFullCutoffs(cutData.getCutoffValues());
				setLevelOneFullDensityModel(densityModel);
				setLevelOneFullChartPanel(chartPanel);
			}
		} else {
			if (reportType.equals(ReportTypeEnum.Primary)) {
				setLevelTwoPrimaryCutData(cutData);
				setLevelTwoPrimaryCutoffs(cutData.getCutoffValues());
				setLevelTwoPrimaryDensityModel(densityModel);
				setLevelTwoPrimaryChartPanel(chartPanel);
			} else {
				setLevelTwoFullCutData(cutData);
				setLevelTwoFullCutoffs(cutData.getCutoffValues());
				setLevelTwoFullDensityModel(densityModel);
				setLevelTwoFullChartPanel(chartPanel);
			}
		}

		return chartPanel;
	}

	/**
	 * 
	 * @param saveToView - boolean specifying whether reportView cutoff variables
	 *                   should be updated
	 * @return ChartPanel object containing cutoff graph and sets level cutoff and
	 *         density variables
	 */
	public ChartPanel createL4CutoffChartAndDensityModel(double queryLength) {
		setLevelFourDensityModel(null);
		List<DensityRow> densityData = new ArrayList<DensityRow>();
		for (LevelFourAccessionRow row : levelFourReport) {
			// don't add query accession
			if (!row.getNcbiAccession().equals(levelOneReport.get(0).getAccession())) {
//				if (row.getAbsLength() != 0.0) {
					densityData.add(new DensityRow(row.getAbsLength(), "NA"));
//				}
			}
		}
		Collections.sort(densityData, Comparator.comparing(DensityRow::getPercSim));

		CutoffData cutData = CutoffData.newInstance(densityData, 4);

		LineChartModel densityModel = null;
		ChartPanel chartPanel = null;
		if (cutData != null) {
			densityModel = cutData.genL4DensityModel();
			densityModel.setTitle("Cut-off Based on Length Difference");
			int level = 4;
			chartPanel = cutData.genCutoffChart(level);
		}
		setLevelFourCutData(cutData);
		setLevelFourDensityModel(densityModel);
		setLevelFourChartPanel(chartPanel);

		return chartPanel;
	}

	/**
	 * Generates all cutoff data needed for viewing level 1 primary report
	 * 
	 * @param accessionRunId
	 * @param eValue
	 * @param commonDomains
	 */
	public void genLevelOnePrimaryCutoff(int accessionRunId, double eValue, int commonDomains) {
		System.out.println("generating primary level one cutoff");
		try {
			createCutoffChartAndDensityModel(1, accessionRunId, ReportTypeEnum.Primary, eValue, commonDomains);
			getLevelOnePrimaryChartPanel().setSize(560, 367);
		} catch (Exception e) {
			System.out.println("Primary cutoff not available for accessionRunId:" + accessionRunId);
		}

		System.out.println("finished generating primary cutoffs");
	}

	/**
	 * Generates all cutoff data needed for viewing level 1 full report
	 * 
	 * @param accessionRunId
	 */
	public void genLevelOneFullCutoff(int accessionRunId) {
		System.out.println("generating full level 1 cutoff");
		// try/catch is needed for backwards compatibility for runs without full
		// cutoff data
		try {
			createCutoffChartAndDensityModel(1, accessionRunId, ReportTypeEnum.Full, -1, -1);
			getLevelOneFullChartPanel().setSize(560, 367);
		} catch (Exception e) {
			System.out.println("Full cutoff not available for accessionRunId:" + accessionRunId);
		}
		System.out.println("finished generating primary cutoffs");
	}

	/**
	 * Generates all cutoff data needed for viewing primary level 2 report assumes
	 * that selectedCompletedDomain has been populated
	 * 
	 * @param eValue
	 */
	public void genLevelTwoPrimaryCutoff(double eValue) {
		try {
			// createCutoffChart(2, selectedCompletedDomain.getLevel2RunId(), 1,
			// eValue, -1, true);
			createCutoffChartAndDensityModel(2, loadedCompletedDomain.getLevel2RunId(), ReportTypeEnum.Primary, eValue,
					-1);
			getLevelTwoPrimaryChartPanel().setSize(560, 367);
		} catch (Exception e) {
			System.out.println(
					"Level 2 Primary cutoff not available for level2RunId:" + loadedCompletedDomain.getLevel2RunId());
		}
	}

	/**
	 * Generates all cutoff data needed for viewing full level 2 report assumes that
	 * selectedCompletedDomain has been populated
	 */
	public void genLevelTwoFullCutoff() {
		try {
			createCutoffChartAndDensityModel(2, loadedCompletedDomain.getLevel2RunId(), ReportTypeEnum.Full, -1, -1);
			getLevelTwoFullChartPanel().setSize(560, 367);
		} catch (Exception e) {
			System.out.println(
					"Level 2 Full cutoff not available for level2RunId:" + loadedCompletedDomain.getLevel2RunId());
		}

	}

	/**
	 * Generates all cutoff data needed for viewing level 4 report
	 */
	public void genLevelFourCutoff() {
		int queryLength = 0;
		if (levelFourSourceLevel == 1) {
			queryLength = levelOneReport.get(0).getHitLength();
		} else {
			//queryLength = levelTwoReport.get(0).getHitLength();
			queryLength = itasserLevelTwoReport.get(0).getHitLength();
		}
		try {
			createL4CutoffChartAndDensityModel(queryLength);
			getLevelFourChartPanel().setSize(560, 367);
		} catch (Exception e) {
			System.out.println(
					"Level 4 cutoff not available for level4RunId:" + selectedStartedLevelFourRun.getLevel4RunId());
		}

	}

	/**
	 * Updates level one cutoff data based on available cutoff data and chosen
	 * report type
	 */
	public void updateLevOneCutoff() {
		// Determine component calling this method
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());

		// if (component.getId().equals("updateCutoffButton")) {
		// // If component is "Update Cutoff" button from cutoff page, then do
		// // not reset to primary upon returning to level1 report page
		// System.out.println("Setting reset to false");
		// setResetLevOneToPrimary(false);
		// }

		if (chosenLevelOneCutoffOption != 3) {
			if (levelOnePrimaryCutoffs != null) {
				// check to make sure there is enough cutoffs for chosen cutoff
				// option
				if (levelOnePrimaryCutoffs.size() < chosenLevelOneCutoffOption) {
					setLevelOnePrimaryCutValue(levelOnePrimaryCutoffs.get(levelOnePrimaryCutoffs.size() - 1));
				} else {
					setLevelOnePrimaryCutValue(levelOnePrimaryCutoffs.get(chosenLevelOneCutoffOption - 1));
				}
			}
			if (levelOneFullCutoffs != null) {
				// check to make sure there is enough cutoffs for chosen cutoff
				// option
				if (levelOneFullCutoffs.size() < chosenLevelOneCutoffOption) {
					setLevelOneFullCutValue(levelOneFullCutoffs.get(levelOneFullCutoffs.size() - 1));
				} else {
					setLevelOneFullCutValue(levelOneFullCutoffs.get(chosenLevelOneCutoffOption - 1));
				}
			} else if (levelOnePrimaryCutoffs != null) {
				// backwards compatibility - if no full cutoff use default
				// primary
				setLevelOneFullCutValue(levelOnePrimaryCutoffs.get(0));
			}
		} else {
			setLevelOnePrimaryCutValue(userDefinedLevOneCut);
			setLevelOneFullCutValue(userDefinedLevOneCut);
			// if (component.getId().equals("cutoffRadio")) {
			// // // If component is cutoffRadio from levelOneReport.xhtml then
			// // // that means user clicked the "User Defined" radio
			// // // button on that page. This triggers navigation to the cutoff
			// // // page
			// // FacesContext context = FacesContext.getCurrentInstance();
			// //
			// context.getApplication().getNavigationHandler().handleNavigation(context,
			// null, "cutoff.xhtml");
			//
			// // StringBuilder sb = new StringBuilder();
			// // sb.append("OpenCutoffInBrowserTab(1)");
			// //
			// // RequestContext context = RequestContext.getCurrentInstance();
			// // context.execute(sb.toString());
			//
			// }
		}

		if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
			// Primary
			setLevelOneCutData(getLevelOnePrimaryCutData());
			setUserDefinedLevOneCut(Double.parseDouble(twoDecimalDigitFormatter.format(levelOnePrimaryCutValue)));

			// Is this needed here?
			setLevelOneCutoffs(getLevelOnePrimaryCutoffs());
			setLevelOneDensityModel(getLevelOnePrimaryDensityModel());
		} else {
			// Full
			setLevelOneCutData(getLevelOneFullCutData());
			setUserDefinedLevOneCut(Double.parseDouble(twoDecimalDigitFormatter.format(levelOneFullCutValue)));
			setLevelOneCutoffs(getLevelOneFullCutoffs());
			setLevelOneDensityModel(getLevelOneFullDensityModel());
		}

		if (getLevelOnePrimaryCutData() != null && levelOnePrimaryReport.size() > 0) {
			setLevelOnePrimaryFirstRow(levelOnePrimaryReport.get(0));
		}

		// Determine Susceptibility for Primary and Full Report
		updateLevOneSusceptibility(levelOneReport, levelOneFullCutValue, true);
		updateLevOneSusceptibility(levelOnePrimaryReport, levelOnePrimaryCutValue, false);

		// Update viz pages
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelOneVizData(updateLevOneBox);
		updateLevOneBox = true;

	}

	/**
	 * Updates level two cutoff data based on available cutoff data and chosen
	 * report type
	 */
	public void updateLevTwoCutoff() {
		// Determine component calling this method
		UIComponent component = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance());

		// if (component.getId().equals("updateCutoffButton")) {
		// // If component is "Update Cutoff" button from cutoff page, then do
		// // not reset to primary upon returning to level1 report page
		// setResetLevTwoToPrimary(false);
		// }

		if (chosenLevelTwoCutoffOption != 3) {
			if (levelTwoPrimaryCutoffs != null) {
				// check to make sure there is enough cutoffs for chosen cutoff
				// option
				if (chosenLevelTwoCutoffOption == 2 && levelTwoPrimaryCutoffs.size() < 2) {
					setLevelTwoPrimaryCutValue(levelTwoPrimaryCutoffs.get(levelTwoPrimaryCutoffs.size() - 1));
				} else {
					setLevelTwoPrimaryCutValue(levelTwoPrimaryCutoffs.get(chosenLevelTwoCutoffOption - 1));
				}
				setLevelTwoFullCutValue(levelTwoPrimaryCutoffs.get(chosenLevelTwoCutoffOption - 1));
			}
			if (levelTwoFullCutoffs != null) {
				// check to make sure there is enough cutoffs for chosen cutoff
				// option
				if (levelTwoFullCutoffs.size() < 2) {
					setLevelTwoFullCutValue(levelTwoFullCutoffs.get(levelTwoFullCutoffs.size() - 1));
				} else {
					setLevelTwoFullCutValue(levelTwoFullCutoffs.get(chosenLevelTwoCutoffOption - 1));
				}
			}
		} else {
			setLevelTwoPrimaryCutValue(userDefinedLevTwoCut);
			setLevelTwoFullCutValue(userDefinedLevTwoCut);
			// if (component.getId().equals("cutoffRadio")) {
			// // If component is cutoffRadio from levelTwoReport.xhtml then
			// // that means user clicked the "User Defined" radio
			// // button on that page. This triggers navigation to the cutoff
			// // page
			// FacesContext context = FacesContext.getCurrentInstance();
			// context.getApplication().getNavigationHandler().handleNavigation(context,
			// null, "cutoff2.xhtml");
			// }
		}

		if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
			// Primary
			setLevelTwoCutData(getLevelTwoPrimaryCutData());
			setUserDefinedLevTwoCut(Double.parseDouble(twoDecimalDigitFormatter.format(levelTwoPrimaryCutValue)));
			setLevelTwoCutoffs(getLevelTwoPrimaryCutoffs());
			setLevelTwoDensityModel(getLevelTwoPrimaryDensityModel());
		} else {
			// Full
			setLevelTwoCutData(getLevelTwoFullCutData());
			setUserDefinedLevTwoCut(Double.parseDouble(twoDecimalDigitFormatter.format(levelTwoFullCutValue)));
			setLevelTwoCutoffs(getLevelTwoFullCutoffs());
			setLevelTwoDensityModel(getLevelTwoFullDensityModel());
		}

		// Update report rows here

		if (getLevelTwoPrimaryCutData() != null) {
			setLevelTwoPrimaryFirstRow(levelTwoPrimaryReport.get(0));
		}

		updateLevTwoSusceptibility(levelTwoReport, levelTwoFullCutValue, true);
		updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoPrimaryCutValue, false);

		// Update viz pages
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelTwoVizData(updateLevTwoBox);
		updateLevTwoBox = true;

	}

	/***************************************************************
	 * Methods that determine susceptibility
	 ****************************************************************/

	/**
	 * Updates level one susceptibility determination based on current cutoff and
	 * choice of species read across
	 * 
	 * @param lev1Report                  - list of levelOneReportRow objects
	 * @param cutoff                      - cutoff value (double)
	 * @param useDefaultSpeciesReadAcross - boolean specifying whether to use
	 *                                    species read-across when determining
	 *                                    susceptibility
	 */
	public void updateLevOneSusceptibility(List<LevelOneReportRow> lev1Report, double cutoff,
			boolean useDefaultSpeciesReadAcross) {

		List<Integer> badTaxGroupIds = ReportController.getBadTaxGroupIds();

		cutoff = Double.parseDouble(twoDecimalDigitFormatter.format(cutoff));
		Set<Integer> susceptibleGroups = new HashSet<Integer>();
		for (LevelOneReportRow row : lev1Report) {
			row.setCutoff(cutoff);
			double percSim = Double.parseDouble(twoDecimalDigitFormatter.format(100 * row.getPercentSimilarity()));
			// check if ortholog
			if (row.getOrtholog().toLowerCase().equals("y")) {
				row.setSusceptible("Y");
				if (percSim >= cutoff) {
					int rowTaxGroupId = row.getTaxonomyTaxid();
					if (!badTaxGroupIds.contains(rowTaxGroupId)) {
						susceptibleGroups.add(rowTaxGroupId);
					}
				}
			}
			// check if percSim > cutoff
			else if (percSim >= cutoff) {
				row.setSusceptible("Y");
				int rowTaxGroupId = row.getTaxonomyTaxid();
				if (!badTaxGroupIds.contains(rowTaxGroupId)) {
					susceptibleGroups.add(rowTaxGroupId);
				}
			} else {
				row.setSusceptible("N");
			}
		}

		// check for same tax group as previously determined susceptible tax
		// group
		if (useDefaultSpeciesReadAcross || isLevelOneSpeciesReadAcross()) {
			for (LevelOneReportRow row : lev1Report) {
				if (susceptibleGroups.contains(row.getTaxonomyTaxid())) {
					row.setSusceptible("Y");
				}
			}
		}
	}

	/**
	 * Updates level two susceptibility determination based on current cutoff and
	 * choice of species read across
	 * 
	 * @param lev2Report                  - list of levelTwoReportRow objects
	 * @param cutoff                      - cutoff value (double)
	 * @param useDefaultSpeciesReadAcross - boolean specifying whether to use
	 *                                    species read-across when determining
	 *                                    susceptibility
	 */
	public void updateLevTwoSusceptibility(List<LevelTwoReportRow> lev2Report, double cutoff,
			boolean useDefaultSpeciesReadAcross) {

		List<Integer> badTaxGroupIds = ReportController.getBadTaxGroupIds();

		cutoff = Double.parseDouble(twoDecimalDigitFormatter.format(cutoff));
		Set<Integer> susceptibleGroups = new HashSet<Integer>();
		for (LevelTwoReportRow row : lev2Report) {
			row.setCutoff(cutoff);
			double percSim = Double.parseDouble(twoDecimalDigitFormatter.format(100 * row.getPercentSimilarity()));
			// check if ortholog
			if (row.getOrtholog().toLowerCase().equals("y")) {
				row.setSusceptible("Y");
				if (percSim > cutoff) {
					row.setSusceptible("Y");
					if (100 * row.getPercentSimilarity() >= cutoff) {
						int rowTaxGroupId = row.getTaxonomyTaxid();
						if (!badTaxGroupIds.contains(rowTaxGroupId)) {
							susceptibleGroups.add(rowTaxGroupId);
						}
					}
				}
			}
			// check if percSim > cutoff
			else if (100 * row.getPercentSimilarity() >= cutoff) {
				row.setSusceptible("Y");
				row.setSusceptible("Y");
				if (percSim > cutoff) {
					int rowTaxGroupId = row.getTaxonomyTaxid();
					if (!badTaxGroupIds.contains(rowTaxGroupId)) {
						susceptibleGroups.add(rowTaxGroupId);
					}
				}
			} else {
				row.setSusceptible("N");
			}
		}

		// check for same tax group as previously determined susceptible tax
		// group
		if (useDefaultSpeciesReadAcross || isLevelTwoSpeciesReadAcross()) {
			for (LevelTwoReportRow row : lev2Report) {
				if (susceptibleGroups.contains(row.getTaxonomyTaxid())) {
					row.setSusceptible("Y");
				}
			}
		}
	}

	/**********************************************************
	 * Methods that determine ortholog counts
	 *********************************************************/

	/**
	 * updates full and primary reportView ortholog variables for level one
	 */
	public void updateLevOneOrthologCount() {
		setFull_ortholog_count(returnLevOneOrthologCount(levelOneReport));
		setPrimary_ortholog_count(returnLevOneOrthologCount(levelOnePrimaryReport));
	}

	/**
	 * Returns number of orthologs in given level 1 full or primary report
	 * 
	 * @param lev1Report - list of LevelOneReportRow objects
	 * @return number of orthologs in report
	 */
	public int returnLevOneOrthologCount(List<LevelOneReportRow> lev1Report) {
		// get ortholog count
		int orthologCount = 0;
		for (LevelOneReportRow row : lev1Report) {
			if (row.getOrtholog().toLowerCase().equals("y")) {
				orthologCount++;
			}
		}
		if (orthologCount <= 0) {
			// setOrtholog_count(0);
			return 0;
		} else {
			return orthologCount - 1;
		}
	}

	/**
	 * updates full and primary reportView ortholog variables for level two
	 */
	public void updateLevTwoOrthologCount() {
		// get full ortholog count
		setFull_level2_ortholog_count(returnLevTwoOrthologCount(levelTwoReport));
		setPrimary_level2_ortholog_count(returnLevTwoOrthologCount(levelTwoPrimaryReport));

	}

	/**
	 * Returns number of orthologs in given level 2 full or primary report
	 * 
	 * @param lev2Report - list of LevelTwoReportRow objects
	 * @return number of orthologs in report
	 */
	public int returnLevTwoOrthologCount(List<LevelTwoReportRow> lev2Report) {
		// get ortholog count
		int orthologCount = 0;
		for (LevelTwoReportRow row : lev2Report) {
			if (row.getOrtholog().toLowerCase().equals("y")) {
				orthologCount++;
			}
		}
		if (orthologCount <= 0) {
			// setOrtholog_count(0);
			return 0;
		} else {
			return orthologCount - 1;
		}

	}

	// this resets the button bar (if currently visible) whenever a new radio
	// button is selected from the main report
	public void resetToMain() {
		// Check to make sure a report has already been loaded and therefore the
		// Button bar is already visible
		if (getRunId() != -1) {
			setRenderLevelButtons(true);
			setRenderLevelOne(false);
			setRenderLevelTwo(false);
			setRenderLevelThree(false);
			setRenderLevelFour(false);
		}
		setMainReportButtonDisabled(false);
		setDisableRAReport(true);
	}

	public void clearLevelThreePanel() {
		setSelectedTaxGroup("");
		setLevel3JobName("");
		setTemplateText("");
		setAdditionalComparisonsText("");
		selectedLevelOneRows.clear();
		selectedLevelOnePrimaryRows.clear();
		setSelectedSpeciesCount(0);
		// updateFilter();
	}

	/********************************************************************
	 * Methods that handle taxonomic group pulldown menu (level 3 panel)
	 *******************************************************************/

	/**
	 * Updates level one report filter based on level3 taxonomic group filtering
	 * pulldown
	 */
	public void updateFilter() {
		setLevelOneFilterString(selectedTaxGroup);
		if (selectedTaxGroup != null) {
			// RequestContext context = RequestContext.getCurrentInstance();
			// context.execute("updateFilterJS();");
			PrimeFaces.current().executeScript("updateFilterJS();");
			if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
				// context.execute("PF('LevOnePrimaryTable').filter();");
				PrimeFaces.current().executeScript("PF('LevOnePrimaryTable').filter();");
			} else {
				// context.execute("PF('LevOneFullTable').filter();");
				PrimeFaces.current().executeScript("PF('LevOneFullTable').filter();");
			}
		}
	}

	/**
	 * Resets the selected taxonomic group and filters by text entered in the
	 * datatable search box (needed to override selected taxonomic group)
	 * 
	 * @param filterEvent
	 */
	public void filterListener(FilterEvent filterEvent) {
		// Map<String, Object> filters = filterEvent.getFilters();
		Map<String, FilterMeta> filters = filterEvent.getFilterBy();

		// handle global filter
//		if (!selectedTaxGroup.equals(filters.get("globalFilter")) && !selectedTaxGroup.equals("")) {
		if (!selectedTaxGroup.equals(filters.get("globalFilter").getFilterValue().toString())
				&& !selectedTaxGroup.equals("")) {
			setSelectedTaxGroup("");
			setLevelOneFilterString(filters.get("globalFilter").toString());
			// setTaxFilterVal("");
		}

	}

	/*****************************************************************
	 * Methods that handle main report check boxes Deprecated. Current code assumes
	 * all values are true.
	 ****************************************************************/

//	/**
//	 * Row checkbox select event for main_report_save.xhtml
//	 * 
//	 * @param event
//	 */
//	public void onRowSelect(SelectEvent event) {
//		ReportRow row = (ReportRow) event.getObject();
//		// only check all boxes if none are already selected.
//		// if boxes already selected, leave them with their current choices
//		// This is needed because clicking any levXChkBox will fire this event
//		// also
//		if (!row.isLev1ChkBox() && !row.isLev2ChkBox() && !row.isLev3ChkBox()) {
//			row.setLev1ChkBox(true);
//			row.setLev2ChkBox(true);
//			row.setLev3ChkBox(true);
//		}
////		setMainReportButtonDisabled(false);
//	}
//
//	/**
//	 * Row checkbox unselect event for main_report_save.xhtml
//	 * 
//	 * @param event
//	 */
//	public void onRowUnselect(UnselectEvent event) {
//		ReportRow row = (ReportRow) event.getObject();
//		row.setLev1ChkBox(false);
//		row.setLev2ChkBox(false);
//		row.setLev3ChkBox(false);
//
//		if (selectedReports.size() == 0) {
//			setMainReportButtonDisabled(true);
//		}
//	}
//
//	/**
//	 * Row checkbox select all event - selects all rows on all pages
//	 * 
//	 * @param event
//	 */
//	public void onAllRowSelect(ToggleSelectEvent event) {
//		if (event.isSelected()) {
//			for (ReportRow row : selectedReports) {
//				row.setLev1ChkBox(true);
//				row.setLev2ChkBox(true);
//				row.setLev3ChkBox(true);
//			}
//			setMainReportButtonDisabled(false);
//		} else {
//			for (ReportRow row : reportList) {
//				if (!selectedReports.contains(row)) {
//					row.setLev1ChkBox(false);
//					row.setLev2ChkBox(false);
//					row.setLev3ChkBox(false);
//				}
//			}
//			setMainReportButtonDisabled(true);
//		}
//	}
//
//	/**
//	 * Updates report rows when lev1ChkBox,lev2ChkBox,lev3ChkBox are
//	 * checked/unchecked
//	 */
//	public void updateReportListLevelChkBox() {
//
//		for (ReportRow row : reportList) {
//			// make sure all rows that have a level box checked have the row box
//			// checked
//			if (row.isLev1ChkBox() || row.isLev2ChkBox() || row.isLev3ChkBox()) {
//				if (!selectedReports.contains(row)) {
//					selectedReports.add(row);
//				}
//				// make sure that row is not checked if no level 1,2,or 3 box is
//				// checked
//			} else {
//				selectedReports.remove(row); // will remove row if it is in list
//			}
//		}
//
//		// make sure the reportList (all reports) has the same checks as the
//		// selectedReports list (selected rows list)
//		for (ReportRow row : selectedReports) {
//			int index = reportList.indexOf(row);
//			if (index != -1) {
//				reportList.get(index).setLev1ChkBox(row.isLev1ChkBox());
//				reportList.get(index).setLev2ChkBox(row.isLev2ChkBox());
//				reportList.get(index).setLev3ChkBox(row.isLev3ChkBox());
//			}
//		}
//
//		if (selectedReports.isEmpty()) {
//			mainReportButtonDisabled = true;
//		} else {
//			mainReportButtonDisabled = false;
//		}
//
//	}

	/*********************************************************************
	 * Level 3 report methods
	 ********************************************************************/

	/**
	 * <p>
	 * Returns a level 3 report (List<LevelThreeReportRow>) containing all available
	 * residues.
	 * <p>
	 * This method is intended to be used when downloading multiple reports without
	 * viewing via a datatable. It does not set up any of the view variables
	 * required for level3report.xhtml
	 * 
	 * @param lev3Req LevelThreeRequestableRow object
	 */
	public List<LevelThreeReportRow> downloadLevel3Complete(LevelThreeRequestableRow lev3Req) {

		// Get all sequence and residue position info

		// Get template sequence, split to list while adding position and left
		// padding to strings
		String sequence = ReportController.requestLevelThreeSequence(lev3Req.getLevel3RunId());
		// 1st integer represents location with all (-) removed, 2nd integer
		// represents actual location in sequence returned from cobalt,
		Map<Integer, Integer> thisSequenceMap = new LinkedHashMap<Integer, Integer>();
		thisSequenceMap.clear();
		List<String> pickListSource = new ArrayList<String>();
		int longest = String.valueOf(sequence.length()).length() + 1;
		String formatStatement = "%" + longest + "s";
		int loc = 0;
		for (int i = 0; i < sequence.length(); i++) {
			if (!Character.toString(sequence.charAt(i)).equals("-")) {
				loc++;
				String newString = String.valueOf(loc) + sequence.charAt(i);
				newString = String.format(formatStatement, newString);
				pickListSource.add(String.format(formatStatement, newString));
				thisSequenceMap.put(loc, i + 1);
			}
		}
		// Populate list that contains actual position in sequence
		List<Integer> positionList = new ArrayList<Integer>();
		for (String row : pickListSource) {
			int actualPosition = thisSequenceMap.get(Integer.parseInt(row.replaceAll("[^0-9]", "")));
			positionList.add(actualPosition);
		}

		LevelThreeViewRequest request = new LevelThreeViewRequest(lev3Req.getLevel3RunId(), positionList);
		// Get level 3 report
		List<LevelThreeReportRow> thisLev3Report = ReportController.getLevelThreeReportForUser(request);
		// Get corresponding level 1 report
		List<LevelOneReportRow> thisLevelOneReport = ReportController
				.getLevelOneReportForUser(lev3Req.getAccessionRunId());
		// Set level 1 info on level 3 report
		for (LevelOneReportRow oneRow : thisLevelOneReport) {
			for (LevelThreeReportRow threeRow : thisLev3Report) {
				if (threeRow.getAccession().equals(oneRow.getAccession())) {
					threeRow.setSpeciesTaxId(oneRow.getSpeciesTaxId());
					threeRow.setProteinCount(oneRow.getProteinCount());
					threeRow.setTaxonomyName(oneRow.getTaxonomyName());
					threeRow.setScientificName(oneRow.getScientificName());
					threeRow.setCommonName(oneRow.getCommonName());
					threeRow.setProteinName(oneRow.getProteinName());
				}
			}
		}

		return thisLev3Report;

	}

	/**
	 * This method sets a level 3 report (List<LevelThreeReportRow>) to the view
	 * variable "levelThreeReport" with no residues chosen. This method is intended
	 * to be used to view a level 3 report via a datatable on level3report.xhtml. It
	 * also sets all of the required view variables required for level3report.xhtml
	 */
	public void updateLevel3Positions() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (levelThreePickList.getTarget().size() > residueLimit) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Residue Limit Exceeded",
					"Maximum of 50 Residues Allowed"));
			return;
		} else if (levelThreePickList.getTarget().size() == 0) {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Residues Selected", "User must select residues"));
			return;
		}

		List<Integer> positionList = new ArrayList<Integer>();

		// This handles a single report
		chosenQueryResidues.clear();
		for (String row : levelThreePickList.getTarget()) {
			chosenQueryResidues.add(row);
			int actualPosition = sequenceMaps.get(0).get(Integer.parseInt(row.replaceAll("[^0-9]", "")));
			positionList.add(actualPosition);
		}
		LevelThreeViewRequest request = new LevelThreeViewRequest(selectedLevel3Info.getLevel3RunId(), positionList);
		setLevelThreeReport(ReportController.getLevelThreeReportForUser(request));

		// This handles a combined report
		if (combinedLevel3Report && selectedAssessorJobs.size() > 0) {

			List<LevelThreeReportRow> combinedReport = new ArrayList<LevelThreeReportRow>();
			combinedReport.add(levelThreeReport.get(0));

			for (int i = 0; i < selectedAssessorJobs.size(); i++) {

				positionList.clear();
				for (String row : levelThreePickList.getTarget()) {
					int actualPosition = sequenceMaps.get(i).get(Integer.parseInt(row.replaceAll("[^0-9]", "")));
					positionList.add(actualPosition);
				}

				LevelThreeViewRequest newRequest = new LevelThreeViewRequest(
						selectedAssessorJobs.get(i).getLevel3RunId(), positionList);
				List<LevelThreeReportRow> newReport = ReportController.getLevelThreeReportForUser(newRequest);
				System.out.println(newReport.get(0).toString());
				for (int j = 1; j < newReport.size(); j++) {
					LevelThreeReportRow row = newReport.get(j);
					combinedReport.add(row);
				}

				// Why won't this work?
				//// newReport.remove(0); //remove first row (template row)
				//// levelThreeReport.addAll(newReport);

			}
			levelThreeReport = combinedReport;
		}
		System.out.println("FE finished call to ReportController.getLevelThreeReportForUser");
		for (LevelOneReportRow oneRow : levelOneReport) {
			for (LevelThreeReportRow threeRow : levelThreeReport) {
				if (threeRow.getAccession().equals(oneRow.getAccession())) {
					threeRow.setSpeciesTaxId(oneRow.getSpeciesTaxId());
					threeRow.setProteinCount(oneRow.getProteinCount());
					threeRow.setTaxonomyName(oneRow.getTaxonomyName());
					threeRow.setScientificName(oneRow.getScientificName());
					threeRow.setCommonName(oneRow.getCommonName());
					threeRow.setProteinName(oneRow.getProteinName());
				}
			}
		}

		List<LevelThreeResidueResult> queryResidueList = levelThreeReport.get(0).getResidueResultList();

		System.out.println("Finished determineLevelThreeMatches");

		for (LevelThreeReportRow row : levelThreeReport) {
			row.getFlatResidues().clear();
			row.getPrimaryFlatResidues().clear();
			int resNum = 0;
			for (LevelThreeResidueResult resResult : row.getResidueResultList()) {
				resNum++;
				int position = resResult.getPosition();
				AminoAcid resAcid = resResult.getAminoAcid();
				if (resAcid == null) {
					row.getFlatResidues().put("Position " + resNum, "-");
					row.getPrimaryFlatResidues().put("Position " + resNum, "-");
					row.getFlatResidues().put("Amino Acid " + resNum, "-");
					row.getPrimaryFlatResidues().put("Amino Acid " + resNum, "-");
					row.getFlatResidues().put("Direct Match " + resNum, "N");
					row.getFlatResidues().put("Side Chain " + resNum, "-");
					row.getFlatResidues().put("Side Chain Match " + resNum, "N");
					row.getFlatResidues().put("MW " + resNum, "-");
					row.getFlatResidues().put("MW Match " + resNum, "N");
					row.getFlatResidues().put("Total Match " + resNum, "N");
					row.getPrimaryFlatResidues().put("Total Match " + resNum, "N");
				} else {
					row.getFlatResidues().put("Position " + resNum, (position == 0) ? "-" : String.valueOf(position));
					row.getPrimaryFlatResidues().put("Position " + resNum,
							(position == 0) ? "-" : String.valueOf(position));
					row.getFlatResidues().put("Amino Acid " + resNum, String.valueOf(resAcid.getId()));
					row.getPrimaryFlatResidues().put("Amino Acid " + resNum, String.valueOf(resAcid.getId()));
					row.getFlatResidues().put("Direct Match " + resNum, convertBooleanToYN(resResult.getDirectMatch()));
					row.getFlatResidues().put("Side Chain " + resNum, resAcid.getSideChain());
					row.getFlatResidues().put("Side Chain Match " + resNum,
							convertBooleanToYN(resResult.getSideChainMatch()));
					row.getFlatResidues().put("MW " + resNum, String.format("%.3f", resAcid.getSize()));
					row.getFlatResidues().put("MW Match " + resNum, convertBooleanToYN(resResult.getSizeMatch()));
					row.getFlatResidues().put("Total Match " + resNum, convertBooleanToYN(resResult.getTotalMatch()));
					row.getPrimaryFlatResidues().put("Total Match " + resNum,
							convertBooleanToYN(resResult.getTotalMatch()));
				}

			}

		}

		// build header list from map keys
		residueHeaders.clear();
		for (String key : levelThreeReport.get(0).getFlatResidues().keySet()) {
			residueHeaders.add(key);
		}
		// Do the same for primary header list
		System.out.println("Primary Header list");
		primaryResidueHeaders.clear();
		for (String key : levelThreeReport.get(0).getPrimaryFlatResidues().keySet()) {
			primaryResidueHeaders.add(key);
			System.out.println("Adding: " + key);
		}

		if (levelThreePickList.getTarget().size() != 0) {
			queryResiduesText = levelThreePickList.getTarget().toString().replaceAll("\\[|\\]", "");
		} else {
			queryResiduesText = "No Residues Selected";
		}

		// update filteredList
		filteredLevelThreeReport.clear();
		for (LevelThreeReportRow row : levelThreeReport) {
			filteredLevelThreeReport.add(row);
		}

		copyFromPickList();

		checkDiffLevel3RAReport();

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelThreeVizData();

		vizView.checkDiffLevel3HeatmapRAReport();

	}

	public void determineLevelThreeMatches(List<LevelThreeReportRow> levelThreeReport) {
		// Assume 1st row is query accession (need to verify/install
		// contingencies)
		List<LevelThreeResidueResult> baseList = levelThreeReport.get(0).getResidueResultList();
		int numRes = baseList.size();
		for (LevelThreeReportRow row : levelThreeReport) {
			List<LevelThreeResidueResult> residueList = row.getResidueResultList();
			for (int i = 0; i < numRes; i++) {
				LevelThreeResidueResult queryResidue = baseList.get(i);
				AminoAcid queryAcid = queryResidue.getAminoAcid();
				LevelThreeResidueResult rowResidue = residueList.get(i);
				AminoAcid rowAcid = rowResidue.getAminoAcid();

				rowResidue.setDirectMatch(rowAcid.getId() == queryAcid.getId());
				rowResidue.setSideChainMatch(rowAcid.getSideChain().equals(queryAcid.getSideChain()));
				rowResidue.setSizeMatch(Math.abs(rowAcid.getSize() - queryAcid.getSize()) <= levelThreeSizeTolerance);

				rowResidue.setTotalMatch(
						rowResidue.getDirectMatch() && rowResidue.getSideChainMatch() && rowResidue.getSizeMatch());
			}
		}

	}

	public AminoAcid findAminoAcidInfo(String id) {

		for (AminoAcid a : AminoAcidInfo) {
			if (a.getId() == id.charAt(0)) {
				return a;
			}
		}

		return null;
	}

	public String convertBooleanToYN(boolean val) {
		if (val) {
			return "Y";
		}
		return "N";
	}

	/************************************************************
	 * Methods to support level 3 report panel
	 ************************************************************/
	/**
	 * Sorts the picklist in level 3 report page
	 * 
	 * @param event
	 */
	public void sortList(TransferEvent event) {

		Collections.sort(levelThreePickList.getSource());
		Collections.sort(levelThreePickList.getTarget());

	}

	/**
	 * Updates the species count for the selected level one report type
	 */
	public void checkBoxListener() {
		switch (levelOneReportType) {
		case Primary:
			setSelectedSpeciesCount(selectedLevelOnePrimaryRows.size());
			break;
		case Full:
			setSelectedSpeciesCount(selectedLevelOneRows.size());
			break;
		default:
			System.out.println("ERROR in checkBoxListener: Unknown Level 1 Report Type " + levelOneReportType);
			break;
		}
	}

	/******************************************************************
	 * Methods that handle level one report checkboxes
	 *****************************************************************/

	/**
	 * Selects all rows in current table view (filtered) instead of just the rows on
	 * the current page
	 * 
	 * @param event
	 */
	public void toggleListener(ToggleSelectEvent event) {
		switch (levelOneReportType) {
		case Primary:
			if (event.isSelected()) {
				for (LevelOneReportRow row : filteredLevelOnePrimaryReport) {
					if (!selectedLevelOnePrimaryRows.contains(row)) {
						selectedLevelOnePrimaryRows.add(row);
					}
				}
			} else {
				for (LevelOneReportRow row : filteredLevelOnePrimaryReport) {
					if (selectedLevelOnePrimaryRows.contains(row)) {
						selectedLevelOnePrimaryRows.remove(row);
					}
				}
			}
			setSelectedSpeciesCount(selectedLevelOnePrimaryRows.size());
			break;
		case Full:
			if (event.isSelected()) {
				for (LevelOneReportRow row : filteredLevelOneReport) {
					if (!selectedLevelOneRows.contains(row)) {
						selectedLevelOneRows.add(row);
					}
				}
			} else {
				for (LevelOneReportRow row : filteredLevelOneReport) {
					if (selectedLevelOneRows.contains(row)) {
						selectedLevelOneRows.remove(row);
					}
				}
			}
			setSelectedSpeciesCount(selectedLevelOneRows.size());
			break;
		default:
			System.out.println("ERROR in toggleListener: Unknown Level 1 Report Type " + levelOneReportType);
			break;
		}

	}

	/**
	 * Handles setting selected rows when switching between full and primary level
	 * one reports
	 */
	public void updateCheckedRowsByReportType() {
		switch (levelOneReportType) {
		case Primary:
			// clear selected(primary) rows and then only add rows from full
			// selected list that are contained in primary report
//			selectedLevelOnePrimaryRows.clear();
			selectedLevelOnePrimaryRows = new ArrayList<LevelOneReportRow>();
			for (LevelOneReportRow row : selectedLevelOneRows) {
				if (levelOnePrimaryReport.contains(row)) {
					selectedLevelOnePrimaryRows.add(row);
				}
			}
			setSelectedSpeciesCount(selectedLevelOnePrimaryRows.size());
			break;
		case Full:
			// only add rows from primary selected list if they are not already
			// in full selected list
			for (LevelOneReportRow row : selectedLevelOnePrimaryRows) {
				if (!selectedLevelOneRows.contains(row)) {
					selectedLevelOneRows.add(row);
				}
			}
			setSelectedSpeciesCount(selectedLevelOneRows.size());
			break;
		default:
			System.out.println("ERROR in updateCheckedRows: Unknown Level 1 Report Type " + levelOneReportType);
			break;
		}
	}

	// this event updates tooltips and enables/disables the level 2 request
	// button
	public void changeSelectedDomain() {
		if (selectedDomain == null) {
			setDisableRequestDomainButton(true);
			setShowLevelTwoRequestTip(false);
		} else if (latestUpdateInfo.getUpdateVersion() > getCurrentReportInfo().getUpdateVersion()) {
			setDisableRequestDomainButton(true);
			setLevelTwoRequestTip(
					"This was run with a previous data version.  Please re-submit the Level 1 accession to enable Level 2 runs.");
			setShowLevelTwoRequestTip(true);
		} else if (!completedLevelTwoDomains.contains(selectedDomain)) {
			setDisableRequestDomainButton(false);
			setShowLevelTwoRequestTip(false);
		} else {
			setDisableRequestDomainButton(true);
			setLevelTwoRequestTip("This domain has already been submitted.");
			setShowLevelTwoRequestTip(true);
		}
	}

	/*****************************************************************
	 * Eukaryote methods
	 ****************************************************************/

	/**
	 * Constructs the full level one report based on user selected eukaryote option
	 * 
	 * @param defaultList - List of LevelOneReportRow objects in default level one
	 *                    report
	 * @return fullList - List of LevelOneReportRow objects in full level one report
	 *         with chosen eukaryote option
	 */
	public List<LevelOneReportRow> filterLevOneEukaryote(List<LevelOneReportRow> defaultList) {
		List<LevelOneReportRow> fullList = new ArrayList<LevelOneReportRow>();
		for (LevelOneReportRow row : defaultList) {
			// if (eukaryotesOnly) {
			if (eukaryotesOnly1) {
				if (row.isEukaryote()) {
					fullList.add(LevelOneReportRow.newInstance(row));
				}
			} else {
				fullList.add(LevelOneReportRow.newInstance(row));
			}
		}

		return fullList;
	}

	/**
	 * Constructs the full level two report based on user selected eukaryote option
	 * 
	 * @param defaultList - List of LevelTwoReportRow objects in default level two
	 *                    report
	 * @return fullList - List of LevelTwoReportRow objects in full level two report
	 *         with chosen eukaryote option
	 */
	public List<LevelTwoReportRow> filterLevTwoEukaryote(List<LevelTwoReportRow> defaultList) {
		List<LevelTwoReportRow> fullList = new ArrayList<LevelTwoReportRow>();

		for (LevelTwoReportRow row : defaultList) {
			// if (eukaryotesOnly) {
			if (eukaryotesOnly2) {
				if (row.isEukaryote()) {
					fullList.add(LevelTwoReportRow.newInstance(row));
				}
			} else {
				fullList.add(LevelTwoReportRow.newInstance(row));
			}
		}
		return fullList;
	}

	/**
	 * Updates level one report based on eukaryote choice
	 */
	public void changeLevelOneReportsWithEukaryoteChoice() {
		setLevelOneReport(filterLevOneEukaryote(getDefaultLevelOneReport()));
		filteredLevelOneReport.clear();
		for (int i = 0; i < levelOneReport.size(); i++) {
			filteredLevelOneReport.add(levelOneReport.get(i));
		}

		// first reset to primary report and filter on evalue and common domains
		setLevelOnePrimaryReport(filterLevOnePrimaryReport(getLevelOneReport()));
		filteredLevelOnePrimaryReport.clear();
		// now set taxonomic group
		System.out.println("levelOnePrimaryReport size: " + levelOnePrimaryReport.size());
		if (levelOnePrimaryReport.size() > 0) {
			updateLevOneReportTaxGroups();
			// updateLevOneTaxGroups();
			// now set susceptibility based on species read-across choice
			updateLevOneSusceptibility(levelOnePrimaryReport, levelOnePrimaryCutValue, false);
			// Update filtered primary report
			for (int i = 0; i < levelOnePrimaryReport.size(); i++) {
				filteredLevelOnePrimaryReport.add(levelOnePrimaryReport.get(i));
			}
		}
		updateLevOneOrthologCount();
		if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
			setOrtholog_count(primary_ortholog_count);
			genLevelOnePrimaryCutoff(this.accessionRunId, this.primaryLevOneEvalueLimit,
					this.primaryLevOneCommonDomainLimit);
		} else {
			setOrtholog_count(full_ortholog_count);
			genLevelOneFullCutoff(this.accessionRunId);
		}

		updateTaxGroupChoices();
		updateLevOneCutoff();

		checkDiffLevel1RAReport();

		setupNewLevel4Run();
	}

	/**
	 * Updates level two report based on eukaryote choice
	 */
	public void changeLevelTwoReportsWithEukaryoteChoice() {
		setLevelTwoReport(filterLevTwoEukaryote(getDefaultLevelTwoReport()));

		filteredLevelTwoReport.clear();
		for (int i = 0; i < levelTwoReport.size(); i++) {
			filteredLevelTwoReport.add(levelTwoReport.get(i));
		}

		// updateLevTwoReport();
		// first reset to primary report and filter on evalue
		setLevelTwoPrimaryReport(filterLevTwoPrimaryReport(getLevelTwoReport()));
		filteredLevelTwoPrimaryReport.clear();

		if (levelTwoPrimaryReport.size() > 0) {
			// now set taxonomic group
			updateLevTwoReportTaxGroups();
			// now set susceptibility based on species read-across choice
			updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoPrimaryCutValue, false);
			// updateLevTwoSusceptibility(levelTwoPrimaryReport,
			// levelTwoCutValue, false);
			// Update filtered primary report

			for (int i = 0; i < levelTwoPrimaryReport.size(); i++) {
				filteredLevelTwoPrimaryReport.add(levelTwoPrimaryReport.get(i));
			}
		}

		updateLevTwoOrthologCount();
		if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
			setLevel2_ortholog_count(primary_level2_ortholog_count);
			genLevelTwoPrimaryCutoff(this.primaryLevTwoEvalueLimit);
		} else {
			setLevel2_ortholog_count(full_level2_ortholog_count);
			genLevelTwoFullCutoff();
		}
		updateTaxGroup2Choices();
		updateLevTwoCutoff();

		checkDiffLevel2RAReport();
	}

	/****************************************************************
	 * Methods that handle report settings for level 1 and 2 reports
	 ***************************************************************/

	/**
	 * Constructs the primary level one report based on user selected eValue and
	 * common domain count
	 * 
	 * @param fullList - List of LevelOneReportRow objects in full level one report
	 * @return primaryList - List of LevelOneReportRow objects in primary level one
	 *         report
	 */
	public List<LevelOneReportRow> filterLevOnePrimaryReport(List<LevelOneReportRow> fullList) {
		List<LevelOneReportRow> primaryList = new ArrayList<LevelOneReportRow>();

		for (LevelOneReportRow row : fullList) {
			if (row.getCommonDomainCount() >= primaryLevOneCommonDomainLimit
					&& row.getEvalue() <= primaryLevOneEvalueLimit) {
				primaryList.add(LevelOneReportRow.newInstance(row));
			}
		}

		return primaryList;
	}

	/**
	 * Constructs the primary level two report based on user selected eValue
	 * 
	 * @param fullList - List of LevelTwoReportRow objects in full level two report
	 * @return - List of LevelTwoReportRow objects in primary level two report
	 */
	public List<LevelTwoReportRow> filterLevTwoPrimaryReport(List<LevelTwoReportRow> fullList) {
		List<LevelTwoReportRow> primaryList = new ArrayList<LevelTwoReportRow>();

		for (LevelTwoReportRow row : fullList) {
			if (row.getEvalue() <= primaryLevTwoEvalueLimit) {
				primaryList.add(LevelTwoReportRow.newInstance(row));
			}
		}

		return primaryList;
	}

	/**
	 * Sets default settings for level one primary reports
	 */
	public void setLevelOneDefaults() {
		setLevelOneSpeciesReadAcross(true);
		setPrimaryLevOneEvalueLimit(0.01d);
		setPrimaryLevOneCommonDomainLimit(1);
		setLevOnePrimaryTaxGroup(taxRanking.CLASS.label);
	}

	/**
	 * Sets default settings for level two primary reports
	 */
	public void setLevelTwoDefaults() {
		setLevelTwoSpeciesReadAcross(true);
		setPrimaryLevTwoEvalueLimit(10.0d);
		setLevTwoPrimaryTaxGroup(taxRanking.CLASS.label);
	}

	/**
	 * Updates level one report using default report settings
	 */
	public void updateLevelOneDefault() {
		setLevelOneDefaults();
		updateLevOneCutoff();
		genLevelOnePrimaryCutoff(accessionRunId, primaryLevOneEvalueLimit, primaryLevOneCommonDomainLimit);
		updateLevOneSusceptibility(levelOnePrimaryReport, levelOnePrimaryCutValue, true);
		updateLevOneReport();
		updateLevOneOrthologCount();

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelOneVizData(true);

		checkDiffLevel1RAReport();
		setupNewLevel4Run();
	}

	/**
	 * Updates level two report using default report settings
	 */
	public void updateLevelTwoDefault() {
		setLevelTwoDefaults();
		genLevelTwoPrimaryCutoff(levelTwoPrimaryCutValue);
		// updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoCutValue,
		// true);
		updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoPrimaryCutValue, true);
		updateLevTwoReport();
		updateLevTwoOrthologCount();

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelTwoVizData(true);

		checkDiffLevel2RAReport();
	}

	/**
	 * Updates level one report using current report settings (Does not update
	 * cutoff data or graph)
	 */
	public void updateLevOneReport() {
		// first get primary report from default report (so tax groups can be
		// set for all rows regardless of eukaryote status)
		setLevelOnePrimaryReport(filterLevOnePrimaryReport(getDefaultLevelOneReport()));
		retrieveLevOneTaxGroups();

		// now get primary report (from full report) and filter on evalue and
		// common domains
		setLevelOnePrimaryReport(filterLevOnePrimaryReport(getLevelOneReport()));
		filteredLevelOnePrimaryReport.clear();
		// now set taxonomic group

		if (getLevelOnePrimaryReport().size() > 0) {
			updateLevOneReportTaxGroups();
			updateTaxGroupChoices();
			// now set susceptibility based on species read-across choice
			updateLevOneSusceptibility(levelOnePrimaryReport, levelOnePrimaryCutValue, false);
			// Update filtered primary report
			for (int i = 0; i < levelOnePrimaryReport.size(); i++) {
				filteredLevelOnePrimaryReport.add(levelOnePrimaryReport.get(i));
			}
		}

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelOneVizData(true);
	}

	/**
	 * Updates level one report using current report settings (Updates cutoff data
	 * AND graph)
	 */
	public void updateLevOneReportButton() {
		genLevelOnePrimaryCutoff(accessionRunId, primaryLevOneEvalueLimit, primaryLevOneCommonDomainLimit);
		updateLevOneReport();
		updateLevOneOrthologCount();

		// compareLevel1RAReport();
		// level1RAReportDiffers = true;
		checkDiffLevel1RAReport();
		setupNewLevel4Run();
	}

	/**
	 * Updates level two report using current report settings (Does not update
	 * cutoff data or graph)
	 */
	public void updateLevTwoReport() {
		// first get primary report from default report (so tax groups can be
		// set for all rows regardless of eukaryote status)
		setLevelTwoPrimaryReport(filterLevTwoPrimaryReport(getDefaultLevelTwoReport()));
		retrieveLevTwoTaxGroups();

		// new get primary report (from full report) and filter on evalue
		setLevelTwoPrimaryReport(filterLevTwoPrimaryReport(getLevelTwoReport()));

		if (getLevelTwoPrimaryReport().size() > 0) {
			updateLevTwoReportTaxGroups();
			updateTaxGroup2Choices();
			// now set susceptibility based on species read-across choice
			// updateLevTwoSusceptibility(levelTwoPrimaryReport,
			// levelTwoCutValue, false);
			updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoPrimaryCutValue, false);
			// Update filtered primary report
			filteredLevelTwoPrimaryReport.clear();
			for (int i = 0; i < levelTwoPrimaryReport.size(); i++) {
				filteredLevelTwoPrimaryReport.add(levelTwoPrimaryReport.get(i));
			}
		}
		updateLevTwoOrthologCount();

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");
		vizView.reloadLevelTwoVizData(true);
	}

	/**
	 * Updates level two report using current report settings (Updates cutoff data
	 * AND graph)
	 */
	public void updateLevTwoReportButton() {
		System.out.println("Inside updateLevTwoReportButton");
		genLevelTwoPrimaryCutoff(primaryLevTwoEvalueLimit);
		// updateLevTwoSusceptibility(levelTwoPrimaryReport, levelTwoCutValue,
		// true);
		updateLevTwoReport();

		checkDiffLevel2RAReport();
	}

	/*********************************************************
	 * Methods that update taxonomic groups
	 ********************************************************/

	/**
	 * Updates list of taxonomic groups available for sorting via report settings
	 * panel
	 */
	public void updateTaxGroupChoices() {

		List<LevelOneReportRow> report = new ArrayList<LevelOneReportRow>();

		taxonomyGroup.clear();
		switch (levelOneReportType) {
		case Primary:
			report = levelOnePrimaryReport;
			break;
		case Full:
			report = levelOneReport;
			break;
		default:
			break;
		}

		taxonomyGroup = returnLev1TaxGroupChoices(report);

		// Collections.copy(taxonomyGroupByPercSim, taxonomyGroup);

		taxonomyGroupByPercSim = new ArrayList<String>(taxonomyGroup);
		System.out.println("     " + taxonomyGroupByPercSim.size() + " taxonomy groups");

		Collections.sort(taxonomyGroup, String.CASE_INSENSITIVE_ORDER);

	}

	public List<String> returnLev1TaxGroupChoices(List<LevelOneReportRow> report) {

		List<String> taxGroup = new ArrayList<String>();

		for (LevelOneReportRow row : report) {
			String taxName = row.getTaxonomyName();
			if (!taxGroup.contains(taxName)) {
				taxGroup.add(taxName);
			}
		}
		return taxGroup;
	}

	/**
	 * Updates list of taxonomic groups available for sorting via report settings
	 * panel
	 */
	public void updateTaxGroup2Choices() {
		taxonomyGroup2.clear();
		switch (levelTwoReportType) {
		case Primary:
			for (LevelTwoReportRow row : levelTwoPrimaryReport) {
				// taxGroups.put(row.getTaxonomyTaxid(), row.getTaxonomyName());
				String taxName = row.getTaxonomyName();
				if (!taxonomyGroup2.contains(taxName)) {
					taxonomyGroup2.add(taxName);
				}
			}
			break;
		case Full:
			for (LevelTwoReportRow row : levelTwoReport) {
				String taxName = row.getTaxonomyName();
				if (!taxonomyGroup2.contains(taxName)) {
					taxonomyGroup2.add(taxName);
				}
			}
			break;
		default:
			break;
		}

		// Collections.copy(taxonomyGroupByPercSim, taxonomyGroup);

		taxonomyGroup2ByPercSim = new ArrayList<String>(taxonomyGroup2);
		System.out.println("     " + taxonomyGroup2ByPercSim.size() + " taxonomy groups");

		Collections.sort(taxonomyGroup2, String.CASE_INSENSITIVE_ORDER);
	}

	public void retrieveLevOneTaxGroups() {
		// compile hashmap with species taxids

		HashMap<Integer, TaxGroup> grpMap = new HashMap<Integer, TaxGroup>();
		for (LevelOneReportRow row : getDefaultLevelOneReport()) {
			grpMap.put(row.getSpeciesTaxId(), new TaxGroup());
		}

		if (grpMap.size() > 0) {
			SpeciesTaxGrouping speciesTaxGrp = new SpeciesTaxGrouping(getLevOnePrimaryTaxGroup(), grpMap);

			// call webservice to return map<int,String> for each taxid
			setLevelOneReportTaxGrouping(ReportController.getTaxGroups(speciesTaxGrp));
		}
	}

	public void updateLevOneReportTaxGroups() {
		// compile hashmap with species taxids
		HashMap<Integer, TaxGroup> grpMap = new HashMap<Integer, TaxGroup>();
		for (LevelOneReportRow row : getLevelOnePrimaryReport()) {
			grpMap.put(row.getSpeciesTaxId(), new TaxGroup());
		}

		if (getLevelOneReportTaxGrouping() == null) {
			System.out.println("Level one groupMap is null!!");
			return;
		}

		if (getLevelOneReportTaxGrouping().getGroupMap().size() > 0) {

			// update primary table with new tax groups
			for (LevelOneReportRow row : getLevelOnePrimaryReport()) {

				TaxGroup rowGrp = getLevelOneReportTaxGrouping().getGroupMap().get(row.getSpeciesTaxId());
				row.setTaxonomyLevel(rowGrp.getLevel());
				String name = rowGrp.getName();
				if (name != null) {
					if (name.equalsIgnoreCase("root")) {
						name = row.getScientificName();
					}
				} else {
					name = "Not Found";
				}
				row.setTaxonomyName(name);
				row.setTaxonomyTaxid(rowGrp.getId());
			}

			setLevelOnePrimaryFirstRow(getLevelOnePrimaryReport().get(0));
		} else {
			getLevelOnePrimaryReport().clear();
		}
	}

	public void retrieveLevTwoTaxGroups() {
		// compile hashmap with species taxids

		HashMap<Integer, TaxGroup> grpMap = new HashMap<Integer, TaxGroup>();
		for (LevelTwoReportRow row : getDefaultLevelTwoReport()) {
			grpMap.put(row.getSpeciesTaxId(), new TaxGroup());
		}

		if (grpMap.size() > 0) {
			SpeciesTaxGrouping speciesTaxGrp = new SpeciesTaxGrouping(getLevTwoPrimaryTaxGroup(), grpMap);

			// call webservice to return map<int,String> for each taxid
			setLevelTwoReportTaxGrouping(ReportController.getTaxGroups(speciesTaxGrp));
		}

	}

	// updates level two primary report based on selected tax group
	public void updateLevTwoReportTaxGroups() {

		if (getLevelTwoReportTaxGrouping() == null) {
			System.out.println("Level two groupMap is null!!");
			return;
		}

		if (getLevelTwoReportTaxGrouping().getGroupMap().size() > 0) {

			// update primary table with new tax groups
			for (LevelTwoReportRow row : getLevelTwoPrimaryReport()) {

				TaxGroup rowGrp = getLevelTwoReportTaxGrouping().getGroupMap().get(row.getSpeciesTaxId());
				row.setTaxonomyLevel(rowGrp.getLevel());
				String name = rowGrp.getName();
				if (name != null) {
					if (name.equalsIgnoreCase("root")) {
						name = row.getScientificName();
					}
				} else {
					name = "Not Found";
				}
				row.setTaxonomyName(name);
				row.setTaxonomyTaxid(rowGrp.getId());
			}
			setLevelTwoPrimaryFirstRow(getLevelTwoPrimaryReport().get(0));
		} else {
			getLevelTwoPrimaryReport().clear();
		}

	}

	// updates level two primary report based on selected tax group
	// public void updateLevTwoTaxGroups() {
	// // compile hashmap with species taxids
	//
	// HashMap<Integer, TaxGroup> grpMap = new HashMap<Integer, TaxGroup>();
	// for (LevelTwoReportRow row : getLevelTwoPrimaryReport()) {
	// grpMap.put(row.getSpeciesTaxId(), new TaxGroup());
	// }
	//
	// if (grpMap.size() > 0) {
	// SpeciesTaxGrouping speciesTaxGrp = new
	// SpeciesTaxGrouping(getLevTwoPrimaryTaxGroup(), grpMap);
	//
	// // call webservice to return map<int,String> for each taxid
	// SpeciesTaxGrouping taxGrpMap =
	// ReportController.getTaxGroups(speciesTaxGrp);
	//
	// // update primary table with new tax groups
	// for (LevelTwoReportRow row : getLevelTwoPrimaryReport()) {
	//
	// TaxGroup rowGrp = taxGrpMap.getGroupMap().get(row.getSpeciesTaxId());
	// row.setTaxonomyLevel(rowGrp.getLevel());
	// String name = rowGrp.getName();
	// if (name != null) {
	// if (name.equalsIgnoreCase("root")) {
	// name = row.getScientificName();
	// }
	// } else {
	// name = "Not Found";
	// }
	// row.setTaxonomyName(name);
	// row.setTaxonomyTaxid(rowGrp.getId());
	// }
	// setLevelTwoPrimaryFirstRow(getLevelTwoPrimaryReport().get(0));
	// } else {
	// getLevelTwoPrimaryReport().clear();
	// }
	//
	// }

	// Updates the level 3 amino acid picklist with valus from comma separated
	// list of amino acid positions
	public void copyToPickList() {

		FacesContext context = FacesContext.getCurrentInstance();
		String errorMessage = null;
		List<String> errorLocs = new ArrayList<String>();

		// reset picklist before moving items
		for (String item : levelThreePickList.getTarget()) {
			levelThreePickList.getSource().add(item);
		}
		levelThreePickList.getTarget().clear();
		Collections.sort(levelThreePickList.getSource());

		// now determine index of items to move to target list
		List<Integer> itemLocs = new ArrayList<Integer>();
		List<String> items = Arrays.asList(positionBoxList.split(","));
		for (int i = 0; i < items.size(); i++) {
			if (!items.get(i).contains("-")) {
				Integer loc = Integer.parseInt(items.get(i).replaceAll("[^0-9]", ""));
				if (loc != null) {
					if (loc > levelThreePickList.getSource().size() || loc <= 0) {
						// add invalid items to error list for logging
						errorLocs.add(items.get(i));
					} else {
						itemLocs.add(loc);
					}
				}
			} else {
				// handle - sign (range of values (ok) or negative value
				// (error))
				// first remove whitespace
				String tmp = items.get(i).replaceAll("\\s", "");
				// now remove all non-numbers (allow -)
				tmp = tmp.replaceAll("[^0-9-]", "");
				// now check if it is actually a range of integers such as
				// 127-135
				Pattern rangePattern = Pattern.compile("([0-9]+)-([0-9]+)");
				Matcher m = rangePattern.matcher(tmp);
				if (m.find()) {
					Integer startLoc = Integer.parseInt(m.group(1));
					Integer endLoc = Integer.parseInt(m.group(2));
					if (startLoc != null && endLoc != null) {
						for (int loc = startLoc; loc <= endLoc; loc++) {
							if (loc < levelThreePickList.getSource().size()) {
								itemLocs.add(loc);
							} else {
								errorLocs.add(Integer.toString(loc));
							}
						}
					} else {
						errorLocs.add(tmp);
					}
				} else {
					System.out.println(" pattern NOT matched");
					errorLocs.add(tmp);
				}

			}
		}
		// now remove duplicates
		itemLocs = new ArrayList<>(new HashSet<>(itemLocs));
		// and reverse sort items
		Collections.sort(itemLocs, Collections.reverseOrder());

		for (Integer loc : itemLocs) {
			String item = levelThreePickList.getSource().get(loc - 1);
			levelThreePickList.getTarget().add(item);
			levelThreePickList.getSource().remove(item);
		}

		// sort list
		Collections.sort(levelThreePickList.getSource());
		Collections.sort(levelThreePickList.getTarget());

		// display error message (if needed)
		if (errorLocs.size() > 0) {
			errorMessage = "Error: " + errorLocs.toString() + " outside of template range 1-"
					+ (levelThreePickList.getSource().size() + levelThreePickList.getTarget().size());
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_WARN, errorMessage, ""));
		}

	}

	// copies amino acids selected in level 3 picklist to comma separated list
	public void copyFromPickList() {
		List<String> pickListItems = levelThreePickList.getTarget();
		String copyListItems = "";
		for (String item : pickListItems) {
			copyListItems += item.replaceAll("[^0-9]", "");
			copyListItems += ",";
		}
		positionBoxList = copyListItems.substring(0, copyListItems.length() - 1);
	}

	// this only works on local machine. Not when deployed to server
	// public void copyToClipboard(String stringToCopy) {
	// System.out.println("You want to copy " + stringToCopy);
	// Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	// StringSelection selection = new StringSelection(stringToCopy);
	// clipboard.setContents(selection, selection);
	// }

	public void populateInfoTextfromDB(String infoBox) {
		setInfoText(infoBox);
		setInfoHeaderText("Need to set this");
	}

	public void populateInfoTextFromDB(Link link) {
		setInfoText(link.getInfoText());
		setInfoHeaderText(link.getInfoHeader());
	}

	public void populateInfoText(String infoBox) {
		switch (infoBox) {
		// Level 1 Page
		case "lev1PrimaryReportSettings":
			setInfoText("<html><body>" + "<p><b>Manipulating Primary Report Settings</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To manipulate the \"Primary Report Settings\" for the data table use the "
					+ "options below and click the \"Update Report\" button." + "<br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; By clicking the \"Use Default Settings\" button, "
					+ "all default settings will be restored including the \"E-Value\" to 0.01, \"Sorted by Taxonomic Group\" to "
					+ "class, \"Common Domains\" to 1, and \"Species Read-Across\" to Yes" + "</p>" + "</body></html>");
			setInfoHeaderText("Primary Report Settings");
			break;
		case "eValue":
			setInfoText("<html><body>" + "<p><b>An E-value (expect value) describes the number of different alignments"
					+ " expected to occur by chance.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Any protein with an E-value greater than the number in the"
					+ " box will be eliminated from the data table.  Most queries require values to be change by many orders"
					+ " of magnitude to see changes in the data.<br/></p>"
					+ "<p><p style=\"font-size: 80%\">Note: Scientific notation is also supported (.01 = 1.0E-2)</p>"
					+ "</body></html>");
			setInfoHeaderText("E-value");
			break;
		case "taxGroup":
			setInfoText("<html><body>" + "<p><b>Selected Taxonomic Hierarchy</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Choose the level of taxonomic hierarchy to display in the "
					+ "\"Filtered Taxonomic Group\" column in the table below.<br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the selected taxonomic hierarchy level is not described in "
					+ "NCBI for a species, that species will be grouped at the next available taxonomic hierarchy assigned by "
					+ "the NCBI Taxonomy database (class is the broadest grouping available).</p>" + "</body></html>");
			setInfoHeaderText("Taxonomic Group");
			break;
		case "commonDomains":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The number chosen for the \"Common Domains\" setting determines"
					+ " how many common domains a protein must share with the query protein to be included in the \"Primary Report\""
					+ " table below.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Proteins have a maximum number of common domains for each protein."
					+ " Setting this value greater than the number of domains in your query protein will result in an empty table.  To"
					+ " identify the maximum number of domains for each protein, click on the \"Full Report\" radio button above the"
					+ " primary data table and view the \"Common Domain Count\" column for the query species.</p>"
					+ "</body></html>");
			setInfoHeaderText("Common Domains");
			break;
		case "speciesReadAcross":
			setInfoText("<html><body>"
					+ "<p><b>Species Read-Across is used to set the susceptibility prediction</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Under the default setting of \"Yes\", in the data table, all ortholog"
					+ " candidates have a susceptibility prediction of \"Y\"; all species listed above the susceptibility cut-off"
					+ " have a susceptibility prediction of \"Y\"; all species below the cut-off from the same taxonomic group with one"
					+ " or more species above the cut-off have a susceptibility prediction of \"Y\"; and those below the cut-off that"
					+ " are not ortholog candidates and do not belong to a taxonomic group above the cut-off have a susceptibility"
					+ " prediction of \"N\".</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; When \"No\" is selected for \"Species Read-Across\", the susceptibility"
					+ " predictions will only be \"Y\" in the table below if the \"Percent Similarity\" is greater than or equal to the"
					+ " cut-off or if the hit is identified as an ortholog candidate.  Any hit below the cut-off will yield a susceptibility"
					+ " prediction of \"N\".</p>" + "</body></html>");
			setInfoHeaderText("Species Read-Across");
			break;
		case "cutoff":
			// not currently used
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Click the button beside \"Default\" or \"Second Local Minimum\" to automatically"
					+ " set the susceptibility cut-off.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Click the \"User Defined\" button to open a new tab where you can set the"
					+ " cut-off manually.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Click \"View Cutoff\" for more detailed information."
					+ "</body></html>");
			setInfoHeaderText("Susceptibility Cut-off");
			break;
		case "visualization":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; A new tab displaying the user defined information will open upon clicking the \"Visualize Data\""
					+ " button and the option to select an interactive graphical representation of the selected data table will be available in either the"
					+ " \"Primary Report\" or \"Full Report\".</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Visualization will actively update to reflect changes to the data table.</p>"
					+ "</body></html>");
			setInfoHeaderText("Visualize Data");
			break;
		case "visualizationHeatMap":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Clicking the \"Visualize Data\" button will open a new tab displaying graphical information"
					+ " and an option to select an interactive graphical representation of the selected data table (\"Primary Report\" or \"Full Report\").</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Visualization will actively update to reflect changes to the Heat Map.</p>"
					+ "</body></html>");
			setInfoHeaderText("Visualize Data");
			break;
		case "level2":
			setInfoText("<html><body>"
					+ "<p><b>Level 2 analyses align selected functional domains across species to provide additional lines of evidence:"
					+ " for protein similarity</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the \"NCBI Conserved Domain Database\" link provided to find \"specific hits\" to align.  Select"
					+ " from all available domains in the dropdown menu titled \"Functional Domains\" and submit the alignment of the selected domain by clicking"
					+ " \"Request Domain Run\" button.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Upon completion and refreshing the page, the domain alignment results will be available in the second"
					+ " dropdown menu titled \"Choose Domain to View\".  Select domain results to view and click \"View Level 2 Data\" button.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 2");
			break;
		case "ncbiCDD":
			setInfoText("<html><body>"
					+ "<p><b>Unlike previous links found in SeqAPASS, the \"NCBI Conserved Domain Database\" link is a necessary step in the"
					+ " pipeline for a Level 2 SeqAPASS analysis.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the database link to identify domains that are \"specific hits\".</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; In the NCBI Conserved Domain Database, scroll over the graphical representation of the domains associated"
					+ " with the query sequence to highlight and identify the Accession associated with domain \"specific hits\" (e.g. cd03436 or pfam00123).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Return to SeqAPASS when domain accession(s) of interest have been identified.</p>"
					+ "</body></html>");
			setInfoHeaderText("NCBI Conserved Domain Database");
			break;
		case "selectDomain":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Having identified domain accession(s) of interest from the \"NCBI Conserved Domain Database\", open the"
					+ " \"Functional Domains\" dropdown menu and scroll to the domain of interest.  Note that the position of the domain is listed first in parenthesis"
					+ " followed by the domain accession and domain name.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search text box on top of the dropdown menu can be used to search the dropdown list.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the selected domain has not already been run by the user, the \"Request Domain Run\" button will become"
					+ " active and the user can click it to submit the domain query.</p>" + "</body></html>");
			setInfoHeaderText("Select Functional Domain");
			break;
		case "viewDomain":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; After a domain run has been requested, it will need to complete (typically in a matter of seconds) and the page"
					+ " will need to be refreshed before the domain becomes available to view.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user can check the run status in the \"Run Status\" tab (make sure to click the \"Level 2\" radio button) or"
					+ " simply refresh this page by clicking on the \"Refresh Level 2 and 3 runs\" button below.  Once the domain run is complete, it will be"
					+ " present in the \"View Level 2 Data\" dropdown.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To view completed results for Level 2, highlight the domain of interest in the dropdown box and click the \"View"
					+ " Level 2 Data\" button.  This action will bring the user to the \"Level 2\" data page for the selected query protein/domain.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search text box on top of the dropdown menu can be used to search the dropdown list.</p>"
					+ "</body></html>");
			setInfoHeaderText("View Completed Domain");
			break;
		case "level3":
			setInfoText("<html><body>"
					+ "<p><b>Level 3 evaluates similarity of critical individual amino acids across selected species.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This feature requires the user to select which sequences/species to align via interaction with both the \"Level 3"
					+ " Amino Acid Residue(s)\" box and the Level 1 \"Primary/Full Report\" data table.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 3");
			break;
		case "refExplorer":
			setInfoText("<html><body>"
					+ "<p><b>The reference explorer can be used to identify primary literature that supports the identification of critical amino"
					+ " acids to be used in the Level 3 SeqAPASS evaluation.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Here, the protein name from the query protein autopopulates a predefined Boolean string that includes relevant"
					+ " search terms.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user can use the generated search string to query Google Scholar to identify relevant literature.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user can customize the search string by adding or removing terms using the functions in Reference Explorer.</p>"
					+ "</body></html>");
			setInfoHeaderText("Reference Explorer");
			break;
		case "templateSequence":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; In the \"Select Template Sequence\" box, enter either an NCBI protein accession with the version number (e.g. NP_000116.2)"
					+ " or a FASTA formatted sequence.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The template sequence is the sequence to which all other selected sequences will be compared and is expected to be derived"
					+ " from published literature (x-ray crystallography, site-directed mutagenesis, field resistance/sensitivity, homology models, QSARs).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that the template sequence may not be the same as the query sequence, depending on available structural information.</p>"
					+ "</body></html>");
			setInfoHeaderText("Select Template Sequence");
			break;
		case "additionalComps":
			setInfoText("<html><body>"
					+ "<p><b>The \"Additional Comparisons\" text box is an optional feature used to add an accession/sequence that does not appear in the \"Primary/Full"
					+ " Report\" tables.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Enter a NCBI protein accession with the version number (e.g. NP_000116.2) or a FASTA formatted sequence.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that this text box can also be left empty if all accessions/sequences of interest can be found in the primary data table.</p>"
					+ "</body></html>");
			setInfoHeaderText("Additional Comparisons");
			break;
		case "lev3RunName":
			setInfoText("<html><body>"
					+ "<p><b>Enter a name for the Level 3 run in the \"Enter Level 3 Run Name\" text box.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The chosen name can be composed of alphanumeric characters, spaces, underscores, and dashes.  A name that contains"
					+ " other characters will be rejected.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user-defined name is used to identify the completed Level 3 run in the \"View Level 3 Data\" dropdown.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user must submit unique names for each Level 3 evaluation within the same Level 1 Query Accession.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 3 Run Name");
			break;
		case "chooseTaxGroups":
			setInfoText("<html><body>"
					+ "<p><b>The \"Choose Taxonomic Group(s)\" dropdown is used to select species from a specific taxonomic group to align with the"
					+ " specified template sequence.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Selection of a taxonomic group will auto-fill the search bar on the data table below with the name selected and"
					+ " filter the table by that taxonomic group."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user manually selects the checkbox beside any species you wish to align to the template sequence.  Ideally"
					+ " these sequences share similar names and are not labelled as low quality or partial sequences (unless that is all that is available).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Checked boxes remain selected when moving through the other pages and the total number of species selected for"
					+ " the alignment can be viewed in the Level 3 box by the \"# species selected\" text above the \"Request Residue Run\" button.</p>"
					+ "</body></html>");
			setInfoHeaderText("Choose Taxonomic Group(s)");
			break;
		case "prioritizeLev3":
			setInfoText("<html><body>"
					+ "<p><b>To aid users in more rapid Level 3 sequence alignments, default settings deselect hypothetical protein, LOW QUALITY PROTEIN, unnamed protein product,"
					+ " partial, etc. sequences that are unlikely to be useful in the Level 3 sequence alignment."
					+ "<p><p style=\"font-size: 80%\">&#8226; Users should still be sure to check all annotations to ensure alignments are between proper sequences."
					+ "<p><p style=\"font-size: 80%\">&#8226; If a user is interested in a specific species, it may be of interest to include a sequence that is deselected by default."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user may choose to include/exclude any sequence by selecting or deselecting the check box."
					);
			setInfoHeaderText("Prioritize Protein Accessions for Level 3 Sequence Alignments");
			break;
		case "viewLev3Data":
			setInfoText("<html><body>"
					+ "<p><b>Use the \"Choose Query to View\" dropdown menu to select a Level 3 run to view.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Runs are indexed by the user-defined name provided when the run was originally submitted.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the run was successfully submitted but not listed in the dropdown, refresh the page by clicking"
					+ " the \"Level 1\" button in the top left of the page.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user can check the \"SeqAPASS Run Status\" tab to determine if the Level 3 run has completed.</p>"
					+ "</body></html>");
			setInfoHeaderText("View Level 3 Data");
			break;
		case "level3ComboReport":
			setInfoText("<html><body>"
					+ "<p><b>The Level 3 \"View Combined Report\" section allows the user to combine alignments from more than one taxonomic"
					+ " group when the same template sequence was used</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This feature allows the user to customize the data output table in a specific order, based on taxonomic"
					+ " group, when moving on to view the Level 3 results and select individual amino acids to align in the next step of the Level 3 workflow.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; A pop-up will appear where the user will first select the common template sequence, then select the"
					+ " Level 3 runs to combine, and finally choose the order in which those runs will be presented in the data table.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 3 Combination Report");
			break;
		case "level4":
			setInfoText("<html><body>"
					+ "<p><b>Level 4 generates a list of FASTA sequences required for submission to I-TASSER, which creates protein structural models for structural alignment and use in advanced bioinformatics analyses.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This feature requires the user to select pre-prioritized sequences for developing structural models using I-TASSER "
					+ " (Iterative Threading ASSEmbly Refinement, <a href=\"https://zhanggroup.org/I-TASSER/\" target=\"_blank\">https://zhanggroup.org/I-TASSER/</a>"
					+ " <a class=\"exit-disclaimer\" style=\"font-size: 0.75em;\" href=\"https://www.epa.gov/home/exit-epa\" title=\"EPA's External Link Disclaimer\">Exit</a>.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; After running I-TASSER, protein structural models are generated along with metrics describing the quality of these structures.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Quality structures can be aligned to the query protein using TM-align structural alignment tool, producing metrics"
					+ " associated with the alignment including a prediction of susceptibility based on structural conservation.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 4");
			break;
		case "level4RunName":
			setInfoText("<html><body>"
					+"<p><b>Enter a name for the Level 4 run in the \"Enter Level 4 Run Name\" text box.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The chosen name can be composed of alphanumeric characters, spaces, underscores, and dashes. A name that contains other"
					+ " characters will be rejected."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user defined name is used to identify the completed Level 4 jobs in the \"View Level 4 Data\" dropdown."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user must submit unique names for each Level 4 evaluation within the same Level 1 Query Accession."
					+ "<p><p style=\"font-size: 80%\">&#8226; NOTE: User defined names are identifiers that can be used to combine multiple jobs in the \"View Combined Report\" section"
					+ " of the Level 4 workflow");
			setInfoHeaderText("Level 4 Run Name");
			break;
		case "prioritizeFASTAs":
			setInfoText("<html><body>"
					+ "<p><b>Select sequences to generate a list of FASTA</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The accessions have been pre-priorized by default to include accessions with NP_######.#, and Swiss-Prot Accessions."
					+ " Sequences that are hypothetical, partial, Low quality, unnamed, unknown or XP_######.# are excluded by default."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user has the option to include additional sequences to generate FASTA, however, if using the sequences for molecular"
					+ " modeling it is advisable to ensure quality sequences are selected."
					+ "<p><p style=\"font-size: 80%\">&#8226; The number of accessions selected for FASTA generation is tallied below the \"Prioritize Accessions\" button");
			setInfoHeaderText("Level 4 Prioritize Accessions Table");
			break;
		case "levFourPriorityTable":
			setInfoText("<html><body>"
					+ "<p><b>Select sequences to generate a list of FASTA</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The accessions have been pre-priorized by default to include accessions with NP_######.#, and Swiss-Prot Accessions."
					+ " Sequences that are hypothetical, partial, Low quality, unnamed, unknown or XP_######.# are excluded by default."
					+ "<p><p style=\"font-size: 80%\">&#8226; The user has the option to include additional sequences to generate FASTA, however, if using the sequences for molecular"
					+ " modeling it is advisable to ensure quality sequences are selected."
					+ "<p><p style=\"font-size: 80%\">&#8226; The number of accessions selected for FASTA generation is tallied below the \"Prioritize Accessions\" button");
			setInfoHeaderText("Level 4 Prioritize Accessions Table");
			break;
		case "levFourFASTAsTable":
			setInfoText("<html><body>"
					+"<p><b>Select FASTAs for submission to I-TASSER</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; All protein FASTAs are de-selected by default."
					+"<p><p style=\"font-size: 80%\">&#8226; Users should submit <b>no more than 10</b> FASTA at a time, focusing only on those necessary for answering their specific question."
					+"<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase in the search bar"
					+" will be kept in the table. However, Dat Version, Protein Count, Species Tax ID, BLASTp Bitscore, Ortholog Count, Cutoff, Percent Similarity, and Eukaryote are not searchable,"
					+" and you should not use them to identify runs you want to find later."
					+"<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click the column header for"
					+" the category you wish to sort by. To reverse the direction of the sort, click on the highlighted header."
					+"<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next page in the direction"
					+" indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number buttons."
					+"<p><p style=\"font-size: 80%\">&#8226; If user customizes the table, \"Update Changes\" button should be clicked to proceed with selected FASTA."
					+"<p><p style=\"font-size: 80%\">&#8226; The number of selected FASTA is tallied below the \"Filter FASTA Table\" button.");
			setInfoHeaderText("Level 4 Select FASTA for Protein Modeling");
			break;
		case "selectLevel4FASTA":
			setInfoText("<html><body>"
					+ "<p><b>Select the user defined name from the dropdown to choose FASTA for creating protein structural models</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; User defined names are listed in the dropdown."
					+ "<p><p style=\"font-size: 80%\">&#8226; Upon selecting the appropriate FASTA list, click \"Filter FASTA Table\" button");
			setInfoHeaderText("Select Level 4 FASTA List");
			break;
		case "level4FilterFASTA":
			setInfoText("<html><body>"
					+"<p><b>Select FASTAs for submission to I-TASSER</b><br/></p>"
					+"<p><ul>"
					+"<li>All protein FASTAs are de-selected by default.</li>"
					+"<li>The user should <b>limit the number to 10</b> FASTA at a time submitted to those needed for the question being answered by the user.</li>"
					+"<li>The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase in the search bar"
					+" will be kept in the table. However, Dat Version, Protein Count, Species Tax ID, BLASTp Bitscore, Ortholog Count, Cutoff, Percent Similarity, and Eukaryote are not searchable,"
					+" and you should not use them to identify runs you want to find later.</li>"
					+"<li>This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click the column header for"
					+" the category you wish to sort by. To reverese the direction of the sort, click on the highlighted header.</li>"
					+"<li>Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next page in the direction"
					+" indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number buttons.</li>"
					+"<li>If user customizes the table, \"Update Changes\" button should be clicked to proceed with selected FASTA.</li>"
					+"<li>The number of selected FASTA is tallied below the \"Filter FASTA Table\" button.</li>"
					+"<li>SeqAPASS will detect if a user selects multiple accessions with identical FASTAs to be submitted to I-TASSER in Level 4. To reduce runtimes, only one of the identical accessions will be submitted through I-TASSER, and it's resultant structure and metrics will be used for all the other identical accessions.</li>");
			setInfoHeaderText("Level 4 Select FASTA for Protein Modeling");
			break;
		case "pdbLink":
			setInfoText("<html><body>"
					+"<p><b>The user can optionally select a PDB to be considered when generated the protein model in I-TASSER. The link to the RCSB homepage is provided to identify empirically"
					+" derived protein structures</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; I-TASSER allows the user to submit a RCSB Protein Data Bank protein structure as a template."
					+"<p><p style=\"font-size: 80%\">&#8226; It would be desirable to use a PDB for which a chemical of interest is bound to the protein structure."
					+"<p><p style=\"font-size: 80%\">&#8226; Type in the PDBID:ChainID, (e.g., 1WOR:A). If the chain information is not present in the PDB file, indicate the ChainID using \"_\" (e.g., 1WOR:_)");
			setInfoHeaderText("Select a Protein Structure Template for I-TASSER");
			break;
		case "alphaFoldPanel":
			setInfoText("<html><body>"
					+"<p><b>Rows of the table include all Level 1 full report protein accessions that could be converted to Swiss Prot Accessions</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Click on the row (not direction on the radio button) to push information to the \"Information for TM-align\" section below."
					+"<p><p style=\"font-size: 80%\">&#8226; Click on a different row to push new information to the \"Information for TM-align\" section below."
					+"\"<p><p style=\"font-size: 80%\">&#8226; The user must identify the PDB from external sources to submit to TM-align."
					);
			setInfoHeaderText("Input Protein Structures from External Sources for TM-align");
			break;
		case "alphaFoldReport":
			setInfoText("<html><body>"
					+"<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry. Only rows that contain the phrase"
					+" in the search bar will be kept in the table."
					+"<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages. The inner arrow will take you to the next page in"
					+" the direction indicated, while the outer arrows take you to the first and last pages. You can also move between pages by clicking the page number buttons."
					+"<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page and can be changed by clicking"
					+" on the box and then selecting the desired number."
					);
			setInfoHeaderText("Swiss Prot Conversion Table for AlphaFold Structure Identification");
			break;
		case "requestUniProt":
			setInfoText("<html><body>"
					+"<p><b>Use this button to update the Swiss Prot Conversion Table below with the NCBI to Swiss Prot conversions based on the current Level One Full Report.</b><br/></p>"
					+"<p><p style=\\\"font-size: 80%\\\">&#8226; This process may take some time to complete. This response time will depend on the size of the Level One Full Report and"
					+" the number of accessions that need to be converted."
					);
			setInfoHeaderText("Request/Update Swiss Prot Conversion Table");
			break;
		case "icn3d":
			setInfoText("<html><body>"
					+ "<p><b>The iCn3D feature in SeqAPASS is intended to be used for a quick visual assessment of the I-TASSER generated protein structures. For full iCn3D functionality use iCn3D on the web."
					+ "</br></br>"
					+ "Further information and detailed instructions for usage of the iCn3D tool can be found in links in the \"Help\" dropdown in the iCn3D menu or at "
					+ " <a href=\"https://www.ncbi.nlm.nih.gov/Structure/icn3d/icn3d.html#selectb\" target=\"_blank\" rel=\"noreferrer_noopener\">iCn3D:Web-based 3D Structure Viewer (nih.gov)</a>. However, instructions for some of the functions most frequently used within SeqAPASS can be found below.</b>"
					+ "<p><ul>"
					+ "<li>Push structures to iCn3D directly from the \"I-TASSER Results &amp; TM-align Selection\" table using the \"Push to iCn3D\" button. Note: Remember the order of the structures you push as the 1st will be named stru; the 2nd will be named stru2, 3rd will be stru3……etc.</li>"
					+ "<li>Use \"Background Color\" radio buttons to change the background color.</li>"
					+ "<li>Use the \"Align/Superpose\" button at the bottom of the iCn3d viewer to align structures. Note: If dialog boxes open on the page, resize the box and then they can be moved or closed.</li>"
					+ "<li>Use the \"Open as Pop-out\" button to view a smaller visualization. Pressing \"X\" to exit this pop-out will return iCn3D to its full-sized embedding in SeqAPASS.</li>"
					+ "<li>Use the \"Reset iCn3D\" button to clear the iCn3D viewer.</li>"
					+ "<li>Load a RCSB PDB file into iCn3D</li>"
					+ "<ul>"
					+ "<li>Select \"File\", \"Retrieve by ID\", \"PDB/MMDB/AlphaFold IDs\", and then enter the PDB ID for the structure of interest.</li>"
					+ "<li>Select the \"Append Biological Unit\" option.</li>"
					+ "</ul>"
					+ "<li>Load a PDB file from your computer</li>"
					+ "<ul>"
					+ "<li>Select \"File\", \"Open File\", \"PDB Files (appendable)\", and choose PDB files from your computer.</li>"
					+ "<li>Click the Append button to open in the iCn3D window.</li>"
					+ "</ul>"
					+ "<li>Alter the visual appearance of structures</li>"
					+ "<ul>"
					+ "<li>Click \"Select\", \"Defined Sets\", and click or ctrl+click (for selecting multiple structures) any/all sets of interest. The selected structure will be the one highlighted in the viewer.</li>"
					+ "<li>Change structure color</li>"
					+ "<ul>"
					+ "<li>Color options can be selected from within the \"Color\" dropdown in the iCn3D menu. Choose \"Unicolor\", then select the color of interest.  Use \"Defined sets\" to select another structure and repeat as desired. Note: colors will all return to magenta upon pushing a new structure into the iCn3D viewer.</li>"
					+ "</ul>"
					+ "<li>Change structure style</li>"
					+ "<ul>"
					+ "<li>Style options can be selected from within the \"Style\" dropdown in the iCn3D menu. Users can change the representation of selected proteins as ribbons, strands, ball and stick, and more. Users can also show or hide protein side chains.</li>"
					+ "</ul>"
					+ "</ul>"
					+ "</ul>"
					);
			setInfoHeaderText("iCn3D");
			break;
		case "selectRunLevel":
			setInfoText("<html><body>"
					+ "<p><b>Select whether structural models will be generated for the entire protein sequence or a specific domain</b>"
					+ "<p><ul>"
					+ "<li>The \"Level 1\" option uses the full protein sequence to generate structure models of the entire protein for each accession.</li>"
					+ "<li>The \"Level 2\" option allows users to select from a dropdown of NCBI Conserved Domain Database-curated protein domains for which each homology models will be generated for each accession.</li>"
					+ "<ul>"
					+ "<li>Protein domains must have been previously ran in Level 2 of SeqAPASS in order to appear in the dropdown for Level 4.</li>"
					+ "<li>NOTE: Selecting a Level 2 domain to be run will automatically add text to the user defined run name to inform the user that the jobs was a submitted domain (eg. [(310) cd06949]).</li>"
					+ "</ul>"
					+ "</ul>");
			setInfoHeaderText("Select Run Level");
			break;
		case "level4Restraint":
			setInfoText("<html><body>"
					+ "<p><ul>"
					+ "<li>If submitting a Level 2 (domain-specific) run, it would be desirable to use a PDB representing the entire domain sequence of interest.</li>"
					+ "<li>To submit a user defined restraint, open the protein structure file as a .txt and copy the PDB text. Paste the text in the \"Input User-Defined Restraint (Optional)\" field.</li>"
					+ "<li>NOTE: Selecting a restraint for a run will automatically add text to the run name to inform the user a PDB restraint was submitted (eg. [w/restraint 4ZN7+A)]. This will combine with any domain signifiers present if a Level 2 job was submitted (eg. [(310) cd06949(w/restraint 4ZN7+A)]).</li>"
					+ "</ul>");
			setInfoHeaderText("Choose I-TASSER Restraint");
			break;
		case "icn3dResultCol":
			setInfoText("<html><body>"
					+ "<p><b>Push protein PDBs directly to the \"iCn3D: Visualize Protein Structures\" embedded tool in the Level 4 Report</b>"
					+ "<p><ul>"
					+ "<li>Click the \"Push to iCn3D\" button to visualize 3D structures predicted by I-TASSER in SeqAPASS directly with the embedded iCn3D tool.</li>"
					+ "</ul>");
			setInfoHeaderText("iCn3D");
			break;
//		case "chooseLevel4Report":
//			setInfoText("<html><body>"
//					+"<p><b>After the I-TASSER runs have been requested, at least one job must begin to run to be viewed (typically this will take hours to days) and the page will need to be refreshed"
//					+" before the user defined Report Name becomes available to view.</b><br/></p>"
//					+"<p><p style=\"font-size: 80%\">&#8226; The user can check the run status in the \"Run Status\" tab (make sure to click the \"Level 4\" radio button) or simply refresh this page by"
//					+" clicking on the \"Refresh Level 4 Runs\" button below. Once the I-TASSER run has begun to run, it will be present in the \"Choose Report to View\" dropdown."
//					+"<p><p style=\"font-size: 80%\">&#8226; To view completed results for Level 4, highlight the user defined name of interest in the dropdown box and click the \"View Level 4 Data\" button."
//					+" This action will bring the user to the \"Level 4\" data page for the selected job.");
//			setInfoHeaderText("Level 4 Select Report to View I-TASSER Results");
//			break;
		case "chooseLevel4Reports":
			setInfoText("<html><body>"
					+"<p><b>After the I-TASSER runs have been requested, at least one job must begin to run to be viewed (typically this will take hours to days) and the page will need to be refreshed"
					+" before the user defined Report Name becomes available to view.</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; The user can check the run status in the \"Run Status\" tab (make sure to click the \"Level 4\" radio button) or simply refresh this page by"
					+" clicking on the \"Refresh Level 4 Runs\" button below. Once the I-TASSER run has started, it will be present in the \"Choose Report to View\" dropdown."
					+"<p><p style=\"font-size: 80%\">&#8226; To view completed results for Level 4, highlight the user defined name of interest in the dropdown box and click the \"View Level 4 Data\" button."
					+" This action will bring the user to the \"Level 4\" data page for the selected job."
					+"<p><b>Due to the limit on the number of I-TASSER jobs being run at any one time per user there will be instances where the user has multiple user-defined names for I-TASSER jobs that\n"
					+" must be combined upon completion to represent the full dataset.</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; After the I-TASSER jobs have completed, the user may combine reports by selecting the user-defined job names from the drop-down."
					+"<p><p style=\"font-size: 80%\">&#8226; Ideally the user should combine jobs that included the same I-TASSER Restraint upon Requesting the I-TASSER Run or did not include an I-TASSER"
					+" Restraint at all."
					+"<p><p style=\"font-size: 80%\">&#8226; To view combined results, click the \"View Combined Level 4 Data\" button. This action will bring the user to the \"Level 4\" data page for the selected jobs."
					);
			setInfoHeaderText("Level 4 Select Report(s) to View I-TASSER Results");
			break;
		case "ReportTableSettings":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Default \"Primary/Full Report\" table settings highlight partial protein sequences, sequences with a bitscore"
					+ " higher than the query domain and therefore percent similarity greater than 100% (commonly synthetic constructs), and when zero ortholog candidates"
					+ " are identified (in which case a different query sequence should be chosen).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; These highlights can be deselected by clicking the checkbox next to the highlighted text.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Additionally, by default the \"Primary/Full Report\" table shows only eukaryote data.  To include prokaryote"
					+ " data, deselect the \"Show Only Eukaryotes\" checkbox.</p>" + "</body></html>");
			setInfoHeaderText("Report Table Settings");
			break;
		case "viewSummaryReport":
			setInfoText("<html><body>"
					+ "<p><b>The user can view a summary report of the data based on the taxonomic groups.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; For each Taxonomic group, the data summary includes number of species, mean percent similarity,"
					+ " median percent similarity, and susceptibility prediction.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; These data can be downloaded.</p>" + "</body></html>");
			setInfoHeaderText("Summary Report");
			break;
		case "currentReportSettings":
			setInfoText("<html><body>"
					+ "<p><b>Downloading the current report settings allows the user to capture the current settings applied, either"
					+ " default or user-defined, to the specific report (primary or full).</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The specific SeqAPASS evaluation Level (1, 2, or 3) will dictate the settings that"
					+ " are presented in the report</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Note that if the report settings are modified by the user post-download, a new report should"
					+ " be downloaded to capture the updated settings that represent the current report.</p>"
					+ "</body></html>");
			setInfoHeaderText("Current Report Settings");
			break;
		case "levOnePrimaryReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Protein Count, Species Tax ID, BLASTp Bitscore, Ortholog Count, Cutoff, Percent"
					+ " Similarity, and Eukaryote are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 1 Data - Primary");
			break;
		case "levOneFullReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Identical Protein,  Protein Count, Species Tax ID, Hit Length, Identity, Evalue,"
					+ " BLASTp Bitscore, Ortholog Count, Cutoff, Common Domain Count, Percent"
					+ " Similarity, and Eukaryote are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 1 Data - Full");
			break;
		case "pushLevOneToDS":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To push information to the Decision Summary Report, the user can choose to push either the current default settings or"
					+ " the user defined settings by clicking the \"Push Level 1 to DS Report\" button.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the user chooses to make subsequent changes to the Level 1 settings after pushing results to the Decision Summary"
					+ " Report, the user must again push the results from the updated Level 1 Report settings by clicking the \"Push Level 1 DS Report\" button to modify the Level 1"
					+ " section of the Decision Summary Report.</p>" + "</body></html>");
			setInfoHeaderText("Push Level 1 Results to Decision Summary Report");
			break;
		case "ecotoxWidget":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The ECOTOX Widget allows for the user to create an ECOTOX Custom Filter by selecting specie(s) and chemical(s) of interest. "
					+ "The widget allows for rapid access of curated empirical toxicity data from the ECOTOXicology (ECOTOX) Knowledgebase (<a href=\"https://cfpub.epa.gov/ecotox\" target=\"_blank\">https://cfpub.epa.gov/ecotox</a>) that can "
					+ "be compared to sequence-based predictions of chemical susceptibility from SeqAPASS results.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The species and chemicals that can be selected are those that are available in ECOTOX.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The selections from the ECOTOX Widget in SeqAPASS are pushed to the ECOTOX Explore feature, opening the ECOTOX results "
					+ "for the selected species/chemical combination in a separate browser tab for the user to mine the data.</p>"
					+ "</body></html>");
			setInfoHeaderText("ECOTOX Widget");
			break;
		case "ecotoxSpeciesSelection":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The species present in \"Select Species\" are generated from the Level 1 results table. If a species is not found in ECOTOX, "
					+ "the selection checkbox will be greyed out and cannot be included in the Custom Group pushed to ECOTOX.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; There is a maximum number of 500 species that can be selected using the ECOTOX Widget.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Species selected will be included in the ECOTOX Custom Group by NCBI TaxID and those "
					+ "with available toxicity data will be shown in the ECOTOX browser.</p>" + "</body></html>");
			setInfoHeaderText("Select Species");
			break;
		// ecotoxChemicalSelection
		case "ecotoxChemicalSelection":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Selecting chemicals will add that chemical to the Custom Group created in ECOTOX.  Please note that the user can click the "
					+ "\"Open in ECOTOX\" button without selecting any chemicals as well.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the user chooses to incorporate a chemical in the ECOTOX query, the chemical search can be initiated in the ECOTOX Widget "
					+ "by typing 3 letters in the text box to get an autopopulated list of chemicals or by typing the exact name of the chemical of interest.  Chemicals can also be found "
					+ "by searching by the exact CASRN or the DTXSID.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Only 5 chemicals can be included in the Custom Group.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Links out to the \"CompTox Chemicals Dashboard\" and \"ECOTOX Chemicals\" are available to help find chemicals of interest.</p>"
					+ "</body></html>");
			setInfoHeaderText("Select Chemicals (Optional)");
			break;
		// Level 2 page
		case "lev2PrimaryReportSettings":
			setInfoText("<html><body>" + "<p><b>Manipulating Primary Report Settings</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To manipulate the \"Primary Report Settings\" for the data table use the "
					+ "options below and click the \"Update Report\" button." + "<br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; By clicking the \"Use Default Settings\" button, "
					+ "all default settings will be restored including the \"E-Value\" to 0.01, \"Sorted by Taxonomic Group\" to "
					+ "class, and \"Species Read-Across\" to Yes" + "</p>" + "</body></html>");
			setInfoHeaderText("Primary Report Settings");
			break;
		case "levTwoPrimaryReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Protein Count, Species Tax ID, BLASTp Bitscore, Ortholog Count, Cutoff, Percent"
					+ " Similarity, and Eukaryote are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 2 Data - Primary");
			break;
		case "levTwoFullReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Identical Protein, Protein Count, Species Tax ID, BLASTp Bitscore, Ortholog Count, Cutoff, Percent"
					+ " Similarity, and Eukaryote are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 2 Data - Full");
			break;
		case "pushLevTwoToDS":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To push information to the Decision Summary Report, the user must push either the current default settings or"
					+ " the user defined settings to populate the DS Report.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If the user chooses to change the settings after the DS Report has been populated, the user must again push the"
					+ " current Level 2 Report settings to change the Level 2 section of the report by clicking the \"Push Level 2 to DS Report\" button.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To add more than onde domain to the Level 2 section of the Decision Summary Report, the user must run additional"
					+ " domains in Level 2, open the results for the domains, and click the \"Push to Level 2 DS Report\" button to populate the Level 2 section of the"
					+ " Decision Summary Report with results from multiple domains.</p>" + "</body></html>");
			setInfoHeaderText("Push Level 2 Results to Decision Summary Report");
			break;
		// level 3 page
		case "residues":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To select specific amino acid(s) from the template sequence to align to other selected species,"
					+ " select position(s)/amino acid(s) in the left-hand box and click the right arrow button to shuttle the selections to the right.  Use"
					+ " ctrl-click to select multiple individual amino acids. To remove an amino acid from the right-hand box, select the amino acid and click"
					+ " the left arrow. To clear all amino acids from the right-hand box, click the bottom arrow with a bar.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Alternatively, if amino acid positions are known, the user can type them in a comma separated list"
					+ " (e.g., 145,222,367,680) into the \"Enter Amino Acid Residue Positions\" text box to the right and click \"Copy to Residue List\".</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Click \"Update Report\" when desired amino acids have been selected. The Level 3 data table will update"
					+ " with the aligned amino acids.</p>" + "</body></html>");
			setInfoHeaderText("Select Amino Acid Residues");
			break;
		case "levThreePrimaryReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Protein Count, Species Tax ID, Position, Amino Acid, and Total Match"
					+ " are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 3 Data - Primary");
			break;
		case "levThreeFullReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry.  Only rows that contain the phrase"
					+ " in the search bar will be kept in the table.  However, Data Version, Protein Count, Species Tax ID, Position, Amino Acid, Direct Match, Side Chain,"
					+ " Side Chain Match, MW, MW Match, and Total Match"
					+ " are <b>not</b> searchable and you should not use them to identify runs you want to find later.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Percent Similarity, highest to lowest by default.  To sort by a different column, click"
					+ " the column header for the category you wish to sort by.  To reverse the direction of the sort, click on the highlighted header.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages.  The inner arrow will take you to the next"
					+ " page in the direction indicated, while the outer arrows take you to the first and last pages.  You can also move between pages by clicking the page number"
					+ " buttons.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by"
					+ " clicking on the box and then selecting the desired number.</p>" + "</body></html>");
			setInfoHeaderText("Level 3 Data - Full");
			break;
		case "pushLevThreeToDS":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user may choose to populate the Decision Summary Report with results from one complete Level 3 run at a time.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Once amino acids are selected and Level 3 results are populated, the user may choose to push the results to the"
					+ " Decision Summary Report to provide a summary in the Level 3 section.  To change amino acids, the user must update the Level 3 page with the new amino acids"
					+ " then again, click the \"Push to Level 3 DS Report\" button to populate the Level 3 results in the Decision Summary Report.</p>"
					+ "</body></html>");
			setInfoHeaderText("Push Level 3 Results to Decision Summary Report");
			break;
		// level 4 page
		case "level4Cutoff":
			setInfoText("<html><body>"
					+ "<p><b>The Density Plot generated here displays the Absolute Length difference between the query species sequence and the hit species sequence, expressed as"
					+" a percent</b><br/></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; On the density plot, the length cutoff is the first local minimum greater than the global maximum."
					+"<p><p style=\"font-size: 80%\">&#8226; Length \"Okay\" in the \"Cut-off Status\" column represents amino acid sequence lengths where values were above the"
					+" cutoff (i.e., lengths are similar enough to query for meaningful comparison)."
					+"<p><p style=\"font-size: 80%\">&#8226; Length \"Check\" in the \"Cut-off Status\" column represents amino acid sequence lengths were values above cutoff"
					+" (i.e., lengths differ too much from query species).");
			setInfoHeaderText("Level 4 Cut-off");
			break;
		case "level4ITasser":
			setInfoText("<html><body>"
					+ "<p><b>The table containing I-TASSER results displaying the metrics associated with each protein structure generated. Quality of structures are considered"
					+ " \"High\" if C-Score is between -5 and 2, will receive a label to \"Check\" the structure if C-Score is between 5 and 2 but RMSD&gt;95th percentile and/or"
					+ " Density&lt;5th percentile, and \"Low\" if C-Score&lt;-5 and &gt;2 and/or TM-Score&lt;0.5</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; The C-score or confidence score is commonly between -5 and 2, where the greater the value the higher the confidence in the model"
					+"<p><p style=\"font-size: 80%\">&#8226; TM-Score is a metric for assessing the topological similarity of protein structures and has a value between 0 and 1,"
					+" where 1 indicates a perfect match between two structures. A TM-Score&gt;0.5 indicates a model of correct topology whereas a TM-Score&lt;0.17 indicates a random similarity"
					+"<p><p style=\"font-size: 80%\">&#8226; The Root Mean Square Deviation (RMSD) of atomic positions is the measure of the average distance between the atoms of superimposed"
					+" proteins.  Therefore, the lower RMSD, the closer the model is to the target structure."
					+"<p><p style=\"font-size: 80%\">&#8226; The number of protein structure decoys, which are the artificial structural conformations of proteins used to guide the design,"
					+" testing, and training of the protein folding force fields, are reported for each model. The cluster density is also reported and used to define the number of structure"
					+" decoys at a unit of space in the cluster. A higher cluster density means the structure occurs more often in the simulation trajectory and is therefore likely a"
					+" higher-quality model.");
			setInfoHeaderText("Level 4 I-TASSER Results & PDB Selection");
			break;
		case "levelFourQuality":
			setInfoText("<html><body>"
					+"<p><b>This call will be designated in the column with the header \"Quality\"</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; There are 3 possible designations for Quality:"
					+"<p><p style=\"font-size: 80%;margin-left: 40px\">&#8226; \"High\" which indicates metrics are within the recommended ranges for a quality structure."
					+"<p><p style=\"font-size: 80%;margin-left: 40px\">&#8226; \"Check\" which indicates either the RMSD or Density is outside the expected range (highlighting the the cells"
					+" describes which parameter is driving the destination)."
					+"<p><p style=\"font-size: 80%;margin-left: 40px\">&#8226; \"Low\" which indicates that the metrics are outside the recommended ranges for a quality structure.");
			setInfoHeaderText("Determination of Quality Structures");
			break;
		case "levFourITasserReport":
			setInfoText("<html><body>"
					+"<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry. Only rows that contain the phrase in the search bar"
					+" will be kept in the table. However, PDB, C-Score, TM-Score, RMSD, Density, and Absolute Length are not searchable, and you should not use them to identify runs"
					+" you want to find later"
					+"<p><p style=\"font-size: 80%\">&#8226; This table is sorted by C-Score, highest to lowest by default. To sort by a different column, click the column header for the category"
					+" you wish to sort by. To reverse the direction of the sort, click on the highlighted header."
					+"<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages. The inner arrow will take you to the next page in the direction"
					+" indicated, while the outer arrows take you to the first and last pages. You can also move between pages by clicking the page number buttons."
					+"<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by clicking on the box"
					+" and then selecting the desired number.");
			setInfoHeaderText("Level 4 I-TASSER Results Table");
			break;
		case "pushITasserToTMAlign":
			setInfoText("<html><body>"
					+"<p><b>After evaluating I-TASSER structures for quality and length the user will choose those for alignment.</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Click the \"Push to TM-align\" button to shuttle the I-TASSER structures to the \"TM-Align Selection\" Table.");
			setInfoHeaderText("Determination of Quality Structures");
			break;
		case "levelFourDownloadZippedPDB":
			setInfoText("<html><body>"
					+"<p><b>Collect all generated protein structures</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Click the \"Download Zipped PDB Text Files\" button below the table to create a .zip file with all the PDBs in the table.>");
			setInfoHeaderText("Download Protein Structures as PDB Files");
			break;
		case "LevFourTMAlignSelectionPanel":
			setInfoText("<html><body>"
					+"<p><b>Select checkboxes for protein structures you wish to align in TM-align</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; All proteins structures are selected by default.");
			setInfoHeaderText("Select Structures for TM-align Submission");
			break;
		case "LevFourTMAlignSelectionTable":
			setInfoText("<html><body>"
					+"<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry."
					+"<p><p style=\"font-size: 80%\">&#8226; All columns are searchable in this table"
					+"<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages. The inner arrow will take you to the next page in the"
					+" direction indicated, while the outer arrows take you to the first and last pages. You can also move between pages by clicking the page number buttons."
					+"<p><p style=\"font-size: 80%\">&#8226; On the bottom of the page, use the dropdown box to the right of the arrows to control how many rows are shown on the table."
					+" The number of rows can be changed by clicking on the box and then selecting the desired number of rows.");
			setInfoHeaderText("Level 4 TM-align Selection");
			break;
		case "LevelFourSearchTemplate":
			setInfoText("<html><body>"
					+"<p><b>This option allows the user to choose which species all other species will be compared to for the structural alignment.</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; All proteins in the table are available in the drop down to \"Select Query Accession\"."
					+"<p><p style=\"font-size: 80%\">&#8226; Job Names include original protein sequence accession followed by the user defined name of the job(s) and whether they were"
					+" submitted to I-TASSER with a restraint as a template."
					+"<p><p style=\"font-size: 80%\">&#8226; Use the search features to identify a specific query species/structure."
					+"<p><p style=\"font-size: 80%\">&#8226; The selected species will be compared 1:1 to every structure in the table that has been highlighted."
					+"<p><p style=\"font-size: 80%\">&#8226; Click the \"Submit to TM-Align\" to begin structural alignments between the query species and all other structures using TM-align."
					);
			setInfoHeaderText("Select Query Species/Structure");
			break;
		case "LevFourTMAlignResultsPanel":
			setInfoText("<html><body>"
					+"<p><b>The table containing the TM-align results displays the similarity between the user selected template and all other selected structures.</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; At this Level all aligned structures are by default designated with a Susceptibility call of \"Y\" for \"yes\"."
					+" It is up to the expert user to determine if there is enough evidence from visualizing the quality metrics and the structures to alter the Susceptibility call to"
					+" \"N\" for \"no\"."
					+"<p><p style=\"font-size: 80%\">&#8226; Metrics regarding the aligned structures from I-TASSER are available in the table."
					+"<p><p style=\"font-size: 80%\">&#8226; L1 is the length of the user selected template structure and L2 is the length of the aligned hit structure."
					+"<p><p style=\"font-size: 80%\">&#8226; Value 1 is the TM-score that compares the template structure to the hit structure and Value 2 is the TM-score that compares"
					+" the hit structure to the template."
					+"<p><p style=\"font-size: 80%\">&#8226; The Average Value is the average of Value 1 and Value 2."
					);
			setInfoHeaderText("Level 4 TM-align Results");
			break;
		case "viewLevFourTMAlignReport":
			setInfoText("<html><body>"
					+"<p><b>Multiple TM-align Reports can be generated. The user can select which Report to View in the table below.</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Jobs are named by the TM-align Template Accession (TM-align SeqAPASS ID(s):User defined"
					+" name of the report)."
					+"<p><p style=\"font-size: 80%\">&#8226; Select the desired TM-align Report from the dropdown menu and click \"View TM-align Report\" button."
					);
			setInfoHeaderText("Select Level 4 TM-align Report to View");
			break;
		case "levFourTMAlignReport":
			setInfoText("<html><body>"
					+"<p><p style=\"font-size: 80%\">&#8226; The search bar above the table can be used to quickly find a particular entry. Only rows that contain the phrase in the search bar"
					+" will be kept in the table."
					+"<p><p style=\"font-size: 80%\">&#8226; This table is sorted by Average Percent Similarity, highest to lowest by default. To sort by a different column, click the column header for the category"
					+" you wish to sort by. To reverse the direction of the sort, click on the highlighted header."
					+"<p><p style=\"font-size: 80%\">&#8226; Use the arrow icons at the bottom of the table to switch between pages. The inner arrow will take you to the next page in the direction"
					+" indicated, while the outer arrows take you to the first and last pages. You can also move between pages by clicking the page number buttons."
					+"<p><p style=\"font-size: 80%\">&#8226; The dropdown box to the right of the arrows controls how many entries are shown on a page, and can be changed by clicking on the box"
					+" and then selecting the desired number.");
			setInfoHeaderText("Level 4 TM-align Results Table");
			break;
		case "pdbHeader":
			setInfoText("<html><body>"
					+"<p><b>Download each I-TASSER generated protein structure</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Click the \"Download PDB As Text File\" button within the table.");
			setInfoHeaderText("Download Protein Structure as PDB Files");
			break;
		case "TMalignDownloadZippedPDB":
			setInfoText("<html><body>"
					+"<p><b>Collect all TM-align protein structures</b></br></p>"
					+"<p><p style=\"font-size: 80%\">&#8226; Click the \"Download Zipped PDB Text Files\" button below the table to create a .zip file with all the PDBs in the table.>");
			setInfoHeaderText("Download Protein Structures as PDB Files");
			break;
		// cutoff pages
		case "densityPlot":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The default Susceptibility Cut-off is determined from the density plot and detection of ortholog candidates (sequences"
					+ " that have diverged from a speciation event and are more likely to maintain similar function).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Cut-offs #s presented in the table are identified by finding the local minimums in the density plot and moving up in"
					+ " percent similarity until the next ortholog candidate = \"Y\" is identified. The percent similarity of the identified ortholog candidate is presented in the table.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The default setting uses the first local minimum and the next ortholog candidate to set the Susceptibility Cut-off.  The"
					+ " Cut-off # presented depends on the number of local minimums identified in the density plot.</p>"
					+ "</body></html>");
			setInfoHeaderText("Density Plot");
			break;
		case "selectCutoff":
			setInfoText("<html><body>"
					+ "<p><b>The susceptibility cut-off can be manipulated by choosing an option from the \"Select"
					+ " Cut-off\" dropdown.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; If \"User defined cut-off\" is selected from the dropdown, the text"
					+ " box to the right of the menu will become active, allowing the user to type a cut-off value.  Entry must be a"
					+ " number 0-100, and all values will automatically round to the nearest hundredth.  Inputs outside of this range"
					+ " will not be processed, and the cut-off will be set at whatever value it was before the improper input was entered.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that the user should have a scientific justification for changing"
					+ " the Susceptibility Cut-off.</p>" + "</body></html>");
			setInfoHeaderText("Select Cutoff");
			break;
		// home page
		case "reportProblem":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">When submitting a Comment/Question, please provide "
					+ "as much detail as possible.  If reporting a bug, please include details such as what "
					+ "happened and what actions led to the problem.  Specific details, such as query protein/"
					+ "species/accession information would be appreciated.  A well documented issue will be easier "
					+ "to address than a poorly documented issue." + "</body></html>");
			setInfoHeaderText("Comment/Question/Problem");
			break;
		// request run page
		case "searchType":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">Selecting the \"By Species\" radio button allows you to create "
					+ "a query with the name of your target species (scientific/common name or NCBI taxonomy ID) and "
					+ "the name of your target protein.</p>"
					+ "<p><p style=\"font-size: 80%\">Selecting the \"By Accession\" radio button allows you to input "
					+ "an NCBI protein accession directly.  Only use this option if you are familiar with NCBI and the "
					+ "protein accessions you wish to query.</p>" + "</body></html>");
			setInfoHeaderText("Search Types");
			break;
		case "statusSample":
			setInfoText("Sample Text");
			setInfoHeaderText("Sample Header");
			break;
		case "querySpeciesSelection":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">Type at least three letters of your target species' common "
					+ "or scientific name or NCBI taxonomy ID (e.g., 90988).  A dropdown list will appear with species whose names "
					+ "contain the text in the search bar.  You can scroll through the list and click on your species or continue "
					+ "typing until you see your species appear in the list and click on it.</p>"
					+ "<p><p style=\"font-size: 80%\">Note that you need to click on your species to progress to the next step.  If "
					+ "you cannot find your species when searching its common name, the easiest solution is to search by scientific name.</p>"
					+ "<p><p style=\"font-size: 80%\">After you have clicked on the species in the dropdown list, click the \"Add Query Species\" "
					+ "button.  The species will then be added to the box below.  At this point, you can move on the next step or "
					+ "repeat this process to add more species to the box.</p>" + "</body></html>");
			setInfoHeaderText("Query Species Selection");
			break;
		case "queryProteinSelection":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">Before beginning this step, ensure the name of the species of interest is selected "
					+ "in the \"Query Species\" box above (this is only applicable if more than one species has been added to the box).</p>"
					+ "<p><p style=\"font-size: 80%\">Once the species is selected, the \"Query Panel\" box populates with the first 200 "
					+ "proteins associated with that species, if available.  Find your protein of interest by typing part of (e.g., estrogen "
					+ "receptor) or all of (e.g., estrogen receptor isoform 1) the protein name in the \"Query Protein Search\" text box "
					+ "and then click the \"Filter Protein\" button.  The list will then include only proteins whose name contains your search "
					+ "term(s).  Click on your protein of interest (Shift-click to highlight a group, Ctrl-click to select multiple proteins) "
					+ "and then click the \"Add Selected Protein(s)\" button, which will move selected proteins to the \"Final Query Proteins(s)\" "
					+ "box for query submission</p>"
					+ "<p><p style=\"font-size: 80%\">If you are not finding your protein of interest try a variety of search terms or follow the "
					+ "NCBI Protein Database link to search for proteins of interest.</p>" + "</body></html>");
			setInfoHeaderText("Query Protein Selection");
			break;
		case "submitRun":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">The \"Final Query Protein(s)\" box contains the protein(s) selected from the previous step.  "
					+ "This is your opportunity to do a final check before submission.  If there are proteins in the box you decide you do not wish "
					+ "to query, click on them and then press \"Remove Selected Protein(s)\".  Click \"Remove All Proteins\" to clear the box completely.  "
					+ "It is HIGHLY recommended that users submit no more than 10 query proteins as a batch.</p>"
					+ "<p><p style=\"font-size: 80%\">To submit proteins for SeqAPASS evaluation, click \"Request Run\".  If you want to clear all fields "
					+ "on the page, click the \"Clear\" button.</p>" + "</body></html>");
			setInfoHeaderText("SeqAPASS Submission");
			break;
		case "submitByAccession":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">Type the NCBI Protein Accession (e.g., NP_000116.2 or P03372.2) that you wish to query in to the box. "
					+ "Type each new accession on a separate line if you wish to query a batch.  It is HIGHLY recommended that no more than 10 accessions be "
					+ "submitted as a batch.  When the accession(s) have been entered, click \"Request Run\".  Click \"Clear\" to clear all accessions typed "
					+ "in the box.</p>"
					+ "<p><p style=\"font-size: 80%\">Once you have submitted a run, check the \"Run Status\" tab to view the status of your run.</p>"
					+ "</body></html>");
			setInfoHeaderText("Submit by Accession");
			break;
		// visualization page
		case "selectVis":
			setInfoText(
					"<html><body>" + "<p><p> Click on the icon to move to the information or visualization page.</p>"
							+ "</body></html>");
			setInfoHeaderText("Data Visualization");
			break;
		// boxplot page
		case "visTaxGroups":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; All taxonomic groups (determined by \"Filtered Taxonomic Group\" column in SeqAPASS data table)"
					+ " with at least on species aligned to the query species/sequence are presented in the box.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; A taxonomic group can be removed by clicking the X in the blue rectangle that contains the name."
					+ " Removing a group removes is from the x-axis on the boxplot visualization.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that removed taxonomic groups can be brought back. To bring back a removed group, click"
					+ " on the dropdown arrow at the far-right side of the box.  This will open a dropdown menu of all groups with checkboxes by their names."
					+ " Click an unchecked box to add that group to the graph, or click a checked box to remove it.  Selecting the checkbox at the very top of"
					+ " the dropdown list will check all boxes, unless all boxes are already checked in which case it will uncheck all boxes except the query species.</p>"
					+ "</body></html>");
			setInfoHeaderText("Taxonomic Groups");
			break;
		case "visSpecies4Legend":
			setInfoText("<html><body>"
					+ "<p><b>To create an interactive legend on the plot, click the dropdown arrow.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; This dropdown menu contains a list of all species represented in the boxplot.  Select the checkbox next to"
					+ " a species name to add it to the legend on the boxplot.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The search bar at the top of the dropdown menu can be used to find species of interest.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; An interactive legend is automatically created for each species selected.</p>"
					+ "</body></html>");
			setInfoHeaderText("Select Species for Legend");
			break;
		case "visLegendOptions":
			setInfoText("<html><body>" + "<p><b>Use these settings to manipulate the boxplot legend.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; By default, the legend displays the common name of a species. When multiple species share the same common"
					+ " name, all of the species with the chosen common name will be marked with the same icon.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The user can switch to scientific names by selecting the Scientific Name radio button.  This will cause"
					+ " species previously grouped together by common name to be split apart, unless the \"Group by Common Name\" checkbox is selected.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Upon selecting the \"Group by Common Name\", the scientific names represented under the same common name"
					+ " will display the same icon on the graph (Boxplot only).</p>" + "</body></html>");
			setInfoHeaderText("Species Legend Options");
			break;
		case "visOptionalSelections":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Choose to display specialized groups on the boxplot by clicking one of the checkboxes."
					+ " The specialized group will be identified on the graph with a red circle icon.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Threatened Species and Endangered Species icons on the graph can be clicked to open a new tab describing"
					+ " the threatened or endangered species.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that only one specialized group can be displayed on the graph at a time. A legend and a"
					+ " specialized group can be displayed simultaneously.</p>" + "</body></html>");
			setInfoHeaderText("Optional Selections");
			break;
		case "visBoxplot":
			setInfoText("<html><body>" + "<p><b>This boxplot is interactive.</b><br/></p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Hover over a taxonomic group name on the x-axis to view the first three species with the highest percent"
					+ " similarity. Click on the taxonomic group name to remove that group from the x-axis( The group can be added back using the \"Taxonomic Groups:"
					+ " (x-axis labels)\" controls above).</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Upon adding a legend or specialized groups to the boxplot, all icons and text becomes interactive on the"
					+ " plot via hover over and select features.  Clicking on a box in the boxplot will bring up a summary table for species in the selected taxonomic group.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The \"Open Size Controls...\" button allows the user to zoom and pan, as well as change the width of the bars"
					+ " on the plot to make viewing easier.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To download the graph, click the \"Download Boxplot\" button to select the desired image type and size, then"
					+ " click \"Download Image\".</p>" + "</body></html>");
			setInfoHeaderText("Boxplot");
			break;
		case "PushBoxplotToDS":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Pushing the boxplot visualization from Level 1 or Level 2 to Decision Summary Report will give the user the option"
					+ " to include the visualization in the downloaded PDF file.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Only one Level 1 boxplot can be pushed to the DS Report.  Multiple Level 2 visualizations can be pushed to the DS"
					+ " Report but the user must go into each Level 2 visualization and push the visualization to the report.</p>"
					+ "</body></html>");
			setInfoHeaderText("Boxplot");
			break;
		// Heat Map
		case "vizHeatMap":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Heat Map can visually show the results for amino acid comparisons across species from an individual Level 3 run"
					+ " or a combination of Level 3 runs.  It gives the user a simplified visualization of the amino acid comparisons to the template sequence.  This allows for"
					+ " rapid assessment of a susceptibility predictions from Level 3 results.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To view other important information for a specific species, the user may scroll-over the name of the species.  A"
					+ " pop-up window will be generated containing the NCBI accession, protein name, taxonomic group, and the common name or a scientific name (depending on which"
					+ " option is selected by the user).</p>" + "</body></html>");
			setInfoHeaderText("Heat Map");
			break;
		case "heatmapReportOptions":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Heat Map displayed can be either a \"Simple\" report which displays the amino acid(s) and its respective position,"
					+ " or a \"Full\" report which gives added information about each amino acid displayed.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; There is an option to change between the \"Common Name\" or the \"Scientific Name\" which will be displayed on the"
					+ " Heat Map.</p>" + "</body></html>");
			setInfoHeaderText("Report Options");
			break;
		case "heatmapOptionalSelections":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Choose to display specialized groups on the Heat Map by clicking one of the radio buttons.  The specialized group"
					+ " will be identified on the map with a highlighted name.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that only one specialized group can be displayed on the Heat Map at a time.  The hover over will display"
					+ " all specialized groups that pertain to the respective specie.</p>" + "</body></html>");
			setInfoHeaderText("Optional Selections");
			break;
		case "vizHeatMapSettings":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Heat Map Settings allow for a fully customizable Heat Map.  Each button pertains to a specific function on the Heat Map.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Heat Map Settings have similar functions between the Simple Report and the Full Report.</p>"
					+ "</body></html>");
			setInfoHeaderText("Heat Map Settings");
			break;
		// DS report
		case "DSReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Decision Summary Report contains the SeqAPASS results (i.e. data tables and/or visualizations) that the user"
					+ " has selected and pushed over from Level 1, 2 or 3 result pages.  The customized report allows the user to quickly evaluate susceptibility predictions"
					+ " across multiple Levels of the SeqAPASS evaluation for multiple species simultaneously and create a downloadable PDF.</p>"
					+ "</body></html>");
			setInfoHeaderText("Decision Summary Report (DS Report)");
			break;
		case "DSLevel1":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; All Level 1 taxonomic groups, will be present in the selection box.  The displayed taxonomic groups are"
					+ " determined by the \"Filtered Taxonomic Group\" column in the SeqAPASS Level 1 data table.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; A taxonomic group can be selected by clicking the checkbox that is associated with the name.  Selecting"
					+ " a group will activate and select the species of that taxonomic group in the \"Select Species\" box.  The template species is automatically"
					+ " selected and cannot be unselected.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Please note that unselected taxonomic groups can be selected and reintegrated into the report.  To bring back"
					+ " an unselected group, click on the checkbox at the far-left side of the taxonomic group.  Click an unchecked box to activate those species to the"
					+ " \"Select Species\" box or click a checked box to remove them.  Selecting the checkbox at the very top of the dropdown list under the \"Select"
					+ " All\" text will check all the taxonomic groups, unless all are already checked, in which selecting it will uncheck all boxes.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 1 Report");
			break;
		case "DSLevel2":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Level 2 Report section contains all the domain results the user has pushed to the report.  Selecting"
					+ " specific domains, by clicking the \"Add to Final Decision Summary Report\" checkboxes, will populate in the \"Final Decision Summary Report\" table."
					+ "  The user is able to include a visualization that is associated with a domain that has been pushed to the Final Decision Summary Report.</p>"
					+ "</body></html>");
			setInfoHeaderText("Level 2 Report");
			break;
		case "DSLevel3":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The Level 3 Report section contains the important Level 3 information including the selected amino acids"
					+ " for the respective run.  The visualization can be added to the Final Decision Summary Report and individually saved.  Also, the Heat Map can be"
					+ " added to the PDF in a simpler form.</p>" + "</body></html>");
			setInfoHeaderText("Level 3 Report");
			break;
		case "DSOptionalComponents":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; By selecting the box to add in a visualization, the corresponding level visualization will populate in the"
					+ " PDF.  Whether it is the modified or the default visualization, the user must push the visualization from the respective level page for it to be"
					+ " integrated and available on the DS Report.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To add a report to the \"Final Decision Summary Report\" table, select the \"Add To Report Table\".  A"
					+ " Level 1 run must be populated within the DS Report for the table to be present.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Selecting the \"Add Info to Report\" button will populate the DS Report PDF with the respective Level"
					+ " 1, 2, or 3 query information.</p>" + "</body></html>");
			setInfoHeaderText("Optional Components");
			break;
		case "DSFinal":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; The \"Final Decision Summary Report\" table gives the user their customized results across SeqAPASS Levels"
					+ " all summarized in one place.  These data can be downloaded as an individual table or a simplified table which is included in the Final Decision"
					+ " Summary Report PDF.</p>" + "</body></html>");
			setInfoHeaderText("Final Decision Summary Report");
			break;
		case "DownloadDSReport":
			setInfoText("<html><body>"
					+ "<p><p style=\"font-size: 80%\">&#8226; Downloading the information on the Decision Summary Report will present the data within a simplified PDF format.</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; For information to be displayed in the downloaded report, the user must first push all the desired data to the DS"
					+ " Report and also select optional data (e.g. Level 1,2,3 visualizations, Level 2 domains, etc.)</p>"
					+ "<p><p style=\"font-size: 80%\">&#8226; To update the DS Report, a new report must be downloaded.</p>"
					+ "</body></html>");
			setInfoHeaderText("Download DS Report");
			break;
		default:
			setInfoText("");
			break;
		}
	}

	public String exportLink(UIColumn column) {
		String value = "=HYPERLINK(\"";
		String text = "";
		String url = "";
		for (UIComponent child : column.getChildren()) {
			if (child instanceof org.primefaces.component.link.Link) {
				text = ComponentUtils.getValueToRender(FacesContext.getCurrentInstance(), child);
				url = child.getAttributes().get("href").toString();
				value = value + url + "\",\"" + text + "\")";
			}
		}

		return value;
	}

	public void viewLevelSummaryButton(int level) {
		System.out.println("You chose level " + level);
		if (level == 1) {
			levelOneSummaryReport = createSummaryReport(level);
		} else if (level == 2) {
			levelTwoSummaryReport = createSummaryReport(level);
		} else if (level == 3) {
			levelThreeSummaryReport = createSummaryReport(level);
			// for (String key : primaryResidueHeaders){
			// System.out.println("Key=" + key);
			// }
		} else {
			System.out.println("Invalid level in viewLevel1SummaryButton: " + level);
		}
	}

	public List<String> getUniqueLev1TaxGroups(List<LevelOneReportRow> rows) {
		List<String> uniqTaxGroups = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			LevelOneReportRow row = rows.get(i);
			if (!uniqTaxGroups.contains(row.getTaxonomyName())) {
				uniqTaxGroups.add(row.getTaxonomyName());
			}
		}
		return uniqTaxGroups;
	}

	public List<String> getUniqueLev2TaxGroups(List<LevelTwoReportRow> rows) {
		List<String> uniqTaxGroups = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			LevelTwoReportRow row = rows.get(i);
			if (!uniqTaxGroups.contains(row.getTaxonomyName())) {
				uniqTaxGroups.add(row.getTaxonomyName());
			}
		}
		return uniqTaxGroups;
	}

	public List<String> getUniqueLev3TaxGroups(List<LevelThreeReportRow> rows) {
		List<String> uniqTaxGroups = new ArrayList<>();
		for (int i = 0; i < rows.size(); i++) {
			LevelThreeReportRow row = rows.get(i);
			if (!uniqTaxGroups.contains(row.getTaxonomyName())) {
				uniqTaxGroups.add(row.getTaxonomyName());
			}
		}
		return uniqTaxGroups;
	}

	public List<SummaryReportRow> createSummaryReport(int level) {
		List<SummaryReportRow> sumReportList = new ArrayList<>();

		// Get unique tax groups
		List<LevelOneReportRow> lev1Report = new ArrayList<>();
		List<LevelTwoReportRow> lev2Report = new ArrayList<>();
		List<LevelThreeReportRow> lev3Report = new ArrayList<>();
		List<String> uniqTaxGroups = new ArrayList<>();
		if (level == 1) {
			if (levelOneReportType == ReportTypeEnum.Primary) {
				lev1Report = levelOnePrimaryReport;
			} else if (levelOneReportType == ReportTypeEnum.Full) {
				lev1Report = levelOneReport;
			} else {
				return sumReportList;
			}
			uniqTaxGroups = getUniqueLev1TaxGroups(lev1Report);
		} else if (level == 2) {
			if (levelTwoReportType == ReportTypeEnum.Primary) {
				lev2Report = levelTwoPrimaryReport;
			} else if (levelTwoReportType == ReportTypeEnum.Full) {
				lev2Report = levelTwoReport;
			} else {
				return sumReportList;
			}
			uniqTaxGroups = getUniqueLev2TaxGroups(lev2Report);
		} else if (level == 3) {
			lev3Report = levelThreeReport;
			uniqTaxGroups = getUniqueLev3TaxGroups(lev3Report);
		}

		String susceptible = null;

		for (int j = 0; j < uniqTaxGroups.size(); j++) {
			List<Double> percSims = new ArrayList<Double>();
			String taxGroup = "unknown";
			String filteredTaxGroup = "unknown";
			int numY = 0;
			int numN = 0;
			Map<String, String> residues = new LinkedHashMap<String, String>();
			if (level == 1) {
				for (int i = 0; i < lev1Report.size(); i++) {
					LevelOneReportRow row = lev1Report.get(i);
					if (row.getTaxonomyName().equals(uniqTaxGroups.get(j))) {
						taxGroup = row.getDefaultTaxonomyName();
						filteredTaxGroup = row.getTaxonomyName();
						percSims.add(row.getPercentSimilarity());
						if (row.getSusceptible().toUpperCase().equals("Y")) {
							numY++;
						} else if (row.getSusceptible().toUpperCase().equals("N")) {
							numN++;
						}
					}
				}
				// if (numY >= numN) {
				if (numY > 0) {
					susceptible = "Y";
				} else {
					susceptible = "N";
				}
				// susceptible = lev1Report.get(0).getSusceptible();
			} else if (level == 2) {
				for (int i = 0; i < lev2Report.size(); i++) {
					LevelTwoReportRow row = lev2Report.get(i);
					if (row.getTaxonomyName().equals(uniqTaxGroups.get(j))) {
						taxGroup = row.getDefaultTaxonomyName();
						filteredTaxGroup = row.getTaxonomyName();
						percSims.add(row.getPercentSimilarity());
						if (row.getSusceptible().toUpperCase().equals("Y")) {
							numY++;
						} else if (row.getSusceptible().toUpperCase().equals("N")) {
							numN++;
						}
					}
				}
				// if (numY >= numN) {
				if (numY > 0) {
					susceptible = "Y";
				} else {
					susceptible = "N";
				}
				// susceptible = lev2Report.get(0).getSusceptible();
			} else if (level == 3) {

				List<TreeSet<String>> resList = new ArrayList<>();
				int numCols = lev3Report.get(0).getResidueResultList().size();
				summaryResidueHeaders.clear();
				for (int i = 1; i <= numCols; i++) {
					resList.add(new TreeSet<String>());
					summaryResidueHeaders.add("Position " + i + " AminoAcid(s)");
				}
				for (int i = 0; i < lev3Report.size(); i++) {
					LevelThreeReportRow row = lev3Report.get(i);
					if (row.getTaxonomyName().equals(uniqTaxGroups.get(j))) {
						taxGroup = row.getTaxonomyName();
						filteredTaxGroup = null;
						percSims.add(0.0);
						if (row.getSusceptible().toUpperCase().equals("Y")) {
							numY++;
						} else if (row.getSusceptible().toUpperCase().equals("N")) {
							numN++;
						}

						int resNum = 0;
						for (LevelThreeResidueResult resResult : row.getResidueResultList()) {
							AminoAcid aminoAcid = resResult.getAminoAcid();
							if (aminoAcid != null) {
								resList.get(resNum).add(Character.toString(aminoAcid.getId()));
							} else {
								resList.get(resNum).add("-");
							}
							resNum++;
						}

					}
				}
				if (primaryResidueHeaders.size() > 0) {
					if (numY >= numN) {
						susceptible = "Y";
					} else {
						susceptible = "N";
					}
				} else {
					susceptible = "-";
				}
				for (int i = 0; i < resList.size(); i++) {
					String tmpStr = resList.get(i).toString();
					residues.put(summaryResidueHeaders.get(i), tmpStr.substring(1, tmpStr.length() - 1));
				}
			}
			if (level == 3) {
				sumReportList.add(new SummaryReportRow(taxGroup, percSims.size(), susceptible, numY, numN, residues));
			} else {
				// level 1 or 2
				sumReportList.add(new SummaryReportRow(taxGroup, filteredTaxGroup, percSims.size(), getMean(percSims),
						getMedian(percSims), susceptible));
			}
		}

		System.out.println("summaryResHeaders: " + summaryResidueHeaders.toString());

		return sumReportList;
	}

	public double getMean(List<Double> vals) {
		double sum = 0;
		for (Double val : vals) {
			sum += val;
		}
		return sum / vals.size();
	}

	public double getMedian(List<Double> vals) {
		Collections.sort(vals);
		int middle = vals.size() / 2;
		if (vals.size() % 2 == 1) {
			return vals.get(middle);
		} else {
			return (vals.get(middle - 1) + vals.get(middle)) / 2.0;
		}
	}

	public void addRefXplorerProtein() {
		if (!refExplorerAddName.isEmpty()) {
			refExplorerAddName = refExplorerAddName.replace("\"", "");
			if (!proteinXplorerList.contains(refExplorerAddName)) {
				proteinXplorerList.add(refExplorerAddName);
				refExplorerAddName = "";
			}
			refExplorerAddName = "";
		}

	}

	public void removeSelectedRefXplorerProtein() {
		proteinXplorerList.remove(xplorerSelectedProtein);
	}

	public void restoreDefaultRefXplorerProteins() {
		proteinXplorerList.clear();

		// proteinXplorerList.add(queryProtein);

		// Parse query protein into search strings
		if (queryProtein.startsWith("RecName")) {
			// first split at semicolons
			String[] strArray = queryProtein.split(";");
			// then choose string after equals sign
			for (int i = 0; i < strArray.length; i++) {
				String[] tmpStr = strArray[i].split("=");
				if (tmpStr.length > 1) {
					proteinXplorerList.add(tmpStr[1].trim());
				} else {
					proteinXplorerList.add(tmpStr[0].trim());
				}
			}
		} else {
			// split after commas
			String[] strArray = queryProtein.split(",");
			for (int i = 0; i < strArray.length; i++) {
				boolean include = true;
				String tmpStr = strArray[i].trim();

				// do not include any entry that starts with "chain" and is the
				// start of the
				// original query protein
				if (tmpStr.toLowerCase().startsWith("chain") && i == 0) {
					include = false;
				}
				// do not include any entry that is exactly 'partial'
				if (tmpStr.toLowerCase().equals("partial")) {
					include = false;
				}

				// if string contains unequal numbers of open and close
				// parenthesis
				// combine array elements. Advance loop variable to
				// skip next element in array
				// this code combines elements until openParenths =
				// closeParenths
				int openParenths = StringUtils.countMatches(tmpStr, "(");
				int closeParenths = StringUtils.countMatches(tmpStr, ")");

				int appendIndex = i;

				while (openParenths != closeParenths && appendIndex <= strArray.length) {
					appendIndex++;
					tmpStr = tmpStr + strArray[appendIndex];

					openParenths = StringUtils.countMatches(tmpStr, "(");
					closeParenths = StringUtils.countMatches(tmpStr, ")");

				}
				tmpStr = tmpStr.trim();
				i = appendIndex;

				// if (openParenths != closeParenths){
				// int diff = openParenths - closeParenths;
				// for (int j = 0; j<diff; j++){
				// tmpStr = tmpStr + strArray[i+j];
				// }
				//
				// tmpStr = tmpStr.trim();
				// i = i+diff;
				// }

				if (include) {
					proteinXplorerList.add(tmpStr);
				}
			}
		}

	}

	public void loadRefXplorer() {
		restoreDefaultRefXplorerProteins();
	}

	public void xplorerSearchBtn() {
		System.out.println(scholarString);
	}

	// public void showSearchString(){
	//
	// }

	public void generateGoogleScholarLink() {
		String searchTerms = "(\"site-directed mutagenesis\" OR \"molecular docking\" OR \"docking analysis\" OR \"docking simulations\" OR \"x-ray crystallography\" OR \"crystal structure\" OR \"homology modeling\" OR \"protein structure\" OR \"protein binding\" OR \"molecular model\" OR \"binding\" OR \"field resistance\" OR \"amino acid\" OR \"amino acid residues\" OR \"mutation\" OR \"mutations\" OR \"molecular dynamics\" OR \"transcriptional activation\" OR \"3D-pharmacophore\" OR \"pharmacophore\" OR \"structure-based\" OR \"chemo-bioinformatics\" OR \"3D-structures\" OR \"3D-QSAR\")";
		// String searchProteins = "(\"" + String.join("\" OR \"",
		// proteinXplorerList) + "\")" ;
		String searchProteins = "(" + String.join(" OR ", proteinXplorerList) + ")";

		scholarString = "https://scholar.google.com/scholar?hl=en&as_sdt=0%2C34&q=" + searchProteins + "AND"
				+ searchTerms;
		System.out.println(scholarString);
		// return url;
	}

	public void createLev1SettingsFile() {

		String NEW_LINE_SEPARATOR = "\n";
		CSVPrinter csvFilePrinter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		StringBuilder sb = new StringBuilder();

		try {
			csvFilePrinter = new CSVPrinter(sb, csvFileFormat);
			csvFilePrinter.printRecord("Level 1 Report Settings");
			csvFilePrinter.printRecord("");
			csvFilePrinter.printRecord("");
			csvFilePrinter.printRecord("Analysis TimeStamp", convertUnixTimeToDate(levelOneReport.get(0).getEndDate()));
			csvFilePrinter.printRecord("SeqAPASS version", seqapassVersion);
			csvFilePrinter.printRecord("Query Species", querySpecies);
			csvFilePrinter.printRecord("Query Protein", queryProtein);
			csvFilePrinter.printRecord("Query Accession", accession);
			csvFilePrinter.printRecord("Ortholog Count", Integer.toString(ortholog_count));

			String eukaryoteCheck = null;
			if (eukaryotesOnly1) {
				eukaryoteCheck = "Checked";
			} else {
				eukaryoteCheck = "Unchecked";
			}
			String cutoffChoice = null;
			if (chosenLevelOneCutoffOption == 1) {
				cutoffChoice = "Default";
			} else if (chosenLevelOneCutoffOption == 2) {
				cutoffChoice = "2nd Local Minimum";
			} else {
				cutoffChoice = "User Defined";
			}

			csvFilePrinter.printRecord("L1 Cutoff", cutoffChoice);

			if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
				csvFilePrinter.printRecord("L1 Cutoff Value", Double.toString(levelOnePrimaryCutValue));
				csvFilePrinter.printRecord("E-value", Double.toString(primaryLevOneEvalueLimit));
				csvFilePrinter.printRecord("Sorted by Taxonomic Group", levOnePrimaryTaxGroup);
				csvFilePrinter.printRecord("Common Domains", Integer.toString(primaryLevOneCommonDomainLimit));
				String readAcross = null;
				if (levelOneSpeciesReadAcross) {
					readAcross = "Y";
				} else {
					readAcross = "N";
				}
				csvFilePrinter.printRecord("Species Read Across", readAcross);
				csvFilePrinter.printRecord("Show Only Eukaryotes", eukaryoteCheck);
				csvFilePrinter.printRecord("Report", "Primary");
			} else {
				csvFilePrinter.printRecord("L1 Cutoff Value", Double.toString(levelOneFullCutValue));
				csvFilePrinter.printRecord("E-value", Double.toString(defaultLevelOneEvalue));
				csvFilePrinter.printRecord("Sorted by Taxonomic Group", "Class");
				csvFilePrinter.printRecord("Common Domains", Integer.toString(defaultCommonDomains));
				csvFilePrinter.printRecord("Species Read Across", "Yes");
				csvFilePrinter.printRecord("Show Only Eukaryotes", eukaryoteCheck);
				csvFilePrinter.printRecord("Report", "Full");
			}

			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();
			csvFilePrinter.printRecord();

		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());

		String fileName = Integer.toString(runId) + "_" + accession + "_levelOneReportSettings.csv";

		// lev1SettingsFile = new DefaultStreamedContent(in, "application/txt",
		// fileName);

		lev1SettingsFile = DefaultStreamedContent.builder().contentType("application/txt").name(fileName)
				.stream(() -> in).build();

	}

	public void createLev2SettingsFile() {

		String NEW_LINE_SEPARATOR = "\n";
		CSVPrinter csvFilePrinter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		StringBuilder sb = new StringBuilder();

		try {
			csvFilePrinter = new CSVPrinter(sb, csvFileFormat);
			csvFilePrinter.printRecord("Level 2 Report Settings");
			csvFilePrinter.printRecord("");
			csvFilePrinter.printRecord("");

			csvFilePrinter.printRecord("Analysis TimeStamp", convertUnixTimeToDate(levelOneReport.get(0).getEndDate()));
			csvFilePrinter.printRecord("SeqAPASS version", seqapassVersion);
			csvFilePrinter.printRecord("Query Species", querySpecies);
			csvFilePrinter.printRecord("Query Protein", queryProtein);
			csvFilePrinter.printRecord("Query Domain", loadedCompletedDomain.getDisplayText());
			csvFilePrinter.printRecord("Query Accession", accession);
			csvFilePrinter.printRecord("Ortholog Count", Integer.toString(ortholog_count));

			String eukaryoteCheck = null;
			if (eukaryotesOnly2) {
				eukaryoteCheck = "Checked";
			} else {
				eukaryoteCheck = "Unchecked";
			}

			String cutoffChoice = null;
			if (chosenLevelTwoCutoffOption == 1) {
				cutoffChoice = "Default";
			} else if (chosenLevelTwoCutoffOption == 2) {
				cutoffChoice = "2nd Local Minimum";
			} else {
				cutoffChoice = "User Defined";
			}

			csvFilePrinter.printRecord("L2 Cutoff", cutoffChoice);

			if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
				csvFilePrinter.printRecord("L2 Cutoff Value", Double.toString(levelTwoPrimaryCutValue));
				csvFilePrinter.printRecord("E-value", Double.toString(primaryLevTwoEvalueLimit));
				csvFilePrinter.printRecord("Sorted by Taxonomic Group", levTwoPrimaryTaxGroup);
				String readAcross = null;
				if (levelTwoSpeciesReadAcross) {
					readAcross = "Y";
				} else {
					readAcross = "N";
				}
				csvFilePrinter.printRecord("Species Read Across", readAcross);
				csvFilePrinter.printRecord("Show Only Eukaryotes", eukaryoteCheck);
				csvFilePrinter.printRecord("Report", "Primary");
			} else {
				csvFilePrinter.printRecord("L2 Cutoff Value", Double.toString(levelTwoFullCutValue));
				csvFilePrinter.printRecord("E-value", Double.toString(defaultLevelTwoEvalue));
				csvFilePrinter.printRecord("Sorted by Taxonomic Group", "Class");
				csvFilePrinter.printRecord("Species Read Across", "Yes");
				csvFilePrinter.printRecord("Show Only Eukaryotes", eukaryoteCheck);
				csvFilePrinter.printRecord("Report", "Full");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());

		// lev2SettingsFile = new DefaultStreamedContent(in, "application/txt",
		// "levelTwoReportSettings.csv");

		lev2SettingsFile = DefaultStreamedContent.builder().contentType("application/txt")
				.name("levelTwoReportSettings.csv").stream(() -> in).build();

	}

	public void createLev3SettingsFile() {

		String NEW_LINE_SEPARATOR = "\n";
		CSVPrinter csvFilePrinter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		StringBuilder sb = new StringBuilder();

		try {
			csvFilePrinter = new CSVPrinter(sb, csvFileFormat);
			csvFilePrinter.printRecord("Level 3 Report Settings");
			csvFilePrinter.printRecord("");
			csvFilePrinter.printRecord("");

			csvFilePrinter.printRecord("Analysis TimeStamp", convertUnixTimeToDate(levelOneReport.get(0).getEndDate()));
			csvFilePrinter.printRecord("SeqAPASS version", seqapassVersion);
			csvFilePrinter.printRecord("Level 3 Run Name", levelThreeRunName);
			csvFilePrinter.printRecord("Template Species", templateSpecies);
			csvFilePrinter.printRecord("Template Protein", templateProtein);
			csvFilePrinter.printRecord("Query Residues", queryResiduesText);

			csvFilePrinter.printRecord("Query Accession", accession);
			// csvFilePrinter.printRecord("Ortholog Count",
			// Integer.toString(ortholog_count));

		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());

		// lev3SettingsFile = new DefaultStreamedContent(in, "application/txt",
		// "levelThreeReportSettings.csv");
		lev3SettingsFile = DefaultStreamedContent.builder().contentType("application/txt")
				.name("levelThreeReportSettings.csv").stream(() -> in).build();

	}

	public List<LevelOneReportRow> deepCopyLevel1Report(List<LevelOneReportRow> orig) {
		List<LevelOneReportRow> newCopy = new ArrayList<LevelOneReportRow>();
		for (int i = 0; i < orig.size(); i++) {
			newCopy.add(LevelOneReportRow.newInstance(orig.get(i)));
		}
		return newCopy;
	}

	public List<LevelTwoReportRow> deepCopyLevel2Report(List<LevelTwoReportRow> orig) {
		List<LevelTwoReportRow> newCopy = new ArrayList<LevelTwoReportRow>();
		for (int i = 0; i < orig.size(); i++) {
			newCopy.add(LevelTwoReportRow.newInstance(orig.get(i)));
		}
		return newCopy;
	}

	public List<LevelThreeReportRow> deepCopyLevel3Report(List<LevelThreeReportRow> orig) {
		List<LevelThreeReportRow> newCopy = new ArrayList<LevelThreeReportRow>();
		for (int i = 0; i < orig.size(); i++) {
			newCopy.add(LevelThreeReportRow.newInstance(orig.get(i)));
		}
		return newCopy;
	}

	public void enableDSReport() {
		disableRAReport = false;
	}

	// public void pushLevelOneInfoToRA() {
	//
	// ELContext elContext = FacesContext.getCurrentInstance().getELContext();
	// RiskAssessorView raReportView = (RiskAssessorView)
	// FacesContext.getCurrentInstance().getApplication()
	// .getELResolver().getValue(elContext, null, "riskAssessorView");
	//
	// if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
	// raReportView.setLevOneEvalue(primaryLevOneEvalueLimit);
	// raReportView.setLevOneTaxGroup(levOnePrimaryTaxGroup);
	// raReportView.setLevOneCommonDomainLimit(primaryLevOneCommonDomainLimit);
	// raReportView.setLevOneSpeciesReadAcross(levelOneSpeciesReadAcross);
	// raReportView.setLevOneCutoff(levelOnePrimaryCutValue);
	// } else {
	// raReportView.setLevOneEvalue(defaultLevelOneEvalue);
	// raReportView.setLevOneTaxGroup("Class");
	// raReportView.setLevOneCommonDomainLimit(1);
	// raReportView.setLevOneSpeciesReadAcross(true);
	// raReportView.setLevOneCutoff(levelOneFullCutValue);
	// }
	// raReportView.setLevOneReportType(levelOneReportType);
	// raReportView.setLevOneOrthologCount(ortholog_count);
	// raReportView.setLevOneEukaryotesOnly(eukaryotesOnly1);
	//
	// }
	//
	// public void pushLevelTwoInfoToRA() {
	//
	// }

	public void pushToRA(String toPush) {
		System.out.println("Inside pushToRA");
		System.out.println("Pushing " + toPush);
		disableRAReport = false;
		StreamedContent boxplotStream = null;
		StreamedContent heatmapStream = null;

		ReportSettings reportSettings;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "riskAssessorView");
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "visualizationView");

		RiskAssessorReport raReport = raReportView.getRaReport();
		switch (toPush) {
		case "level1":

			reportSettings = raReportView.getLevOneReportSettings();
			if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
				raReportView.getRaReport().setLevelOneReport(deepCopyLevel1Report(levelOnePrimaryReport));
				reportSettings.setEvalueLimit(primaryLevOneEvalueLimit);
				reportSettings.setTaxGroup(levOnePrimaryTaxGroup);
				reportSettings.setCommonDomainLimit(primaryLevOneCommonDomainLimit);
				reportSettings.setSpeciesReadAcross(levelOneSpeciesReadAcross);
				reportSettings.setCutValue(levelOnePrimaryCutValue);
				// raReportView.setLevOneEvalue(primaryLevOneEvalueLimit);
				// raReportView.setLevOneTaxGroup(levOnePrimaryTaxGroup);
				// raReportView.setLevOneCommonDomainLimit(primaryLevOneCommonDomainLimit);
				// raReportView.setLevOneSpeciesReadAcross(levelOneSpeciesReadAcross);
				// raReportView.setLevOneCutoff(levelOnePrimaryCutValue);
			} else {
				raReportView.getRaReport().setLevelOneReport(deepCopyLevel1Report(levelOneReport));
				reportSettings.setEvalueLimit(defaultLevelOneEvalue);
				reportSettings.setTaxGroup("Class");
				reportSettings.setCommonDomainLimit(1);
				reportSettings.setSpeciesReadAcross(true);
				reportSettings.setCutValue(levelOneFullCutValue);
				// raReportView.setLevOneEvalue(defaultLevelOneEvalue);
				// raReportView.setLevOneTaxGroup("Class");
				// raReportView.setLevOneCommonDomainLimit(1);
				// raReportView.setLevOneSpeciesReadAcross(true);
				// raReportView.setLevOneCutoff(levelOneFullCutValue);
			}
			reportSettings.setReportType(levelOneReportType);
			reportSettings.setOrthologCount(ortholog_count);
			reportSettings.setEukaryotesOnly(eukaryotesOnly1);
			// raReportView.setLevOneReportType(levelOneReportType);
			// raReportView.setLevOneOrthologCount(ortholog_count);
			// raReportView.setLevOneEukaryotesOnly(eukaryotesOnly1);
			raReportView.setLevOneReportSettings(reportSettings);

			raReportView.loadTaxAndSpeciesGroups();

			level1RAReportDiffers = false;
			level1RAPushWarning = false;

			raReportView.setDownloadLev1Info(true);

			break;

		case "level2":
			System.out.println("pushing level 2 report to RA!!");
			// test();

			RiskAssessorLevel2Group grp = new RiskAssessorLevel2Group();
			grp.setInfo(LevelTwoRequestableRow.newInstance(loadedCompletedDomain));
			grp.setBoxPlot(null);

			reportSettings = grp.getReportSettings();
			if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
				grp.setReport(deepCopyLevel2Report(levelTwoPrimaryReport));
				reportSettings.setEvalueLimit(primaryLevTwoEvalueLimit);
				reportSettings.setTaxGroup(levTwoPrimaryTaxGroup);
				reportSettings.setSpeciesReadAcross(levelTwoSpeciesReadAcross);
				reportSettings.setCutValue(levelTwoPrimaryCutValue);
			} else {
				grp.setReport(deepCopyLevel2Report(levelTwoReport));
				reportSettings.setEvalueLimit(defaultLevelTwoEvalue);
				reportSettings.setTaxGroup("Class");
				reportSettings.setSpeciesReadAcross(true);
				reportSettings.setCutValue(levelTwoFullCutValue);
			}
			reportSettings.setReportType(levelTwoReportType);
			reportSettings.setOrthologCount(level2_ortholog_count);
			reportSettings.setEukaryotesOnly(eukaryotesOnly2);

			// remove if already in list then add group
			// but save visualization if already there
			RiskAssessorLevel2Group removeGrp = null;
			// byte[] existingBoxPlot = null;
			for (RiskAssessorLevel2Group existingGrp : raReportView.getLevel2Groups()) {
				boolean foundMatch = true;
				foundMatch = existingGrp.getInfo().getRunId() == grp.getInfo().getRunId();
				foundMatch = foundMatch && existingGrp.getInfo().getDomainNumber() == grp.getInfo().getDomainNumber();
				foundMatch = foundMatch && existingGrp.getInfo().getStartPosition() == grp.getInfo().getStartPosition();
				if (foundMatch)
					removeGrp = existingGrp;
			}
			if (removeGrp != null) {
				grp.setBoxPlot(removeGrp.getBoxPlot()); // save existing boxplot
				raReportView.getLevel2Groups().remove(removeGrp);
			}
			raReportView.getLevel2Groups().add(grp);

			level2RAReportDiffers = false;
			level2RAPushWarning = false;

			break;

		case "level3":

			System.out.println("pushing level 3 report to RA!!");
			System.out.println("with residues: " + chosenQueryResidues.toString());

			raReport.setLevelThreeTemplateProtein(templateProtein);
			raReport.setLevelThreeTemplateSpecies(templateSpecies);
			raReport.setLevelThreeRunName(levelThreeRunName);

			raReportView.getRaReport().setLevelThreeReport(deepCopyLevel3Report(levelThreeReport));
			raReportView.setChosenQueryResidues(new ArrayList<String>(chosenQueryResidues));

			if (raReportView.getRaReport().getLevelOneReport() != null
					&& raReportView.getRaReport().getLevelOneReport().size() > 0) {
				raReportView.createRAReport();
			}

			level3RAReportDiffers = false;
			level3RAPushWarning = false;

			raReportView.setDownloadLev3report(true);
			raReportView.setDownloadLev3Info(true);

			break;

		case "boxplot1":
			System.out.println("pushing level 1 boxplot to RA!!");
			boxplotStream = vizView.downloadPNG(1);
			try {
				// PrimeFaces.current().executeScript("getSVGSource('svg1');");
				// takes streamed content, converts to byte array, and sets to
				// raReport property
				// raReportView.getRaReport().setLev1Boxplot(IOUtils.toByteArray(boxplotStream.getStream()));
				raReportView.getRaReport().setLev1Boxplot(IOUtils.toByteArray(boxplotStream.getStream().get()));

				BoxPlotSettings boxPlotSettings = raReportView.getRaReport().getLev1BoxPlotSettings();
				boxPlotSettings.setSelectedTaxGroups(new ArrayList<String>());
				boxPlotSettings.getSelectedTaxGroups().addAll(vizView.getSelectedBoxPlotTaxGroups());
				boxPlotSettings.setSelectedSpecies(new ArrayList<String>());
				boxPlotSettings.getSelectedSpecies().addAll(vizView.getSelectedBoxPlotSpecies());
				boxPlotSettings.setSpeciesOption(vizView.getBoxPlotSpeciesOption1());
				boxPlotSettings.setGroupByCommonName(vizView.isBoxPlotKeepCommonSymbols1());
				boxPlotSettings.setOrtholog(vizView.isLevelOneBoxPlotOrtholog());
				boxPlotSettings.setThreatened(vizView.isLevelOneBoxPlotThreatened());
				boxPlotSettings.setEndangered(vizView.isLevelOneBoxPlotEndangered());
				boxPlotSettings.setModelOrganisms(vizView.isLevelOneBoxPlotModelOrganisms());
				// boxPlotSettings.setBoxPlotReportType(levelOneReportType);
				reportSettings = boxPlotSettings.getReportSettings();
				if (levelOneReportType.equals(ReportTypeEnum.Primary)) {

					reportSettings.setEvalueLimit(primaryLevOneEvalueLimit);
					reportSettings.setTaxGroup(levOnePrimaryTaxGroup);
					reportSettings.setCommonDomainLimit(primaryLevOneCommonDomainLimit);
					reportSettings.setSpeciesReadAcross(levelOneSpeciesReadAcross);
					reportSettings.setCutValue(levelOnePrimaryCutValue);
					// raReportView.setLevOneEvalue(primaryLevOneEvalueLimit);
					// raReportView.setLevOneTaxGroup(levOnePrimaryTaxGroup);
					// raReportView.setLevOneCommonDomainLimit(primaryLevOneCommonDomainLimit);
					// raReportView.setLevOneSpeciesReadAcross(levelOneSpeciesReadAcross);
					// raReportView.setLevOneCutoff(levelOnePrimaryCutValue);
				} else {
					reportSettings.setEvalueLimit(defaultLevelOneEvalue);
					reportSettings.setTaxGroup("Class");
					reportSettings.setCommonDomainLimit(1);
					reportSettings.setSpeciesReadAcross(true);
					reportSettings.setCutValue(levelOneFullCutValue);
					// raReportView.setLevOneEvalue(defaultLevelOneEvalue);
					// raReportView.setLevOneTaxGroup("Class");
					// raReportView.setLevOneCommonDomainLimit(1);
					// raReportView.setLevOneSpeciesReadAcross(true);
					// raReportView.setLevOneCutoff(levelOneFullCutValue);
				}
				reportSettings.setReportType(levelOneReportType);
				reportSettings.setOrthologCount(ortholog_count);
				reportSettings.setEukaryotesOnly(eukaryotesOnly1);
				updateLevOneBox = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error pushing level 1 boxplot to RA");
			}

			// if (raReportView.getRaReport().getLevelOneReport().size()==0){
			// pushLevelOneInfoToRA();
			// }

			vizView.setLevel1SVGPushWarning(false);
			vizView.setLevel1SVGDiffers(false);
			break;

		case "boxplot2":
			System.out.println("pushing level 2 boxplot to RA!!");

			grp = new RiskAssessorLevel2Group();
			grp.setInfo(LevelTwoRequestableRow.newInstance(loadedCompletedDomain));
			grp.setBoxPlot(null);

			boolean prevL2Grp = false;

			// first check to see if L2 group already exists
			// by comparing runid, domain number and start position
			for (RiskAssessorLevel2Group existingGrp : raReportView.getLevel2Groups()) {
				boolean foundMatch = true;
				foundMatch = existingGrp.getInfo().getRunId() == grp.getInfo().getRunId();
				foundMatch = foundMatch && existingGrp.getInfo().getDomainNumber() == grp.getInfo().getDomainNumber();
				foundMatch = foundMatch && existingGrp.getInfo().getStartPosition() == grp.getInfo().getStartPosition();
				if (foundMatch) {
					grp = existingGrp;
					prevL2Grp = true;
				}
			}

			// if not existing group add to L2
			if (!prevL2Grp) {
				raReportView.getLevel2Groups().add(grp);
			}

			// load jpg from visualization
			boxplotStream = vizView.downloadPNG(2);
			try {
				// grp.setBoxPlot(IOUtils.toByteArray(boxplotStream.getStream()));
				grp.setBoxPlot(IOUtils.toByteArray(boxplotStream.getStream().get()));

				BoxPlotSettings boxPlotSettings = grp.getBoxPlotSettings();
				boxPlotSettings.setSelectedTaxGroups(new ArrayList<String>());
				boxPlotSettings.getSelectedTaxGroups().addAll(vizView.getSelectedBoxPlotTaxGroups2());
				boxPlotSettings.setSelectedSpecies(new ArrayList<String>());
				boxPlotSettings.getSelectedSpecies().addAll(vizView.getSelectedBoxPlotSpecies2());
				boxPlotSettings.setSpeciesOption(vizView.getBoxPlotSpeciesOption2());
				boxPlotSettings.setGroupByCommonName(vizView.isBoxPlotKeepCommonSymbols2());
				boxPlotSettings.setOrtholog(vizView.isLevelTwoBoxPlotOrtholog());
				boxPlotSettings.setThreatened(vizView.isLevelTwoBoxPlotThreatened());
				boxPlotSettings.setEndangered(vizView.isLevelTwoBoxPlotEndangered());
				boxPlotSettings.setModelOrganisms(vizView.isLevelTwoBoxPlotModelOrganisms());
				// boxPlotSettings.setBoxPlotReportType(levelTwoReportType);
				reportSettings = boxPlotSettings.getReportSettings();
				if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
					reportSettings.setEvalueLimit(primaryLevTwoEvalueLimit);
					reportSettings.setTaxGroup(levTwoPrimaryTaxGroup);
					reportSettings.setSpeciesReadAcross(levelTwoSpeciesReadAcross);
					reportSettings.setCutValue(levelTwoPrimaryCutValue);
				} else {
					reportSettings.setEvalueLimit(defaultLevelTwoEvalue);
					reportSettings.setTaxGroup("Class");
					reportSettings.setSpeciesReadAcross(true);
					reportSettings.setCutValue(levelTwoFullCutValue);
				}
				reportSettings.setCommonDomainLimit(-1);
				reportSettings.setReportType(levelTwoReportType);
				reportSettings.setOrthologCount(level2_ortholog_count);
				reportSettings.setEukaryotesOnly(eukaryotesOnly2);
				updateLevTwoBox = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error pushing level 2 boxplot to RA");
			}

			vizView.setLevel2SVGPushWarning(false);
			vizView.setLevel2SVGDiffers(false);
			break;

		case "heatmap3":
			// heatmap png is pushed via constructHeatmapImageForDS in
			// heatmap.js
			// this just pushed heatmap settings

			HeatMapSettings heatMapSettings = raReportView.getRaReport().getLev3HeatMapSettings();
			heatMapSettings.setSelectedTaxGroups(vizView.getLevelThreeHeatmapPickList().getTarget());
			heatMapSettings.setReportType(vizView.getHmReportType());
			System.out.println(vizView.getHmReportType());
			System.out.println(heatMapSettings.getReportType());
			heatMapSettings.setSpeciesOption(vizView.getHeatmapSpeciesNameType());
			heatMapSettings.setOptionalSelection(vizView.getLevel3OptionalSelection());
			heatMapSettings.setSPHeatmap(vizView.isLevelThreeHeatmapSusceptibility());
			heatMapSettings.setSPText(vizView.isLevelThreeHeatmapSusceptibilityText());
			heatMapSettings.setAPHeatmap(vizView.isLevelThreeHeatmapAlignPrediction());
			heatMapSettings.setAA(vizView.isLevelThreeHeatmapAminoAcid());
			heatMapSettings.setAAPos(vizView.isLevelThreeHeatMapPosition());
			List<String> resPosList = new ArrayList<String>();
			for (String q : chosenQueryResidues) {
				resPosList.add(q);
			}
			heatMapSettings.setSelectedPositions(resPosList);

			vizView.setLevel3SVGPushWarning(false);
			vizView.setLevel3SVGDiffers(false);
			break;

		default:
			break;
		}

		System.out.println("disableRAReport = " + disableRAReport);

	}

	public boolean compareLevel1ReportSettings(ReportSettings newReportSettings) {
		boolean noChanges = true;

		noChanges = noChanges && (levelOneReportType == newReportSettings.getReportType());
		noChanges = noChanges && (eukaryotesOnly1 == newReportSettings.isEukaryotesOnly());
		// the following can only change for primary report
		if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
			noChanges = noChanges && (primaryLevOneEvalueLimit == newReportSettings.getEvalueLimit());
			noChanges = noChanges
					&& (levOnePrimaryTaxGroup.toLowerCase().equals(newReportSettings.getTaxGroup().toLowerCase()));
			noChanges = noChanges && (primaryLevOneCommonDomainLimit == newReportSettings.getCommonDomainLimit());
			noChanges = noChanges && (levelOneSpeciesReadAcross == newReportSettings.isSpeciesReadAcross());
			noChanges = noChanges && (ortholog_count == newReportSettings.getOrthologCount());
			noChanges = noChanges && (levelOnePrimaryCutValue == newReportSettings.getCutValue());
		} else {
			noChanges = noChanges && (levelOneFullCutValue == newReportSettings.getCutValue());
		}

		return noChanges;

	}

	public boolean compareLevel2ReportSettings(ReportSettings newReportSettings) {
		boolean noChanges = true;

		noChanges = noChanges && (levelTwoReportType == newReportSettings.getReportType());
		noChanges = noChanges && (eukaryotesOnly2 == newReportSettings.isEukaryotesOnly());
		// the following can only change for primary report
		if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
			noChanges = noChanges && (primaryLevTwoEvalueLimit == newReportSettings.getEvalueLimit());
			noChanges = noChanges
					&& (levTwoPrimaryTaxGroup.toLowerCase().equals(newReportSettings.getTaxGroup().toLowerCase()));
			// noChanges = noChanges && (primaryLevTwoCommonDomainLimit ==
			// newReportSettings.getCommonDomainLimit());
			noChanges = noChanges && (levelTwoSpeciesReadAcross == newReportSettings.isSpeciesReadAcross());
			noChanges = noChanges && (level2_ortholog_count == newReportSettings.getOrthologCount());
			noChanges = noChanges && (levelTwoPrimaryCutValue == newReportSettings.getCutValue());
		} else {
			noChanges = noChanges && (levelTwoFullCutValue == newReportSettings.getCutValue());
		}

		return noChanges;

	}

	// compares current level 1 report to pushed RA level 1 report
	public void checkDiffLevel1RAReport() {
		boolean noChanges = true;

		List<LevelOneReportRow> currentLev1Report;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "riskAssessorView");
		List<LevelOneReportRow> lev1RAReport = raReportView.getRaReport().getLevelOneReport();

		// check if no level 1 has been pushed yet
		if (lev1RAReport.size() == 0) {
			level1RAPushWarning = false;
			level1RAReportDiffers = true;
			return;
		}

		if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
			currentLev1Report = levelOnePrimaryReport;
		} else {
			currentLev1Report = levelOneReport;
		}

		//// ReportSettings dsCurrentSettings =
		//// raReportView.getLevOneReportSettings();
		// // noChanges = noChanges && checkNoChangesLevelOneInfo();
		//
		// // compare other report settings that could differ but still leave
		// // report the same
		// noChanges = noChanges && (levelOneReportType ==
		//// dsCurrentSettings.getReportType());
		// noChanges = noChanges && (eukaryotesOnly1 ==
		//// dsCurrentSettings.isEukaryotesOnly());
		// // the following can only change for primary report
		// if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
		// noChanges = noChanges && (primaryLevOneEvalueLimit ==
		//// dsCurrentSettings.getEvalueLimit());
		// noChanges = noChanges
		// &&
		//// (levOnePrimaryTaxGroup.toLowerCase().equals(dsCurrentSettings.getTaxGroup().toLowerCase()));
		// noChanges = noChanges && (primaryLevOneCommonDomainLimit ==
		//// dsCurrentSettings.getCommonDomainLimit());
		// noChanges = noChanges && (levelOneSpeciesReadAcross ==
		//// dsCurrentSettings.isSpeciesReadAcross());
		// noChanges = noChanges && (ortholog_count ==
		//// dsCurrentSettings.getOrthologCount());
		// noChanges = noChanges && (levelOnePrimaryCutValue ==
		//// dsCurrentSettings.getCutValue());
		// } else {
		// noChanges = noChanges && (levelOneFullCutValue ==
		//// dsCurrentSettings.getCutValue());
		// }

		noChanges = compareLevel1ReportSettings(raReportView.getLevOneReportSettings());

		if (!noChanges) {
			level1RAPushWarning = true;
			level1RAReportDiffers = true;
			return;
		}

		// now use fullEquals for each row to compare report
		if (lev1RAReport.size() != currentLev1Report.size()) {
			level1RAPushWarning = true;
			level1RAReportDiffers = true;
			return;
		}

		for (int i = 0; i < lev1RAReport.size(); i++) {
			noChanges = noChanges && lev1RAReport.get(i).fullEquals(currentLev1Report.get(i));
			if (!noChanges) {
				level1RAPushWarning = true;
				level1RAReportDiffers = true;
				return;
			}
		}

		level1RAPushWarning = false;
		level1RAReportDiffers = false;
		return;
	}

	// returns true if no changes to level one report info
	// public boolean checkNoChangesLevelOneInfo() {
	//
	// boolean noChanges = true;
	//
	// ELContext elContext = FacesContext.getCurrentInstance().getELContext();
	// RiskAssessorView raReportView = (RiskAssessorView)
	// FacesContext.getCurrentInstance().getApplication()
	// .getELResolver().getValue(elContext, null, "riskAssessorView");
	//
	// // compare other report settings that could differ but still leave
	// // report the same
	// noChanges = noChanges && (levelOneReportType ==
	// raReportView.getLevOneReportType());
	// noChanges = noChanges && (eukaryotesOnly1 ==
	// raReportView.isLevOneEukaryotesOnly());
	// // the following can only change for primary report
	// if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
	// noChanges = noChanges && (primaryLevOneEvalueLimit ==
	// raReportView.getLevOneEvalue());
	// noChanges = noChanges
	// &&
	// (levOnePrimaryTaxGroup.toLowerCase().equals(raReportView.getLevOneTaxGroup().toLowerCase()));
	// noChanges = noChanges && (primaryLevOneCommonDomainLimit ==
	// raReportView.getLevOneCommonDomainLimit());
	// noChanges = noChanges && (levelOneSpeciesReadAcross ==
	// raReportView.isLevOneSpeciesReadAcross());
	// noChanges = noChanges && (ortholog_count ==
	// raReportView.getLevOneOrthologCount());
	// noChanges = noChanges && (levelOnePrimaryCutValue ==
	// raReportView.getLevOneCutoff());
	// } else {
	// noChanges = noChanges && (levelOneFullCutValue ==
	// raReportView.getLevOneCutoff());
	// }
	//
	// return noChanges;
	// }

	// compares current level 2 report to pushed RA level 2 report(s)
	public void checkDiffLevel2RAReport() {
		// test();

		boolean noChanges = true;

		List<LevelTwoReportRow> currentLev2Report;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "riskAssessorView");

		if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
			currentLev2Report = levelTwoPrimaryReport;
		} else {
			currentLev2Report = levelTwoReport;
		}

		// first find corresponding level 2 report in levelTwoReportColl
		List<RiskAssessorLevel2Group> lev2RAReportGroups = raReportView.getLevel2Groups();
		RiskAssessorLevel2Group lev2Grp = null;
		for (RiskAssessorLevel2Group grp : lev2RAReportGroups) {
			boolean foundMatch = true;
			foundMatch = grp.getInfo().getRunId() == loadedCompletedDomain.getRunId();
			foundMatch = foundMatch && grp.getInfo().getDomainNumber() == loadedCompletedDomain.getDomainNumber();
			foundMatch = foundMatch && grp.getInfo().getStartPosition() == loadedCompletedDomain.getStartPosition();
			if (foundMatch)
				lev2Grp = grp;
		}
		if (lev2Grp != null) {

			// check if no level 1 has been pushed yet
			if (lev2Grp.getReport().size() == 0) {
				level2RAPushWarning = false;
				level2RAReportDiffers = true;
				return;
			}

			noChanges = compareLevel2ReportSettings(lev2Grp.getReportSettings());

			if (!noChanges) {
				level2RAPushWarning = true;
				level2RAReportDiffers = true;
				return;
			}

			List<LevelTwoReportRow> lev2Report = lev2Grp.getReport();
			if (lev2Grp.getReport().size() != currentLev2Report.size()) {
				level2RAPushWarning = true;
				level2RAReportDiffers = true;
				return;
			}

			// now use fullEquals for each row to compare report
			for (int i = 0; i < lev2Report.size(); i++) {
				noChanges = noChanges && lev2Report.get(i).fullEquals(currentLev2Report.get(i));
				if (!noChanges) {
					level2RAPushWarning = true;
					level2RAReportDiffers = true;
					return;
				}
			}

		} else {
			// report has not been loaded before
			noChanges = false;
			level2RAPushWarning = false;
			level2RAReportDiffers = true;
			return;
		}

		level2RAPushWarning = false;
		level2RAReportDiffers = false;
	}

	// compares current level 1 report to pushed RA level 1 report
	public void checkDiffLevel3RAReport() {
		boolean noChanges = true;

		List<LevelThreeReportRow> currentLev3Report;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		RiskAssessorView raReportView = (RiskAssessorView) FacesContext.getCurrentInstance().getApplication()
				.getELResolver().getValue(elContext, null, "riskAssessorView");
		List<LevelThreeReportRow> lev3RAReport = raReportView.getRaReport().getLevelThreeReport();

		// check if no level 1 has been pushed yet
		if (lev3RAReport.size() == 0) {
			level3RAPushWarning = false;
			level3RAReportDiffers = true;
			return;
		}

		currentLev3Report = levelThreeReport;

		// compare other report settings that could differ but still leave
		// report the same
		// the following can only change for primary report
		// if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
		// noChanges = noChanges && (primaryLevOneEvalueLimit ==
		// raReportView.getLevOneEvalue());
		// noChanges = noChanges &&
		// (levOnePrimaryTaxGroup.toLowerCase().equals(raReportView.getLevOneTaxGroup().toLowerCase()));
		// noChanges = noChanges && (primaryLevOneCommonDomainLimit ==
		// raReportView.getLevOneCommonDomainLimit());
		// noChanges = noChanges && (levelOneSpeciesReadAcross ==
		// raReportView.isLevOneSpeciesReadAcross());
		// noChanges = noChanges && (ortholog_count ==
		// raReportView.getLevOneOrthologCount());
		// noChanges = noChanges && (levelOnePrimaryCutValue ==
		// raReportView.getLevOneCutoff());
		// } else {
		// noChanges = noChanges && (levelOneFullCutValue ==
		// raReportView.getLevOneCutoff());
		// }
		noChanges = noChanges && (chosenQueryResidues.equals(raReportView.getChosenQueryResidues()));
		noChanges = noChanges && (templateProtein.equals(raReportView.getRaReport().getLevelThreeTemplateProtein()));
		noChanges = noChanges && (templateSpecies.equals(raReportView.getRaReport().getLevelThreeTemplateSpecies()));
		noChanges = noChanges && (levelThreeRunName.equals(raReportView.getRaReport().getLevelThreeRunName()));

		if (!noChanges) {
			level3RAPushWarning = true;
			level3RAReportDiffers = true;
			return;
		}

		// now use fullEquals for each row to compare report
		if (lev3RAReport.size() != currentLev3Report.size()) {
			level3RAPushWarning = true;
			level3RAReportDiffers = true;
			return;
		}

		for (int i = 0; i < lev3RAReport.size(); i++) {
			noChanges = noChanges && lev3RAReport.get(i).equals(currentLev3Report.get(i));
			if (!noChanges) {
				level3RAPushWarning = true;
				level3RAReportDiffers = true;
				return;
			}
		}

		level3RAPushWarning = false;
		level3RAReportDiffers = false;
		return;
	}

	// ECOTOX methods
	public void ecotoxBtn() {
		System.out.println("you pressed the ECOTOX button.");

		showEcotoxSpeciesPanel = true;

		List<LevelOneReportRow> report;
		if (levelOneReportType == ReportTypeEnum.Primary) {
			report = levelOnePrimaryReport;
			ecotoxSortTaxGroup = levelOneReportTaxGrouping.getRequestedRank().toUpperCase();
		} else {
			report = levelOneReport;
			ecotoxSortTaxGroup = "CLASS";
		}

		// construct speciesColl
		ecotoxSpeciesColl.clear();
		ecotoxSelectedSpeciesColl.clear();
		ecotoxTaxGroups.clear();
		for (LevelOneReportRow row : report) {

			RiskAssessorTaxGroup ecoTaxGrp = new RiskAssessorTaxGroup(row.getTaxonomyName(), row.getCommonName(),
					row.getScientificName(), row.getSpeciesTaxId(), row.isEcotox());
			// check if taxGroup already exists (either enabled or disabled)
			int index = ecotoxTaxGroups.indexOf(new RiskAssessorTaxGroup(row.getTaxonomyName(), "", "", -9999, false));
			if (index == -1) {
				index = ecotoxTaxGroups.indexOf(new RiskAssessorTaxGroup(row.getTaxonomyName(), "", "", -9999, true));
			}
			if (index != -1) {
				// if taxGroup does exist and it is Ecotox, then enable
				if (row.isEcotox()) {
					ecotoxTaxGroups.get(index).setDisabled(false);
				}
			} else {
				// if taxGroup does not exist then add (and set disabled if not ecotox)
				ecotoxTaxGroups.add(new RiskAssessorTaxGroup(row.getTaxonomyName(), "", "", -9999, row.isEcotox()));
			}

			if (!ecotoxSpeciesColl.contains(ecoTaxGrp)) {
				ecotoxSpeciesColl.add(ecoTaxGrp);
			}

		}

		ecotoxQuerySpecies = ecotoxSpeciesColl.get(0);

//		//all tax groups deselected by default
//		ecotoxSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>(ecotoxTaxGroups);
//		ecotoxPrevSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>(ecotoxSelectedTaxGroups);
		ecotoxSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>();
		ecotoxPrevSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>();
		ecotoxSelectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
		for (RiskAssessorTaxGroup taxGrp : ecotoxTaxGroups) {
			if (!taxGrp.isDisabled()) {
				ecotoxSelectedTaxGroups.add(taxGrp);
				ecotoxPrevSelectedTaxGroups.add(taxGrp); // initialize prev selected collection
			}
		}
		for (RiskAssessorTaxGroup species : ecotoxSpeciesColl) {
			if (!species.isDisabled()) {
				ecotoxSelectedSpeciesColl.add(species);
			}
		}
//		

		onChangeEcotoxTaxMenu(); // needed to disable species rows
		// handleEcotoxQuerySpecies();
	}

	public void onChangeEcotoxTaxMenu() {
		// get all items in selectedTaxGroups that are not in
		// ecotoxPrevSelectedTaxGroups
		// only populates newItems if tax groups were added
		List<RiskAssessorTaxGroup> newItems = new ArrayList<RiskAssessorTaxGroup>();
		// if (ecotoxPrevSelectedTaxGroups.size() < ecotoxSelectedTaxGroups.size()) {
		newItems = new ArrayList<RiskAssessorTaxGroup>(ecotoxSelectedTaxGroups);
		newItems.removeAll(ecotoxPrevSelectedTaxGroups);
		// }
		// reset prevSelectedTaxGroups list
		ecotoxPrevSelectedTaxGroups = new ArrayList<RiskAssessorTaxGroup>(ecotoxSelectedTaxGroups);
		updateEcotoxSpeciesMenu(newItems);
//		handleEcotoxQuerySpecies();
	}

	public void updateEcotoxSpeciesMenu(List<RiskAssessorTaxGroup> newTaxGrps) {
		List<String> newTaxGrpNames = new ArrayList<String>();
		for (RiskAssessorTaxGroup tmpGrp : newTaxGrps) {
			if (!newTaxGrpNames.contains(tmpGrp.getTaxGroup())) {
				newTaxGrpNames.add(tmpGrp.getTaxGroup());
			}
		}

		// first disable all species not matching a currently selected tax group
		for (RiskAssessorTaxGroup speciesGrp : ecotoxSpeciesColl) {
			boolean contains = false;
			for (RiskAssessorTaxGroup taxGrp : ecotoxSelectedTaxGroups) {
				if (taxGrp.getTaxGroup().equals(speciesGrp.getTaxGroup())) {
					contains = true;
					break;
				}
			}
			if (contains) {
//				//set entry to enabled and check
				if (speciesGrp.isEcotox()) {
					speciesGrp.setDisabled(false);
				}
				if (!ecotoxSelectedSpeciesColl.contains(speciesGrp) && !speciesGrp.isDisabled()) {
					ecotoxSelectedSpeciesColl.add(speciesGrp);
				}
			} else {
				// set entry to disabled and uncheck
				speciesGrp.setDisabled(true);
				ecotoxSelectedSpeciesColl.remove(speciesGrp);
			}
		}

	}

	// ensures that query species is always selected and disabled
//	public void handleEcotoxQuerySpecies() {
//		if (ecotoxQuerySpecies != null) {
//			if (!ecotoxSelectedSpeciesColl.contains(ecotoxQuerySpecies)) {
//				ecotoxSelectedSpeciesColl.add(0, ecotoxQuerySpecies);
//			}
//			ecotoxQuerySpecies.setDisabled(true);
//			highlightEcotoxSpeciesFirstRow();
//		}
//	}

//	public void highlightEcotoxSpeciesFirstRow() {
//		if (ecotoxQuerySpecies != null) {
//			StringBuilder sb = new StringBuilder();
//			// highlight first row
//			sb.append("var firstRow = document.getElementById('tabView:reportForm:ecotoxSpeciesMenu_data');");
//			sb.append("if (firstRow !== null){");
//			sb.append("firstRow.childNodes[0].classList.add('ui-state-highlight');");
//			// check first checkbox
//			sb.append(
//					"firstRow.querySelector('span.ui-chkbox-icon').className = 'ui-chkbox-icon ui-icon ui-icon-check ui-c';}");
//			PrimeFaces.current().executeScript(sb.toString());
//		}
//	}

	public void pushEcotoxNCBI() {
		System.out.println("pushing NCBI tax Ids");
		showEcotoxSpeciesPanel = false;
	}

	public void ecotoxBackButton() {
		showEcotoxSpeciesPanel = true;
		setEcotoxSearchChemical(null);
//		handleEcotoxQuerySpecies();
	}

	public void pushEcotoxCASRN() {
		System.out.println("Inside pushEcotoxCASRN");

		// get list of strings for chemical cas numbers and species tax ids
		List<String> casList = new ArrayList<String>();
		for (Chemical chem : ecotoxChemicalList) {
			casList.add(String.valueOf(chem.getCasNum()));
		}
		List<String> taxList = new ArrayList<String>();
		for (RiskAssessorTaxGroup grp : ecotoxSelectedSpeciesColl) {
			taxList.add(String.valueOf(grp.getSpeciesTaxId()));
		}

		// build URL
		StringBuilder urlSB = new StringBuilder();
		urlSB.append("https://cfpub.epa.gov/ecotox/explore.cfm");
		if (taxList.size() > 0) {
			if (casList.size() > 0) {
				urlSB.append("?cas=");
				urlSB.append(String.join(",", casList));
				urlSB.append("&ncbi=");
			} else {
				urlSB.append("?ncbi=");
			}
			urlSB.append(String.join(",", taxList));
		}

		ecotoxURL = urlSB.toString();

		// build javascript call
		StringBuilder sb = new StringBuilder();
		sb.append("window.open('");
		sb.append(ecotoxURL);
		sb.append("', '_blank');");

		System.out.println("SB=" + sb.toString());
		PrimeFaces.current().executeScript(sb.toString());
	}

	public List<Chemical> completeChemical(String searchStr) {

		List<Chemical> searchRes = ReportController.getChemicalSearch(searchStr);

		ecotoxPossibleChemicalList.clear();
		ecotoxPossibleChemicalList.addAll(searchRes);

		return ecotoxPossibleChemicalList;

	}

	public void addEcotoxChemicalButton() {
		if (ecotoxSearchChemical != null && !ecotoxChemicalList.contains(ecotoxSearchChemical)) {
			ecotoxChemicalList.add(ecotoxSearchChemical);
		} else {
			System.out.println("returning  error adding ecotoxSearchChemical to ecotoxSelectedChemicalsList");
			return;
		}

		setEcotoxSearchChemical(null);
	}

	public void removeChemicalButton() {
		System.out.println("inside removeChemicalButton");
		for (Chemical chem : ecotoxSelectedChemicalList) {
			ecotoxChemicalList.remove(chem);
		}
	}

	public void removeAllChemicalsButton() {
		ecotoxChemicalList.clear();
	}

	////////////////////////////////
	// Level 4 code
	////////////////////////////////

	public void setupNewLevel4Run() {
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");
		//only run this method if user has Itasser permissions
		if(theUser.getIsItasser().toLowerCase().equals("n")) {
			return;
		}

//		if (StringUtils.isBlank(level4JobName) || level4JobName == null) {
//			return;
//		}
//		level4JobExists = true;
//		level4JobSubmitted = false;

		levelFourAccessions.clear();
		// create rows using constructor with default priority (uses auto priority)
//		if (ortholog_count !=0) {
//			//use primary report
		int sourceLevel = getSourceLevelInt();
		if (sourceLevel == 1) {
			for (LevelOneReportRow row : levelOnePrimaryReport) {
				levelFourAccessions.add(new LevelFourAccessionRow(row, "0.1 No direc", "Low",
						currentReportInfo.getUpdateVersion(), null, null, null));
			}
		} else if (sourceLevel == 2){
			if (selectedLevelFourDomain != null && selectedLevelFourDomain.getLevel2RunId() > 0) {
				List<LevelTwoReportRow> tmpList = ReportController.getLevelTwoReportForUser(selectedLevelFourDomain.getRunId(),
						selectedLevelFourDomain.getLevel2RunId());
				itasserLevelTwoReport = filterLevTwoPrimaryReport(tmpList);
				String displayText = selectedLevelFourDomain.getDisplayText();
				String[] domainParts = displayText.split(",",3);
				String domainId = domainParts[0].split("\\) ", 2)[1];
				String domainName = domainParts[1];
				for (LevelTwoReportRow row : itasserLevelTwoReport) {
					row.setDomainId(domainId);
					row.setDomainName(domainName);					
					levelFourAccessions.add(new LevelFourAccessionRow(row, "0.1 No direc", "Low",
							currentReportInfo.getUpdateVersion(), null, null));
				}
				
			} else {
				System.out.println("No Level2 RunId");
			}
		} else {
			System.out.println("Incorrect level choice inside setupNewLevel4Run");
		}
//		} else {
//			//use full report
//			for (LevelOneReportRow row: levelOneReport) {
//				levelFourAccessions.add(new LevelFourAccessionRow(row, "0.1 No direc", "Low", currentReportInfo.getUpdateVersion(), "-", "-"));
//			}
//		}

		setL4DefaultPriority(levelFourAccessions);
		resetDefaultL4Priority();
		filteredLevelFourAccessions = new ArrayList<LevelFourAccessionRow>(levelFourAccessions);
	}

	// This method combines a level one report with minimal version of level four
	// data (only contains data from level4_data table)
	public List<LevelFourAccessionRow> combineLevelOneLevel4(List<LevelOneReportRow> levelOneReport,
			List<LevelFourAccessionRow> levelFourData) {

		List<LevelFourAccessionRow> newList = new ArrayList<LevelFourAccessionRow>();

		for (LevelFourAccessionRow l4Row : levelFourData) {
			for (LevelOneReportRow l1Row : levelOneReport) {
				if (l1Row.getAccession().equals(l4Row.getNcbiAccession())) {
					LevelFourAccessionRow newRow = new LevelFourAccessionRow(l1Row, l4Row);
					newList.add(newRow);
				}
			}
		}
		return newList;

	}
	
	// This method combines a level two report with minimal version of level four
		// data (only contains data from level4_data table)
	public List<LevelFourAccessionRow> combineLevelTwoLevel4(List<LevelTwoReportRow> levelTwoReport,
			List<LevelFourAccessionRow> levelFourData) {

		List<LevelFourAccessionRow> newList = new ArrayList<LevelFourAccessionRow>();

		for (LevelFourAccessionRow l4Row : levelFourData) {
			for (LevelTwoReportRow l2Row : levelTwoReport) {
				if (l2Row.getAccession().equals(l4Row.getNcbiAccession())) {
					LevelFourAccessionRow newRow = new LevelFourAccessionRow(l2Row, l4Row);
					newList.add(newRow);
				}
			}
		}
		return newList;

	}

	/**
	 * Updates/reloads level four run info
	 */
	public void reloadLevelFourResults() {
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
				.getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");
		//only run this method if user has Itasser permissions
		if(theUser.getIsItasser().toLowerCase().equals("n")) {
			return;
		}

		setupNewLevel4Run();

		setCreatedLevelFourRuns(ReportController.getCreatedLevelFour(accessionRunId, theUser.getUserid()));

		selectedCreatedLevelFourRun = null;
		level4JobName = "";

		setStartedLevelFourRuns(ReportController.getStartedLevelFour(accessionRunId, theUser.getUserid()));
		
		chosenLevelFourViewLevel = "Level1";
//		filteredStartedLevelFourRuns.clear();
//		int sourceLevelView = getSourceLevelViewInt();
//		for (LevelFourRequestableRow row: startedLevelFourRuns) {
//			if (row.getSourceLevel() == sourceLevelView) {
//				filteredStartedLevelFourRuns.add(row);
//			}
//		}
//		selectedStartedLevelFourRun = null;
		changeLev4JobChoices();

		if (getCurrentReportInfo() != null
				&& latestUpdateInfo.getUpdateVersion() > getCurrentReportInfo().getUpdateVersion()) {
			setDisableRequestLevel4Buttons(true);
			setLevelFourRequestTip(
					"This was run with a previous data version.  Please re-submit the Level 1 accession to enable Level 4 runs.");
			setShowLevelFourRequestTip(true);
		} else {
			setDisableRequestLevel4Buttons(false);
			setShowLevelFourRequestTip(false);
		}

	}
	
	public void changeLev4JobChoices() {
		filteredStartedLevelFourRuns.clear();
		int sourceLevelView = getSourceLevelViewInt();
		for (LevelFourRequestableRow row: startedLevelFourRuns) {
			if (row.getSourceLevel() == sourceLevelView) {
				row.setDisable(false);
				filteredStartedLevelFourRuns.add(row);
			}
		}
		selectedStartedLevelFourRun = null;
		if (selectedCombineLevelFourRuns.size() == 0 && (filteredStartedLevelFourRuns.size() == 0 || filteredStartedLevelFourRuns.get(0).getSourceLevel() == 2)) {
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(true)");
		} else {
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(false)");
		}
	}
	
	public void lev4JobViewSelectionChange() {
		//only need to worry about disabling for sourceLevel = 2
		int sourceLevel = filteredStartedLevelFourRuns.get(0).getSourceLevel();
		if (sourceLevel == 2) {
			for (LevelFourRequestableRow row : filteredStartedLevelFourRuns) {
				if(selectedCombineLevelFourRuns.size() > 0) {
					if (!selectedCombineLevelFourRuns.get(0).getDomain().equals(row.getDomain()) || selectedCombineLevelFourRuns.get(0).getStartPos() != row.getStartPos()) {
						row.setDisable(true);
					} else {
						row.setDisable(false);
					}
				} else {
					row.setDisable(false);
				}
			}
		}
		
		//also handle enable/disable of checkall box
		if (selectedCombineLevelFourRuns.size()==0 && sourceLevel == 2) {
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(true)");
		} else {
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(false)");
		}
	}
	
	public void lev4JobViewSelectAllChange() {
		//don't allow user to select more than one if sourceLevel = 2
		if (selectedCombineLevelFourRuns.size() > 0 && filteredStartedLevelFourRuns.get(0).getSourceLevel() == 2) {
			selectedCombineLevelFourRuns.clear();
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(true)");
		} else {
			PrimeFaces.current().executeScript("disableCheckAllL4ViewMenu(false)");
		}
		for (LevelFourRequestableRow row : filteredStartedLevelFourRuns) {
			row.setDisable(false);
		}
	}

//	public void setAutoPriority(List<LevelFourAccessionRow> list) {
//
//		String priority = "Low";
//		boolean contains = false;
//		String[] lowPriorityProtein = { "partial", "hypothetical", "low quality", "unnamed", "uncharacterized",
//				"unknown", "green fluorescent protein" };
//		Pattern pattern1 = Pattern.compile("^[POQ]\\d+");
//		Pattern pattern2 = Pattern.compile("^NP_");
//		Pattern pattern3 = Pattern.compile("^[A-Z]{3}\\d+");
//		for (int i = 0; i < list.size(); i++) {
//			LevelFourAccessionRow row = list.get(i);
//			System.out.println(row.getNcbiAccession());
//			priority = "Low";
//
//			for (String item : lowPriorityProtein) {
//				if (row.getProteinName().contains(item)) {
//					contains = true;
//					break;
//				}
//			}
//			if (i == 0) {
//				priority = "High";
//			} else if (contains) {
//				priority = "Low";
//			} else if (row.getCommonName().toLowerCase().contains("other sequences")) {
//				priority = "Low";
//			} else if (pattern1.matcher(row.getNcbiAccession()).find()) {
//				priority = "High";
//			} else if (pattern2.matcher(row.getNcbiAccession()).find()) {
//				priority = "High";
//			} else if (pattern3.matcher(row.getNcbiAccession()).find()) {
//				priority = "High";
//			}
//			contains = false;
//			row.setAutoPriority(priority);
//			row.setPriority(priority);
//		}
//
//	}

	public void setL4DefaultPriority(List<LevelFourAccessionRow> list) {

		String priority = "Unknown";
		for (int i = 0; i < list.size(); i++) {
			LevelFourAccessionRow row = list.get(i);
			priority = "Low";
			priority = determineAccessionPriority(row.getNcbiAccession(), row.getProteinName(), row.getCommonName(),
					false);
			row.setAutoPriority(priority);
			row.setPriority(priority);
		}

	}

	public String determineAccessionPriority(String ncbiAccession, String proteinName, String commonName, boolean L3) {
		String priority = "Low";
		boolean containsLow = false;
		boolean containsHigh = false;
		String[] lowPriorityProtein = { "partial", "hypothetical", "low quality", "unnamed", "uncharacterized",
				"unknown", "green fluorescent protein", "other sequences" };
		String[] highPriorityProtein = { "recname" };
		Pattern pattern1 = Pattern.compile("^[POQ]\\d+");
		Pattern pattern2 = Pattern.compile("^NP_");
		Pattern pattern3 = Pattern.compile("^[A-Z]{3}\\d+");
		Pattern pattern4 = Pattern.compile("^XP_");
		priority = "Low";

		for (String item : lowPriorityProtein) {
			if (proteinName.toLowerCase().contains(item)) {
				containsLow = true;
				break;
			}
		}
		
		for (String item : highPriorityProtein) {
			if (proteinName.toLowerCase().contains(item)) {
				containsHigh = true;
				break;
			}
		}

		if (L3) {
			if (proteinName.toLowerCase().contains("predicted"))
				priority = "High";
			if (pattern4.matcher(ncbiAccession).find())
				priority = "High";
		}

		if (ncbiAccession.equals(accession)) {
			priority = "High";
		}
		if (containsHigh) {
				priority = "High";
		}

		if (pattern1.matcher(ncbiAccession).find()) {
			priority = "High";
		}
		if (pattern2.matcher(ncbiAccession).find()) {
			priority = "High";
		}
		if (pattern3.matcher(ncbiAccession).find()) {
			priority = "High";
		}
		//readjust to low as needed
		if (containsLow) {
			priority = "Low";
		}

		return priority;

	}

	public void onHidePrioritiesDlg() {
//		System.out.println("Inside test!!!!!");
//		System.out.println("updatePriorities" + updatePriorities);

		if (updatePriorities) {
			updateL4Priorities();
		} else {
			cancelL4PriorityChanges();
		}

//		System.out.println("Exiting");
		updatePriorities = false;
	}

	public void updatePrioritiesButton() {
		updatePriorities = true;
	}

	public void updateL4Priorities() {
		prevSelectedLevelFourAccessions.clear();
		for (LevelFourAccessionRow row : selectedLevelFourAccessions) {
			prevSelectedLevelFourAccessions.add(LevelFourAccessionRow.newInstance(row));
		}
	}

	public void cancelL4PriorityChanges() {
		selectedLevelFourAccessions = new ArrayList<LevelFourAccessionRow>(prevSelectedLevelFourAccessions);
		// reset rows to default if previously unchecked
		for (LevelFourAccessionRow row : levelFourAccessions) {
			if (!selectedLevelFourAccessions.contains(row)) {
				row.setPriority(row.getAutoPriority());
			}
		}

		// reset rows to previous priority if checked
		for (LevelFourAccessionRow row : selectedLevelFourAccessions) {
			LevelFourAccessionRow tmp = levelFourAccessions.stream()
					.filter(accRow -> row.getNcbiAccession().equals(accRow.getNcbiAccession())).findAny().orElse(null);
			if (tmp != null) {
				tmp.setPriority(row.getPriority());
			}
		}

	}

	public void resetDefaultL4Priority() {
		List<LevelFourAccessionRow> highPriorityList = new ArrayList<LevelFourAccessionRow>();
		for (LevelFourAccessionRow row : levelFourAccessions) {
			if (row.getAutoPriority().toLowerCase().contains("high")
					|| row.getAutoPriority().toLowerCase().contains("auto included")) {
				highPriorityList.add(row);
			}
			row.setPriority(row.getAutoPriority());
		}
		selectedLevelFourAccessions = new ArrayList<LevelFourAccessionRow>(highPriorityList);
		prevSelectedLevelFourAccessions.clear();
		for (LevelFourAccessionRow row : highPriorityList) {
			prevSelectedLevelFourAccessions.add(LevelFourAccessionRow.newInstance(row));
		}
	}

	public List<LevelFourAccessionRow> getPriorityChanges() {
		List<LevelFourAccessionRow> newItems = new ArrayList<LevelFourAccessionRow>(selectedLevelFourAccessions);
		newItems.removeAll(prevSelectedLevelFourAccessions);
		return newItems;
	}

	public void onL4PriorityChange() {
		updateL4Priority();
	}

	public void updateL4Priority() {
		// this method updates levelFourAccessions based on user specified priorites

		for (LevelFourAccessionRow row : levelFourAccessions) {
			LevelFourAccessionRow included = selectedLevelFourAccessions.stream()
					.filter(theRow -> row.getNcbiAccession().equals(theRow.getNcbiAccession())).findAny().orElse(null);
			if (included != null) {
				// update both selected and orig
				if (!row.getAutoPriority().toLowerCase().contains("high")
						&& !row.getAutoPriority().toLowerCase().contains("auto included")) {
					row.setPriority("High (user included)");
					included.setPriority("High (user included)");
				} else {
					row.setPriority(row.getAutoPriority());
					included.setPriority(row.getAutoPriority());
				}
			} else {
				// Not selected - adjust back to default setting
				if (row.getAutoPriority().toLowerCase().contains("high")) {
					// user deselected
					row.setPriority("Low (user excluded)");
				} else {
					row.setPriority(row.getAutoPriority());
				}
			}
		}

	}

	public void requestFastaRun() {

		FacesContext context = FacesContext.getCurrentInstance();
		boolean validated = true;

		int sourceLevel = getSourceLevelInt();
		
		if (StringUtils.isBlank(level4JobName)) {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must specify a Level 4 Run Name"));
			return;
		}

		// Validation
		Pattern jobNamePattern = Pattern.compile("[^a-zA-Z0-9_ -]");
		// Trim leading and trailing spaces for better filenames
		level4JobName = level4JobName.trim();
		Matcher jobNameMatcher = jobNamePattern.matcher(level4JobName);
		if (level4JobName.length() > 50) {
			validated = false;
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Level 4 Run Name must be less than 50 characters."));
		}
		if (jobNameMatcher.find()) {
			validated = false;
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Only alphanumeric and _ - <space> allowed in Level 4 Run Name"));
		}

		if (!validated) {
			return;
		}

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");
		int lev2RunId = -1;
		if (sourceLevel == 2) {
			lev2RunId = selectedLevelFourDomain.getLevel2RunId();
		}
		LevelFourRequestableRow request = new LevelFourRequestableRow(accessionRunId, theUser.getUserid(),
				level4JobName, sourceLevel, lev2RunId, selectedLevelFourDomain.getDisplayText());

		// assume that this can only be used once per level 4 run, so all accessions
		// will need FASTAs
		List<LevelFourAccessionRow> accRows = new ArrayList<LevelFourAccessionRow>();
		accRows = new ArrayList<LevelFourAccessionRow>(selectedLevelFourAccessions);

		// add LevelFourAccessionRow list to LevelFourRequestable Object
		request.setAccessionData(accRows);

		String status = RequestRunController.requestLevel4FASTAs(request);

		if (status.toLowerCase().equals("this run name is already selected")) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", status));
		} else {
			if (sourceLevel == 1) {
				context.addMessage("growl",
					new FacesMessage("Level 4 Run Created", "Status " + status + " FASTAs are being generated."));
			} else if (sourceLevel == 2) {
				context.addMessage("growl",
					new FacesMessage("Level 4 Run Created", "Status " + status + " FASTAs complete."));
			} else {
				context.addMessage("growl",
						new FacesMessage("Error in Level 4 Run", "Status " + status + " Source level not found."));
			}
		}
		
		//reset after request
		level4JobName = "";
		chosenLevelFourLevel = "Level1";
		levelFourDomainVisible = false;
		changeLevelFourLevel();

	}

	public void loadFASTAReport() {

		if (selectedCreatedLevelFourRun == null) {
			// show growl message also
			System.out.println("No selected job");
			selectedLevelFourFASTAs.clear();
			return;
		}

		System.out.println(selectedCreatedLevelFourRun.getJobName());

		List<LevelFourAccessionRow> level4Data = ReportController
				.getLevel4Data(selectedCreatedLevelFourRun.getLevel4RunId());
		
		levelFourFASTAs.clear();
		int sourceLevel = selectedCreatedLevelFourRun.getSourceLevel();
		if(sourceLevel==1) {
		List<LevelOneReportRow> localOneReport = new ArrayList<LevelOneReportRow>();
//	if (ortholog_count !=0) {
//	//use primary report
		localOneReport = levelOnePrimaryReport;
//} else {
//	//use full report
//	localOneReport = levelOneReport;
//	
//}
		
			levelFourFASTAs.addAll(combineLevelOneLevel4(localOneReport, level4Data));
		} else if (sourceLevel == 2) {
			List<LevelTwoReportRow> localTwoReport = new ArrayList<LevelTwoReportRow>();
			List<LevelTwoReportRow> tmpList = ReportController.getLevelTwoReportForUser(selectedCreatedLevelFourRun.getAccessionRunId(),
					selectedCreatedLevelFourRun.getLevel2RunId());
			//find completedLevelTwoDomains using level2RunId
			LevelTwoRequestableRow level2Domain = completedLevelTwoDomains.stream().filter(completedLevelTwoDomains -> selectedCreatedLevelFourRun.getLevel2RunId() == completedLevelTwoDomains.getLevel2RunId()).findAny().orElse(null);
			String displayText = level2Domain.getDisplayText();
			String[] domainParts = displayText.split(",",3);
			String domainId = domainParts[0].split("\\) ", 2)[1];
			String domainName = domainParts[1];
			itasserLevelTwoReport = filterLevTwoPrimaryReport(tmpList);
			localTwoReport = itasserLevelTwoReport;
			for (LevelTwoReportRow row: localTwoReport) {
				row.setDomainId(domainId);
				row.setDomainName(domainName);
			}
			levelFourFASTAs.addAll(combineLevelTwoLevel4(localTwoReport, level4Data));
		} else {
			System.out.println("Error in ReportView.loadFASTAReport:  Invalid source level");
		}
		
		//sort by percent similarity
		Collections.sort(levelFourFASTAs, Comparator.comparing(LevelFourAccessionRow::getPercentSimilarity).reversed());

		filteredLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>(levelFourFASTAs);

		// selectedLevelFourFASTAs.clear();
		// Set default selected I-TASSER FASTAs
//		selectedLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>(levelFourFASTAs);
		selectedLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>();

		prevSelectedLevelFourFASTAs.clear();
		for (LevelFourAccessionRow row : selectedLevelFourFASTAs) {
			prevSelectedLevelFourFASTAs.add(LevelFourAccessionRow.newInstance(row));
		}

	}
	
	public Map<String,String> findIdenticalFASTAs(List<LevelFourAccessionRow> input) {

		//get list of unique FASTAs
		List<String> uniqueFASTAs = new ArrayList<String>();
		for (LevelFourAccessionRow row : input) {
			if (!uniqueFASTAs.contains(row.getFasta())) {
				uniqueFASTAs.add(row.getFasta());
			}
		}
		
		Map<String, String> duplicateAccs = new LinkedHashMap<String,String>();
		for (String fasta : uniqueFASTAs) {
			int found = 0;
			String uniqueAcc = "";
			for (LevelFourAccessionRow row : input) {
				if (row.getFasta().equals(fasta)) {
					found++;
					if (found == 1) {
						uniqueAcc = row.getNcbiAccession();
					} else {
						duplicateAccs.put(row.getNcbiAccession(), uniqueAcc);
					}
				}
			}
		}
		
		return duplicateAccs;
		
	}

	public void createFastaFile() {

		String NEW_LINE_SEPARATOR = "\n";
		CSVPrinter csvFilePrinter = null;

		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		StringBuilder sb = new StringBuilder();

		try {
			csvFilePrinter = new CSVPrinter(sb, csvFileFormat);
			csvFilePrinter.printRecord("Level 4 FASTAs");
			csvFilePrinter.printRecord("");
			for (LevelFourAccessionRow row : levelFourFASTAs) {
				csvFilePrinter.printRecord(row.getFormattedFasta());
			}

			csvFilePrinter.printRecord();


		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());

		String fileName = "L4_" + selectedCreatedLevelFourRun.getJobName() + "_FASTAs.csv";

		lev4FastaFile = DefaultStreamedContent.builder().contentType("application/txt").name(fileName).stream(() -> in)
				.build();
	}
	
	public void updateFilterFASTAsButton() {
		updateFilteredFASTAs = true;


		for (LevelFourAccessionRow row : filteredLevelFourFASTAs) {
			if (selectedLevelFourFASTAs.contains(row)) {
				System.out.println(row.getNcbiAccession());
			}
		}

		System.out.println("    ------------   ");

	}


	public void onRowReorder(ReorderEvent event) {
		System.out.println("Row Moved From: " + event.getFromIndex() + ", To:" + event.getToIndex());
	}

	public void onHideFASTADlg() {

		if (updateFilteredFASTAs) {
			updateL4Fastas();
		} else {
			cancelL4FastaChanges();
		}

//		System.out.println("Exiting");
		updateFilteredFASTAs = false;
	}

	public void updateL4Fastas() {

		prevSelectedLevelFourFASTAs.clear();
		for (LevelFourAccessionRow row : selectedLevelFourFASTAs) {
			prevSelectedLevelFourFASTAs.add(LevelFourAccessionRow.newInstance(row));
		}
	}

	public void cancelL4FastaChanges() {

		selectedLevelFourFASTAs = new ArrayList<LevelFourAccessionRow>(prevSelectedLevelFourFASTAs);

		// reset rows to default if previously unchecked
		for (LevelFourAccessionRow row : levelFourFASTAs) {
			if (!selectedLevelFourFASTAs.contains(row)) {
				row.setPriority(row.getAutoPriority());
			}
		}

		// reset rows to previous priority if checked
		for (LevelFourAccessionRow row : selectedLevelFourFASTAs) {
			LevelFourAccessionRow tmp = levelFourFASTAs.stream()
					.filter(accRow -> row.getNcbiAccession().equals(accRow.getNcbiAccession())).findAny().orElse(null);
			if (tmp != null) {
				tmp.setPriority(row.getPriority());
			}
		}

	}

	public void validateL4Submission() {
		FacesContext context = FacesContext.getCurrentInstance();

		// pattern is up to 3 letters or numbers and then an optional suffix
		// suffix can be started with either : or _ and then any letter
		Pattern templatePattern = Pattern.compile("^[a-zA-Z0-9]{4}[:_][a-zA-Z]$");
		levelFourTemplate = levelFourTemplate.trim();
		Matcher templateMatcher = templatePattern.matcher(levelFourTemplate);
		
		//NEED TO VALIDATE USER DEFINED PDB also
		
		if (!templateMatcher.find() && levelFourTemplate.length() > 0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid PDB:ID"));
		} else if (selectedLevelFourFASTAs.size() > 10) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"Maximum of 10 FASTAs can be submitted for a Level 4 "));
		} else if (levelFourTemplate.length()>0 && userInputL4Restraint.length()>0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
					"Only 1 PDB restraint can be used"));
		} else if (userInputL4Restraint.length()>0 && levelFourTemplateName.trim().length() == 0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", 
					"A name is required for the user-defined restraint"));
		} else {
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('requestITasserDlg').show();");
		}
		

	}

	public void submitItasserRun() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (selectedLevelFourFASTAs.size() == 0) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"You must select FASTAs to submit a Level 4 Run"));
			return;
		}

		List<LevelFourAccessionRow> submissionFASTAs = new ArrayList<LevelFourAccessionRow>();
		// get selected in order
		for (LevelFourAccessionRow row : levelFourFASTAs) {
			if (selectedLevelFourFASTAs.contains(row)) {
				submissionFASTAs.add(row);
			}
		}

		if (!selectedCreatedLevelFourRun.getStatus().equals("FASTAs complete")) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
					"This Level 4 Run has already been submitted.  Please create a run with a new job name."));
			return;
		}
		
		//This should already by validated here so that only one option levelFourTemplate or userInputL4Restraint has text
		if (!levelFourTemplate.equals("")){
			selectedCreatedLevelFourRun.setTemplate(levelFourTemplate);
		} else if (!userInputL4Restraint.equals("")){
			selectedCreatedLevelFourRun.setTemplate(levelFourTemplateName);
			selectedCreatedLevelFourRun.setTemplatePDB(userInputL4Restraint); 
		} else {
			selectedCreatedLevelFourRun.setTemplate("");
			selectedCreatedLevelFourRun.setTemplatePDB("");
		}
		
		selectedCreatedLevelFourRun.setAccessionData(new ArrayList<LevelFourAccessionRow>(submissionFASTAs));
		if (selectedCreatedLevelFourRun.getAccessionData().size() == 0) {
			System.out.println("No accessionData");
		}
		for (LevelFourAccessionRow row : selectedCreatedLevelFourRun.getAccessionData()) {
			System.out.println(row.getNcbiAccession());
		}

		if (StringUtils.isBlank(selectedCreatedLevelFourRun.getJobName())) {
			context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No job selected"));
			return;
		}

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		User theUser = (User) session.getAttribute("UserInfo");

		String status = RequestRunController.requestLevel4Itasser(selectedCreatedLevelFourRun);
		
		levelFourTemplate = null;
		selectedCreatedLevelFourRun = null;
		userInputL4Restraint = null;
		selectedLevelFourFASTAs.clear();

		context.addMessage("growl", new FacesMessage("Level 4 I-Tasser submitted", "Status " + status));

	}

//	public void confirm() {
//		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmed", "You have accepted");
//        FacesContext.getCurrentInstance().addMessage(null, message);
//	}

//	public void viewPDBButton(LevelFourAccessionRow selectedRow) {
//		
//		selectedPDBAccessionRow = selectedRow;
//		selectedPDBAccessionRow.setPdbRows(ProteinDataBankRow.parsePDBIntoRows(selectedRow.getPdb()));
//		
//	}

	public void createLev4PDBFile(LevelFourAccessionRow selectedRow) {

		String fileName = selectedRow.getNcbiAccession() + ".pdb";

		InputStream in = new ByteArrayInputStream(selectedRow.getPdb().getBytes());

		lev4PDBFile = DefaultStreamedContent.builder().contentType("application/txt").name(fileName).stream(() -> in)
				.build();

	}
	
	public void createLev4PDBFile2(LevelFourResultRow selectedRow) {

		String fileName = selectedRow.getAcc2() + ".pdb";

		InputStream in = new ByteArrayInputStream(selectedRow.getPdb().getBytes());

		lev4PDBFile = DefaultStreamedContent.builder().contentType("application/txt").name(fileName).stream(() -> in)
				.build();

	}

	public void createZippedLev4PDBFile() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(out);
		String base = "";
		if (selectedLevelFourRuns.size() > 1) {
			base = "Combined_";
		}
		for (LevelFourRequestableRow row : selectedLevelFourRuns) {
			base = base + row.getJobName() + "_";
		}
		base = base + "pdb_files";
//		String base = selectedStartedLevelFourRun.getLevel4RunId() + "_" + selectedStartedLevelFourRun.getJobName()
//				+ "_pdb_files";
		String dir = base + "/";
		try {
			zos.putNextEntry(new ZipEntry(dir));
			for (LevelFourAccessionRow row : levelFourReport) {
				String fileName = row.getNcbiAccession() + ".pdb";

				if (row.getPdb() != null) {
					zos.putNextEntry(new ZipEntry(dir + fileName));
					zos.write(row.getPdb().getBytes());
					zos.closeEntry();
				} else {
					String errorMsg = "PDB is not available for " + row.getNcbiAccession();
					zos.putNextEntry(new ZipEntry(dir + row.getNcbiAccession() + "_error.txt"));
					zos.write(errorMsg.getBytes());
					zos.closeEntry();
				}
			}

			zos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lev4PDBFile = DefaultStreamedContent.builder().contentType("application/zip").name(base + ".zip")
				.stream(() -> new ByteArrayInputStream(out.toByteArray())).build();

		return;
	}
	
	
public void createZippedTMalignPDBFile() {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(out);
		String base = selectedTMAlignReportChoice.getQueryDisplayName().replaceAll(":", "_").replaceAll(" ", "");

		base = base + "_pdb_files";
		String dir = base + "/";
		try {
			zos.putNextEntry(new ZipEntry(dir));
			for (LevelFourResultRow row : levelFourTMAlignReport) {
				String fileName = row.getAcc2() + ".pdb";

				if (row.getPdb() != null) {
					zos.putNextEntry(new ZipEntry(dir + fileName));
					zos.write(row.getPdb().getBytes());
					zos.closeEntry();
				} else {
					String errorMsg = "PDB is not available for " + row.getAcc2();
					zos.putNextEntry(new ZipEntry(dir + row.getAcc2() + "_error.txt"));
					zos.write(errorMsg.getBytes());
					zos.closeEntry();
				}
			}

			zos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tmalignPDBFile = DefaultStreamedContent.builder().contentType("application/zip").name(base + ".zip")
				.stream(() -> new ByteArrayInputStream(out.toByteArray())).build();

		return;
	}
	

	private void parseLevelFourStatus(List<LevelFourAccessionRow> report) {
		// commented out when X amino acids are replaced with A
//		for (LevelFourAccessionRow row : report) {
//			switch (row.getStatus()) {
//			case "0.4 X aa":
//				row.setStatus("Not Run: contains unknown amino acid");
//				break;
//			default:
//				break;
//			}
//		}

	}

	public void pushITasserToTMalignButton() {

		FacesContext context = FacesContext.getCurrentInstance();
		
		boolean displayMsg = false;

		for (LevelFourAccessionRow row : selectedLevelFourReportRows) {

			//only allow completed I-TASSER accessions to be pushed to TM-align
			if (row.getStatus().equals("I-TASSER complete")) {
				LevelFourResultRow newRow = new LevelFourResultRow(new LevelFourResultRow(), row);
				// TODO:check for duplicate before adding
//				newRow.setPdbSource("I-TASSER");
				newRow.setAcc2(row.getNcbiAccession());
				if (!levelFourTMAlignSelectionReport.contains(newRow)) {
					levelFourTMAlignSelectionReport.add(newRow);
				}

				//add to Query Selection DropDown
				LevelFourRequestableRow tmp = selectedLevelFourRuns.stream()
						.filter(reqRow -> reqRow.getLevel4RunId() == row.getLevel4RunId()).findAny().orElse(null);
				
				if (tmp != null) {
					// here queryTemplate comes from template property
					LevelFourRequestableRow queryRow = new LevelFourRequestableRow(row, tmp.getJobName(), tmp.getSourceLevel(), tmp.getLevel2RunId());
					if (!tmAlignQueryAccs.contains(queryRow)) {
						tmAlignQueryAccs.add(queryRow);
					}
				}
			} else {
				displayMsg = true;
			}

		}

		// select all by default
		selectedTMAlignSelectionReportRows = new ArrayList<LevelFourResultRow>();
		selectedTMAlignSelectionReportRows.addAll(levelFourTMAlignSelectionReport);
		
		if(displayMsg) {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Only completed I-TASSER runs can be pushed to TM-align"));
		}
	}

	public void submitTMAlign() {
		FacesContext context = FacesContext.getCurrentInstance();

		// TODO: Handle submission of combined report

		if (selectedTMAlignSelectionReportRows.size() < 1) {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "At least two accessions must be selected"));
			return;
		} else if (selectedTMAlignQueryAcc == null) {
			context.addMessage("growl",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "A query accession must be selected"));
			return;
		}

		String statuses = "";
		List<LevelFourRequestableRow> requests = new ArrayList<LevelFourRequestableRow>();
		for (LevelFourRequestableRow entry : selectedLevelFourRuns) {
			LevelFourRequestableRow tmAlignRequestable = LevelFourRequestableRow.newInstance(entry);
//			tmAlignRequestable.setAccessionData(new ArrayList<LevelFourAccessionRow>());
//			tmAlignRequestable.getQueryAccessionData().setNcbiAccession(selectedTMAlignQueryAcc.getQueryAccessionData().getNcbiAccession());
			tmAlignRequestable.setQueryAccessionData(
					LevelFourAccessionRow.newInstance(selectedTMAlignQueryAcc.getQueryAccessionData()));
			tmAlignRequestable.setQueryJobName(selectedTMAlignQueryAcc.getQueryJobName());
//			tmAlignRequestable.getQueryAccessionData().setTemplate(selectedTMAlignQueryAcc);
//			tmAlignRequestable.getQueryAccessionData().setLevel4RunId(selectedTMAlignQueryAcc.getQueryAccessionData().getLevel4RunId());
//			tmAlignRequestable.getQueryAccessionData().setTemplate(selectedTMAlignQueryAcc.getQueryAccessionData().getTemplate());
			tmAlignRequestable.constructQueryDisplayName();

			// TODO: Add query acc info to submission

			// only retain those accessions that have been selected
			tmAlignRequestable.setAccessionData(new ArrayList<LevelFourAccessionRow>());
			for (LevelFourResultRow row : selectedTMAlignSelectionReportRows) {
				// only allow accs from given level4 run
				if (row.getLevel4RunId() == tmAlignRequestable.getLevel4RunId()) {
					// don't allow query accession
					if (!(row.getLevel4RunId() == tmAlignRequestable.getQueryAccessionData().getLevel4RunId()
							&& row.getAcc2().equals(tmAlignRequestable.getQueryAccessionData().getNcbiAccession()))) {

						LevelFourAccessionRow reqRow = null;
						if (row.getPdbSource().equals("I-TASSER")) {
							reqRow = findAccessionRowFromResultRow(row);
							if (reqRow != null) {
								tmAlignRequestable.getAccessionData().add(reqRow);
							}
						} 
					}
				}
			}


			if (tmAlignRequestable.getAccessionData().size() > 0) {
				requests.add(tmAlignRequestable);
			}
		}
		
		//another loop with a separate tmAlignRequestable object for AlphaFold, RCSB, Other
		LevelFourRequestableRow tmAlignRequestable = LevelFourRequestableRow.newInstance(selectedLevelFourRuns.get(0));
		tmAlignRequestable.setQueryAccessionData(
				LevelFourAccessionRow.newInstance(selectedTMAlignQueryAcc.getQueryAccessionData()));
		tmAlignRequestable.setQueryJobName(selectedTMAlignQueryAcc.getQueryJobName());
		tmAlignRequestable.constructQueryDisplayName();
		tmAlignRequestable.setAccessionData(new ArrayList<LevelFourAccessionRow>());
		for (LevelFourResultRow row : selectedTMAlignSelectionReportRows) {
			//TODO add check to ensure query accession is not added
			
			if (!row.getPdbSource().equals("I-TASSER")) {
				//make sure this acc is not the query acc
				if (!(row.getPdbSource() == tmAlignRequestable.getQueryAccessionData().getPdbSource()
						&& row.getAcc2().equals(tmAlignRequestable.getQueryAccessionData().getNcbiAccession()))) {
					LevelFourAccessionRow reqRow = new LevelFourAccessionRow();
				//set everything to NCBI acc, even if it is uniprot
					reqRow.setNcbiAccession(row.getAcc2());
					reqRow.setProteinName(row.getProteinName());
					reqRow.setSpeciesTaxId(row.getSpeciesTaxId());
					reqRow.setTaxonomyName(row.getTaxonomyName());
					reqRow.setScientificName(row.getScientificName());
					reqRow.setCommonName(row.getCommonName());
					reqRow.setPdbSource(row.getPdbSource());
					reqRow.setPdb(row.getPdb());
					reqRow.setLevel4RunId(row.getLevel4RunId());
					tmAlignRequestable.getAccessionData().add(reqRow);
				}
			}
			
			
			
		}
		if (tmAlignRequestable.getAccessionData().size() > 0) {
				requests.add(tmAlignRequestable);
		}
		

		String ret = RequestRunController.requestLevel4TMAlign(requests);

		context.addMessage("growl", new FacesMessage("Level 4 TM-align Status", ret));

	}

	public LevelFourAccessionRow findAccessionRowFromResultRow(LevelFourResultRow resRow) {

		for (LevelFourAccessionRow row : selectedLevelFourReportRows) {
			if (row.getNcbiAccession() == resRow.getAcc2() && row.getLevel4RunId() == resRow.getLevel4RunId()) {
				return row;
			}
		}
		// print error if acc row is not found
		System.out.println("Error in findAccessionRowFromResultRow:  I-TASSER row " + resRow.getAcc2() + " not found in level 4 report");

		return null;

	}
	
	public void pushAlphaToTMAlign() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		//validation
		if (alphaFoldPDBInput.trim().isEmpty()) {
			context.addMessage("growl", new FacesMessage("Error in Push to TM-align", "PDB field is empty"));
			return;
		}
		if (sourceChoice.isEmpty()) {
			context.addMessage("growl", new FacesMessage("Error in Push to TM-align", "Selected Source is empty"));
			return;
		}
		if (alphaFoldAccInput.trim().isEmpty()) {
			context.addMessage("growl", new FacesMessage("Error in Push to TM-align", "Accession field is empty"));
			return;
		}
		int speciesTaxId=-1;
		if(alphaFoldTaxIdInput != null && !alphaFoldTaxIdInput.trim().isEmpty()) {
			try {
				speciesTaxId = Integer.parseInt(alphaFoldTaxIdInput);
			} catch (NumberFormatException e) {
				context.addMessage("growl", new FacesMessage("Error in Push to TM-align", "Species Tax Id field must be an integer"));
				return;
			}
			
		}
		
		
		//push to TM-Align
//		for (LevelFourAccessionRow row: levelFourAlphaFoldReport) {
//			LevelFourResultRow newRow = new LevelFourResultRow(new LevelFourResultRow(), row);
			LevelFourResultRow newRow = new LevelFourResultRow();
//			newRow.setAcc1(row.getNcbiAccession()); //temporarily set for matching to uniprot
			newRow.setAcc2(alphaFoldAccInput);
			newRow.setPdbSource(sourceChoice);
			newRow.setProteinName(alphaFoldProtInput);
			newRow.setSpeciesTaxId(speciesTaxId);
			newRow.setTaxonomyName(alphaFoldTaxGrpInput);
			newRow.setScientificName(alphaFoldSciNameInput);
			newRow.setCommonName(alphaFoldCommonNameInput);
			newRow.setPdb(alphaFoldPDBInput);
			
			newRow.setLevel4RunId(selectedLevelFourRuns.get(0).getLevel4RunId());
			//Double check that below contains statement properly validates for duplicate accession names from any given source
			if (!levelFourTMAlignSelectionReport.contains(newRow)) {
				levelFourTMAlignSelectionReport.add(newRow);
			} else {
				//add message for duplicate accession name from a given source
				//this should also allow for same accession name from 2 different
				//itasser runs as long as restraint template is different
				context.addMessage("growl", new FacesMessage("Error in Push to TM-align", "Duplicate accession name for selected source"));
				return;
			}
			
			//now add to query template dropdown
			
			//LevelFourRequestableRow queryRow = new LevelFourRequestableRow(row, "");
			LevelFourRequestableRow queryRow = new LevelFourRequestableRow();
			//String jobName = row.getNcbiAccession() + "(" + newRow.getPdbSource() + ")";
			String jobName = newRow.getAcc2() + "(" + newRow.getPdbSource() + ")";
			queryRow.setQueryJobName(newRow.getPdbSource());
			queryRow.setQueryDisplayName(jobName);
			//LevelFourAccessionRow queryAccData = queryRow.getQueryAccessionData();
			LevelFourAccessionRow queryAccData = new LevelFourAccessionRow();
			queryAccData.setNcbiAccession(alphaFoldAccInput);
			queryAccData.setPdbSource(sourceChoice);
			queryAccData.setProteinName(alphaFoldProtInput);
			queryAccData.setSpeciesTaxId(speciesTaxId);
			queryAccData.setTaxonomyName(alphaFoldTaxGrpInput);
			queryAccData.setScientificName(alphaFoldSciNameInput);
			queryAccData.setCommonName(alphaFoldCommonNameInput);
			//queryAccData.setPdb(formatPDB(alphaFoldPDBInput));
			queryAccData.setPdb(alphaFoldPDBInput);
			queryAccData.setLengthCutOff(null);
			queryAccData.setFasta(null);
			queryAccData.setAutoPriority(null);
			queryAccData.setCutoff(0.0);
			queryAccData.setFormattedFasta(null);
			queryAccData.setHitLength(0);
			queryAccData.setOrtholog(null);
			queryAccData.setPercentSimilarity(0);
			queryAccData.setProteinCount(0);
			queryAccData.setPriority(null);
			queryAccData.setQuality(null);
			queryAccData.setTemplate(null);
			queryAccData.setSusceptible(null);
			queryAccData.setLevel4RunId(selectedLevelFourRuns.get(0).getLevel4RunId());
			queryRow.setQueryAccessionData(queryAccData);
			if (!tmAlignQueryAccs.contains(queryRow)) {
				tmAlignQueryAccs.add(queryRow);
			}
//		}
		
		
		clearAlpha();
	}
	
	public String formatPDB(String inputPDB) {
		//normalize line separators
//		inputPDB = inputPDB.replaceAll("\\r\\n?", "\n");
//		String[] lines = inputPDB.split("\n");
//		
//		StringBuilder sb = new StringBuilder();
//		for(String line: lines) {
//			if(line.trim().toLowerCase().startsWith("atom") || line.trim().toLowerCase().startsWith("ter")) {
//				sb.append(line.trim());
//				sb.append("\n");
//			}
//		}
		
		
//		return sb.toString();
		return inputPDB;
	}
	
	public void clearAlpha() {
		
		alphaFoldAccInput = "";
		alphaFoldProtInput = "";
		alphaFoldTaxIdInput = "";
		alphaFoldTaxGrpInput = "";
		alphaFoldSciNameInput = "";
		alphaFoldCommonNameInput = "";
		alphaFoldPDBInput = "";
		sourceChoice = "";
		
		selectedAlphaFoldRow = new LevelFourAccessionRow();
	}
	
	public void onAlphaRowSelect(SelectEvent event) {
//		if (prevSelectedAlphaAcc == selectedAlphaFoldRow.getNcbiAccession()) {
//			selectedAlphaFoldRow = new LevelFourAccessionRow();
//		} else {
//			prevSelectedAlphaAcc = selectedAlphaFoldRow.getNcbiAccession();
//		}
		
		alphaFoldAccInput = selectedAlphaFoldRow.getUniprot_acc();
		alphaFoldProtInput = selectedAlphaFoldRow.getProteinName();
		alphaFoldTaxIdInput = String.valueOf(selectedAlphaFoldRow.getSpeciesTaxId());
		alphaFoldTaxGrpInput = selectedAlphaFoldRow.getTaxonomyName();
		alphaFoldSciNameInput = selectedAlphaFoldRow.getScientificName();
		alphaFoldCommonNameInput = selectedAlphaFoldRow.getCommonName();
		alphaFoldPDBInput = selectedAlphaFoldRow.getPdb();
	}
	
	public void onAlphaRowSelectRadio(SelectEvent event) {
		alphaFoldAccInput = selectedAlphaFoldRow.getUniprot_acc();
		alphaFoldProtInput = selectedAlphaFoldRow.getProteinName();
		alphaFoldTaxIdInput = String.valueOf(selectedAlphaFoldRow.getSpeciesTaxId());
		alphaFoldTaxGrpInput = selectedAlphaFoldRow.getTaxonomyName();
		alphaFoldSciNameInput = selectedAlphaFoldRow.getScientificName();
		alphaFoldCommonNameInput = selectedAlphaFoldRow.getCommonName();
		alphaFoldPDBInput = selectedAlphaFoldRow.getPdb();
	}
	
	public void removeTMAlignRow() {
		levelFourTMAlignSelectionReport.removeAll(selectedTMAlignSelectionReportRows);
		selectedTMAlignSelectionReportRows.clear();
	}
	
	public void clearTMAlignRows() {
		levelFourTMAlignSelectionReport.clear();
		selectedTMAlignSelectionReportRows.clear();
	}
	
	public void test() {
		System.out.println("here!!!");
	}
	
	public void changeLevelFourLevel() {
		selectedLevelFourDomain = null;
		int sourceLevel = getSourceLevelInt();
		if (sourceLevel == 1) {
			levelFourDomainVisible = false;
			disableL4PrioritizeBtn = false;
			setupNewLevel4Run();
		} else {
			levelFourDomainVisible = true;
			disableL4PrioritizeBtn = true;
		}
	}
	
	public void changeLevelFourViewLevel() {
		changeLev4JobChoices();
	}
	
	public void selectLevelFourFunctionalDomain() {
		setupNewLevel4Run();
		if (selectedLevelFourDomain != null) {
			disableL4PrioritizeBtn = false;
		} else {
			disableL4PrioritizeBtn = true;
		}
	}
	
	public int getSourceLevelInt() {
		if (chosenLevelFourLevel.equals("Level1")) {
			return 1;
		} else if(chosenLevelFourLevel.equals("Level2")) {
			return 2;
		}
		return 0;
	}
	
	public int getSourceLevelViewInt() {
		if (chosenLevelFourViewLevel.equals("Level1")) {
			return 1;
		} else if(chosenLevelFourViewLevel.equals("Level2")) {
			return 2;
		}
		return 0;
	}
	
	public void concatPDB(LevelFourAccessionRow row) {
		System.out.println("row PDB:" + row.getPdb());
		if (!icn3dPDB.equals("")){
			icn3dPDB = icn3dPDB + "\nENDMDL";
		}
		icn3dPDB = icn3dPDB + row.getPdb();

		PrimeFaces.current().executeScript(String.format("pushToICN3D(`%s`);", icn3dPDB));
		
		}


	public void changeIcn3dBackgroundColor() {
		StringBuilder sb = new StringBuilder();
		sb.append("icn3dui.icn3d.setStyleCls.setBackground('");
		sb.append(itasserBackground);
		sb.append("');");
		PrimeFaces.current().executeScript(sb.toString());
	}
	
	public void clearIcn3dSelection() {
		icn3dPDB = "";  //reset current java string
	}
	
	public void resizePopup(){
		StringBuilder sb = new StringBuilder();
		sb.append("moveIcn3d2pop(");
		sb.append(icn3dPopupSize);
		sb.append(");");
		sb.append("positionPopup();");
		PrimeFaces.current().executeScript(sb.toString());
	}

	public void viewIcn3dChainsList() {
		Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String arrayChains = parameterMap.get("chainNames");
		icn3dChains = Arrays.asList(arrayChains.split(":"));
	}
	
	public void superposeIcn3dChains() {
		String strIcn3dChains = String.join(",", selectedIcn3dChains);
		StringBuilder sb = new StringBuilder();
		sb.append("icn3dSuperpose('");
		sb.append(strIcn3dChains);
		sb.append("');");
		PrimeFaces.current().executeScript(sb.toString());
	}
	
	
	public void switchToL4() {
		//PrimeFaces.current().executeScript(String.format("resetICN3D(`%s`);", icn3dPDB));
//		PrimeFaces.current().executeScript("resetICN3D();");
//		PrimeFaces.current().executeScript(String.format("pushToICN3D(`%s`);", icn3dPDB));
//		PrimeFaces.current().executeScript("clearICN3D();");
		PrimeFaces.current().executeScript(String.format("pushToICN3D(`%s`);", icn3dPDB));
	}
	
	public void reloadL4() {
		if (selectedPageURL.equals("levelFourReport.xhtml")) {
			PrimeFaces.current().executeScript(String.format("pushToICN3D(`%s`);", icn3dPDB));
		}
	}
	
	public void clearPDBStr() {
		icn3dPDB="";
	}
	
	public void icn3dReload() {
		icn3dPage = "icn3d.html";
	}
	
	public void closeIcn3dDialogs() {
		if (selectedPageURL.equals("levelFourReport.xhtml")) {
			PrimeFaces.current().executeScript("closeIcn3dDialogs();");		}
	}
	
	public void resetDefaultIcn3dBackground() {
		itasserBackground = "black";
	}
	
	/***********************************************************
	 * Getter and Setter Methods
	 ********************************************************/

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}

	public boolean getHighlightPartials() {
		return highlightPartials;
	}

	public void setHighlightPartials(boolean highlightPartials) {
		this.highlightPartials = highlightPartials;
	}

	public boolean isRenderLevelOne() {
		return renderLevelOne;
	}

	public void setRenderLevelOne(boolean renderLevelOne) {
		this.renderLevelOne = renderLevelOne;
	}

	public boolean isRenderLevelTwo() {
		return renderLevelTwo;
	}

	public void setRenderLevelTwo(boolean renderLevelTwo) {
		this.renderLevelTwo = renderLevelTwo;
	}

	public boolean isRenderLevelThree() {
		return renderLevelThree;
	}

	public void setRenderLevelThree(boolean renderLevelThree) {
		this.renderLevelThree = renderLevelThree;
	}

	public String getSelectedPageURL() {
		return selectedPageURL;
	}

	public void setSelectedPageURL(String selectedPageURL) {
		this.selectedPageURL = selectedPageURL;
	}

	public List<LevelOneReportRow> getLevelOneReport() {
		return levelOneReport;
	}

	public void setLevelOneReport(List<LevelOneReportRow> levelOneReport) {
		this.levelOneReport = levelOneReport;
	}

	public List<LevelOneReportRow> getLevelOnePrimaryReport() {
		return levelOnePrimaryReport;
	}

	public void setLevelOnePrimaryReport(List<LevelOneReportRow> levelOnePrimaryReport) {
		this.levelOnePrimaryReport = levelOnePrimaryReport;
	}

	public String getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(String selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public String getSelectedTaxGroup() {
		return selectedTaxGroup;
	}

	public void setSelectedTaxGroup(String selectedTaxGroup) {
		this.selectedTaxGroup = selectedTaxGroup;
	}

	public String getSelectedLevel1Action() {
		return selectedLevel1Action;
	}

	public void setSelectedLevel1Action(String selectedLevel1Action) {
		this.selectedLevel1Action = selectedLevel1Action;
	}

	public String[] getSelectedLevel1Highlights() {
		return selectedLevel1Highlights;
	}

	public void setSelectedLevel1Highlights(String[] selectedLevel1Highlights) {
		this.selectedLevel1Highlights = selectedLevel1Highlights;
	}

	public ReportTypeEnum getLevelOneReportType() {
		return levelOneReportType;
	}

	public void setLevelOneReportType(ReportTypeEnum levelOneReportType) {
		this.levelOneReportType = levelOneReportType;
	}

	public String getLevelOneReportPage() {
		return levelOneReportPage;
	}

	public void setLevelOneReportPage(String levelOneReportPage) {
		this.levelOneReportPage = levelOneReportPage;
	}

	public String getChosenLevel2Cutoff() {
		return chosenLevel2Cutoff;
	}

	public void setChosenLevel2Cutoff(String chosenLevel2Cutoff) {
		this.chosenLevel2Cutoff = chosenLevel2Cutoff;
	}

	public ReportTypeEnum getLevelTwoReportType() {
		return levelTwoReportType;
	}

	public void setLevelTwoReportType(ReportTypeEnum levelTwoReportType) {
		this.levelTwoReportType = levelTwoReportType;
	}

	public String getLevelTwoReportPage() {
		return levelTwoReportPage;
	}

	public void setLevelTwoReportPage(String levelTwoReportPage) {
		this.levelTwoReportPage = levelTwoReportPage;
	}

	public List<LevelTwoReportRow> getLevelTwoReport() {
		return levelTwoReport;
	}

	public void setLevelTwoReport(List<LevelTwoReportRow> levelTwoReport) {
		this.levelTwoReport = levelTwoReport;
	}

	public List<LevelThreeReportRow> getLevelThreeReport() {
		return levelThreeReport;
	}

	public void setLevelThreeReport(List<LevelThreeReportRow> levelThreeReport) {
		this.levelThreeReport = levelThreeReport;
	}

	public String getSelectedLevel2Action() {
		return selectedLevel2Action;
	}

	public void setSelectedLevel2Action(String selectedLevel2Action) {
		this.selectedLevel2Action = selectedLevel2Action;
	}

	public String getSelectedLevel3Action() {
		return selectedLevel3Action;
	}

	public void setSelectedLevel3Action(String selectedLevel3Action) {
		this.selectedLevel3Action = selectedLevel3Action;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public int getOrtholog_count() {
		return ortholog_count;
	}

	public void setOrtholog_count(int ortholog_count) {
		this.ortholog_count = ortholog_count;
	}

	public int getFull_ortholog_count() {
		return full_ortholog_count;
	}

	public void setFull_ortholog_count(int full_ortholog_count) {
		this.full_ortholog_count = full_ortholog_count;
	}

	public int getPrimary_ortholog_count() {
		return primary_ortholog_count;
	}

	public void setPrimary_ortholog_count(int primary_ortholog_count) {
		this.primary_ortholog_count = primary_ortholog_count;
	}

	public Date getNcbiDate() {
		return ncbiDate;
	}

	public void setNcbiDate(Date ncbiDate) {
		this.ncbiDate = ncbiDate;
	}

	public Date getCddDate() {
		return cddDate;
	}

	public void setCddDate(Date cddDate) {
		this.cddDate = cddDate;
	}

	public Date getCobaltDate() {
		return cobaltDate;
	}

	public void setCobaltDate(Date cobaltDate) {
		this.cobaltDate = cobaltDate;
	}

	public Date getLevelOneReportDate() {
		return levelOneReportDate;
	}

	public void setLevelOneReportDate(Date levelOneReportDate) {
		this.levelOneReportDate = levelOneReportDate;
	}

	public String getQuerySpecies() {
		return querySpecies;
	}

	public void setQuerySpecies(String querySpecies) {
		this.querySpecies = querySpecies;
	}

	public String getQueryProtein() {
		return queryProtein;
	}

	public void setQueryProtein(String queryProtein) {
		this.queryProtein = queryProtein;
	}

	public String getReportOptionPage() {
		return reportOptionPage;
	}

	public void setReportOptionPage(String reportOptionPage) {
		this.reportOptionPage = reportOptionPage;
	}

	public boolean isHighlightPercSim() {
		return highlightPercSim;
	}

	public void setHighlightPercSim(boolean highlightPercSim) {
		this.highlightPercSim = highlightPercSim;
	}

	public boolean isHighlightZeroOrtho() {
		return highlightZeroOrtho;
	}

	public void setHighlightZeroOrtho(boolean highlightZeroOrtho) {
		this.highlightZeroOrtho = highlightZeroOrtho;
	}

	public int getAccessionRunId() {
		return accessionRunId;
	}

	public void setAccessionRunId(int accessionRunId) {
		this.accessionRunId = accessionRunId;
	}

	public LineChartModel getLevelOnePrimaryDensityModel() {
		return levelOnePrimaryDensityModel;
	}

	public void setLevelOnePrimaryDensityModel(LineChartModel levelOnePrimaryDensityModel) {
		this.levelOnePrimaryDensityModel = levelOnePrimaryDensityModel;
	}

	public List<Double> getLevelOnePrimaryCutoffs() {
		return levelOnePrimaryCutoffs;
	}

	public void setLevelOnePrimaryCutoffs(List<Double> levelOnePrimaryCutoffs) {
		this.levelOnePrimaryCutoffs = levelOnePrimaryCutoffs;
	}

	public CutoffData getLevelOnePrimaryCutData() {
		return levelOnePrimaryCutData;
	}

	public void setLevelOnePrimaryCutData(CutoffData levelOnePrimaryCutData) {
		this.levelOnePrimaryCutData = levelOnePrimaryCutData;
	}

	public StreamedContent getLevelOneDensityStream() {
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		try {
			if (levelOneReportType.equals(ReportTypeEnum.Primary)) {
				ChartUtilities.writeChartAsPNG(out2, getLevelOnePrimaryChartPanel().getChart(), 560, 367);
			} else {
				ChartUtilities.writeChartAsPNG(out2, getLevelOneFullChartPanel().getChart(), 560, 367);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream inStream = new ByteArrayInputStream(out2.toByteArray());
		// return new DefaultStreamedContent(inStream, "image/png");
		return DefaultStreamedContent.builder().contentType("image/png").stream(() -> inStream).build();
	}

	public List<LevelOneReportRow> getFilteredLevelOneReport() {
		if (filteredLevelOneReport != null) {
			if (filteredLevelOneReport.contains(levelOneFullFirstRow)) {
				filteredLevelOneReport.remove(levelOneFullFirstRow);
				filteredLevelOneReport.add(0, levelOneFullFirstRow);
			}
		}
		return filteredLevelOneReport;
	}

	public void setFilteredLevelOneReport(List<LevelOneReportRow> filteredLevelOneReport) {
		this.filteredLevelOneReport = filteredLevelOneReport;
	}

	public List<LevelOneReportRow> getFilteredLevelOnePrimaryReport() {
		if (filteredLevelOnePrimaryReport != null) {
			if (filteredLevelOnePrimaryReport.contains(levelOnePrimaryFirstRow)) {
				filteredLevelOnePrimaryReport.remove(levelOnePrimaryFirstRow);
				filteredLevelOnePrimaryReport.add(0, levelOnePrimaryFirstRow);
			}
		}
		return filteredLevelOnePrimaryReport;
	}

	public void setFilteredLevelOnePrimaryReport(List<LevelOneReportRow> filteredLevelOnePrimaryReport) {
		this.filteredLevelOnePrimaryReport = filteredLevelOnePrimaryReport;
	}

	public List<LevelTwoReportRow> getFilteredLevelTwoReport() {
		if (filteredLevelTwoReport != null) {
			if (filteredLevelTwoReport.contains(levelTwoFullFirstRow)) {
				filteredLevelTwoReport.remove(levelTwoFullFirstRow);
				filteredLevelTwoReport.add(0, levelTwoFullFirstRow);
			}
		}
		return filteredLevelTwoReport;
	}

	public void setFilteredLevelTwoReport(List<LevelTwoReportRow> filteredLevelTwoReport) {
		this.filteredLevelTwoReport = filteredLevelTwoReport;
	}

	public List<LevelTwoReportRow> getFilteredLevelTwoPrimaryReport() {
		if (filteredLevelTwoPrimaryReport != null) {
			if (filteredLevelTwoPrimaryReport.contains(levelTwoPrimaryFirstRow)) {
				filteredLevelTwoPrimaryReport.remove(levelTwoPrimaryFirstRow);
				filteredLevelTwoPrimaryReport.add(0, levelTwoPrimaryFirstRow);
			}
		}
		return filteredLevelTwoPrimaryReport;
	}

	public void setFilteredLevelTwoPrimaryReport(List<LevelTwoReportRow> filteredLevelTwoPrimaryReport) {
		this.filteredLevelTwoPrimaryReport = filteredLevelTwoPrimaryReport;
	}

	public List<ReportRow> getFilteredReportList() {
		return filteredReportList;
	}

	public void setFilteredReportList(List<ReportRow> filteredReportList) {
		this.filteredReportList = filteredReportList;
	}

	public List<LevelTwoRequestableRow> getLevelTwoDomains() {
		return levelTwoDomains;
	}

	public void setLevelTwoDomains(List<LevelTwoRequestableRow> levelTwoDomains) {
		this.levelTwoDomains = levelTwoDomains;
	}

	public LevelTwoRequestableRow getSelectedDomain() {
		return selectedDomain;
	}

	public void setSelectedDomain(LevelTwoRequestableRow selectedDomain) {
		this.selectedDomain = selectedDomain;
	}

	public int getChosenLevelOneCutoffOption() {
		return chosenLevelOneCutoffOption;
	}

	public void setChosenLevelOneCutoffOption(int chosenLevelOneCutoffOption) {
		this.chosenLevelOneCutoffOption = chosenLevelOneCutoffOption;
	}

	public double getLevelOnePrimaryCutValue() {
		return levelOnePrimaryCutValue;
	}

	public void setLevelOnePrimaryCutValue(double levelOnePrimaryCutValue) {
		this.levelOnePrimaryCutValue = levelOnePrimaryCutValue;
	}

	public ChartPanel getLevelOnePrimaryChartPanel() {
		return levelOnePrimaryChartPanel;
	}

	public void setLevelOnePrimaryChartPanel(ChartPanel levelOnePrimaryChartPanel) {
		this.levelOnePrimaryChartPanel = levelOnePrimaryChartPanel;
	}

	public LineChartModel getLevelTwoDensityModel() {
		return levelTwoDensityModel;
	}

	public void setLevelTwoDensityModel(LineChartModel levelTwoDensityModel) {
		this.levelTwoDensityModel = levelTwoDensityModel;
	}

	public ChartPanel getLevelTwoChartPanel() {
		return levelTwoChartPanel;
	}

	public void setLevelTwoChartPanel(ChartPanel levelTwoChartPanel) {
		this.levelTwoChartPanel = levelTwoChartPanel;
	}

	public StreamedContent getLevelTwoDensityStream() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			if (levelTwoReportType.equals(ReportTypeEnum.Primary)) {
				ChartUtilities.writeChartAsPNG(out, getLevelTwoPrimaryChartPanel().getChart(), 560, 367);
			} else {
				ChartUtilities.writeChartAsPNG(out, getLevelTwoFullChartPanel().getChart(), 560, 367);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStream inStream = new ByteArrayInputStream(out.toByteArray());
		// return new DefaultStreamedContent(inStream, "image/png");
		return DefaultStreamedContent.builder().contentType("image/png").stream(() -> inStream).build();
	}

	// public void setLevelTwoDensityStream(StreamedContent
	// levelTwoDensityStream) {
	// this.levelTwoDensityStream = levelTwoDensityStream;
	// }

	public List<Double> getLevelTwoCutoffs() {
		return levelTwoCutoffs;
	}

	public void setLevelTwoCutoffs(List<Double> levelTwoCutoffs) {
		this.levelTwoCutoffs = levelTwoCutoffs;
	}

	public CutoffData getLevelTwoCutData() {
		return levelTwoCutData;
	}

	public void setLevelTwoCutData(CutoffData levelTwoCutData) {
		this.levelTwoCutData = levelTwoCutData;
	}

	public int getChosenLevelTwoCutoffOption() {
		return chosenLevelTwoCutoffOption;
	}

	public void setChosenLevelTwoCutoffOption(int chosenLevelTwoCutoffOption) {
		this.chosenLevelTwoCutoffOption = chosenLevelTwoCutoffOption;
	}

	public double getLevelTwoCutValue() {
		return levelTwoCutValue;
	}

	public void setLevelTwoCutValue(double levelTwoCutValue) {
		this.levelTwoCutValue = levelTwoCutValue;
	}

	public List<LevelThreeReportRow> getFilteredLevelThreeReport() {
		return filteredLevelThreeReport;
	}

	public void setFilteredLevelThreeReport(List<LevelThreeReportRow> filteredLevelThreeReport) {
		this.filteredLevelThreeReport = filteredLevelThreeReport;
	}

	public List<LevelTwoRequestableRow> getCompletedLevelTwoDomains() {
		return completedLevelTwoDomains;
	}

	public void setCompletedLevelTwoDomains(List<LevelTwoRequestableRow> completedLevelTwoDomains) {
		this.completedLevelTwoDomains = completedLevelTwoDomains;
	}

	public LevelTwoRequestableRow getSelectedCompletedDomain() {
		return selectedCompletedDomain;
	}

	public void setSelectedCompletedDomain(LevelTwoRequestableRow selectedCompletedDomain) {
		this.selectedCompletedDomain = selectedCompletedDomain;
	}

	public boolean isRenderLevelButtons() {
		return renderLevelButtons;
	}

	public void setRenderLevelButtons(boolean renderLevelButtons) {
		this.renderLevelButtons = renderLevelButtons;
	}

	public LevelThreeRequestableRow getSelectedCompletedLevel3() {
		return selectedCompletedLevel3;
	}

	public void setSelectedCompletedLevel3(LevelThreeRequestableRow selectedCompletedLevel3) {
		this.selectedCompletedLevel3 = selectedCompletedLevel3;
	}

	public List<String> getResidueHeaders() {
		return residueHeaders;
	}

	public void setResidueHeaders(List<String> residueHeaders) {
		this.residueHeaders = residueHeaders;
	}

	public String getTemplateText() {
		return templateText;
	}

	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}

	public List<LevelThreeRequestableRow> getCompletedLevelThreeRuns() {
		return completedLevelThreeRuns;
	}

	public void setCompletedLevelThreeRuns(List<LevelThreeRequestableRow> completedLevelThreeRuns) {
		this.completedLevelThreeRuns = completedLevelThreeRuns;
	}

	public String getResiduePositionText() {
		return residuePositionText;
	}

	public void setResiduePositionText(String residuePositionText) {
		this.residuePositionText = residuePositionText;
	}

	public int getLevelThreeTaxGroup() {
		return levelThreeTaxGroup;
	}

	public void setLevelThreeTaxGroup(int levelThreeTaxGroup) {
		this.levelThreeTaxGroup = levelThreeTaxGroup;
	}

	// public Map<Integer, String> getTaxGroups() {
	// return taxGroups;
	// }
	//
	// public void setTaxGroups(Map<Integer, String> taxGroups) {
	// this.taxGroups = taxGroups;
	// }

	public String getSelectedCompletedDomainId() {
		return selectedCompletedDomainId;
	}

	public void setSelectedCompletedDomainId(String selectedCompletedDomainId) {
		this.selectedCompletedDomainId = selectedCompletedDomainId;
	}

	public String getSelectedCompletedDomainName() {
		return selectedCompletedDomainName;
	}

	public void setSelectedCompletedDomainName(String selectedCompletedDomainName) {
		this.selectedCompletedDomainName = selectedCompletedDomainName;
	}

	public List<LevelOneReportRow> getSelectedLevelOneRows() {
		return selectedLevelOneRows;
	}

	public void setSelectedLevelOneRows(List<LevelOneReportRow> selectedLevelOneRows) {
		this.selectedLevelOneRows = selectedLevelOneRows;
	}

	public List<LevelOneReportRow> getSelectedLevelOnePrimaryRows() {
		return selectedLevelOnePrimaryRows;
	}

	public void setSelectedLevelOnePrimaryRows(List<LevelOneReportRow> selectedLevelOnePrimaryRows) {
		this.selectedLevelOnePrimaryRows = selectedLevelOnePrimaryRows;
	}

	public String getLevel3JobName() {
		return level3JobName;
	}

	public void setLevel3JobName(String level3JobName) {
		this.level3JobName = level3JobName;
	}

	public List<String> getSelectedAccessionIdStrings() {
		return selectedAccessionIdStrings;
	}

	public void setSelectedAccessionIdStrings(List<String> selectedAccessionIdStrings) {
		this.selectedAccessionIdStrings = selectedAccessionIdStrings;
	}

	public String getLevelOneFilterString() {
		return levelOneFilterString;
	}

	public void setLevelOneFilterString(String levelOneFilterString) {
		this.levelOneFilterString = levelOneFilterString;
	}

	public String getTaxFilterVal() {
		return taxFilterVal;
	}

	public void setTaxFilterVal(String taxFilterVal) {
		this.taxFilterVal = taxFilterVal;
	}

	public DualListModel<String> getLevelThreePickList() {
		return levelThreePickList;
	}

	public void setLevelThreePickList(DualListModel<String> levelThreePickList) {
		this.levelThreePickList = levelThreePickList;
	}

	public double getUserDefinedLevOneCut() {
		return userDefinedLevOneCut;
	}

	public void setUserDefinedLevOneCut(double userDefinedLevOneCut) {
		this.userDefinedLevOneCut = userDefinedLevOneCut;
	}

	public double getUserDefinedLevTwoCut() {
		return userDefinedLevTwoCut;
	}

	public void setUserDefinedLevTwoCut(double userDefinedLevTwoCut) {
		this.userDefinedLevTwoCut = userDefinedLevTwoCut;
	}

	public LevelOneReportRow getLevelOneFullFirstRow() {
		return levelOneFullFirstRow;
	}

	public void setLevelOneFullFirstRow(LevelOneReportRow levelOneFullFirstRow) {
		this.levelOneFullFirstRow = levelOneFullFirstRow;
	}

	public String getQueryResiduesText() {
		return queryResiduesText;
	}

	public void setQueryResiduesText(String queryResiduesText) {
		this.queryResiduesText = queryResiduesText;
	}

	public String getLevelOneHeaderText() {
		return levelOneHeaderText;
	}

	public void setLevelOneHeaderText(String levelOneHeaderText) {
		this.levelOneHeaderText = levelOneHeaderText;
	}

	public String getLevelTwoHeaderText() {
		return levelTwoHeaderText;
	}

	public void setLevelTwoHeaderText(String levelTwoHeaderText) {
		this.levelTwoHeaderText = levelTwoHeaderText;
	}

	public List<String> getTaxonomyGroup() {
		return taxonomyGroup;
	}

	public void setTaxonomyGroup(List<String> taxonomyGroup) {
		this.taxonomyGroup = taxonomyGroup;
	}

	public List<String> getTaxonomyGroup2() {
		return taxonomyGroup2;
	}

	public void setTaxonomyGroup2(List<String> taxonomyGroup2) {
		this.taxonomyGroup2 = taxonomyGroup2;
	}

	public LevelTwoReportRow getLevelTwoFullFirstRow() {
		return levelTwoFullFirstRow;
	}

	public void setLevelTwoFullFirstRow(LevelTwoReportRow levelTwoFullFirstRow) {
		this.levelTwoFullFirstRow = levelTwoFullFirstRow;
	}

	public String getTopHitAccession() {
		return topHitAccession;
	}

	public void setTopHitAccession(String topHitAccession) {
		this.topHitAccession = topHitAccession;
	}

	public boolean isDisableRequestDomainButton() {
		return disableRequestDomainButton;
	}

	public void setDisableRequestDomainButton(boolean disableRequestDomainButton) {
		this.disableRequestDomainButton = disableRequestDomainButton;
	}

	public String getSelectedCompletedDomainLoc() {
		return selectedCompletedDomainLoc;
	}

	public void setSelectedCompletedDomainLoc(String selectedCompletedDomainLoc) {
		this.selectedCompletedDomainLoc = selectedCompletedDomainLoc;
	}

	public String getSelectedCompletedDomainInfo() {
		return selectedCompletedDomainInfo;
	}

	public void setSelectedCompletedDomainInfo(String selectedCompletedDomainInfo) {
		this.selectedCompletedDomainInfo = selectedCompletedDomainInfo;
	}

	public double getPrimaryLevOneEvalueLimit() {
		return primaryLevOneEvalueLimit;
	}

	public void setPrimaryLevOneEvalueLimit(double primaryLevOneEvalueLimit) {
		this.primaryLevOneEvalueLimit = primaryLevOneEvalueLimit;
	}

	public int getPrimaryLevOneCommonDomainLimit() {
		return primaryLevOneCommonDomainLimit;
	}

	public void setPrimaryLevOneCommonDomainLimit(int primaryLevOneCommonDomainLimit) {
		this.primaryLevOneCommonDomainLimit = primaryLevOneCommonDomainLimit;
	}

	public int getSelectedSpeciesCount() {
		return selectedSpeciesCount;
	}

	public void setSelectedSpeciesCount(int selectedSpeciesCount) {
		this.selectedSpeciesCount = selectedSpeciesCount;
	}

	public int getFull_level2_ortholog_count() {
		return full_level2_ortholog_count;
	}

	public void setFull_level2_ortholog_count(int full_level2_ortholog_count) {
		this.full_level2_ortholog_count = full_level2_ortholog_count;
	}

	public LevelThreeRequestableRow getSelectedLevel3Info() {
		return selectedLevel3Info;
	}

	public void setSelectedLevel3Info(LevelThreeRequestableRow selectedLevel3Info) {
		this.selectedLevel3Info = selectedLevel3Info;
	}

	public String getBlastVersion() {
		return blastVersion;
	}

	public void setBlastVersion(String blastVersion) {
		this.blastVersion = blastVersion;
	}

	public String getCobaltVersion() {
		return cobaltVersion;
	}

	public void setCobaltVersion(String cobaltVersion) {
		this.cobaltVersion = cobaltVersion;
	}

	public String getMainReportButtonText() {
		return mainReportButtonText;
	}

	public void setMainReportButtonText(String mainReportButtonText) {
		this.mainReportButtonText = mainReportButtonText;
	}

	public List<ReportRow> getSelectedReports() {
		return selectedReports;
	}

	public void setSelectedReports(List<ReportRow> selectedReports) {
		this.selectedReports = selectedReports;
	}

	// public void convertToDownloadList(List<ReportRow> reports){
	// downloadList.clear();
	// for (ReportRow row: reports){
	// ReportRow dRow = new ReportRow(row);
	// downloadList.add(dRow);
	// }
	// }

	public List<ReportRow> getFilteredDownloadList() {
		return filteredDownloadList;
	}

	public void setFilteredDownloadList(List<ReportRow> filteredDownloadList) {
		this.filteredDownloadList = filteredDownloadList;
	}

	public boolean isLevelOneSpeciesReadAcross() {
		return levelOneSpeciesReadAcross;
	}

	public void setLevelOneSpeciesReadAcross(boolean levelOneSpeciesReadAcross) {
		this.levelOneSpeciesReadAcross = levelOneSpeciesReadAcross;
	}

	public String getLevOnePrimaryTaxGroup() {
		return levOnePrimaryTaxGroup;
	}

	public void setLevOnePrimaryTaxGroup(String levOnePrimaryTaxGroup) {
		this.levOnePrimaryTaxGroup = levOnePrimaryTaxGroup;
	}

	public LevelOneReportRow getLevelOnePrimaryFirstRow() {
		return levelOnePrimaryFirstRow;
	}

	public void setLevelOnePrimaryFirstRow(LevelOneReportRow levelOnePrimaryFirstRow) {
		this.levelOnePrimaryFirstRow = levelOnePrimaryFirstRow;
	}

	public LevelTwoReportRow getLevelTwoPrimaryFirstRow() {
		return levelTwoPrimaryFirstRow;
	}

	public void setLevelTwoPrimaryFirstRow(LevelTwoReportRow levelTwoPrimaryFirstRow) {
		this.levelTwoPrimaryFirstRow = levelTwoPrimaryFirstRow;
	}

	public double getPrimaryLevTwoEvalueLimit() {
		return primaryLevTwoEvalueLimit;
	}

	public void setPrimaryLevTwoEvalueLimit(double primaryLevTwoEvalueLimit) {
		this.primaryLevTwoEvalueLimit = primaryLevTwoEvalueLimit;
	}

	public boolean isLevelTwoSpeciesReadAcross() {
		return levelTwoSpeciesReadAcross;
	}

	public void setLevelTwoSpeciesReadAcross(boolean levelTwoSpeciesReadAcross) {
		this.levelTwoSpeciesReadAcross = levelTwoSpeciesReadAcross;
	}

	public String getLevTwoPrimaryTaxGroup() {
		return levTwoPrimaryTaxGroup;
	}

	public void setLevTwoPrimaryTaxGroup(String levTwoPrimaryTaxGroup) {
		this.levTwoPrimaryTaxGroup = levTwoPrimaryTaxGroup;
	}

	public List<LevelTwoReportRow> getLevelTwoPrimaryReport() {
		return levelTwoPrimaryReport;
	}

	public void setLevelTwoPrimaryReport(List<LevelTwoReportRow> levelTwoPrimaryReport) {
		this.levelTwoPrimaryReport = levelTwoPrimaryReport;
	}

	public int getPrimary_level2_ortholog_count() {
		return primary_level2_ortholog_count;
	}

	public void setPrimary_level2_ortholog_count(int primary_level2_ortholog_count) {
		this.primary_level2_ortholog_count = primary_level2_ortholog_count;
	}

	public int getLevel2_ortholog_count() {
		return level2_ortholog_count;
	}

	public void setLevel2_ortholog_count(int level2_ortholog_count) {
		this.level2_ortholog_count = level2_ortholog_count;
	}

	public CutoffData getLevelOneFullCutData() {
		return levelOneFullCutData;
	}

	public void setLevelOneFullCutData(CutoffData levelOneFullCutData) {
		this.levelOneFullCutData = levelOneFullCutData;
	}

	public double getLevelOneFullCutValue() {
		return levelOneFullCutValue;
	}

	public void setLevelOneFullCutValue(double levelOneFullCutValue) {
		this.levelOneFullCutValue = levelOneFullCutValue;
	}

	public List<Double> getLevelOneFullCutoffs() {
		return levelOneFullCutoffs;
	}

	public void setLevelOneFullCutoffs(List<Double> levelOneFullCutoffs) {
		this.levelOneFullCutoffs = levelOneFullCutoffs;
	}

	public LineChartModel getLevelOneFullDensityModel() {
		return levelOneFullDensityModel;
	}

	public void setLevelOneFullDensityModel(LineChartModel levelOneFullDensityModel) {
		this.levelOneFullDensityModel = levelOneFullDensityModel;
	}

	public ChartPanel getLevelOneFullChartPanel() {
		return levelOneFullChartPanel;
	}

	public void setLevelOneFullChartPanel(ChartPanel levelOneFullChartPanel) {
		this.levelOneFullChartPanel = levelOneFullChartPanel;
	}

	public double getLevelOneCutValue() {
		return levelOneCutValue;
	}

	public void setLevelOneCutValue(double levelOneCutValue) {
		this.levelOneCutValue = levelOneCutValue;
	}

	public LineChartModel getLevelOneDensityModel() {
		return levelOneDensityModel;
	}

	public void setLevelOneDensityModel(LineChartModel levelOneDensityModel) {
		this.levelOneDensityModel = levelOneDensityModel;
	}

	public List<Double> getLevelOneCutoffs() {
		return levelOneCutoffs;
	}

	public void setLevelOneCutoffs(List<Double> levelOneCutoffs) {
		this.levelOneCutoffs = levelOneCutoffs;
	}

	public CutoffData getLevelOneCutData() {
		return levelOneCutData;
	}

	public void setLevelOneCutData(CutoffData levelOneCutData) {
		this.levelOneCutData = levelOneCutData;
	}

	// public boolean isResetLevOneToPrimary() {
	// return resetLevOneToPrimary;
	// }
	//
	// public void setResetLevOneToPrimary(boolean resetLevOneToPrimary) {
	// this.resetLevOneToPrimary = resetLevOneToPrimary;
	// }

	public LineChartModel getLevelTwoPrimaryDensityModel() {
		return levelTwoPrimaryDensityModel;
	}

	public void setLevelTwoPrimaryDensityModel(LineChartModel levelTwoPrimaryDensityModel) {
		this.levelTwoPrimaryDensityModel = levelTwoPrimaryDensityModel;
	}

	public LineChartModel getLevelTwoFullDensityModel() {
		return levelTwoFullDensityModel;
	}

	public void setLevelTwoFullDensityModel(LineChartModel levelTwoFullDensityModel) {
		this.levelTwoFullDensityModel = levelTwoFullDensityModel;
	}

	public ChartPanel getLevelTwoPrimaryChartPanel() {
		return levelTwoPrimaryChartPanel;
	}

	public void setLevelTwoPrimaryChartPanel(ChartPanel levelTwoPrimaryChartPanel) {
		this.levelTwoPrimaryChartPanel = levelTwoPrimaryChartPanel;
	}

	public ChartPanel getLevelTwoFullChartPanel() {
		return levelTwoFullChartPanel;
	}

	public void setLevelTwoFullChartPanel(ChartPanel levelTwoFullChartPanel) {
		this.levelTwoFullChartPanel = levelTwoFullChartPanel;
	}

	public List<Double> getLevelTwoPrimaryCutoffs() {
		return levelTwoPrimaryCutoffs;
	}

	public void setLevelTwoPrimaryCutoffs(List<Double> levelTwoPrimaryCutoffs) {
		this.levelTwoPrimaryCutoffs = levelTwoPrimaryCutoffs;
	}

	public List<Double> getLevelTwoFullCutoffs() {
		return levelTwoFullCutoffs;
	}

	public void setLevelTwoFullCutoffs(List<Double> levelTwoFullCutoffs) {
		this.levelTwoFullCutoffs = levelTwoFullCutoffs;
	}

	public CutoffData getLevelTwoPrimaryCutData() {
		return levelTwoPrimaryCutData;
	}

	public void setLevelTwoPrimaryCutData(CutoffData levelTwoPrimaryCutData) {
		this.levelTwoPrimaryCutData = levelTwoPrimaryCutData;
	}

	public CutoffData getLevelTwoFullCutData() {
		return levelTwoFullCutData;
	}

	public void setLevelTwoFullCutData(CutoffData levelTwoFullCutData) {
		this.levelTwoFullCutData = levelTwoFullCutData;
	}

	public double getLevelTwoPrimaryCutValue() {
		return levelTwoPrimaryCutValue;
	}

	public void setLevelTwoPrimaryCutValue(double levelTwoPrimaryCutValue) {
		this.levelTwoPrimaryCutValue = levelTwoPrimaryCutValue;
	}

	public double getLevelTwoFullCutValue() {
		return levelTwoFullCutValue;
	}

	public void setLevelTwoFullCutValue(double levelTwoFullCutValue) {
		this.levelTwoFullCutValue = levelTwoFullCutValue;
	}

	// public boolean isResetLevTwoToPrimary() {
	// return resetLevTwoToPrimary;
	// }
	//
	// public void setResetLevTwoToPrimary(boolean resetLevTwoToPrimary) {
	// this.resetLevTwoToPrimary = resetLevTwoToPrimary;
	// }

	public ReportInfo getLatestUpdateInfo() {
		return latestUpdateInfo;
	}

	public void setLatestUpdateInfo(ReportInfo latestUpdateInfo) {
		this.latestUpdateInfo = latestUpdateInfo;
	}

	public ReportInfo getCurrentReportInfo() {
		return currentReportInfo;
	}

	public void setCurrentReportInfo(ReportInfo currentReportInfo) {
		this.currentReportInfo = currentReportInfo;
	}

	public boolean isDisableRequestResidueButton() {
		return disableRequestResidueButton;
	}

	public void setDisableRequestResidueButton(boolean disableRequestResidueButton) {
		this.disableRequestResidueButton = disableRequestResidueButton;
	}

	public String getLevelThreeRequestTip() {
		return levelThreeRequestTip;
	}

	public void setLevelThreeRequestTip(String levelThreeRequestTip) {
		this.levelThreeRequestTip = levelThreeRequestTip;
	}

	public boolean isShowLevelThreeRequestTip() {
		return showLevelThreeRequestTip;
	}

	public void setShowLevelThreeRequestTip(boolean showLevelThreeRequestTip) {
		this.showLevelThreeRequestTip = showLevelThreeRequestTip;
	}

	public String getLevelTwoRequestTip() {
		return levelTwoRequestTip;
	}

	public void setLevelTwoRequestTip(String levelTwoRequestTip) {
		this.levelTwoRequestTip = levelTwoRequestTip;
	}

	public boolean getShowLevelTwoRequestTip() {
		return showLevelTwoRequestTip;
	}

	public void setShowLevelTwoRequestTip(boolean b) {
		this.showLevelTwoRequestTip = b;
	}

	public ReportRow getSelectedReport() {
		return selectedReport;
	}

	public void setSelectedReport(ReportRow selectedReport) {
		this.selectedReport = selectedReport;
	}

	public List<ReportRow> getReportList() {
		return reportList;
	}

	public void setReportList(List<ReportRow> reportList) {
		this.reportList = reportList;
	}

	public String getAdditionalComparisonsText() {
		return additionalComparisonsText;
	}

	public void setAdditionalComparisonsText(String additionalComparisonsText) {
		this.additionalComparisonsText = additionalComparisonsText;
	}

	public int getIsDup() {
		return isDup;
	}

	public void setIsDup(int isDup) {
		this.isDup = isDup;
	}

	public String getIsDupMessage() {
		return isDupMessage;
	}

	public void setIsDupMessage(String isDupMessage) {
		this.isDupMessage = isDupMessage;
	}

	public String getSeqapassVersion() {
		return seqapassVersion;
	}

	public void setSeqapassVersion(String seqapassVersion) {
		this.seqapassVersion = seqapassVersion;
	}

	// public boolean isEukaryotesOnly() {
	// return eukaryotesOnly;
	// }
	//
	// public void setEukaryotesOnly(boolean eukaryotesOnly) {
	// this.eukaryotesOnly = eukaryotesOnly;
	// }

	public List<LevelOneReportRow> getDefaultLevelOneReport() {
		return defaultLevelOneReport;
	}

	public void setDefaultLevelOneReport(List<LevelOneReportRow> defaultLevelOneReport) {
		this.defaultLevelOneReport = defaultLevelOneReport;
	}

	public List<LevelTwoReportRow> getDefaultLevelTwoReport() {
		return defaultLevelTwoReport;
	}

	public void setDefaultLevelTwoReport(List<LevelTwoReportRow> defaultLevelTwoReport) {
		this.defaultLevelTwoReport = defaultLevelTwoReport;
	}

	public boolean isMainReportButtonDisabled() {
		return mainReportButtonDisabled;
	}

	public void setMainReportButtonDisabled(boolean mainReportButtonDisabled) {
		this.mainReportButtonDisabled = mainReportButtonDisabled;
	}

	public SpeciesTaxGrouping getLevelOneReportTaxGrouping() {
		return levelOneReportTaxGrouping;
	}

	public void setLevelOneReportTaxGrouping(SpeciesTaxGrouping levelOneReportTaxGrouping) {
		this.levelOneReportTaxGrouping = levelOneReportTaxGrouping;
	}

	public SpeciesTaxGrouping getLevelTwoReportTaxGrouping() {
		return levelTwoReportTaxGrouping;
	}

	public void setLevelTwoReportTaxGrouping(SpeciesTaxGrouping levelTwoReportTaxGrouping) {
		this.levelTwoReportTaxGrouping = levelTwoReportTaxGrouping;
	}

	public String getTemplateSpecies() {
		return templateSpecies;
	}

	public void setTemplateSpecies(String templateSpecies) {
		this.templateSpecies = templateSpecies;
	}

	public String getTemplateProtein() {
		return templateProtein;
	}

	public void setTemplateProtein(String templateProtein) {
		this.templateProtein = templateProtein;
	}

	public List<String> getTaxonomyGroupByPercSim() {
		return taxonomyGroupByPercSim;
	}

	public List<String> getTaxonomyGroup2ByPercSim() {
		return taxonomyGroup2ByPercSim;
	}

	public void setTaxonomyGroupByPercSim(List<String> taxonomyGroupByPercSim) {
		this.taxonomyGroupByPercSim = taxonomyGroupByPercSim;
	}

	public void setTaxonomyGroup2ByPercSim(List<String> taxonomyGroup2ByPercSim) {
		this.taxonomyGroup2ByPercSim = taxonomyGroup2ByPercSim;
	}

	public ReportTypeEnum getLevelThreeReportType() {
		return levelThreeReportType;
	}

	public void setLevelThreeReportType(ReportTypeEnum levelThreeReportType) {
		this.levelThreeReportType = levelThreeReportType;
	}

	public String getLevelThreeReportPage() {
		return levelThreeReportPage;
	}

	public void setLevelThreeReportPage(String levelThreeReportPage) {
		this.levelThreeReportPage = levelThreeReportPage;
	}

	public String getLevelThreeHeaderText() {
		return levelThreeHeaderText;
	}

	public void setLevelThreeHeaderText(String levelThreeHeaderText) {
		this.levelThreeHeaderText = levelThreeHeaderText;
	}

	public List<String> getPrimaryResidueHeaders() {
		return primaryResidueHeaders;
	}

	public void setPrimaryResidueHeaders(List<String> primaryResidueHeaders) {
		this.primaryResidueHeaders = primaryResidueHeaders;
	}

	public List<AminoAcid> getAminoAcidInfo() {
		return AminoAcidInfo;
	}

	public void setAminoAcidInfo(List<AminoAcid> aminoAcidInfo) {
		AminoAcidInfo = aminoAcidInfo;
	}

	public ReportChoiceEnum getChosenMainReportOption() {
		return chosenMainReportOption;
	}

	public void setChosenMainReportOption(ReportChoiceEnum chosenMainReportOption) {
		this.chosenMainReportOption = chosenMainReportOption;
	}

	public String getPositionBoxList() {
		return positionBoxList;
	}

	public void setPositionBoxList(String positionBoxList) {
		this.positionBoxList = positionBoxList;
	}

	public boolean isEukaryotesOnly1() {
		return eukaryotesOnly1;
	}

	public void setEukaryotesOnly1(boolean eukaryotesOnly1) {
		this.eukaryotesOnly1 = eukaryotesOnly1;
	}

	public boolean isEukaryotesOnly2() {
		return eukaryotesOnly2;
	}

	public void setEukaryotesOnly2(boolean eukaryotesOnly2) {
		this.eukaryotesOnly2 = eukaryotesOnly2;
	}

	public LevelTwoRequestableRow getLoadedCompletedDomain() {
		return loadedCompletedDomain;
	}

	public void setLoadedCompletedDomain(LevelTwoRequestableRow loadedCompletedDomain) {
		this.loadedCompletedDomain = loadedCompletedDomain;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getInfoHeaderText() {
		return infoHeaderText;
	}

	public void setInfoHeaderText(String infoHeaderText) {
		this.infoHeaderText = infoHeaderText;
	}

	public List<SummaryReportRow> getLevelOneSummaryReport() {
		return levelOneSummaryReport;
	}

	public void setLevelOneSummaryReport(List<SummaryReportRow> levelOneSummaryReport) {
		this.levelOneSummaryReport = levelOneSummaryReport;
	}

	public List<SummaryReportRow> getLevelTwoSummaryReport() {
		return levelTwoSummaryReport;
	}

	public void setLevelTwoSummaryReport(List<SummaryReportRow> levelTwoSummaryReport) {
		this.levelTwoSummaryReport = levelTwoSummaryReport;
	}

	public List<SummaryReportRow> getLevelThreeSummaryReport() {
		return levelThreeSummaryReport;
	}

	public void setLevelThreeSummaryReport(List<SummaryReportRow> levelThreeSummaryReport) {
		this.levelThreeSummaryReport = levelThreeSummaryReport;
	}

	public List<String> getSummaryResidueHeaders() {
		return summaryResidueHeaders;
	}

	public void setSummaryResidueHeaders(List<String> summaryResidueHeaders) {
		this.summaryResidueHeaders = summaryResidueHeaders;
	}

	public String getRefExplorerAddName() {
		return refExplorerAddName;
	}

	public void setRefExplorerAddName(String refExplorerAddName) {
		this.refExplorerAddName = refExplorerAddName;
	}

	public String getScholarString() {
		return scholarString;
	}

	public void setScholarString(String scholarString) {
		this.scholarString = scholarString;
	}

	public List<String> getProteinXplorerList() {
		return proteinXplorerList;
	}

	public void setProteinXplorerList(List<String> proteinXplorerList) {
		this.proteinXplorerList = proteinXplorerList;
	}

	public String getXplorerSelectedProtein() {
		return xplorerSelectedProtein;
	}

	public void setXplorerSelectedProtein(String xplorerSelectedProtein) {
		this.xplorerSelectedProtein = xplorerSelectedProtein;
	}

	public List<String> getAvailableTemplates() {
		return availableTemplates;
	}

	public void setAvailableTemplates(List<String> availableTemplates) {
		this.availableTemplates = availableTemplates;
	}

	public String getSelectedAssessorTemplate() {
		return selectedAssessorTemplate;
	}

	public void setSelectedAssessorTemplate(String selectedAssessorTemplate) {
		this.selectedAssessorTemplate = selectedAssessorTemplate;
	}

	public List<LevelThreeRequestableRow> getAvailableJobs() {
		return availableJobs;
	}

	public void setAvailableJobs(List<LevelThreeRequestableRow> availableJobs) {
		this.availableJobs = availableJobs;
	}

	public List<LevelThreeRequestableRow> getSelectedAssessorJobs() {
		return selectedAssessorJobs;
	}

	public void setSelectedAssessorJobs(List<LevelThreeRequestableRow> selectedAssessorJobs) {
		this.selectedAssessorJobs = selectedAssessorJobs;
	}

	public boolean isCombinedLevel3Report() {
		return combinedLevel3Report;
	}

	public void setCombinedLevel3Report(boolean combinedLevel3Report) {
		this.combinedLevel3Report = combinedLevel3Report;
	}

	public String getLevelThreeRunName() {
		return levelThreeRunName;
	}

	public void setLevelThreeRunName(String levelThreeRunName) {
		this.levelThreeRunName = levelThreeRunName;
	}

	public StreamedContent getLev1SettingsFile() {
		return lev1SettingsFile;
	}

	public void setLev1SettingsFile(StreamedContent lev1SettingsFile) {
		this.lev1SettingsFile = lev1SettingsFile;
	}

	public StreamedContent getLev2SettingsFile() {
		return lev2SettingsFile;
	}

	public void setLev2SettingsFile(StreamedContent lev2SettingsFile) {
		this.lev2SettingsFile = lev2SettingsFile;
	}

	public StreamedContent getLev3SettingsFile() {
		return lev3SettingsFile;
	}

	public void setLev3SettingsFile(StreamedContent lev3SettingsFile) {
		this.lev3SettingsFile = lev3SettingsFile;
	}

	public boolean isDisableRAReport() {
		return disableRAReport;
	}

	public void setDisableRAReport(boolean disableRAReport) {
		this.disableRAReport = disableRAReport;
	}

	public double getDefaultLevelOneEvalue() {
		return defaultLevelOneEvalue;
	}

	public double getDefaultLevelTwoEvalue() {
		return defaultLevelTwoEvalue;
	}

	public List<String> getChosenQueryResidues() {
		return chosenQueryResidues;
	}

	public void setChosenQueryResidues(List<String> chosenQueryResidues) {
		this.chosenQueryResidues = chosenQueryResidues;
	}

	public boolean isLevel1RAReportDiffers() {
		return level1RAReportDiffers;
	}

	public void setLevel1RAReportDiffers(boolean level1raReportDiffers) {
		level1RAReportDiffers = level1raReportDiffers;
	}

	public boolean isLevel2RAReportDiffers() {
		return level2RAReportDiffers;
	}

	public void setLevel2RAReportDiffers(boolean level2raReportDiffers) {
		level2RAReportDiffers = level2raReportDiffers;
	}

	public boolean isLevel1RABoxplotDiffers() {
		return level1RABoxplotDiffers;
	}

	public void setLevel1RABoxplotDiffers(boolean level1raBoxplotDiffers) {
		level1RABoxplotDiffers = level1raBoxplotDiffers;
	}

	public boolean isLevel2RABoxplotDiffers() {
		return level2RABoxplotDiffers;
	}

	public void setLevel2RABoxplotDiffers(boolean level2raBoxplotDiffers) {
		level2RABoxplotDiffers = level2raBoxplotDiffers;
	}

	public boolean isLevel1RAPushWarning() {
		return level1RAPushWarning;
	}

	public void setLevel1RAPushWarning(boolean level1raPushWarning) {
		level1RAPushWarning = level1raPushWarning;
	}

	public boolean isLevel2RAPushWarning() {
		return level2RAPushWarning;
	}

	public void setLevel2RAPushWarning(boolean level2raPushWarning) {
		level2RAPushWarning = level2raPushWarning;
	}

	public boolean isLevel3RAReportDiffers() {
		return level3RAReportDiffers;
	}

	public void setLevel3RAReportDiffers(boolean level3raReportDiffers) {
		level3RAReportDiffers = level3raReportDiffers;
	}

	public boolean isLevel3RAPushWarning() {
		return level3RAPushWarning;
	}

	public void setLevel3RAPushWarning(boolean level3raPushWarning) {
		level3RAPushWarning = level3raPushWarning;
	}

	public List<RiskAssessorTaxGroup> getEcotoxSpeciesColl() {
		return ecotoxSpeciesColl;
	}

	public void setEcotoxSpeciesColl(List<RiskAssessorTaxGroup> ecotoxSpeciesColl) {
		this.ecotoxSpeciesColl = ecotoxSpeciesColl;
	}

//	public List<String> getEcotoxSelectedTaxGroups() {
//		return ecotoxSelectedTaxGroups;
//	}
//
//	public void setEcotoxSelectedTaxGroups(List<String> ecotoxSelectedTaxGroups) {
//		this.ecotoxSelectedTaxGroups = ecotoxSelectedTaxGroups;
//	}
//
//	public List<String> getEcotoxPrevSelectedTaxGroups() {
//		return ecotoxPrevSelectedTaxGroups;
//	}
//
//	public void setEcotoxPrevSelectedTaxGroups(List<String> ecotoxPrevSelectedTaxGroups) {
//		this.ecotoxPrevSelectedTaxGroups = ecotoxPrevSelectedTaxGroups;
//	}

	public List<RiskAssessorTaxGroup> getEcotoxSelectedSpeciesColl() {
		return ecotoxSelectedSpeciesColl;
	}

	public void setEcotoxSelectedSpeciesColl(List<RiskAssessorTaxGroup> ecotoxSelectedSpeciesColl) {
		this.ecotoxSelectedSpeciesColl = ecotoxSelectedSpeciesColl;
	}

	public RiskAssessorTaxGroup getEcotoxQuerySpecies() {
		return ecotoxQuerySpecies;
	}

	public void setEcotoxQuerySpecies(RiskAssessorTaxGroup ecotoxQuerySpecies) {
		this.ecotoxQuerySpecies = ecotoxQuerySpecies;
	}

	public Integer getEcotoxSpeciesNameType() {
		return ecotoxSpeciesNameType;
	}

	public void setEcotoxSpeciesNameType(Integer ecotoxSpeciesNameType) {
		this.ecotoxSpeciesNameType = ecotoxSpeciesNameType;
	}

	public String getEcotoxSortTaxGroup() {
		return ecotoxSortTaxGroup;
	}

	public void setEcotoxSortTaxGroup(String ecotoxSortTaxGroup) {
		this.ecotoxSortTaxGroup = ecotoxSortTaxGroup;
	}

	public boolean isShowEcotoxSpeciesPanel() {
		return showEcotoxSpeciesPanel;
	}

	public void setShowEcotoxSpeciesPanel(boolean showEcotoxSpeciesPanel) {
		this.showEcotoxSpeciesPanel = showEcotoxSpeciesPanel;
	}

	public Chemical getEcotoxSearchChemical() {
		return ecotoxSearchChemical;
	}

	public void setEcotoxSearchChemical(Chemical ecotoxSearchChemical) {
		this.ecotoxSearchChemical = ecotoxSearchChemical;
	}

	public List<Chemical> getEcotoxChemicalList() {
		return ecotoxChemicalList;
	}

	public void setEcotoxChemicalList(List<Chemical> ecotoxChemicalList) {
		this.ecotoxChemicalList = ecotoxChemicalList;
	}

	public List<Chemical> getEcotoxSelectedChemicalList() {
		return ecotoxSelectedChemicalList;
	}

	public void setEcotoxSelectedChemicalList(List<Chemical> ecotoxSelectedChemicalList) {
		this.ecotoxSelectedChemicalList = ecotoxSelectedChemicalList;
	}

	public List<Chemical> getEcotoxPossibleChemicalList() {
		return ecotoxPossibleChemicalList;
	}

	public void setEcotoxPossibleChemicalList(List<Chemical> ecotoxPossibleChemicalList) {
		this.ecotoxPossibleChemicalList = ecotoxPossibleChemicalList;
	}

	public List<RiskAssessorTaxGroup> getEcotoxTaxGroups() {
		return ecotoxTaxGroups;
	}

	public void setEcotoxTaxGroups(List<RiskAssessorTaxGroup> ecotoxTaxGroups) {
		this.ecotoxTaxGroups = ecotoxTaxGroups;
	}

	public List<RiskAssessorTaxGroup> getEcotoxSelectedTaxGroups() {
		return ecotoxSelectedTaxGroups;
	}

	public void setEcotoxSelectedTaxGroups(List<RiskAssessorTaxGroup> ecotoxSelectedTaxGroups) {
		this.ecotoxSelectedTaxGroups = ecotoxSelectedTaxGroups;
	}

	public List<RiskAssessorTaxGroup> getEcotoxPrevSelectedTaxGroups() {
		return ecotoxPrevSelectedTaxGroups;
	}

	public void setEcotoxPrevSelectedTaxGroups(List<RiskAssessorTaxGroup> ecotoxPrevSelectedTaxGroups) {
		this.ecotoxPrevSelectedTaxGroups = ecotoxPrevSelectedTaxGroups;
	}

	public String getEcotoxURL() {
		return ecotoxURL;
	}

	public void setEcotoxURL(String ecotoxURL) {
		this.ecotoxURL = ecotoxURL;
	}

	public String getLevel4JobName() {
		return level4JobName;
	}

	public void setLevel4JobName(String level4JobName) {
		this.level4JobName = level4JobName;
	}

	public boolean isLevel4JobExists() {
		return level4JobExists;
	}

	public void setLevel4JobExists(boolean level4JobExists) {
		this.level4JobExists = level4JobExists;
	}

	public List<LevelFourAccessionRow> getLevelFourAccessions() {
		return levelFourAccessions;
	}

	public void setLevelFourAccessions(List<LevelFourAccessionRow> levelFourAccessions) {
		this.levelFourAccessions = levelFourAccessions;
	}

	public List<LevelFourAccessionRow> getSelectedLevelFourAccessions() {
		return selectedLevelFourAccessions;
	}

	public void setSelectedLevelFourAccessions(List<LevelFourAccessionRow> selectedLevelFourAccessions) {
		this.selectedLevelFourAccessions = selectedLevelFourAccessions;
	}

	public List<LevelFourAccessionRow> getPrevSelectedLevelFourAccessions() {
		return prevSelectedLevelFourAccessions;
	}

	public void setPrevSelectedLevelFourAccessions(List<LevelFourAccessionRow> prevSelectedLevelFourAccessions) {
		this.prevSelectedLevelFourAccessions = prevSelectedLevelFourAccessions;
	}

	public boolean isLevel4JobSubmitted() {
		return level4JobSubmitted;
	}

	public void setLevel4JobSubmitted(boolean level4JobSubmitted) {
		this.level4JobSubmitted = level4JobSubmitted;
	}

	public List<LevelFourRequestableRow> getCreatedLevelFourRuns() {
		return createdLevelFourRuns;
	}

	public void setCreatedLevelFourRuns(List<LevelFourRequestableRow> createdLevelFourRuns) {
		this.createdLevelFourRuns = createdLevelFourRuns;
	}

	public LevelFourRequestableRow getSelectedCreatedLevelFourRun() {
		return selectedCreatedLevelFourRun;
	}

	public void setSelectedCreatedLevelFourRun(LevelFourRequestableRow selectedCreatedLevelFourRun) {
		this.selectedCreatedLevelFourRun = selectedCreatedLevelFourRun;
	}

	public String getLevelFourTemplate() {
		return levelFourTemplate;
	}

	public void setLevelFourTemplate(String levelFourTemplate) {
		this.levelFourTemplate = levelFourTemplate;
	}

	public StreamedContent getLev4FastaFile() {
		return lev4FastaFile;
	}

	public void setLev4FastaFile(StreamedContent lev4FastaFile) {
		this.lev4FastaFile = lev4FastaFile;
	}

	public List<LevelFourAccessionRow> getLevelFourFASTAs() {
		return levelFourFASTAs;
	}

	public void setLevelFourFASTAs(List<LevelFourAccessionRow> levelFourFASTAs) {
		this.levelFourFASTAs = levelFourFASTAs;
	}

	public List<LevelFourAccessionRow> getSelectedLevelFourFASTAs() {
		return selectedLevelFourFASTAs;
	}

	public void setSelectedLevelFourFASTAs(List<LevelFourAccessionRow> selectedLevelFourFASTAs) {
		this.selectedLevelFourFASTAs = selectedLevelFourFASTAs;
	}

	public List<LevelFourAccessionRow> getPrevSelectedLevelFourFASTAs() {
		return prevSelectedLevelFourFASTAs;
	}

	public void setPrevSelectedLevelFourFASTAs(List<LevelFourAccessionRow> prevSelectedLevelFourFASTAs) {
		this.prevSelectedLevelFourFASTAs = prevSelectedLevelFourFASTAs;
	}

	public List<LevelFourAccessionRow> getFilteredLevelFourAccessions() {
		return filteredLevelFourAccessions;
	}

	public void setFilteredLevelFourAccessions(List<LevelFourAccessionRow> filteredLevelFourAccessions) {
		this.filteredLevelFourAccessions = filteredLevelFourAccessions;
	}

	public List<LevelFourAccessionRow> getFilteredLevelFourFASTAs() {
		return filteredLevelFourFASTAs;
	}

	public void setFilteredLevelFourFASTAs(List<LevelFourAccessionRow> filteredLevelFourFASTAs) {
		this.filteredLevelFourFASTAs = filteredLevelFourFASTAs;
	}

	public boolean isUpdatePriorities() {
		return updatePriorities;
	}

	public void setUpdatePriorities(boolean updatePriorities) {
		this.updatePriorities = updatePriorities;
	}

	public boolean isUpdateFilteredFASTAs() {
		return updateFilteredFASTAs;
	}

	public void setUpdateFilteredFASTAs(boolean updateFilteredFASTAs) {
		this.updateFilteredFASTAs = updateFilteredFASTAs;
	}

	public List<LevelFourRequestableRow> getStartedLevelFourRuns() {
		return startedLevelFourRuns;
	}

	public void setStartedLevelFourRuns(List<LevelFourRequestableRow> startedLevelFourRuns) {
		this.startedLevelFourRuns = startedLevelFourRuns;
	}

	public LevelFourRequestableRow getSelectedStartedLevelFourRun() {
		return selectedStartedLevelFourRun;
	}

	public void setSelectedStartedLevelFourRun(LevelFourRequestableRow selectedStartedLevelFourRun) {
		this.selectedStartedLevelFourRun = selectedStartedLevelFourRun;
	}

	public List<LevelFourAccessionRow> getLevelFourReport() {
		return levelFourReport;
	}

	public void setLevelFourReport(List<LevelFourAccessionRow> levelFourReport) {
		this.levelFourReport = levelFourReport;
	}

	public boolean isRenderLevelFour() {
		return renderLevelFour;
	}

	public void setRenderLevelFour(boolean renderLevelFour) {
		this.renderLevelFour = renderLevelFour;
	}

	public List<LevelFourAccessionRow> getSelectedLevelFourReportRows() {
		return selectedLevelFourReportRows;
	}

	public void setSelectedLevelFourReportRows(List<LevelFourAccessionRow> selectedLevelFourReportRows) {
		this.selectedLevelFourReportRows = selectedLevelFourReportRows;
	}

	public LineChartModel getLevelFourDensityModel() {
		return levelFourDensityModel;
	}

	public void setLevelFourDensityModel(LineChartModel levelFourDensityModel) {
		this.levelFourDensityModel = levelFourDensityModel;
	}

	public ChartPanel getLevelFourChartPanel() {
		return levelFourChartPanel;
	}

	public void setLevelFourChartPanel(ChartPanel levelFourChartPanel) {
		this.levelFourChartPanel = levelFourChartPanel;
	}

	public CutoffData getLevelFourCutData() {
		return levelFourCutData;
	}

	public void setLevelFourCutData(CutoffData levelFourCutData) {
		this.levelFourCutData = levelFourCutData;
	}

	public StreamedContent getLev4PDBFile() {
		return lev4PDBFile;
	}

	public void setLev4PDBFile(StreamedContent lev4pdbFile) {
		lev4PDBFile = lev4pdbFile;
	}

	public LevelFourAccessionRow getSelectedPDBAccessionRow() {
		return selectedPDBAccessionRow;
	}

	public void setSelectedPDBAccessionRow(LevelFourAccessionRow selectedPDBAccessionRow) {
		this.selectedPDBAccessionRow = selectedPDBAccessionRow;
	}

	public double getLevelFourCutValue() {
		return levelFourCutValue;
	}

	public void setLevelFourCutValue(double levelFourCutValue) {
		this.levelFourCutValue = levelFourCutValue;
	}

	public List<Double> getLevelFourCutoffs() {
		return levelFourCutoffs;
	}

	public void setLevelFourCutoffs(List<Double> levelFourCutoffs) {
		this.levelFourCutoffs = levelFourCutoffs;
	}

	public boolean isDisableRequestLevel4Buttons() {
		return disableRequestLevel4Buttons;
	}

	public void setDisableRequestLevel4Buttons(boolean disableRequestLevel4Buttons) {
		this.disableRequestLevel4Buttons = disableRequestLevel4Buttons;
	}

	public String getLevelFourRequestTip() {
		return levelFourRequestTip;
	}

	public void setLevelFourRequestTip(String levelFourRequestTip) {
		this.levelFourRequestTip = levelFourRequestTip;
	}

	public boolean isShowLevelFourRequestTip() {
		return showLevelFourRequestTip;
	}

	public void setShowLevelFourRequestTip(boolean showLevelFourRequestTip) {
		this.showLevelFourRequestTip = showLevelFourRequestTip;
	}

	public boolean isUpdateLevOneBox() {
		return updateLevOneBox;
	}

	public void setUpdateLevOneBox(boolean updateLevOneBox) {
		this.updateLevOneBox = updateLevOneBox;
	}

	public boolean isUpdateLevTwoBox() {
		return updateLevTwoBox;
	}

	public void setUpdateLevTwoBox(boolean updateLevTwoBox) {
		this.updateLevTwoBox = updateLevTwoBox;
	}

	public List<LevelFourRequestableRow> getSelectedCombineLevelFourRuns() {
		return selectedCombineLevelFourRuns;
	}

	public void setSelectedCombineLevelFourRuns(List<LevelFourRequestableRow> selectedCombineLevelFourRuns) {
		this.selectedCombineLevelFourRuns = selectedCombineLevelFourRuns;
	}

	public String getDisplayedLevel4JobName() {
		return displayedLevel4JobName;
	}

	public void setDisplayedLevel4JobName(String displayedLevel4JobName) {
		this.displayedLevel4JobName = displayedLevel4JobName;
	}

	public String getItasserVersion() {
		return itasserVersion;
	}

	public void setItasserVersion(String itasserVersion) {
		this.itasserVersion = itasserVersion;
	}

	public List<LevelFourResultRow> getLevelFourTMAlignReport() {
		return levelFourTMAlignReport;
	}

	public void setLevelFourTMAlignReport(List<LevelFourResultRow> levelFourTMAlignReport) {
		this.levelFourTMAlignReport = levelFourTMAlignReport;
	}

	public List<LevelFourResultRow> getLevelFourTMAlignSelectionReport() {
		return levelFourTMAlignSelectionReport;
	}

	public void setLevelFourTMAlignSelectionReport(List<LevelFourResultRow> levelFourTMAlignSelectionReport) {
		this.levelFourTMAlignSelectionReport = levelFourTMAlignSelectionReport;
	}

	public List<LevelFourResultRow> getSelectedTMAlignSelectionReportRows() {
		return selectedTMAlignSelectionReportRows;
	}

	public void setSelectedTMAlignSelectionReportRows(List<LevelFourResultRow> selectedTMAlignSelectionReportRows) {
		this.selectedTMAlignSelectionReportRows = selectedTMAlignSelectionReportRows;
	}

	public List<LevelFourRequestableRow> getTmAlignQueryAccs() {
		return tmAlignQueryAccs;
	}

	public void setTmAlignQueryAccs(List<LevelFourRequestableRow> tmAlignQueryAccs) {
		this.tmAlignQueryAccs = tmAlignQueryAccs;
	}

	public LevelFourRequestableRow getSelectedTMAlignQueryAcc() {
		return selectedTMAlignQueryAcc;
	}

	public void setSelectedTMAlignQueryAcc(LevelFourRequestableRow selectedTMAlignQueryAcc) {
		this.selectedTMAlignQueryAcc = selectedTMAlignQueryAcc;
	}

	public List<LevelFourRequestableRow> getTmalignReportChoices() {
		return tmalignReportChoices;
	}

	public void setTmalignReportChoices(List<LevelFourRequestableRow> tmalignReportChoices) {
		this.tmalignReportChoices = tmalignReportChoices;
	}

	public LevelFourRequestableRow getSelectedTMAlignReportChoice() {
		return selectedTMAlignReportChoice;
	}

	public void setSelectedTMAlignReportChoice(LevelFourRequestableRow selectedTMAlignReportChoice) {
		this.selectedTMAlignReportChoice = selectedTMAlignReportChoice;
	}

	public List<LevelFourRequestableRow> getSelectedLevelFourRuns() {
		return selectedLevelFourRuns;
	}

	public void setSelectedLevelFourRuns(List<LevelFourRequestableRow> selectedLevelFourRuns) {
		this.selectedLevelFourRuns = selectedLevelFourRuns;
	}

	public String getSourceChoice() {
		return sourceChoice;
	}

	public void setSourceChoice(String sourceChoice) {
		this.sourceChoice = sourceChoice;
	}

	public List<LevelFourAccessionRow> getLevelFourAlphaFoldReport() {
		return levelFourAlphaFoldReport;
	}

	public void setLevelFourAlphaFoldReport(List<LevelFourAccessionRow> levelFourAlphaFoldReport) {
		this.levelFourAlphaFoldReport = levelFourAlphaFoldReport;
	}

	public LevelFourAccessionRow getSelectedAlphaFoldRow() {
		return selectedAlphaFoldRow;
	}

	public void setSelectedAlphaFoldRow(LevelFourAccessionRow selectedAlphaFoldRow) {
		this.selectedAlphaFoldRow = selectedAlphaFoldRow;
	}

	public String getAlphaFoldAccInput() {
		return alphaFoldAccInput;
	}

	public void setAlphaFoldAccInput(String alphaFoldAccInput) {
		this.alphaFoldAccInput = alphaFoldAccInput;
	}

	public String getAlphaFoldProtInput() {
		return alphaFoldProtInput;
	}

	public void setAlphaFoldProtInput(String alphaFoldProtInput) {
		this.alphaFoldProtInput = alphaFoldProtInput;
	}

	public String getAlphaFoldTaxIdInput() {
		return alphaFoldTaxIdInput;
	}

	public void setAlphaFoldTaxIdInput(String alphaFoldTaxIdInput) {
		this.alphaFoldTaxIdInput = alphaFoldTaxIdInput;
	}

	public String getAlphaFoldTaxGrpInput() {
		return alphaFoldTaxGrpInput;
	}

	public void setAlphaFoldTaxGrpInput(String alphaFoldTaxGrpInput) {
		this.alphaFoldTaxGrpInput = alphaFoldTaxGrpInput;
	}

	public String getAlphaFoldSciNameInput() {
		return alphaFoldSciNameInput;
	}

	public void setAlphaFoldSciNameInput(String alphaFoldSciNameInput) {
		this.alphaFoldSciNameInput = alphaFoldSciNameInput;
	}

	public String getAlphaFoldCommonNameInput() {
		return alphaFoldCommonNameInput;
	}

	public void setAlphaFoldCommonNameInput(String alphaFoldCommonNameInput) {
		this.alphaFoldCommonNameInput = alphaFoldCommonNameInput;
	}

	public String getAlphaFoldPDBInput() {
		return alphaFoldPDBInput;
	}

	public void setAlphaFoldPDBInput(String alphaFoldPDBInput) {
		this.alphaFoldPDBInput = alphaFoldPDBInput;
	}

	public String getTmalignVersion() {
		return tmalignVersion;
	}

	public void setTmalignVersion(String tmalignVersion) {
		this.tmalignVersion = tmalignVersion;
	}

	public Date getUniprotDate() {
		return uniprotDate;
	}

	public void setUniprotDate(Date uniprotDate) {
		this.uniprotDate = uniprotDate;
	}

	public StreamedContent getTmalignPDBFile() {
		return tmalignPDBFile;
	}

	public void setTmalignPDBFile(StreamedContent tmalignPDBFile) {
		this.tmalignPDBFile = tmalignPDBFile;
	}

	public List<UniprotMap> getUniprotMap() {
		return uniprotMap;
	}

	public void setUniprotMap(List<UniprotMap> uniprotMap) {
		this.uniprotMap = uniprotMap;
	}

	public String getChosenLevelFourLevel() {
		return chosenLevelFourLevel;
	}

	public void setChosenLevelFourLevel(String chosenLevelFourLevel) {
		this.chosenLevelFourLevel = chosenLevelFourLevel;
	}

	public LevelTwoRequestableRow getSelectedLevelFourDomain() {
		return selectedLevelFourDomain;
	}

	public void setSelectedLevelFourDomain(LevelTwoRequestableRow selectedLevelFourDomain) {
		this.selectedLevelFourDomain = selectedLevelFourDomain;
	}

	public boolean isLevelFourDomainVisible() {
		return levelFourDomainVisible;
	}

	public void setLevelFourDomainVisible(boolean levelFourDomainVisible) {
		this.levelFourDomainVisible = levelFourDomainVisible;
	}

	public boolean isDisableL4PrioritizeBtn() {
		return disableL4PrioritizeBtn;
	}

	public void setDisableL4PrioritizeBtn(boolean disableL4PrioritizeBtn) {
		this.disableL4PrioritizeBtn = disableL4PrioritizeBtn;
	}

	public List<LevelFourRequestableRow> getLoadedLevelFourRuns() {
		return loadedLevelFourRuns;
	}

	public void setLoadedLevelFourRuns(List<LevelFourRequestableRow> loadedLevelFourRuns) {
		this.loadedLevelFourRuns = loadedLevelFourRuns;
	}

	public int getLevelFourSourceLevel() {
		return levelFourSourceLevel;
	}

	public void setLevelFourSourceLevel(int levelFourSourceLevel) {
		this.levelFourSourceLevel = levelFourSourceLevel;
	}

	public String getChosenLevelFourViewLevel() {
		return chosenLevelFourViewLevel;
	}

	public void setChosenLevelFourViewLevel(String chosenLevelFourViewLevel) {
		this.chosenLevelFourViewLevel = chosenLevelFourViewLevel;
	}

	public List<LevelFourRequestableRow> getFilteredStartedLevelFourRuns() {
		return filteredStartedLevelFourRuns;
	}

	public void setFilteredStartedLevelFourRuns(List<LevelFourRequestableRow> filteredStartedLevelFourRuns) {
		this.filteredStartedLevelFourRuns = filteredStartedLevelFourRuns;
	}

	public String getIcn3dPDB() {
		return icn3dPDB;
	}

	public void setIcn3dPDB(String icn3dPDB) {
		this.icn3dPDB = icn3dPDB;
	}

	public String getItasserBackground() {
		return itasserBackground;
	}

	public void setItasserBackground(String itasserBackground) {
		this.itasserBackground = itasserBackground;
	}
		
	public String getUserInputL4Restraint() {
		return userInputL4Restraint;
	}

	public void setUserInputL4Restraint(String userInputL4Restraint) {
		this.userInputL4Restraint = userInputL4Restraint;
	}

	public String getLevelFourTemplateName() {
		return levelFourTemplateName;
	}

	public void setLevelFourTemplateName(String levelFourTemplateName) {
		this.levelFourTemplateName = levelFourTemplateName;
	}

	public int getIcn3dPopupSize() {
		return icn3dPopupSize;
	}

	public void setIcn3dPopupSize(int icn3dPopupSize) {
		this.icn3dPopupSize = icn3dPopupSize;
	}
	
	public List<String> getIcn3dChains() {
		return icn3dChains;
	}

	public void setIcn3dChains(List<String> icn3dChains) {
		this.icn3dChains = icn3dChains;
	}

	
	public String[] getSelectedIcn3dChains() {
		return selectedIcn3dChains;
	}

	public void setSelectedIcn3dChains(String[] selectedIcn3dChains) {
		this.selectedIcn3dChains = selectedIcn3dChains;
	}

	public String getIcn3dPage() {
		return icn3dPage;
	}

	public void setIcn3dPage(String icn3dPage) {
		this.icn3dPage = icn3dPage;
	}
	    
//	public Map<String, Boolean> getEcotoxTaxMap() {
//		return ecotoxTaxMap;
//	}
//
//	public void setEcotoxTaxMap(Map<String, Boolean> ecotoxTaxMap) {
//		this.ecotoxTaxMap = ecotoxTaxMap;
//	}

}
