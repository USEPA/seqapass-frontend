console.log("heatmap.js loaded from static file");

	///////////
	//GLOBALS//
	///////////

var seqhm = new Object();  //SEQapass Heat Map global variable

//define arrays
seqhm.levelThreeRows = new Array();
seqhm.levelOneRows = new Array(); //needed for otholog info
seqhm.heatMapRowData = new Array();
seqhm.selectedHeatMapRowData = new Array();
seqhm.aminoAcidInfo = new Array();

	
	
	
	//variables to be set by java controls
	seqhm.showSusceptibility = true;
	seqhm.showSusceptibilityText = true;
	seqhm.showAlignmentHeatMap = true;
	seqhm.showAminoAcid = true;
	seqhm.showPosition = true;
	seqhm.showOrtholog = false;
	seqhm.showThreatened = false;
	seqhm.showEndangered = false;
	seqhm.showCommonModel = false;
	
	seqhm.orthosAvail = false;
	seqhm.endangeredsAvail = false;
	seqhm.threatenedsAvail = false;
	seqhm.modelsAvail = false;
	
	seqhm.showCommonName = true;
	
	seqhm.showFullReport = false;
	
	seqhm.margin = {top: 25, right: 25, bottom: 30, left: 25};
	seqhm.padding = 20;
	
	//css color codes
	seqhm.color = new Object();
	seqhm.color.totalMatch = "#345d96";
	seqhm.color.partialMatch = "#98afd2";
	seqhm.color.noMatch = "#face00";
	seqhm.color.susceptibleY = "#2eb82e";
	seqhm.color.susceptibleN = "#ff0000";
	seqhm.color.optionalSelection = "#c468e3";
	//seqhm.color.rowHeaderWInfo = "#fdfd9b";
	//seqhm.color.rowHeaderNoInfo = "#f0f0f0";
	
	seqhm.maxRenderSize = 2000000;



	
//This should be called when the heatmap page is first loaded or when level 3 data changes 
//	to load new level 3 JSON data
function loadHeatMap(){
	console.log("Inside loadHeatMap");
	loadData();
	
	loadDiv();
		
	console.log("seqhm.heatMapRowData has length = " + seqhm.heatMapRowData.length);
		
	reloadHeatMap();
	
	
//	setSelectedData();
//		
//	drawHeatMap();
		
}

function loadDiv(){
	
	// set initial dimensions and margins of the graph
	//width and height are updated later based on table size
	var w = 200,
		h = 200,
		border = 1,
		bordercolor="black";
	
	var	width = w - seqhm.margin.left - seqhm.margin.right,
	height = h - seqhm.margin.top - seqhm.margin.bottom;
	
	var svg = d3.select("#heatmapDiv")
	.append("svg")
	.attr("id","heatMapSVG")
	.attr("border", border)
	.append("g")
	.attr("transform",
		"translate(" + seqhm.margin.left + "," + seqhm.margin.top + ")");
	
	var svgBorderPath = svg.append("rect")
		.attr("id","svgBorder")
		.attr("x", -1*seqhm.margin.left)
		.attr("y", -1*seqhm.margin.top)
		//.attr("height", height + seqhm.margin.top + seqhm.margin.bottom)
		//.attr("width", width + seqhm.margin.left + seqhm.margin.right)
		.style("stroke", bordercolor)
		.style("fill", "none")
		.style("stroke-width", border);
	
	var forObj = svg.append("foreignObject")
		.attr("id","heatMapForObj")
		.attr("width",width)
		.attr("height",height)
		.append("xhtml:body");
		
	// create a tooltip
	var tooltip = svg
		.append("foreignObject")
		.attr("id","tipTableDiv")
		.attr("width", "200px")  //CWS
		.attr("height", "200px") //CWS
		.attr("overflow", "visible") //CWS
		.style("display", "none")
		.attr("class", "tooltip")
		.style("border", "solid")
		.style("border-width", "2px")
		.style("border-radius", "5px")
		.style("padding", "5px")
		.style("position", "absolute")
		.style("z-index","10")
		.append("xhtml:body");

//	var tooltip = d3.select("#heatmapDiv")
//		.append("div")
//		.attr("id","tipTableDiv")
//		.style("opacity", 0)
//		.attr("class", "tooltip")
//		.style("border", "solid")
//		.style("border-width", "2px")
//		.style("border-radius", "5px")
//		.style("padding", "5px")
//		.style("position", "absolute")
//		.style("z-index","10")

			
	var legendDiv = forObj.append("div").attr("id","legendDiv");
	var legend1Table = legendDiv.append("table").attr("class", "legend").attr("id","legend1");
	var legend1body = legend1Table.append("tbody").attr("id","legend1Body");
	var legend2Table = legendDiv.append("table").attr("class", "legend").attr("id","legend2");
	var legend2body = legend2Table.append("tbody").attr("id","legend2Body");
	
	var table = forObj.append("table").attr("id","heatMapTable");
	var header = table.append("thead").append("tr").attr("id","headerRow");
	var tablebody = table.append("tbody").attr("id","heatMapBody");
	
}

//This should be called when selected tax groups are changed for heatmap (that does not require
//	reloading level 3 data)
function reloadHeatMap(){
	console.log("Inside reloadHeatMap");
	setSelectedData();
	
	
	if (seqhm.selectedHeatMapRowData.length == 0){
		handleEmptyHeatmap();
		return;
	} else {
		drawHeatMap();
	}
	
}

//This be called when heatmap settings are changed that do not require reloading selected tax groups
// or level 3 data
function updateHeatMap(){
	
}

	
function setSelectedData(){
	
	seqhm.selectedHeatMapRowData.length = 0;  //clear array
	
	//later update  this to only use heatMapRowData with selected Tax groups
	//seqhm.selectedHeatMapRowData = seqhm.heatMapRowData;
	
	var taxGroupNames = new Array();
	var targetList = document.getElementById("levThreeVizForm:lev3TaxPick_target");
	var children = targetList.children;
	for (var i = 0; i < children.length; i++){
		//var taxGroup = children[i].value;
		console.log("You selected" + children[i].value);
		taxGroupNames.push(children[i].value);
	}
	
	//select all rows excluding queryspecies row
	for (var i = 0; i < taxGroupNames.length; i++){
		var selectedTaxName = taxGroupNames[i];
		for (var j = 0; j < seqhm.heatMapRowData.length; j++){
			var row = seqhm.heatMapRowData[j];
			if (row.taxonomyName == selectedTaxName && !row.querySpecies){
				seqhm.selectedHeatMapRowData.push(row);
			}
		}
		
	}
	
	//add query species row to front of array
	for (var j = 0; j < seqhm.heatMapRowData.length; j++){
		var row = seqhm.heatMapRowData[j];
		if (row.querySpecies){
			seqhm.selectedHeatMapRowData.unshift(row);
		}
	}

	
}
	
function handleEmptyHeatmap(){
	console.log("Need to implement handleEmptyHeatmap!!!");
}

function convertLevel3ToHeatMap(){
	if (seqhm.levelThreeRows.length == 0){
		return;
	}
	
	if (seqhm.heatMapRowData.length != 0){
		seqhm.heatMapRowData.length = 0;  //clear array
	}
	
	
	//var row = new heatMapRow(7460, "Honey bee", "Apis mellifera", true, "XP_006562363.1", "cytochrome P450 9e2", true, false, false, true);
	
	var userDefinedCount = 0;

	seqhm.levelThreeRows.forEach(function(d){
		var row = new heatMapRow(d.speciesTaxId, d.taxonomyName, d.commonName, d.scientificName, false, d.accession, d.proteinName, false, d.threatened, d.endangered, d.model, d.accession==seqhm.queryAccession);
		//handle user defined sequences (d.speciesTaxId == 0)
		//needed since taxId is used as row identifier
		if (row.taxId == 0){
			row.taxId = "userDefined" + userDefinedCount;
			userDefinedCount = userDefinedCount + 1;
		}
		if (d.threatened){
			seqhm.threatenedsAvail = true;
		}
		if (d.endangered){
			seqhm.endangeredsAvail = true;
		}
		if (d.model){
			seqhm.modelsAvail = true;
		}
		
		
		d.residueResultList.forEach(function(res){
			console.log("common name=" + d.commonName);
			console.log("res="+JSON.stringify(res));
			console.log("amino acid="+JSON.stringify(res.aminoAcid));
			if (typeof res.aminoAcid !== 'undefined'){ //undefined if no amino acid at that position, "-" in level three report
				var id = res.aminoAcid.id
				row.res.push(new residue(seqhm.aminoAcidInfo.find(aminoAcid => aminoAcid.id == id),res.position,res.directMatch,res.sideChainMatch,res.sizeMatch,res.totalMatch));
			} else {
				row.res.push(new residue(seqhm.aminoAcidInfo[22],"-",false,false,false,false));
			}	
		});
	
		//susceptible only if all residues are total match
		var susceptible = true;
		row.res.forEach(function(s){
			susceptible = susceptible && s.totalMatch;
		});
		row.susceptible = susceptible;
		
		//TODO:  need to finish levelThreeRow properties: 
		//ortholog -- from level 1 data
		//will probably need to load levelOneReportRows JSON and then lookup using speciesTaxId to find ortholog status
		var i;
		for (i=0; i < seqhm.levelOneRows.length; i++){
			var levOneRow = seqhm.levelOneRows[i];
			//TODO - FIX THIS FOR DUPLICATE TAXID
			if (levOneRow.accession == row.accession){
				row.ortholog = YN2Boolean(levOneRow.ortholog);
				//row.ortholog = levOneRow.ortholog;
				if (row.ortholog){
					seqhm.orthosAvail = true;
				}
				break;
			}
		}
		
		
		seqhm.heatMapRowData.push(row);
	});
	
	
	//enable/disable checkboxes based on data availability
//	disableBox3Checkbox([{
//		name : 'type',
//		value : 'ortho'
//	}, {
//		name : 'avail',
//		value : seqhm.orthosAvail
//	}]);
//	
//	disableBox3Checkbox([{
//		name : 'type',
//		value : 'endangered'
//	}, {
//		name : 'avail',
//		value : seqhm.endangeredsAvail
//	}]);
//	
//	disableBox3Checkbox([{
//		name : 'type',
//		value : 'threatened'
//	}, {
//		name : 'avail',
//		value : seqhm.threatenedsAvail
//	}]);
//	
//	disableBox3Checkbox([{
//		name : 'type',
//		value : 'model'
//	}, {
//		name : 'avail',
//		value : seqhm.modelsAvail
//	}]);
//	
//	if(!seqhm.orthosAvail){
//		var txt = document.getElementById("levThreeVizForm:noOrthoText");
//		txt.style.display = "unset";
//	}
//	if(!seqhm.endangeredsAvail){
//		var txt = document.getElementById("levThreeVizForm:noEndangeredText");
//		txt.style.display = "unset";
//	}
//	if(!seqhm.threatenedsAvail){
//		var txt = document.getElementById("levThreeVizForm:noThreatenedText");
//		txt.style.display = "unset";
//	}
//	if(!seqhm.modelsAvail){
//		var txt = document.getElementById("levThreeVizForm:noModelText");
//		txt.style.display = "unset";
//	}
	
}
	

function loadData(){
	
	var divDataHolder = null;
	//divDataHolder = document.getElementById("levelThreeReportJSON"); //for prototype only
	divDataHolder = document.getElementById("levThreeVizForm:levelThreeReportJSON");
	if (divDataHolder.innerHTML.length > 0) {
		console.log("parsing level 3 rows");
		seqhm.levelThreeRows = JSON.parse(divDataHolder.innerHTML);
		divDataHolder.innerHTML = ""; // Clear contents
		console.log("finished parsing level 3 rows");
	}
	
	divDataHolder = document.getElementById("levThreeVizForm:levelOneReportJSON");
	//divDataHolder = document.getElementById("levelOneReportJSON");
	if (divDataHolder.innerHTML.length > 0) {
		console.log("parsing level 1 rows");
		seqhm.levelOneRows = JSON.parse(divDataHolder.innerHTML);
		divDataHolder.innerHTML = ""; // Clear contents
		console.log("finished parsing level 1 rows");
	}
	
	seqhm.queryAccession = seqhm.levelThreeRows[0].accession;
	
	
	///BEGIN DATA TO LOAD FROM APP
	//eventually load these from json on page (similar to levelOneRows in boxplot)
	seqhm.aminoAcidInfo[0] = new aminoAcid('A','Alanine','Aliphatic','89.094');
	seqhm.aminoAcidInfo[1] = new aminoAcid('C','Cysteine','Sulfur-Containing','121.154');
	seqhm.aminoAcidInfo[2] = new aminoAcid('D','Aspartic Acid','Acidic','133.104');
	seqhm.aminoAcidInfo[3] = new aminoAcid('E','Glutamic Acid','Acidic','147.131');
	seqhm.aminoAcidInfo[4] = new aminoAcid('F','Phenylalanine','Aromatic','165.192');
	seqhm.aminoAcidInfo[5] = new aminoAcid('G','Glycine','Aliphatic','75.067');
	seqhm.aminoAcidInfo[6] = new aminoAcid('H','Histidine','Basic','155.156');
	seqhm.aminoAcidInfo[7] = new aminoAcid('I','Isoleucine','Aliphatic','131.175');
	seqhm.aminoAcidInfo[8] = new aminoAcid('K','Lysine','Basic','146.189');
	seqhm.aminoAcidInfo[9] = new aminoAcid('L','Leucine','Aliphatic','131.175');
	seqhm.aminoAcidInfo[10] = new aminoAcid('M','Methionine','Sulfur-Containing','149.208');
	seqhm.aminoAcidInfo[11] = new aminoAcid('N','Asparagine','Amidic','132.119');
	seqhm.aminoAcidInfo[12] = new aminoAcid('P','Proline','Aliphatic','115.132');
	seqhm.aminoAcidInfo[13] = new aminoAcid('Q','Glutamine','Amidic','146.146');
	seqhm.aminoAcidInfo[14] = new aminoAcid('R','Arginine','Basic','174.203');
	seqhm.aminoAcidInfo[15] = new aminoAcid('S','Serine','Hydroxylic','105.093');
	seqhm.aminoAcidInfo[16] = new aminoAcid('T','Threonine','Hydroxylic','119.119');
	seqhm.aminoAcidInfo[17] = new aminoAcid('U','Seleno-cysteine','Sulfur-Containing','116.064');
	seqhm.aminoAcidInfo[18] = new aminoAcid('V','Valine','Aliphatic','117.148');
	seqhm.aminoAcidInfo[19] = new aminoAcid('W','Tryptophan','Aromatic','204.228');
	seqhm.aminoAcidInfo[20] = new aminoAcid('X','Unknown','Unknown','-');
	seqhm.aminoAcidInfo[21] = new aminoAcid('Y','Tyrosine','Aromatic','181.191');
	seqhm.aminoAcidInfo[22] = new aminoAcid('-','-','-','-');
	
	convertLevel3ToHeatMap();
	

}

	
	
    
//BEGIN CREATE TABLE METHOD

function drawHeatMap(){
	
	if (seqhm.selectedHeatMapRowData.length == 0){
		console.log("No data available for heatmap");
		return;
	}

	var myArray = [];

	seqhm.selectedHeatMapRowData.forEach(function(d){
		var tmpArray = new Array();
		tmpArray.push(d.commonName);
		tmpArray.push(d.susceptible);
		d.res.forEach(function(d){
			tmpArray.push(d.pos + d.aminoAcid.id);
		});
		myArray.push(tmpArray);
	});
	
	
	setMinSize();  //sets width and height to minimum.  Is updated later based on data
	
	if (document.getElementById("heatMapSVG") == null){
		loadDiv();
	}
	
	var svg = d3.select("#heatmapDiv")
	var svgBorderPath = d3.select("#svgBorder");
	var forObj = d3.select("#heatMapForObj");
	var tooltip = d3.select("#tipTableDiv");
	var heatMapTable = d3.select("#heatMapTable");
	
	var header = d3.select("#headerRow");
	var heatMapTablebody = d3.select("#heatMapBody");
	var legendDiv = d3.select("#legendDiv");
	var legend1Table = d3.select("#legend1");
	//var legendTablebody = d3.select("#legendBody");
	var legend1body = d3.select("#legend1Body");
	var legend2Table = d3.select("#legend2");
	var legend2body = d3.select("#legend2Body");
	
	var headerArray = new Array();
	if (seqhm.showCommonName){
		headerArray.push("Common Name");
	} else {
		headerArray.push("Scientific Name");
	}
	if (seqhm.showSusceptibility){
		headerArray.push("Similar Susceptibility");
	}
	if (seqhm.showAlignmentHeatMap){
	var tmp = 0;
		seqhm.selectedHeatMapRowData[0].res.forEach(function(d){
			tmp = tmp + 1;
			headerArray.push("Amino Acid " + tmp);
			if (seqhm.showFullReport){
				headerArray.push("Side Chain " + tmp);
				headerArray.push("MW " + tmp);
				headerArray.push("Total Match " + tmp);
			}
		});
	}
	
	header.selectAll("th").remove();
    header
		.selectAll("th")
		.data(headerArray)
        .enter()
        .append("th")
        .text(function(d) { return d; });
    
    heatMapTablebody.selectAll("tr").remove();
    
    var rows = heatMapTablebody
        .selectAll("tr")
		.data(seqhm.selectedHeatMapRowData)
		.enter()
        .append("tr")
		.attr("id", function (d) { return d.accession; });
        // rows built using the nested array - now each row has its own array.
    cells = rows.selectAll("td")
        // each row has data associated; get it and enter it for the cells.
		.data(function(d,i) {
			var tmpArray = new Array();
			if (seqhm.showCommonName){
				tmpArray.push(d.commonName);
			} else {
				tmpArray.push(d.scientificName);
			}
			
			if (seqhm.showSusceptibility){
				if (seqhm.showSusceptibilityText){
					if(d.susceptible){
						tmpArray.push("Y");
					} else {
						tmpArray.push("N");
					}
				} else {
					tmpArray.push("");
				}
			}
			if (seqhm.showAlignmentHeatMap){
				d.res.forEach(function(d){
					if (seqhm.showAminoAcid && seqhm.showPosition) {
						tmpArray.push(d.pos + d.aminoAcid.id);
					} else if (seqhm.showAminoAcid){
						tmpArray.push(d.aminoAcid.id);
					} else if (seqhm.showPosition){
						tmpArray.push(d.pos);
					} else {
						tmpArray.push("");
					}
					if(seqhm.showFullReport){
						if (seqhm.showAminoAcid){
							tmpArray.push(d.aminoAcid.sideChain);
							tmpArray.push(d.aminoAcid.mw);
							tmpArray.push(boolean2YN(d.totalMatch));
						}else{
							tmpArray.push("");
							tmpArray.push("");
							tmpArray.push("");
						}
					}
				});
			}
			return tmpArray;
        })
        .enter()
        .append("td")
        .text(function(d) {
			return d;
        })
		.attr("id", function (d) {return d.accession;})
		.style("text-align","center")
		.style("background-color", getCellColor);
			
	cells = rows.selectAll("td")
        .data(function(d) {
			return d;
        })
		
	//LEGENDS
	var leg1Array = new Array();  //1st column of legend
	var leg2Array = new Array();  //2nd column of legend
	if (seqhm.showFullReport){
		leg1Array.push([" ","Match"]);
	} else {
		leg1Array.push([" ","Total Match"]);
		leg1Array.push([" ","Partial Match"]);
	}
	leg1Array.push([" ","Not a Match"]);
	if (seqhm.showSusceptibility){
		leg2Array.push([" ","Susceptible Yes"]);
		leg2Array.push([" ","Susceptible No"]);
	}
	if (seqhm.showOrtholog){
		leg2Array.push([" ","Ortholog Candidate"]);
	} else if (seqhm.showThreatened){
		leg2Array.push([" ","Threatened Species"]);
	} else if (seqhm.showEndangered){
		leg2Array.push([" ","Endangered Species"]);
	}else if (seqhm.showCommonModel){
		leg2Array.push([" ","Common Model Organism"]);
	}
	
	d3.select("#legend1Body").selectAll("tr").remove();
//    var rows = legendTablebody
	var rows = legend1body
        .selectAll("tr")
		.data(leg1Array)
        .enter()
        .append("tr")
    cells = rows.selectAll("td")
		.data(function(d) {
			return d;
		})
		.enter()
		.append("td")
		.text(function(d) {
			return d;
		})
		
	d3.select("#legend2Body").selectAll("tr").remove();
	if (seqhm.showSusceptibility || seqhm.showOrtholog || seqhm.showThreatened || seqhm.showEndangered || seqhm.showCommonModel){
		var rows = legend2body
        	.selectAll("tr")
        	.data(leg2Array)
        	.enter()
        	.append("tr")
        	cells = rows.selectAll("td")
        	.data(function(d) {
        		return d;
        	})
        	.enter()
        	.append("td")
        	.text(function(d) {
        		return d;
        	})
	}
			
	legend1Table.select("tr:nth-child(1)").select("td:nth-child(1)").style("background-color", seqhm.color.totalMatch);
	if (seqhm.showFullReport){
		legend1Table.select("tr:nth-child(2)").select("td:nth-child(1)").style("background-color", seqhm.color.noMatch);
		legend1Table.select("tr:nth-child(3)").style("display","none");
	} else {
		legend1Table.select("tr:nth-child(2)").select("td:nth-child(1)").style("background-color", seqhm.color.partialMatch);
		legend1Table.select("tr:nth-child(3)").style("display","");
		legend1Table.select("tr:nth-child(3)").select("td:nth-child(1)").style("background-color", seqhm.color.noMatch);
	}
	
	if (seqhm.showSusceptibility){
		//these will always be 1st and 2nd children
		legend2Table.select("tr:nth-child(1)").select("td:nth-child(1)").style("background-color", seqhm.color.susceptibleY);
		legend2Table.select("tr:nth-child(2)").select("td:nth-child(1)").style("background-color", seqhm.color.susceptibleN);
	}
	if (seqhm.showOrtholog || seqhm.showThreatened || seqhm.showEndangered || seqhm.showCommonModel){
		//this could be 1st or 3rd child
		var selector = "tr:nth-child(" + legend2body.node().rows.length + ")"
		legend2Table.select(selector).select("td:nth-child(1)").style("background-color", seqhm.color.optionalSelection);
	}
	
	
	
	resizeSVG();
	
	addEvents();

//END CREATE TABLE METHOD
}	

function addEvents(){
	var heatMapTablebody = d3.select("#heatMapBody");
	var rows = heatMapTablebody
        .selectAll("tr")
	var cells = rows.selectAll("td")
		.on("mouseover", mouseover)
		.on("mouseleave", mouseleave)
		.on("mousemove",mousemove)
}
	
	//FUNCTIONS
	
function getCellColor(d,i){
	var localheatMapRowData = seqhm.selectedHeatMapRowData;
	
	var theRow = localheatMapRowData.find(localheatMapRowData => localheatMapRowData.accession == d3.select(this.parentNode).attr("id"));
	
	console.log("i=" + i);
	if (i > 1){
		console.log("i>1");
		console.log("found row:" + theRow.accession);
		console.log("column i: " + i);
	}
	
	if (i==0){
		//always common name or scientific name column
		if ((seqhm.showOrtholog && theRow.ortholog) || (seqhm.showThreatened && theRow.threatened) || (seqhm.showEndangered && theRow.endangered) || (seqhm.showCommonModel && theRow.commonModel)){
			return seqhm.color.optionalSelection;
		} else {
			return "#f0f0f0"
		}
	} else if (seqhm.showSusceptibility && i==1){
		console.log("found row:" + theRow.accession);
//		//susceptibility cell if seqhm.showSusceptibility = true
		if (theRow.susceptible){
			return "#2eb82e"; 
		} else {
			return "#ff0000";
		}
	} else {
		if (!seqhm.showSusceptibility) {
			//This is needed for resIndex logic to work if susceptibility column is not shown
			i=i+1;
		}
		if (seqhm.showFullReport){
			var resIndex = Math.floor((i-2)/4);
			directMatch = theRow.res[resIndex].directMatch;
			sideChainMatch = theRow.res[resIndex].sideChainMatch;
			mwMatch = theRow.res[resIndex].mwMatch;
			totalMatch = theRow.res[resIndex].totalMatch;
			
			if ((i-2)%4 == 0){
				//amino acid column
				//if (directMatch && sideChainMatch && mwMatch && totalMatch){  //total match (as defined in PowerPoint slides)
				if (directMatch){
					return seqhm.color.totalMatch;
				} else {
					//Not a match (as defined in PowerPoint slides)
					return seqhm.color.noMatch;
				}
			} else if ((i-2)%4 == 1){
				//side chain column
				if (sideChainMatch){
					return seqhm.color.totalMatch;
				} else {
					return seqhm.color.noMatch;
				}
			} else if ((resIndex*4+4) == i){
				//mw column
				if (mwMatch){
					return seqhm.color.totalMatch;
				} else {
					return seqhm.color.noMatch;
				}
			} else if ((resIndex*4+5) == i){
				//total match column
				if (totalMatch){
					return seqhm.color.totalMatch;
				} else {
					return seqhm.color.noMatch;
				}
			} else {
				//error
				return "#FFFFFF";
			}
		} else {
			var resIndex = i-2;
		
			//amino acid cell
			directMatch = theRow.res[resIndex].directMatch;
			sideChainMatch = theRow.res[resIndex].sideChainMatch;
			mwMatch = theRow.res[resIndex].mwMatch;
			totalMatch = theRow.res[resIndex].totalMatch;
		
			console.log("&& Match:" + (directMatch && sideChainMatch && mwMatch && totalMatch));
			console.log("|| Match:" + (directMatch || sideChainMatch || mwMatch || totalMatch));
			if (directMatch && sideChainMatch && mwMatch && totalMatch){
	//			//total match (as defined in PowerPoint slides)
				return seqhm.color.totalMatch;
			} else if (directMatch || sideChainMatch || mwMatch || totalMatch){
	//			//Partial match (as defined in PowerPoint slides)
				return seqhm.color.partialMatch;
			} else {
	//			//Not a match (as defined in PowerPoint slides)
				return seqhm.color.noMatch;
			}
		}
	}
}


  // Three function that change the tooltip when user hover / move / leave a cell
function mouseover(d,i) {
	//var tooltip = d3.select("#heatmapDiv").select("#tipTableDiv");
	var toolTipForObj = d3.select("#heatMapSVG").select("#tipTableDiv");
	var tooltip = toolTipForObj.select('body');
	toolTipForObj
		.style("display", "")
	d3.select(this)
		.style("stroke", "black")
		.style("opacity", 0.8)
}

//function svgFromScreen(x,y){
//	//var svg = d3.select("#heatMapSVG");
//	var svg = document.getElementById("heatMapSVG");
//	var pt = svg.createSVGPoint();
//	pt.x = x;
//	pt.y = y;
//	return pt.matrixTransform(svg.getScreenCTM().inverse());
//}
//
//function screenFromSVG(x,y){
//	//var svg = d3.select("#heatMapSVG");
//	var svg = document.getElementById("heatMapSVG");
//	var pt = svg.createSVGPoint();
//	pt.x = x;
//	pt.y = y;
//	return pt.matrixTransform(svg.getScreenCTM());
//}

function mousemove(d,i) {
	var localheatMapRowData = seqhm.selectedHeatMapRowData
	var theRow = localheatMapRowData.find(localheatMapRowData => localheatMapRowData.accession == d3.select(this.parentNode).attr("id"));
	var toolTipForObj = d3.select("#heatMapSVG").select("#tipTableDiv");
	var pt;
	
	console.log("mousemove:" + d3.mouse(this));
	console.log("pageX: " + d3.event.pageX);
	console.log("pageY: " + d3.event.pageY);
	
	console.log("newMouse: " + d3.mouse(toolTipForObj.node()));
	
	var loc = d3.mouse(toolTipForObj.node());
	
	var tooltip = d3.select("#heatMapSVG").select("#tipTableDiv").select('body');
	tabulate(theRow,i, loc);
}
  
  //function mouseleave(d,i,r) {
function mouseleave(d,i) {
	var toolTipForObj = d3.select("#heatMapSVG").select("#tipTableDiv");
	var tooltip = toolTipForObj.select('body');
	toolTipForObj
		.style("display", "none")

	
	d3.select(this)
		.style("opacity", 1)
}
  
function getOrCreateTipTable(tooltip){
	var table = d3.select("#tipTable")
	
	if (!table.empty()){
		table.remove();
	}
	
	var table = tooltip.append("table")
	table
		.attr("id","tipTable")
		.style("border-collapse","collapse");
		
	var pTip = tooltip.select('p');
	if (!pTip.empty()){
		pTip.remove();
	}
		
	return table;
}	
  
//creates tooltip table
function tabulate(theRow, i, loc) {
	
	var retName;  //return value
	var toolTipForObj = d3.select("#heatMapSVG").select("#tipTableDiv");
	var tooltip = toolTipForObj.select('body');
	
	//first reset tooltipForObj size so that html tables sizes correctly
	toolTipForObj
			.attr("width", 1)
			.attr("height", 1);

	var table;
	
	
	if (i==0){
			
		console.log("pageX: " + d3.event.pageX);
		console.log("pageY: " + d3.event.pageY);
		
		table = getOrCreateTipTable(tooltip);
		
		var rowInfoArray = new Array();
		rowInfoArray.push(["NCBI Accession",theRow.accession]);
		rowInfoArray.push(["Protein Name",theRow.protein]);
		if (seqhm.showCommonName){
			rowInfoArray.push(["Scientific Name",theRow.scientificName]);
		} else {
			rowInfoArray.push(["Common Name",theRow.commonName]);
		}
		rowInfoArray.push(["Taxonomic Group", theRow.taxonomyName])
		var tablebody = table.append("tbody");
		var rows = tablebody
			.selectAll("tr")
			.data(rowInfoArray)
			.enter()
			.append("tr");
		cells = rows.selectAll("td")
			.data(function(d) {
				return d;
			})
			.enter()
			.append("td")
			.text(function(d) {
				return d;
			})
		var tipString = [];
		if (theRow.ortholog){
			tipString.push("Ortholog Candidate<br/>");
		}
		if (theRow.threatened){
			tipString.push("Threatened Species<br/>");
		}
		if (theRow.endangered){
			tipString.push("Endangered Species<br/>");
		}
		if (theRow.commonModel){
			tipString.push("Common Model Organism<br/>");
		}
		
		var tipPar;
		if (tipString.length > 0){
			tipPar = tooltip.append("p");  //.html(tipString);
			for (j = 0; j < tipString.length; j++){
				tipPar.append("span").html(tipString[j]);
			}
			
		}

//		if (seqhm.showOrtholog && theRow.ortholog){
//			for (j = 0; j < tipPar.node().childElementCount; j++){
//				var span = tipPar.node().children[j];
//				if (span.textContent == "Ortholog Candidate"){
//					span.setAttribute("style", "background-color:"+seqhm.color.optionalSelection);
//				}
//			}
//			
//		}
		if (typeof tipPar !== 'undefined'){
			for (j = 0; j < tipPar.node().childElementCount; j++){
				var span = tipPar.node().children[j];
				if ((span.textContent == "Ortholog Candidate" && seqhm.showOrtholog) ||
					(span.textContent == "Threatened Species" && seqhm.showThreatened) ||
					(span.textContent == "Endangered Species" && seqhm.showEndangered) ||
					(span.textContent == "Common Model Organism" && seqhm.showCommonModel)){
				
					span.setAttribute("style", "background-color:"+seqhm.color.optionalSelection);
				} 
			
			}
		}
		
		
		
		retName = table.name;
	}else if ((seqhm.showSusceptibility && i>1) || (!seqhm.showSusceptibility && i>0)){
		var index = i;
//		if (!seqhm.showSusceptibility){
//			index = index+1;  //this is needed to correct rowIndex when seqhm.showSusceptibility = false
//		}
//		var rowIndex = index-2;
		var rowIndex;
		
		//this is needed to correct rowIndex when seqhm.showSusceptibility = false
		if (seqhm.showSusceptibility){
			rowIndex = index-2;
		} else {
			rowIndex = index-1;
		}
		
		//get AA number 
		if (seqhm.showFullReport){
			rowIndex = (rowIndex)/4;
		}
		if (!seqhm.showFullReport || (seqhm.showFullReport && Number.isInteger(rowIndex))){
			var aa = theRow.res[rowIndex].aminoAcid
		
			table = getOrCreateTipTable(tooltip);
	
			var resArray = new Array();
			resArray.push(["Name",theRow.res[rowIndex].aminoAcid.name]);
			resArray.push(["Abv",theRow.res[rowIndex].aminoAcid.id]);
			resArray.push(["Side Chain",theRow.res[rowIndex].aminoAcid.sideChain]);
			resArray.push(["MW",theRow.res[rowIndex].aminoAcid.mw]);
			var tablebody = table.append("tbody");
			rows = tablebody
				.selectAll("tr")
				.data(resArray)
				.enter()
				.append("tr");
			cells = rows.selectAll("td")
				.data(function(d) {
					return d;
				})
				.enter()
				.append("td")
				.text(function(d) {
					return d;
				})
				

			
			retName = table.name;
		} else {
			toolTipForObj.style("display","none");
			retName = "div";
		}
	} else {
		toolTipForObj.style("display","none");
		retName = "div";
	}
	
	
	if ((!seqhm.showSusceptibility) || (seqhm.showSusceptibility && i!=1)){
		if (typeof table !== 'undefined'){
			resizeTooltipForObj(table);
		}
		positionToolTip(toolTipForObj, loc);
	}
	
	return retName;
	
	
	
}


function getZmVal(){
	var zmVal = Math.round(((window.outerWidth - 10)/window.innerWidth)*100)/100;
	
	return zmVal;
}

function resizeTooltipForObj(table){
	var toolTipForObj = d3.select("#heatMapSVG").select("#tipTableDiv");
	var otherText = toolTipForObj.select('body').select('p').node();
	
	var zmVal = getZmVal();
	
	var tableSize = table.node().getBoundingClientRect();
	
	
	//first set width to allow <p> to expand
	var tableWidth = tableSize.width/zmVal + 2*seqhm.padding;
	toolTipForObj
	.attr("width", tableWidth);
	
	//then get height
	var otherHeight = 0;
	if (otherText != null){
		otherHeight = otherText.getBoundingClientRect().height;
	}
	var tableHeight = (tableSize.height + otherHeight)/zmVal + 2*seqhm.padding;
		
	toolTipForObj
		.attr("height", tableHeight);
}


function positionToolTip(tooltipForObj, loc){
		
		var xloc = loc[0] + seqhm.margin.left;
		var yloc = loc[1] + seqhm.margin.top;
		
		
		var svg = document.getElementById('heatMapSVG');
		var tip = document.getElementById('tipTableDiv');
		
		var tipRect = tip.getBoundingClientRect();
		var svgRect = svg.getBoundingClientRect();
		
		var width = svgRect.width;
		var height = svgRect.height;
		var updateSVGSize = false;
		if (tipRect.left + tipRect.width > svgRect.width){
			 width = tipRect.left + tipRect.width;
			 updateSVGSize = true;
		}
		
		if (tipRect.top + tipRect.height > svgRect.height){
			height = tipRect.top + tipRect.height;
			updateSVGSize = true;
		}
		
		if (updateSVGSize){
			console.log("resize SVG with width:" + width + " & height:" + height);
			var zmVal = getZmVal();
			setSVGSize(width/zmVal,height/zmVal);
		}
		
		tooltipForObj
			.attr("x",xloc)
			.attr("y",yloc);
}
  
function resizeSVG(){
	console.log("Inside resizeSVG");
	  
	var mainTable = document.getElementById("heatMapTable");
	var legendTable = document.getElementById("legendDiv");
	
	var zmVal = getZmVal();
	
	var width = mainTable.getBoundingClientRect().width;
	var height = mainTable.getBoundingClientRect().height;
	console.log("found height:" + height + " and width:" + width); 
	  
	//find width for foreign object
	height = height + legendTable.getBoundingClientRect().height;
	height = height + seqhm.margin.top + seqhm.margin.bottom; 
	width = width + seqhm.margin.left + seqhm.margin.right;
	
	height = height / zmVal;
	width = width / zmVal;
	
	var theForObj = document.getElementById("heatMapForObj");
	theForObj.setAttribute("width",width + "px");
	theForObj.setAttribute("height",height + "px");
	  
	//add padding for svg and set width/height
	width = width + 2*seqhm.padding;
	height = height + 2*seqhm.padding;
	
	setSVGSize(width,height);
	
	
//	var theSVG = document.getElementById("heatMapSVG");
//	theSVG.setAttribute("width",width);
//	theSVG.setAttribute("height",height);
//	
//	//set viewbox to full extent
//	theSVG.viewBox.baseVal.width = width;
//	theSVG.viewBox.baseVal.height = height;
//	//theSVG.width.baseVal.value = width;
//	//theSVG.height.baseVal.value = height;
//	  
	//set svg border size
	var theBorder = document.getElementById("svgBorder");
	theBorder.setAttribute("width",width);
	theBorder.setAttribute("height",height);
	  
}

function setSVGSize(width, height){
	var theSVG = document.getElementById("heatMapSVG");
	theSVG.setAttribute("width",width);
	theSVG.setAttribute("height",height);
	
	//set viewbox to full extent
//	theSVG.viewBox.baseVal.width = width;
//	theSVG.viewBox.baseVal.height = height;
	theSVG.width.baseVal.value = width;
	theSVG.height.baseVal.value = height;
	  
	//set svg border size
	//var theBorder = document.getElementById("svgBorder");
	//theBorder.setAttribute("width",width);
	//theBorder.setAttribute("height",height);
}

function boolean2YN(value){
	if (value){
		return "Y";
	} else {
		return "N";
	}
}

function YN2Boolean(value){
	if (value.toUpperCase()==="Y"){
		return true;
	} else {
		return false;
	}
}

function boolean2String(value){
	if (value){
		return "true";
	} else {
		return "false";
	}
}


function updateHMSpeciesNameType(){
	var nameTypeRadioBtn = document.getElementById("levThreeVizForm:nameType:0");
	if (nameTypeRadioBtn.checked.valueOf()){
		seqhm.showCommonName = true;
	}else {
		seqhm.showCommonName = false;
	}
	
	drawHeatMap();
}

function updateHMReportType(){
	console.log("inside updateHMReportType");
	var reportTypeRadioBtn = document.getElementById("levThreeVizForm:reportType:0");
	if (reportTypeRadioBtn.checked.valueOf()){
		seqhm.showFullReport = false;
	}else {
		seqhm.showFullReport = true;
	}
	
	drawHeatMap();
}

//sets width and height to minimum.  
//To be updated later based on data
function setMinSize(){
	d3.select("#heatMapForObj").attr("width",200);
	d3.select("#heatMapForObj").attr("height",200);
}

function updateOptionalSelections(){
	console.log("updateOptionalSelections");
	var ortho = document.getElementById('levThreeVizForm:optionalSelectionRadio:0');
	var threat = document.getElementById('levThreeVizForm:optionalSelectionRadio:1');
	var endanger = document.getElementById('levThreeVizForm:optionalSelectionRadio:2');
	var common = document.getElementById('levThreeVizForm:optionalSelectionRadio:3');
	
	seqhm.showOrtholog = ortho.checked;
	seqhm.showThreatened = threat.checked;
	seqhm.showEndangered = endanger.checked;
	seqhm.showCommonModel = common.checked;
	
//	seqhm.showOrtholog = PF('orthoCheck3').isChecked();
//	seqhm.showThreatened = PF('threatenedCheck3').isChecked();
//	seqhm.showEndangered = PF('endangeredCheck3').isChecked();
//	seqhm.showCommonModel = PF('modelCheck3').isChecked();
//	
	drawHeatMap();
}

function updateHeatMapSettings(){
	seqhm.showSusceptibility = PF('susceptibleCheck3').isChecked();
	seqhm.showSusceptibilityText = PF('susceptibleTextCheck3').isChecked();
	seqhm.showAlignmentHeatMap = PF('alignCheck3').isChecked();
	seqhm.showAminoAcid = PF('aminoAcidCheck3').isChecked();
	seqhm.showPosition = PF('positionCheck3').isChecked();
	
	drawHeatMap();
}


function getHeatMapSVGSize(elementId){
	var element = document.getElementById(elementId);
//	var width = element.viewBox.baseVal.width;
//	var height = element.viewBox.baseVal.height;
	var width = element.width.baseVal.value;
	var height = element.height.baseVal.value;
	
	updateHeatMapBeanSizeRC([{
		name : 'width',
		value : width
	}, {
		name : 'height',
		value : height
	}]);
	
}



function constructHeatmapImage(elementId, imageType) {
	console.log("inside getHeatMapSVGSource");
	console.log("with elementId: " + elementId);
	
	disableHeatMapDownloadBtn();
	
    var svgType = false;
    var pngType = false;
    var jpgType = false;
	
	if (imageType === "svg"){
		svgType = true;
	} else if (imageType === "png"){
		pngType = true;
	} else if (imageType === "jpg"){
		jpgType = true;
	} else {
		svgType = document.getElementById('levThreeVizForm:imageType:0').checked;
		pngType = document.getElementById('levThreeVizForm:imageType:1').checked;
		jpgType = document.getElementById('levThreeVizForm:imageType:2').checked;
	}
	
	if (svgType){
		imageType = "svg";
	} else if (pngType){
		imageType = "png";
	} else if (jpgType){
		imageType = "jpeg";
	} else {
		console.log("Could not find image type in constructHeatmapImage");
	}
	
//	var svgType = document.getElementById('levThreeVizForm:imageType:0').checked;
//	var pngType = document.getElementById('levThreeVizForm:imageType:1').checked;
//	var jpgType = document.getElementById('levThreeVizForm:imageType:2').checked;
	
	
	
	var reader = new FileReader();
	
	var svg = document.getElementById(elementId);
	  
	var canvas = document.getElementById('svgCanvas');
	
//	var svgElement = document.getElementById('heatMapSVG');
	
	var width = 0;
	var height = 0;
	if (svgType){
//		width = svg.viewBox.baseVal.width;
//		height = svg.viewBox.baseVal.height;
		width = svg.width.baseVal.value;
		height = svg.height.baseVal.value;
	} else {
		var widthStr = document.getElementById('levThreeVizForm:imageWidth_input').value;
		var heightStr = document.getElementById('levThreeVizForm:imageHeight_input').value;
		width = parseInt(widthStr.replace(",",""));
		height = parseInt(heightStr.replace(",",""));
	}
	
	console.log("found width: " + width);
	console.log("found height: " + height);
	
	canvas.width = width;
	canvas.height = height;
	var data = new XMLSerializer().serializeToString(svg);
	
	var theCSS = retrieveHeatMapCSS('viz.css');
	//insert css into svg
	data = data.replace("</svg>","<defs id=\"cssDefs\"><style><![CDATA[" + theCSS + "]]></style></defs></svg>");
	
	
	if (svgType){
	
		//insert d3 source
		data = data.replace("</svg>", "<script xlink:actuate=\"onLoad\" xlink:type=\"simple\" xlink:show=\"other\" type=\"text/javascript\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"https://d3js.org/d3.v5.min.js\"/></svg>");
	
		//insert javascript and data
		console.log("b4 retrieveJavaScript");
		//var theJS = retrieveJavaScript();
	
		var theJS = retrieveHeatMapJavaScript();

		data = data.replace("</svg>", "<script xlink:actuate=\"onLoad\" xlink:type=\"simple\" xlink:show=\"other\" type=\"text/javascript\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><![CDATA[" + theJS + "]]></" + "script></svg>");
	}
	
	
	var img = new Image();
	var blob = new Blob([data], { type: 'image/svg+xml' });
	
	img.onload = function () {
		var ctx = canvas.getContext('2d');
		ctx.drawImage(img, 0, 0, width, height);
		if (svgType){
			var uri = data;
			//var uri = canvas.toDataURL('image/svg').replace('image/svg', 'octet/stream');
		} else if (pngType){
			var uri = canvas.toDataURL('image/png').replace('image/png', 'octet/stream');
		} else if (jpgType){
			ctx.globalCompositeOperation='destination-over';
			ctx.fillStyle = "white";
			ctx.fillRect(0,0,canvas.width,canvas.height);
			var uri = canvas.toDataURL('image/jpeg').replace('image/jpeg', 'octet/stream');
		}
		theSVGb64 = uri;
		
		if(pngType || jpgType){
			if (theSVGb64.length > seqhm.maxRenderSize || theSVGb64 === "data:,"){
				var ratio = Math.ceil(theSVGb64.length/seqhm.maxRenderSize *10)/10;
				console.log("image is larger than 2MB");
				alert("Heat Map too large to render - consider fewer species/amino acid comparisons, selecting a different image type, or reducing resolution. Expected file is " + ratio + "x maximum.");
				return;
			}
		}
		
		getHeatMapSVGSourceRC([ {
			name : 'theSVG',
			value : theSVGb64
		}, {
			name : 'imageType',
			value : imageType
		}]);
	
	};
	reader.onloadend = function(){
		img.src = reader.result;
	};	
	reader.readAsDataURL(blob);
	
	
}

function enableHeatMapDownloadBtn(){
	button = document.getElementById('levThreeVizForm:triggerDownload');
	button.disabled = false;
	button.style.opacity = 1.0;
}

function disableHeatMapDownloadBtn(){
	button = document.getElementById('levThreeVizForm:triggerDownload');
	button.disabled = true;
	button.style.opacity = 0.3;
}


function constructHeatmapImageForDS(elementId) {
	console.log("inside getHeatMapSVGSource");
	console.log("with elementId: " + elementId);
	
//    var svgType = false;
//    var pngType = false;
//    var jpgType = false;
	
//	if (imageType === "svg"){
//		svgType = true;
//	} else if (imageType === "png"){
//		pngType = true;
//	} else if (imageType === "jpg"){
//		jpgType = true;
//	} else {
//		svgType = document.getElementById('levThreeVizForm:imageType:0').checked;
//		pngType = document.getElementById('levThreeVizForm:imageType:1').checked;
//		jpgType = document.getElementById('levThreeVizForm:imageType:2').checked;
//	}
	
//	var svgType = document.getElementById('levThreeVizForm:imageType:0').checked;
//	var pngType = document.getElementById('levThreeVizForm:imageType:1').checked;
//	var jpgType = document.getElementById('levThreeVizForm:imageType:2').checked;
	
	
	
	var reader = new FileReader();
	
	var svg = document.getElementById(elementId);
	  
	var canvas = document.getElementById('svgCanvas');
	
//	var svgElement = document.getElementById('heatMapSVG');
//	var width = svg.viewBox.baseVal.width;
//	var height = svg.viewBox.baseVal.height; 
	var width = svg.width.baseVal.value;
	var height = svg.height.baseVal.value; 
	
	console.log("found width: " + width);
	console.log("found height: " + height);
	
	canvas.width = width;
	canvas.height = height;
	var data = new XMLSerializer().serializeToString(svg);
	
	var theCSS = retrieveHeatMapCSS('viz.css');
	//insert css into svg
	data = data.replace("</svg>","<defs id=\"cssDefs\"><style><![CDATA[" + theCSS + "]]></style></defs></svg>");
	
	var theSVGb64;
	var img = new Image();
	var blob = new Blob([data], { type: 'image/svg+xml' });
	
	img.onload = function () {
		var ctx = canvas.getContext('2d');
		ctx.drawImage(img, 0, 0, width, height);
//		if (svgType){
//			var uri = data;
//			//var uri = canvas.toDataURL('image/svg').replace('image/svg', 'octet/stream');
//		} else if (pngType){
			var uri = canvas.toDataURL('image/png').replace('image/png', 'octet/stream');
//		} else if (jpgType){
//			var uri = canvas.toDataURL('image/jpeg').replace('image/jpeg', 'octet/stream');
//		}
		theSVGb64 = uri;
		//console.log("theSVGb64: \n")
		//console.log(theSVGb64);
		
		if (theSVGb64.length > seqhm.maxRenderSize || theSVGb64 === "data:,"){
			var ratio = Math.ceil(theSVGb64.length/seqhm.maxRenderSize *10)/10;
			console.log("image is larger than 2MB");
			alert("Heat Map too large to push - consider fewer species/amino acid comparisons.  Expected size is " + ratio + "x maximum.");
		}
		
		
		pushDSHeatmap([{
			name : 'theSVG',
			value : theSVGb64
		}]);
		
//		getHeatMapSVGSourceRC([ {
//			name : 'theSVG',
//			value : theSVGb64
//		}]);
	
	};
	reader.onloadend = function(){
		img.src = reader.result;
	};	
	reader.readAsDataURL(blob);
	
	return theSVGb64;
}


function retrieveHeatMapCSS(sheetName) {

	var rules = getAllHeatMapCSSRules(sheetName);
	var cssString = "";
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].includes('#heatmapDiv')){
			cssString += rules[i].replace(/#heatmapDiv /g,'');
		}
	}

	return cssString;
}

function getAllHeatMapCSSRules(sheetHrefRegexMatch) {
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

//function retrieveHeatMapJavaScript() {
//	
////	console.log("svg: " + svg);
////	var sqgString = JSON.stringify(sqg).replace(/'/g, "\\'");
////	var jsString = "var theJSON = '" + sqgString + "';";
////	jsString += "var sqg = JSON.parse(theJSON);";
//
//	var functionString = getStandaloneFunctions();
//	
//	console.log("functionString: " + functionString);
//	
////	jsString += functionString;
//	var jsString = functionString;
//
////	var eventString = getStandaloneEvents.toString() + ";";
////	jsString += eventString;
////
////	// add call to function to add events
////	jsString += "var levelNumber = " + levelNumber + ";";
////	jsString += "getStandaloneEvents(levelNumber);";
////	jsString += "appendPopupG(levelNumber);";
//
//	return jsString;
//}

function retrieveHeatMapJavaScript() {
	
	
	var functionString = addEvents.toString();
	functionString += mouseover.toString();
	functionString += mouseleave.toString();
	functionString += mousemove.toString();
	functionString += getCellColor.toString();
//	functionString += svgFromScreen.toString();
	functionString += tabulate.toString();
	functionString += positionToolTip.toString();
	functionString += resizeTooltipForObj.toString();
	functionString += getOrCreateTipTable.toString();
	functionString += heatMapRow.toString();
	functionString += aminoAcid.toString();
	functionString += residue.toString();
	functionString += levelThreeRow.toString();
	functionString += levelOneRow.toString();
	functionString += setSVGSize.toString();
	functionString += getZmVal.toString();
	
	var selfExecuteStr = " (function() { seqhm = new Object(); addEvents(); addData(); })();"
		
	functionString += selfExecuteStr;
	
	var addDataStr = "function addData(){ seqhm.levelThreeRows = new Array(); seqhm.levelOneRows = new Array(); seqhm.heatMapRowData = new Array(); seqhm.selectedHeatMapRowData = new Array(); seqhm.aminoAcidInfo = new Array();";
	addDataStr += "seqhm.showSusceptibility = " + boolean2String(seqhm.showSusceptibility) + ";";
	addDataStr += "seqhm.showSusceptibilityText = " + boolean2String(seqhm.showSusceptibilityText) + ";";
	addDataStr += "seqhm.showAlignmentHeatMap = " + boolean2String(seqhm.showAlignmentHeatMap) + ";";
	addDataStr += "seqhm.showAminoAcid = " + boolean2String(seqhm.showAminoAcid) + ";";
	addDataStr += "seqhm.showPosition = " + boolean2String(seqhm.showPosition) + ";";
	addDataStr += "seqhm.showOrtholog = " + boolean2String(seqhm.showOrtholog) + ";";
	addDataStr += "seqhm.showThreatened = " + boolean2String(seqhm.showThreatened) + ";";
	addDataStr += "seqhm.showEndangered = " + boolean2String(seqhm.showEndangered) + ";";
	addDataStr += "seqhm.showCommonModel = " + boolean2String(seqhm.showCommonModel) + ";";
	
	addDataStr += "seqhm.orthosAvail = " + boolean2String(seqhm.orthosAvail) + ";";
	addDataStr += "seqhm.endangeredsAvail = " + boolean2String(seqhm.endangeredsAvail) + ";";
	addDataStr += "seqhm.threatenedsAvail = " + boolean2String(seqhm.threatenedsAvail) + ";";
	addDataStr += "seqhm.modelsAvail = " + boolean2String(seqhm.modelsAvail) + ";";
	
	addDataStr += "seqhm.showCommonName = " + boolean2String(seqhm.showCommonName) + ";";
	
	addDataStr += "seqhm.showFullReport = " + boolean2String(seqhm.showFullReport) + ";";
	
	addDataStr += "seqhm.margin = {top: 25, right: 25, bottom: 30, left: 25};";
	addDataStr += "seqhm.padding = 20" + ";";
	
	addDataStr += "seqhm.color = new Object(); seqhm.color.totalMatch = \"#345d96\"; seqhm.color.partialMatch = \"#98afd2\"; seqhm.color.noMatch = \"#face00\"; seqhm.color.susceptibleY = \"#2eb82e\"; seqhm.color.susceptibleN = \"#ff0000\"; ";
	
   
   addDataStr += "seqhm.selectedHeatMapRowData = " 
   console.log("JSON to string: " + JSON.toString(seqhm.selectedHeatMapRowData));
   addDataStr += JSON.stringify(seqhm.selectedHeatMapRowData);
   addDataStr += ";}"
   
   functionString += addDataStr

	return functionString;

}


function getD3Source(){
	divDataHolder = document.getElementById("levThreeVizForm:d3Source");
	if (divDataHolder.innerHTML.length > 0) {
		console.log("parsing D3 source");
		var src = divDataHolder.innerHTML;
		console.log("D3 src = " + src);
		console.log("finished parsing D3 source");
	}
	return src;
	
}
  
  //CLASSES
  
 function heatMapRow(taxId, taxonomyName, commonName, scientificName, susceptible, accession, protein, ortholog, threatened, endangered, commonModel, querySpecies){
	this.taxId = taxId;
	this.taxonomyName = taxonomyName;
	this.commonName = commonName;
	this.scientificName = scientificName;
	this.susceptible = susceptible;
	this.accession = accession;
	this.protein = protein;
	this.ortholog = ortholog;
	this.threatened = threatened;
	this.endangered = endangered;
	this.commonModel = commonModel;
	this.res = new Array();
	this.querySpecies= querySpecies;
}
	
	
function aminoAcid(id,name,sideChain,mw){
	this.id = id;
	this.name = name;
	this.sideChain = sideChain;
	this.mw = mw;
}
	
function residue(aminoAcid, pos, directMatch, sideChainMatch, mwMatch, totalMatch){
	this.aminoAcid = aminoAcid;
	this.pos = pos;
	this.directMatch = directMatch;
	this.sideChainMatch = sideChainMatch;
	this.mwMatch = mwMatch;
	this.totalMatch = totalMatch;
}

function levelThreeRow() {
	this.accession= "1JLY_A";
	this.proteinCount=224;
	this.speciesTaxId=3567;
	this.taxonomyName="Caryophyllales";
	this.scientificName="Amaranthus caudatus";
	this.commonName="Amaranth";
	this.proteinName="Chain A, Crystal Structure Of Amaranthus Caudatus Agglutinin";
	this.endDate=1562692851;	
	this.residueResultList= "";//[{"position":3,"directMatch":true,"sideChainMatch":true,"sizeMatch":true,"totalMatch":true,"aminoAcid":{"id":"G","name":"Glycine","sideChain":"Aliphatic","size":75.067}},{"position":18,"directMatch":true,"sideChainMatch":true,"sizeMatch":true,"totalMatch":true,"aminoAcid":{"id":"Y","name":"Tyrosine","sideChain":"Aromatic","size":181.191}},{"position":38,"directMatch":true,"sideChainMatch":true,"sizeMatch":true,"totalMatch":true,"aminoAcid":{"id":"K","name":"Lysine","sideChain":"Basic","size":146.189}}];
	this.flatResidues="";//{"Position 1":"3","Amino Acid 1":"G","Direct Match 1":"Y","Side Chain 1":"Aliphatic","Side Chain Match 1":"Y","MW 1":"75.067","MW Match 1":"Y","Total Match 1":"Y","Position 2":"18","Amino Acid 2":"Y","Direct Match 2":"Y","Side Chain 2":"Aromatic","Side Chain Match 2":"Y","MW 2":"181.191","MW Match 2":"Y","Total Match 2":"Y","Position 3":"38","Amino Acid 3":"K","Direct Match 3":"Y","Side Chain 3":"Basic","Side Chain Match 3":"Y","MW 3":"146.189","MW Match 3":"Y","Total Match 3":"Y"};
	this.primaryFlatResidues=""//{"Position 1":"3","Amino Acid 1":"G","Total Match 1":"Y","Position 2":"18","Amino Acid 2":"Y","Total Match 2":"Y","Position 3":"38","Amino Acid 3":"K","Total Match 3":"Y"};
	this.updateVersion=4;
	this.susceptible="TBD";
	this.jobName="test";
	this.endangered=false;
	this.threatened=false;
	this.model=false;
	this.ecotox=false;
}	

function levelOneRow() {
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
