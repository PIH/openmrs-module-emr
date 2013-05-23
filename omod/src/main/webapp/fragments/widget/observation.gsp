<%
    config.require("id", "fieldProvider", "field", "concept", "formFieldName")

    def concept = conceptService.getConceptByUuid(config.concept)

    def options = []
    concept.getAnswers().each { answer ->
        options.add([label: ui.format(answer.getAnswerConcept()), value: answer.getAnswerConcept().getUuid()])
    }
%>

${ ui.includeFragment(config.fieldProvider, "field/" + config.field, [
        label: ui.format(concept),
        formFieldName: "${ config.formFieldName }[${ concept.getUuid() }]",
        options: options,
        classes: config.classes,
        hideEmptyLabel: config.hideEmptyLabel,
        emptyOptionLabel: config.emptyOptionLabel,
        depends: config.depends,
        dependency: config.dependency,
        observable: config.id
])}