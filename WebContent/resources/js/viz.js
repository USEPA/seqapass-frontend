//viz.js
var vizNamespace = {};
console.log("Viz.js loaded from static file");

vizNamespace.childTabs = [ "_viz1", "_viz2", "_viz3", "_cut1", "_cut2" ];

function OpenVizInBrowserTab(level) {
	if (level === 1) {
		vizNamespace.viz1Window = window.open(
				"levelOneVisualization.xhtml", '_viz1');
		vizNamespace.viz1Window.focus();
	} else if (level === 2) {
		vizNamespace.viz2Window = window.open(
				"levelTwoVisualization.xhtml", '_viz2');
		vizNamespace.viz2Window.focus();
	} else if (level === 3) {
		vizNamespace.viz3Window = window.open(
				"levelThreeVisualization.xhtml", '_viz3');
		vizNamespace.viz3Window.focus();
	}
}

function updateVizWindows(level) {
	if (level === 1) {
		if (typeof vizNamespace.viz1Window !== 'undefined') {
			if (!vizNamespace.viz1Window.closed) {
				vizNamespace.viz1Window.location.reload();
			}
			if (typeof vizNamespace.viz2Window !== 'undefined') {
				if (!vizNamespace.viz2Window.closed) {
					vizNamespace.viz2Window.close();
				}
			}
			if (typeof vizNamespace.viz3Window !== 'undefined') {
				if (!vizNamespace.viz3Window.closed) {
					vizNamespace.viz3Window.close();
				}
			}
		}
	} else if (level === 2) {
		if (typeof vizNamespace.viz2Window !== 'undefined') {
			if (!vizNamespace.viz2Window.closed) {
				vizNamespace.viz2Window.location.reload();
			}
		}
	} else if (level === 3) {
		if (typeof vizNamespace.viz3Window !== 'undefined') {
			if (!vizNamespace.viz3Window.closed) {
				vizNamespace.viz3Window.location.reload();
			}
		}
	}
}

function OpenCutoffInBrowserTab(level, switchToUserDefined) {
	var interval;
	var attempts = 0;
	var maxAttempts = 6;
	var intervalWait = 500;
	if (level === 1) {
		if (typeof vizNamespace.cut1Window !== 'undefined'
				&& vizNamespace.cut1Window !== null) {
			if (!vizNamespace.cut1Window.closed) {
				vizNamespace.cut1Window.location.reload();
				vizNamespace.cut1Window.focus();
			} else {
				vizNamespace.cut1Window = window.open(
						"cutoff.xhtml", '_cut1');
				if (switchToUserDefined) {
					interval = setInterval(checkForCutoffAvailability,
							intervalWait);
				}
				vizNamespace.cut1Window.focus();
			}
		} else {
			vizNamespace.cut1Window = window.open(
					"cutoff.xhtml", '_cut1');
			vizNamespace.cut1Window.focus();
		}
	} else if (level === 2) {
		vizNamespace.cut2Window = window.open(
				"cutoff2.xhtml", '_cut2');
		if (switchToUserDefined) {
			interval = setInterval(checkForCutoffAvailability, intervalWait);
		}
		vizNamespace.cut2Window.focus();
	}

	function checkForCutoffAvailability() {
		attempts++;
		if (attempts === maxAttempts) {
			clearInterval(interval);
		}

		var menuExists;
		if (level == 1) {
			menuExists = vizNamespace.cut1Window.document
					.getElementById("cutoffForm:selectCutoffMenu");
		} else if (level == 2) {
			menuExists = vizNamespace.cut2Window.document
					.getElementById("cutoffForm2:selectCutoffMenu");
		}

		if (menuExists) {
			if (level == 1) {
				vizNamespace.cut1Window.PF('cutoff1Menu').selectValue('3');
				clearInterval(interval);
			} else if (level == 2) {
				vizNamespace.cut2Window.PF('cutoff2Menu').selectValue('3');
				clearInterval(interval);
			}
		}
	}
}

function updateCutWindows(level) {
	if (level === 1) {
		if (typeof vizNamespace.cut1Window !== 'undefined') {
			if (!vizNamespace.cut1Window.closed) {
				vizNamespace.cut1Window.close();
				//vizNamespace.cut1Window.location.reload();
			}
			if (typeof vizNamespace.cut2Window !== 'undefined') {
				if (!vizNamespace.cut2Window.closed) {
					vizNamespace.cut2Window.close();
				}
			}
		}
	} else if (level === 2) {
		if (typeof vizNamespace.cut2Window !== 'undefined') {
			if (!vizNamespace.cut2Window.closed) {
				vizNamespace.cut2Window.close();
				//vizNamespace.cut2Window.location.reload();
			}
		}
	}
}

//function openUserDefinedCutoff(level) {
//
//	if (document.getElementById('tabView:reportForm:cutoffRadio:2').checked) {
//		OpenCutoffInBrowserTab(level, true);
//	}
//}

function updateDSReportFromViz(){
	window.opener.document.getElementById('tabView:reportForm:levelOneButton').click();
}

function updateFromCutoffChange(level) {
	var checkedInt;
	if (level === 1) {
		checkedInt = document
				.getElementById('cutoffForm:selectCutoffMenu_input').selectedIndex;
	} else if (level === 2) {
		checkedInt = document
				.getElementById('cutoffForm2:selectCutoffMenu_input').selectedIndex;
	}
	//var componentId = 'tabView:reportForm:cutoffRadio:' + checkedInt
	//window.opener.document.getElementById(componentId).checked = true;

	if (level === 1) {
		window.opener.document.getElementById(
				'tabView:reportForm:levelOneButton').click();

		if (vizNamespace.viz1Window !== undefined) {
			var boxButton = vizNamespace.viz1Window.document
					.getElementById('levOneVizForm:boxPlotButton');
			if (boxButton !== null) {
				var resetBoxPlot = true;
				boxButton.click();
			}
		} else {
			var boxButton = document
					.getElementById('levOneVizForm:boxPlotButton');
			if (boxButton !== null) {
				var resetBoxPlot = true;
				boxButton.click();
			}
		}
	} else if (level === 2) {
		window.opener.document.getElementById(
				'tabView:reportForm:levelTwoButton').click();

		if (vizNamespace.viz2Window !== undefined) {
			var boxButton = vizNamespace.viz2Window.document
					.getElementById('levTwoVizForm:boxPlotButton');
			if (boxButton !== null) {
				boxButton.click();
			}
		} else {
			var boxButton = document
					.getElementById('levTwoVizForm:boxPlotButton');
			if (boxButton !== null) {
				boxButton.click();
			}
		}
	}

	this.close();
}

function closeCutWin(level) {
	if (level === 1) {
		console.log("level 1");
		if (typeof vizNamespace.cut1Window !== 'undefined') {
			if (!vizNamespace.cut1Window.closed) {
				vizNamespace.cut1Window.close();
			}
			if (typeof vizNamespace.cut2Window !== 'undefined') {
				if (!vizNamespace.cut2Window.closed) {
					vizNamespace.cut2Window.close();
				}
			}
		}
	} else if (level === 2) {
		if (typeof vizNamespace.cut2Window !== 'undefined') {
			if (!vizNamespace.cut2Window.closed) {
				vizNamespace.cut2Window.close();
			}
		}
	}
}

function transition2MainTab() {

	if (vizNamespace.childTabs.includes(window.name)) {
		window.opener.location.replace(window.location.href);
		window.close();
	} else {
		window.name = "seqMain";
	}
	// if (window.name === '_viz1' || window.name === '_viz2' || window.name ===
	// '_cut1' || window.name === '_cut2') {
	// window.opener.location.replace(window.location.href);
	// window.close();
	// }
}

/*******************************************************************************
 * checks whether child window is still open by checking variables in local
 * storage (variable name = window.name). If open, it retrieves handle and
 * closes
 * 
 * @returns
 */
function closeAllChildTabs() {
	vizNamespace.childTabs.forEach(function(entry) {
		var lastClick = localStorage.getItem("lastClick");
		var winOpen = localStorage.getItem(entry);
		if (winOpen !== null && winOpen === "true") {
			console.log("Closing " + entry);
			var childWindow = window.open('', entry);
			if (childWindow != null){
				childWindow.close();
			}
			if (lastClick === entry) {
				localStorage.removeItem("lastClick");
			}
		}
	});

	var lastClick = localStorage.getItem("lastClick");
	if (lastClick != null) {
		if (lastClick !== "lev1") {
			var childWindow = window.open('', lastClick);
			if (childWindow != null){
				childWindow.close();
				localStorage.removeItem("lastClick");
			}
		} else {
			var childWindow = window.open('', '_viz1');
			try{
				childWindow.close();
			} catch(err){
				console.log("_viz1 tab is already closed");
			}
//			childWindow = window.open('', '_cut1');
//			try{
//				childWindow.close();
//			} catch(err){
//				console.log("_cut1 tab is already closed");
//			}
		}
	}
}

function setLastClick(name) {
	localStorage.setItem("lastClick", name);
}

function removeLastClick(name){
	if (localStorage.getItem("lastClick") == name){
		localStorage.removeItem("lastClick");
	}
}

function closeTaxTable(level) {
	if (level == 1) {
		var win = document.getElementById("levOneVizForm:taxTable");
		if (win != undefined) {
			if (win.style.display == "block") {
				PF('taxTableWidget').hide();
			}
		}
	} else if (level == 2){
		var win = document.getElementById("levTwoVizForm:taxTable2");
		if (win != undefined) {
			if (win.style.display == "block") {
				PF('taxTableWidget2').hide();
			}
		}
	}
}
