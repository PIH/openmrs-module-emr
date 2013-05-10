<%
    config.require("label")
    def date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
%>

<p id="${config.uuid}" style="display: none">
    <label for="${ config.id }-field">${ ui.message(config.label) }</label>
    <input type="date" id="${ config.id }-field" name="${ config.id }" value="${date}"  />
</p>