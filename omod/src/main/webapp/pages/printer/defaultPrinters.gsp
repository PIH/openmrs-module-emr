
<%
    ui.decorateWith("emr", "standardEmrPage")

    def idCardPrinterOptions = []

    idCardPrinters.sort { it.name }.each {
        idCardPrinterOptions.push([ label: ui.format(it.name) + ' (' + ui.format(it.physicalLocation) + ')', value: it.id ])
    }

    def labelPrinterOptions = []

    labelPrinters.sort { it.name }.each {
        labelPrinterOptions.push([ label: ui.format(it.name) + ' (' + ui.format(it.physicalLocation) + ')', value: it.id ])
    }
%>

<script type="text/javascript">
    jq(function() {

        jq('select').change(function() {

            var name = jq(this).attr('name').split("-");

            var data = { location: name[1],
                         type: name[0],
                         printer: jq(this).val() };

            jq.ajax({
                url: '${ ui.actionLink('emr','printer/defaultPrinters','saveDefaultPrinter') }',
                data: data,
                dataType: 'json',
                type: 'POST'
            })
                    .success(function(data) {
                        emr.successMessage('${ ui.message("emr.printer.defaultUpdate") }');
                    })
                    .error(function(xhr, status, err) {
                        emr.errorMessage('${ ui.message("emr.printer.error.defaultUpdate") }')
                    })

        });
    });


</script>


<h3>${ ui.message("emr.printer.defaultPrinters") }</h3>

<table class="bordered">

    <tr>
        <th>
            ${ ui.message("emr.printer.defaultPrinterTable.loginLocation.label") }
        </th>
        <th>
            ${ ui.message("emr.printer.defaultPrinterTable.idCardPrinter.label") }
        </th>
        <th>
            ${ ui.message("emr.printer.defaultPrinterTable.labelPrinter.label") }
        </th>
    </tr>


    <% locationsToPrintersMap.sort { it.key.name }.each {   %>
            <tr>
                <td>${ ui.format(it.key.name) }</td>

                <td>
                    ${ ui.includeFragment("emr", "field/dropDown", [ id: "ID_CARD-" + it.key.id, formFieldName: "ID_CARD-" + it.key.id, emptyOptionLabel: ui.message("emr.printer.defaultPrinterTable.emptyOption.label"), initialValue: it.value.idCardPrinter?.id ?: '', options: idCardPrinterOptions ])}
                </td>

                <td>
                    ${ ui.includeFragment("emr", "field/dropDown", [ id: "LABEL-" + it.key.id, formFieldName: "LABEL-" + it.key.id, emptyOptionLabel: ui.message("emr.printer.defaultPrinterTable.emptyOption.label"), initialValue: it.value.labelPrinter?.id ?: '', options: labelPrinterOptions ])}
                </td>
            </tr>


    <% } %>



</table>