<%
    ui.decorateWith("emr", "standardEmrPage")

    ui.includeJavascript("emr", "custom/xrayOrder.js")
%>

<script type="text/javascript">
    jq(document).ready( function() {
        ko.applyBindings( new StudiesViewModel(${ultrasoundOrderables}) );
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
    <p><span style="color:red">This is only a mockup. It's not a working version!</span></p>
    <h1>Ultrasound Requisition</h1>
    <form action="${ ui.actionLink("emr", "radiology/radiologyRequisition", "orderUltrasound") }" onsubmit="return false;">
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
        </div>


        <div class="left-column">
            <label for="study-search">Type the name of a study:</label><br/>
            <input id="study-search" style="width: 430px; height: 2em; padding-left: 5px; margin-top: 5px;" type="text" size="40" data-bind="autocomplete: searchTerm" placeholder="eg. Head without contrast"/>
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

