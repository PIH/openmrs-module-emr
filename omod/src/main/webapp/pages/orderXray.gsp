<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "jquery-1.8.1.min.js")
    ui.includeJavascript("emr", "jquery-ui-1.8.23.custom.min.js")
    ui.includeJavascript("emr", "knockout-2.1.0.js")
    ui.includeJavascript("emr", "custom/xrayOrder.js")
    ui.includeCss("emr", "cupertino/jquery-ui-1.8.23.custom.css")
%>

<script type="text/javascript" xmlns="http://www.w3.org/1999/html">
    jq(document).ready( function() {
        ko.applyBindings( new StudiesViewModel(${xrayOrderables}, ${portableLocations}) );

        // Preventing form submission when pressing enter on study-search input field
        jq('#study-search').bind('keypress', function(eventKey) {
           if(event.keyCode == 13) {
               event.preventDefault();
               return false;
           }
        });
    });
</script>
<style type="text/css">
    .left-column, .right-column {
        float: left;
        width: 45%;
        padding-bottom: 20px;
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
    #contentForm {
        padding-left: 50px;
        padding-right: 50px;
    }
</style>


${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }



<div id="contentForm">
<h1>X-Ray Requisition</h1>
<form action="${ ui.actionLink("emr", "radiologyRequisition", "orderXray") }" data-bind="submit: isValid">
    <input type="hidden" name="successUrl" value="${ ui.pageLink("emr", "patient", [ patientId: patient.id ]) }"/>
    <input type="hidden" name="patient" value="${ patient.id }"/>
    <input type="hidden" name="requestedBy" value="${ currentProvider.id }"/>

    <div class="left-column">
        ${ ui.includeFragment("emr", "field/textarea", [ label: "Indication", formFieldName: "clinicalHistory", labelPosition: "top", rows: 5, cols: 60 ]) }
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
        <div class="row">
            <span class="radio-label">Do you need a portable x-ray?</span>
            <div>
                <input type="checkbox" class="field-value" value="portable" data-bind="checked: portable"/>
                <span>Yes</span>
            </div>
            <input type="text" placeholder="Type location ..."
                   data-bind="visible:portable, autocomplete:searchLocationTerm, search:convertedPortableLocations, select:selectLocation, clearValue:function() { return false; }"/>
            <input name="examLocation" type="hidden" data-bind="value:portableLocation"/>
        </div>
    </div>


    <div class="left-column">
        <label for="study-search">Type the name of a study:</label><br/>
        <input id="study-search" style="width: 430px; height: 2em; padding-left: 5px; margin-top: 5px;" type="text" size="40"
               data-bind="autocomplete:searchTerm, search:convertedStudies, select:selectStudy, clearValue:function() { return true; }"
               placeholder="eg. Chest x-ray"/>
    </div>
    <div class="right-column">
        <div data-bind="visible: selectedStudies().length == 0">
            <span style="color: blue;">Please select at least one study.</span>
        </div>
        <div data-bind="visible: selectedStudies().length > 0">
            <label>Selected studies</label>
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
</div>

