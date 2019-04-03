var regexID = /^[A-Za-z][A-Za-z0-9]{3,14}$/;

var regexPwd = /^[A-Za-z0-9\`~!@\#$%<>\^&*\(\)\-=+_\'\[\]\{\}\\\|\:\;\"<>\?,\.\/]{8,15}$/;
var regexPwdAlpha = /.*[A-Za-z]{1,}.*/;
var regexPwdDigit = /.*[0-9]{1,}.*/;
var regexPwdPunct = /.*[\!\"#$%&'()*+,\-\.\/:;<=>\?@\[\\\]^_`\{\|\}~]{1,}.*/;

var _ATTACHED_FILE_MAX_COUNT = 2;

function checkValidUserID(title, userID) {
	if (userID == '') {
		var errmsg = title + " 아이디를 넣어주세요";
		throw errmsg;
	}
	if (!regexID.test(userID)) {
		var errmsg = title + " 아이디는 첫 문자가 영문자 그리고 영문과 숫자로만 최소 4자, 최대 15자로 구성됩니다. 다시 입력해 주세요";
		throw errmsg;
	}
}

function checkValidPwd(title, pwd) {	
	if (pwd == '') {
		var errmsg = title + " 비밀번호를 넣어주세요";
		throw errmsg;
	}

	if (! regexPwd.test(pwd)) {
		var errmsg = title + " 게시글 비밀번호는 영문, 숫자 그리고 특수문자 조합으로 최소 8자, 최대 15자로 구성됩니다. 다시 입력해 주세요"; 
		throw errmsg;
	}
	
	if (! regexPwdAlpha.test(pwd)) {
		var errmsg = title + " 게시글 비밀번호는 최소 영문 1자가 포함되어야 합니다. 다시 입력해 주세요" ; 
		throw errmsg;
	}

	if (! regexPwdDigit.test(pwd)) {
		var errmsg = title + " 게시글 비밀번호는 최소 숫자 1자가 포함되어야 합니다. 다시 입력해 주세요"; 
		throw errmsg;
	}

	if (! regexPwdPunct.test(pwd)) {
		var errmsg = title + " 게시글 비밀번호는 최소 특수문자 1자가 포함되어야 합니다. 다시 입력해 주세요";
		throw errmsg;
	}
}

<!-- WARNING! 파라미터 비밀번호(pwd)에 대한 유효성 검증을 먼저 했다는것을 전제로합니다 -->
function checkValidPwdConfirm(title, pwd, pwdConfirm) {
	if (pwdConfirm == '') {
		var errmsg = title + " 비밀번호 확인을 넣어주세요";
		throw errmsg;
	}
	
	if (pwd != pwdConfirm) {
		var errmsg = title + " 비밀번호와 비밀번호 확인이 일치 하지 않습니다";
		throw errmsg;
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

<!-- 목적한 DIV 로 화면 이동 시켜주는 window.scrollTo 함수의 파라미터 offsetTop 을 가져오는 함수 -->
function getOffsetTop(elem){
    var offsetTop = 0;
    do {
      if(!isNaN(elem.offsetTop)){
          offsetTop += elem.offsetTop;
      }
    } while( elem = elem.offsetParent );
    return offsetTop;
}
 
<!-- 목적한 DIV 로 화면 이동 시켜주는 window.scrollTo 함수의 파라미터 offsetLeft 을 가져오는 함수 -->
function getOffsetLeft(elem){
    var offsetLeft = 0;
    do {
      if(!isNaN(elem.offsetLeft)){
          offsetLeft += elem.offsetLeft;
      }
    } while( elem = elem.offsetParent );
    return offsetLeft;
}