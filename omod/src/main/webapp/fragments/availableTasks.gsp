
${ ui.message("emr.actions") }:
<ul>
    <% availableTasks.each { %>
    <li><a href="/${ contextPath }/${ it.getUrl(emrContext) }">${ it.getLabel(emrContext) }</a></li>
    <% } %>
</ul>