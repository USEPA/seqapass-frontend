console.log("seqapass.js loaded from static file");
/**
 * Global array of SeqAPASS variable objects
 */
var sqg = new Object();

sqg.useCommon = true;  // default to using common name in legend
sqg.resetBoxPlot1 = true;
sqg.resetBoxPlot2 = true;
sqg.maintainCommonLegendInfo = false;  // default to not maintaining common
										// legend shape/color for scientific

sqg.scaleDelta = 5;  // this is how much fixed zoom values change by: ex)
						// scaleDelta=5 -> 110, 105, 100, 95, 90
sqg.fixedZoomLocations = 15;  // this is the number of fixed zoom positions on
								// either side of 100%
sqg.zoomMinDefault = 0.5;
sqg.zoomMaxDefault = 5;
sqg.barWidthMin = 6;
sqg.barWidthMax = 60;
sqg.wheelZoomDelta = 0.9;    // Multiplicative factor for wheel zoom
sqg.arrowZoomDelta = 0.05;   // additive factor for using zoom arrows

/**
 * An array of distinctive colors from:
 * https://sashat.me/2017/01/11/list-of-20-simple-distinct-colors/
 */
sqg.colors = new Array();
sqg.colors.push("#3cb44b"); // 0
sqg.colors.push("#ffe119"); // 1
sqg.colors.push("#0082c8"); // 2
sqg.colors.push("#f58231"); // 3
sqg.colors.push("#911eb4"); // 4
sqg.colors.push("#46f0f0"); // 5
sqg.colors.push("#d2f53c"); // 6
sqg.colors.push("#f032e6"); // 7
sqg.colors.push("#fabebe"); // 8
sqg.colors.push("#008080"); // 9
sqg.colors.push("#e6beff"); // 10
sqg.colors.push("#aa6e28"); // 11
sqg.colors.push("#fffac8"); // 12
sqg.colors.push("#800000"); // 13
sqg.colors.push("#aaffc3"); // 14
sqg.colors.push("#808000"); // 15
sqg.colors.push("#ffd8b1"); // 16
sqg.colors.push("#000080"); // 17
sqg.colors.push("#808080"); // 18

sqg.shapes = new Array();
sqg.shapes.push(" m -3.5 3.5 a 5 5 0 1 1 0.01 0.01");	// circle
sqg.shapes.push(" m 0 6 l -4 -6 l 4 -6 l 4 6z");		// diamond
sqg.shapes.push(" m 4 4 l -8 0 l 0 -8 l 8 0z");			// square
sqg.shapes.push(" m -5 5 l 5 -10 l 5 10z");				// triangle pointing up
sqg.shapes.push(" m -5 -5 l 5 10 l 5 -10z");			// triangle pointing
														// down

sqg.commonLegendInfo = new Array();
		
var pp = new Object();
pp.height = 650;
pp.leftMargin = 60;
pp.topMargin = 50;
pp.minWidth = 1500;
pp.bottomTextHeight = 170;
pp.bottomRest = 30;
// Starting height is 900, , witdh has a minimum, but otherwise adjusts
pp.yAxisTextMatrix = "matrix(0,-1,1,0,5,0)";
pp.xAxisTextX = 400;
pp.xAxisYpos = pp.topMargin + pp.height;
pp.xAxisTextY = pp.xAxisYpos + pp.bottomTextHeight;
pp.barWidthDefault = 10;
pp.barWidth = pp.barWidthDefault;
pp.barSpacing = 5;
pp.browserWidth = -1;
pp.browserHeight = -1;
pp.zoomFactor = 1;
pp.zoomFactorPrev = 1;

/**
 * Plot parameters for Visualization Panel 1
 */
sqg.plotParams = pp;

/**
 * The zoomArray is a list of valid zoom levels from low to high rounded to 2
 * decimal places
 */
// sqg.zoomArray = new Array();
// for (var i=sqg.fixedZoomLocations; i>=1; i--){
// sqg.zoomArray.push(Math.round(Math.pow(sqg.scaleFactor, i)*100)/100);
// }
// sqg.zoomArray.push(1.00);
// for (var i=1; i<=sqg.fixedZoomLocations; i++){
// sqg.zoomArray.push(Math.round(1.0/(Math.pow(sqg.scaleFactor,i))*100)/100);
// }
sqg.zoomMin = Math.max(1.0 - sqg.scaleDelta*sqg.fixedZoomLocations, sqg.zoomMinDefault);
sqg.zoomMax = Math.min(1.0 + sqg.scaleDelta*sqg.fixedZoomLocations, sqg.zoomMaxDefault);
/**
 * An list of endangered species taxids (stored in the database)
 */
sqg.endangeredSpecies = new Array();

/**
 * An list of threatened species taxids (stored in the database)
 */
sqg.threatenedSpecies = new Array();

/**
 * An Object mapping taxids to ecosIds for endangered species taxids (stored in
 * the database)
 */
sqg.ecosIds = new Object();

/**
 * An alphabetized list of model organism taxids (stored in the database)
 */
sqg.modelOrganisms = new Array();

/**
 * The index will be accessible using the speciesIndexes1. Each entry will be a
 * speciesObject.
 */
sqg.speciesObjects = new Array();

sqg.speciesCommonObjects = new Array();
sqg.speciesScientificObjects = new Array();

/**
 * This map will have key=index from speciesScientificObjects and value=index
 * from speciesCommonObjects. To be used to keep common shape/color
 */
sqg.scientificToCommonObjects = new Map();  // key=index from
											// speciesScientificObjects,
											// value=index from
											// speciesCommonObjects

/**
 * This array will hold a unique list of level1 species names (either common or
 * scientific)
 */
sqg.speciesIndexes = new Array();

sqg.speciesCommonIndexes = new Array();
sqg.speciesScientificIndexes = new Array();



/**
 * An array of taxGroup objects from 0 to n, going left to right in the plot
 */
sqg.taxGroups = new Array();

/**
 * An array of taxonomy group names in order. Can be used to get an index into
 * the taxGroups1 array
 */
sqg.taxIndexes = new Array();

/**
 * An array of species names currently in the legend for Level 1
 */
sqg.legendNames = new Array();

/**
 * The data array of LevelOneReportRows
 */
sqg.levelOneReportRows = new Array();

/**
 * The data array of LevelTwoReportRows
 */
sqg.levelTwoReportRows = new Array();

/**
 * An array with two entries 1 and 2 (0 is unused). Each is a boolean indicating
 * whether orthos are present.
 * 
 * @returns
 */
sqg.noOrthos = new Array();

/**
 * An array with two entries 1 and 2 (0 is unused). Each is a boolean indicating
 * whether endangereds are present.
 * 
 * @returns
 */
sqg.noEndangereds = new Array();

/**
 * An array with two entries 1 and 2 (0 is unused). Each is a boolean indicating
 * whether threateneds are present.
 * 
 * @returns
 */
sqg.noThreateneds = new Array();

/**
 * An array with two entries 1 and 2 (0 is unused). Each is a boolean indicating
 * whether models are present.
 * 
 * @returns
 */
sqg.noModels = new Array();

function svgPlot() {
	this.svgObjectInDOM;
	this.layer = -1;
}

function level1DataRow() {
	this.accession = "NP_001009012.1";
	this.proteinCount = 86333;
	this.speciesTaxId = 9598;
	this.taxonomyName = "Mammalia";
	this.defaultTaxonomyName = "only for primary";
	this.taxonomyLevel = "class";
	this.taxonomyTaxid = 40674;
	this.scientificName = "Pan troglodytes";
	this.commonName = "chimpanzee";
	this.proteinName = "androgen receptor";
	this.hitLength = 911;
	this.identity = 911;
	this.positives = 911;
	this.evalue = 0.0;
	this.blastPBitScore = 1873.6;
	this.ortholog = "Y";
	this.percentSimilarity = 1.0;
	this.cutoff = 16.796221178479932;
	this.commonDomainCount = 69;
	this.susceptible = "Y";
	this.endDate = 1502387525;
	this.updateVersion = 2;
	this.isDup = 0;
	this.eukaryote = true;
	this.partialProtein = false;
	this.percSimOver = false;
}

function level2dataRow() {
	this.accession = "NP_001009012.1";
	this.blastPBitScore = 514.227;
	this.commonName = "chimpanzee";
	this.commonNameClass = "";
	this.cutoff = 41.34672041724764;
	this.defaultTaxonomyName = "Mammalia";
	this.domainId = "TODO";
	this.domainName = "TODO";
	this.endDate = 1502714319;
	this.eukaryote = true;
	this.evalue = 0.0;
	this.hitLength = 246;
	this.identity = 246;
	this.isDup = 0;
	this.ortholog = "Y";
	this.partialProtein = false;
	this.percSimOver = false
	this.percentSimilarity = 1.0;
	this.positive = 246;
	this.proteinCount = 86333;
	this.proteinName = "androgen receptor ";
	this.pssmId = "132758";
	this.scientificName = "Pan troglodytes";
	this.speciesTaxId = 9598;
	this.susceptible = "Y";
	this.taxonomyLevel = "only for primary";
	this.taxonomyName = "Mammalia";
	this.taxonomyTaxid = 40674;
	this.updateVersion = 3;
}

function speciesObject(name, taxonomyName, firstIndex) {
	this.name = name;
	this.taxonomyName = taxonomyName;
	this.menuIndex = -1;
	this.speciesMenuIndex = -1;  // Is this used??
	this.legendPos = -1;
	this.legendColor = 'red';
	this.legendSymbol = "not set";
	this.redisplayWhenTaxGroupUnHidden = false;
	this.indexes = new Array();
	this.indexes.push(firstIndex);
	this.commonIndex = -1;  // to be used by speciesScientificObjects only to
							// map to speciesCommonObjects index
}

function taxGroup(name, dist, orthoIndexes, endangeredIndexes, threatenedIndexes, modelIndexes,
		taxSpeciesIndexes, displayColumn) {
	this.name = name;
	this.dist = dist; // sorted low to high list of percent similarities
	this.orthoIndexes = orthoIndexes;
	this.endangeredIndexes = endangeredIndexes;
	this.threatenedIndexes = threatenedIndexes;
	this.modelIndexes = modelIndexes;
	this.taxSpeciesIndexes = taxSpeciesIndexes;
	this.displayColumn = displayColumn; // initiate at position in taxGroups
	var n = dist.length;
	this.n = n;
	this.outliersLow = new Array();
	this.outliersHigh = new Array();
	if (n == 0) {
		// place holder
	} else if (n == 1) {
		var v = dist[0];
		this.min = v;
		this.max = v;
		this.mean = v;
		this.median = v;
		this.percent25 = v;
		this.percent75 = v;
		this.IRQrange = 0;
		this.minCutoff = v;
		this.maxCutoff = v;
		this.whiskerMin = v;
		this.whiskerMax = v;
	} else {
		this.min = dist[0];
		this.max = dist[n - 1];
		if (n % 2 == 0) {
			this.median = (dist[n / 2 - 1] + dist[n / 2]) / 2;
		} else {
			this.median = dist[(n - 1) / 2];
		}
		var sum = 0;
		for (var i = 0; i < n; i++) {
			sum += dist[i];
		}
		this.mean = sum / n;
		this.percent25 = getPercentile(0.25, dist);
		this.percent75 = getPercentile(0.75, dist);
		this.IRQrange = this.percent75 - this.percent25;
		this.minCutoff = Math.max(0, this.percent25 - (1.5 * this.IRQrange));
		this.maxCutoff = Math.min(100, this.percent75 + (1.5 * this.IRQrange));
		this.whiskerMin = this.min;
		this.whiskerMax = this.max;
		if (this.min < this.minCutoff) {
			for (var i = 0; i < n; i++) {
				if (dist[i] >= this.minCutoff) {
					this.whiskerMin = dist[i];
					break;
				}
				this.outliersLow.push(dist[i]);
			}
		}
		if (this.max > this.maxCutoff) {
			for (var i = n - 1; i >= 0; i--) {
				if (dist[i] <= this.maxCutoff) {
					this.whiskerMax = dist[i];
					break;
				}
				this.outliersHigh.push(dist[i]);
			}
		}
	}
}

function legendSpeciesVar(rowIndex, levelNumber) {
	this.rowIndex = rowIndex;
	var rows = sqg.levelOneReportRows;
	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
	}
	this.speciesName = rows[rowIndex].commonName;
	this.symbolShape = "none";
	this.displayOrder = -1;
	this.colorIndex = 0;
}

function loadBoxPlot(levelNumber){
	var divDataHolder = null;
	var endHolder = null;
	var modelHolder = null;
	var dataTarget = null;
	var resetBoxPlot = false;
	
	if ((levelNumber == 1 && sqg.resetBoxPlot1) || (levelNumber == 2 && sqg.resetBoxPlot2)){
		resetBoxPlot = true;
	}
	
	if (resetBoxPlot){
	if (levelNumber == 1) {	
		if (sqg.endangeredSpecies.length == 0) {
			endHolder = document
					.getElementById("levOneVizForm:endangeredSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var endangeredPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<endangeredPairs.length;i++){
					var taxid = endangeredPairs[i].taxid;
					sqg.endangeredSpecies.push(taxid);
					sqg.ecosIds[taxid]=endangeredPairs[i].ecosId;
// console.log(endangeredPairs[i], endangeredPairs[i].ecosId);
				}
// sqg.endangeredSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.threatenedSpecies.length == 0) {
			endHolder = document
					.getElementById("levOneVizForm:threatenedSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var threatPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<threatPairs.length;i++){
					var taxid = threatPairs[i].taxid;
					sqg.threatenedSpecies.push(taxid);
					sqg.ecosIds[taxid]=threatPairs[i].ecosId;
// console.log(threatPairs[i], threatPairs[i].ecosId);

				}
// sqg.threatenedSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.modelOrganisms.length == 0) {
			modelHolder = document
					.getElementById("levOneVizForm:modelOrganismsJSON");
			if (modelHolder.innerHTML.length > 0) {
				sqg.modelOrganisms = JSON.parse(modelHolder.innerHTML);
			}
		}

		divDataHolder = document
				.getElementById("levOneVizForm:levelOneReportJSON");
		if (divDataHolder.innerHTML.length > 0) {
			sqg.levelOneReportRows = JSON.parse(divDataHolder.innerHTML);
			divDataHolder.innerHTML = ""; // Clear contents
		}
		if (sqg.levelOneReportRows.length == 0) {
			handleEmptyBoxPlot(levelNumber);
			return;
		}
	} else if (levelNumber == 2) {
		if (sqg.endangeredSpecies.length == 0) {
			endHolder = document
					.getElementById("levTwoVizForm:endangeredSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var endangeredPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<endangeredPairs.length;i++){
					var taxid = endangeredPairs[i].taxid;
					sqg.endangeredSpecies.push(taxid);
					sqg.ecosIds[taxid]=endangeredPairs[i].ecosId;
				}
			}
// sqg.endangeredSpecies = JSON.parse(endHolder.innerHTML);
		}
		if (sqg.threatenedSpecies.length == 0) {
			endHolder = document
					.getElementById("levTwoVizForm:threatenedSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var threatPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<threatPairs.length;i++){
					var taxid = threatPairs[i].taxid;
					sqg.threatenedSpecies.push(taxid);
					sqg.ecosIds[taxid]=threatPairs[i].ecosId;
				}
// sqg.threatenedSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.modelOrganisms.length == 0) {
			modelHolder = document
					.getElementById("levTwoVizForm:modelOrganismsJSON");
			if (modelHolder.innerHTML.length > 0) {
				sqg.modelOrganisms = JSON.parse(modelHolder.innerHTML);
			}
		}
		divDataHolder = document
				.getElementById("levTwoVizForm:levelTwoReportJSON");
		if (divDataHolder.innerHTML.length > 0) {
			sqg.levelTwoReportRows = JSON.parse(divDataHolder.innerHTML);
			divDataHolder.innerHTML = ""; // Clear contents
		}
		if (sqg.levelTwoReportRows.length == 0) {
			handleEmptyBoxPlot(levelNumber);
		}
	}
	}
	
	if (resetBoxPlot){
		parseDataRows(levelNumber);
	}
	drawTaxGroups(levelNumber);
	if (resetBoxPlot){
		resetPlotWidths(levelNumber);
		drawTaxGroups(levelNumber);
		resetMissingSpeciesControls(levelNumber);
	}
	window.addEventListener("keydown", svgKeyDown, true);
	window.addEventListener("keyup", svgKeyUp, true);

	window.addEventListener("mouseup", globalMouseUp, true);
	if (resetBoxPlot){
		zoomReset(levelNumber);
	}
	
	pp.barWidthDefault = pp.barWidth;  // set default value for use in
										// resetting width
	if (resetBoxPlot){
		updatePlotControlValues();
		updateBoxWidthDefault();
	}
	
	// disable controls for query tax group
	disableQueryGroup(levelNumber);
	disableTaxMenuToken(levelNumber);
	
	//if (levelNumber == 1){
	//	sqg.resetBoxPlot1 = true;
	//} else if (levelNumber == 2){
	//	sqg.resetBoxPlot2 = true;
	//}
	
	//if (levelNumber == 1){
	//	sqg.resetBoxPlot1 = false;
	//} else if (levelNumber == 2){
	//	sqg.resetBoxPlot2 = false;
	//}
	
}

/**
 * This method<br>
 * 1) copies data from a special div into the sqg.levelOneReportRows or
 * sqg.levelTwoReportRows array<br>
 * 2) clears the contents of that div to avoid excessive reloading<br>
 * 3) parses that to create some objects<br>
 * 4) draws the boxplots and appends the boxplot objects to the SVG.
 * 
 * @param levelNumber
 *            1 or 2 depending on which svg is to be appended to
 */
function loadBoxPlot2(levelNumber) {
	var divDataHolder = null;
	var endHolder = null;
	var modelHolder = null;
	var dataTarget = null;
	/*if (levelNumber == 1) {
		if (sqg.endangeredSpecies.length == 0) {
			endHolder = document
					.getElementById("levOneVizForm:endangeredSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var endangeredPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<endangeredPairs.length;i++){
					var taxid = endangeredPairs[i].taxid;
					sqg.endangeredSpecies.push(taxid);
					sqg.ecosIds[taxid]=endangeredPairs[i].ecosId;
// console.log(endangeredPairs[i], endangeredPairs[i].ecosId);
				}
// sqg.endangeredSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.threatenedSpecies.length == 0) {
			endHolder = document
					.getElementById("levOneVizForm:threatenedSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var threatPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<threatPairs.length;i++){
					var taxid = threatPairs[i].taxid;
					sqg.threatenedSpecies.push(taxid);
					sqg.ecosIds[taxid]=threatPairs[i].ecosId;
// console.log(threatPairs[i], threatPairs[i].ecosId);

				}
// sqg.threatenedSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.modelOrganisms.length == 0) {
			modelHolder = document
					.getElementById("levOneVizForm:modelOrganismsJSON");
			if (modelHolder.innerHTML.length > 0) {
				sqg.modelOrganisms = JSON.parse(modelHolder.innerHTML);
			}
		}

		divDataHolder = document
				.getElementById("levOneVizForm:levelOneReportJSON");
		if (divDataHolder.innerHTML.length > 0) {
			sqg.levelOneReportRows = JSON.parse(divDataHolder.innerHTML);
			divDataHolder.innerHTML = ""; // Clear contents
		}
		if (sqg.levelOneReportRows.length == 0) {
			handleEmptyBoxPlot(levelNumber);
			return;
		}
	} else if (levelNumber == 2) {
		if (sqg.endangeredSpecies.length == 0) {
			endHolder = document
					.getElementById("levTwoVizForm:endangeredSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var endangeredPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<endangeredPairs.length;i++){
					var taxid = endangeredPairs[i].taxid;
					sqg.endangeredSpecies.push(taxid);
					sqg.ecosIds[taxid]=endangeredPairs[i].ecosId;
				}
			}
// sqg.endangeredSpecies = JSON.parse(endHolder.innerHTML);
		}
		if (sqg.threatenedSpecies.length == 0) {
			endHolder = document
					.getElementById("levTwoVizForm:threatenedSpeciesJSON");
			if (endHolder.innerHTML.length > 0) {
				var threatPairs = JSON.parse(endHolder.innerHTML);
				for (var i=0;i<threatPairs.length;i++){
					var taxid = threatPairs[i].taxid;
					sqg.threatenedSpecies.push(taxid);
					sqg.ecosIds[taxid]=threatPairs[i].ecosId;
				}
// sqg.threatenedSpecies = JSON.parse(endHolder.innerHTML);
			}
		}
		if (sqg.modelOrganisms.length == 0) {
			modelHolder = document
					.getElementById("levTwoVizForm:modelOrganismsJSON");
			if (modelHolder.innerHTML.length > 0) {
				sqg.modelOrganisms = JSON.parse(modelHolder.innerHTML);
			}
		}
		divDataHolder = document
				.getElementById("levTwoVizForm:levelTwoReportJSON");
		if (divDataHolder.innerHTML.length > 0) {
			sqg.levelTwoReportRows = JSON.parse(divDataHolder.innerHTML);
			divDataHolder.innerHTML = ""; // Clear contents
		}
		if (sqg.levelTwoReportRows.length == 0) {
			handleEmptyBoxPlot(levelNumber);
		}
	}*/

	parseDataRows(levelNumber);
	drawTaxGroups(levelNumber);
	resetPlotWidths(levelNumber);
	drawTaxGroups(levelNumber);
	resetMissingSpeciesControls(levelNumber);
	window.addEventListener("keydown", svgKeyDown, true);
	window.addEventListener("keyup", svgKeyUp, true);

	window.addEventListener("mouseup", globalMouseUp, true);
	zoomReset(levelNumber);
	
	pp.barWidthDefault = pp.barWidth;  // set default value for use in
										// resetting width
	updatePlotControlValues();
	updateBoxWidthDefault();
	
	// disable controls for query tax group
	disableQueryGroup(levelNumber);
	disableTaxMenuToken(levelNumber);
	// FIXME - These do not need to be added once the viz.css is updated on the front end
	//addCSSClassRule("viz\.css", ".removeThis1", "fill:#ff0000" );
	//addCSSClassRule("viz\.css", ".removeThis2", "fill:#ff0000" );
	//addCSSClassRule("viz\.css", ".xaxis1", "font-size:12pt" );
	//addCSSClassRule("viz\.css", ".xaxis2", "font-size:12pt" );
	//addCSSClassRule("viz\.css", ".yaxis1", "font-size:12pt" );
	//addCSSClassRule("viz\.css", ".yaxis2", "font-size:12pt" );
	// FIXME - Remove above 6 lines in version 4.0
}

function svgKeyDown(e) {
  var code = e.keyCode;
  //  16 = Shift
  //  17 = Ctrl
  //  18 = Alt / Option
  //  91 = Command (Meta) on left in Safari & Chrome
  //  93 = Command (Meta) on right in Safari & Chrome
  // 224 = Command (Meta) in Mozilla
  if (code == 17 || code == 18 || code == 91 || code == 93 || code == 224){
//    changeStyle("viz\.css", ".cursorRemove", "cursor", "pointer");
    changeStyle("viz\.css", ".taxonText1", "cursor", "pointer");
    changeStyle("viz\.css", ".taxonText2", "cursor", "pointer");
  }
  console.log("key: " +code);
}

function svgKeyUp(e) {
  var code = e.keyCode;
  changeStyle("viz\.css", ".taxonText1", "cursor", ' url("../resources/images/delete_cursor6.gif"), pointer');
  changeStyle("viz\.css", ".taxonText2", "cursor", ' url("../resources/images/delete_cursor6.gif"), pointer');
//  changeStyle("viz\.css", ".cursorRemove", "cursor", ' url("../resources/images/delete_cursor6.gif"), pointer');
}

var debounceUpdatePlotControlValues = debounce(updatePlotControlValues, 250);

function updatePlotControlValues(){
	var levelNumber = 1;
	if (document.title.startsWith('Visualization for Level Two')){
		levelNumber = 2;
	}
	var bwSpinner;
	var zoomSpinner;
	if (levelNumber == 1){
		bwSpinner = document.getElementById("levOneVizForm:bwSpinner_input");
		zoomSpinner = document.getElementById("levOneVizForm:zoomSpinner_input");
	} else if (levelNumber == 2){
		bwSpinner = document.getElementById("levTwoVizForm:bwSpinner_input");
		zoomSpinner = document.getElementById("levTwoVizForm:zoomSpinner_input");
	}
	bwSpinner.value = pp.barWidth;
	zoomSpinner.value = Math.round(pp.zoomFactor * 100);
// console.log("Setting zoomSpinner value: " + zoomSpinner.value);
	
//	if (levelNumber == 1){
//		updateBoxPlotBeanValues1([{
//			name : 'boxWidth',
//			value : pp.barWidth
//		}])
//	} else if (levelNumber == 2){
//		updateBoxPlotBeanValues2([{
//			name : 'boxWidth',
//			value : pp.barWidth
//		}])
//	}
}

function updateBoxWidthDefault(){
	var levelNumber = 1;
	if (document.title.startsWith('Visualization for Level Two')){
		levelNumber = 2;
	}
	
	if (levelNumber == 1){
		updateBoxPlotBeanValues1([{
			name : 'boxWidth',
			value : pp.barWidth
		}])
	} else if (levelNumber == 2){
		updateBoxPlotBeanValues2([{
			name : 'boxWidth',
			value : pp.barWidth
		}])
	}
}

function handleEmptyBoxPlot(levelNumber){
	console.log("inside HandleEmptyBoxPlot");
	var downloadBtn;
	var controlsBtn
	if (levelNumber == 1){
		downloadBtn = document.getElementById("levOneVizForm:downloadBoxPlot");
		controlsBtn = document.getElementById("levOneVizForm:sizeControls");
	} else if (levelNumber == 2){
		downloadBtn = document.getElementById("levTwoVizForm:downloadBoxPlot");
		controlsBtn = document.getElementById("levTwoVizForm:sizeControls");
	}
	downloadBtn.classList.add("ui-state-disabled");
	controlsBtn.classList.add("ui-state-disabled");
	sqg.noOrthos[levelNumber] = true;
	sqg.noEndangereds[levelNumber] = true;
	sqg.noThreateneds[levelNumber] = true;
	sqg.noModels[levelNumber] = true;
	resetMissingSpeciesControls(levelNumber);
}
function parseDataRows(levelNumber) {
	var rows = sqg.levelOneReportRows;
	var speciesCommonIndexes = new Array();
	var speciesScientificIndexes = new Array();
	var speciesCommonObjects = new Array();
	var speciesScientificObjects = new Array();
	var idPrefix = "levOneVizForm";

	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
		idPrefix = "levTwoVizForm";
	}

	var isPrimary = true;
	if (rows[0].taxonomyLevel == null || rows[0].taxonomyLevel == undefined) {
		isPrimary = false;
		for (var i = 0; i < rows.length; i++) {
			rows[i].taxonomyLevel = "class";
		}
	}
	var taxIndexes = new Array();
	var dist = new Array();
	sqg.noOrthos[levelNumber] = true;
	sqg.noEndangereds[levelNumber] = true;
	sqg.noThreateneds[levelNumber] = true;
	sqg.noModels[levelNumber] = true;

	var orthoIndexes = new Array();
	var endangeredIndexes = new Array();
	var threatenedIndexes = new Array();
	var modelIndexes = new Array();
	var taxSpeciesIndexes = new Array();
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		var taxonomyName = row.taxonomyName;
		var commonName = row.commonName;
		var scientificName = row.scientificName;
		
		var	speciesCommonIndex = speciesCommonIndexes.indexOf(commonName);
		if (speciesCommonIndex == -1) {
			var speciesObj = new speciesObject(commonName, taxonomyName, i);
			speciesCommonIndexes.push(commonName);
			speciesCommonObjects.push(speciesObj);
			// note that speciesCommonObjects stays parallel with array
			// speciesCommonIndex
		} else {
			speciesCommonObjects[speciesCommonIndex].indexes.push(i);
		}
		var	speciesScientificIndex = speciesScientificIndexes.indexOf(scientificName);
		if (speciesScientificIndex == -1) {
			var speciesObj = new speciesObject(scientificName, taxonomyName, i);
			speciesScientificIndexes.push(scientificName);
			speciesScientificObjects.push(speciesObj);
			// note that speciesScientificObjects stays parallel with array
			// speciesScientificIndexes
		} else {
			speciesScientificObjects[speciesScientificIndex].indexes.push(i);
		}
		
		var column = taxIndexes.indexOf(taxonomyName);
		if (column == -1) { // New
			taxIndexes.push(taxonomyName);
			column = taxIndexes.indexOf(taxonomyName);
			dist[column] = new Array();
			orthoIndexes[column] = new Array();
			endangeredIndexes[column] = new Array();
			threatenedIndexes[column] = new Array();
			modelIndexes[column] = new Array();
			taxSpeciesIndexes[column] = new Array();
		}

		dist[column].unshift(row.percentSimilarity * 100); // low to high
		if (row.ortholog == "Y") {
			sqg.noOrthos[levelNumber] = false;
			orthoIndexes[column].push(i);
		}
		if (sqg.endangeredSpecies.indexOf(row.speciesTaxId) > -1) {
			sqg.noEndangereds[levelNumber] = false;
			endangeredIndexes[column].push(i);
		}
		if (sqg.threatenedSpecies.indexOf(row.speciesTaxId) > -1) {
			sqg.noThreateneds[levelNumber] = false;
			threatenedIndexes[column].push(i);
		}
		if (sqg.modelOrganisms.indexOf(row.speciesTaxId) > -1) {
			sqg.noModels[levelNumber] = false;
			modelIndexes[column].push(i);
		}
		
		// the taxSpeciesIndexes should be same regardless of whether common or
		// scientific
		// name is used
		if (speciesCommonIndexes.indexOf(commonName) == speciesCommonIndexes.length - 1) {
			taxSpeciesIndexes[column].push(speciesCommonIndexes.indexOf(commonName));
		}
	}

	var taxGroups = new Array();
	for (var i = 0; i < taxIndexes.length; i++) {
		taxGroups
				.push(new taxGroup(taxIndexes[i], dist[i], orthoIndexes[i],
						endangeredIndexes[i], threatenedIndexes[i], modelIndexes[i],
						taxSpeciesIndexes[i], i));
	}

	// Set the menuIndex # for each speciesObject
	// var speciesMenu = document
	// .getElementById("levOneVizForm:speciesMenu_panel");
	// if (levelNumber == 2) {
	// speciesMenu = document
	// .getElementById("levTwoVizForm:speciesMenu2_panel");
	// }
	
	// This step will have to be repeated first time user changes between
	// common/scientific
	// in order to populate menuIndex for other speciesObject
	var allLegendNames = getSpeciesMenuItems(levelNumber);
	var activeLegendNames = getLegendSpeciesItems(levelNumber);
	for (var i = 0; i < allLegendNames.length; i++) {
		var name = allLegendNames[i];
		if (sqg.useCommon){
			speciesCommonObjects[speciesCommonIndexes.indexOf(name)].menuIndex = i;
		} else {
			speciesScientificObjects[speciesScientificIndexes.indexOf(name)].menuIndex = i;
		}
	}

	sqg.speciesCommonIndexes = speciesCommonIndexes;
	sqg.speciesCommonObjects = speciesCommonObjects;
	sqg.speciesScientificIndexes = speciesScientificIndexes;
	sqg.speciesScientificObjects = speciesScientificObjects;
	if (sqg.useCommon){
		sqg.speciesObjects = speciesCommonObjects;
		sqg.speciesIndexes = speciesCommonIndexes;
	} else {
		sqg.speciesObjects = speciesScientificObjects;
		sqg.speciesIndexes = speciesScientificIndexes;
	}
	sqg.taxGroups = taxGroups;
	sqg.taxIndexes = taxIndexes;
	sqg.legendNames = activeLegendNames;
	resetMissingSpeciesControls(levelNumber);
}

function getAllTaxMenuItems(levelNumber) {
	var resultArray = new Array();
	var taxPanel = document.getElementById("levOneVizForm:taxMenu_panel");
	if (levelNumber == 2) {
		taxPanel = document.getElementById("levTwoVizForm:taxMenu2_panel");
	}
	var list = taxPanel.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	for (var i = 0; i < list.length; i++) {
		resultArray.push(list[i].dataset.itemValue);
	}
	return resultArray;
}

function getTaxMenuCheckedItems(levelNumber) {
	var resultArray = new Array();
	var taxPanel = document.getElementById("levOneVizForm:taxMenu_panel");
	if (levelNumber == 2) {
		taxPanel = document.getElementById("levTwoVizForm:taxMenu2_panel");
	}
	var list = taxPanel.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	for (var i = 0; i < list.length; i++) {
		if (list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild.classList
				.contains("ui-icon-check")) {
			resultArray.push(list[i].dataset.itemValue);
		}
	}
	return resultArray;
}

function getSpeciesMenuItems(levelNumber) {
	var resultArray = new Array();
	var speciesMenu = document
			.getElementById("levOneVizForm:speciesMenu_panel");
	if (levelNumber == 2) {
		speciesMenu = document
				.getElementById("levTwoVizForm:speciesMenu2_panel");
	}
	var list = speciesMenu.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	for (var i = 0; i < list.length; i++) {
		var name = list[i].dataset.itemValue;
		resultArray.push(name);
	}
	return resultArray;
}

function getLegendSpeciesItems(levelNumber) {
	var resultArray = new Array();
	var speciesMenu = document.getElementById("levOneVizForm:speciesMenu");
	if (levelNumber == 2) {
		speciesMenu = document.getElementById("levTwoVizForm:speciesMenu2");
	}

	// These are the species already selected, not the ones possibly selected
	var list = speciesMenu.firstElementChild.nextElementSibling.nextElementSibling.childNodes;
	for (var i = 0; i < list.length; i++) {
		resultArray.push(list[i].dataset.itemValue);
	}
	
	if (areOrthosVisible(levelNumber)){
		resultArray.unshift("Ortholog Candidate");
	} else if (areEndangeredsVisible(levelNumber)){
		resultArray.unshift("Endangered Species");
	} else if (areThreatenedsVisible(levelNumber)){
		resultArray.unshift("Threatened Species");
	} else if (areModelsVisible(levelNumber)) {
		resultArray.unshift("Common Model Organism");
	}
	return resultArray;
}

function getDisplayedBoxPlotTaxNames(levelNumber) {
	var resultArray = new Array();
	var tg = sqg.taxGroups;
	for (var i = 0; i < tg.length; i++) {
		var taxGroup = tg[i];
		if (taxGroup.displayColumn > -1) {
			resultArray.push(taxGroup.name);
		}
	}
	return resultArray;
}

function disableFirstTaxonButtons(levelNumber) {
	var taxPanel = document.getElementById("levOneVizForm:taxMenu_panel");
	var taxMenu = document.getElementById("levOneVizForm:taxMenu");

	if (levelNumber == 2) {
		taxPanel = document.getElementById("levTwoVizForm:taxMenu2_panel");
		taxMenu = document.getElementById("levTwoVizForm:taxMenu2");
	}
	if (taxPanel != null && taxPanel != undefined) {
		var list = taxPanel.firstElementChild.nextElementSibling.firstElementChild.childNodes;
		if (list.length > 0) {
			var item = list[0];
			var div = item.firstElementChild.firstElementChild.nextElementSibling;
			div.classList.remove('ui-state-disabled');

			// ----------------
			var itemContainer = taxMenu
					.getElementsByClassName('ui-selectcheckboxmenu-multiple-container')[0];
			if (itemContainer.childElementCount == 0) {
				// just finished deselecting all need to add queryGroup token to
				// taxMenu
				var taxMenuItems = taxPanel
						.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
						.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
				var item = taxMenuItems
						.getElementsByClassName('ui-selectcheckboxmenu-item')[0];
				var itemChkBox = item.getElementsByClassName('ui-chkbox')[0]
						.getElementsByClassName('ui-chkbox-box')[0];
				// If not checked now
				if (!itemChkBox.classList.contains("ui-state-active")) {
					itemChkBox.click(); // Turn it on
				} else {
					itemChkBox.click(); // Turn it on
					itemChkBox.click(); // Turn it on
				}
			}
			// ----------------

			div.classList.add('ui-state-disabled');
		}
	}

	if (taxMenu != null && taxMenu != undefined) {
		var list = taxMenu.firstElementChild.nextElementSibling.nextElementSibling.childNodes;
		if (list.length > 0) {
			var firstSpan = list[0];
			var box = firstSpan.firstElementChild.nextElementSibling;
			if (box != null && box != undefined) {
				firstSpan.removeChild(box);
			}
		}
	}

}

function drawTaxGroups(levelNumber) {
	clearSVG(levelNumber);
	drawSVGAxes(levelNumber);
	appendSpeciesGs(levelNumber);

	var taxGroups = sqg.taxGroups;
	// resetPlotWidths(levelNumber);
	var firstUnder = -1;
	for (var i = 0; i < taxGroups.length; i++) {
		var tg = taxGroups[i];
		var tgName = tg.name;
		if (tg.displayColumn > -1) {
			drawOneDist(tg.displayColumn, taxGroups[i], levelNumber);
		}
	}
	popSpeciesGsToTop(levelNumber);
	appendPopupG(levelNumber);
	redrawLegend(levelNumber);
	drawHorizCutoffLine(levelNumber);
	checkAndResize(levelNumber);
	addDebuggingBorder(levelNumber);
}

function clearSVG(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	if (svg == null || svg == undefined) {
		return;
	}
	var pp = sqg.plotParams;
	var toClear = svg.firstChild;
	while (toClear != null) {
		svg.removeChild(toClear);
		toClear = svg.firstChild;
	}

}

function addDebuggingBorder(levelNumber) {
	// For debugging - add a border
	var pp = sqg.plotParams;
	var svg = document.getElementById("svg" + levelNumber);
	var b = document.getElementById("border" + levelNumber);
	if (b != null && b != undefined) {
		svg.removeChild(b);
	}

	var border = document.createElementNS('http://www.w3.org/2000/svg', 'path');
	border.setAttribute('stroke', '#dddddd');
	border.setAttribute('stroke-width', 1);
	border.setAttribute('fill', 'none');
	var h = pp.xAxisTextY + pp.bottomRest - 5;
// var w = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1));
// var w = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1)) + 30;
// var w = getBoxPlotMinWidth(levelNumber) + 30;
	var w = getBoxPlotMinWidth(levelNumber);

	
// if (w < pp.minWidth){w = pp.minWidth;}
	border.setAttribute('d', 'M 0 0 l ' + w + ' 0 0 ' + h + ' -' + w + ' 0 z');
	border.id = "border" + levelNumber;
	svg.appendChild(border);
}

function appendSpeciesGs(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var orthoG = document.createElementNS('http://www.w3.org/2000/svg', "g");
	orthoG.id = "orthoG" + levelNumber;
	orthoG.addEventListener('mouseover', displaySpeciesInfo, false);
	orthoG.addEventListener('mouseout', hidePopup, false);
	// orthoG.addEventListener("click",dudeReport,false);
	if (!areOrthosVisible(levelNumber)) {
		orthoG.classList.add("invis");
	}
	svg.appendChild(orthoG);

	var endangeredG = document.createElementNS('http://www.w3.org/2000/svg',
			"g");
	endangeredG.id = "endangeredG" + levelNumber;
	endangeredG.addEventListener('mouseover', displaySpeciesInfo, false);
	endangeredG.addEventListener('mouseout', hidePopup, false);
	endangeredG.style.setProperty('cursor','pointer');
	endangeredG.addEventListener('click', openEcosPage, false);

	if (!areEndangeredsVisible(levelNumber)) {
		endangeredG.classList.add("invis");
	}
	svg.appendChild(endangeredG);
	
	var threatenedG = document.createElementNS('http://www.w3.org/2000/svg',
	"g");
	threatenedG.id = "threatenedG" + levelNumber;
	threatenedG.addEventListener('mouseover', displaySpeciesInfo, false);
	threatenedG.addEventListener('mouseout', hidePopup, false);
	threatenedG.style.setProperty('cursor','pointer');
	threatenedG.addEventListener('click', openEcosPage, false);

	if (!areThreatenedsVisible(levelNumber)) {
		threatenedG.classList.add("invis");
	}
	svg.appendChild(threatenedG);

	var modelG = document.createElementNS('http://www.w3.org/2000/svg', "g");
	modelG.id = "modelG" + levelNumber;
	modelG.addEventListener('mouseover', displaySpeciesInfo, false);
	modelG.addEventListener('mouseout', hidePopup, false);

	if (!areModelsVisible(levelNumber)) {
		modelG.classList.add("invis");
	}
	svg.appendChild(modelG);
}

function openEcosPage(event){
	var target = event.target;
	var levelNumber = 1 * target.id.substring(1, 2);
	var rows = sqg.levelOneReportRows;
	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
	}
	var rowId = target.id.substring(3)*1;
	var taxid = rows[rowId].speciesTaxId;
	var ecosId = sqg.ecosIds[taxid];
	if (ecosId < 1){return;}
	console.log(rowId, taxid, ecosId);
	window.open(	"https://ecos.fws.gov/ecp/species/"+ecosId, '');
}

function popSpeciesGsToTop(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var orthoG = document.getElementById("orthoG" + levelNumber);
	var endangeredG = document.getElementById("endangeredG" + levelNumber);
	var threatenedG = document.getElementById("threatenedG" + levelNumber);
	var modelG = document.getElementById("modelG" + levelNumber);
	svg.appendChild(svg.removeChild(orthoG));
	svg.appendChild(svg.removeChild(endangeredG));
	svg.appendChild(svg.removeChild(threatenedG));
	svg.appendChild(svg.removeChild(modelG));
}

function drawSVGAxes(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var pp = sqg.plotParams;

	var textObj = document
			.createElementNS('http://www.w3.org/2000/svg', "text");
	textObj.setAttribute("font-size", "30px");
	textObj.setAttribute("x", pp.xAxisTextX);
	textObj.setAttribute("y", pp.xAxisTextY);
	textObj.appendChild(document.createTextNode("Taxon"));
	textObj.setAttribute("text-anchor", "middle");

	textObj.id = "xAxisLabel" + levelNumber;
	svg.appendChild(textObj);
	textObj = document.createElementNS('http://www.w3.org/2000/svg', "text");
	textObj.setAttribute("font-size", "30px");
	// textObj.setAttribute("x","20");
	// textObj.setAttribute("y","500");
	textObj.setAttribute("transform", pp.yAxisTextMatrix);
	textObj.appendChild(document.createTextNode("Percent Similarity"));
	textObj.id = "yAxisLabel" + levelNumber;
	svg.appendChild(textObj);
	var aPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
	aPath.setAttribute('stroke-width', 1);
	aPath.setAttribute('fill', 'none');
// var w = Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1));
	var w = getBoxPlotMinWidth(levelNumber) - pp.leftMargin;
	console.log("drawSVGAxes width: " + w);
	aPath.setAttribute('d', 'M ' + pp.leftMargin + ' ,' + pp.topMargin
			+ ' l 0,' + pp.height + ' l ' + w + ', 0');
	aPath.setAttribute('stroke', 'black');
	svg.appendChild(aPath);

	var mult = pp.height / 100;
	for (var i = 0; i < 105; i += 10) {
		var aLine = document.createElementNS('http://www.w3.org/2000/svg',
				'line');
		aLine.setAttribute('stroke-width', 1);
		aLine.setAttribute('x1', pp.leftMargin - 5);
		aLine.setAttribute('y1', pp.xAxisYpos - i * mult);
		aLine.setAttribute('x2', pp.leftMargin);
		aLine.setAttribute('y2', pp.xAxisYpos - i * mult);
		aLine.setAttribute('stroke', 'black');
		svg.appendChild(aLine);

		var textObj = document.createElementNS('http://www.w3.org/2000/svg',
				"text");
	  textObj.classList.add('yaxis' + levelNumber);
		textObj.setAttribute("x", pp.leftMargin - 7);
		textObj.setAttribute("y", pp.xAxisYpos - i * mult + 3)
		textObj.setAttribute("text-anchor", "end")
		textObj.appendChild(document.createTextNode(i));
		svg.appendChild(textObj);
	}
}

function drawOneDist(columnNumber, taxGroup, levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var pp = sqg.plotParams;
	var taxIndexes = sqg.taxIndexes;

	var mult = pp.height / 100;
	var halfBar = pp.barWidth / 2;
	var centerX = (columnNumber + 1) * (pp.barWidth + pp.barSpacing)
			+ pp.leftMargin + halfBar;

	if (taxGroup.whiskerMax > taxGroup.whiskerMin) {
		var aLine = document.createElementNS('http://www.w3.org/2000/svg',
				'line');
		aLine.setAttribute('stroke', "black");
		aLine.setAttribute('stroke-width', 1);
		aLine.setAttribute('x1', centerX);
		aLine.setAttribute('y1', pp.xAxisYpos - (taxGroup.whiskerMax * mult));
		aLine.setAttribute('x2', centerX);
		aLine.setAttribute('y2', pp.xAxisYpos - (taxGroup.whiskerMin * mult));
		aLine.setAttribute('stroke', 'black');
		svg.appendChild(aLine);
	}
	if (taxGroup.percent75 > taxGroup.percent25) {
		var box = document
				.createElementNS('http://www.w3.org/2000/svg', "rect");
		var leftOfBox = centerX - halfBar;
		box.setAttributeNS(null, 'x', leftOfBox);
		var topOfBox = pp.xAxisYpos - (taxGroup.percent75 * mult);
		box.setAttributeNS(null, 'y', topOfBox);
		box.setAttributeNS(null, 'height',
				(taxGroup.percent75 - taxGroup.percent25) * mult);
		box.setAttributeNS(null, 'width', pp.barWidth);
		box.setAttributeNS(null, 'stroke', 'black');
		box.setAttributeNS(null, 'fill', '#aaa');
		box.style.setProperty("cursor", "context-menu");
		box.id = "box" + levelNumber + "_" + taxGroup.name;
		box.addEventListener('click', showTable, false);
		svg.appendChild(box);
	}

	var topCircle = document.createElementNS('http://www.w3.org/2000/svg',
			'circle');
	topCircle.setAttributeNS(null, 'cx', centerX);
	topCircle.setAttributeNS(null, 'cy', pp.xAxisYpos - (taxGroup.max * mult));
	topCircle.setAttributeNS(null, 'r', '5');
	topCircle.setAttributeNS(null, 'stroke', 'black');
	topCircle.style.setProperty("cursor", "context-menu");
	topCircle.id = "top" + levelNumber + "_" + taxGroup.name;
	topCircle.addEventListener('click', showTable, false);

	if (taxGroup.max == 100 && columnNumber == 0) {
		topCircle.setAttributeNS(null, 'fill', 'white');
	} else {
		topCircle.setAttributeNS(null, 'fill', 'black');
	}
	svg.appendChild(topCircle);

	var meanLine = document.createElementNS('http://www.w3.org/2000/svg',
			'line');
	meanLine.setAttribute('stroke', "black");
	meanLine.setAttribute('stroke-width', 2);
	meanLine.setAttribute('x1', centerX - halfBar);
	meanLine.setAttribute('y1', pp.xAxisYpos - (taxGroup.mean * mult));
	meanLine.setAttribute('x2', centerX + halfBar);
	meanLine.setAttribute('y2', pp.xAxisYpos - (taxGroup.mean * mult));
	meanLine.setAttribute('stroke', 'black');
	svg.appendChild(meanLine);
	var medianLine = document.createElementNS('http://www.w3.org/2000/svg',
			'line');
	medianLine.setAttribute('stroke', "black");
	medianLine.setAttribute('stroke-width', 1);
	medianLine.setAttribute('x1', centerX - halfBar);
	medianLine.setAttribute('y1', pp.xAxisYpos - (taxGroup.median * mult));
	medianLine.setAttribute('x2', centerX + halfBar);
	medianLine.setAttribute('y2', pp.xAxisYpos - (taxGroup.median * mult));
	medianLine.setAttribute('stroke', 'black');
	svg.appendChild(medianLine);
	for (var i = 0; i < taxGroup.outliersHigh.length; i++) {
		var y = taxGroup.outliersHigh[i];
		if (y == 100) {
			continue;
		}
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		circle.setAttributeNS(null, 'r', '3');
		circle.setAttributeNS(null, 'stroke', 'black');
		circle.setAttributeNS(null, 'fill', 'black');
		svg.appendChild(circle);
	}
	for (var i = 0; i < taxGroup.outliersLow.length; i++) {
		var y = taxGroup.outliersLow[i];
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		circle.setAttributeNS(null, 'r', '3');
		circle.setAttributeNS(null, 'stroke', 'black');
		circle.setAttributeNS(null, 'fill', 'black');
		svg.appendChild(circle);
	}
	var orthoG = document.getElementById("orthoG" + levelNumber);
	for (var i = 0; i < taxGroup.orthoIndexes.length; i++) {
		var orthoIndex = taxGroup.orthoIndexes[i];
		var y;
		if (levelNumber === 1) {
			y = sqg.levelOneReportRows[orthoIndex].percentSimilarity * 100;
		} else if (levelNumber === 2) {
			y = sqg.levelTwoReportRows[orthoIndex].percentSimilarity * 100;
		}
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		var rad = Math.floor(30 / pp.zoomFactor) / 10;
		circle.setAttributeNS(null, 'r', rad);
		// circle.setAttributeNS(null, 'r', '3');
		circle.setAttributeNS(null, 'stroke', 'red');
		circle.setAttributeNS(null, 'fill', 'red');
		// circle.setAttributeNS(null, 'class', 'ortho' + levelNumber);
		circle.id = "o" + levelNumber + "_" + orthoIndex;
		// circle.addEventListener('mouseover', displaySpeciesInfo, false);
		// circle.addEventListener('mouseout', hidePopup, false);
		orthoG.appendChild(circle);
	}

	var endangeredG = document.getElementById("endangeredG" + levelNumber);
	for (var i = 0; i < taxGroup.endangeredIndexes.length; i++) {
		var endangeredIndex = taxGroup.endangeredIndexes[i];
		var y;
		if (levelNumber === 1) {
			y = sqg.levelOneReportRows[endangeredIndex].percentSimilarity * 100;
		} else if (levelNumber === 2) {
			y = sqg.levelTwoReportRows[endangeredIndex].percentSimilarity * 100;
		}
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		// circle.setAttributeNS(null, 'r', '3');
		var rad = Math.floor(30 / pp.zoomFactor) / 10;
		circle.setAttributeNS(null, 'r', rad);
		circle.setAttributeNS(null, 'stroke', 'red');
		circle.setAttributeNS(null, 'fill', 'red');
		// circle.setAttributeNS(null, 'class', 'ortho' + levelNumber);
		circle.id = "e" + levelNumber + "_" + endangeredIndex;
		// circle.addEventListener('mouseover', displaySpeciesInfo, false);
		// circle.addEventListener('mouseout', hidePopup, false);
		endangeredG.appendChild(circle);
	}
	
	var threatenedG = document.getElementById("threatenedG" + levelNumber);
	for (var i = 0; i < taxGroup.threatenedIndexes.length; i++) {
		var threatenedIndex = taxGroup.threatenedIndexes[i];
		var y;
		if (levelNumber === 1) {
			y = sqg.levelOneReportRows[threatenedIndex].percentSimilarity * 100;
		} else if (levelNumber === 2) {
			y = sqg.levelTwoReportRows[threatenedIndex].percentSimilarity * 100;
		}
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		// circle.setAttributeNS(null, 'r', '3');
		var rad = Math.floor(30 / pp.zoomFactor) / 10;
		circle.setAttributeNS(null, 'r', rad);
		circle.setAttributeNS(null, 'stroke', 'red');
		circle.setAttributeNS(null, 'fill', 'red');
		// circle.setAttributeNS(null, 'class', 'ortho' + levelNumber);
		circle.id = "e" + levelNumber + "_" + threatenedIndex;
		// circle.addEventListener('mouseover', displaySpeciesInfo, false);
		// circle.addEventListener('mouseout', hidePopup, false);
		threatenedG.appendChild(circle);
	}

	var modelG = document.getElementById("modelG" + levelNumber);
	for (var i = 0; i < taxGroup.modelIndexes.length; i++) {
		var modelIndex = taxGroup.modelIndexes[i];
		var y;
		if (levelNumber === 1) {
			y = sqg.levelOneReportRows[modelIndex].percentSimilarity * 100;
		} else if (levelNumber === 2) {
			y = sqg.levelTwoReportRows[modelIndex].percentSimilarity * 100;
		}
		var circle = document.createElementNS('http://www.w3.org/2000/svg',
				'circle');
		circle.setAttributeNS(null, 'cx', centerX);
		circle.setAttributeNS(null, 'cy', pp.xAxisYpos - (y * mult));
		// circle.setAttributeNS(null, 'r', '3');
		var rad = Math.floor(30 / pp.zoomFactor) / 10;
		circle.setAttributeNS(null, 'r', rad);
		circle.setAttributeNS(null, 'stroke', 'red');
		circle.setAttributeNS(null, 'fill', 'red');
		// circle.setAttributeNS(null, 'class', 'ortho' + levelNumber);
		circle.id = "m" + levelNumber + "_" + modelIndex;
		// circle.addEventListener('mouseover', displaySpeciesInfo, false);
		// circle.addEventListener('mouseout', hidePopup, false);
		modelG.appendChild(circle);
	}
	// Now axis info
	var aLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
	aLine.setAttribute('stroke', "black");
	aLine.setAttribute('stroke-width', 1);
	aLine.setAttribute('x1', centerX);
	aLine.setAttribute('y1', pp.xAxisYpos);
	aLine.setAttribute('x2', centerX);
	aLine.setAttribute('y2', pp.xAxisYpos + 5);
	aLine.setAttribute('stroke', 'black');
	svg.appendChild(aLine);

	var textObj = document
			.createElementNS('http://www.w3.org/2000/svg', "text");
	//textObj.setAttribute("font-size", "12");
	// textObj.setAttribute("x",centerX);
	// textObj.setAttribute("y",pp.xAxisYpos+7)
	var xPlus = pp.xAxisYpos + 7;
	var yPlus = centerX + Math.floor(pp.barSpacing / 2);
	textObj.setAttribute("transform", "matrix(0,-1,1,0," + yPlus + "," + xPlus
			+ ")");
	textObj.setAttribute("text-anchor", "end")
	textObj.appendChild(document.createTextNode(taxGroup.name));
	// textObj.setAttribute("style", "cursor:pointer");

	textObj.addEventListener('mouseover', showCommonName, false);
	textObj.addEventListener('mouseout', hidePopup, false);
	textObj.classList.add('taxonText' + levelNumber);
	textObj.classList.add("cursorRemove");

	if (taxIndexes.indexOf(taxGroup.name) > 0) {
//		textObj.classList.add('cursorRemove');
		textObj.addEventListener('mouseup', removeTaxon, false);
//		textObj.addEventListener('click', removeTaxon, false);
		textObj.addEventListener('mousedown', dragHighlightTaxon, false);
	}
	svg.appendChild(textObj);
}

function appendPopupG(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var pDivContainer = document.createElement('div');
	pDivContainer.id="popupContainer"+levelNumber;
	var pDiv = document.createElement('div');
	pDivContainer.setAttribute("style", "max-width:600px; margin: 0 auto;");
// var pDiv = document.createElement('div');
	var pDiv = document.createElementNS('http://www.w3.org/1999/xhtml','div');
	pDiv.setAttribute("style", "width:auto; display:table;");
	pDiv.appendChild(document.createTextNode("info"));
	pDiv.id = "popupDiv" + levelNumber;

	var fo = document.createElementNS('http://www.w3.org/2000/svg',
			"foreignObject");
	fo.setAttribute("width", 200); // FOR SOME REASON THIS IS VERY
	// IMPORTANT
	fo.setAttribute("height", 420); // FOR SOME REASON THIS IS VERY
	// IMPORTANT
	pDivContainer.appendChild(pDiv);
	fo.appendChild(pDivContainer);

	var popupG = document.createElementNS('http://www.w3.org/2000/svg', "g");
	// popupG.setAttribute("transform","matrix(1,0,0,1,200,200)");
	popupG.setAttribute("transform", "translate(100,100)");
	popupG.classList.add("invis");
	popupG.id = "popupG" + levelNumber;
	popupG.appendChild(fo);
	svg.appendChild(popupG);
	// return popupG;
}

function handleTaxMenuClick(levelNumber) {
	resetVisibleTaxa(levelNumber);
	drawTaxGroups(levelNumber);
	if (levelNumber == 1){
		PF('loadDialog').hide();
	} else if (levelNumber == 2){
		PF('loadDialog2').hide();
	}
}

function handleSpeciesMenuClick(levelNumber) {
	sqg.legendNames = getLegendSpeciesItems(levelNumber);
	redrawLegend(levelNumber);
// if (levelNumber == 1){
// PF('loadDialog').hide();
// } else if (levelNumber == 2){
// PF('loadDialog2').hide();
// }
}

function resizeForLegend(levelNumber){
	var legend = document.getElementById("legend"+levelNumber);
	if (legend != null){
	var svg = document.getElementById("svg"+levelNumber);
	var pt = svg.createSVGPoint();
	pt.x = legend.getBoundingClientRect().right
	var svgPt = svgFromScreen(pt.x, pt.y, levelNumber);
	svgPt.x += 10;  // add buffer
	svgPt.x = Math.floor(svgPt.x);

	var minWidth = getBoxPlotMinWidth(levelNumber);
	var border = document.getElementById("border"+levelNumber);
	if (border == null){ return; }
	var tmpDim = border.getAttribute("d").split(' ');
	var h = tmpDim[7];  // height
	var prevWidth = tmpDim[4];  // start with previous width

	w = Math.max(Math.max(svgPt.x, sqg.plotParams.minWidth), minWidth);

	svg.viewBox.baseVal.width = w;
	border.setAttribute('d', 'M 0 0 l ' + w + ' 0 0 ' + h + ' -' + w + ' 0 z');
	}
}

// gets minimum width needed to fit boxplot (without fitting legend)
function getBoxPlotMinWidth(levelNumber){
	var pp = sqg.plotParams;
	var width = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) * (getTaxMenuCheckedItems(levelNumber).length + 1));
	return Math.max(width, pp.minWidth);
}

function redrawLegend(levelNumber) {
	var legendNames = sqg.legendNames;
	removeLegend(levelNumber);
	
	if (sqg.useCommon){ sqg.commonLegendInfo = new Array(); }
	
	var optionalOffset = 0;   // optional offset = 1 if ortho/endangered/etc
								// are shown
	if (checkIfLegendOption(sqg.legendNames[0])){
		optionalOffset = 1;
	}

	// var legendSpeciesIndexes = getLegendSpeciesIndexes(levelNumber);
	var legendRowCount = legendNames.length;
	if (legendRowCount == 0) {
		return;
	}
	var threshold = 80;
	var legendRowHeight = 22;

	var firstUnder = -1;
	var heightThisColumn = -1;
	while (firstUnder < 0) {
		var twoVals = getFirstUnder(threshold, levelNumber);
		firstUnder = twoVals[0];
		heightThisColumn = twoVals[1];
		threshold += 2;
	}
	var rows = sqg.levelOneReportRows;
	var svg = document.getElementById("svg" + levelNumber);
	var pp = sqg.plotParams;
	var taxGroups = sqg.taxGroups;
	var taxIndexes = sqg.taxIndexes;
	var speciesIndexes = sqg.speciesIndexes;
	var speciesObjects = sqg.speciesObjects;

	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
	}
	
	console.log("heightThisColumn: " + heightThisColumn);

	var maxRows = Math.floor(pp.height * ((100 - heightThisColumn) / 100)
			/ legendRowHeight) - 1;
	var legend = document.createElementNS('http://www.w3.org/2000/svg', "g");
	var legend2 = document.createElementNS('http://www.w3.org/2000/svg', "g");
	legend2.id = "legendTargets" + levelNumber;
	legend2.addEventListener('mouseover', displaySpeciesInfo, false);
	legend2.addEventListener('mouseout', hidePopup, false);

	// var xXform = pp.leftMargin+(pp.barWidth + pp.barSpacing)*numBars;
	var leftSide = leftSide = pp.leftMargin + (2 + firstUnder)
			* (pp.barSpacing + pp.barWidth);
	if (legendRowCount <= 2*maxRows && leftSide < 400) {
		leftSide = 400;
	}
	// if (isNaN(leftSide)) {
	// leftSide = 300;
	// }
	legend.setAttribute("transform", "translate(" + leftSide + ",20)");
	legend.id = "legend" + levelNumber;
	svg.appendChild(legend);
	svg.appendChild(legend2);
	var columnLeftSide = 0;
	var colorMinus = 0;
	var widestTextWidth = 0;
	var rowsDrawn = 0;

	for (var i = 0; i < legendNames.length; i++) {
		var isSpecies = !checkIfLegendOption(sqg.legendNames[i]);
		var legendSpeciesName = legendNames[i];
		console.log("drawing legend: " + legendSpeciesName);
		var legendSpeciesIndex;
		var legendSpeciesObject;
		if (isSpecies){
			legendSpeciesIndex = speciesIndexes.indexOf(legendSpeciesName);
			legendSpeciesObject = speciesObjects[legendSpeciesIndex];
			legendSpeciesObject.legendPos = i;
			var taxonomyName = legendSpeciesObject.taxonomyName;
			var col = taxGroups[taxIndexes.indexOf(taxonomyName)].displayColumn + 1;
		}
		// var legendSpeciesName = legendSpeciesObject.name;

		var legendRow = i;
		var vertPosition = legendRow - rowsDrawn;
		
		if (vertPosition == maxRows) {
			console.log("at vertPosition == maxRows");
			vertPosition = 0;
			columnLeftSide += widestTextWidth + 50;
			
			// check to see if legend label is past the right side of plot
			// if so change maxRows to allow more legend rows
			var pt = svg.createSVGPoint();
			
			// get right side of last taxon text
			var taxonText = document.getElementsByClassName("taxonText" + levelNumber);
			pt.x = taxonText[taxonText.length-1].getBoundingClientRect().right;
			var svgPt = svgFromScreen(pt.x, pt.y, levelNumber);
			var lastTaxonTextRightSide = Math.round(svgPt.x) + 30;  // 30 pixel
																	// buffer
			
			// get right side of cutoff line
			var cutoffLine = document.getElementById("cutoffLine" + levelNumber);
			var cutoffLineTop = 0;
			if(cutoffLine != null){
				pt.x = cutoffLine.getBoundingClientRect().right;
				if (levelNumber == 1){
					cutoffLineTop = sqg.levelOneReportRows[0].cutoff + 5;  // a
																		// small
																		// vertical
																		// buffer
				} else if (levelNumber == 2){
					cutoffLineTop = sqg.levelTwoReportRows[0].cutoff + 5;  // a
																		// small
																		// vertical
																		// buffer
				}
			} else {
				pt.x = svg.viewBox.baseVal.width;
			}
			svgPt = svgFromScreen(pt.x, pt.y, levelNumber);
			var cutoffLineRightSide = Math.round(svgPt.x) + 30;  // 30 pixel
																	// buffer
			var cutoffLineTop = Math.round(cutoffLineTop);
			
			var altMaxRows;
			if(columnLeftSide+leftSide > cutoffLineRightSide) { 
				altMaxRows = maxRows = Math.floor(pp.height/ legendRowHeight) - 1;
			} else if (cutoffLineTop != 0){
				altMaxRows = Math.floor(pp.height * ((100 - cutoffLineTop) / 100)
						/ legendRowHeight) - 1;
			}
			
			rowsDrawn = legendRow;
			widestTextWidth = 0;
			var newColLeft = Math
					.floor((leftSide + columnLeftSide - pp.leftMargin)
							/ (pp.barSpacing + pp.barWidth));
			for (var j = 0; j < taxGroups.length; j++) {
				if (taxGroups[j].displayColumn >= (newColLeft - 1)) {
					maxRows = Math.floor(pp.height
							* ((100 - taxGroups[j].max) / 100)
							/ legendRowHeight) - 1;
					break;
				}
			}			
			maxRows = Math.min(maxRows, altMaxRows);
		}
		var textObj = document.createElementNS('http://www.w3.org/2000/svg',
				"text");
		textObj.setAttribute("font-size", "16");
		textObj.setAttribute("text-anchor", "start");
		textObj.setAttribute("x", columnLeftSide + 20);
		textObj.setAttribute("y", legendRowHeight * vertPosition + 20);
		textObj.id = "legText" + levelNumber + "_" + legendRow;
		if (isSpecies){
			textObj.classList.add("cursorRemove");
		}
		textObj.addEventListener('click', removeFromLegend, false);

		textObj.appendChild(document.createTextNode(legendSpeciesName));
		legend.appendChild(textObj);
		widestTextWidth = Math.max(widestTextWidth, textObj.getBBox().width);

		var symbol = document.createElementNS('http://www.w3.org/2000/svg',
				'path');
		var vCoord = legendRowHeight * vertPosition + 15;
		var dPosition = "M " + columnLeftSide + " " + vCoord;
		var dShape = "";
//		if (isSpecies){
//			var symbolBlock = (legendRow-optionalOffset) % 5;
//			// var symbolBlock = Math.floor(legendRow / 19) % 5;
//			dShape += sqg.shapes[symbolBlock];
//// switch (symbolBlock) {
//// case 0:
//// dShape += " m -3.5 3.5 a 5 5 0 1 1 0.01 0.01"; // circle
//// break;
//// case 1:
//// dShape += " m 0 6 l -4 -6 l 4 -6 l 4 6z"; // diamond
//// break;
//// case 2:
//// dShape += " m 4 4 l -8 0 l 0 -8 l 8 0z"; // square
//// break;
//// case 3:
//// dShape += " m -5 5 l 5 -10 l 5 10z"; // triangle pointing up
//// break;
//// case 4:
//// dShape += " m -5 -5 l 5 10 l 5 -10z"; // triangle pointing down
//// break;
//// }
//		} else {
//			dShape = sqg.shapes[0]; // circle
//// dShape = " m -3.5 3.5 a 5 5 0 1 1 0.01 0.01";
//		}
		
		
		var color;
		if (isSpecies){
//			symbol.setAttributeNS(null, 'fill', sqg.colors[(legendRow - optionalOffset) % 19]);
			
			if (sqg.maintainCommonLegendInfo && !sqg.useCommon){
				var commonIndex = sqg.scientificToCommonObjects.get(legendSpeciesIndex);
				var commonName = sqg.speciesCommonObjects[commonIndex].name;
				for (var j=0; j<sqg.commonLegendInfo.length; j++){
					legendInfo = sqg.commonLegendInfo[j];
					if (legendInfo.name == commonName){
						dShape = legendInfo.legendSymbol;
						color = legendInfo.legendColor;	
						break;
					}
				}
				//This next if statement is only possible if species is added when scientific name type 
				// is selected AND "group by common name" is checked
				if (dShape == ""){ 
					//common name associated with scientific name is not in sqg.commonLegendInfo
					//determine next color and shape from previous commonLegendInfo entry and add to array
					dShape = getNextShape(sqg.commonLegendInfo[sqg.commonLegendInfo.length-1].legendSymbol);
					color = getNextColor(sqg.commonLegendInfo[sqg.commonLegendInfo.length-1].legendColor);
					//add to sqg.commonLegendInfo
					var newLegendSpecies = new speciesObject(commonName, sqg.speciesCommonObjects[commonIndex].taxonomyName, sqg.speciesCommonObjects[396].indexes[0]);
					newLegendSpecies.legendColor = color;
					newLegendSpecies.legendSymbol = dShape;
					sqg.commonLegendInfo.push(newLegendSpecies);
				}
			} else {
				var symbolBlock = (legendRow-optionalOffset) % 5;
				dShape += sqg.shapes[symbolBlock];
				color = sqg.colors[(legendRow - optionalOffset) % 19];
			}
		} else {
//			symbol.setAttributeNS(null, 'fill', 'red');
			dShape = sqg.shapes[0]; // circle
			color = 'red';
		}
		
		if (isSpecies){
			legendSpeciesObject.legendColor = color;
			legendSpeciesObject.legendSymbol = dShape;
		}
		if (isSpecies && sqg.useCommon){
			sqg.commonLegendInfo.push(legendSpeciesObject);
		}
		

		symbol.setAttribute('d', dPosition + dShape);
		symbol.setAttributeNS(null, 'fill', color);
		
		symbol.setAttributeNS(null, 'stroke', "#000");
		symbol.setAttributeNS(null, 'stroke-width', 1);
		symbol.id = "leg" + levelNumber + "_" + legendRow;
		// The above ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ id is used to generate
		// the classname below vvv
		if (isSpecies){
			symbol.addEventListener('mouseover', highlightSymbol, false);
			symbol.addEventListener('mouseout', restoreSymbol, false);
		}

		symbol.addEventListener('click', popToTop, false);
		symbol.setAttribute("style", "cursor:pointer");
		legend.appendChild(symbol);
		if (isSpecies){
		for (var j = 0; j < legendSpeciesObject.indexes.length; j++) {
			var speciesId = legendSpeciesObject.indexes[j];
			var row = rows[speciesId];

			var width = pp.leftMargin + pp.barWidth / 2 + col
					* (pp.barWidth + pp.barSpacing);
			var height = pp.xAxisYpos - (pp.height * row.percentSimilarity);
			var symbol2 = document.createElementNS(
					'http://www.w3.org/2000/svg', 'path');

			var dPosition2 = "M " + width + " " + height;

			symbol2.setAttribute('d', dPosition2 + dShape);
			symbol2.setAttributeNS(null, 'fill', color);
			symbol2.setAttributeNS(null, 'stroke', "#000");
			symbol2.setAttributeNS(null, 'stroke-width', 1);
			symbol2.setAttributeNS(null, 'r', '5');
			symbol2.classList.add("ex_leg" + levelNumber + "_" + legendRow);
			// The above classList ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ is a
			// prepended form of string above ^^^
			symbol2.id = "l" + levelNumber + "_" + speciesId;
			// circle.setAttribute("style", "cursor:pointer");
			// symbol2.addEventListener('mouseover', displaySpeciesInfo, false);
			// symbol2.addEventListener('mouseout', hidePopup, false);
			legend2.appendChild(symbol2);

		  }
		}
	}
	
	resizeForLegend(levelNumber);

	// box.setAttributeNS(null, 'height', 10 + seenCommonNames.length * 22);
}

function checkIfLegendOption(legendString){
	
	switch(legendString){
	case 'Ortholog Candidate':
	case 'Threatened Species':
	case 'Endangered Species':
	case 'Common Model Organism':
		return true;
	}
	return false;
}

function removeLegendItems(levelNumber, list) {
	if (list.length == 0) {
		return;
	}
	var joinedList = list.join("\t");
	for (var i = 0; i < list.length; i++) {
		sqg.legendNames.splice(sqg.legendNames.indexOf(list[i]), 1);
	}
	if (levelNumber == 1) {
		updateSpeciesMenu1([ {
			name : 'action',
			value : 'remove'
		}, {
			name : 'items',
			value : joinedList
		} ]);
	} else if (levelNumber == 2) {
		updateSpeciesMenu2([ {
			name : 'action',
			value : 'remove'
		}, {
			name : 'items',
			value : joinedList
		} ]);
	}
}

function restoreLegendItems(levelNumber, list) {
	if (list.length == 0) {
		return;
	}
	var joinedList = list.join("\t");
	for (var i = 0; i < list.length; i++) {
		if (!sqg.legendNames.includes(list[i])){
			sqg.legendNames.push(list[i]);
		}
	}
	if (levelNumber == 1) {
		updateSpeciesMenu1([ {
			name : 'action',
			value : 'restore'
		}, {
			name : 'items',
			value : joinedList
		} ]);
	} else if (levelNumber == 2) {
		updateSpeciesMenu2([ {
			name : 'action',
			value : 'restore'
		}, {
			name : 'items',
			value : joinedList
		} ]);
	}
}

function drawHorizCutoffLine(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);

	getTaxMenuCheckedItems(levelNumber).length;
	var pp = sqg.plotParams;
	var horizDashed = document.createElementNS('http://www.w3.org/2000/svg',
			'path');
	horizDashed.setAttribute('stroke', "black");
	horizDashed.setAttribute('stroke-width', 2);
	horizDashed.setAttribute('stroke-dasharray', '5, 5');
	horizDashed.setAttribute('fill', 'none');
	var dashedHeight;
	if (levelNumber == 1) {
		var dashedHeight = pp.xAxisYpos
				- (pp.height * sqg.levelOneReportRows[0].cutoff / 100);
	} else if (levelNumber == 2) {
		var dashedHeight = pp.xAxisYpos
				- (pp.height * sqg.levelTwoReportRows[0].cutoff / 100);
	}
	
	var w = getBoxPlotMinWidth(levelNumber) - pp.leftMargin;
	horizDashed.setAttribute('d', 'M ' + pp.leftMargin + ' ,' + dashedHeight
			+ ' l ' + w + ', 0');
	horizDashed.setAttribute('stroke', 'black');
	horizDashed.id = "cutoffLine" + levelNumber;
	horizDashed.addEventListener('mouseover', showCutoffInfo, false);
	horizDashed.addEventListener('mouseout', hideCutoffInfo, false);

	if (svg == null) {
		console.log("Why is svg null?!? " + levelNumber);
	}
	svg.appendChild(horizDashed);
}

function resetPlotWidths(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
// var totalWidth = svg.width.baseVal.value;
	var totalWidth = svg.viewBox.baseVal.width;
	var pp = sqg.plotParams;
	var taxGroups = sqg.taxGroups;

	var freeWidth = totalWidth - pp.leftMargin;
	// var colWidth = pp.barWidth + pp.barSpacing;
	// var colCount = Math.floor(totalWidth/colWidth)-1;
	var newColWidthThird = Math.floor(freeWidth / (3 * (taxGroups.length + 2)));
	if (newColWidthThird < 3) {
		newColWidthThird = 3;
		// FIXME -- need to make SVG wider or viewBox smaller or something or
		// things will run off the edge!
	} else if (newColWidthThird > 30) {
		newColWidthThird = 30;
	}
	
	pp.barWidth = 2 * newColWidthThird;
	pp.barSpacing = newColWidthThird;
	var textHeight = newColWidthThird + 8;

	changeStyle("viz\.css", ".taxonText" + levelNumber, "fontSize", textHeight
			+ "px");
	var minLabelSize = Math.max(30, textHeight);
	document.getElementById("xAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";
	document.getElementById("yAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";
}

function checkAndResize(levelNumber) {
	var max = 140;
	var taxonTexts = document.getElementsByClassName("taxonText" + levelNumber);
	for (var i = 0; i < taxonTexts.length; i++) {
		var height = taxonTexts[i].getBBox().width; // sideways text, so width
		// => height
		max = Math.max(max, height);
	}
	var svg = document.getElementById("svg" + levelNumber);

	var pp = sqg.plotParams;

	pp.bottomTextHeight = 50 + Math.ceil(max / 10) * 10;
	var xLabel = document.getElementById("xAxisLabel" + levelNumber);
	pp.bottomRest = Math.ceil(xLabel.getBBox().height);

	pp.xAxisTextY = pp.xAxisYpos + pp.bottomTextHeight;

	xLabel.setAttribute("y", pp.xAxisTextY);
	var columnCount = getNumberOfVisibleTaxons(levelNumber);
	xLabel.setAttribute("x", pp.leftMargin + xLabel.getBBox().width / 2
			+ +Math.floor(columnCount * (pp.barSpacing + pp.barWidth) / 2));

	var yLabel = document.getElementById("yAxisLabel" + levelNumber);
	pp.marginLeft = 50 + Math.ceil(yLabel.getBBox().height);
	yLabel.setAttribute("y", yLabel.getBBox().height / 2 + 5);
	yLabel.setAttribute("x",
			-(pp.topMargin + pp.height / 2 + yLabel.getBBox().width / 2));


}

function resetMissingSpeciesControls(levelNumber) {
    var orthosAvail = true;
    var endangeredsAvail = true;
    var threatenedsAvail = true;
    var modelsAvail = true;
	var idPrefix = "levOneVizForm";
	if (levelNumber == 2) {
		idPrefix = "levTwoVizForm";
	}

	if (sqg.noOrthos[levelNumber]) {
		// Show text and disable button
		var txt = document.getElementById(idPrefix + ":noOrthoText");
		txt.style.display = "unset";
		orthosAvail = false;
	} 
	if (sqg.noEndangereds[levelNumber]) {
		// Show text and disable button
		var txt = document.getElementById(idPrefix + ":noEndangeredText");
		txt.style.display = "unset";
		endangeredsAvail = false;
	}
	if (sqg.noThreateneds[levelNumber]) {
		// Show text and disable button
		var txt = document.getElementById(idPrefix + ":noThreatenedText");
		txt.style.display = "unset";
		threatenedsAvail = false;
	}
	if (sqg.noModels[levelNumber]) {
		// Show text and disable button
		var txt = document.getElementById(idPrefix + ":noModelText");
		txt.style.display = "unset";
		modelsAvail = false;
	}
	
	if (levelNumber == 1){
		disableBox1Checkbox([{
			name : 'type',
			value : 'ortho'
		}, {
			name : 'avail',
			value : orthosAvail
		}]);
		
		disableBox1Checkbox([{
			name : 'type',
			value : 'endangered'
		}, {
			name : 'avail',
			value : endangeredsAvail
		}]);
		
		disableBox1Checkbox([{
			name : 'type',
			value : 'threatened'
		}, {
			name : 'avail',
			value : threatenedsAvail
		}]);
		
		disableBox1Checkbox([{
			name : 'type',
			value : 'model'
		}, {
			name : 'avail',
			value : modelsAvail
		}]);
		
		
	} else if (levelNumber == 2){
		disableBox2Checkbox([{
			name : 'type',
			value : 'ortho'
		}, {
			name : 'avail',
			value : orthosAvail
		}]);
		
		disableBox2Checkbox([{
			name : 'type',
			value : 'endangered'
		}, {
			name : 'avail',
			value : endangeredsAvail
		}]);
		
		disableBox2Checkbox([{
			name : 'type',
			value : 'threatened'
		}, {
			name : 'avail',
			value : threatenedsAvail
		}]);
		
		disableBox2Checkbox([{
			name : 'type',
			value : 'model'
		}, {
			name : 'avail',
			value : modelsAvail
		}]);
	}
}

function turnPZOff(levelNumber){
// var target = document.getElementById("panZoom" + levelNumber);
// var svg = document.getElementById("svg" + levelNumber);
// target.innerHTML = "Pan & Zoom: OFF";
// target.style.setProperty("background-color", "#ffffff");
// svg.style.setProperty("cursor", "");
// svg.removeEventListener("mousedown", panOn, true);
// window.onwheel = null;
}

//function togglePZEvent(event){
//	togglePZ(event.target);
//}
//function togglePZ(target) {
//	var levelNumber = target.id.substring(7, 8) * 1;
//	var svg = document.getElementById("svg" + levelNumber);
//	if (target.innerHTML.endsWith("OFF")) {
//		target.innerHTML = "Pan & Zoom: ON ";
//		target.style.setProperty("background-color", "#ff8888");
//		svg.style.setProperty("cursor", "grab");
//		svg.addEventListener("mousedown", panOn, true);
//		svg.addEventListener("wheel", wheelZoom, true);
//		// window.addEventListener("wheel",noop,true);
//		window.onwheel = function() {
//			return false;
//		}
//	} else {
//		target.innerHTML = "Pan & Zoom: OFF";
//		target.style.setProperty("background-color", "#ffffff");
//		svg.style.setProperty("cursor", "");
//		svg.removeEventListener("mousedown", panOn, true);
//		window.onwheel = null;
//	}
//}

function panZoomOff(levelNumber){
	console.log("inside panZoomOff level:" + levelNumber);
	
	var svg = document.getElementById("svg" + levelNumber);
	
	console.log("turning pan/zoom off");
	svg.style.setProperty("cursor", "");
	svg.removeEventListener("mousedown", panOn, true);
	window.onwheel = null;
}

function panZoomOnOff(levelNumber){
	console.log("inside panZoomOnOff level:" + levelNumber);
	
	if (levelNumber == 1){
		panZoomValue = document.getElementById("levOneVizForm:hiddenZoomVal").value;
	} else if (levelNumber == 2){
		panZoomValue = document.getElementById("levTwoVizForm:hiddenZoomVal").value;
	}
	var svg = document.getElementById("svg" + levelNumber);
	
	if (panZoomValue == "true") {
		console.log("turning pan/zoom on");
		svg.style.setProperty("cursor", "grab");
		svg.addEventListener("mousedown", panOn, true);
		svg.addEventListener("wheel", wheelZoom, true);
		window.onwheel = function() {
			return false;
		}
	} else {
		console.log("turning pan/zoom off");
		svg.style.setProperty("cursor", "");
		svg.removeEventListener("mousedown", panOn, true);
		window.onwheel = null;
	}
}


function panOn(event) {
	console.log("Inside panOn event");
	var svg = event.target;
	var levelNumber = svg.id.substring(3,4) * 1;
	
	var pt = svg.createSVGPoint();
	pt.x = event.clientX; pt.y = event.clientY;
	var newPt = svgFromScreen(pt.x, pt.y, levelNumber);
	
	svg.style.setProperty("cursor", "grabbing");
	event.target.addEventListener("mousemove", tick, true);
}

function globalMouseUp(event) {
	
	var windowName = window.name;
	var levelNumber = 1;
	if (windowName.endsWith("2")) {
		levelNumber = 2;
	}
	
	if (levelNumber == 1){
		panZoomValue = document.getElementById("levOneVizForm:hiddenZoomVal").value;
	} else if (levelNumber == 2){
		panZoomValue = document.getElementById("levTwoVizForm:hiddenZoomVal").value;
	}
	
	var svg = document.getElementById("svg" + levelNumber);
	if (svg == null || svg == undefined){
		return;
	}
	if (panZoomValue=="false") {
		svg.style.setProperty("cursor", "");
	} else {
		svg.style.setProperty("cursor", "grab");
	}
	svg.removeEventListener("mousemove", tick, true);

	removeTaxonLabelEventListeners(levelNumber);
}

function removeTaxonLabelEventListeners(levelNumber){
  var allTaxa = document.getElementsByClassName("taxonText" + levelNumber);
  for (var i = 0; i < allTaxa.length; i++) {
    var listeningTaxon = allTaxa[i];
    var taxonName = listeningTaxon.innerHTML;
    if (taxonName != sqg.taxGroups[0].name){
      listeningTaxon.removeEventListener("mouseover", toggleSelect, false);
    }
  }
}

function tick(event) {
	var target = event.target;
	var svg = target; // until we determine otherwise
	while (!svg.id.startsWith("svg")) {
		svg = svg.parentNode;
		if (svg == null) {
			console.log("FAILED TO FIND svg ANCESTOR: " + svg);
			return;
		}
	}
	var levelNumber = svg.id.substring(3, 4) * 1;
	var svgScale = svg.parentElement.clientWidth / svg.width.baseVal.value;

	svg.viewBox.baseVal.x -= (event.movementX / svgScale);
	// if (svg.viewBox.baseVal.x < 0) {
	// svg.viewBox.baseVal.x = 0;
	// }
	// if (svg.viewBox.baseVal.x > svgScale * svg.viewBox.baseVal.width) {
	// svg.viewBox.baseVal.x = svgScale * svg.viewBox.baseVal.width;
	// }
	svg.viewBox.baseVal.y -= (event.movementY / svgScale);
	// if (svg.viewBox.baseVal.y < 0) {
	// svg.viewBox.baseVal.y = 0;
	// }
	// if (svg.viewBox.baseVal.y > svgScale * svg.viewBox.baseVal.height) {
	// svg.viewBox.baseVal.y = svgScale * svg.viewBox.baseVal.height;
	// }
}

function getNumberOfVisibleTaxons(levelNumber) {
	var groups = sqg.taxGroups;
	var count = 0;
	for (var i = 0; i < groups.length; i++) {
		if (groups[i].displayColumn > -1) {
			count++;
		}
	}
	return count;
}

function retrieveCSS(svg) {

	var rules = getAllCSSRules("viz.css");
	var cssString = "";
	for (var i = 0; i < rules.length; i++) {
		cssString += rules[i];
	}

	return cssString;
}

// function addCSStoSVG(svg){
// console.log("inside addCSStoSVG");
// // check if stylesheet has already been added
// var defs = svg.getElementsByTagName('defs');
// if (defs[0] != undefined){
// if (defs[0].getElementsByTagName('style') != undefined){
// return;
// }
// }
//	
// var openString = "<![CDATA[";
// var closeString = "]]>";
//
// var rules = getAllCSSRules("viz.css");
// var cssString = openString;
// for (var i = 0; i < rules.length; i++) {
// cssString += rules[i];
// }
// cssString += closeString;
//
// var defs = document.createElementNS('http://www.w3.org/2000/svg', "defs");
// defs.id = "cssDefs";
// var styles = document.createElementNS('http://www.w3.org/2000/svg', "style");
// styles.innerHTML = cssString;
// defs.appendChild(styles);
// svg.appendChild(defs);
//	
// }

function retrieveJavaScript(svg) {
//	var sqgString = JSON.stringify(sqg).replace(/'/g, "\\'");
//	var jsString = "var theJSON = '" + sqgString + "';";
//	jsString += "var sqg = JSON.parse(theJSON);";

	var levelNumber = svg.id.slice(3);
	var functionString = getStandaloneFunctions(levelNumber) + ";";
//	jsString += functionString;
	var jsString = functionString;

	var eventString = getStandaloneEvents.toString() + ";";
	jsString += eventString;

	// add call to function to add events
	jsString += "var levelNumber = " + levelNumber + ";";
	jsString += "getStandaloneEvents(levelNumber);";
	jsString += "appendPopupG(levelNumber);";

	return jsString;
}

function retrieveSQG(){
	console.log("inside retrieveSQG");
//	var sqgString = JSON.stringify(sqg).replace(/'/g, "\\'");
//	var jsString = "var theJSON = '" + sqgString + "';";
//	jsString += "var sqg = JSON.parse(theJSON);";
//	return jsString;
}

// function addJavaScripttoSVG(svg){
// var openString = "<![CDATA[";
// var closeString = "]]>";
// var sqgString = JSON.stringify(sqg).replace(/'/g, "\\'");
// var jsString = openString;
// // jsString += "console.log('stuff');";
// jsString += "var theJSON = '" + sqgString + "';";
// jsString += "var sqg = JSON.parse(theJSON);";
// //////var jsString = "var theSVG = document.getElementById('svg1');var theBox
// = theSVG.getElementById('box1_Mammalia');theBox.addEventListener('click',
// function(){console.log('You clicked')}, false);"
//	
// var levelNumber = svg.id.slice(3);
// var functionString = getStandaloneFunctions(levelNumber) +";";
// // console.log(functionString);
// jsString += functionString;
//	
// var eventString = getStandaloneEvents.toString() + ";";
// jsString += eventString;
//	
// // add call to function to add events
// jsString +="var levelNumber = " + levelNumber +";";
// jsString += "getStandaloneEvents(levelNumber);";
// jsString += "appendPopupG(levelNumber);";
//	
// jsString += closeString;
// var script = document.createElementNS('http://www.w3.org/2000/svg',
// "script");
// script.setAttribute("type", "text/javascript");
// script.innerHTML = jsString;
// svg.appendChild(script);
// }

function getStandaloneFunctions(levelNumber) {
	var functionString = displaySpeciesInfo.toString();
	functionString += appendPopupG.toString();
	functionString += hidePopup.toString();
	functionString += capitalizeFirstLetter.toString();
	functionString += svgSetXY.toString();
	functionString += popupTotop.toString();
	functionString += popToTop.toString();
	functionString += positionAndSizePopup.toString();
	functionString += svgFromScreen.toString();
	functionString += showCommonName.toString();
	functionString += highlightSymbol.toString();
	functionString += restoreSymbol.toString();
	functionString += scaleInPlace.toString();
	functionString += roundToPrecision.toString();
	functionString += showCutoffInfo.toString();
	functionString += hideCutoffInfo.toString();
	functionString += openEcosPage.toString();
	functionString += checkIfLegendOption.toString();

	// var functionString = getAllFunctionsFromJS('seqapass.js');

	return functionString;

}

function getStandaloneEvents(levelNumber) {
	var svg = document.getElementById('svg' + levelNumber);
	// ortholog hover
	var orthoG = svg.getElementById('orthoG' + levelNumber);
	orthoG.addEventListener('mouseover', displaySpeciesInfo, false);
	orthoG.addEventListener('mouseout', hidePopup, false);
	// endangered hover
	var endangeredG = svg.getElementById('endangeredG'+levelNumber);
	endangeredG.addEventListener('mouseover', displaySpeciesInfo, false);
	endangeredG.addEventListener('mouseout', hidePopup, false);
	endangeredG.addEventListener('click', openEcosPage, false);
	// threatened hover
	var threatenedG = svg.getElementById('threatenedG'+levelNumber);
	threatenedG.addEventListener('mouseover', displaySpeciesInfo, false);
	threatenedG.addEventListener('mouseout', hidePopup, false);
	threatenedG.addEventListener('click', openEcosPage, false);
	// common model hover
	var modelG = svg.getElementById('modelG' + levelNumber);
	modelG.addEventListener('mouseover', displaySpeciesInfo, false);
	modelG.addEventListener('mouseout', hidePopup, false);
	// taxonomy (x-axis) hover
	var taxonText = svg.getElementsByClassName('taxonText'+levelNumber);
	for (var i=0; i<taxonText.length; i++){
		taxonText[i].addEventListener('mouseover', showCommonName, false);
		taxonText[i].addEventListener('mouseout', hidePopup, false);
	}
	// species legend hover
	var legend = svg.getElementById('legend' + levelNumber);
	if (legend != null){
		for (var i=0; i<legend.childElementCount; i++){
			var node = legend.childNodes[i];
			var isSpecies = true;
			if (i % 2 == 0){
				isSpecies = !checkIfLegendOption(node.innerHTML);
				if (!isSpecies){
					i++;
				}
			}
			if (node.tagName == "path"){
				node.addEventListener('mouseover', highlightSymbol, false);
				node.addEventListener('mouseout', restoreSymbol, false);
				node.addEventListener('click', popToTop, false);
			}
		}
		var legendTargets = svg.getElementById('legendTargets' + levelNumber);
		legendTargets.addEventListener('mouseover', displaySpeciesInfo, false);
		legendTargets.addEventListener('mouseout', hidePopup, false);
	}
	
	var cutoffLine = svg.getElementById('cutoffLine' + levelNumber);
	cutoffLine.addEventListener('mouseover', showCutoffInfo, false);
	cutoffLine.addEventListener('mouseout', hideCutoffInfo, false);
	
	
	
}

function getAllFunctionsFromJS(jsFile) {
	var url = null;
	var jsFiles = document.getElementsByTagName('script');
	var file = null;
	for (var i = 0; i < jsFiles.length; i++) {
		file = jsFiles[i];
		if (file.src.includes(jsFile)) {
			url = file.src;
			break;
		}
	}

	console.log("url is: " + url);

	var storedText;

	fetch(url).then(function(response) {
		response.text().then(function(text) {
			storedText = text;
			console.log(text);
		});
	});
	console.log("function text is:");
	console.log(storedText);

	return storedText;

}

function getAllCSSRules(sheetHrefRegexMatch) {
	var sheets = document.styleSheets;
	var sheet = null;
	for (var i = 0; i < sheets.length; i++) {
		sheet = sheets[i];
		if (sheet.href.match(sheetHrefRegexMatch) != null) {
			break;
		}
	}
	var rules = [];
	for (var i = 0; i < sheet.cssRules.length; i++) {
		var rule = sheet.cssRules[i];
		rules[i] = rule.cssText;
	}
	return rules;
}

function getCSSRule(sheetHrefRegexMatch, styleSelector) {
	var sheets = document.styleSheets;
	var sheet = null;
	for (var i = 0; i < sheets.length; i++) {
		sheet = sheets[i];
		if (sheet.href.match(sheetHrefRegexMatch) != null) {
			break;
		}
	}
	for (var i = 0; i < sheet.cssRules.length; i++) {
		var rule = sheet.cssRules[i];
		if (rule.selectorText == styleSelector) {
			return rule.cssText;
		}
	}
}

function addCSSClassRule(sheetHrefRegexMatch, styleSelector, cssText ) {
	var sheets = document.styleSheets;
	var sheet = null;
	for (var i = 0; i < sheets.length; i++) {
		sheet = sheets[i];
		if (sheet.href.match(sheetHrefRegexMatch) != null) {
			break;
		}
	}
	for (var i = 0; i < sheet.cssRules.length; i++) {
		var rule = sheet.cssRules[i];
		if (rule.selectorText == styleSelector) {
			return; // Don't try to add again!
		}
	}

	sheet.insertRule(styleSelector+" {"+cssText+"}",1);
	//sheet.insertRule(".removeThis1 {fill:#ff0000;}",1);
}



function changeStyle(sheetHrefRegexMatch, styleSelector, styleProperty,
		newValue) {
	var sheets = document.styleSheets;
	var sheet = null;
	for (var i = 0; i < sheets.length; i++) {
		sheet = sheets[i];
		if (sheet.href.match(sheetHrefRegexMatch) != null) {
			break;
		}
	}
	for (var i = 0; i < sheet.cssRules.length; i++) {
		var rule = sheet.cssRules[i];
		if (rule.selectorText == styleSelector) {
			rule.style[styleProperty] = newValue;
			break;
		}
	}
}

function checkSpeciesMenu(index, checkBool, levelNumber) {
	var tp = document.getElementById("levOneVizForm:speciesMenu_panel");
	if (levelNumber == 2) {
		tp = document.getElementById("levTwoVizForm:speciesMenu2_panel");
	}
	var list = tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	var item = list[index];
	var div = item.firstElementChild.firstElementChild.nextElementSibling;
	var span = div.firstElementChild;
	if (span.classList.contains('ui-icon-check') && !checkBool) {
		span.click();
	} else if (span.classList.contains('ui-icon-blank') && checkBool) {
		span.click();
	}
	// if (checkBool) {
	// span.classList.add('ui-icon-check');
	// span.classList.remove('ui-icon-blank');
	// } else {
	// span.classList.remove('ui-icon-check');
	// span.classList.add('ui-icon-blank');
	// }
}

function disableSomeSpecies(levelNumber) {
	var visibleTax = getTaxMenuCheckedItems(levelNumber);
	var disableNames = new Array();
	var so = sqg.speciesObjects;
	for (var i = 0; i < so.length; i++) {
		var speciesObject = so[i];
		if (visibleTax.indexOf(speciesObject.taxonomyName) == -1) {
			enableSpeciesMenu(speciesObject.menuIndex, false, levelNumber);
		}
	}
	redrawLegend(levelNumber);
}

function enableSpeciesMenu(index, enableBool, levelNumber) {
	var tp = document.getElementById("levOneVizForm:speciesMenu_panel");
	if (levelNumber == 2) {
		tp = document.getElementById("levTwoVizForm:speciesMenu2_panel");
	}
	var list = tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	var item = list[index];
	var div = item.firstElementChild.firstElementChild.nextElementSibling;
	if (enableBool) {
		div.classList.remove('ui-state-disabled');
	} else {
		div.classList.add('ui-state-disabled');
	}
}

/**
 * Get the status of a species menu item
 * 
 * @param index -
 *            the index into the speciesMenu
 * @param levelNumber -
 *            1 or 2
 * @returns 0 if disabled<br/> -1 if enabled and unchecked<br/> 1 if enabled
 *          and checked
 */
function getSpeciesMenuStatus(index, levelNumber) {
	var tp = document.getElementById("levOneVizForm:speciesMenu_panel");
	if (levelNumber == 2) {
		tp = document.getElementById("levTwoVizForm:speciesMenu2_panel");
	}
	var list = tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	var item = list[index];
	var div = item.firstElementChild.firstElementChild.nextElementSibling;
	var disabled = div.classList.contains('ui-state-disabled');
	var span = div.firstElementChild;
	if (disabled) {
		span.classList.remove('ui-icon-check');
		span.classList.add('ui-icon-blank');
		return 0;
	}
	if (span.classList.contains('ui-icon-check')) {
		return 1;
	}
	return -1;
}

function getLegendSpeciesIndexes(levelNumber) {
	var legendSpeciesIndexes = new Array();
	var rows = sqg.levelOneReportRows;
	var speciesMenu = document.getElementById("levOneVizForm:speciesMenu");
	var speciesIndexes = sqg.speciesIndexes;
	var speciesObjects = sqg.speciesObjects;

	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
		speciesMenu = document.getElementById("levTwoVizForm:speciesMenu2");
	}

	// These are the species already selected, not the ones possibly selected
	var list = speciesMenu.firstElementChild.nextElementSibling.nextElementSibling.childNodes;
	for (var i = 0; i < list.length; i++) {
		var name = list[i].dataset.itemValue;
		var speciesObjectId = speciesIndexes.indexOf(name);
		speciesObjects[speciesObjectId].legendPos = i;
		legendSpeciesIndexes.push(speciesIndexes.indexOf(name));
	}
	return legendSpeciesIndexes;
}

function getLegendSpeciesObjectIds(levelNumber) {
	var ids = new Array();
	var speciesObjects = sqg.speciesObjects;
	for (var i = 0; i < speciesObjects.length; i++) {
		var speciesObject = speciesObjects[i];
		if (speciesObject.legendPos > -1) {
			ids.push(i);
		}
	}
	return ids;
}

// ========================================================================================
// == Clean up utilities
// ========================================================================================

function removeLegend(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var legend = document.getElementById("legend" + levelNumber);
	if (legend != null && legend != undefined) {
		svg.removeChild(legend);
	}

	var legendTargets = document.getElementById("legendTargets" + levelNumber);
	if (legendTargets != null && legendTargets != undefined) {
		svg.removeChild(legendTargets);
	}

	var speciesObjects = sqg.speciesObjects;
	for (var i = 0; i < speciesObjects.length; i++) {
		var speciesObject = speciesObjects[i];
		speciesObject.legendPos = -1;
	}
}

// ========================================================================================
// == Toggle, Turn on/off, Show/Hide utilities
// ========================================================================================

function changeBarWidth(levelNumber) {
	console.log("inside changeBarWidth");
	var pp = sqg.plotParams;
	
	var svg = document.getElementById("svg" + levelNumber);
	var oldWidth = svg.viewBox.baseVal.width;
	var oldHeight = svg.viewBox.baseVal.height;
	console.log("old width/height: " + oldWidth+"/"+oldHeight);
	console.log("zoomFactorPrev:" + pp.zoomFactorPrev);
	console.log("zoomFactor:    " + pp.zoomFactor);
	
	var inputVal;
	if (levelNumber == 1){
		inputVal = document.getElementById("levOneVizForm:bwSpinner_input").value;
	} else if (levelNumber == 2){
		inputVal = document.getElementById("levTwoVizForm:bwSpinner_input").value;
	}
	
	pp.barWidth = inputVal*1;
	if (pp.barWidth > sqg.barWidthMax) {
		pp.barWidth = sqg.barWidthMax;
		return;
	}
	if (pp.barWidth < sqg.barWidthMin) {
		pp.barWidth = sqg.barWidthMin;
		return;
	}	

	pp.barSpacing = pp.barWidth / 2;

	var textHeight = pp.barSpacing + 8;
	changeStyle("viz\.css", ".taxonText" + levelNumber, "fontSize", textHeight
			+ "px");
	var minLabelSize = Math.max(30, textHeight);
	document.getElementById("xAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";
	document.getElementById("yAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";

	drawTaxGroups(levelNumber);
	
	redrawLegend(levelNumber);
	
	console.log("new width/height: " + svg.viewBox.baseVal.width+"/"+svg.viewBox.baseVal.height);
	console.log("end zoomFactorPrev:" + pp.zoomFactorPrev);
	console.log("end zoomFactor:    " + pp.zoomFactor);
}

function bwResetEvent(event){
	var target = event.target;
	var levelNumber = target.id.substring(7, 8) * 1;
	
	bwReset(levelNumber);
}

function bwReset(levelNumber){
	console.log("inside bwreset");
	var svg = document.getElementById("svg" + levelNumber);
	var oldWidth = svg.viewBox.baseVal.width;
	var oldHeight = svg.viewBox.baseVal.height;
	
	var pp = sqg.plotParams;
	console.log("old width/height: " + oldWidth+"/"+oldHeight);
	console.log("zoomFactorPrev:" + pp.zoomFactorPrev);
	console.log("zoomFactor:    " + pp.zoomFactor);
	
	pp.barWidth = pp.barWidthDefault;
	pp.barSpacing = pp.barWidthDefault / 2;
	
	var target = document.getElementById("levOneVizForm:bwSpinner_input");
	if (levelNumber == 2){
		target = document.getElementById("levTwoVizForm:bwSpinner_input");
	}
	
	target.value = pp.barWidth;

	var textHeight = pp.barSpacing + 8;
	changeStyle("viz\.css", ".taxonText" + levelNumber, "fontSize", textHeight
			+ "px");
	var minLabelSize = Math.max(30, textHeight);
	document.getElementById("xAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";
	document.getElementById("yAxisLabel" + levelNumber).style.fontSize = minLabelSize
			+ "px";
	
	drawTaxGroups(levelNumber);
	
	redrawLegend(levelNumber);
	
	// this hack is needed to work around a firefox bug that prevents updating
	// after zoom reset
	var hackObject = document.getElementsByClassName("taxonText"+levelNumber)[0];
	var hackEvent = new MouseEvent('mouseover', {bubbles: true, clientX: hackObject.right, clientY: hackObject.bottom});
	hackObject.dispatchEvent(hackEvent);
	var hackEvent = new MouseEvent('mouseout', {bubbles: true, clientX: hackObject.right, clientY: hackObject.bottom});
	hackObject.dispatchEvent(hackEvent);
	
	console.log("new width/height: " + svg.viewBox.baseVal.width+"/"+svg.viewBox.baseVal.height);
	console.log("end zoomFactorPrev:" + pp.zoomFactorPrev);
	console.log("end zoomFactor:    " + pp.zoomFactor);
}

function setBwVal(event) {
	var target = event.target;
	var levelNumber = target.id.substring(5, 6) * 1;
	var newVal = target.value;
	var pp = sqg.plotParams;
	if (newVal != newVal * 1) { // bogus input (not numeric)
		target.value = pp.barWidth;
		return;
	}
	if (newVal < 6) {
		pp.barWidth = 6;
	} else if (newVal > 60) {
		pp.barWidth = 60;
	} else {
		pp.barWidth = Math.floor(newVal);
	}
	target.value = pp.barWidth;
	drawTaxGroups(levelNumber);
}

function returnValidZoomLevel(proposedZoom){
	var min = sqg.zoomMinDefault*100;
	var max = sqg.zoomMaxDefault*100;
	
	if (proposedZoom < min) {
		return min;
	} else if (proposedZoom > max) {
		return max;
	} else {
		return proposedZoom;
	}
}

function setZmVal(levelNumber) {
	
	var svg = document.getElementById("svg" + levelNumber);
	var pt = svg.createSVGPoint();
	pt.x = 0;
	pt.y = 0;
	
	var zoomInputVal;
	if (levelNumber==1){
		zoomInputVal = document.getElementById("levOneVizForm:zoomSpinner_input").value*1;
	}
	if (levelNumber==2){
		zoomInputVal = document.getElementById("levTwoVizForm:zoomSpinner_input").value*1;
	}
	zoomInputVal = returnValidZoomLevel(zoomInputVal);
	// First determine if arrow zoom or user input value
	var curZoom = Math.round(sqg.plotParams.zoomFactor*10000)/100;
	var diff = Math.abs(zoomInputVal - curZoom);  // diff between previous
													// zoom and current zoom
													// input value
	
	var directSetVal = true;   // boolean for whether incremental zoom (using
								// arrows) or user set number
	var eps = 0.01;  // small value for use in comparing double values
	if (Math.abs(diff - sqg.arrowZoomDelta*100) < eps){
		directSetVal = false;  // zoom change equals arrow step size
	}
	
	
	if (!directSetVal){
// console.log("arrow setZmVal");
		var delta = -1;
		if (zoomInputVal > sqg.plotParams.zoomFactor*100){
			delta = 1;
		}
		
		zoomAtPoint(levelNumber, pt, delta, false);
	} else {
// console.log("direct setZmVal");
		var target = document.getElementById("levOneVizForm:zoomSpinner_input");
		if (levelNumber == 2){
			target = document.getElementById("levTwoVizForm:zoomSpinner_input");
		}
//		var newVal = target.value;
		var newVal =  returnValidZoomLevel(target.value);
		var pp = sqg.plotParams;
		
		pp.zoomFactor = newVal/100;
	
// console.log("zoomFactorPrev:" + pp.zoomFactorPrev);
// console.log("zoomFactor: " + pp.zoomFactor);

		if (newVal != newVal * 1) { // bogus input (not numeric)
// console.log("bad zoom value!")
			target.value = Math.floor(100 * pp.zoomFactor);
			return;
		}
//		pp.zoomFactor = returnValidZoomLevel(newVal)/100;
//		if (newVal < 53) {
//			pp.zoomFactor = 0.53;
//		} else if (newVal > 485) {
//			pp.zoomFactor = 4.85;
//		} else {
//			pp.zoomFactor = Math.floor(newVal) / 100;
//		}
	
		target.value = Math.round(100 * pp.zoomFactor);
		var svg = document.getElementById("svg" + levelNumber);
		var oldWidth = svg.viewBox.baseVal.width;
		var oldHeight = svg.viewBox.baseVal.height;
	
// console.log("old width/height: " + oldWidth+"/"+oldHeight);
		var h = pp.xAxisTextY + pp.bottomRest;
		var w = getBoxPlotMinWidth(levelNumber);
// if (w < pp.minWidth){w = pp.minWidth;}
	
		svg.viewBox.baseVal.width = w / pp.zoomFactor;
		svg.viewBox.baseVal.height = h / pp.zoomFactor;
		rescaleSymbols(levelNumber, pp.zoomFactor);
		pp.zoomFactorPrev = pp.zoomFactor;
	
// console.log("new width/height: " +
// svg.viewBox.baseVal.width+"/"+svg.viewBox.baseVal.height);
// console.log("end zoomFactorPrev:" + pp.zoomFactorPrev);
// console.log("end zoomFactor: " + pp.zoomFactor);
	}
	updatePlotControlValues();
}

function zoom(event) {
	var target = event.target;
	var delta = target.id.substring(4);
	var levelNumber = target.id.substring(2, 3) * 1;
	
	var svg = document.getElementById("svg" + levelNumber);
	var pt = svg.createSVGPoint();
	pt.x = 0;
	pt.y = 0;

	
	if (zoomPanOff(levelNumber)) {
		return;
	}
	if (delta == "-") {
		zoomAtPoint(levelNumber, pt, 1, false);
		return;
	}
	if (delta == "+") {
		zoomAtPoint(levelNumber, pt, -1, false);
	}

}

function rescaleSymbols(levelNumber, zoomFactor) {
	console.log("inside rescaleSymbols: " + zoomFactor);
	var gSpecies = null;
	var recipZoom = 1 / zoomFactor;
	if (areOrthosVisible(levelNumber)) {
		gSpecies = document.getElementById("orthoG" + levelNumber);
	} else if (areEndangeredsVisible(levelNumber)) {
		gSpecies = document.getElementById("endangeredG" + levelNumber);
	} else if (areThreatenedsVisible(levelNumber)) {
		gSpecies = document.getElementById("threatenedG" + levelNumber);
	} else if (areModelsVisible(levelNumber)) {
		gSpecies = document.getElementById("modelG" + levelNumber);
	}
	if (gSpecies != null) {
		for (var i = 0; i < gSpecies.childElementCount; i++) {
			var symbol = gSpecies.childNodes[i];
			scaleInPlace(symbol, recipZoom);
		}
	}
	var legendTargets = document.getElementById("legendTargets" + levelNumber);
	if (legendTargets != null && legendTargets != undefined) {
		for (var i = 0; i < legendTargets.childElementCount; i++) {
			var symbol = legendTargets.childNodes[i];
			scaleInPlace(symbol, recipZoom);
		}
	}
	scaleInPlace(document.getElementById("popupG" + levelNumber), recipZoom);
}

function zoomPanOff(levelNumber) {
	var pz = document.getElementById("panZoom" + levelNumber);
	if (pz == null || pz == undefined){
		return true;
	}
	var col = pz.style.getPropertyValue("background-color");

	return (col == "rgb(255, 255, 255)") || (col == "#ffffff");
}

function hidePopup(event) {
	var target = event.target;
	var levelNumber = 1;
	// FIXME - this stuff below needs work
	if (target.classList.contains("taxonText2")
			|| target.id.substring(1, 2) == 2) {
		levelNumber = 2;
	}

	var popupG = document.getElementById("popupG" + levelNumber);
	popupG.classList.add("invis");
}

function setTaxaCheck(taxonName, checkBool, levelNumber) {
	var tg = sqg.taxGroups;
	var ti = sqg.taxIndexes;
	var tp = document.getElementById("levOneVizForm:taxMenu_panel");
	if (levelNumber == 2) {
		tp = document.getElementById("levTwoVizForm:taxMenu2_panel");
	}

	var list = tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	var changeIt = false;
	for (var i = 0; i < list.length; i++) {
		if (list[i].dataset.itemValue == taxonName) {
			var checkSpan = list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild;

			if ((checkBool && !checkSpan.classList.contains("ui-icon-check"))
					|| (!checkBool && checkSpan.classList
							.contains("ui-icon-check"))) {
				checkSpan.click(); // clicking toggles it
				// changeIt = true;
				break;
			}
		}
	}
}

function resetVisibleTaxa(levelNumber) {
	var newVisibleTaxGroups = getTaxMenuCheckedItems(levelNumber);
	var curDisplayedTaxGroups = getDisplayedBoxPlotTaxNames(levelNumber);
	if (newVisibleTaxGroups.length == curDisplayedTaxGroups.length) {
		return;
	}

	var taxGroups = sqg.taxGroups;
	var ti = sqg.taxIndexes;
	var speciesObjects = sqg.speciesObjects;

	if (newVisibleTaxGroups.length == curDisplayedTaxGroups.length + 1) { // One
																			// was
																			// added
		for (var i = 0; i < newVisibleTaxGroups.length; i++) {
			if (newVisibleTaxGroups[i] != curDisplayedTaxGroups[i]) {
				var newName = newVisibleTaxGroups[i];
				var index = ti.indexOf(newName);
				var group = taxGroups[index];
				group.displayColumn = i;
				for (var j = index + 1; j < taxGroups.length; j++) {
					if (taxGroups[j].displayColumn > -1) {
						taxGroups[j].displayColumn++;
					}
				}

				var namesToRestore = new Array();
				var seenSpeciesIndexes = new Array();
				for (var j = 0; j < group.taxSpeciesIndexes.length; j++) {
					var speciesIndex = group.taxSpeciesIndexes[j];
					if (seenSpeciesIndexes.indexOf(speciesIndex) == -1) {
						seenSpeciesIndexes.push(speciesIndex);
						var speciesObject = speciesObjects[speciesIndex];
						if (speciesObject.redisplayWhenTaxGroupUnHidden) {
							speciesObject.redisplayWhenTaxGroupUnHidden = false;
							namesToRestore.push(speciesObject.name);
						}
					}
				}
				restoreLegendItems(levelNumber, namesToRestore);
				break;
			}
		}
	} else if (newVisibleTaxGroups.length == curDisplayedTaxGroups.length - 1) { // One
																					// was
																					// removed
		for (var i = 0; i < curDisplayedTaxGroups.length; i++) {
			if (newVisibleTaxGroups[i] != curDisplayedTaxGroups[i]) {
				var taxToRemove = curDisplayedTaxGroups[i];
				var index = ti.indexOf(taxToRemove);
				var group = taxGroups[index];
				group.displayColumn = -1;
				for (var j = index + 1; j < taxGroups.length; j++) {
					if (taxGroups[j].displayColumn > -1) {
						taxGroups[j].displayColumn--;
					}
				}
				var namesToRemove = new Array();
				var seenSpeciesIndexes = new Array();
				for (var j = 0; j < group.taxSpeciesIndexes.length; j++) {
					var speciesIndex = group.taxSpeciesIndexes[j];
					if (seenSpeciesIndexes.indexOf(speciesIndex) == -1) {
						seenSpeciesIndexes.push(speciesIndex);
						var speciesObject = speciesObjects[speciesIndex];
						if (speciesObject.legendPos > -1) {
							speciesObject.redisplayWhenTaxGroupUnHidden = true;
							namesToRemove.push(speciesObject.name);
						}
					}
				}
				removeLegendItems(levelNumber, namesToRemove);
				break;
			}
		}
	} else if (newVisibleTaxGroups.length > curDisplayedTaxGroups.length) { // Several
																			// were
																			// added
		var oldIndex = 0;
		var namesToRestore = new Array();

		for (var i = 0; i < newVisibleTaxGroups.length; i++) {
			if (newVisibleTaxGroups[i] != curDisplayedTaxGroups[oldIndex]) {
				oldIndex--;
				var newName = newVisibleTaxGroups[i];
				var index = ti.indexOf(newName);
				var group = taxGroups[index];
				group.displayColumn = i;
				for (var j = index + 1; j < taxGroups.length; j++) {
					if (taxGroups[j].displayColumn > -1) {
						taxGroups[j].displayColumn++;
					}
				}
				var seenSpeciesIndexes = new Array();
				for (var j = 0; j < group.taxSpeciesIndexes.length; j++) {
					var speciesIndex = group.taxSpeciesIndexes[j];
					if (seenSpeciesIndexes.indexOf(speciesIndex) == -1) {
						seenSpeciesIndexes.push(speciesIndex);
						var speciesObject = speciesObjects[speciesIndex];
						if (speciesObject.redisplayWhenTaxGroupUnHidden) {
							speciesObject.redisplayWhenTaxGroupUnHidden = false;
							namesToRestore.push(speciesObject.name);
						}
					}
				}
			}
			oldIndex++;
		}
		restoreLegendItems(levelNumber, namesToRestore);

	} else if (newVisibleTaxGroups.length < curDisplayedTaxGroups.length) { // Several
																			// were
																			// removed
		var newIndex = 0;
		var namesToRemove = new Array();
		for (var i = 0; i < curDisplayedTaxGroups.length; i++) {
			if (newVisibleTaxGroups[newIndex] != curDisplayedTaxGroups[i]) {
				newIndex--;
				var taxToRemove = curDisplayedTaxGroups[i];
				var index = ti.indexOf(taxToRemove);
				var group = taxGroups[index];
				group.displayColumn = -1;
				for (var j = index + 1; j < taxGroups.length; j++) {
					if (taxGroups[j].displayColumn > -1) {
						taxGroups[j].displayColumn--;
					}
				}
				var seenSpeciesIndexes = new Array();
				for (var j = 0; j < group.taxSpeciesIndexes.length; j++) {
					var speciesIndex = group.taxSpeciesIndexes[j];
					if (seenSpeciesIndexes.indexOf(speciesIndex) == -1) {
						seenSpeciesIndexes.push(speciesIndex);
						var speciesObject = speciesObjects[speciesIndex];
						if (speciesObject.legendPos > -1) {
							speciesObject.redisplayWhenTaxGroupUnHidden = true;
							namesToRemove.push(speciesObject.name);
						}
					}
				}
			} else {
				var taxToRenumber = curDisplayedTaxGroups[i];
				var index = ti.indexOf(taxToRenumber);
				var group = taxGroups[index];
				group.displayColumn = newIndex;
			}
			newIndex++;
		}
		removeLegendItems(levelNumber, namesToRemove);
	}
}

function highlightSymbol(event) {
	var targetId = event.target.id;
	var levelNumber = targetId.substring(3, 4);
	var pp = sqg.plotParams;
	var legendSymbols = document.getElementsByClassName("ex_" + targetId);
	for (var i = 0; i < legendSymbols.length; i++) {
		var legendItem = legendSymbols[i];
		scaleInPlace(legendItem, 4 / pp.zoomFactor);
	}
	// first make sure legendItem is last
	var legendTargets = document.getElementById("legendTargets"+levelNumber);
	for (var i = 0; i < legendSymbols.length; i++) {
		var legendItem = legendSymbols[0];
		legendTargets.appendChild(legendTargets.removeChild(legendItem));
	}
	// trigger displaySpeciesEvent for symbol
	var childLoc = legendTargets.childNodes[legendTargets.childElementCount-1].getBoundingClientRect();
	childEvent = new MouseEvent('mouseover', {bubbles: true, clientX: childLoc.right, clientY: childLoc.bottom});
	legendTargets.childNodes[legendTargets.childElementCount-1].dispatchEvent(childEvent);
}

function showTable(event) {
// console.log("inside show table event");
	var targetId = event.target.id;
// console.log("targetId: " + targetId);
	var levelNumber = 1 * targetId.substring(3, 4);
	var taxonName = targetId.substring(5);
	var mean = 0.0;
	var median = 0.0;
	for (var i = 0; i < sqg.taxGroups.length; i++) {
		if (sqg.taxGroups[i].name == taxonName) {
			mean = sqg.taxGroups[i].mean;
			median = sqg.taxGroups[i].median;
		}
	}
	if (levelNumber == 1) {
		showTaxTable1([ {
			name : 'dataTaxon',
			value : taxonName
		}, {
			name : 'mean',
			value : mean
		}, {
			name : 'median',
			value : median
		} ]);
	} else if (levelNumber == 2) {
		showTaxTable2([ {
			name : 'dataTaxon',
			value : taxonName
		}, {
			name : 'mean',
			value : mean
		}, {
			name : 'median',
			value : median
		} ]);
	} else {
		console.log("error in showTable event");
	}
	console.log("Need to show table for level: " + levelNumber
			+ " for taxonomy name: " + taxonName);
}

function restoreSymbol(event) {
	var targetId = event.target.id;
	var levelNumber = targetId.substring(3, 4);
	var pp = sqg.plotParams;
	var legendSymbols = document.getElementsByClassName("ex_" + targetId);
	for (var i = 0; i < legendSymbols.length; i++) {
		var legendItem = legendSymbols[i];
		scaleInPlace(legendItem, 1 / pp.zoomFactor);
		legendItem.setAttribute("stroke-width", 1 / pp.zoomFactor);
	}
	
	// hide speciesInfo
	var childSymbol = document.getElementById("legendTargets" + levelNumber);
	childEvent = new MouseEvent('mouseout', {bubbles: true});
	childSymbol.childNodes[childSymbol.childElementCount-1].dispatchEvent(childEvent);
	
}

function scaleInPlace(symbol, scaleFactor) {
	if (symbol.tagName == "path") {
		var targetId = symbol.id;
		var levelNumber = targetId.substring(3, 4);
		var pp = sqg.plotParams;

		if (scaleFactor == 1) {
			symbol.setAttribute("transform", "matrix(1,0,0,1,0,0)");
			symbol.setAttribute("stroke-width", pp.zoomFactor);
			return;
		}
		var parsedParts = symbol.getAttribute("d").split(" ");
		var x = parsedParts[1];
		var y = parsedParts[2];
		var unX = -x * (scaleFactor - 1);
		var unY = -y * (scaleFactor - 1);
		symbol.setAttribute("stroke-width", scaleFactor * pp.zoomFactor / 2);
		var matrix = "matrix( " + scaleFactor + " , 0 , 0 , " + scaleFactor
				+ " , " + unX + " , " + unY + " )"
		symbol.setAttribute("transform", matrix);
	} else if (symbol.tagName == "circle") {
		symbol.r.baseVal.value = 3 * scaleFactor;
	} else if (symbol.tagName == "g") {
		if (scaleFactor == 1) {
			symbol.setAttribute("transform", "matrix(1,0,0,1,0,0)");
			return;
		}
		var matrix = "matrix( " + scaleFactor + " , 0 , 0 , " + scaleFactor
				+ " , " + 0 + " , " + 0 + " )"
		symbol.setAttribute("transform", matrix);

	}
}

function popToTop(event) {
	var target = event.target;
	var targetId = target.id;
	var levelNumber = targetId.substring(3, 4);
	var legendTargets = document.getElementById("legendTargets" + levelNumber);
	// var svg = document.getElementById("svg" + levelNumber);
	var legendCircle = Array.from(document.getElementsByClassName("ex_" + targetId));
	for (var i = 0; i < legendCircle.length; i++) {
		var legendItem = legendCircle[0];
		legendTargets.appendChild(legendTargets.removeChild(legendItem));
	}
	popupTotop(levelNumber);
	
	// update speciesInfo with new values
	var childLoc = legendTargets.childNodes[legendTargets.childElementCount-1].getBoundingClientRect();
	childEvent = new MouseEvent('mouseover', {bubbles: true, clientX: childLoc.right, clientY: childLoc.bottom});
	legendTargets.childNodes[legendTargets.childElementCount-1].dispatchEvent(childEvent);
}

function popupTotop(levelNumber) {
	var svg = document.getElementById("svg" + levelNumber);
	var popupG = document.getElementById("popupG" + levelNumber);
	svg.appendChild(svg.removeChild(popupG));
}

/**
 * method ensures query tax group is always selected in tax group menu needed
 * when checkall box is used to reselect query tax group
 * 
 * @param level
 * @returns
 */
function addQueryGroupToTaxMenu(level) {
	enableQueryGroup(level);
	var menu;
	var menuPanel;
	if (level == 1) {
		menu = document.getElementById('levOneVizForm:taxMenu');
		menuPanel = document.getElementById('levOneVizForm:taxMenu_panel');
	} else if (level == 2) {
		menu = document.getElementById('levTwoVizForm:taxMenu2');
		menuPanel = document.getElementById('levTwoVizForm:taxMenu2_panel');
	} else {
		console.log("Error in addQueryGroupToTaxMenu: invalid level");
	}

	var itemContainer = menu
			.getElementsByClassName('ui-selectcheckboxmenu-multiple-container')[0];
	if (itemContainer.childElementCount == 0) {
		// just finished deselecting all
		// need to add queryGroup token to taxMenu
		var taxMenuItems = menuPanel
				.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
				.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
		var item = taxMenuItems
				.getElementsByClassName('ui-selectcheckboxmenu-item')[0];
		var itemChkBox = item.getElementsByClassName('ui-chkbox')[0]
				.getElementsByClassName('ui-chkbox-box')[0];
		// this will deselect query group
		itemChkBox.click();
		// this will reselect query group and add token to taxMenu
		itemChkBox.click();
		disableTaxMenuToken(level);
	}
	disableQueryGroup(level);
}

function enableQueryGroup(level) {
	var taxMenuItems;
	if (level == 1) {
		taxMenuItems = document.getElementById('levOneVizForm:taxMenu_panel')
				.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
				.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
	} else if (level == 2) {
		taxMenuItems = document.getElementById('levTwoVizForm:taxMenu2_panel')
				.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
				.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
	} else {
		console.log("Error in enableQueryGroup: invalid level");
	}
	var item = taxMenuItems
			.getElementsByClassName('ui-selectcheckboxmenu-item')[0];
	var itemChkBox = item.getElementsByClassName('ui-chkbox')[0]
			.getElementsByClassName('ui-chkbox-box')[0];
	if (itemChkBox.classList.contains('ui-state-disabled')) {
		console.log("removing disabled state");
		itemChkBox.classList.remove('ui-state-disabled');
	}
}

function disableTaxMenuToken(levelNumber) {
	var menuPanel;
	if (levelNumber == 1) {
		if (sqg.levelOneReportRows.length == 0) return;
		menu = document.getElementById('levOneVizForm:taxMenu');
	} else if (levelNumber == 2) {
		if (sqg.levelTwoReportRows.length == 0) return;
		menu = document.getElementById('levTwoVizForm:taxMenu2');
	} else {
		console.log("Error in addQueryGroupToTaxMenu: invalid level");
	}

	var itemContainer = menu
			.getElementsByClassName('ui-selectcheckboxmenu-multiple-container')[0];
	var removeButton = itemContainer
			.getElementsByClassName('ui-selectcheckboxmenu-token')[0]
			.getElementsByClassName('ui-selectcheckboxmenu-token-icon')[0];
	removeButton.style.display = "none";
	resetMissingSpeciesControls(levelNumber);
}

function disableQueryGroup(level) {
	var taxMenuItems;
	if (level == 1) {
		if (sqg.levelOneReportRows.length == 0) return;
		taxMenuItems = document.getElementById('levOneVizForm:taxMenu_panel')
				.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
				.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
	} else if (level == 2) {
		if (sqg.levelTwoReportRows.length == 0) return;
		taxMenuItems = document.getElementById('levTwoVizForm:taxMenu2_panel')
				.getElementsByClassName('ui-selectcheckboxmenu-items-wrapper')[0]
				.getElementsByClassName('ui-selectcheckboxmenu-items')[0];
	} else {
		console.log("Error in disableQueryGroup: invalid level");
	}
	if (taxMenuItems.length == 0) {
		console.log("No taxMenuItems in level: " + level);
		return;
	}
	var item = taxMenuItems
			.getElementsByClassName('ui-selectcheckboxmenu-item')[0];
	var itemChkBox = item.getElementsByClassName('ui-chkbox')[0]
			.getElementsByClassName('ui-chkbox-box')[0];
	if (!itemChkBox.classList.contains('ui-state-disabled')) {
		itemChkBox.classList.add('ui-state-disabled');
	}
}

function getPercentile(frac, dist) {
	var n = dist.length;
	if (n == 1) {
		return dist[0];
	}
	var index = 1 + (n - 1) * frac;
	var indexLo = Math.floor(index);
	if (index == indexLo) {
		return dist[index];
	}

	var split = index - indexLo;
	var splitComplement = 1 - split;
	return (splitComplement * dist[indexLo - 1]) + (split * dist[indexLo]);
}

function svgTranslate(_element, _x, _y) {
	if (_element.transform.baseVal.length > 0) {
		var transform = _element.transform.baseVal.getItem(0);
		var mat = transform.matrix;

		mat = mat.translate(_x, _y);
		transform.setMatrix(mat);
	}
}

function svgSetXY(_element, _x, _y) {
	var transform = _element.transform.baseVal.getItem(0);
	var mat = transform.matrix;
	mat.e = _x;
	mat.f = _y;
	transform.setMatrix(mat);
}

function displaySpeciesInfo(event) {
	var target = event.target;
	var levelNumber = 1 * target.id.substring(1, 2);
	var rows = sqg.levelOneReportRows;
	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
	}

	var rowIndex = 1 * target.id.substring(3);
	var row = rows[rowIndex];
	var percentSim = roundToPrecision(row.percentSimilarity*100,2);
	var innerHTML;
	if(sqg.useCommon){
		innerHTML = row.commonName + " (taxid: " + row.speciesTaxId + ")<br/>";
	} else {
		innerHTML = row.scientificName + " (taxid: " + row.speciesTaxId + ")<br/>";
	}
	var proteinName = row.proteinName;
	if (proteinName.length > 100) {
		proteinName = proteinName.substring(0, 100) + "...";
	}
	innerHTML += capitalizeFirstLetter(proteinName) + "<br/>";
	innerHTML += percentSim + "% similarity<br/>";
	
	positionAndSizePopup(event, levelNumber, innerHTML)
	
	popupTotop(levelNumber);
}
	
function resizeSpeciesInfo(levelNumber, width) {
	var svg = document.getElementById("svg" + levelNumber);
	var startWidth = width;
	var popupDiv = document.getElementById("popupDiv" + levelNumber);
	var popupG = document.getElementById("popupG" + levelNumber);

	var svgScale = svg.parentElement.clientWidth / svg.width.baseVal.value;
	// Shring until a little too small
	var curHeight = popupDiv.clientHeight;
	while (popupDiv.clientHeight == curHeight) {
		width -= 5;
		if (width < 20) {
			width = startWidth - 5;
			break;
		}
		// popupDiv.style.width = width + "px";
		popupDiv.setAttribute("style", "width:" + width + "px;");
		popupDiv.parentNode.setAttribute("width", width + 10);
	}

	// Grow one click so it fits again
	width += 5;
	// popupDiv.style.width = width + "px";
	popupDiv.setAttribute("style", "width:" + width + "px;");
	popupDiv.parentNode.setAttribute("width", width + 10);

	// Now, is it too wide? If so, shrink font and width (without increasing row
	// count)
	var fontPixelHeight = 20;
	while (width > 600) {
		fontPixelHeight -= 2;
		popupDiv.style.fontSize = fontPixelHeight + "px";
		var curHeight = popupDiv.clientHeight;
		while (popupDiv.clientHeight == curHeight) {
			width -= 5;
			// popupDiv.style.width = width + "px";
			popupDiv.setAttribute("style", "width:" + width + "px;");
			popupDiv.parentNode.setAttribute("width", width + 10);
		}
		width += 5;
		// popupDiv.style.width = width + "px";
		popupDiv.setAttribute("style", "width:" + width + "px;");
		popupDiv.parentNode.setAttribute("width", width + 10);
	}
	// OK, size is good, what about position?!?
	var containBox = popupG.getBoundingClientRect();
	if (containBox.x + width > svg.parentElement.clientWidth - 40) {
		svgSetXY(popupG, right - 50 - width, mid - 20);
	}
}

function capitalizeFirstLetter(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}

function areOrthosVisible(levelNumber) {
	if (levelNumber === 1) {
		var checkbox = document
				.getElementById("levOneVizForm:showOrthologChkBox");
	} else if (levelNumber === 2) {
		var checkbox = document
				.getElementById("levTwoVizForm:showOrthologChkBox2");
	}
	var checkSpan = checkbox.firstElementChild.nextElementSibling.firstElementChild;
	return checkSpan.classList.contains("ui-icon-check");
}

function areEndangeredsVisible(levelNumber) {
	var checkbox;
	if (levelNumber === 1) {
		var checkbox = document
				.getElementById("levOneVizForm:showEndangeredChkBox");
	} else if (levelNumber === 2) {
		var checkbox = document
				.getElementById("levTwoVizForm:showEndangeredChkBox2");
	}

	var checkSpan = checkbox.firstElementChild.nextElementSibling.firstElementChild;
	return checkSpan.classList.contains("ui-icon-check");
}

function areThreatenedsVisible(levelNumber) {
	var checkbox;
	if (levelNumber === 1) {
		var checkbox = document
				.getElementById("levOneVizForm:showThreatenedChkBox");
	} else if (levelNumber === 2) {
		var checkbox = document
				.getElementById("levTwoVizForm:showThreatenedChkBox2");
	}

	var checkSpan = checkbox.firstElementChild.nextElementSibling.firstElementChild;
	return checkSpan.classList.contains("ui-icon-check");
}

function areModelsVisible(levelNumber) {
	var checkbox;
	if (levelNumber === 1) {
		var checkbox = document
				.getElementById("levOneVizForm:showModelOrganismsChkBox");
	} else if (levelNumber === 2) {
		var checkbox = document
				.getElementById("levTwoVizForm:showModelOrganismsChkBox2");
	}

	var checkSpan = checkbox.firstElementChild.nextElementSibling.firstElementChild;
	return checkSpan.classList.contains("ui-icon-check");
}

function getFirstUnder(threshold, levelNumber) {
	var resultTwoVals = new Array();
	resultTwoVals[0] = -1;
	resultTwoVals[1] = -1;

	var groups = sqg.taxGroups;
	if (groups.length == 0) {
		return -1;
	}
	for (var i = 0; i < groups.length; i++) {
		var group = groups[i];
		if (group.max < threshold) {
			resultTwoVals[0] = group.displayColumn;
			resultTwoVals[1] = group.max;
			return resultTwoVals;
		}
	}
	return resultTwoVals;
}

function setSpeciesCheck(speciesName, checkBool, levelNumber) {
	var tp = document.getElementById("levOneVizForm:speciesMenu_panel");
	if (levelNumber == 2) {
		tp = document.getElementById("levTwoVizForm:speciesMenu2_panel");
	}
	var list = tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
	for (var i = 0; i < list.length; i++) {
		if (list[i].dataset.itemValue == speciesName) {
			var checkSpan = list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild;

			if ((checkBool && !checkSpan.classList.contains("ui-icon-check"))
					|| (!checkBool && checkSpan.classList
							.contains("ui-icon-check"))) {
				checkSpan.click(); // clicking toggles it
				break; // there is only one to click
			}
		}
	}
	resetVisibleTaxa(levelNumber);
}

function removeFromLegend(event) {
	var target = event.target;
	var targetId = target.id;
	var levelNumber = 1 * targetId.substring(7, 8);
	var legIndex = 1 * targetId.substring(9);
	setSpeciesCheck(target.innerHTML, false, levelNumber);
}

function dragHighlightTaxon(event) {
  // event.preventDefault();
  
  if (!event.ctrlKey && !event.metaKey && !event.altKey){return;}
  var target = event.target;
  toggleSelect(event);
  var levelNumber = 1;
  if (target.classList.contains("taxonText2")) {
    levelNumber = 2;
  }
  var listeningTaxa = document.getElementsByClassName("taxonText" + levelNumber);
  for (var i = 0; i < listeningTaxa.length; i++) {
    var listeningTaxon = listeningTaxa[i];
    var taxonName = listeningTaxon.innerHTML;
    if (taxonName != sqg.taxGroups[0].name){
      listeningTaxon.addEventListener("mouseover",toggleSelect,false);
    }
  }
}

function toggleSelect(event){
  var target = event.target;
  var levelNumber = 1;
  if (target.classList.contains("taxonText2")) {
    levelNumber = 2;
  }
  if (target.classList.contains("removeThis"+levelNumber)) {
    target.classList.remove("removeThis"+levelNumber);
  } else {
    target.classList.add("removeThis"+levelNumber);
  }
}

function removeTaxon(event) {
	/*
	 * LOGIC:
	 *   If click had Ctrl, Meta, or Alt/Option keys ==> ignore this action
	 *   If not ==> delete this and all red (selected) Taxonomies
	 */
  if (event.ctrlKey || event.metaKey || event.altKey){return;}

  var target = event.target;

  var levelNumber = 1;
  if (target.classList.contains("taxonText2")) {
    levelNumber = 2;
  }
  var taxonName = target.innerHTML;
  setTaxaCheck(taxonName, false, levelNumber);
  var markedTaxa = document.getElementsByClassName("removeThis" + levelNumber);
  for (var i = 0; i < markedTaxa.length; i++) {
    var taxon = markedTaxa[i];
    var taxonName = taxon.innerHTML;
    setTaxaCheck(taxonName, false, levelNumber);
  }
  resetVisibleTaxa(levelNumber); 
}

function showCutoffInfo(event) {
	var target = event.target;
	var targetId = target.id;
	var levelNumber = 1 * targetId.substring(10, 11);
	target.style.strokeWidth = 5;
	var rows = sqg.levelOneReportRows;
	if (levelNumber == 2) {
		rows = sqg.levelTwoReportRows;
	}
	var cutoff = rows[0].cutoff;
	
	var cutoffPercent = roundToPrecision(cutoff, 2);
	var innerHTML = "Cutoff " + cutoffPercent + "% similarity";

	positionAndSizePopup(event, levelNumber, innerHTML);
	
	popupTotop(levelNumber);
}

function hideCutoffInfo(event) {
	var target = event.target;
	var targetId = target.id;
	var levelNumber = 1 * targetId.substring(10, 11);
	target.style.strokeWidth = 2;
	var popupG = document.getElementById("popupG" + levelNumber);
	popupG.classList.add("invis");
}

function showCommonName(event) {
	var target = event.target;
	var rows = sqg.levelOneReportRows;
	var levelNumber = 1;
	if (target.classList.contains("taxonText2")) {
		rows = sqg.levelTwoReportRows;
		levelNumber = 2;
	}

	var taxonName = target.innerHTML;
	var commonName = null;
	var names = new Array();
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		if (row.taxonomyName == taxonName) {
			names.push(row.commonName);
			if (names.length == 4) {
				break;
			}
		}
	}
	if (names.length == 0) {
		return;
	}
	var innerHTML = "- " + names[0];
	var lineCount = names.length;
	for (var i = 1; i < names.length; i++) {
		if (i == 3) {
			innerHTML += "<br/>...";
			lineCount = 4;
			break;
		}
		innerHTML += "<br/>- " + names[i];
	}
	
	positionAndSizePopup(event, levelNumber, innerHTML)
	
	popupTotop(levelNumber);

}

function positionAndSizePopup(event, levelNumber, innerHTML){
	var svg = document.getElementById("svg" + levelNumber);
	var svgScale;
	try{
		svgScale = svg.parentElement.clientWidth / svg.width.baseVal.value;
	} catch(e){ 
		svgScale = 1;
	};
	var popupDiv = document.getElementById("popupDiv" + levelNumber);
	var popupDivContainer = document.getElementById("popupContainer"+ levelNumber)
	popupDiv.innerHTML = innerHTML;
	var popupG = document.getElementById("popupG" + levelNumber);
	
	
	
	var svgP = svgFromScreen(event.clientX, event.clientY,levelNumber);
	svgP.x += 20;
	
	svgSetXY(popupG, svgP.x, svgP.y);
	popupG.classList.remove("invis");
	
	clientRect = popupDiv.getBoundingClientRect();
	
	popupDiv.setAttribute("style", "display:table;");
	popupDivContainer.parentNode.setAttribute("width", 600);  // max width 600
	if (svgParent != null){
		popupDivContainer.parentNode.setAttribute("height", clientRect.height/svgScale);
	} else {  // standalone svg
		popupDivContainer.parentNode.setAttribute("height", clientRect.height + 20);
	}
	popupTotop(levelNumber);

	// OK, size is good, what about position?!?
	var containBox = popupG.getBoundingClientRect();
	var width = clientRect.width/svgScale+20;
	var svgParent = svg.parentElement; 
	if (svgParent != null){  // this means that is not a standalone
								// (downloaded) SVG file
		clientWidth = svg.parentElement.clientwidth;
		var diffWidth = containBox.x+width*svgScale - (svg.parentElement.clientWidth -40);
		if (diffWidth > 0){
			svgSetXY(popupG, svgP.x - diffWidth, svgP.y+20);
		}
	}
}


function svgFromScreen(x,y, levelNumber){
	var svg = document.getElementById("svg" + levelNumber);
	var pt = svg.createSVGPoint();
	pt.x = x;
	pt.y = y;
	return pt.matrixTransform(svg.getScreenCTM().inverse());
}

function screenFromSVG(x,y, levelNumber){
	var svg = document.getElementById("svg" + levelNumber);
	var pt = svg.createSVGPoint();
	pt.x = x;
	pt.y = y;
	return pt.matrixTransform(svg.getScreenCTM());
}

function toggleSpecies(type, levelNumber) {
	// first remove any optional legend names
	if (checkIfLegendOption(sqg.legendNames[0])){
		sqg.legendNames.shift();
	}
	
	if (type == "orthos") {
		if (areOrthosVisible(levelNumber)) {
			document.getElementById("orthoG" + levelNumber).classList
					.remove("invis");
			document.getElementById("endangeredG" + levelNumber).classList
					.add("invis");
			document.getElementById("threatenedG" + levelNumber).classList
					.add("invis");
			document.getElementById("modelG" + levelNumber).classList
					.add("invis");
			sqg.legendNames.unshift("Ortholog Candidate");
		} else {
			document.getElementById("orthoG" + levelNumber).classList
					.add("invis");
		}
		redrawLegend(levelNumber);
		return;
	}
	if (type == "endangereds") {
		if (areEndangeredsVisible(levelNumber)) {
			document.getElementById("endangeredG" + levelNumber).classList
					.remove("invis");
			document.getElementById("threatenedG" + levelNumber).classList
					.add("invis");
			document.getElementById("orthoG" + levelNumber).classList
					.add("invis");
			document.getElementById("modelG" + levelNumber).classList
					.add("invis");
			sqg.legendNames.unshift("Endangered Species");
		} else {
			document.getElementById("endangeredG" + levelNumber).classList
					.add("invis");
		}
		redrawLegend(levelNumber);
		return;
	}
	if (type == "threateneds") {
		if (areThreatenedsVisible(levelNumber)) {
			document.getElementById("threatenedG" + levelNumber).classList
					.remove("invis");
			document.getElementById("endangeredG" + levelNumber).classList
					.add("invis");
			document.getElementById("orthoG" + levelNumber).classList
					.add("invis");
			document.getElementById("modelG" + levelNumber).classList
					.add("invis");
			sqg.legendNames.unshift("Threatened Species");
		} else {
			document.getElementById("threatenedG" + levelNumber).classList
					.add("invis");
		}
		redrawLegend(levelNumber);
		return;
	}
	if (type == "models") {
		if (areModelsVisible(levelNumber)) {
			document.getElementById("modelG" + levelNumber).classList
					.remove("invis");
			document.getElementById("orthoG" + levelNumber).classList
					.add("invis");
			document.getElementById("endangeredG" + levelNumber).classList
					.add("invis");
			document.getElementById("threatenedG" + levelNumber).classList
					.add("invis");
			sqg.legendNames.unshift("Common Model Organism");
		} else {
			document.getElementById("modelG" + levelNumber).classList
					.add("invis");
		}
		redrawLegend(levelNumber);
		return;
	}
}

function zoomAtPoint(levelNumber, pt, delta, wheelZoom){
	console.log ("delta: " + delta);
	var pp = sqg.plotParams;
//	if (delta > 0 && pp.zoomFactor > sqg.zoomMax){
//		return;
//	} else if (delta < 0 && pp.zoomFactor < sqg.zoomMin){
//		return;
//	}
		
	if (wheelZoom){
		if (delta > 0){
			pp.zoomFactor /= sqg.wheelZoomDelta;
		} else if (delta < 0){
			pp.zoomFactor *= sqg.wheelZoomDelta;
		}
	} else {
		if (delta > 0){
			pp.zoomFactor += sqg.arrowZoomDelta;
		} else if (delta < 0){
			pp.zoomFactor -= sqg.arrowZoomDelta;
		} else {
			console.log("???? delta: " + delta);
		}
	}
	
	var zoomFactorPercent = returnValidZoomLevel(pp.zoomFactor*100);
	pp.zoomFactor = zoomFactorPercent/100;

	var scaleFactor = pp.zoomFactorPrev/pp.zoomFactor;
	console.log("pp.zoomFactorPrev: " + pp.zoomFactorPrev);
	console.log("pp.zoomFactor: " + pp.zoomFactor);
	console.log("scaleFactor: " + scaleFactor);
		

	var svg = document.getElementById("svg" + levelNumber);
	var oldWidth = svg.viewBox.baseVal.width;
	var oldHeight = svg.viewBox.baseVal.height;
	
	console.log("old width/height: " + oldWidth+"/"+oldHeight);
		
	var adjX = pt.x-svg.viewBox.baseVal.x;
	var adjY = pt.y-svg.viewBox.baseVal.y;

	svg.viewBox.baseVal.width = oldWidth*scaleFactor;
	svg.viewBox.baseVal.height = oldHeight*scaleFactor;
	adjX *= scaleFactor -1.0;
	adjY *= scaleFactor -1.0;
	
	
	console.log("new width/height: " + svg.viewBox.baseVal.width+"/"+svg.viewBox.baseVal.height);
	
	svg.viewBox.baseVal.x -= adjX;
	svg.viewBox.baseVal.y -= adjY;
		
	rescaleSymbols(levelNumber, pp.zoomFactor);
	
	pp.zoomFactorPrev = pp.zoomFactor;
}

function wheelZoom(event) {
	console.log("inside wheelZoom event");
    var target = event.target;
	var svg = target; // until we determine otherwise
	var levelNumber = target.id.substring(3,4) *1;

	var tries = 0;
	while (levelNumber == 0 && tries <10){
		svg = target.parentElement;
		levelNumber = svg.id.substring(3,4) *1;
		tries++;
	}
	
	var pt = document.getElementById("svg" + levelNumber).createSVGPoint();
	pt.x = event.clientX; pt.y = event.clientY;
	var origPt = svgFromScreen(pt.x, pt.y, levelNumber);  // get point in SVG
															// coordinates
// console.log("origPt x:" + origPt.x + ", y:"+origPt.y);
	
	while (!svg.id.startsWith("svg")) {
		svg = svg.parentNode;
		if (svg == null) {
			alert("failed with svg: " + svg)
		}
	}
	var div = svg.parentNode;

	var levelNumber = svg.id.substring(3, 4) * 1;
	if (levelNumber == 1){
		panZoomValue = document.getElementById("levOneVizForm:hiddenZoomVal").value;
	} else if (levelNumber == 2){
		panZoomValue = document.getElementById("levTwoVizForm:hiddenZoomVal").value;
	}
	if (panZoomValue=="false"){
		return;
	}
// if (zoomPanOff(levelNumber)) {
// return;
// }
	var delta = -1 * Math.sign(event.deltaY);
	
	if (pp.zoomVal > sqg.zoomMaxDefault || pp.zoomVal < sqg.zoomMinDefault){
		delta = 0;
	}

// console.log("calling zoomAtPoint:");
// console.log("xpt:" + origPt.x +", ypt:" + origPt.y);
// console.log("delta:"+delta);
	zoomAtPoint(levelNumber, origPt, delta, true);
	
	
// updatePlotControlValues(levelNumber);
	debounceUpdatePlotControlValues();
}


function debounce(func, wait, immediate){
	var timeout;
	return function(){
		var context = this, args = arguments;
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(function(){
			timeout = null;
			if (!immediate){
				func.apply(context, args);
			}
		}, wait);
		if (callNow) func.apply(context, args);
	};
}

// function html2svg(levelNumber, x, y) {
// var results = new Array();
// var svg = document.getElementById("svg" + levelNumber);
// // var pp = sqg.plotParams1;
// // if (levelNumber == 2) {
// // pp = sqg.plotParams2;
// // }
// // First translate the clientX and clientY to the correct positions in the
// // Div
// var div = svg.parentNode;
// x -= div.offsetLeft; //
// x -= 20; // margin of div
//
// y -= div.getBoundingClientRect().top;
// y -= 10; // margin of div
//
// // Now scale to SVG space
// var svgScale = (div.clientWidth - 40) / svg.width.baseVal.value;
// y /= svgScale;
// x /= svgScale;
//
// // Finally, translate according to offset of svg
// x += svg.viewBox.baseVal.x;
// y += svg.viewBox.baseVal.y
// results[0] = x;
// results[1] = y;
// return results;
// }

// /**
// * NOT TESTED YET!!
// */
// function svg2html(levelNumber, x, y) {
// var results = new Array();
// var svg = document.getElementById("svg" + levelNumber);
// var div = svg.parentNode;
//
// // First, translate according to offset of svg
// x -= svg.viewBox.baseVal.x;
// y -= svg.viewBox.baseVal.y
//
// // Now scale to SVG space
// var svgScale = (div.clientWidth - 40) / svg.width.baseVal.value;
// x *= svgScale;
// y *= svgScale;
//
// // Finally translate the clientX and clientY to the correct positions in the
// // Div
// var div = svg.parentNode;
// x += div.offsetLeft; //
// x += 20; // margin of div
//
// y += div.getBoundingClientRect().top;
// y += 10; // margin of div
//
// results[0] = x;
// results[1] = y;
// return results;
// }

// function zoomIn(levelNumber) {
//
// var pp = sqg.plotParams;
// var z = -1;
// var zf = pp.zoomFactor;
// for (var i = 0; i < sqg.zoomArray.length; i++) {
// z = sqg.zoomArray[i];
// if (zf < z) {
// break;
// }
// }
// pp.zoomFactor = z;
//
// var svg = document.getElementById("svg" + levelNumber);
// var h = pp.xAxisTextY + pp.bottomRest;
// var w = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1));
// if (w < pp.minWidth){w = pp.minWidth;}
// svg.viewBox.baseVal.width = w / pp.zoomFactor;
// svg.viewBox.baseVal.height = h / pp.zoomFactor;
//
// rescaleSymbols(levelNumber, pp.zoomFactor);
//
// var input = document.getElementById("zmVal" + levelNumber);
// input.value = Math.floor(pp.zoomFactor * 100);
// }

function zoomResetEvent(event) {
	var target = event.target;
	var levelNumber = target.id.substring(14, 15) * 1;
	zoomReset(levelNumber);
}

function zoomReset(levelNumber){
	var pp = sqg.plotParams;
	pp.zoomFactor = 1;
	var svg = document.getElementById("svg" + levelNumber);
	var h = pp.topMargin + pp.height + pp.bottomTextHeight + pp.bottomRest;
	
// var w = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1));
	var w = getBoxPlotMinWidth(levelNumber);
	
	var oldWidth = svg.viewBox.baseVal.width;
	var oldHeight = svg.viewBox.baseVal.height;
	
// if (w < pp.minWidth){w = pp.minWidth;}
	
	svg.viewBox.baseVal.width = w;
	svg.viewBox.baseVal.height = h;
	svg.viewBox.baseVal.x = 0;
	svg.viewBox.baseVal.y = 0;

	rescaleSymbols(levelNumber, pp.zoomFactor);

	pp.zoomFactorPrev = 1;
	
	resizeForLegend(levelNumber);
	
	// this hack is needed to work around a firefox bug that prevents updating
	// after zoom reset
	var hackObject = document.getElementsByClassName("taxonText"+levelNumber)[0];
	var hackEvent = new MouseEvent('mouseover', {bubbles: true, clientX: hackObject.right, clientY: hackObject.bottom});
	hackObject.dispatchEvent(hackEvent);
	var hackEvent = new MouseEvent('mouseout', {bubbles: true, clientX: hackObject.right, clientY: hackObject.bottom});
	hackObject.dispatchEvent(hackEvent);
}

// function zoomOut(levelNumber) {
// var pp = sqg.plotParams;
// var z = 100;
// var zf = pp.zoomFactor;
// for (var i = sqg.zoomArray.length - 1; i >= 0; i--) {
// z = sqg.zoomArray[i];
// if (zf > z) {
// break;
// }
// }
// pp.zoomFactor = z;
//
// var svg = document.getElementById("svg" + levelNumber);
// var h = pp.xAxisTextY + pp.bottomRest;
// var w = pp.leftMargin + Math.floor((pp.barWidth+pp.barSpacing) *
// (getTaxMenuCheckedItems(levelNumber).length + 1));
// if (w < pp.minWidth){w = pp.minWidth;}
// svg.viewBox.baseVal.width = w / pp.zoomFactor;
// svg.viewBox.baseVal.height = h / pp.zoomFactor;
//
// rescaleSymbols(levelNumber, pp.zoomFactor);
//
// var input = document.getElementById("zmVal" + levelNumber);
// input.value = Math.floor(pp.zoomFactor * 100);
// }

// //var sDiv = document.getElementById("svgContainer");
// var svg = document.getElementById("svg1");
// var baseVal = svg.viewBox.baseVal;
// //console.log("svgZoomFactor: "+svgZoomFactor,"mouseX: "+svgMouseX,"mouseY:
// //"+svgMouseY,"x: "+xScale,"y: "+yScale,"baseX: "+baseVal.x,"baseY:
// //"+baseVal.y);

// var curWidth = 1 * baseVal.width;
// if (e.deltaY > 0 && svgZoomFactor > minZoom) {
// //console.log("smaller "+curWidth);
// //var newX = baseVal.x - (svgMouseX*0.20);
// //var newY = baseVal.y - (svgMouseY*0.20);
// svgZoomFactor *= 0.9;
// var xScale = svgMouseX / svgZoomFactor;
// var yScale = svgMouseY / svgZoomFactor;
// var newX = baseVal.x - (xScale * 0.1);
// var newY = baseVal.y - (yScale * 0.1);

// //if (newX > 0){baseVal.x = newX;}else {baseVal.x = 0;}
// //if (newY > 0){baseVal.y = newY;}else {baseVal.y = 0;}
// baseVal.height /= 0.9;
// baseVal.width /= 0.9;
// baseVal.x = newX;
// baseVal.y = newY;
// //baseVal.x = slideX;
// //baseVal.y = slideY;
// } else if (e.deltaY < 0 && svgZoomFactor < maxZoom) {
// baseVal.height *= 0.9;
// baseVal.width *= 0.9;
// var xScale = svgMouseX / svgZoomFactor;
// var yScale = svgMouseY / svgZoomFactor;
// var newX = baseVal.x + (xScale * 0.1);
// var newY = baseVal.y + (yScale * 0.1);
// svgZoomFactor /= 0.9;
// baseVal.x = newX;
// baseVal.y = newY;
// //baseVal.x = slideX;
// //baseVal.y = slideY;
// }
// var varBoxes = document.getElementsByClassName('varBox');
// for (var i = 0; i < varBoxes.length; i++) {
// var vb = varBoxes[i];
// var bw = 1 / svgZoomFactor;
// //console.log(bw,svgZoomFactor);
// vb.style.setProperty("border-width", bw);
// }
// }

function download(filename, text) {
	var element = document.createElement('a');
	element.setAttribute('href', 'data:text/plain;charset=utf-8,'
			+ encodeURIComponent(text));
	element.setAttribute('download', filename);

	element.style.display = 'none';
	document.body.appendChild(element);

	element.click();

	document.body.removeChild(element);
}

// to be used for retrieving SVG source in Java
function getSVGSource(elementId) {
	console.log("inside getSVGSource");
	var element = document.getElementById(elementId);
	// addCSStoSVG(element);
	var theCSS = retrieveCSS(element);
	// addJavaScripttoSVG(element);
	// var theJS = "";
	var theJS = retrieveJavaScript(element);
	var theSQG = retrieveSQG();
	element.setAttribute("xmlns", "http://www.w3.org/2000/svg");
	var theSVG = element.outerHTML;
	console.log("calling getSVGSourceRC");
	getSVGSourceRC([ {
		name : 'theSVG',
		value : theSVG
	}, {
		name : 'theCSS',
		value : theCSS
	}, {
		name : 'theJS',
		value : theJS
	}, {
		name : 'theSQG',
		value : theSQG
	} ]);
}


function changeSpeciesNameType(levelNumber){
	console.log("inside changeSpeciesNameType for level: " + levelNumber);
	
	var choice;
	if (levelNumber == 1){
		choice= document.getElementById("levOneVizForm:hiddenSpeciesNameType").value;
	} else if (levelNumber == 2){
		choice= document.getElementById("levTwoVizForm:hiddenSpeciesNameType").value;
	}
	console.log("choice: " + choice);
	if (choice === "COMMON"){
		sqg.useCommon=true;
	} else {
		sqg.useCommon=false;
	}
	
	// load menuIndex values if first time using COMMON or SCIENTIFIC species
	// name
	if (sqg.useCommon == true && sqg.speciesCommonObjects[0].menuIndex == -1){
		var allLegendNames = getSpeciesMenuItems(levelNumber);
		var activeLegendNames = getLegendSpeciesItems(levelNumber);
		for (var i = 0; i < allLegendNames.length; i++) {
			var name = allLegendNames[i];
			sqg.speciesCommonObjects[sqg.speciesCommonIndexes.indexOf(name)].menuIndex = i;
		}
	} else if(sqg.useCommon==false && sqg.speciesScientificObjects[0].menuIndex == -1){
		var allLegendNames = getSpeciesMenuItems(levelNumber);
		var activeLegendNames = getLegendSpeciesItems(levelNumber);
		for (var i = 0; i < allLegendNames.length; i++) {
			var name = allLegendNames[i];
			sqg.speciesScientificObjects[sqg.speciesScientificIndexes.indexOf(name)].menuIndex = i;
		}
		mapScientificToCommonObjects(levelNumber);
	} else {
		console.log("error in changeSpeciesNameType");
	}
	
	if (sqg.useCommon){
		sqg.speciesObjects = sqg.speciesCommonObjects;
		sqg.speciesIndexes = sqg.speciesCommonIndexes;
	} else {
		sqg.speciesObjects = sqg.speciesScientificObjects;
		sqg.speciesIndexes = sqg.speciesScientificIndexes;
	}
	var oldLegendNames = sqg.legendNames;
	sqg.legendNames = getLegendSpeciesItems(levelNumber);
	
	// now map old species name to new species name for menu
	var namesToRestore = new Array();
	if (sqg.useCommon){
		console.log("using Common names");
		for (var i = 0; i < oldLegendNames.length; i++ ){
			console.log("name: " + oldLegendNames[i]);
			var tmp = getAllCommonFromScientific(levelNumber, oldLegendNames[i]);
			for (var j=0; j < tmp.length; j++){
				namesToRestore.push(tmp[j]);
			}
		}
	} else {
		console.log("using Scientific names");
		for (var i = 0; i < oldLegendNames.length; i++ ){
			var tmp = getAllScientificFromCommon(levelNumber, oldLegendNames[i]);
			for (var j=0; j < tmp.length; j++){
				namesToRestore.push(tmp[j]);
			}
		}
	}
	
	drawTaxGroups(levelNumber);
	resetPlotWidths(levelNumber);
	drawTaxGroups(levelNumber);
	resetMissingSpeciesControls(levelNumber);
	zoomReset(levelNumber);
	
	restoreLegendItems(levelNumber, namesToRestore);
// redrawLegend(levelNumber);
	
}

function changeMaintainCommonLegendInfo(levelNumber){
	sqg.maintainCommonLegendInfo = !sqg.maintainCommonLegendInfo;
	if (!sqg.useCommon){
		redrawLegend(levelNumber);
	}
}

function getAllScientificFromCommon(levelNumber, commonName){
	var resList = new Array();
	var reportRows;
	if (levelNumber == 1){
		reportRows = sqg.levelOneReportRows;
	} else {
		reportRows = sqg.levelTwoReportRows;
	}
	for (var i=0; i<reportRows.length; i++){
		var row = reportRows[i];
		if (row.commonName === commonName){
			resList.push(row.scientificName);
		}
	}
	
	return resList;
	
}

function getAllCommonFromScientific(levelNumber, scientificName){
	var resList = new Array();
	var reportRows;
	if (levelNumber == 1){
		reportRows = sqg.levelOneReportRows;
	} else {
		reportRows = sqg.levelTwoReportRows;
	}
	for (var i=0; i<reportRows.length; i++){
		var row = reportRows[i];
		if (row.scientificName === scientificName){
			resList.push(row.commonName);
		}
	}
	
	return resList;
}

function mapScientificToCommonObjects(levelNumber){
	var reportRows;
	if (levelNumber == 1){
		reportRows = sqg.levelOneReportRows;
	} else {
		reportRows = sqg.levelTwoReportRows;
	}
	var sciObj = sqg.speciesScientificObjects;
	var commonObj = sqg.speciesCommonObjects;
	
	var sciToCommNameMap = new Map();
	for (var i=0; i<reportRows.length; i++){
		var row = reportRows[i];
		sciToCommNameMap.set(row.scientificName, row.commonName);
	}
	var commonNameToIndexMap = new Map();
	for (var i=0; i<commonObj.length; i++){
		var theObj = commonObj[i];
		commonNameToIndexMap.set(theObj.name, i);
	}
	
	for (var i=0; i<sciObj.length; i++){
		var commonIndex = commonNameToIndexMap.get(sciToCommNameMap.get(sciObj[i].name));
		sqg.scientificToCommonObjects.set(i, commonIndex);
	}
	
}

function getNextShape(shape){
	var loc;
	for (var i=0; i< sqg.shapes.length; i++){
		if (shape == sqg.shapes[i]){
			loc = i+1;
		}
	}
	if (loc >= sqg.shapes.length){
		loc -= sqg.shapes.length;
	}
	
	return sqg.shapes[loc];
}

function getNextColor(color){
	var loc;
	for (var i=0; i< sqg.colors.length; i++){
		if (color == sqg.colors[i]){
			loc = i+1;
		}
	}
	if (loc >= sqg.colors.length){
		loc -= sqg.colors.length;
	}
	
	return sqg.colors[loc];
}


function resetAll(levelNumber){
	bwReset(levelNumber);
	zoomReset(levelNumber);
}

//functions for levelOneReport page

function disableCheckAllL4ViewMenu(disable){
	
	menuPanel=document.getElementById("tabView:reportForm:lev4JobChoices_panel");
	chkbox=menuPanel.firstElementChild.firstElementChild.getElementsByClassName("ui-chkbox-box")[0];
	if(disable){
		chkbox.classList.add("ui-state-disabled");	
	} else {
		chkbox.classList.remove("ui-state-disabled")
	}
}


//general helper functions
function roundToPrecision(value, decimals){
	return Number(Math.round(value + 'e' + decimals) + 'e-' + decimals).toFixed(decimals);
}



function below_is_not_used_0000000000000000000000000000000000000000000000000000000000000() {
	// comment
}

// function clickBoxPlotButton() {
// if (vizNamespace.viz1Window !== undefined) {
// //console.log('vizNamespace.viz1Window exists!!!');
// var boxButton =
// vizNamespace.viz1Window.document.getElementById('levOneVizForm:boxPlotButton');
// if (boxButton !== null) {
// boxButton.click();
// }
// } else {
// //console.log('vizNamespace.viz1Window DNE!!!');
// var boxButton = document.getElementById('levOneVizForm:boxPlotButton');
// if (boxButton !== null) {
// boxButton.click();
// }
// }
// }

// ========================================================================================
// == Redraw / regenerate / recreate
// ========================================================================================

// ========================================================================================
// == Lookup utilities
// ========================================================================================

// function isTaxaChecked(taxon, levelNumber) {
// //console.log("Taxon: " + taxon);
// var tp = document.getElementById("levOneVizForm:taxMenu_panel");
// if (levelNumber == 2) {
// tp = document.getElementById("levTwoVizForm:taxMenu2_panel");
// }
// var list =
// tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
// for (var i = 0; i < list.length; i++) {
// if (list[i].dataset.itemValue == taxon) {
// return
// list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild.classList.contains("ui-icon-check");
// }
// }
// return false;
// }

// function isSpeciesChecked(commonName, levelNumber) {
// var tp = document.getElementById("levOneVizForm:speciesMenu_panel");
// if (levelNumber == 2) {
// tp = document.getElementById("levTwoVizForm:speciesMenu2_panel");
// }
// var list =
// tp.firstElementChild.nextElementSibling.firstElementChild.childNodes;
// for (var i = 0; i < list.length; i++) {
// if (list[i].dataset.itemValue == commonName) {

// console.log(list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild);
// return
// list[i].firstElementChild.firstElementChild.nextElementSibling.firstElementChild.classList.contains("ui-icon-check");
// }
// }
// return false;
// }

// ========================================================================================
// == String utilities
// ========================================================================================

// function capAndDespace(string) {
// var capped = string.charAt(0).toUpperCase() + string.slice(1);
// return capped.replace(/ /g, "_");
// }
// ========================================================================================
// == Math and graphics utilities
// ========================================================================================

// function scaleSVGtoFit() {
// var svgDiv = document.getElementById("svgDiv");
// var svg = document.getElementById("svg1");
// var widthText = svgDiv.style.width;
// var width = widthText.substring(0, widthText.length - 2) * 1;
// var heightText = svgDiv.style.height;
// var height = heightText.substring(0, heightText.length - 2) * 1;
// //console.log(width, height);
// }

// function resizeScreen() {
// var fullWidth = document.getElementById("fullWidth");
// var fullHeight = document.getElementById("fullHeight");
// var screenWidth = document.getElementById("screenWidth");
// var screenHeight = document.getElementById("screenHeight");
// var svgDiv = document.getElementById("svgDiv");
// var svg = document.getElementById("svg1");
// if (fullWidth.checked) {
// screenWidth.disabled = true;
// var newWidth = window.innerWidth - 25;
// screenWidth.value = newWidth;
// svgDiv.style.width = newWidth + "px";
// svg.width.baseVal = newWidth;
// svg.viewBox.baseVal.width = newWidth;
// } else {
// screenWidth.disabled = false;
// var newWidth = screenWidth.value * 1;
// svgDiv.style.width = newWidth + "px";
// svg.width.baseVal = newWidth;
// svg.viewBox.baseVal.width = newWidth;
// }
// if (fullHeight.checked) {
// screenHeight.disabled = true;
// var newHeight = window.innerHeight - 60;
// screenHeight.value = newHeight;
// svgDiv.style.height = newHeight + "px";
// svg.height.baseVal = newHeight;
// svg.viewBox.baseVal.height = newHeight;
// } else {
// screenHeight.disabled = false;
// var newHeight = screenHeight.value * 1;
// svgDiv.style.height = newHeight + "px";
// svg.height.baseVal = newHeight;
// svg.viewBox.baseVal.height = newHeight;
// }
// //console.log(svg.viewBox);
// scaleSVGtoFit();
// }

// ========================================================================================
// == File I/O and read utilities
// ========================================================================================

// var openJSONFile = function(event) {
// sqg.levelOneReportRows = null;
// var text;
// var input = event.target;

// var reader = new FileReader();
// reader.onload = function() {
// text = reader.result;
// };
// reader.readAsText(input.files[0]);

// setTimeout(function() {
// //console.log("waiting...");
// sqg.levelOneReportRows = JSON.parse(text);
// jsonFile(sqg.levelOneReportRows);
// }, 2000);
// };

// ============
// == Added from Light Rail includes some zoom / pan functions
// ============

// svgEl.addEventListener("wheel", wheelZoom, false);
// function svgFieldCoord2svgCoord(x, y) {
// var baseVal = document.getElementById("svg1").viewBox.baseVal;

// var xOut = (x / svgZoomFactor) + baseVal.x;
// var yOut = (y / svgZoomFactor) + baseVal.y;
// return [ xOut, yOut ];
// }

// function setStyleForClass(className, styleName, styleVal) {
// //console.log(className, styleName, styleVal);
// var elements = document.getElementsByClassName(className);
// for (var i = 0; i < elements.length; i++) {
// var el = elements[i];
// //console.log(i);
// //console.log("el=", el);
// if (typeof el != "object") {
// continue;
// }
// el.style[styleName] = styleVal;
// }
// }

// function globalMouseMove(e) {
// var deltaX = e.clientX - globalClientX;
// var deltaY = e.clientY - globalClientY;
// if (Math.abs(deltaX) > 60) {
// deltaX = 0;
// }
// if (Math.abs(deltaY) > 60) {
// deltaY = 0;
// }
// globalClientX = e.clientX;
// globalClientY = e.clientY;

// if (mouseDragFlags.modelVar) {
// var theG = varBoxDragItem.parentNode.parentNode;
// var svgBaseVal = document.getElementById("svg1").viewBox.baseVal;
// svgTranslate(theG, deltaX * (svgBaseVal.width / svgWidth), deltaY *
// (svgBaseVal.height / svgHeight));
// redrawLinks(theG);
// return;
// }

// if (mouseDragFlags.pseudoVar) {
// var theG = varBoxDragItem.parentNode.parentNode;
// var svgBaseVal = document.getElementById("svg1").viewBox.baseVal;
// svgTranslate(theG, deltaX * (svgBaseVal.width / svgWidth), deltaY *
// (svgBaseVal.height / svgHeight));
// redrawLinks(theG);
// return;
// }

// if (mouseDragFlags.svg) {
// var baseVal = document.getElementById("svg1").viewBox.baseVal;
// baseVal.x -= deltaX * (baseVal.width / svgWidth);
// baseVal.y -= deltaY * (baseVal.height / svgHeight);
// return;
// }

// if (mouseDragFlags.divider) {
// var svgCont = document.getElementById("svgContainer");
// var heightString = svgCont.style.height;
// var curHeight = parseInt(heightString.substring(0, heightString.length - 2));
// curHeight += deltaY;

// svgCont.style.height = curHeight + "px";
// return;
// }

// if (mouseDragFlags.sliderBar) {
// var sliderLine = document.getElementById("yearSlider");
// var oldX = sliderLine.x1.baseVal.value;
// var newX = oldX + deltaX;
// if (newX < 34) {
// newX = 34;
// }
// if (newX > 286) {
// newX = 286;
// }

// sliderLine.x1.baseVal.value = newX;
// sliderLine.x2.baseVal.value = newX;

// //var divText = document.getElementById("radarTitleDiv");
// var divText2 = document.getElementById("year_slider_div");

// selectedYear = getYearFromSliderPos();
// //divText.innerHTML = "Sustainability Radar Plots: "+selectedYear;
// divText2.firstChild.textContent = "Time Slider: " + selectedYear;
// for (var i = 1; i < 6; i++) {
// if (scen[i].data != null && scen[i].data != "changed") {
// plotRadarData(i);
// updateVarValue(i);
// }
// }
// updateHistProjValues();
// for (var i = 1; i < 3; i++) {
// var graphSliderLine = document.getElementById("yearSlider" + i);
// var newX = 80 + 8 * (selectedYear - 2000);
// graphSliderLine.x1.baseVal.value = newX;
// graphSliderLine.x2.baseVal.value = newX;
// }
// return;
// }
// }

// function numberWithCommas(x) {
// var parts = x.toString().split(".");
// parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
// return parts.join(".");
// }

// function svgMouseMove(e) {
// //setFocusSelection("svgTab");
// var svg = document.getElementById("svg1");
// var caller = e.target;
// if (focusSelection != "svgTab") {
// return;
// }
// if (mouseDragFlag.svg) {
// //svgMoveFlag = 1;
// var baseVal = document.getElementById("svg1").viewBox.baseVal;
// var deltaX = (svgClickDragStartX - e.clientX) * (baseVal.width / svgWidth);
// baseVal.x += deltaX;
// //if (baseVal.x < 0){baseVal.x = 0;}
// //if (baseVal.x > svgWidth){baseVal.x = svgWidth;}
// svgClickDragStartX = e.clientX;

// var deltaY = (svgClickDragStartY - e.clientY) * (baseVal.height / svgHeight);
// baseVal.y += deltaY;
// //if (baseVal.y < 0){baseVal.y = 0;}
// //if (baseVal.y > svgHeight){baseVal.y = svgHeight;}
// svgClickDragStartY = e.clientY;
// //console.log(svg.viewBox.baseVal.x + " , " + svg.viewBox.baseVal.y);
// } else if (caller === svg) {
// svgMouseX = e.layerX - 6;
// svgMouseY = e.layerY - 6;
// //console.log(svgMouseX, svgMouseY);
// } else if (varBoxDragItem != null) {
// if (caller.nodeType == 3) {
// return;
// }
// //console.log("dragging",caller);
// //caller.style.MozUserSelect="none";
// var svgBaseVal = document.getElementById("svg1").viewBox.baseVal;
// var theG = varBoxDragItem.parentNode.parentNode;
// var deltaX = (e.clientX - varBoxClickDragStartX) * (svgBaseVal.width /
// svgWidth);
// var deltaY = (e.clientY - varBoxClickDragStartY) * (svgBaseVal.height /
// svgHeight);
// svgTranslate(theG, deltaX, deltaY);
// varBoxClickDragStartX = e.clientX;
// varBoxClickDragStartY = e.clientY;
// varBoxDragItem.style.cursor = "move";
// //redrawSectorLinks(theG);
// redrawLinks(theG);
// }
// }

// function svgMouseUp(e) {
// //setFocusSelection("svgTab");
// if (moveSizeIsActive()) {
// if (draggingMouseStart != null) {
// draggingMouseStart = null; // IN CASE THE MOUSE WAS IN THE SVG WHEN
// //IT WENT UP
// return;
// }
// mouseDragFlags.svg = false;
// varBoxDragItem = null;
// }
// }


// function getEndXY(g1, g2) {
// vb1 = g1.firstElementChild.firstElementChild;
// vb2 = g2.firstElementChild.firstElementChild;
// var vb1xy = getGxy(g1);
// var vb2xy = getGxy(g2);
// var hw2 = vb2.clientWidth / 2;
// var hh2 = vb2.clientHeight / 2;
// var x1 = vb1xy[0];
// var y1 = vb1xy[1];
// var x2 = vb2xy[0];
// var y2 = vb2xy[1];
// if (x2 == x1) {
// return ([ x2, y2 + (Math.sign(y1 - y2)) * hh2 ]);
// }
// if (y2 == y1) {
// return ([ x2 + (Math.sign(x1 - x2)) * hw2, y2 ]);
// }
// var ratioX = hw2 / Math.abs(x2 - x1);
// var ratioY = hh2 / Math.abs(y2 - y1);
// if (ratioX < ratioY) {
// return ([ x2 + (Math.sign(x1 - x2)) * hw2, y2 + (Math.sign(y1 - y2)) * (hh2 *
// ratioX) ]);
// }
// return ([ x2 + (Math.sign(x1 - x2)) * (hw2 * ratioY), y2 + (Math.sign(y1 -
// y2)) * hh2 ]);
// }

// function getGxy(g) {
// if (g == null) {
// return ([ 0, 0 ]);
// }
// var vb = g.firstElementChild.firstElementChild;
// var x = g.transform.baseVal.getItem(0).matrix.e + (vb.clientWidth / 2);
// var y = g.transform.baseVal.getItem(0).matrix.f + (vb.clientHeight / 2);
// return ([ x, y ]);
// }

// function findSVGBoundaries() {
// var varBoxes = document.getElementsByClassName('varBox');
// for (var i = 0; i < varBoxes.length; i++) {
// var thisBox = varBoxes[i];
// var thisGroup = thisBox.parentNode.parentNode;
// var ulPoint = getGroupULCorner(thisGroup);
// svgVisMinX = Math.min(svgVisMinX, ulPoint[0]);
// svgVisMinY = Math.min(svgVisMinY, ulPoint[1]);
// var xMax = ulPoint[0] + thisBox.clientWidth;
// var yMax = ulPoint[1] + thisBox.clientHeight;
// svgVisMaxX = Math.max(svgVisMaxX, xMax);
// svgVisMaxY = Math.max(svgVisMaxY, yMax);
// }
// }

// function wheelZoom(e) {
// if (!moveSizeIsActive()) {
// return;
// }
// var minZoom = 0.3;
// var maxZoom = 2;

// //var sDiv = document.getElementById("svgContainer");
// var svg = document.getElementById("svg1");
// var baseVal = svg.viewBox.baseVal;
// //console.log("svgZoomFactor: "+svgZoomFactor,"mouseX: "+svgMouseX,"mouseY:
// //"+svgMouseY,"x: "+xScale,"y: "+yScale,"baseX: "+baseVal.x,"baseY:
// //"+baseVal.y);

// var curWidth = 1 * baseVal.width;
// if (e.deltaY > 0 && svgZoomFactor > minZoom) {
// //console.log("smaller "+curWidth);
// //var newX = baseVal.x - (svgMouseX*0.20);
// //var newY = baseVal.y - (svgMouseY*0.20);
// svgZoomFactor *= 0.9;
// var xScale = svgMouseX / svgZoomFactor;
// var yScale = svgMouseY / svgZoomFactor;
// var newX = baseVal.x - (xScale * 0.1);
// var newY = baseVal.y - (yScale * 0.1);

// //if (newX > 0){baseVal.x = newX;}else {baseVal.x = 0;}
// //if (newY > 0){baseVal.y = newY;}else {baseVal.y = 0;}
// baseVal.height /= 0.9;
// baseVal.width /= 0.9;
// baseVal.x = newX;
// baseVal.y = newY;
// //baseVal.x = slideX;
// //baseVal.y = slideY;
// } else if (e.deltaY < 0 && svgZoomFactor < maxZoom) {
// baseVal.height *= 0.9;
// baseVal.width *= 0.9;
// var xScale = svgMouseX / svgZoomFactor;
// var yScale = svgMouseY / svgZoomFactor;
// var newX = baseVal.x + (xScale * 0.1);
// var newY = baseVal.y + (yScale * 0.1);
// svgZoomFactor /= 0.9;
// baseVal.x = newX;
// baseVal.y = newY;
// //baseVal.x = slideX;
// //baseVal.y = slideY;
// }
// var varBoxes = document.getElementsByClassName('varBox');
// for (var i = 0; i < varBoxes.length; i++) {
// var vb = varBoxes[i];
// var bw = 1 / svgZoomFactor;
// //console.log(bw,svgZoomFactor);
// vb.style.setProperty("border-width", bw);
// }
// }

// function toggleRadarTooltip(e) {
// var t = e.target;
// //console.log(e.clientX,t.getAttributeNS(null,'x'));
// var curText = t.innerHTML;
// for (var i = 0; i < spikeVars.length; i++) {
// if (spikeVars[i].brief == curText) {
// //console.log(spikeVars[i].x+svgCenterX);
// t.innerHTML = spikeVars[i].full;
// t.setAttributeNS(null, 'style', "font-weight:bold;font-size:10pt;");
// var w = t.getBBox().width;
// var newX = spikeVars[i].x + svgCenterX - (w / 2);
// //console.log(newX);
// if (newX < 0) {
// newX = 0;
// }
// if (newX + w > 400) {
// newX = 400 - w;
// }
// if (newX < 0 || newX + w > 400) {
// t.setAttributeNS(null, 'style', "font-weight:bold;font-size:8.5pt;");
// newX = 0;
// }
// t.setAttributeNS(null, 'x', newX);
// return;
// } else if (spikeVars[i].full == curText) {
// t.innerHTML = spikeVars[i].brief;
// t.setAttributeNS(null, 'x', spikeVars[i].x + svgCenterX);
// t.setAttributeNS(null, 'style', 'font-size:10pt');
// //console.log(t.getBBox());
// return;
// }
// }
// }

// function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
// var angleInRadians = angleInDegrees * Math.PI / 180.0;
// var deltaX = radius * Math.cos(angleInRadians);
// var deltaY = radius * Math.sin(angleInRadians);
// var x = centerX + deltaX;
// var y = centerY + deltaY;
// return [ x, y ]; // Double check, the original was below!
// //return {x , y};
// }

// function describeArc(x, y, radius, startAngle, endAngle) {
// var invStartAngle = 0 - endAngle;
// var invEndAngle = 0 - startAngle;
// var start = polarToCartesian(x, y, radius, invEndAngle);
// var end = polarToCartesian(x, y, radius, invStartAngle);

// var arcSweep = invEndAngle - invStartAngle <= 180 ? "0" : "1";

// var sweep = 0;
// if (invEndAngle < invStartAngle) {
// sweep = 1;
// }
// var d = [ "M", start.x, start.y, "A", radius, radius, 0, arcSweep, sweep,
// end.x, end.y ].join(" ");
// return d;
// }

// function getTextPath(x, y, radius, startAngle, endAngle) {
// var invStartAngle = 0 - endAngle;
// var invEndAngle = 0 - startAngle;
// if (invEndAngle < invStartAngle) {
// radius *= 1.2;
// } else {
// radius *= 1.35;
// }
// var start = polarToCartesian(x, y, radius, invEndAngle);
// var end = polarToCartesian(x, y, radius, invStartAngle);

// var arcSweep = invEndAngle - invStartAngle <= 180 ? "0" : "1";
// var sweep = 0;
// if (invEndAngle < invStartAngle) {
// sweep = 1;
// }

// var d = [ "M", start.x, start.y, "A", radius, radius, 0, arcSweep, sweep,
// end.x, end.y ].join(" ");
// return d;
// }

