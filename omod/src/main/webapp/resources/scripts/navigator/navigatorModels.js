function FieldModel(question, elem) {
    var element = $(elem);

    var model = {};
    model.parentQuestion = question;
    model.isSelected = false;
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.focus();
        } else {
            element.blur();
        }
    };
    model.value = function() {
        var selectedOption = element.find('option:selected');
        if(selectedOption.length > 0) {
            return selectedOption.text();
        }
        if(element.val()) {
            return element.val();
        }
        return "";
    }

    return model;
}

var QuestionModel = function(section, elem) {
    var element = $(elem);
    var questionLegend = element.find('legend').first();
    var questionTitle = questionLegend.text();
    questionLegend.append("<span></span>");
    var valueElement = questionLegend.find('span').first();
    var computedValue = function() {
        return _.reduce(model.fields, function(memo, f) { return memo + " " + f.value();}, "");
    }

    var model = {};
    model.parentSection = section;
    model.fields = _.map(element.find("input, select"), function(i) {
        return FieldModel(model, i);
    });
    model.isSelected = false;
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.addClass("focused");
            valueElement.text("");
        } else {
            element.removeClass("focused");
            valueElement.text(computedValue())
            valueElement.show();
        }
    };
    model.title = function() {
        return questionTitle;
    }
    model.value = function() {
        return computedValue();
    }

    return model;
}

var SectionModel = function(elem) {
    var element = $(elem);
    var title = element.find("span.title").first();
    element.hide();

    var model = {};
    model.isSelected = false;
    model.questions = _.map(element.find("fieldset"), function(q) {
        return QuestionModel(model, q);
    });
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            element.show();
            title.addClass("focused");
        } else {
            element.hide();
            title.removeClass("focused");
        }
    };
    model.moveTitleTo = function(el) {
        title.detach();
        title.appendTo(el);
    }

    return model;
}

var ConfirmationSectionModel = function(elem, regularSections) {
    var element = $(elem);
    var sections = regularSections;
    var title = element.find("span.title").first();

    element.append("<div id='dataCanvas'></div>");
    var blah = element.find('#dataCanvas').first();
    element.hide();

    var showDataForConfirmation = function() {
        _.each(sections, function(s) {
            _.each(s.questions, function(q) {
                blah.append("<p>" + q.title() + "<span>" + q.value() + "</span></p>");
            })
        });
    };

    var model = {};
    model.isSelected = false;
    model.questions = _.map(element.find("p"), function(q) {
        return QuestionModel(model, q);
    });
    model.toggleSelection = function() {
        model.isSelected = !model.isSelected;
        if(model.isSelected) {
            showDataForConfirmation();
            element.show();
            title.addClass("focused");
        } else {
            blah.empty();
            element.hide();
            title.removeClass("focused");
        }
    };
    model.moveTitleTo = function(el) {
        title.detach();
        title.appendTo(el);
    }

    return model;
}