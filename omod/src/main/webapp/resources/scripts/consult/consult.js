function ConceptSearchResult(obj) {
    var api = _.extend(obj, {
        valueToSubmit: function() {
            return "ConceptName:" + api.conceptName.id;
        }
    });
    return api;
}

function ConsultFormViewModel() {
    var api = {};

    api.searchTerm = ko.observable();
    api.primaryDiagnosis = ko.observable();
    api.secondaryDiagnoses = ko.observableArray();

    api.isValid = function() {
        return api.primaryDiagnosis() != null;
    }

    api.findSelectedSimilarDiagnosis = function(diagnosis) {
        if (api.primaryDiagnosis() && api.primaryDiagnosis().concept.id == diagnosis.concept.id) {
            return api.primaryDiagnosis()
        } else {
            return _.findWhere(api.secondaryDiagnoses(), { "concept.id": diagnosis.concept.id});
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
        if (api.primaryDiagnosis().conceptId == diagnosis.conceptId) {
            api.removePrimaryDiagnosis();
        } else {
            api.secondaryDiagnoses.remove(function(item) {
                return item.conceptId == diagnosis.conceptId;
            });
        }
    }

    api.selectedConceptIds = function() {
        var selected = [];
        if (api.primaryDiagnosis()) {
            selected.push(api.primaryDiagnosis().concept.id);
        }
        _.each(api.secondaryDiagnoses(), function(item) {
            selected.push(item.concept.id);
        });
        return selected;
    }

    return api;
}

ko.bindingHandlers.autocomplete = {
    init: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {

        $(element).autocomplete({
            source: emr.fragmentActionLink("emr", "diagnoses", "search"),
            response: function(event, ui) {
                var items = ui.content;
                var selected = viewModel.selectedConceptIds();
                // remove any already-selected concepts
                for (var i = items.length - 1; i >= 0; --i) {
                    if (_.contains(selected, items[i].concept.id)) {
                        items.splice(i, 1);
                    }
                }
            },
            select: function( event, ui ) {
                viewModel.addDiagnosis(ConceptSearchResult(ui.item));
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