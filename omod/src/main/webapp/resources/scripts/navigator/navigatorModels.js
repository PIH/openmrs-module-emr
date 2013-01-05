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