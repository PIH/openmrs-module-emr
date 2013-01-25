<%
	ui.includeJavascript("emr", "jquery-1.8.3.min.js", Integer.MAX_VALUE)
	ui.includeJavascript("emr", "jquery-ui-1.9.2.custom.min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("emr", "underscore-min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("emr", "knockout-2.1.0.js", Integer.MAX_VALUE - 15)
    ui.includeJavascript("emr", "emr.js", Integer.MAX_VALUE - 15)

    ui.includeCss("emr", "cupertino/jquery-ui-1.9.2.custom.min.css", Integer.MAX_VALUE - 10)

    // toastmessage plugin: https://github.com/akquinet/jquery-toastmessage-plugin/wiki
    ui.includeJavascript("emr", "jquery.toastmessage.js", Integer.MAX_VALUE - 20)
    ui.includeCss("emr", "jquery.toastmessage.css", Integer.MAX_VALUE - 20)

    // simplemodal plugin: http://www.ericmmartin.com/projects/simplemodal/
    ui.includeJavascript("emr", "jquery.simplemodal.1.4.4.min.js", Integer.MAX_VALUE - 20)

%>