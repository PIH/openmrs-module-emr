/*
 * Base prototype for validators
 */
function FieldValidator() {};
FieldValidator.prototype = {
    constructor: FieldValidator,
    validate: function(field) {
        if(!this.isValid(field.value())) {
            return this.errorMessage;
        }
        return null;
    }
}

function RequiredFieldValidator() {
    this.errorMessage = "This field can't be blank!";
}
RequiredFieldValidator.prototype = new FieldValidator();
RequiredFieldValidator.prototype.constructor = RequiredFieldValidator;
RequiredFieldValidator.prototype.isValid = function(fieldValue) {
    return fieldValue != null && fieldValue.length > 0;
}

var Validators = {
    required: new RequiredFieldValidator()
}