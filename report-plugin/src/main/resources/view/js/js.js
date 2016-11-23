jQuery(document).ready( function($) {

    var editor = ace.edit("editor");
    var Range = ace.require('ace/range').Range;
    var _range = new Range(7, 0, 7, 1);
    editor.setTheme("ace/theme/chrome");
    editor.getSession().setMode("ace/mode/latex");
    editor.getSession().setMode("ace/mode/latex");
    editor.getSession().addMarker(
        _range, "ace_active-line", "fullLine"
    );

    editor.getSession().setValue( $(this).find(':input[name=lines]').text() );


    $('form#generate').bind('submit', function () {
        var lines = $(this).find(':input[name=lines]');
        lines.prop('value', editor.getValue());
    });
    $('form#generate').bind('submit', function () {
        var fix_fields = $('form#fix').find(':input');
        $.each( fix_fields, function( key, value ) {
            value.prop('value', value);
        });
    });
});