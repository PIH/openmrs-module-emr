describe("Test for form validators", function() {
    emrMessages = {
        requiredField: 'requiredFieldMessage',
        dateField: 'dateFieldMessage'
    };
    var validator, field;

    describe("Required fields", function() {
        beforeEach(function() {
            validator = new RequiredFieldValidator();
            field = jasmine.createSpyObj("field", ['value']);
        });

        it("should validate non empty field", function() {
            field.value.andReturn("something");

            var validationMessage = validator.validate(field);
            expect(validationMessage).toBe(null);
        });
        it("should not validate empty field", function() {
            field.value.andReturn("");

            var validationMessage = validator.validate(field);
            expect(validationMessage).toBe('requiredFieldMessage');
        });

        describe("Date fields", function() {
            beforeEach(function() {
                validator = new DateFieldValidator();
                field = jasmine.createSpyObj("field", ['value']);
            });

            it("should validate correct date", function() {
                field.value.andReturn("30/10/2010");

                var validationMessage = validator.validate(field);
                expect(validationMessage).toBe(null);
            });
            it("should not validate incorrect date", function() {
                field.value.andReturn("32/13/2010");

                var validationMessage = validator.validate(field);
                expect(validationMessage).toBe('dateFieldMessage');
            });
            it("should not validate a non date", function() {
                field.value.andReturn("nondate");

                var validationMessage = validator.validate(field);
                expect(validationMessage).toBe('dateFieldMessage');
            });
        })
    });
})