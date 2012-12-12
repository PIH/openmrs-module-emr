<% if (errorMessage) { %>
    <div id="error-message" class="note error"><i class="icon-remove small"></i>${ ui.message(errorMessage) }</div>
<% } %>

<% if (infoMessage) { %>
    <div id="info-message" class="note success"><i class="icon-ok small"></i>${ ui.message(infoMessage) }</div>
<% } %>