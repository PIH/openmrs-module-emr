describe("Tests of emr functions", function() {

    it("should display success message", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.successMessage("some success message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'success', text : 'some success message' });

    });

    it("should display error message", function() {

        var jqueryWithSpy = jq();
        emr.setJqObject(jqueryWithSpy);

        spyOn(jqueryWithSpy,'toastmessage').andCallThrough();  // call through just to make sure the underlying plugin doesn't throw an error

        emr.errorMessage("some error message");

        expect(jqueryWithSpy.toastmessage).toHaveBeenCalledWith('showToast', { type : 'error', text : 'some error message' });

    });

});

