var emr = (function($) {

    var toQueryString = function(options) {
        var ret = "?";
        if (options) {
            for (key in options) {
                ret += key + '=' + options[key] + '&';
            }
        }
        return ret;
    };

    var jqObject = $();

    return {

        // just used in testing so we can replace the instance with a mock or spy as needed
        setJqObject: function(jqueryInstanceToSet)  {
            jqObject = jqueryInstanceToSet;
        },

        navigateTo: function(opts) {
            var url = opts.url;
            if (opts.page) {
                var provider = opts.provider;
                if (provider == null) {
                    provider = "emr"
                }
                url = this.pageLink(provider, opts.page, opts.query);
            }
            location.href = url;
        },

        pageLink: function(providerName, pageName, options) {
            var ret = '/' + OPENMRS_CONTEXT_PATH + '/' + providerName + '/' + pageName + '.page';
            return ret + toQueryString(options);
        },

        resourceLink: function(providerName, resourceName) {
            if (providerName == null)
                providerName = '*';
            return '/' + OPENMRS_CONTEXT_PATH + '/ms/uiframework/resource/' + providerName + '/' + resourceName;
        },

        fragmentActionLink: function(providerName, fragmentName, actionName, options) {
            var ret = '/' + OPENMRS_CONTEXT_PATH + '/' + providerName + '/' + fragmentName + '/' + actionName + '.action';
            return ret += toQueryString(options);
        },

        /*
         * opts should contain:
         *   provider (defaults to 'emr')
         *   fragment
         *   action
         *   query, e.g. { q: "bob", checkedInAt: 5 }
         *   resultTarget e.g. '#search-results'
         *   resultTemplate (should be an underscore template)
         */
        ajaxSearch: function(opts) {
            var provider = opts.provider;
            if (!provider) {
                provider = 'emr';
            }
            var url = this.fragmentActionLink(provider, opts.fragment, opts.action);
            var target = $(opts.resultTarget);
            $.getJSON(url, opts.query)
                .success(function(data) {
                    target.html('');
                    jq.each(data, function(i, result) {
                        jq(opts.resultTemplate(result)).appendTo(target);
                    });
                })
                .error(function(err) {
                    emr.errorMessage(err);
                });
        },

        successMessage: function(message) {
            jqObject.toastmessage( 'showToast', { type: 'success',
                                              position: 'top-right',
                                              text:  message } );
        },

        errorMessage: function(message) {
            jqObject.toastmessage( 'showToast', { type: 'error',
                                              position: 'top-right',
                                              text:  message } );
        },

        successAlert: function(message, options) {
            jqObject.toastmessage( 'showToast', { type: 'success',
                position: 'middle-center',
                sticky: true,
                text:  message,
                close: options && options.close ? options.close : null } );
        },

        errorAlert: function(message, options) {
            jqObject.toastmessage( 'showToast', { type: 'error',
                position: 'middle-center',
                sticky: true,
                text:  message,
                close: options && options.close ? options.close : null } )
        }

    };

})(jQuery);

var jq = jQuery;
_.templateSettings = {
    interpolate : /{{=(.+?)}}/g ,
    escape : /{{-(.+?)}}/g ,
    evaluate : /{{(.+?)}}/g
};