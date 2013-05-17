<%
    config.require("id", "field", "concept")

    def concept = conceptService.getConceptByUuid(config.concept)

    def options = []
    concept.getAnswers().each { answer ->
        options.add([label: ui.format(answer.getAnswerConcept()), value: "{&quot;concept&quot;:&quot;${ concept.getUuid() }&quot;, &quot;value_coded&quot;: &quot;${ answer.getAnswerConcept().getUuid() }&quot;}"])
    }

%>

${ ui.includeFragment("emr", "field/" + config.field, [
        label: ui.format(concept),
        formFieldName: config.formFieldName,
        options: options,
        classes: config.classes,
        hideEmptyLabel: config.hideEmptyLabel,
        emptyOptionLabel: config.emptyOptionLabel,
        depends: config.depends,
        dependency: config.dependency,
        observable: config.id
])}