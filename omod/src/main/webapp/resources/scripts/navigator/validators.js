var Validators = {
    required: function(fieldValue) {
        var isValid = fieldValue != null && (fieldValue.length > 0);
        if(isValid) {
            $("#error-message p").text("");
        } else {
            $("#error-message p").text("This field can't be blank!");
        }
        return isValid;
    }
};

function fieldIsValid(field) {
    var classes = field.attr('class');
    var isValid = true;
    if(classes) {
        isValid = _.reduce(classes.split(' '), function(memo, className) {
            var isValid = true;
            if(Validators[className]) {
                isValid = Validators[className](field.val());
            }
            return memo && isValid;
        }, true);
    }
    if(isValid) {
        $("#error-message").css("display", "none");
    } else {
        $("#error-message").css("display", "inline-block");
    }
    return isValid;
}