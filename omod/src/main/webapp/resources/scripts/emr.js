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
                url = this.pageLink(opts.provider, opts.page, opts.query);
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

        fragmentActionLink: function(fragmentName, actionName, options) {
            var ret = '/' + OPENMRS_CONTEXT_PATH + '/' + fragmentName + '/' + actionName + '.action';
            return ret += toQueryString(options);
        }

    };

})(jQuery);