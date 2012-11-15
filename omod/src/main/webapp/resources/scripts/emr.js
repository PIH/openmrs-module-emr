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

    return {

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

        showError: function(obj) {
            window.alert("Error: " + obj);
        },

        pageLink: function(providerName, pageName, options) {
            var ret = '/' + OPENMRS_CONTEXT_PATH + '/pages/' + providerName + '/' + pageName + '.page';
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
                    emr.showError(err);
                });
        }

    };

})(jQuery);

var jq = jQuery;
_.templateSettings = {
    interpolate : /{{=(.+?)}}/g ,
    escape : /{{-(.+?)}}/g ,
    evaluate : /{{(.+?)}}/g
};