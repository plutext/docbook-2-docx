var factory = Java.type("com.alphasystem.openxml.builder.wml.WmlBuilderFactory");
var _true = factory.BOOLEAN_DEFAULT_TRUE_TRUE;

var handleBold = function (rprBuilder){
    return rprBuilder.withB(_true).withBCs(_true);
};

var handleItalic = function(rprBuilder){
    return rprBuilder.withI(_true).withICs(_true);
};

var handleUnderline = function(rprBuilder){
    return rprBuilder.withU(factory.getUBuilder().withVal(org.docx4j.wml.UnderlineEnumeration.SINGLE).getObject());
};

var handleStrikeThrough = function(rprBuilder){
    return rprBuilder.withStrike(_true);
};

var handleLiteral = function(rprBuilder){
    return rprBuilder.withRFonts(factory.getRFontsBuilder().withAscii("Courier New").withHAnsi("Courier New").withCs("Courier New").getObject());
};

var handleSubscript = function(rprBuilder){
    return rprBuilder.withVertAlign(factory.getCTVerticalAlignRunBuilder().withVal(org.docx4j.wml.STVerticalAlignRun.SUBSCRIPT).getObject());
};

var handleSuperscript = function(rprBuilder){
    return rprBuilder.withVertAlign(factory.getCTVerticalAlignRunBuilder().withVal(org.docx4j.wml.STVerticalAlignRun.SUPERSCRIPT).getObject());
};

var handleStyle = function(rprBuilder, styleName){
    return rprBuilder.withRStyle(com.alphasystem.openxml.builder.OpenXmlAdapter.getRStyle(styleName));
};