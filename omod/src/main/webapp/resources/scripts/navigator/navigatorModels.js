/*
 * Base prototype for selectable models
 */
function SelectableModel(elem) {
    this.element = $(elem);
    this.isSelected = false;
    this.toggleSelection = this.select;
}
SelectableModel.prototype = {
    constructor: SelectableModel,
    select: function() {
        this.isSelected = true;
        this.element.addClass("focused");
        this.toggleSelection = this.unselect;
    },
    unselect: function() {
        this.isSelected = false;
        this.element.removeClass("focused");
        this.toggleSelection = this.select;
    },
    enable: function() {
        this.element.removeAttr('disabled');
    },
    disable: function() {
        this.element.attr('disabled', 'true');
    }
}

/*
 * Prototype for fields
 */
function FieldModel(elem, parentQuestion, messagesContainer) {
    SelectableModel.apply(this, [elem]);
    this.parentQuestion = parentQuestion;
    this.messagesContainer = messagesContainer;
    this.validators = [];
    this.exitHandlers = [];

    var classes = this.element.attr("class");
    if(classes) {
        _.each(classes.split(' '), function(klass) {
            Validators[klass] && this.validators.push(Validators[klass]);
        }, this);
        _.each(classes.split(' '), function(klass) {
            ExitHandlers[klass] && this.exitHandlers.push(ExitHandlers[klass]);
        }, this);
    }
}
FieldModel.prototype = new SelectableModel();
FieldModel.prototype.constructor = FieldModel;
FieldModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.element.focus();
}
FieldModel.prototype.unselect = function() {
    SelectableModel.prototype.unselect.apply(this);
    this.element.removeClass("error");
    this.element.blur();
}

FieldModel.prototype.enable = function() {
    SelectableModel.prototype.enable.apply(this);
}

FieldModel.prototype.disable = function() {
    SelectableModel.prototype.disable.apply(this);
}

FieldModel.prototype.isValid = function() {
    var validationMessages = _.reduce(this.validators, function(memo, validator) {
        var validationMessage = validator.validate(this);
        if (validationMessage) {
            memo.push(validationMessage);
        }
        return memo;
    }, [], this);

    this.messagesContainer.empty();
    if(validationMessages.length > 0) {
        _.each(validationMessages, function(message) {
           this.messagesContainer.append(message);
        }, this);
        this.element.addClass("error");
        this.messagesContainer.show();
        return false;
    }
    return true;
}

FieldModel.prototype.onExit = function ()  {
    var exit = _.reduce(this.exitHandlers, function(memo, exitHandler) {
        return memo && exitHandler.handleExit(this);
    }, true, this);

    return exit;
}

FieldModel.prototype.value = function() {
    var selectedOption = this.element.find('option:selected');
    if(selectedOption.length > 0) {
        return selectedOption.text();
    }
    return this.element.val() ? this.element.val() : "";
}
FieldModel.prototype.displayValue = function() {
    var value = this.value();
    if (value) {
        var extra = _.map(this.element.parent().find(".append-to-value"), function(item) { return $(item).html() }).join(" ");
        return extra ? (value + " <span class='after-value'>" + extra + "</span>") : value;
    } else {
        return "";
    }
}
FieldModel.prototype.resetErrorMessages = function() {
    this.messagesContainer.empty();
    this.element.removeClass("error");
}

/*
 * Prototype for questions
 */
function QuestionModel(elem, section, titleListElem) {
    SelectableModel.apply(this, [elem]);
    this.parentSection = section;
    var fieldContainers = this.element.find("p").has("input, select");
    this.fields = _.map(fieldContainers, function(container) {
        return new FieldModel($(container).find("input, select").first(), this, $(container).find("span.field-error").first());
    }, this);
    this.questionLegend = this.element.find('legend').first();
    this.questionLi = $('<li><span>' + this.questionLegend.text() + '</span></li>');
    this.questionLi.appendTo(titleListElem);
    this.fieldSeparator = this.element.attr('field-separator') ? this.element.attr('field-separator') : ' ';
}
QuestionModel.prototype = new SelectableModel();
QuestionModel.prototype.constructor = QuestionModel;
QuestionModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.valueAsText = "";
    this.questionLi.addClass("focused");
    _.each(this.fields, function(field) { field.resetErrorMessages(); });
}
QuestionModel.prototype.unselect = function() {
    SelectableModel.prototype.unselect.apply(this);
    this.valueAsText = _.map(this.fields, function(field) { return field.displayValue() }, this).join(this.fieldSeparator);
    _.each(this.fields, function(field) { field.unselect(); });
    this.questionLi.removeClass("focused");
}
QuestionModel.prototype.isValid = function() {
    return _.reduce(this.fields, function(memo, field) {
        return field.isValid() && memo;
    }, true);
}
QuestionModel.prototype.title = function() {
    return this.questionLegend;
}

/*
 * Specific model for the Yes/No confirmation question
 */
function ConfirmationQuestionModel(elem, section, titleListElem) {
    QuestionModel.apply(this, [elem, section, titleListElem]);

    this.confirm = _.find(this.fields, function (field) {
        return field.element.hasClass('confirm');
    });
    this.cancel =_.find(this.fields, function (field) {
        return field.element.hasClass('cancel');
    });

    // return to beginning of form if user hits cancel
    if (this.cancel) {
        this.cancel.element.click(function() {
            section.sections[0].title.click();
        });
    }
}
ConfirmationQuestionModel.prototype = new QuestionModel();
ConfirmationQuestionModel.prototype.constructor = ConfirmationQuestionModel;

/*
 * Prototype for sections
 */
function SectionModel(elem, formMenuElem) {
    SelectableModel.apply(this, [elem]);

    var title = this.element.find("span.title").first();
    var newTitle = $("<li><span>" + title.text() + "</span></li>");
    var questionsTitlesList = $("<ul></ul>");
    newTitle.append(questionsTitlesList);
    formMenuElem.append(newTitle);
    title.remove();

    this.title = newTitle;
    this.questions = _.map(this.element.find("fieldset"), function(questionElement) {
        return new QuestionModel(questionElement, this, questionsTitlesList);
    }, this);
}
SectionModel.prototype = new SelectableModel();
SectionModel.prototype.constructor = SectionModel;
SectionModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.title.addClass("doing");
}
SectionModel.prototype.unselect = function() {
    SelectableModel.prototype.unselect.apply(this);
    this.title.removeClass("doing");
    _.each(this.questions, function(question) { question.unselect() });
}
SectionModel.prototype.isValid = function() {
    return _.reduce(this.questions, function(memo, question) {
        return question.isValid() && memo;
    }, true);
}


function ConfirmationSectionModel(elem, formMenuElem, regularSections) {
    SelectableModel.apply(this, [elem]);
    this.sections = regularSections;

    var title = this.element.find("span.title").first();
    this.title = $("<li><span>" + title.text() + "</span></li>");
    formMenuElem.append(this.title);
    title.remove();
    this.dataCanvas = this.element.find('#dataCanvas');
    this.questions = [ new ConfirmationQuestionModel(this.element.find("#confirmationQuestion"), this) ];
}
ConfirmationSectionModel.prototype = new SelectableModel();
ConfirmationSectionModel.prototype.constructor = ConfirmationSectionModel;
ConfirmationSectionModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.title.addClass("doing");

    // scan through the form and confirm that at least one of the fields has a value
    var hasData =_.some(this.sections, function (section) {
        return _.some(section.questions, function (question) {
            return _.some(question.fields, function (field) {
                return (field.value() && field.value().length > 0)
            })
        })
    })

    if (!hasData) {
        this.questions[0].confirm.disable();
        if (this.element.find("#emptyFormError")) {
            this.element.find("#emptyFormError").show();
        }
    }
    else {
        this.questions[0].confirm.enable();
        if (this.element.find("#emptyFormError")) {
            this.element.find("#emptyFormError").hide();
        }
    }

    var listElement = $("<ul></ul>");
    this.dataCanvas.append(listElement);
    _.each(this.sections, function(section) {
        _.each(section.questions, function(question) {
            listElement.append("<li><span class='label'>" + question.title().text() + ":</span> <strong>" + (question.valueAsText &&  !/^\s*$/.test(question.valueAsText) ? question.valueAsText : "--") + "</strong></li>");
        })
    });
}
ConfirmationSectionModel.prototype.unselect = function() {
    SelectableModel.prototype.unselect.apply(this);
    this.title.removeClass("doing");
    this.dataCanvas.empty();
    _.each(this.questions, function(question) { question.unselect() });
}
ConfirmationSectionModel.prototype.isValid = function() {
    return true;
}