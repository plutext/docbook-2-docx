var adapter = Java.type("com.alphasystem.openxml.builder.wml.WmlAdapter");
var factory = Java.type("com.alphasystem.openxml.builder.wml.WmlBuilderFactory");
var _true = factory.BOOLEAN_DEFAULT_TRUE_TRUE;

var handleBold = function (rprBuilder) {
    return rprBuilder.withB(_true).withBCs(_true);
};

var handleItalic = function (rprBuilder) {
    return rprBuilder.withI(_true).withICs(_true);
};

var handleUnderline = function (rprBuilder) {
    return rprBuilder.withU(factory.getUBuilder().withVal(org.docx4j.wml.UnderlineEnumeration.SINGLE).getObject());
};

var handleStrikeThrough = function (rprBuilder) {
    return rprBuilder.withStrike(_true);
};

var handleLiteral = function (rprBuilder) {
    return rprBuilder.withRFonts(factory.getRFontsBuilder().withAscii("Courier New").withHAnsi("Courier New").withCs("Courier New").getObject());
};

var handleSubscript = function (rprBuilder) {
    return rprBuilder.withVertAlign(factory.getCTVerticalAlignRunBuilder().withVal(org.docx4j.wml.STVerticalAlignRun.SUBSCRIPT).getObject());
};

var handleSuperscript = function (rprBuilder) {
    return rprBuilder.withVertAlign(factory.getCTVerticalAlignRunBuilder().withVal(org.docx4j.wml.STVerticalAlignRun.SUPERSCRIPT).getObject());
};

var handleHyperlink = function (rprBuilder) {
    return rprBuilder.withRStyle(adapter.getRStyle("Hyperlink"));
};

var handleColor = function (rprBuilder, color){
    return rprBuilder.withColor(adapter.getColor(color));
};

var handleStyle = function (rprBuilder, styleName) {
    return rprBuilder.withRStyle(adapter.getRStyle(styleName));
};

var handleExample = function () {
    var border = adapter.getBorder(org.docx4j.wml.STBorder.SINGLE, 4, 0, "E0E0DC");
    var tblBorders = factory.getTblBordersBuilder().withTop(border).withLeft(border)
        .withBottom(border).withRight(border).withInsideH(border).withInsideV(border).getObject();

    var tblPr = factory.getTblPrBuilder().withTblBorders(tblBorders).getObject();

    var shade = factory.getCTShdBuilder().withVal(org.docx4j.wml.STShd.CLEAR).withColor("auto").withFill("FFFEF7").getObject();
    var tcPr = factory.getTcPrBuilder().withShd(shade).getObject();

    return new com.alphasystem.openxml.builder.wml.TableAdapter(1).startTable(tblPr).startRow().addColumn(0, null, tcPr).endRow().getTable();
};

var handleSideBar = function () {
    var border = adapter.getBorder(org.docx4j.wml.STBorder.SINGLE, 4, 0, "E0E0DC");
    var tblBorders = factory.getTblBordersBuilder().withTop(border).withLeft(border)
        .withBottom(border).withRight(border).withInsideH(border).withInsideV(border).getObject();

    var tblPr = factory.getTblPrBuilder().withTblBorders(tblBorders).getObject();

    var shade = factory.getCTShdBuilder().withVal(org.docx4j.wml.STShd.CLEAR).withColor("auto").withFill("F8F8F7").getObject();
    var tcPr = factory.getTcPrBuilder().withShd(shade).getObject();

    return new com.alphasystem.openxml.builder.wml.TableAdapter(1).startTable(tblPr).startRow().addColumn(0, null, tcPr).endRow().getTable();
};

var handleCaution = function(admonitionCaptionStyle, captionText){
    return handleAdmonition(15, admonitionCaptionStyle, captionText);
};

var handleImportant = function(admonitionCaptionStyle, captionText){
    return handleAdmonition(20, admonitionCaptionStyle, captionText);
};

var handleNote = function(admonitionCaptionStyle, captionText){
    return handleAdmonition(15, admonitionCaptionStyle, captionText);
};

var handleTip = function(admonitionCaptionStyle, captionText){
    return handleAdmonition(15, admonitionCaptionStyle, captionText);
};

var handleWarning = function(admonitionCaptionStyle, captionText){
    return handleAdmonition(15, admonitionCaptionStyle, captionText);
};

var handleAdmonition = function (widthOfCaptionColumn, admonitionCaptionStyle, captionText) {
    var widthOfContentColumn = 100.0 - widthOfCaptionColumn;
    var nilBorder = adapter.getNilBorder();
    var tblBorders = factory.getTblBordersBuilder().withTop(nilBorder).withLeft(nilBorder).withBottom(nilBorder)
        .withRight(nilBorder).withInsideH(nilBorder).withInsideV(nilBorder).getObject();
    var tblPr = factory.getTblPrBuilder().withTblBorders(tblBorders).getObject();
    var tableAdapter = new com.alphasystem.openxml.builder.wml.TableAdapter(widthOfCaptionColumn, widthOfContentColumn)
        .startTable(tblPr).startRow();

    var border = adapter.getBorder(org.docx4j.wml.STBorder.SINGLE, 8, 0, "DDDDD8");
    var columnBorders = factory.getTcPrInnerBuilder().getTcBordersBuilder().withRight(border).getObject();
    var vertAlign = factory.getCTVerticalJcBuilder().withVal(org.docx4j.wml.STVerticalJc.CENTER).getObject();
    var tcPr = factory.getTcPrBuilder().withVAlign(vertAlign).withTcBorders(columnBorders).getObject();
    tableAdapter.addColumn(0, null, tcPr, adapter.getParagraphWithStyle(admonitionCaptionStyle, captionText));

    columnBorders = factory.getTcPrInnerBuilder().getTcBordersBuilder().withLeft(border).getObject();
    tcPr = factory.getTcPrBuilder().withTcBorders(columnBorders).getObject();
    tableAdapter.addColumn(1, null, tcPr).endRow();

    return tableAdapter.getTable();
};