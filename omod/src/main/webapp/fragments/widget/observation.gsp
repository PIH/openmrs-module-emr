<%
    config.require("id", "type", "concept")

    def concept = conceptService.getConceptByUuid(config.concept)

    def options = []
    concept.getAnswers().each { answer ->
        options.add([label: ui.format(answer.getAnswerConcept()), value: "{&quot;concept&quot;:&quot;${ concept.getUuid() }&quot;, &quot;value_coded&quot;: &quot;${ answer.getAnswerConcept().getUuid() }&quot;}"])
    }

%>

${ ui.includeFragment("emr", "field/" + config.type, [
        label: ui.format(concept),
        formFieldName: "additionalObservations",
        options: options,
        classes: config.classes,
        hideEmptyLabel: config.hideEmptyLabel,
        depends: config.depends,
        dependency: config.dependency
])}