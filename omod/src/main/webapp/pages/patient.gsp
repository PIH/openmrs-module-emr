<%
	ui.decorateWith("emr", "standardEmrPage")
%>

${ ui.includeFragment("emr", "patientHeader", [ patient: patient ]) }

Actions:
<ul>
    <li>
        <a href="${ ui.pageLink("emr", "orderXray", [ patientId: patient.id ]) }">Order XRay</a>
    </li>
</ul>