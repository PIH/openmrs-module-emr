describe("Retrospective Checkin", function() {

    var viewModel;

    beforeEach(function() {
        viewModel = RetrospectiveCheckinViewModel();
    })

    it("should find a patient", function() {
        spyOn($, "ajax").andCallFake(function(params) {
           params.success(
               $.parseJSON('[' +
                   '{"patientId":"12","gender":"M","age":"47","birthdate":"09-Aug-1965","birthdateEstimated":"false",' +
                   '"preferredName":{"givenName":"Alberto","middleName":"","familyName":"Dummont","familyName2":"","fullName":"Alberto Dummont"},' +
                   '"primaryIdentifiers":[{"identifier":"TT309R"}]}]'));
        });
        viewModel.patientIdentifier("TT309R");
        expect(viewModel.patientName()).toBe("Alberto Dummont");
    });


    it("should not find a patient", function() {
        spyOn($, "ajax").andCallFake(function(params) {
            params.success($.parseJSON("[]"));
        });

        viewModel.patientIdentifier("TT012345");
        expect(viewModel.patientName()).toBe(undefined);
    });
});