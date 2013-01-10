function FieldModel(question, elem) {
    var element = $(elem);

    var model = {};
    model.parentQuestion = question;
    model.isSelected = false;
    model.toggleSelection = function() {
        if(model.isSelected) {
            model.unselect();
        } else {
            model.select();
        }
    };
    model.select = function() {
        model.isSelected = true;
        element.focus();
    };
    model.unselect = function() {
        model.isSelected = false;
        element.blur();
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
    };
    model.element = function() {
        return element;
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
        if(model.isSelected) {
            model.unselect();
        } else {
            model.select();
        }
    };
    model.select = function() {
        model.isSelected = true;
        element.addClass("focused");
        valueElement.text("");
    };
    model.unselect = function() {
        model.isSelected = false;
        element.removeClass("focused");
        valueElement.text(computedValue())
        valueElement.show();
        _.each(model.fields, function(f) {
            f.unselect();
        });
    };
    model.questionLegend = function() {
        return questionLegend;
    }
    model.title = function() {
        return questionTitle;
    };
    model.value = function() {
        return computedValue();
    };
    model.hide = function() {
        element.hide();
    };
    model.show = function() {
        element.show();
    };

    return model;
}

var SectionModel = function(elem) {
    var element = $(elem);
    var title = element.find("span.title").first();

    var model = {};
    model.isSelected = false;
    model.questions = _.map(element.find("fieldset"), function(q) {
        return QuestionModel(model, q);
    });
    model.toggleSelection = function() {
        if(model.isSelected) {
            model.unselect();
        } else {
            model.select();
        }
    };
    model.select = function() {
        model.isSelected = true;
        element.addClass("focused");
        title.addClass("doing");
    };
    model.unselect = function() {
        model.isSelected = false;
        element.removeClass("focused");
        title.removeClass("doing");
        _.each(model.questions, function(q) {
            q.unselect();
        });
    };
    model.moveTitleTo = function(el) {
        var newTitle = $("<li>" + title.text() + "</li>");
        title.remove();
        el.append(newTitle);
        title = newTitle;
    };
    model.title = function() {
        return title;
    }

    return model;
}

var ConfirmationSectionModel = function(elem, regularSections) {
    var element = $(elem);
    var sections = regularSections;
    var title = element.find("span.title").first();

    element.append("<div id='dataCanvas'></div>");
    var dataCanvas = element.find('#dataCanvas').first();

    var showDataForConfirmation = function() {
        dataCanvas.append("<ul></ul>");
        var listElement = dataCanvas.find("ul").first();
        _.each(sections, function(s) {
            _.each(s.questions, function(q) {
                listElement.append("<li><span class='label'>" + q.title() + "</span><span>" + q.value() + "</span></li>");
            })
        });
    };

    var model = {};
    model.isSelected = false;
    model.questions = _.map(element.find("#confirmationQuestion"), function(q) {
        return QuestionModel(model, q);
    });
    model.toggleSelection = function() {
        if(model.isSelected) {
            model.unselect();
        } else {
            model.select();
        }
    };
    model.select = function() {
        model.isSelected = true;
        showDataForConfirmation();
        element.addClass("focused");
        title.addClass("doing");
    };
    model.unselect = function() {
        model.isSelected = false;
        dataCanvas.empty();
        element.removeClass("focused");
        title.removeClass("doing");
    };
    model.moveTitleTo = function(el) {
        var newTitle = $("<li>" + title.text() + "</li>");
        title.remove();
        el.append(newTitle);
        title = newTitle;
    };
    model.title = function() {
        return title;
    }

    return model;
}