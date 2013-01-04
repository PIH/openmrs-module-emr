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

    return model;
}

var QuestionModel = function(section, elem) {
    var element = $(elem);
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
        } else {
            element.removeClass("focused");
        }
    }

    return model;
}

var SectionModel = function(elem) {
    var element = $(elem);
    var title = element.find("span.title").text();
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
        } else {
            element.hide();
        }
    }

    return model;
}