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

    #selected-studies {
        list-style: none;
        margin-left: 0;
        padding-left: 0;
    }

    #selected-studies li {
        border: 1px black solid;
        border-radius: 5px;
        background-color: #e0e0e0;
        padding: 1em;
    }

    #bottom {
        clear: both;
        float: right;
    }
</style>

<script type="text/template" id="selected-study-template">
    <li>
        {{- label }}
        <input type="hidden" value="{{- value }}" name="studies" />
        <span style="float:right">X</span>
    </li>
</script>

<script type="text/javascript">
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
    <input id="study-search" type="text" size="40"/>
</div>


<form action="${ ui.actionLink("emr", "radiologyRequisition", "orderXray") }">
    <input type="hidden" name="patient" value="${ patient.id }"/>
    <input type="hidden" name="requestedBy" value="${ currentProvider.id }"/>

    <div class="right-column">
        Selected studies:
        <br/>
        <ul id="selected-studies" class="right-column">

        </ul>
    </div>

    <br/><br/>

    <div class="left-column">
        ${ ui.includeFragment("emr", "field/textarea", [ label: "Clinical History", formFieldName: "clinicalHistory", labelPosition: "top", rows: 10, cols: 60 ]) }
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
        <button type="button" onclick="location.href = emr.pageLink('emr', 'patient', { patientId: <%= patient.id %> })">
            <img src="${ ui.resourceLink("uilibrary", "images/close_32.png") }"/> <br/>
            ${ ui.message("emr.cancel") }
        </button>
        <button type="submit">
            <img src="${ ui.resourceLink("uilibrary", "images/arrow_right_32.png") }"/> <br/>
            ${ ui.message("emr.next") }
        </button>
    </div>

</form>