<%
    ui.includeJavascript("emr", "infoAndErrorMessage.js")
%>

<div id="error-message" class="note error" <% if (!errorMessage) { %> style="display: none" <% } %>>
    <div class="icon"><i class="icon-remove medium"></i></div>
    <div class="text">
        <% if (errorMessage) { %>
            <p>${ ui.message(errorMessage) }</p>
        <% } %>
    </div>
    <div class="close-icon"><i class="icon-remove"></i></div>
</div>

<div id="info-message" class="note success" <% if (!infoMessage) { %> style="display: none" <% } %>>
    <div class="icon"><i class="icon-ok medium"></i></div>
    <div class="text">
        <% if (infoMessage) { %>
            <p>${ ui.message(infoMessage) }</p>
        <% } %>
    </div>
    <div class="close-icon"><i class="icon-remove"></i></div>
</div>