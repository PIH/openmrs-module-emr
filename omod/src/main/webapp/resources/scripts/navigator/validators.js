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