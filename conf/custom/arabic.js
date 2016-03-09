// var wmlBuilders = new JavaImporter(com.alphasystem.openxml.builder.wml);
var factory = Java.type("com.alphasystem.openxml.builder.wml.WmlBuilderFactory");
var adapter = Java.type("com.alphasystem.openxml.builder.wml.WmlAdapter");

var getFont = function () {
    return factory.getRFontsBuilder().withAscii("Arabic Typesetting").withHAnsi("Arabic Typesetting")
        .withCs("Arabic Typesetting").getObject();
};

var arabicLabel = function (rprBuilder) {
    rprBuilder.withRFonts(getFont()).withRtl(factory.BOOLEAN_DEFAULT_TRUE_TRUE);
    return rprBuilder;
};

var arabicLabelWithSize = function (rprBuilder, size) {
    rprBuilder = arabicLabel(rprBuilder).withSz(adapter.getHpsMeasure(size)).withSzCs(adapter.getHpsMeasure(size));
    return rprBuilder;
};

var arabicHeading1 = function (rprBuilder) {
    return arabicLabelWithSize(rprBuilder, 52);
};

var arabicNormal = function (rprBuilder) {
    return arabicLabelWithSize(rprBuilder, 36);
};