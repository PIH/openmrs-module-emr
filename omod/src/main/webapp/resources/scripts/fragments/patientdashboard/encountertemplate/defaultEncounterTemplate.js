jq(function() {
	jq(document).on('click','.view-details.collapsed', function(event){
	        var encounterId = jq(event.currentTarget).attr("data-encounter-id");
	        var isHtmlForm = jq(event.currentTarget).attr("data-encounter-form");
	        var dataTarget = jq(event.currentTarget).attr("data-target");
	        getEncounterDetails(encounterId, isHtmlForm, dataTarget);
	    });
	    
	jq(document).on('click', '.deleteEncounterId', function(event){
		var encounterId = jq(event.target).attr("data-encounter-id");
		createDeleteEncounterDialog(encounterId, jq(this));
		showDeleteEncounterDialog();
	});
	    
	var defaultEncounterDetailsTemplate = _.template(jq('#defaultEncounterDetailsTemplate').html());
	
	function getEncounterDetails(id, isHtmlForm, dataTarget){
	    var encounterDetailsSection = jq(dataTarget + ' .encounter-summary-container');
	    if (isHtmlForm == "true"){
	        jq.getJSON(
	            emr.fragmentActionLink("emr", "htmlform/viewEncounterWithHtmlForm", "getAsHtml", { encounterId: id })
	        ).success(function(data){
	            encounterDetailsSection.html(data.html);
	        }).error(function(err){
	            emr.errorAlert(err);
	        });
	    } else {
	        jq.getJSON(
	            emr.fragmentActionLink("emr", "visit/visitDetails", "getEncounterDetails", { encounterId: id })
	        ).success(function(data){
	            encounterDetailsSection.html(defaultEncounterDetailsTemplate(data));
	        }).error(function(err){
	            emr.errorAlert(err);
	        });
	    }
	}
});