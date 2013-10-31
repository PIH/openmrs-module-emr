<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeCss("mirebalais", "inpatient.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("emr.app.inpatients.label")}"}
    ];

    jq(function() {
       jq("#inpatients-filterByLocation").change(function(event){

           var selectedItems= "";
           var selectedItemId="";
           jq("select option:selected").each(function(){
               selectedItems = jq(this).text() + "; id=" + this.value;
               selectedItemId =this.value;
               if(parseInt(selectedItemId, 10) > 0){
                   emr.navigateTo({
                       provider: 'emr',
                       page: 'inpatients',
                       query: { ward: selectedItemId }
                   });
               }else{
                   emr.navigateTo({
                       provider: 'emr',
                       page: 'inpatients'
                   });
               }

           });
       });
    });

</script>

<h3 class="inpatient-count">${ ui.message("emr.inpatients.patientCount") }: ${inpatientsList.size()}</h3>
    <div class="inpatient-filter">
        <% if(ward!= null && ward.id>0) { %>
            ${ ui.includeFragment("emr", "field/location", [
                    "id": "inpatients-filterByLocation",
                    "formFieldName": "filterByLocationId",
                    "label": "emr.inpatients.filterByCurrentWard",
                    "initialValue" : ward.id,
                    "withTag": "Admission Location;Transfer Location"
            ] ) }
        <% }else{ %>
            ${ ui.includeFragment("emr", "field/location", [
                "id": "inpatients-filterByLocation",
                "formFieldName": "filterByLocationId",
                "label": "emr.inpatients.filterByCurrentWard",
                "withTag": "Admission Location;Transfer Location"
            ] ) }
        <% } %>
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
