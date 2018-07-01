com.alphasystem.docbook.DocumentBuilder.buildDocument
converts adoc to dockbook to docx

Sample code:

		
        File inputFile = new File(path);
        Path srcPath = Paths.get(path);
        
        Path result = DocumentBuilder.buildDocument(srcPath);	

        System.out.println(result.toString());


An error like No style found with id "ExampleTitle" at com.alphasystem.openxml.builder.... check src/main/resources
is present on  your class path.

Then you need the entries in system-default.properties to match
the styles defined in open-xml-builder resources/META-INF
(default.dotx or styles.xml?)


16:14:36.666 [main] WARN  c.a.d.b.impl.block.ArticleBuilder - Unhandled type "org.docbook.model.Info".

<article xmlns="http://docbook.org/ns/docbook" xmlns:xl="http://www.w3.org/1999/xlink" version="5.0" xml:lang="en">
  <info>
    <title>PlutextDocumentsServices API</title>
    <date>2018-07-01</date>
  </info>


Handled type "org.docbook.model.LiteralLayout".
 
 