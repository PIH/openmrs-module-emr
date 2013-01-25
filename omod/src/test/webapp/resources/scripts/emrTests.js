describe("Tests of emr functions", function() {

    it("should display success message", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.successMessage("some success message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'success', position : 'top-right', text : 'some success message' });

    });

    it("should display error message", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.errorMessage("some error message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'error', position : 'top-right', text : 'some error message' });

    });

    it("should display success alert", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.successAlert("some success message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'success', position : 'top-right', text : 'some success message', close: null });

    });

    it("should display error alert", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.errorAlert("some error message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'error', position : 'top-right', text : 'some error message', close: null });

    });

});

