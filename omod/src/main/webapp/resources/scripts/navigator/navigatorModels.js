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

    var classes = this.element.attr("class");
    if(classes) {
        _.each(classes.split(' '), function(klass) {
            Validators[klass] && this.validators.push(Validators[klass]);
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
        this.messagesContainer.css("display", "inline");
        return false;
    }
    return true;
}
FieldModel.prototype.value = function() {
    var selectedOption = this.element.find('option:selected');
    if(selectedOption.length > 0) {
        return selectedOption.text();
    }
    return this.element.val() ? this.element.val() : "";
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
    var fieldContainers = this.element.find("p");
    this.fields = _.map(fieldContainers, function(container) {
        return new FieldModel($(container).find("input, select").first(), this, $(container).find("span.field-error").first());
    }, this);
    this.valueElement = $("<span></span>");
    var questionLegend = this.element.find('legend').first();
    this.valueElement.appendTo(questionLegend);
    this.questionLi = $('<li><span>' + questionLegend.text() + '</span></li>');
    this.questionLi.appendTo(titleListElem);
    this.fieldSeparator = this.element.attr('field-separator') ? this.element.attr('field-separator') : ' ';
}
QuestionModel.prototype = new SelectableModel();
QuestionModel.prototype.constructor = QuestionModel;
QuestionModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.valueElement.text("");
    this.questionLi.addClass("focused");
    _.each(this.fields, function(field) { field.resetErrorMessages(); });
}
QuestionModel.prototype.unselect = function() {
    SelectableModel.prototype.unselect.apply(this);
    this.valueElement.text( _.map(this.fields, function(field) { return field.value() }, this).join(this.fieldSeparator) );
    this.valueElement.show();
    _.each(this.fields, function(field) { field.unselect(); });
    this.questionLi.removeClass("focused");
}
QuestionModel.prototype.isValid = function() {
    return _.reduce(this.fields, function(memo, field) {
        return field.isValid() && memo;
    }, true);
}
QuestionModel.prototype.title = function() {
    return this.valueElement;
}

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
    this.dataCanvas = $("<div id='dataCanvas'></div>");
    this.element.append(this.dataCanvas);

    this.questions = _.map(this.element.find("#confirmationQuestion"), function(questionElement) {
        return new QuestionModel(questionElement, this);
    }, this);
}
ConfirmationSectionModel.prototype = new SelectableModel();
ConfirmationSectionModel.prototype.constructor = ConfirmationSectionModel;
ConfirmationSectionModel.prototype.select = function() {
    SelectableModel.prototype.select.apply(this);
    this.title.addClass("doing");

    var listElement = $("<ul></ul>");
    this.dataCanvas.append(listElement);
    _.each(this.sections, function(section) {
        _.each(section.questions, function(question) {
            listElement.append("<li><span class='label'>" + question.title().text() + "</span> " + question.valueElement.text() + "</li>");
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