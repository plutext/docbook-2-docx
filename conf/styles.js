var adapter = Java.type("com.alphasystem.openxml.builder.wml.WmlAdapter");
var factory = Java.type("com.alphasystem.openxml.builder.wml.WmlBuilderFactory");

var handleExample = function () {
    var border = adapter.getBorder(org.docx4j.wml.STBorder.SINGLE, 4, 0, "E0E0DC");
    var tblBorders = factory.getTblBordersBuilder().withTop(border).withLeft(border)
        .withBottom(border).withRight(border).withInsideH(border).withInsideV(border).getObject();

    var tblPr = factory.getTblPrBuilder().withTblBorders(tblBorders).getObject();

    var shade = factory.getCTShdBuilder().withVal(org.docx4j.wml.STShd.CLEAR).withColor("auto").withFill("FFFEF7").getObject();
    var tcPr = factory.getTcPrBuilder().withShd(shade).getObject();

    return new com.alphasystem.openxml.builder.wml.TableAdapter(1).startTable(tblPr).startRow()
        .addColumn(0, null, tcPr, null).endRow().getTable();
};

var handleInformalExample = function () {
    return handleExample();
};

var handleSideBar = function () {
    var border = adapter.getBorder(org.docx4j.wml.STBorder.SINGLE, 4, 0, "E0E0DC");
    var tblBorders = factory.getTblBordersBuilder().withTop(border).withLeft(border)
        .withBottom(border).withRight(border).withInsideH(border).withInsideV(border).getObject();

    var tblPr = factory.getTblPrBuilder().withTblBorders(tblBorders).getObject();

    var shade = factory.getCTShdBuilder().withVal(org.docx4j.wml.STShd.CLEAR).withColor("auto").withFill("F8F8F7").getObject();
    var tcPr = factory.getTcPrBuilder().withShd(shade).getObject();

    return new com.alphasystem.openxml.builder.wml.TableAdapter(1).startTable(tblPr).startRow().addColumn(0, null, tcPr, null).endRow().getTable();
};

var handleCaution = function (captionText) {
    return handleAdmonition(15, captionText);
};

var handleImportant = function (captionText) {
    return handleAdmonition(20, captionText);
};

var handleNote = function (captionText) {
    return handleAdmonition(15, captionText);
};

var handleTip = function (captionText) {
    return handleAdmonition(15, captionText);
};

var handleWarning = function (captionText) {
    return handleAdmonition(15, captionText);
};

var handleAdmonition = function (widthOfCaptionColumn, captionText) {
    var widthOfContentColumn = 100.0 - widthOfCaptionColumn;
    return new com.alphasystem.openxml.builder.wml.TableAdapter(widthOfCaptionColumn, widthOfContentColumn)
        .startTable("AdmonitionTable").startRow()
        .addColumn(0, adapter.getParagraph(captionText))
        .addColumn(1).endRow().getTable();
};