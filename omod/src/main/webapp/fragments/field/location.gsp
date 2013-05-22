<%
    config.require("label")
    config.require("formFieldName")
    // config supports withTag

    def options;
    if (config.withTag) {
        def tag = config.withTag instanceof String ? context.locationService.getLocationTagByName(config.withTag) : config.withTag
        options = context.locationService.getLocationsHavingAnyTag([ tag ])
    } else {
        options = context.locationService.allLocations
    }
    options = options.collect {
        [ label: ui.format(it), value: it.id, selected: false ]
    }
    options = options.sort { a, b -> a.label <=> b.label }
%>

${ ui.includeFragment("emr", "field/dropDown", [ options: options ] << config) }