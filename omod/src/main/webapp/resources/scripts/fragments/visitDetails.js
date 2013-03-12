function loadTemplates () {
    function loadVisit(visitElement) {
        var localVisitId = visitElement.attr('visitId');
        if (visitElement != null &&  localVisitId!= undefined) {
            visitDetailsSection.html("<i class=\"icon-spinner icon-spin icon-2x pull-left\"></i>");
            $.getJSON(
                emr.fragmentActionLink("emr", "visit/visitDetails", "getVisitDetails", {
                    visitId: localVisitId
                })
            ).success(function(data) {
                $('.viewVisitDetails').removeClass('selected');
                visitElement.addClass('selected');
                visitDetailsSection.html(visitDetailsTemplate(data));
                visitDetailsSection.show();
                $(".deleteEncounterId").click(function(event){
                    var encounterId = $(event.target).attr("data-encounter-id");
                    createDeleteEncounterDialog(encounterId, $(this));
                    showDeleteEncounterDialog();
                });
            }).error(function(err) {
                emr.errorMessage(err);
            });
        }
    }

    var encounterDetailsTemplate = _.template($('#encounterDetailsTemplate').html());

    var visitDetailsTemplate = _.template($('#visitDetailsTemplate').html());
    var visitsSection = $("#visits-list");

    var visitDetailsSection = $("#visit-details");

    //load first visit
    loadVisit($('.viewVisitDetails').first());


    $('.viewVisitDetails').click(function() {
        loadVisit($(this));
        return false;
    });

    $(document).on("click",'.view-details.collapsed', function(event){
        var encounterId = $(event.currentTarget).attr("data-encounter-id");
        var isHtmlForm = $(event.currentTarget).attr("data-encounter-form");
        var dataTarget = $(event.currentTarget).attr("data-target");
        getEncounterDetails(encounterId, isHtmlForm, dataTarget);
    });

    function getEncounterDetails(id, isHtmlForm, dataTarget){
        var encounterDetailsSection = $(dataTarget + ' .encounter-summary-container');
        if (isHtmlForm == "true"){
            $.getJSON(
                emr.fragmentActionLink("emr", "htmlform/viewEncounterWithHtmlForm", "getAsHtml", { encounterId: id })
            ).success(function(data){
                encounterDetailsSection.html(data.html);
            }).error(function(err){
                emr.errorAlert(err);
            });
        } else {
            $.getJSON(
                emr.fragmentActionLink("emr", "visit/visitDetails", "getEncounterDetails", { encounterId: id })
            ).success(function(data){
                encounterDetailsSection.html(encounterDetailsTemplate(data));
            }).error(function(err){
                emr.errorAlert(err);
            });
        }
    }
};

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