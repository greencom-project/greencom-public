$(document).ready(function(){

	// dropdown menu
	$("header nav > ul > li").each(function(){
		$(this).find("ul").parents("li").addClass("dropdown");
	});
	if( $("header nav .login").find("ul") ){
		$("header nav .login").addClass("dropdown");
	}
	positionLogin = function(){
		var position = $("header nav .login").position();
		$("header nav .login ul").css('left', parseInt(position.left) + 'px');
	}
	// End dropdown menu
	
	// dropdown menu mobile 
	$(".show_menu").click(function(e){
		e.preventDefault();
		$('body').addClass("expanded_menu");
	});
	$(".hide_menu, #menu .close").click(function(e){
		e.preventDefault();
		$('body').removeClass("expanded_menu");
	});
	// End dropdown menu mobile 

	// Height for aside
	heightFooter = function(){
		var asideHeight = parseInt($("aside").height());
		var footerHeight = parseInt($("footer").height());
		if(!asideHeight){ asideHeight=0; }
		if(!footerHeight){ asideHeight=0; }
		$("section").css("padding-bottom",asideHeight + footerHeight);
	}
	$(window).load(function(){
		positionLogin();
		heightFooter();
	});
	$(window).resize(function(){
        positionLogin();
		heightFooter();
    });
    
    // Detect touch devices
    touchDevice = function(){
        var touch = typeof window.ontouchstart !== 'undefined';
        if(touch){
            $('body').addClass('touch');
        } else {
            $('body').addClass('no-touch'); 
        }
    }
    touchDevice();
	
	// Menu for touch device
    $(".touch nav .dropdown > a").not("#loginButton").click(function () {
		return false;
	});
	
});