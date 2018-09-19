function trim(str) {
	return str.replace(/^\s+|\s+$/gm,'');
}

function trimcheck(str) {
	var trimStr = trim(str);
	if (trimStr == str) {
		return false;
	} else {
		return true;
	}
}

function getNumberOfLines(str) {
	return str.split(/\r\n|[\n\r\u0085\u2028\u2029]/g).length;
}
