package gov.epa.seqapass.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
//import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.IOUtils;
//import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import gov.epa.seqapass.common.LevelOneReportRow;
import gov.epa.seqapass.common.LevelThreeReportRow;
import gov.epa.seqapass.common.LevelTwoReportRow;
import gov.epa.seqapass.common.ReportTypeEnum;
import gov.epa.seqapass.model.ReportSettings;
import gov.epa.seqapass.model.RiskAssessorLevel2Group;
import gov.epa.seqapass.model.RiskAssessorReport;
import gov.epa.seqapass.model.RiskAssessorReportRow;
import gov.epa.seqapass.model.RiskAssessorTaxGroup;

@ManagedBean
@SessionScoped
public class RiskAssessorView {

	private ReportView reportView;

	private RiskAssessorReport raReport;

	// level 1
	private List<String> taxGroups;
	private List<String> selectedTaxGroups;
	private List<String> prevSelectedTaxGroups;

	private List<RiskAssessorTaxGroup> speciesColl;
	private List<RiskAssessorTaxGroup> selectedSpeciesColl;
	private RiskAssessorTaxGroup querySpecies;
	private Integer speciesNameType; // common or scientific
	private boolean downloadLev1Viz;
	private boolean downloadLev1Info;
	
	private ReportSettings levOneReportSettings;

//	private ReportTypeEnum levOneReportType;
//	private double levOneEvalue;
//	private String levOneTaxGroup;
//	private int levOneCommonDomainLimit;
//	private boolean levOneSpeciesReadAcross;
//	private int levOneOrthologCount;
//	private double levOneCutoff;
//	private boolean levOneEukaryotesOnly;

	// level 2
	private List<RiskAssessorLevel2Group> level2Groups;
	// private List<RiskAssessorLevel2Group> selectedLevel2Groups;
	// private List<RiskAssessorLevel2Group> prevSelectedLevel2Groups;

	private List<RiskAssessorReportRow> filteredRAReport = new ArrayList<RiskAssessorReportRow>();
	private List<String> level2RunHeaders;

	// level 3
	private List<String> chosenQueryResidues;
	private boolean downloadLev3Info;
	private boolean downloadLev3Viz;
	private boolean downloadLev3report;

	DecimalFormat twoDecimalDigitFormatter = new DecimalFormat("#.00");

	@PostConstruct
	public void init() {
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		reportView = (ReportView) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext,
				null, "reportView");

		resetRAReport();
		// prevSelectedTaxGroups = new ArrayList<String>();
		// speciesColl = new ArrayList<RiskAssessorTaxGroup>();
		// selectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
		//
		// selectedLevel2Groups = new ArrayList<RiskAssessorLevel2Group>();
		// prevSelectedLevel2Groups = new ArrayList<RiskAssessorLevel2Group>();
		//
		// level2RunHeaders = new ArrayList<String>();

	}

	public void onPageLoad() {

		// this is needed to override Primefaces which tries to deselect the
		// disabled item
		highlightSpeciesFirstRow();
	}

	// handles initial loading of level one RA Report controls
	public void loadTaxAndSpeciesGroups() {

		// load Tax groups
		taxGroups = reportView.returnLev1TaxGroupChoices(raReport.getLevelOneReport());
		// load species groups
		List<LevelOneReportRow> report = raReport.getLevelOneReport();
		if (report == null || report.size() == 0) {
			return;
		}
		// construct speciesColl
		speciesColl.clear();
		for (LevelOneReportRow row : report) {
			RiskAssessorTaxGroup raTaxGrp = new RiskAssessorTaxGroup(row.getTaxonomyName(), row.getCommonName(),
					row.getScientificName(), row.getSpeciesTaxId());
			if (!speciesColl.contains(raTaxGrp)) {
				raTaxGrp.setDisabled(false);
				speciesColl.add(raTaxGrp);
			}
		}
		// currently assumes that speciesColl can not be sorted so that query
		// species is always 1st entry
		querySpecies = speciesColl.get(0);

		// set initial defaults
		// all tax groups selected by default
		// selectedTaxGroups = new ArrayList<String>(taxGroups);
		// prevSelectedTaxGroups = new ArrayList<String>(selectedTaxGroups);
		// all species selected by default
		// selectedSpeciesColl = new
		// ArrayList<RiskAssessorTaxGroup>(speciesColl);

		// all tax groups deselected by default
		selectedTaxGroups = new ArrayList<String>();
		prevSelectedTaxGroups = new ArrayList<String>();
		// all species deselected by default
		selectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
		onChangeRATaxMenu(); // needed to disable species rows
		handleQuerySpecies();
		createRAReport();

	}

	// creates Risk Assessor Report based on selected level 1, 2, and 3 items
	public void createRAReport() {

		System.out.println("Creating DS Report");

		handleQuerySpecies();

		// LEVEL 1
		List<RiskAssessorReportRow> theReport = raReport.getReport();

		if (theReport != null) {

			theReport.clear();
			List<LevelOneReportRow> lev1Report = raReport.getLevelOneReport();
			for (LevelOneReportRow row : lev1Report) {
				for (RiskAssessorTaxGroup grp : selectedSpeciesColl) {
					// match grp to level one report row
					if (grp.getSpeciesTaxId() == row.getSpeciesTaxId()) {
						RiskAssessorReportRow newRow = new RiskAssessorReportRow(row.getUpdateVersion(),
								row.getAccession(), row.getTaxonomyName(), row.getCommonName(), row.getScientificName(),
								row.getSpeciesTaxId(), row.getProteinName(), row.getSusceptible());
						theReport.add(newRow);
					}
				}
			}

			// LEVEL 2
			for (RiskAssessorReportRow raRow : raReport.getReport()) {
				Map<String, String> lev2Run = raRow.getLevel2Run();
				lev2Run.clear();

				// for (int i = 0; i < getSelectedLevel2Groups().size(); i++) {
				// RiskAssessorLevel2Group lev2Grp =
				// selectedLevel2Groups.get(i);
				// // boolean foundSpecies = false;
				// lev2Run.put(lev2Grp.getInfo().getDisplayText(), "N/A");
				// for (LevelTwoReportRow lev2Row : lev2Grp.getReport()) {
				// if (lev2Row.getSpeciesTaxId() == raRow.getSpeciesTaxId()) {
				// lev2Run.put(lev2Grp.getInfo().getDisplayText(),
				// lev2Row.getSusceptible());
				// // foundSpecies = true;
				// }
				// }
				// }
				for (int i = 0; i < getLevel2Groups().size(); i++) {
					RiskAssessorLevel2Group lev2Grp = level2Groups.get(i);
					// System.out.println("Level2group i: " + i + ",
					// isDownloadTable: " + lev2Grp.isDownloadTable());
					if (lev2Grp.isDownloadTable()) {
						// System.out.println("displayTxt: " +
						// lev2Grp.getInfo().getDisplayText());
						String cleanDisplay = lev2Grp.getInfo().getDisplayText().replaceAll("\\[.*\\]", "");
						lev2Run.put(cleanDisplay, "N/A");
						for (LevelTwoReportRow lev2Row : lev2Grp.getReport()) {
							// System.out.println("looking for lev2 Species
							// TaxId: " + lev2Row.getSpeciesTaxId());
							if (lev2Row.getSpeciesTaxId() == raRow.getSpeciesTaxId()) {
								// System.out.println("found TaxId adding text:
								// " + lev2Grp.getInfo().getDisplayText() + ",
								// susceptible: " + lev2Row.getSusceptible() );
								cleanDisplay = lev2Grp.getInfo().getDisplayText().replaceAll("\\[.*\\]", "");
								// lev2Run.put(lev2Grp.getInfo().getDisplayText(),
								// lev2Row.getSusceptible());
								lev2Run.put(cleanDisplay, lev2Row.getSusceptible());
								// foundSpecies = true;
							}
						}
					}
				}

				System.out.println("lev2Run map");
				for (Map.Entry<String, String> entry : lev2Run.entrySet()) {
					System.out.println(entry.getKey() + ":" + entry.getValue());
				}
			}
			// build level 2 header list from map keys
			level2RunHeaders.clear();
			for (String key : raReport.getReport().get(0).getLevel2Run().keySet()) {
				System.out.println("Adding header: " + key + " with row 0 value: "
						+ raReport.getReport().get(0).getLevel2Run().get(key));
				// String cleanHeader = key.replaceAll("\\[.*\\]", "");
				level2RunHeaders.add(key);
			}

			// LEVEL 3
			for (RiskAssessorReportRow raRow : raReport.getReport()) {
				for (LevelThreeReportRow lev3Row : raReport.getLevelThreeReport()) {
					if (lev3Row.getSpeciesTaxId() == raRow.getSpeciesTaxId()) {
						raRow.setLevel3Susceptible(lev3Row.getSusceptible());
					}
				}

			}

			// populate initial filtered report
			filteredRAReport.clear();
			for (int i = 0; i < raReport.getReport().size(); i++) {
				filteredRAReport.add(raReport.getReport().get(i));
			}

			System.out.println("Finished DS Report");
		}

	}

	// ensures that query species is always selected and disabled
	public void handleQuerySpecies() {
		if (querySpecies != null) {
			if (!selectedSpeciesColl.contains(querySpecies)) {
				selectedSpeciesColl.add(0, querySpecies);
			}
			querySpecies.setDisabled(true);
			highlightSpeciesFirstRow();
		}
	}

	public void highlightSpeciesFirstRow() {
		if (querySpecies != null) {
			StringBuilder sb = new StringBuilder();
			// highlight first row
			sb.append("var firstRow = document.getElementById('tabView:reportForm:speciesMenu_data');");
			sb.append("if (firstRow !== null){");
			sb.append("firstRow.childNodes[0].classList.add('ui-state-highlight');");
			// check first checkbox
			sb.append(
					"firstRow.querySelector('span.ui-chkbox-icon').className = 'ui-chkbox-icon ui-icon ui-icon-check ui-c';}");
			PrimeFaces.current().executeScript(sb.toString());
		}
	}

	public void onChangeRATaxMenu() {
		System.out.println("onChangeRATaxMenu");
		System.out.println("selectedTaxGroups.size()=" + selectedTaxGroups.size());
		System.out.println("prevSelectedTaxGroups.size()=" + prevSelectedTaxGroups.size());

		// get all items in selectedTaxGroups that are not in
		// prevSelectedTaxGroups;
		// only populates newItems if tax groups were added
		List<String> newItems = new ArrayList<String>();
		if (prevSelectedTaxGroups.size() < selectedTaxGroups.size()) {
			newItems = new ArrayList<String>(selectedTaxGroups);
			newItems.removeAll(prevSelectedTaxGroups);
		}
		// reset prevSelectedTaxGroups list
		prevSelectedTaxGroups = new ArrayList<String>(selectedTaxGroups);

		updateRASpeciesMenu(newItems);
	}

	public void updateRASpeciesMenu(List<String> newTaxGrps) {
		// populate selectedSpeciesColl and enable/disable
		// first set species enable/disable
		for (RiskAssessorTaxGroup grp : speciesColl) {
			if (selectedTaxGroups.contains(grp.getTaxGroup())) {
				grp.setDisabled(false);
				// newly add species are checked by default
				if (newTaxGrps.contains(grp.getTaxGroup()) && !selectedSpeciesColl.contains(grp)) {
//					System.out.println("Adding " + grp.getCommonName());
					//ensure list has been initialized
					if (selectedSpeciesColl.size() == 0) selectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();
					selectedSpeciesColl.add(grp);
				}
			} else {
				grp.setDisabled(true);
				// remove from selected species if disabling
				if (selectedSpeciesColl.contains(grp) && grp != querySpecies) {
					if (selectedSpeciesColl.contains(grp)) {
						selectedSpeciesColl.remove(grp);
					}
				}
			}

		}

		for (RiskAssessorTaxGroup grp : selectedSpeciesColl) {
			System.out.println(grp.getCommonName());
		}

		// recreate Risk Assessor Report
		createRAReport();
	}

	public void onChangeRASpeciesMenu() {
		createRAReport();
	}

	// public void onChangeRADomainsMenu() {
	//
	// for (RiskAssessorLevel2Group grp : selectedLevel2Groups) {
	// System.out.println(grp.getInfo().getDisplayText());
	// // System.out.println(" with key: " + grp.getInfo().getKey());
	// }
	//
	// if (raReport.getLevelOneReport().size() > 0) {
	// createRAReport();
	// }
	// }

	public void updateRAReport() {
		System.out.println("Inside updateRAReport");
		if (raReport.getLevelOneReport().size() > 0) {
			createRAReport();
		}

	}

	public void onChangeNameType() {
		System.out.println("onChangeNameType!!");
	}

	public void resetRAReport() {
		raReport = new RiskAssessorReport();
		levOneReportSettings = new ReportSettings();
		speciesNameType = 0;

		taxGroups = new ArrayList<String>();
		selectedTaxGroups = new ArrayList<String>();
		prevSelectedTaxGroups = new ArrayList<String>();

		speciesColl = new ArrayList<RiskAssessorTaxGroup>();
		selectedSpeciesColl = new ArrayList<RiskAssessorTaxGroup>();

		level2Groups = new ArrayList<RiskAssessorLevel2Group>();
		// selectedLevel2Groups = new ArrayList<RiskAssessorLevel2Group>();
		// prevSelectedLevel2Groups = new ArrayList<RiskAssessorLevel2Group>();

		level2RunHeaders = new ArrayList<String>();
		
		downloadLev1Info = false;
		downloadLev1Viz = false;
		downloadLev3Info = false;
		downloadLev3Viz = false;
		downloadLev3report = false;
	}

	public String formatSelectedAminoAcids() {
		return chosenQueryResidues.stream().collect(Collectors.joining(",", "", ""));
	}

	public StreamedContent downloadRAReport() {
		System.out.println("Inside createPDF");
		SimpleDateFormat dateFrm = new SimpleDateFormat("MM/dd/yyyy");

		float mainWidthPerc = 100f; // seqapass banner
		float secondaryWidthPerc = 90f; // risk assessor report banner
		float tertiaryWidthPerc = 85f; // Level 1, 2, 3, visualizations, final
										// report
		float nestedWidthPerc = 98f; // for nested tables

		// Document doc = new Document(PageSize.LETTER, 0f, 0f, 0f, 0f);
		// //left,right,top,bottom
		Document doc = new Document(PageSize.LETTER); // defaults to 36f for all
														// margins
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			PdfWriter writer = PdfWriter.getInstance(doc, baos);
			writer.setPageEvent(new PageStamper());
			doc.open();
			doc.addAuthor("SeqAPASS");
			doc.addCreationDate();

			Font bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
			bannerFont.setColor(BaseColor.WHITE);
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(mainWidthPerc);
			PdfPCell cell = new PdfPCell();
			Paragraph p = new Paragraph("Sequence Alignment to Predict Across Species Susceptibility (SeqAPASS)",
					bannerFont);
			p.setAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(new BaseColor(35, 120, 195));
			cell.addElement(p);
			cell.setMinimumHeight(50);
			table.addCell(cell);
			table.setSpacingAfter(10f);
			doc.add(table);

			bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
			bannerFont.setColor(BaseColor.BLACK);
			table = new PdfPTable(1);
			table.setWidthPercentage(secondaryWidthPerc);
			cell = new PdfPCell();
			p = new Paragraph("Decision Summary Report", bannerFont);
			p.setAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(new BaseColor(170, 205, 236));
			cell.addElement(p);
			cell.setMinimumHeight(40);
			table.addCell(cell);
			table.setSpacingAfter(10f);
			doc.add(table);

			Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);
			headerFont.setColor(BaseColor.BLACK);

			PdfPTable nestedTable = null;
			List<String> colHeaders = new ArrayList<String>();
			List<String> rowTextList = new ArrayList<String>();
			Font cellFont = null;

			////////////
			// Level 1 //
			////////////

			if (downloadLev1Info) {

				// main table
				table = new PdfPTable(1);
				table.setWidthPercentage(tertiaryWidthPerc);
				table.setSpacingAfter(10f);
				// table.getDefaultCell().setBorderColor(BaseColor.GRAY);

				// header nested table
				bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
				bannerFont.setColor(BaseColor.BLACK);
				nestedTable = new PdfPTable(1);
				nestedTable.setWidthPercentage(nestedWidthPerc);

				cell = new PdfPCell();
				p = new Paragraph("Level 1", bannerFont);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.setBackgroundColor(new BaseColor(170, 205, 236));
				cell.addElement(p);
				cell.setMinimumHeight(30);
				nestedTable.addCell(cell);

				// add header table to main table
				cell = new PdfPCell();
				cell.addElement(nestedTable);
				cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
				cell.setPaddingLeft(-2);
				cell.setPaddingRight(-2);
				table.addCell(cell);

				colHeaders.clear();
				colHeaders.add("Level 1 Query Protein Information");
				colHeaders.add("Report Settings");

				nestedTable = new PdfPTable(2);
				nestedTable.setWidthPercentage(nestedWidthPerc);
				nestedTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				nestedTable.setTableEvent(new OutlineBorderEvent(1f, new BaseColor(170, 205, 236)));

				for (int i = 0; i < colHeaders.size(); i++) {
					cell = new PdfPCell();
					p = new Paragraph(colHeaders.get(i), headerFont);
					cell.addElement(p);
					cell.setBorder(Rectangle.NO_BORDER);
					nestedTable.addCell(cell);
				}

				cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
				cellFont.setColor(BaseColor.BLACK);

				rowTextList.clear();
				rowTextList.add("SeqAPASS ID: " + reportView.getRunId());
				rowTextList.add("Report Type: " + (levOneReportSettings.getReportType() == ReportTypeEnum.Primary ? "Primary" : "Full"));
				rowTextList.add("Query Species: " + reportView.getQuerySpecies());
				rowTextList.add("E-value: " + levOneReportSettings.getEvalueLimit());
				rowTextList.add("Query Protein: " + reportView.getQueryProtein());
				rowTextList.add("Sorted By Taxonomic Group: " + levOneReportSettings.getTaxGroup());
				rowTextList.add("Query Accession: " + reportView.getAccession());
				rowTextList.add("Common Domains: " + levOneReportSettings.getCommonDomainLimit());
				rowTextList.add("Ortholog Count: " + levOneReportSettings.getOrthologCount());
				rowTextList.add("Species Read-Across: " + reportView.convertBooleanToYN(levOneReportSettings.isSpeciesReadAcross()));
				rowTextList.add("Protein and Taxonomy Data: " + dateFrm.format(reportView.getNcbiDate()));
				rowTextList.add("Cut-off %: " + twoDecimalDigitFormatter.format(levOneReportSettings.getCutValue()));
				rowTextList.add("BLAST Version: " + reportView.getBlastVersion());
				rowTextList.add("Show Only Eukaryotes: " + reportView.convertBooleanToYN(levOneReportSettings.isEukaryotesOnly()));
				rowTextList.add("Software Version: " + reportView.getSeqapassVersion());
				rowTextList.add("");

				for (String text : rowTextList) {
					nestedTable.addCell(new Phrase(text, cellFont));
				}

				// add nested table (Level 1 content)
				cell = new PdfPCell();
				cell.addElement(nestedTable);
				cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
				table.addCell(cell);
				doc.add(table);
			}

			// Visualization
			if (raReport.getLev1Boxplot() != null && downloadLev1Viz) {
				try {
					table = new PdfPTable(1);
					table.setWidthPercentage(tertiaryWidthPerc);
					table.setSpacingAfter(10f);

					Image jpg = Image.getInstance(raReport.getLev1Boxplot());

					table.addCell(new Phrase("Level 1 Visualization "
							+ (raReport.getLev1BoxPlotSettings().getReportSettings().getReportType() == ReportTypeEnum.Primary ? "- Primary Report" : "- Full Report"),
							headerFont));

					cell = new PdfPCell(jpg, true);

					table.addCell(cell);
					doc.add(table);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			doc.newPage();

			/////////////
			// Level 2 //
			/////////////
			// if (selectedLevel2Groups.size() > 0) {

			for (RiskAssessorLevel2Group group : level2Groups) {
				
				// main table
				table = new PdfPTable(1);
				table.setWidthPercentage(tertiaryWidthPerc);
				table.setSpacingAfter(10f);

				// header nested table
				bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
				bannerFont.setColor(BaseColor.BLACK);
				nestedTable = new PdfPTable(1);
				nestedTable.setWidthPercentage(nestedWidthPerc);
				cell = new PdfPCell();
				p = new Paragraph("Level 2", bannerFont);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.setBackgroundColor(new BaseColor(170, 205, 236));
				cell.addElement(p);
				cell.setMinimumHeight(30);
				nestedTable.addCell(cell);

				// add banner table to main table
				cell = new PdfPCell();
				cell.addElement(nestedTable);
				cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
				cell.setPaddingLeft(-2);
				cell.setPaddingRight(-2);
				table.addCell(cell);

				if (group.isDownloadInfo()) {

					// content nested table
					headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);
					headerFont.setColor(BaseColor.BLACK);

					nestedTable = new PdfPTable(2);
					nestedTable.setWidthPercentage(nestedWidthPerc);
					nestedTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
					nestedTable.setTableEvent(new OutlineBorderEvent(1f, new BaseColor(170, 205, 236)));

					colHeaders.clear();
					colHeaders.add("Level 2 Query Protein Information");
					colHeaders.add("Report Settings");

					for (int i = 0; i < colHeaders.size(); i++) {
						cell = new PdfPCell();
						p = new Paragraph(colHeaders.get(i), headerFont);
						cell.addElement(p);
						cell.setBorder(Rectangle.NO_BORDER);
						nestedTable.addCell(cell);
					}

					cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
					cellFont.setColor(BaseColor.BLACK);

					rowTextList.clear();
					rowTextList.add("SeqAPASS ID: " + reportView.getRunId());
					rowTextList.add("Report Type: "
							+ (group.getReportSettings().getReportType() == ReportTypeEnum.Primary ? "Primary" : "Full"));
					rowTextList.add("Query Species: " + reportView.getQuerySpecies());
					rowTextList.add("E-value: " + group.getReportSettings().getEvalueLimit());
					rowTextList.add("Query Domain: " + group.getInfo().getDisplayText());
					rowTextList.add("Sorted By Taxonomic Group: " + group.getReportSettings().getTaxGroup());
					rowTextList.add("Query Accession: " + reportView.getAccession());
					rowTextList.add(
							"Species Read-Across: " + reportView.convertBooleanToYN(group.getReportSettings().isSpeciesReadAcross()));
					rowTextList.add("Ortholog Count: " + group.getReportSettings().getOrthologCount());
					rowTextList.add("Cut-off %: " + twoDecimalDigitFormatter.format(group.getReportSettings().getCutValue()));
					rowTextList.add("Protein and Taxonomy Data: " + dateFrm.format(reportView.getNcbiDate()));
					rowTextList.add(
							"Show Only Eukaryotes: " + reportView.convertBooleanToYN(group.getReportSettings().isEukaryotesOnly()));
					rowTextList.add("BLAST Version: " + reportView.getBlastVersion());
					rowTextList.add("");
					rowTextList.add("Software Version: " + reportView.getSeqapassVersion());
					rowTextList.add("");

					for (String text : rowTextList) {
						// cell = new PdfPCell();
						// p = new Paragraph(rowTextList.get(i), cellFont);
						// cell.addElement(p);
						// cell.setBorder(Rectangle.NO_BORDER);
						// nestedTable.addCell(cell);
						nestedTable.addCell(new Phrase(text, cellFont));
					}

					cell = new PdfPCell();
					cell.addElement(nestedTable);
					cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);

					table.addCell(cell);
					
				}
				
				//Add L2 banner if info or boxplot is selected
				if(group.isDownloadInfo() || (group.getBoxPlot() != null && group.isDownloadViz())){
					doc.add(table);
				}

				// Visualization
				if (group.getBoxPlot() != null && group.isDownloadViz()) {
					try {
						table = new PdfPTable(1);
						table.setWidthPercentage(tertiaryWidthPerc);
						table.setSpacingAfter(10f);

						// byte[] myByteArray =
						// IOUtils.toByteArray(group.getBoxPlot().getStream());
						// Image jpg = Image.getInstance(myByteArray);
						Image jpg = Image.getInstance(group.getBoxPlot());

						table.addCell(new Phrase(
								"Level 2 Visualization " + (group.getBoxPlotSettings().getReportSettings().getReportType() == ReportTypeEnum.Primary
										? "- Primary Report" : "- Full Report"),
								headerFont));

						cell = new PdfPCell(jpg, true);

						table.addCell(cell);
						doc.add(table);

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//page break in between L2 groups
				doc.newPage();

			}

			doc.newPage();

			// }

			/////////////
			// Level 3 //
			/////////////

			if (downloadLev3Info || downloadLev3Viz) {
				
				// main table
				table = new PdfPTable(1);
				table.setWidthPercentage(tertiaryWidthPerc);
				table.setSpacingAfter(10f);

				// header nested table
				bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
				bannerFont.setColor(BaseColor.BLACK);
				nestedTable = new PdfPTable(1);
				nestedTable.setWidthPercentage(nestedWidthPerc);
				cell = new PdfPCell();
				p = new Paragraph("Level 3", bannerFont);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.setBackgroundColor(new BaseColor(170, 205, 236));
				cell.addElement(p);
				cell.setMinimumHeight(30);
				nestedTable.addCell(cell);
				
				// add header table to main table
				cell = new PdfPCell();
				cell.addElement(nestedTable);
				cell.setPaddingLeft(-2);
				cell.setPaddingRight(-2);
				table.addCell(cell);
				
				if (downloadLev3Info) {

					// nestedTable holds left and right table
					nestedTable = new PdfPTable(2);
					nestedTable.setWidthPercentage(nestedWidthPerc);
					// nestedTable.setWidths(new float[] { 1f, 3f });

					// Left table (selected amino acids)
					PdfPTable leftTable = new PdfPTable(1);
					leftTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
					headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.UNDERLINE | Font.BOLD);
					headerFont.setColor(BaseColor.BLACK);
					cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
					cellFont.setColor(BaseColor.BLACK);

					leftTable.addCell(new Phrase("Selected Amino Acids", headerFont));
					leftTable.addCell(new Phrase(formatSelectedAminoAcids(), cellFont));

					PdfPCell leftCell = new PdfPCell();
					leftCell.addElement(leftTable);
					leftCell.setBorder(Rectangle.NO_BORDER);

					// Right table (protein info and report settings)
					PdfPTable rightTable = new PdfPTable(1);
					rightTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

					headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.UNDERLINE | Font.BOLD);
					headerFont.setColor(BaseColor.BLACK);
					cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
					cellFont.setColor(BaseColor.BLACK);

					// Strings for right table column headers
					colHeaders.clear();
					colHeaders.add("Level 3 Template Protein Information");

					for (int i = 0; i < colHeaders.size(); i++) {
						cell = new PdfPCell();
						p = new Paragraph(colHeaders.get(i), headerFont);
						cell.addElement(p);
						cell.setBorder(Rectangle.NO_BORDER);
						rightTable.addCell(cell);
					}

					rowTextList.clear();
					rowTextList.add("SeqAPASS ID: " + reportView.getRunId());
					rowTextList.add("Template Species: " + raReport.getLevelThreeTemplateSpecies());
					rowTextList.add("Template Protein: " + raReport.getLevelThreeTemplateProtein());
					rowTextList.add("Protein and Taxonomy Data: " + dateFrm.format(reportView.getNcbiDate()));
					rowTextList.add("BLAST Version: " + reportView.getBlastVersion());
					rowTextList.add("Software Version: " + reportView.getSeqapassVersion());

					for (int i = 0; i < rowTextList.size(); i++) {
						cell = new PdfPCell();
						p = new Paragraph(rowTextList.get(i), cellFont);
						cell.addElement(p);
						cell.setBorder(Rectangle.NO_BORDER);
						rightTable.addCell(cell);
					}

					PdfPCell rightCell = new PdfPCell();
					rightCell.addElement(rightTable);
					rightCell.setBorder(Rectangle.NO_BORDER);

					nestedTable.addCell(leftCell);
					nestedTable.addCell(rightCell);
					nestedTable.setTableEvent(new OutlineBorderEvent(1f, new BaseColor(170, 205, 236)));

					cell = new PdfPCell();
					cell.addElement(nestedTable);

					table.addCell(cell);
//					doc.add(table);

				}
				
				//Add L3 banner if info or boxplot is selected
				if(downloadLev3Info || downloadLev3Viz){
					doc.add(table);
				}
			}

			// Visualization
			if (raReport.getLev3Heatmap() != null && downloadLev3Viz) {
				ELContext elContext = FacesContext.getCurrentInstance().getELContext();
				VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext,
						null, "visualizationView");
				
				
				try {
					table = new PdfPTable(1);
					table.setWidthPercentage(tertiaryWidthPerc);
					table.setSpacingAfter(10f);
					
					byte[] heatmap = raReport.getLev3Heatmap();
					String base64Str = Base64.encodeBase64String(heatmap);
					System.out.println("base64 string : " + base64Str);
					Image png = Image.getInstance(Base64.decodeBase64(base64Str));


					table.addCell(new Phrase("Level 3 Visualization", headerFont));

					cell = new PdfPCell(png, true);

					table.addCell(cell);
					doc.add(table);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			doc.newPage();

			/////////////////
			// Risk Report //
			/////////////////
			// main table
			table = new PdfPTable(1);
			table.setWidthPercentage(tertiaryWidthPerc);
			table.setSpacingAfter(10f);
			table.setSplitLate(false);
			table.setSplitRows(true);

			// header nested table
			bannerFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
			bannerFont.setColor(BaseColor.BLACK);
			nestedTable = new PdfPTable(1);
			nestedTable.setWidthPercentage(nestedWidthPerc);
			cell = new PdfPCell();
			p = new Paragraph("Final Decision Summary Report", bannerFont);
			p.setAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(new BaseColor(170, 205, 236));
			cell.addElement(p);
			cell.setMinimumHeight(30);
			// cell.setColspan(1);
			nestedTable.addCell(cell);

			// add header table to main table
			cell = new PdfPCell();
			cell.addElement(nestedTable);
			cell.setPaddingLeft(-2);
			cell.setPaddingRight(-2);
			table.addCell(cell);

			List<BaseColor> headerColor = new ArrayList<BaseColor>();
			// Strings for column headers
			colHeaders.clear();
			colHeaders.add("Species");
			headerColor.add(new BaseColor(35, 120, 195));
			colHeaders.add("Protein");
			headerColor.add(new BaseColor(35, 120, 195));
			colHeaders.add("Level 1 Susceptible (Y/N)");
			headerColor.add(new BaseColor(35, 120, 195));
			for (String lev2Header : level2RunHeaders) {
				colHeaders.add(lev2Header);
				headerColor.add(new BaseColor(44, 96, 138));
			}

			if (!raReport.getLevelThreeReport().isEmpty() && downloadLev3report) {
				colHeaders.add("Level 3 Template");
				headerColor.add(new BaseColor(39, 72, 99));
				colHeaders.add("Level 3 Amino Acids (Y/N)");
				headerColor.add(new BaseColor(39, 72, 99));
			}

			headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);
			headerFont.setColor(BaseColor.WHITE);
			cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
			cellFont.setColor(BaseColor.BLACK);

			nestedTable = new PdfPTable(colHeaders.size());
			nestedTable.setWidthPercentage(nestedWidthPerc);
			nestedTable.setSplitLate(false);
			nestedTable.setSplitRows(true);

			for (int i = 0; i < colHeaders.size(); i++) {
				cell = new PdfPCell();
				p = new Paragraph(colHeaders.get(i), headerFont);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
				cell.setBackgroundColor(headerColor.get(i));
				nestedTable.addCell(cell);
			}

			rowTextList.clear();

			for (RiskAssessorReportRow row : raReport.getReport()) {
				rowTextList.add(speciesNameType == 0 ? row.getCommonName() : row.getScientificName());
				rowTextList.add(row.getProtein());
				rowTextList.add(row.getSusceptible());
				for (String lev2Header : level2RunHeaders) {
					rowTextList.add(row.getLevel2Run().get(lev2Header));
				}
				if (!raReport.getLevelThreeReport().isEmpty() && downloadLev3report) {
					rowTextList.add(raReport.getLevelThreeTemplateSpecies());
					rowTextList.add(row.getLevel3Susceptible());
				}
			}

			for (int i = 0; i < rowTextList.size(); i++) {
				cell = new PdfPCell();
				p = new Paragraph(rowTextList.get(i), cellFont);
				p.setAlignment(Element.ALIGN_CENTER);
				cell.addElement(p);
				nestedTable.addCell(cell);
			}

			// Zebra stripe rows
			BaseColor rowA = BaseColor.WHITE;
			BaseColor rowB = new BaseColor(241, 243, 246);
			// BaseColor rowA = new BaseColor(237, 239, 240);
			// BaseColor rowB = new BaseColor(220, 222, 224);
			boolean b = true;
			int rowNum = 0;
			for (PdfPRow r : nestedTable.getRows()) {
				if (rowNum != 0) {
					for (PdfPCell c : r.getCells()) {
						c.setBackgroundColor(b ? rowA : rowB);
					}
					b = !b;
				}
				rowNum += 1;
			}

			// add 2nd nested table (Risk Report content)
			cell = new PdfPCell();
			cell.addElement(nestedTable);

			// cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);

			doc.add(table);

			doc.close();
			writer.close();

			//InputStream stream = null;
			//stream = new ByteArrayInputStream(baos.toByteArray());
			//StreamedContent SVG = new DefaultStreamedContent(stream, "application/pdf", "DSReport.pdf");
			InputStream stream = new ByteArrayInputStream(baos.toByteArray());
			StreamedContent SVG = DefaultStreamedContent.builder().contentType("application/pdf").name("DSReport.pdf").stream(()->stream).build();
			return SVG;
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public void showBase64(){
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		VisualizationView vizView = (VisualizationView) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext,
				null, "visualizationView");
		
//		byte[] bytes = null;
//		try {
//			bytes = IOUtils.toByteArray(vizView.getSvgBinarySource3().getStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		byte[] bytes = raReport.getLev3Heatmap();
		String encoded = Base64.encodeBase64String(bytes);
		System.out.println("lev3Heatmap base64 string : " + encoded);
		
//		try {
//			System.out.println("from svgBinary3: " + Base64.encodeBase64String(IOUtils.toByteArray(vizView.getSvgBinarySource3().getStream())));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("problem");
//			e.printStackTrace();
//		}
	}

	public class PageStamper extends PdfPageEventHelper {

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			final int currentPageNumber = writer.getCurrentPageNumber();

			if (currentPageNumber == 1) {
				return;
			}

			try {
				final Rectangle pageSize = document.getPageSize();
				final PdfContentByte directContent = writer.getDirectContent();

				directContent.setColorFill(BaseColor.GRAY);
				directContent.setFontAndSize(BaseFont.createFont(), 10);

				directContent.setTextMatrix(pageSize.getRight(40), pageSize.getBottom(30));
				directContent.showText(String.valueOf(currentPageNumber));
			} catch (Exception e) {
				System.out.println("Error generating pdf page number");
			}
		}
	}

	// public void updateLevel2InfoSelection() {
	//
	// // first set info check to false for all unselected groups
	// List<RiskAssessorLevel2Group> unselectedGroups = new
	// ArrayList<RiskAssessorLevel2Group>(level2Groups);
	// unselectedGroups.removeAll(selectedLevel2Groups);
	// for (RiskAssessorLevel2Group group : unselectedGroups) {
	// group.setDownloadInfo(false);
	// group.setDownloadViz(false);
	// }
	//
	// // now set info check to true for newly selected group
	//
	// // get all items in selectedLevel2Groups that are not in level2Groups;
	// // only populates newItems if groups were added
	// List<RiskAssessorLevel2Group> newItems = new
	// ArrayList<RiskAssessorLevel2Group>(selectedLevel2Groups);
	// if (prevSelectedLevel2Groups.size() < selectedLevel2Groups.size()) {
	// // newItems = in selectedLevel2Groups that are not in level2Groups;
	// // only new
	// // ArrayList<RiskAssessorLevel2Group>(selectedLevel2Groups);
	// newItems.removeAll(prevSelectedLevel2Groups);
	// }
	//
	// for (RiskAssessorLevel2Group group : newItems) {
	// // should only be one new item per call, but using list and loop for
	// // convenience
	// group.setDownloadInfo(true);
	// group.setDownloadViz(false);
	// }
	//
	// // reset prevSelectedLevel2Groups list
	// prevSelectedLevel2Groups = new
	// ArrayList<RiskAssessorLevel2Group>(selectedLevel2Groups);
	//
	// }

	// Getters and Setters
	public RiskAssessorReport getRaReport() {
		return raReport;
	}

	public void setRaReport(RiskAssessorReport raReport) {
		this.raReport = raReport;
	}

	// public boolean isTaxGroupSelectAll() {
	// return taxGroupSelectAll;
	// }
	//
	// public void setTaxGroupSelectAll(boolean taxGroupSelectAll) {
	// this.taxGroupSelectAll = taxGroupSelectAll;
	// }

	public List<String> getTaxGroups() {
		return taxGroups;
	}

	public void setTaxGroups(List<String> taxGroups) {
		this.taxGroups = taxGroups;
	}

	public List<String> getSelectedTaxGroups() {
		return selectedTaxGroups;
	}

	public void setSelectedTaxGroups(List<String> selectedTaxGroups) {
		this.selectedTaxGroups = selectedTaxGroups;
	}

	public Integer getSpeciesNameType() {
		return speciesNameType;
	}

	public void setSpeciesNameType(Integer speciesNameType) {
		this.speciesNameType = speciesNameType;
	}

	public List<RiskAssessorTaxGroup> getSpeciesColl() {
		return speciesColl;
	}

	public void setSpeciesColl(List<RiskAssessorTaxGroup> speciesColl) {
		this.speciesColl = speciesColl;
	}

	public List<RiskAssessorTaxGroup> getSelectedSpeciesColl() {
		return selectedSpeciesColl;
	}

	public void setSelectedSpeciesColl(List<RiskAssessorTaxGroup> selectedSpeciesColl) {
		this.selectedSpeciesColl = selectedSpeciesColl;
	}

	// public boolean isSpeciesSelectAll() {
	// return speciesSelectAll;
	// }
	//
	// public void setSpeciesSelectAll(boolean speciesSelectAll) {
	// this.speciesSelectAll = speciesSelectAll;
	// }

	public RiskAssessorTaxGroup getQuerySpecies() {
		return querySpecies;
	}

	public void setQuerySpecies(RiskAssessorTaxGroup querySpecies) {
		this.querySpecies = querySpecies;
	}

	public List<RiskAssessorLevel2Group> getLevel2Groups() {
		return level2Groups;
	}

	public boolean isDownloadLev1Viz() {
		return downloadLev1Viz;
	}

	public void setDownloadLev1Viz(boolean downloadLev1Viz) {
		this.downloadLev1Viz = downloadLev1Viz;
	}

	public void setLevel2Groups(List<RiskAssessorLevel2Group> level2Groups) {
		this.level2Groups = level2Groups;
	}

	// public List<RiskAssessorLevel2Group> getSelectedLevel2Groups() {
	// return selectedLevel2Groups;
	// }
	//
	// public void setSelectedLevel2Groups(List<RiskAssessorLevel2Group>
	// selectedLevel2Groups) {
	// this.selectedLevel2Groups = selectedLevel2Groups;
	// }

	// public boolean isDomainsSelectAll() {
	// return domainsSelectAll;
	// }
	//
	// public void setDomainsSelectAll(boolean domainsSelectAll) {
	// this.domainsSelectAll = domainsSelectAll;
	// }

	public List<RiskAssessorReportRow> getFilteredRAReport() {
		return filteredRAReport;
	}

	public void setFilteredRAReport(List<RiskAssessorReportRow> filteredRAReport) {
		this.filteredRAReport = filteredRAReport;
	}

	public List<String> getLevel2RunHeaders() {
		return level2RunHeaders;
	}

	public void setLevel2RunHeaders(List<String> level2RunHeaders) {
		this.level2RunHeaders = level2RunHeaders;
	}

	public List<String> getPrevSelectedTaxGroups() {
		return prevSelectedTaxGroups;
	}

	public void setPrevSelectedTaxGroups(List<String> prevSelectedTaxGroups) {
		this.prevSelectedTaxGroups = prevSelectedTaxGroups;
	}

	public List<String> getChosenQueryResidues() {
		return chosenQueryResidues;
	}

	public void setChosenQueryResidues(List<String> chosenQueryResidues) {
		this.chosenQueryResidues = chosenQueryResidues;
	}

	public boolean isDownloadLev1Info() {
		return downloadLev1Info;
	}

	public void setDownloadLev1Info(boolean downloadLev1Info) {
		this.downloadLev1Info = downloadLev1Info;
	}

	public boolean isDownloadLev3report() {
		return downloadLev3report;
	}

	public void setDownloadLev3report(boolean downloadLev3report) {
		this.downloadLev3report = downloadLev3report;
	}

	public boolean isDownloadLev3Info() {
		return downloadLev3Info;
	}

	public void setDownloadLev3Info(boolean downloadLev3Info) {
		this.downloadLev3Info = downloadLev3Info;
	}

	public boolean isDownloadLev3Viz() {
		return downloadLev3Viz;
	}

	public void setDownloadLev3Viz(boolean downloadLev3Viz) {
		this.downloadLev3Viz = downloadLev3Viz;
	}

	public ReportSettings getLevOneReportSettings() {
		return levOneReportSettings;
	}

	public void setLevOneReportSettings(ReportSettings levOneReportSettings) {
		this.levOneReportSettings = levOneReportSettings;
	}

	// public List<RiskAssessorLevel2Group> getPrevSelectedLevel2Groups() {
	// return prevSelectedLevel2Groups;
	// }
	//
	// public void setPrevSelectedLevel2Groups(List<RiskAssessorLevel2Group>
	// prevSelectedLevel2Groups) {
	// this.prevSelectedLevel2Groups = prevSelectedLevel2Groups;
	// }

}

enum NameTypeEnum {

	Common(0), Scientific(1);

	private int value;

	public int getValue() {
		return value;
	}

	private NameTypeEnum(int value) {
		this.value = value;
	}

}

class OutlineBorderEvent implements PdfPTableEvent {

	private float borderWidth;
	private BaseColor borderColor;

	public OutlineBorderEvent(float borderWidth, BaseColor borderColor) {
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;
	}

	public OutlineBorderEvent() {
		this.borderWidth = 0.5f;
		this.borderColor = BaseColor.BLACK;
	}

	public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart,
			PdfContentByte[] canvases) {
		float width[] = widths[0];
		float x1 = width[0];
		float x2 = width[width.length - 1];
		float y1 = heights[0];
		float y2 = heights[heights.length - 1];
		PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
		cb.rectangle(x1, y1, x2 - x1, y2 - y1);
		// cb.setColorStroke(new BaseColor(170, 205, 236));
		// cb.setLineWidth(1f);
		cb.setColorStroke(borderColor);
		cb.setLineWidth(borderWidth);
		cb.stroke();
		cb.resetRGBColorStroke();
	}

	public void createPdf() {

	}
}
