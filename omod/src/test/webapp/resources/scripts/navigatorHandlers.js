describe("Tests for simple form navigation handlers", function() {

    describe("Keyboard handlers", function() {
        var fieldsKeyboardHandler, questionsKeyboardHandler;

        describe("Fields Keyboard handler", function() {
            var firstField, secondField;
            beforeEach(function() {
                firstField = jasmine.createSpyObj('firstField', ['isValid', 'toggleSelection']);
                secondField = jasmine.createSpyObj('secondField', ['isValid', 'toggleSelection']);
                questionsKeyboardHandler = jasmine.createSpyObj('questionsHandler',
                        ['handleUpKey', 'handleDownKey',
                         'selectedQuestion']);
                fieldsKeyboardHandler = new FieldsKeyboardHandler([firstField, secondField], questionsKeyboardHandler);
            });

            it("should delegate up key handling if no selected field", function() {
                firstField.isSelected = false; secondField.isSelected = false;
                questionsKeyboardHandler.handleUpKey.andReturn(true);

                var wasHandled = fieldsKeyboardHandler.handleUpKey();

                expect(questionsKeyboardHandler.handleUpKey).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should not handle up key if there is a selected field", function() {
                firstField.isSelected = false; secondField.isSelected = true;

                var wasHandled = fieldsKeyboardHandler.handleUpKey();

                expect(wasHandled).toBe(false);
            });

            it("should delegate down key handling if no selected field", function() {
                firstField.isSelected = false; secondField.isSelected = false;
                questionsKeyboardHandler.handleDownKey.andReturn(true);

                var wasHandled = fieldsKeyboardHandler.handleDownKey();

                expect(questionsKeyboardHandler.handleDownKey).toHaveBeenCalled();
                expect(wasHandled).toBe(true);
            });
            it("should not handle down key if there is a selected field", function() {
                firstField.isSelected = false; secondField.isSelected = true;

                var wasHandled = fieldsKeyboardHandler.handleDownKey();

                expect(wasHandled).toBe(false);
            });

            it("should switch selection to next field within same question", function() {
                firstField.isSelected = true; secondField.isSelected = false;
                firstField.isValid.andReturn(true);
                questionsKeyboardHandler.selectedQuestion.andReturn({
                    fields: [firstField, secondField]
                });

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
            });
            it("should switch selection to next field within different question", function() {
                firstField.isSelected = true; secondField.isSelected = false;
                firstField.isValid.andReturn(true);

                var firstQuestion = jasmine.createSpyObj('firstQuestion', ['toggleSelection']);
                firstQuestion.fields = [firstQuestion]; firstField.parentQuestion = firstQuestion;
                var secondQuestion = jasmine.createSpyObj('secondQuestion', ['toggleSelection']);
                secondQuestion.fields = [secondQuestion]; secondField.parentQuestion = secondQuestion;
                questionsKeyboardHandler.selectedQuestion.andReturn(firstQuestion);

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to next field if current field is invalid", function() {
                firstField.isSelected = true; secondField.isSelected = false;
                firstField.isValid.andReturn(false);

                var wasHandled = fieldsKeyboardHandler.handleTabKey();

                expect(wasHandled).toBe(true);
            });

            it("should switch selection to previous field within same question", function() {
                firstField.isSelected = false; secondField.isSelected = true;
                secondField.isValid.andReturn(true);
                questionsKeyboardHandler.selectedQuestion.andReturn({
                    fields: [firstField, secondField]
                });

                var wasHandled = fieldsKeyboardHandler.handleShiftTabKey();

                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(firstField.toggleSelection).toHaveBeenCalled();
            });
            it("should switch selection to next field within different question", function() {
                firstField.isSelected = false; secondField.isSelected = true;
                secondField.isValid.andReturn(true);

                var firstQuestion = jasmine.createSpyObj('firstQuestion', ['toggleSelection']);
                firstQuestion.fields = [firstQuestion]; firstField.parentQuestion = firstQuestion;
                var secondQuestion = jasmine.createSpyObj('secondQuestion', ['toggleSelection']);
                secondQuestion.fields = [secondQuestion]; secondField.parentQuestion = secondQuestion;
                questionsKeyboardHandler.selectedQuestion.andReturn(secondQuestion);

                var wasHandled = fieldsKeyboardHandler.handleShiftTabKey();

                expect(secondField.toggleSelection).toHaveBeenCalled();
                expect(firstField.toggleSelection).toHaveBeenCalled();
                expect(secondQuestion.toggleSelection).toHaveBeenCalled();
                expect(firstQuestion.toggleSelection).toHaveBeenCalled();
            });
            it("should not switch selection to previous field if current field is invalid", function() {
                firstField.isSelected = false; secondField.isSelected = true;
                secondField.isValid.andReturn(false);

                var wasHandled = fieldsKeyboardHandler.handleShiftTabKey();

                expect(wasHandled).toBe(true);
            });
        });
    });
})