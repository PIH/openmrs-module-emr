describe("Test for simple form models", function() {

    describe("Unit tests for FieldModel", function() {

        it("should select and unselect the field", function() {
            var fieldModel = new FieldModel();
            fieldModel.element = jasmine.createSpyObj('element', ['focus', 'blur', 'addClass', 'removeClass']);

            fieldModel.toggleSelection();
            expect(fieldModel.element.focus).toHaveBeenCalled();
            expect(fieldModel.element.addClass).toHaveBeenCalledWith("focused");
            expect(fieldModel.isSelected).toBe(true);

            fieldModel.toggleSelection();
            expect(fieldModel.element.blur).toHaveBeenCalled();
            expect(fieldModel.element.removeClass).toHaveBeenCalledWith("error");
            expect(fieldModel.element.removeClass).toHaveBeenCalledWith("focused");
            expect(fieldModel.isSelected).toBe(false);
        });

        it("should state the field is valid", function() {
            var firstValidator = jasmine.createSpyObj('firstValidator', ['validate']);
            var secondValidator = jasmine.createSpyObj('secondValidator', ['validate']);
            firstValidator.validate.andReturn(null);
            secondValidator.validate.andReturn(null);

            var fieldModel = new FieldModel();
            fieldModel.messagesContainer = jasmine.createSpyObj('messagesContainer', ['empty']);
            fieldModel.validators = [firstValidator, secondValidator];

            var isValid = fieldModel.isValid();

            expect(firstValidator.validate).toHaveBeenCalledWith(fieldModel);
            expect(secondValidator.validate).toHaveBeenCalledWith(fieldModel);
            expect(fieldModel.messagesContainer.empty).toHaveBeenCalled();
            expect(isValid).toBe(true);
        });

        it("should state the field is invalid", function() {
            var firstValidator = jasmine.createSpyObj('firstValidator', ['validate']);
            var secondValidator = jasmine.createSpyObj('secondValidator', ['validate']);
            firstValidator.validate.andReturn('Invalid field');
            secondValidator.validate.andReturn(null);

            var fieldModel = new FieldModel();
            fieldModel.element = jasmine.createSpyObj('element', ['addClass']);
            fieldModel.messagesContainer = jasmine.createSpyObj('messagesContainer', ['empty', 'append', 'css']);
            fieldModel.validators = [firstValidator, secondValidator];

            var isValid = fieldModel.isValid();

            expect(firstValidator.validate).toHaveBeenCalledWith(fieldModel);
            expect(secondValidator.validate).toHaveBeenCalledWith(fieldModel);
            expect(fieldModel.messagesContainer.empty).toHaveBeenCalled();
            expect(fieldModel.messagesContainer.append).toHaveBeenCalledWith("Invalid field");
            expect(fieldModel.messagesContainer.css).toHaveBeenCalledWith("display", "inline");
            expect(fieldModel.element.addClass).toHaveBeenCalledWith("error");
            expect(isValid).toBe(false);
        });

    });

    describe("Unit tests for QuestionModel", function() {
        it("should select and unselect the question", function() {
            var questionModel = new QuestionModel();
            var firstField = jasmine.createSpyObj('firstField', ['unselect', 'resetErrorMessages', 'value']);
            var secondField = jasmine.createSpyObj('secondField', ['unselect', 'resetErrorMessages', 'value']);
            questionModel.fields = [firstField, secondField];
            questionModel.element = jasmine.createSpyObj('element', ['addClass', 'removeClass']);
            spyOn(questionModel.questionLi, 'addClass');
            spyOn(questionModel.questionLi, 'removeClass');

            questionModel.toggleSelection();
            expect(questionModel.element.addClass).toHaveBeenCalledWith("focused");
            expect(questionModel.questionLi.addClass).toHaveBeenCalledWith("focused");
            expect(firstField.resetErrorMessages).toHaveBeenCalled();
            expect(secondField.resetErrorMessages).toHaveBeenCalled();
            expect(questionModel.isSelected).toBe(true);

            questionModel.toggleSelection();
            expect(questionModel.element.removeClass).toHaveBeenCalledWith("focused");
            expect(questionModel.questionLi.removeClass).toHaveBeenCalledWith("focused");
            expect(questionModel.isSelected).toBe(false);
            expect(firstField.unselect).toHaveBeenCalled();
            expect(secondField.unselect).toHaveBeenCalled();
        });
    });

    it("should state the question is valid", function() {
        var firstField = jasmine.createSpyObj('firstField', ['isValid']);
        var secondField = jasmine.createSpyObj('firstField', ['isValid']);
        firstField.isValid.andReturn(true);
        secondField.isValid.andReturn(true);

        var questionModel = new QuestionModel();
        questionModel.fields = [firstField, secondField];

        var isValid = questionModel.isValid();

        expect(firstField.isValid).toHaveBeenCalled();
        expect(secondField.isValid).toHaveBeenCalled();
        expect(isValid).toBe(true);
    });

    it("should state the question is invalid", function() {
        var firstField = jasmine.createSpyObj('firstField', ['isValid']);
        var secondField = jasmine.createSpyObj('firstField', ['isValid']);
        firstField.isValid.andReturn(false);
        secondField.isValid.andReturn(true);

        var questionModel = new QuestionModel();
        questionModel.fields = [firstField, secondField];

        var isValid = questionModel.isValid();

        expect(firstField.isValid).toHaveBeenCalled();
        expect(secondField.isValid).toHaveBeenCalled();
        expect(isValid).toBe(false);
    });
})