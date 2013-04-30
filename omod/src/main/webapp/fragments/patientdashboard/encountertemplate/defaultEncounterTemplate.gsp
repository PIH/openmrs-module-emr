<script type="text/javascript">
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

function getEncounterIcon(encounterType) {
    var encounterIconMap = {
        "4fb47712-34a6-40d2-8ed3-e153abbd25b7": "icon-vitals",
        "55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b": "icon-check-in",
        "92fd09b4-5335-4f7e-9f63-b2a663fd09a6": "icon-stethoscope",
        "1b3d1e13-f0b1-4b83-86ea-b1b1e2fb4efa": "icon-x-ray",
        "873f968a-73a8-4f9c-ac78-9f4778b751b6": "icon-register",
        "f1c286d0-b83f-4cd4-8348-7ea3c28ead13": "icon-money",
        "c4941dee-7a9b-4c1c-aa6f-8193e9e5e4e5": "icon-user-md",
        "1373cf95-06e8-468b-a3da-360ac1cf026d": "icon-calendar"
    };
    return encounterIconMap[encounterType] || "icon-time";
};
</script>

<script type="text/template" id="defaultEncounterTemplate">
<li>
	<div class="encounter-date">
	    <i class="icon-time"></i>
	    <strong>
	        {{- encounter.encounterTime }}
	    </strong>
	    {{- encounter.encounterDate }}
	</div>
	<ul class="encounter-details">
	    <li> 
	        <div class="encounter-type">
	            <strong>
	                <i class="{{- getEncounterIcon(encounter.encounterType.uuid) }}"></i>
	                <span class="encounter-name" data-encounter-id="{{- encounter.encounterId }}">{{- encounter.encounterType.name }}</span>
	            </strong>
	        </div>
	    </li>
	    <li>
	        <div>
	            ${ ui.message("emr.by") }
	            <strong>
	                {{- encounter.encounterProviders[0] ? encounter.encounterProviders[0].provider : '' }}
	            </strong>
	            ${ ui.message("emr.in") }
	            <strong>{{- encounter.location }}</strong>
	        </div>
	    </li>
	    <li>
	        <div class="details-action">
	            <a class="view-details collapsed" href='javascript:void(0);' data-encounter-id="{{- encounter.encounterId }}" data-encounter-form="{{- encounter.form != null}}" data-target="#encounter-summary{{- encounter.encounterId }}" data-toggle="collapse" data-target="#encounter-summary{{- encounter.encounterId }}">
	                <span class="show-details">${ ui.message("emr.patientDashBoard.showDetails")}</span>
	                <span class="hide-details">${ ui.message("emr.patientDashBoard.hideDetails")}</span>
	                <i class="icon-caret-right"></i>
	            </a>
	        </div>
	    </li>
	</ul>
	{{ if ( encounter.canDelete ) { }}
	<span>
	    <i class="deleteEncounterId delete-item icon-remove" data-encounter-id="{{- encounter.encounterId }}" title="${ ui.message("emr.delete") }"></i>
	</span>
	{{  } }}
	<div id="encounter-summary{{- encounter.encounterId }}" class="collapse">
	    <div class="encounter-summary-container"></div>
	</div>
</li>
</script>

<script type="text/template" id="defaultEncounterDetailsTemplate">
    {{ _.each(observations, function(observation) { }}
        {{ if(observation.answer != null) {}}
            <p><small>{{- observation.question}}</small><span>{{- observation.answer}}</span></p>
        {{}}}
    {{ }); }}

    {{ _.each(diagnoses, function(diagnosis) { }}
        {{ if(diagnosis.answer != null) {}}
            <p><small>{{- diagnosis.question}}</small><span>{{- diagnosis.answer}}</span></p>
    {{}}}
    {{ }); }}

    {{ _.each(orders, function(order) { }}
         <p><small>${ ui.message("emr.patientDashBoard.order")}</small><span>{{- order.concept }}</span></p>
    {{ }); }}
</script>