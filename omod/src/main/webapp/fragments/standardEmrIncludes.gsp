<%
	ui.includeJavascript("emr", "jquery-1.8.3.min.js", Integer.MAX_VALUE)
	ui.includeJavascript("emr", "jquery-ui-1.9.2.custom.min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("emr", "underscore-min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("emr", "emr.js")
    ui.includeJavascript("emr", "knockout-2.1.0.js")

    ui.includeCss("emr", "cupertino/jquery-ui-1.9.2.custom.min.css", Integer.MAX_VALUE - 10)
    ui.includeCss("emr", "emr.css")

    // toastmessage plugin: https://github.com/akquinet/jquery-toastmessage-plugin/wiki
    ui.includeJavascript("emr", "jquery.toastmessage.js")
    ui.includeCss("emr", "jquery.toastmessage.css")

%>