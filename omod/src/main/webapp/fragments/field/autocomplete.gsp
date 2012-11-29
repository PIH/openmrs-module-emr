<%
    // config supports "label"
    // config.require("label")

    //config supports "placeholder"
    //config.require("placeholder")

    config.require("formFieldName")
    config.require("fragment")
    config.require("action")
    config.require("itemValueProperty")
    config.require("itemLabelFunction")
    def fragmentProvider = config.fragmentProvider ?: "emr"
%>

<script type="text/javascript">
    jq(function() {

        function setSearchValue(objectItem){
            jq('#${ config.id }-value').val(objectItem.${ config.itemValueProperty });
            jq('#${ config.id }-search').val(${ config.itemLabelFunction }(objectItem));
        };

        jq('#${ config.id }-search').autocomplete({
            source:   '${ ui.actionLink(fragmentProvider, config.fragment, config.action) }',
            minLength: 2,
            delay: 100,
            select: function(event, ui) {
                setSearchValue(ui.item);
                return false;
            }
        });
        jq('#${ config.id }-search').data('autocomplete')._renderItem = function(ul, item) {
            return jq('<li>')
                    .data('item.autocomplete', item)
                    .append('<a>' + ${ config.itemLabelFunction }(item) + '</a>')
                    .appendTo(ul);
        };

        jq('#${ config.id }-search').data('autocomplete')._renderMenu = function(ul, items){
            var self= this;
            if(items.length ==1){
                var firstItem = items[0];
                setSearchValue(firstItem);
            }else{
                jq.each( items , function(i, item){
                    self._renderItem(ul, item);
                });
            }
        };
    });
</script>

<span class="autocomplete-label">${ config.label }</span>
<div>
    <input type="hidden" class="field-value" id="${ config.id }-value" name="${ config.formFieldName }"/>
    <input type="text" class="field-display" id="${ config.id }-search" placeholder="${ config.placeholder}" size="40"/>
</div>

<span class="field-error" style="display: none"></span>