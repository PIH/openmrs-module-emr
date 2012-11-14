<% if (errorMessage) { %>
    <div id="error-message">${ ui.message(errorMessage) }</div>
<% } %>

<% if (infoMessage) { %>
    <div id="info-message">${ ui.message(infoMessage) }</div>
<% } %>