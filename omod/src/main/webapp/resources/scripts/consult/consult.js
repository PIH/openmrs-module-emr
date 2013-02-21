var mapTypeOrder = [ "SAME-AS", "NARROWER-THAN" ]

function findConceptMapping(concept, sourceName) {
    var matches = _.filter(concept.conceptMappings, function(item) {
        return item.conceptReferenceTerm.conceptSource.name == sourceName
    });
    if (!matches) {
        return "";
    }
    return _.sortBy(matches, function(item) {
        var temp = _.indexOf(mapTypeOrder, item.conceptMapType);
        return temp < 0 ? 9999 : temp;
    })[0].conceptReferenceTerm.code;
};


function ConceptSearchResult(item) {
    var api = _.extend(item, {
        matchedName: item.conceptName.name,
        preferredName: item.nameIsPreferred ? null : item.concept.preferredName,
        code: findConceptMapping(item.concept, "ICD-10-WHO"),
        conceptId: item.concept.id,
        valueToSubmit: function() {
            return "ConceptName:" + item.conceptName.id;
        }
    });
    return api;
}

function FreeTextListItem(text) {
    var api = {
        matchedName: text,
        preferredName: null,
        code: null,
        conceptId: null,
        valueToSubmit: function() {
            return "Non-Coded:" + text;
        }
    };
    return api;
}

function ConsultFormViewModel() {
    var sameDiagnosis = function(d1, d2) {
        if (d1.conceptId && d2.conceptId) {
            return d1.conceptId === d2.conceptId;
        } else {
            return d1.matchedName === d2.matchedName;
        }
    }

    var api = {};

    api.searchTerm = ko.observable();
    api.primaryDiagnosis = ko.observable();
    api.secondaryDiagnoses = ko.observableArray();

    api.isValid = function() {
        return api.primaryDiagnosis() != null;
    }

    api.findSelectedSimilarDiagnosis = function(diagnosis) {
        if (api.primaryDiagnosis() && sameDiagnosis(diagnosis, api.primaryDiagnosis())) {
            return api.primaryDiagnosis()
        } else {
            return _.find(api.secondaryDiagnoses(), function(item) {
                return sameDiagnosis(diagnosis, item);
            });
        }
    }

    api.addDiagnosis = function(diagnosis) {
        if (api.findSelectedSimilarDiagnosis(diagnosis)) {
            return;
        }
        if (api.primaryDiagnosis()) {
            api.secondaryDiagnoses.push(diagnosis);
        } else {
            api.primaryDiagnosis(diagnosis);
        }
    }

    api.removePrimaryDiagnosis = function() {
        var useInstead = api.secondaryDiagnoses.shift();
        api.primaryDiagnosis(useInstead);
    }

    api.removeDiagnosis = function(diagnosis) {
        if (api.primaryDiagnosis() && sameDiagnosis(diagnosis, api.primaryDiagnosis())) {
            api.removePrimaryDiagnosis();
        } else {
            api.secondaryDiagnoses.remove(function(item) {
                return sameDiagnosis(diagnosis, item);
            });
        }
    }

    api.selectedConceptIds = function() {
        var selected = [];
        if (api.primaryDiagnosis()) {
            selected.push(api.primaryDiagnosis().conceptId);
        }
        _.each(api.secondaryDiagnoses(), function(item) {
            selected.push(item.conceptId);
        });
        return selected;
    }

    return api;
}

ko.bindingHandlers.autocomplete = {
    init: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {

        $(element).keypress(function(e) {
            return e.which != 13;
        });
        $(element).autocomplete({
            source: emr.fragmentActionLink("emr", "diagnoses", "search"),
            response: function(event, ui) {
                var items = ui.content;
                var selected = viewModel.selectedConceptIds();
                // remove any already-selected concepts
                for (var i = items.length - 1; i >= 0; --i) {
                    items[i] = ConceptSearchResult(items[i]);
                    if (_.contains(selected, items[i].conceptId)) {
                        items.splice(i, 1);
                    }
                }
                items.push(FreeTextListItem($(element).val()))
            },
            select: function( event, ui ) {
                viewModel.addDiagnosis(ui.item);
                $(this).val("");
                return false;
            }
        })
        .data( "autocomplete" )._renderItem = function( ul, item ) {
            var formatted = allBindingsAccessor().itemFormatter(item);
            return jq('<li>').append('<a>' + formatted + '</a>').appendTo(ul);
        };
    },
    update: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        // It's invoked everytime we update the observable associated to the element
        // In this cases, we assume we'll always want to reset it
        $(element).val("");
    }
}