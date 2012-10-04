<%
    ui.decorateWith("emr", "standardEmrPage")
%>

<style type="text/css">
    #top-left, #top-right {
        float: left;
        width: 45%;
    }
    #top-left, #bottom {
        clear: left;
    }
</style>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

<h1>X-Ray Requisition</h1>

<form action="${ ui.actionLink("emr", "radiologyOrder", "orderXray") }">

    <div id="top-left">
        ${ ui.includeFragment("emr", "field/textarea", [ label: "Reasons for exam", formFieldName: "reason", labelPosition: "top" ]) }
    </div>

    <div id="top-right">
        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Urgency",
                formFieldName: "urgency",
                options: [
                    [ value: "routine", label: "Routine", checked: true ],
                    [ value: "urgent", label: "Urgent/Stat" ]
                ]
        ]) }
        <br/>

        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Exam Location",
                formFieldName: "examLocation",
                options: [
                        [ value: "radiology", label: "Radiology", checked: true ],
                        [ value: "portable", label: "Portable" ]
                ]
        ]) }
        <br/>

        ${ ui.includeFragment("emr", "field/radioButtons", [
                label: "Transport",
                formFieldName: "transport",
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