

jq(document).ready( function() {

    // set up tabs
    jq("#tabs").tabs();
    jq('#tabs').tabs('select', '#tab-${ activeTab }');

    // set up models for the tables
    var pullRequestsViewModel = PullRequestsViewModel([]);
    ko.applyBindings(pullRequestsViewModel, document.getElementById('pullrequest'));

    var createRequestsViewModel = CreateRequestsViewModel([]);
    ko.applyBindings(createRequestsViewModel, document.getElementById('createrequest'));

    var assignedPullRequestsViewModel = AssignedPullRequestsViewModel([]);
    ko.applyBindings(assignedPullRequestsViewModel, document.getElementById('assignedpullrequest'));

    var assignedCreateRequestsViewModel = AssignedCreateRequestsViewModel([]);
    ko.applyBindings(assignedCreateRequestsViewModel, document.getElementById('assignedcreaterequest'));

    // load the tables
    pullRequestsViewModel.load();
    createRequestsViewModel.load()
    assignedCreateRequestsViewModel.load();
    assignedPullRequestsViewModel.load();


    // handle entering bar codes
    jq(".mark-as-pulled").submit(function (e) {

        e.preventDefault();

        var identifier = jq.trim(jq(this).children('.mark-as-pulled-identifier').val());

        if (identifier) {
            jq.ajax({
                url: emr.fragmentActionLink("emr", "paperrecord/archivesRoom", "markPaperRecordRequestAsSent"),
                data: { identifier: identifier },
                dataType: 'json',
                type: 'POST'
            })
                    .success(function(data) {
                        // clear out the input box
                        jq('.mark-as-pulled-identifier:visible').val('');

                        // reload the lists
                        pullRequestsViewModel.load();
                        createRequestsViewModel.load()
                        assignedCreateRequestsViewModel.load();
                        assignedPullRequestsViewModel.load();

                        emr.successAlert(data.message);
                    })
                    .error(function(xhr, status, err) {
                        jq('.mark-as-pulled-identifier:visible').val('');
                        emr.handleError(xhr);
                    })
        }
    });

    // if an alphanumeric character is pressed, send focus to the appropriate mark-as-pulled-identifier input box
    jq(document).keydown(function(event) {
        if (event.which > 47 && event.which < 91) {
            jq(".mark-as-pulled-identifier:visible").focus();
        }
    })

});
