function SelectableModel(elem) {
    var element = $(elem);
    this.isSelected = false;

    this.parentSelect = function() {
        this.isSelected = true;
        element.addClass("focused");
        this.toggleSelection = this.unselect;
    };

    this.parentUnselect = function() {
        this.isSelected = false;
        element.removeClass("focused");
        this.toggleSelection = this.select;
    };

    return this;
}

function FieldModel(question, elem) {
    var element = $(elem);

    var model = {};
    $.extend(model, SelectableModel(elem));

    model.parentQuestion = question;
    model.select = function() {
        model.parentSelect();
        element.focus();
    };

    model.unselect = function() {
        model.parentUnselect();
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

    model.toggleSelection = model.select;

    return model;
}

function QuestionModel(section, elem) {
    var element = $(elem);

    var questionLegend = element.find('legend').first();
    var questionTitle = questionLegend.text();
    questionLegend.append("<span></span>");
    var valueElement = questionLegend.find('span').first();
    var fieldSeparator = element.attr('field-separator') ? element.attr('field-separator') : ' ';
    var computedValue = function() {
        return _.map(model.fields, function(f) { return f.value() }).join(fieldSeparator);
    };
    var questionLi;

    var model = {};

    $.extend(model, SelectableModel(elem));

    model.parentSection = section;
    model.fields = _.map(element.find("input, select"), function(i) {
        return FieldModel(model, i);
    });

    model.select = function() {
        model.parentSelect();
        valueElement.text("");
        if(questionLi) {
            questionLi.addClass("focused");
        }
    };
    model.unselect = function() {
        model.parentUnselect();
        valueElement.text(computedValue())
        valueElement.show();
        _.each(model.fields, function(f) {
            f.unselect();
        });
        if(questionLi) {
            questionLi.removeClass("focused");
        }
    };

    model.questionLegend = function() {
        return questionLegend;
    }
    model.questionLi = function() {
        return questionLi;
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
    model.moveTitleTo = function(elem) {
      questionLi = $('<li><span>' + questionTitle + '</span></li>');
      elem.append(questionLi);
    };

    model.toggleSelection = model.select;

    return model;
}

function SectionMixin(elem) {
    $.extend(this, SelectableModel(elem));

    var element = $(elem);
    this.select = function() {
        this.parentSelect();
        this
    }
    return this;
}

function SectionModel(elem) {
    var element = $(elem);
    var title = element.find("span.title").first();

    var model = {};

    $.extend(model, SelectableModel(elem));

    model.questions = _.map(element.find("fieldset"), function(q) {
        return QuestionModel(model, q);
    });

    model.select = function() {
        model.parentSelect();
        title.addClass("doing");
    };

    model.unselect = function() {
        model.parentUnselect();
        title.removeClass("doing");
        _.each(model.questions, function(q) {
            q.unselect();
        });
    };

    model.moveTitleTo = function(el) {
        var newTitle = $("<li><span>" + title.text() + "</span></li>");
        var list = $("<ul></ul>");
        _.each(model.questions, function(q) {
            q.moveTitleTo(list);
        });
        newTitle.append(list);
        title.remove();
        el.append(newTitle);
        title = newTitle;
    };
    model.title = function() {
        return title;
    };

    model.toggleSelection = model.select;

    return model;
}

function ConfirmationSectionModel(elem, regularSections) {
    var element = $(elem);
    var sections = regularSections;
    var title = element.find("span.title").first();

    var dataCanvas = $("<div id='dataCanvas'></div>");
    element.append(dataCanvas);

    var showDataForConfirmation = function() {
        dataCanvas.append("<ul></ul>");
        var listElement = dataCanvas.find("ul").first();
        _.each(sections, function(s) {
            _.each(s.questions, function(q) {
                listElement.append("<li><span class='label'>" + q.title() + "</span> <span>" + q.value() + "</span></li>");
            })
        });
    };

    var model = {};
    $.extend(model, SelectableModel(elem));

    model.questions = _.map(element.find("#confirmationQuestion"), function(q) {
        return QuestionModel(model, q);
    });

    model.select = function() {
        model.parentSelect();
        title.addClass("doing");
        showDataForConfirmation();
    };

    model.unselect = function() {
        model.parentUnselect();
        title.removeClass("doing");
        _.each(model.questions, function(q) {
            q.unselect();
        });
        dataCanvas.empty();
    };

    model.moveTitleTo = function(el) {
        var newTitle = $("<li><span>" + title.text() + "</span></li>");
        title.remove();
        el.append(newTitle);
        title = newTitle;
    };

    model.title = function() {
        return title;
    };

    model.toggleSelection = model.select;

    return model;
}