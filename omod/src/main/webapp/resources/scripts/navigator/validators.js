/*
 * Base prototype for validators
 */
function FieldValidator() {};
FieldValidator.prototype = {
    constructor: FieldValidator,
    validate: function(field) {
        if(!this.isValid(field.value())) {
            var message = _.template($('#'+this.messageTemplateId).html());
            return message();
        }
        return null;
    }
}

function RequiredFieldValidator() {
    this.messageTemplateId = "requiredFieldMessage";
}
RequiredFieldValidator.prototype = new FieldValidator();
RequiredFieldValidator.prototype.constructor = RequiredFieldValidator;
RequiredFieldValidator.prototype.isValid = function(fieldValue) {
    return fieldValue != null && fieldValue.length > 0;
}


function DateFieldValidator() {
    this.messageTemplateId = "dateFieldMessage";
}
DateFieldValidator.prototype = new FieldValidator();
DateFieldValidator.prototype.constructor = DateFieldValidator;
DateFieldValidator.prototype.isValid = function(fieldValue) {
    if(fieldValue && fieldValue.length > 0) {
        var dateRegex = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
        var regexResult = dateRegex.exec(fieldValue);
        if(!regexResult) {
            return false;
        }
        var day=regexResult[1], month=regexResult[2], year=regexResult[3];
        if(day < 1 || day > 31 || month < 1 || month > 12) return false;
    }
    return true;
}

var Validators = {
    required: new RequiredFieldValidator(),
    date: new DateFieldValidator()
}