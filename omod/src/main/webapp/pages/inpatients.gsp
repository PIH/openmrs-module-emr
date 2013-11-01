<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("mirebalais", "inpatient.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.inpatients.label")}"}
    ];

    var WARD_COLUMN_INDEX = 3;

    jq(function() {
       jq("#inpatients-filterByLocation").change(function(event){
           var ward= "";
           var selectedItemId="";
           var inpatientsTable = jq("#active-visits").dataTable();
           jq("select option:selected").each(function(){
               ward = jq(this).text();
               selectedItemId =this.value;
               if (ward.length > 0) {
                   inpatientsTable.fnFilter(ward, WARD_COLUMN_INDEX);
               }else {
                   inpatientsTable.fnFilter('', WARD_COLUMN_INDEX);
               }
               jq("#listSize").text(inpatientsTable.fnSettings().fnRecordsDisplay());
           });
       });
    });

</script>

<h3 class="inpatient-count">${ ui.message("emr.inpatients.patientCount") }: <span id="listSize">${inpatientsList.size()}</span></h3>
    <div class="inpatient-filter">
        ${ ui.includeFragment("emr", "field/location", [
            "id": "inpatients-filterByLocation",
            "formFieldName": "filterByLocationId",
            "label": "emr.inpatients.filterByCurrentWard",
            "withTag": "Admission Location;Transfer Location"
        ] ) }
    </div>

<table id="active-visits" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
    <tr>
        <th>${ ui.message("emr.patient.identifier") }</th>
        <th>${ ui.message("emr.person.name") }</th>
        <th>${ ui.message("emr.inpatients.firstAdmitted") }</th>
        <th>${ ui.message("emr.inpatients.currentWard") }</th>
    </tr>
    </thead>
    <tbody>
    <% if (inpatientsList.size() == 0) { %>
    <tr>
        <td colspan="4">${ ui.message("emr.none") }</td>
    </tr>
    <% } %>
    <% inpatientsList.each { v ->
    %>
        <tr id="visit-${ v[0]}">
            <td>${ v[1] }</td>
            <td>
                <a href="${ ui.pageLink("coreapps", "patientdashboard/patientDashboard", [ patientId: v[0] ]) }">
                    ${ ui.format(v[2] + " " + v[3]) }
                </a>
            </td>
            <td>
                ${ ui.message("ui.i18n.Location.name." + v[6]) }
                <br/>
                <small>
                    ${ ui.format(v[4]) }
                </small>
            </td>
            <td>
                ${ ui.message("ui.i18n.Location.name." + v[9]) }
                <br/>
                <small>
                    ${ ui.format(v[7]) }
                </small>
            </td>
        </tr>
    <% } %>
    </tbody>
</table>

${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#active-visits",
        options: [
                bFilter: true,
                bJQueryUI: true,
                bLengthChange: false,
                iDisplayLength: 10,
                sPaginationType: '\"full_numbers\"',
                bSort: false,
                sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
        ]
]) }
