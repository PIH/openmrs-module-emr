<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "jquery-1.8.1.min.js")
    ui.includeJavascript("emr", "jquery-ui-1.8.23.custom.min.js")
    ui.includeCss("emr", "cupertino/jquery-ui-1.8.23.custom.css")
%>

<style type="text/css">
    .left-column, .right-column {
        float: left;
        width: 45%;
    }
    .left-column {
        clear: left;
    }
</style>

<script type="text/template" id="selected-study-template">
    <li>
        {{ label }}
        <input type="hidden" value="{{ value }}" name="studies" />
        <span style="float:right">X</span>
    </li>
</script>

<script type="text/javascript">
    _.templateSettings = {
        interpolate : /\\{\\{(.+?)\\}\\}/g
    };

    var selectedStudyTemplate = _.template(jq('#selected-study-template').html());

    var xrayOrderables = ${ xrayOrderables };

    jq(function() {
        jq('#study-search').autocomplete({
            source: function(request, response) {
                response(jq.ui.autocomplete.filter(xrayOrderables, request.term));
            },
            focus: function( event, ui ) {
                this.value = ui.item.label;
                return false;
            },
            select: function( event, ui ) {
                jq( "#selected-studies").append(selectedStudyTemplate(ui.item));
                this.value = '';
                var index = jq.inArray(ui.item, xrayOrderables);
                xrayOrderables.splice(index, 1)
                return false;
            }
        });
    });
</script>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<h1>X-Ray Requisition</h1>

<div class="left-column">
    <label for="study-search">Which Studies?</label>
    <input id="study-search" type="text"/>
</div>


<form action="${ ui.actionLink("emr", "radiologyRequisition", "orderXray") }">
    <input type="hidden" name="patient" value="${ patient.id }"/>
    <input type="hidden" name="requestedBy" value="${ currentProvider.id }"/>

    <ul id="selected-studies" class="right-column">

    </ul>

    <br/><br/>

    <div class="left-column">
        ${ ui.includeFragment("emr", "field/textarea", [ label: "Reasons for exam", formFieldName: "indication", labelPosition: "top" ]) }
    </div>

    <div class="right-column">
        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Urgency",
                formFieldName: "urgency",
                options: [
                    [ value: "ROUTINE", label: "Routine", checked: true ],
                    [ value: "STAT", label: "Urgent/Stat" ]
                ]
        ]) }
        <br/>

        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Exam Location",
                formFieldName: "ignoredExamLocation",
                options: [
                        [ value: "radiology", label: "Radiology", checked: true ],
                        [ value: "portable", label: "Portable" ]
                ]
        ]) }
        <br/>

        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Transport",
                formFieldName: "ignoredTransport",
                options: [
                        [ value: "walking", label: "Walking", checked: true ],
                        [ value: "wheelchair", label: "Wheelchair" ],
                        [ value: "stretcher", label: "Stretcher" ]
                ]
        ]) }

    </div>

    <div id="bottom">

    </div>

    <input type="button" value="${ ui.format("emr.cancel") }" onclick="location.href = emr.pageLink('emr', 'patient', { patientId: <%= patient.id %> })"/>
    <input type="submit" value="${ ui.format("emr.next") }"/>

</form>