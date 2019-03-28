var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
var regexPwdDigit = /.*[0-9]{1,}.*/;
var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;

var _ATTACHED_FILE_MAX_COUNT = 2;

function checkValidPwd(pwd) {	
	if (pwd == '') {
		throw "비밀번호를 넣어주세요.";
	}

	if (! regexPwd.test(pwd)) {
		throw "게시글 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요.";
	}
	
	if (! regexPwdAlpha.test(pwd)) {
		throw "게시글 비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요." ;
	}

	if (! regexPwdDigit.test(pwd)) {
		throw "게시글 비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요.";
	}

	if (! regexPwdPunct.test(pwd)) {
		throw "게시글 비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요.";
	}
}


function checkValidPwdConfirm(pwd, pwdConfirm) {	
	if (pwdConfirm == '') {
		throw "비밀번호 확인을 넣어주세요";
	}
	
	if (pwd != pwdConfirm) {
		throw "비밀번호와 비밀번호 확인이 일치 하지 않습니다";
	}
}

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

function expandTextarea(id) {
    document.getElementById(id).addEventListener('keyup', function() {
        this.style.overflow = 'hidden';
        this.style.height = 0;
        if (this.scrollHeight < 100) {
        	this.style.height = '100px';
        } else {
        	this.style.height = this.scrollHeight + 'px';
        }
    }, false);
}








