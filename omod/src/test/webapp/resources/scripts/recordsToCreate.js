describe("Tests to create medical records", function() {

    function verifySelectedRecordsToBeCreated(quantity) {
        for (var i = 0; i < quantity; i++) {
            expect(viewModel.recordsToCreate()[i].selected()).toBe(true);
        }
        for (var i = quantity; i < viewModel.recordsToCreate().length; i++) {
            expect(viewModel.recordsToCreate()[i].selected()).toBe(false);
        }
    }

    var thirdRecord = RecordRequestModel(3, "Darius", 1, null, "Lacoline", "12:34 pm");
    var secondRecord = RecordRequestModel(2, "Mark", 2, null, "Lacoline", "12:34 pm");

    var recordsToPull = [
        RecordRequestModel(1, "Alex", 3, null, "Mirebalais", "12:34 pm"),
        secondRecord,
        thirdRecord,
        RecordRequestModel(4, "Neil", 4, null, "Lacoline", "12:34 pm"),
        RecordRequestModel(5, "Mario", 5, null, "Lacoline", "12:34 pm"),
        RecordRequestModel(6, "Renee", 6, null, "Lacoline", "12:34 pm"),
        RecordRequestModel(7, "Ellen", 7, null, "Lacoline", "12:34 pm"),
        RecordRequestModel(8, "Mike", 8, null, "Lacoline", "12:34 pm")
    ];

    var viewModel = CreateRequestsViewModel(recordsToPull);

    it("should select two records and then unselect one", function() {
        viewModel.selectRecordToBeCreated(secondRecord);
        viewModel.selectRecordToBeCreated(thirdRecord);
        expect(viewModel.recordsToCreate()[1].selected()).toBe(true);
        expect(viewModel.recordsToCreate()[2].selected()).toBe(true);

        viewModel.selectRecordToBeCreated(thirdRecord);
        expect(viewModel.recordsToCreate()[1].selected()).toBe(true);
        expect(viewModel.recordsToCreate()[2].selected()).toBe(false);

    });

});