var factory = Java.type("com.alphasystem.openxml.builder.wml.WmlBuilderFactory");

var getFont = function () {
    return factory.getRFontsBuilder().withAscii("Arabic Typesetting").withHAnsi("Arabic Typesetting")
        .withCs("Arabic Typesetting").getObject();
};

var arabicLabel = function (rprBuilder) {
    rprBuilder.withRFonts(getFont()).withRtl(true);
    return rprBuilder;
};

var arabicLabelWithSize = function (rprBuilder, size) {
    rprBuilder = arabicLabel(rprBuilder).withSz(size).withSzCs(size);
    return rprBuilder;
};

var arabicHeading1 = function (rprBuilder) {
    return arabicLabelWithSize(rprBuilder, 52);
};

var arabicNormal = function (rprBuilder) {
    return arabicLabelWithSize(rprBuilder, 36);
};