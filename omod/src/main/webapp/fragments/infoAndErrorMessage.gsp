<%
    ui.includeJavascript("emr", "infoAndErrorMessage.js")
%>

<% if (errorMessage) { %>
    <div id="error-message" class="note error">
        <div class="icon"><i class="icon-remove medium"></i></div>
        <div class="text">
            <p>${ ui.message(errorMessage) }</p>
        </div>
        <div class="close-icon"><i class="icon-remove"></i></div>
    </div>
<% } %>

<% if (infoMessage) { %>
    <div id="info-message" class="note success">
        <div class="icon"><i class="icon-ok medium"></i></div>
        <div class="text">
            <p>${ ui.message(infoMessage) }</p>
        </div>
        <div class="close-icon"><i class="icon-remove"></i></div>
    </div>
<% } %>