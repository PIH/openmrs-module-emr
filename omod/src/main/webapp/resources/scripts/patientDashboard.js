jq(document).ready( function() {

    var divItems = new Array("visitsDiv", "contactInfoDiv");

    jq.hideAllDiv = function(){
        for(i=0; i<divItems.length; i++){
            var divItem = "#"+divItems[i];
            //jq.(divItem).css("visibility", "hidden");
            jq(divItem).hide();
        }
    };

    jq.setupVisitsDiv = function() {
      jq.hideAllDiv();
      jq("#visitsDiv").css("visibility", "visible");
      jq("#visitsDiv").show();
      return true;
    };
    jq.setupContactInfoDiv = function() {
        jq.hideAllDiv();
        jq("#contactInfoDiv").css("visibility", "visible");
        jq("#contactInfoDiv").show();
        return true;
    };

    jq.setupVisitsDiv();
});
