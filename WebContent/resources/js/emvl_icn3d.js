
function pushToICN3D(pdb) {
	if (pdb !== ""){
		if (!localStorage.getItem("icn3dload")){
			firstLoadICN3D();
		}
		if (typeof icn3dui !== 'undefined'){
			//first check to see if in popup
			popup = document.getElementById('tabView:reportForm:icn3dpopup');
			var popupOpen = false;
			//if popup, we need to move back to main div, load pdb, and the move back to popup
			if (popup.style["display"] !== 'none' && popup.style["display"] !== ''){
				moveIcn3dFrompop()
				popupOpen = true;
			}
			icn3dui.show3DStructure(pdb);
			resetDefaultBackground();
			if (popupOpen){
				moveIcn3d2pop(0);
			}
		}
	}
}

function clearProteins(){
	clearPDBStr();
	icn3dui.cfg.mmdbafid = undefined;
}

function onLoadL4(){
	if (localStorage.getItem("icn3dload")){
		clearICN3D();
	}
	
}

function clearICN3D(clearOnly){
	clearProteins();
	if (!clearOnly){
	  resetICN3D();
	  closeIcn3dDialogs();
	}
	//$('#icn3dwrap_dl_mmdbafid').parent().css('display', 'none');
}

function closeIcn3dDialogs(){
	try {
		icn3dui.icn3d.resizeCanvasCls.closeDialogs();
	} catch{}
	//secondary to close load dialog
	loadDlg = document.querySelectorAll('[aria-describedby="icn3dwrap_dl_mmdbafid"]');
	for (const element of loadDlg){
		element.style["display"] = "none";
	}
}

function deleteICN3DLocalStorage(){
		localStorage.clear("icn3dload");
}

function startOrResetICN3D(clearAll){
	if (!localStorage.getItem("icn3dload")){
		firstLoadICN3D();
	} else {
		if(clearAll) clearProteins();
		icn3dui.show3DStructure();
		icn3dui.icn3d.setStyleCls.setBackground("black");
	}
}

async function docReadyICN3D(){
	startOrResetICN3D();
	//Check local storage to see if iCn3D has already been loaded
	//if (localStorage.getItem("icn3dload")){
	//	//page has already been loaded
	//	resetICN3D();
	//	closeIcn3dDialogs();
	//} //else {
	//	//page has not been loaded yet
	//	firstLoadICN3D();
	//	localStorage.setItem("icn3dload", "loaded");
	//}
}

async function firstLoadICN3D(){
	//this will break if icn3d has already been loaded.
	if (!localStorage.getItem("icn3dload")){
		var cfg = getConfig();
	
		icn3dui = new icn3d.iCn3DUI(cfg);

		//communicate with the 3D viewer with chained functions
		await icn3dui.show3DStructure();
    
    	icn3dui.icn3d.setStyleCls.setBackground("black");
    	resetDefaultBackground();
    
    	localStorage.setItem("icn3dload", "loaded");
    }
}

async function resetICN3D(pdb) {

	//icn3dui = new icn3d.iCn3DUI(icn3dui.cfg);

	let ic = icn3dui.icn3d;
    //ic.resizeCanvasCls.closeDialogs();
    closeIcn3dDialogs();
    
	//communicate with the 3D viewer with chained functions
	await icn3dui.show3DStructure();
    
    icn3dui.icn3d.setStyleCls.setBackground("black");
    resetDefaultBackground();
}

function moveIcn3d2pop(size) {
	const elementToMove = document.getElementById('icn3d');
	const newParentElement = document.getElementById('icn3dDiv');
	newParentElement.appendChild(elementToMove);
	//get window width and height
	thisView = document.getElementById('tabView');
	popWidth = thisView.clientWidth/8;
	popHeight = thisView.clientHeight/4;
	chosenWidth = popWidth;
	if (chosenWidth>popHeight) chosenWidth = popHeight;
	if (size > 0) chosenWidth = chosenWidth*2;
	
	icn3dui.icn3d.resizeCanvasCls.resizeCanvas(chosenWidth, chosenWidth, true, true);
	
	cmdSection = document.getElementById('icn3dwrap_cmdlog');
	menuSection = document.getElementById('icn3dwrap_mnlist');
	buttonsSection = document.getElementById('tabView:reportForm:icn3dButtons');
	menuLogSection = document.getElementById('icn3dwrap_mnLogSection');
	
	cmdSection.style.display = 'none';
	menuSection.style.display = 'none';
	buttonsSection.style.display = 'none';
	menuLogSection.style.display = 'none';	
}

function resizePopupWindow(width, height){
	popWin = document.getElementById('tabView:reportForm:icn3dpopup');
	popWin.style.width = width + 'px';
	popWin.style.height = height + 'px';
	popWinContent = document.getElementById('tabView:reportForm:icn3dpopup_content');
	popWinContent.style.width = width + 'px';
	popWinContent.style.height = height + 'px';
}

function resizeCanvas(){
	popWin = document.getElementById('tabView:reportForm:icn3dpopup');
	windowHeight = popWin.clientHeight;
	theHeader = popWin.getElementsByClassName('ui-dialog-titlebar')[0];
	headerHeight = theHeader.clientHeight;
	popWinContent = document.getElementById('tabView:reportForm:icn3dpopup_content');
	canvasWidth = parseInt(popWinContent.style.width);
	canvasHeight = windowHeight - headerHeight - 16;
	
	icn3dui.icn3d.resizeCanvasCls.resizeCanvas(canvasWidth, canvasHeight, true, true);
}

function positionPopup(){
	page = document.getElementById('page');
	popup = document.getElementById('tabView:reportForm:icn3dpopup');
	
	popup.style.top = '0px';
	popup.style.left = page.getBoundingClientRect().width - popup.getBoundingClientRect().width + 'px';
}

function moveIcn3dFrompop() {
	const elementToMove = document.getElementById('icn3d');
	const newParentElement = document.getElementById('tabView:reportForm:icn3dPanel_content');
	newParentElement.appendChild(elementToMove);
	
	cmdSection = document.getElementById('icn3dwrap_cmdlog');
	menuSection = document.getElementById('icn3dwrap_mnlist');
	buttonsSection = document.getElementById('tabView:reportForm:icn3dButtons');
	menuLogSection = document.getElementById('icn3dwrap_mnLogSection');
	
	cmdSection.style.display = 'block';
	menuSection.style.display = 'block';
	buttonsSection.style.display = 'block';
	menuLogSection.style.display = 'block';	
	
	icn3dui.icn3d.resizeCanvasCls.resizeCanvas(icn3dui.htmlCls.WIDTH, icn3dui.htmlCls.HEIGHT, true, true);
}			

function icn3dSetupChainsSelection() {
	chainNames = Object.keys(icn3dui.icn3d.structures).concat(Object.values(icn3dui.icn3d.structures).flat()).join(":");
	showChainsOptions([{
		name: 'chainNames',
		value: chainNames
	}]);
}

function icn3dSuperpose(chainsString) {
	let myChains = [];
	myChains = chainsString.split(",");
	icn3dui.cfg.aligntool = "tmalign";
	icn3dui.icn3d.nameArray = myChains;
	icn3dui.icn3d.hAtoms = icn3dui.icn3d.definedSetsCls.getAtomsFromNameArray(icn3dui.icn3d.nameArray);
	icn3dui.icn3d.realignParserCls.realignOnStructAlign();
}

function getConfig(){
	var cfg = {
				divid: 'icn3dwrap',
				width: '93%',
	            height: '100%',
	            resize: true,
	            rotate: 'right',
	            mobilemenu: false,
	            showcommand: true,
	            showtitle: true
	};
	return cfg;
}