function Study(id, name) {
    var api = {};
    api.id = id;
    api.name = name;

    return api;
}

function StudiesViewModel(observables) {
    var api = {};
    api.searchTerm = ko.observable(true);
    api.selectedStudies = ko.observableArray([]);
    api.studies = ko.observableArray([]);

    api.selectStudy = function(study) {
        api.selectedStudies.push(study);
        api.studies.remove( function(item) { return item.id == study.id } );
    };
    api.unselectStudy = function(study) {
        api.studies.push(study);
        api.selectedStudies.remove( function(item) { return item.id == study.id } );
    };

    api.convertedStudies = function() {
        return $.map( api.studies(), function(element) {
            return { "label": element.name, "value": element.id };
        });
    };

    for(var i=0; i<observables.length; i++) {
        api.studies.push( Study(observables[i]["value"],
            observables[i]["label"]) );
    }

    return api;
}

ko.bindingHandlers.autocomplete = {
    init: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
        $(element).autocomplete({
            source: function( request, response ) {
                response($.ui.autocomplete.filter(viewModel.convertedStudies(), request.term));
            },
            select: function( event, ui ) {
                viewModel.selectStudy(Study(ui.item.value, ui.item.label));
                this.value = "";
                return false;
            }
        });
    },
    update: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
    }
};