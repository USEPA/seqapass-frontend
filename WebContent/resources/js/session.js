console.log("Reading session.js from static file");
var maxTime = 29000; // (in seconds)
var activity;
var running = false;
var globalTime = 0;
var timestep = 10;

asyncCounter();

function sessionOnLoad() {
	activity = debounce(doActivity, 3000, false);
	window.addEventListener("mousemove", activity, false);
	window.addEventListener("scroll", activity, false);
}

function doActivity() {
	globalTime = 0;
	pingBE();
}

function checkTime() {
	if (globalTime >= maxTime) {

		var lowestTime;
		var isParent = (window.name === "seqMain");
		if (isParent) {
			lowestTime = getLowestGlobalTime();
		} else {
			lowestTime = window.opener.getLowestGlobalTime();
		}

		if (lowestTime >= maxTime) {
			endAllSessions();
			// endSession();
		} else {
			killTimer();
		}
	}
}

function endAllSessions() {
	var isParent = (window.name === "seqMain");
	if (isParent) {
		if (vizNamespace.viz1Window && !vizNamespace.viz1Window.closed) {
			vizNamespace.viz1Window.close();
		}
		if (vizNamespace.viz2Window && !vizNamespace.viz2Window.closed) {
			vizNamespace.viz2Window.close();
		}
		if (vizNamespace.viz3Window && !vizNamespace.viz3Window.closed) {
			vizNamespace.viz3Window.close();
		}
		if (vizNamespace.cut1Window && !vizNamespace.cut1Window.closed) {
			vizNamespace.cut1Window.close();
		}
		if (vizNamespace.cut2Window && !vizNamespace.cut2Window.closed) {
			vizNamespace.cut2Window.close();
		}
		window.focus();
		endSession();
		return;
	}
	// If we get here, we were a child, so tell parent to endAllSessions();
	window.opener.endAllSessions();
}

// must be called by seqMain, not by child windows (_viz1, _viz2, etc.)
function getLowestGlobalTime() {
	var lowestTime = globalTime;
	var localTime;

	if (vizNamespace.viz1Window && !vizNamespace.viz1Window.closed) {
		localTime = vizNamespace.viz1Window.globalTime
		if (localTime < lowestTime)
			lowestTime = localTime;
	}
	if (vizNamespace.viz2Window && !vizNamespace.viz2Window.closed) {
		localTime = vizNamespace.viz2Window.globalTime
		if (localTime < lowestTime)
			lowestTime = localTime;
	}
	if (vizNamespace.viz3Window && !vizNamespace.viz3Window.closed) {
		localTime = vizNamespace.viz3Window.globalTime
		if (localTime < lowestTime)
			lowestTime = localTime;
	}
	if (vizNamespace.cut1Window && !vizNamespace.cut1Window.closed) {
		localTime = vizNamespace.cut1Window.globalTime
		if (localTime < lowestTime)
			lowestTime = localTime;
	}
	if (vizNamespace.cut2Window && !vizNamespace.cut2Window.closed) {
		localTime = vizNamespace.cut2Window.globalTime
		if (localTime < lowestTime)
			lowestTime = localTime;
	}

	return lowestTime;
}

function asyncCounter() {
	globalTime = globalTime + timestep;
	if (!running) {
		return;
	}
	checkTime();
	setTimeout(asyncCounter, timestep * 1000);
}

function killTimer() {
	// console.log("My timer was killed");
	running = false;
	globalTime = maxTime;
}

function killTimerChild() {
	// console.log("Killing all child timers");
	if (vizNamespace.viz1Window) {
		vizNamespace.viz1Window.killTimer();
	}
	if (vizNamespace.viz2Window) {
		vizNamespace.viz2Window.killTimer();
	}
	if (vizNamespace.viz3Window) {
		vizNamespace.viz3Window.killTimer();
	}
	if (vizNamespace.cut1Window) {
		vizNamespace.cut1Window.killTimer();
	}
	if (vizNamespace.cut2Window) {
		vizNamespace.cut2Window.killTimer();
	}
	running = false;
}

function killTimerParent() {
	// console.log("Killing parent timer");
	window.opener.killTimer();
	running = false;
}

function startTimer() {
	// console.log("My timer is starting");
	globalTime = 0;
	running = true;
	asyncCounter();
}
