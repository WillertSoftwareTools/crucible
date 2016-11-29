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

    $( "#fix" ).click(function() {
        $(this).parents('form:first').find(':input[name=lines]').prop('value', editor.getSession().getValue());
        $("#generate-report").find('input[name=nextStep]').prop('value', "TWO");
        document.getElementById("generate-report").submit();
    });
    $( "#generate" ).click(function() {
        $(this).parents('form:first').find(':input[name=lines]').prop('value', editor.getSession().getValue());
        $("#generate-report").find('input[name=nextStep]').prop('value', "THREE");
        document.getElementById("generate-report").submit();
    });
});