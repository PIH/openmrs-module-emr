<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "jquery-1.8.1.min.js")
    ui.includeJavascript("emr", "jquery-ui-1.8.23.custom.min.js")
    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "xrayOrder.js")
    ui.includeCss("emr", "cupertino/jquery-ui-1.8.23.custom.css")
%>

<script type="text/javascript">
    jq(document).ready( function() {
        ko.applyBindings( new StudiesViewModel(${xrayOrderables}) );
    });
</script>
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
        padding: 0.5em 1em 0.5em 1em;
        width: 100%;
    }

    #bottom {
        clear: both;
    }

    .row {
        display: table-row;
    }
    .row .radio-label , .row div {
        display: table-cell;
        padding-right: 1.0em;
        white-space: nowrap;
    }
</style>


${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }


<h1>X-Ray Requisition</h1>

<form action="${ ui.actionLink("emr", "radiologyRequisition", "orderXray") }">
    <input type="hidden" name="successUrl" value="${ ui.pageLink("emr", "patient", [ patientId: patient.id ]) }"/>
    <input type="hidden" name="patient" value="${ patient.id }"/>
    <input type="hidden" name="requestedBy" value="${ currentProvider.id }"/>

    <div class="left-column">
        ${ ui.includeFragment("emr", "field/textarea", [ label: "Indication", formFieldName: "clinicalHistory", labelPosition: "top", rows: 10, cols: 60 ]) }
    </div>

    <div class="right-column">
        <div class="row">
            ${ ui.includeFragment("emr", "field/radioButtons", [
                    label: "Timing",
                    formFieldName: "urgency",
                    options: [
                        [ value: "ROUTINE", label: "Routine", checked: true ],
                        [ value: "STAT", label: "STAT" ]
                    ]
            ]) }
        </div>
    </div>


    <div class="left-column">
        <label for="study-search">Which Studies?</label><br/>
        <input id="study-search" type="text" size="40" data-bind="autocomplete: searchTerm" />
    </div>
    <div class="right-column">
        <p>Selected studies:</p>
        <div style="border: 1px solid #000000; height: 100px; width: 100%;">
        <ul id="selected-studies" data-bind="foreach: selectedStudies">
            <li>
                <input type="hidden" data-bind="value: id" name="studies" />
                <span data-bind="text: name"></span>
                <span style="float:right" data-bind="click: \$root.unselectStudy">X</span>
            </li>
        </ul>
        </div>
    </div>

    <div id="bottom">
        <button type="button" style="float: left;" onclick="location.href = emr.pageLink('emr', 'patient', { patientId: <%= patient.id %> })">
            <img src="${ ui.resourceLink("uilibrary", "images/close_32.png") }"/> <br/>
            ${ ui.message("emr.cancel") }
        </button>
        <button type="submit" style="float: right;" data-bind="visible: selectedStudies().length > 0">
            <img src="${ ui.resourceLink("uilibrary", "images/arrow_right_32.png") }"/> <br/>
            ${ ui.message("emr.next") }
        </button>
    </div>
</form>


