<%@ page language="java" session="true" autoFlush="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %><%
%><%@ page import="kr.pe.sinnori.weblib.common.WebCommonStaticFinalVars" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width,initial-scale=1.0,user-scalable=no" />
<title><%=WebCommonStaticFinalVars.WEBSITE_TITLE%></title>
    <!-- 공통요소 -->
	<link rel="stylesheet" type="text/css" href="/axisj/axicon/axicon.min.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXJ.css" />
	<link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/page.css" />

    <script type="text/javascript" src="/axisj/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXJ.js"></script>

    <!-- 추가하는 UI 요소 -->
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXInput.css" />
	 <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXButton.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXSelect.css" />
    <link rel="stylesheet" type="text/css" href="/axisj/ui/arongi/AXGrid.css" />
    <script type="text/javascript" src="/axisj/lib/AXInput.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXSelect.js"></script>
    <script type="text/javascript" src="/axisj/lib/AXGrid.js"></script>
	<script type="text/javascript" src="/axisj/lib/AXTopDownMenu.js"></script>
	<script type="text/javascript" src="/axisj/lib/AXMobileMenu.js"></script>

	
    <!-- js block -->

	<script type="text/javascript">
	var pageID = "siteMenu";
	var myMobileMenu = new AXMobileMenu();
	var fnObj = {
		pageStart: function(){
            fnObj.menu.bind();
        },
        menu: {
            mxMenu: new AXMobileMenu(),
            dxMenu: new AXTopDownMenu(),
            bind: function(){
                // 데스크탑 메뉴 설정
                window.dxMenu = fnObj.menu.dxMenu;
                dxMenu.setConfig({
                    menuBoxID:"menuBox",
                    parentMenu:{
                        className:"parentMenu"
                    },
                    childMenu:{
                        className:"childMenu",
                        align:"center",
                        valign:"top",
                        margin:{top:0, left:0},
                        arrowClassName:"varrow2",
                        arrowMargin:{top:1, left:0}
                    },
                    childsMenu:{
                        className:"childsMenu",
                        align:"left",
                        valign:"top",
                        margin:{top:-4, left:0},
                        arrowClassName:"harrow",
                        arrowMargin:{top:13, left:1}
                    },
                    onComplete: function(){
                        //myMenu.setHighLightMenu(0);
                        //myMenu.setHighLightMenu([0,1,3,2]);
                        dxMenu.setHighLightOriginID("ID10001");
                    }
                });

                // 모바일 메뉴 설정
                window.mxMenu = fnObj.menu.mxMenu;
                mxMenu.setConfig({
                    reserveKeys:{
                        primaryKey:"menuID",
                        labelKey:"label",
                        urlKey:"url",
                        targetKey:"target",
                        addClassKey:"ac",
                        subMenuKey:"cn"
                    },
                    menuBoxID:"menuBox",
                    parentMenu:{
                        className:"parentMenu"
                    },
                    childMenu:{
                        className:"childMenu"
                    },
                    childsMenu:{
                        className:"childsMenu"
                    },
                    onclick: function(){ // 메뉴 클릭 이벤트
                        //location.href = this.url; 원하는 링크를 구현하세요
                        mxMenu.close(); // 모바일 메뉴를 닫습니다.
                    }
                });
                mxMenu.setHighLightOriginID("ID10001");

                axdom("#mx-menu-handle").bind("click", function(){
                    mxMenu.open();
                });

            }
        }
	};
	$(document.body).ready(function(){
		fnObj.pageStart();
	});
	</script>
	<!-- js block -->

</head>
<body>

<div id="AXPage">
    <div id="AXPageHeader" class="SampleAXSelect">

    <style type="text/css">

        .varrow1 	{position:absolute;background: transparent url('/images/axisj/allow_menu1.gif') no-repeat;width:19px;height:10px;z-index:2;_margin-top:4px;}
        .varrow2 	{position:absolute;background: transparent url('/images/axisj/allow_menu2.gif') no-repeat;width:19px;height:10px;z-index:2;_margin-top:4px;}
        .harrow 	{position:absolute;background: transparent url('/images/axisj/allow_menu3.gif') no-repeat;width:9px;height:13px;z-index:2;}

        .AXMenuBox{position:relative;background: #3e4558;height:40px;line-height:40px;padding:0px 20px;min-width:700px;}
        .AXMenuBox a{
            transition-delay:0s;
            transition-timing-function:ease;
            transition-duration:0.3s;
            transition-property:all;
        }
        .AXMenuBox ul{list-style:none;margin:0px; padding:0px;}
        .AXMenuBox ul li{list-style:none;margin:0px; padding:0px;float:left;}

        .AXMenuBox .parentMenu{color:#fff;position:relative;font-size:14px;}
        .AXMenuBox .parentMenu a{
            display:block;_display:inline-block;
            color:#fff;text-decoration:none;
            height:40px;line-height:40px;font-size:11px;font-style:italic;
            padding:0px 10px;
        }
        .AXMenuBox .parentMenu a:hover{background:#9197a4;}
        .AXMenuBox .parentMenu a:focus{background:#9197a4;}
        .AXMenuBox .parentMenu a.on{background:#9197a4;}

        .AXMenuBox .childMenu{position:absolute;line-height:25px;font-size:12px;display:none;}
        .AXMenuBox .childMenu ul{
            background:#ebebeb;border:1px solid #ccc;border-radius:5px;
            padding:3px; margin:0px;display:block;position:relative;
            box-shadow:0px 0px 3px #ccc;
        }
        .AXMenuBox .childMenu ul li{float:none;padding:0px; margin:0px;border-bottom:1px solid #ebebeb;}
        .AXMenuBox .childMenu ul li a{
            display:block;_width:100%;
            padding:0px 10px;
            height:30px;line-height:30px;
            background:#424a5c;
            border-radius:3px;
            color:#fff; text-decoration: none;white-space:nowrap;
        }
        .AXMenuBox .childMenu ul li a:hover{background:#9197a4;}

        .AXMenuBox .childMenu ul li a.on{background:#9197a4;}
        .AXMenuBox .childMenu ul li a.expand{background-image:url(/images/axisj/rightArrows.png);background-repeat:no-repeat;background-position:100% 0px;padding-right:30px;}
        .AXMenuBox .childMenu ul li a.expand:hover{background-image:url(/images/axisj/rightArrows.png);background-repeat:no-repeat;background-position:100% -30px;padding-right:30px;}

        .AXMenuBox .childsMenu{position:absolute;line-height:20px;font-size:12px;}
        .AXMenuBox .childsMenu ul{background:#ebebeb;border:1px solid #ccc;padding:3px; margin:0px;}
        .AXMenuBox .childsMenu ul li{float:none;padding:0px; margin:0px;border-bottom:1px solid #ebebeb;}
        .AXMenuBox .childsMenu ul li a{}

        .clear{clear:both;}

        .mx-menu-handle{
            display:none;
            cursor: pointer;
            color: #fff;
        }

        @media (max-width: 600px) {
            .mx-menu-handle{
                display:block;
            }
            #menuBox{
                display:none;
            }
        }

    </style>
    <div class="H20"></div>
    <div class="AXMenuBox" style="z-index:5;">
        <a class="mx-menu-handle" id="mx-menu-handle">Mobile Menu Open</a>
        <div id="menuBox">
            <ul>
                <li>
                    <div class="parentMenu">
                        <a href="#" id="ID1001">About</a>
                    </div>
                </li>
                <li>
                    <div class="parentMenu">
                        <a href="#" id="ID1002">Sinnori</a>
                        <div class="childMenu">
                            <ul>
                                <li><a href="#" id="ID1003">Getting Started</a></li>
                                <li><a href="#" id="ID1004">Download</a></li>
                                <li><a href="#" id="ID1005">FAQ</a></li>
                                <li><a href="#" id="ID1006">License</a></li>                                
                            </ul>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="parentMenu">
                        <a href="#" id="ID1007">Document</a>
                    </div>
                </li>
                <li>
                    <div class="parentMenu">
                        <a href="#" id="ID1008">Communcation</a>
							<div class="childMenu">
                            <ul>
                                <li><a href="#" id="ID1009">Notice Board</a></li>
                                <li><a href="#" id="ID1010">Free Board</a></li>                             
                            </ul>
                        </div>
                    </div>
                </li>
                <li>
                    <div class="parentMenu">
                        <a href="#" id="ID1011">Utils</a>
                        <div class="childMenu">
                            <ul>
                                <li><a href="/menu/util/javaEscapeStringStep01.jsp" id="ID1012">sql string to java escape string</a></li>
                                <li><a href="#" id="ID1013">AXInput</a></li>
                                <li><a href="#" id="ID1014">Jowrney</a></li>
                                <li><a href="#" id="ID1015">Stacey</a></li>
                            </ul>
                        </div>
                    </div>
                </li>
					<li>
                    <div class="parentMenu">
                        <a href="#" id="ID1016">Member</a>
                        <div class="childMenu">
                            <ul>
                                <li><a href="/servlet/Login" id="ID1017">Login</a></li>
                                <li><a href="/servlet/Member" id="ID1018">register member</a></li>
                            </ul>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

        <div class="clear"></div>
    </div>	
    </div>
	<div id="AXPageBody" class="SampleAXSelect">
		<div class="AXdemoPageContent" style="min-height: 600px;">
		hello
		</div>
	</div>
</div>

</body>
</html>
