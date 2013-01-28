jq(function() {
    jq('.close-icon').click(function(e) {
    	jq(this).parent().fadeOut()
            .find(".text").html("");
    });
});
