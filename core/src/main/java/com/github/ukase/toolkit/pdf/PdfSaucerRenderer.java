/*
 * Copyright (c) 2018 Pavel Uvarov <pauknone@yahoo.com>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.toolkit.pdf;

import com.github.ukase.toolkit.CompoundSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.w3c.dom.*;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFontContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFEncryption;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.function.Consumer;

public class PdfSaucerRenderer extends ITextRenderer {
    private static final String DIRECTIVE_MINIMAL_PAGES_COUNT_START = "<!--== @minimalPagesCount=";
    private static final String DIRECTIVE_END = "==-->";

    private BlockBox root;
    private LayoutContext layoutContext;
    private com.itextpdf.text.Document pdfDoc;
    private PdfWriter writer;
    private int minimalPagesCount;

    public PdfSaucerRenderer(CompoundSource source) {
        super();

        SharedContext context = getSharedContext();
        int dotsPerPixel = context.getDotsPerPixel();
        UserAgentCallback delegate = context.getUserAgentCallback();
        UserAgentCallback callback = new WrappedUserAgentCallback(source, dotsPerPixel, delegate);
        context.setUserAgentCallback(callback);
    }

    @Override
    public void layout() {
        layoutContext = newLayoutContext();
        root = BoxBuilder.createRootBox(layoutContext, getDocument());
        root.setContainingBlock(new ViewportBox(getInitialExtents(layoutContext)));
        root.layout(layoutContext);
        Dimension dim = root.getLayer().getPaintingDimension(layoutContext);
        root.getLayer().trimEmptyPages(layoutContext, dim.height);
        addEmptyPagesToCount(minimalPagesCount);
        root.getLayer().layoutPages(layoutContext);
    }

    public void exportText(Writer writer) throws IOException {
        RenderingContext c = newRenderingContext();
        c.setPageCount(root.getLayer().getPages().size());
        root.exportText(c, writer);
    }

    public BlockBox getRootBox() {
        return root;
    }


    public void writeNextDocument(int initialPageNo) throws DocumentException, IOException {
        java.util.List pages = root.getLayer().getPages();

        RenderingContext c = newRenderingContext();
        c.setInitialPageNo(initialPageNo);
        PageBox firstPage = (PageBox) pages.get(0);
        com.itextpdf.text.Rectangle firstPageSize = new com.itextpdf.text.Rectangle(0, 0, firstPage.getWidth(c) / getDotsPerPoint(),
                firstPage.getHeight(c) / getDotsPerPoint());

        getOutputDevice().setStartPageNo(writer.getPageNumber());

        pdfDoc.setPageSize(firstPageSize);
        pdfDoc.newPage();

        writePDF(pages, c, firstPageSize, pdfDoc, writer);
    }

    public void createPDF(OutputStream os) throws DocumentException, IOException {
        java.util.List pages = root.getLayer().getPages();

        RenderingContext c = newRenderingContext();
        c.setInitialPageNo(0);
        PageBox firstPage = (PageBox) pages.get(0);
        com.itextpdf.text.Rectangle firstPageSize = new com.itextpdf.text.Rectangle(0, 0, firstPage.getWidth(c) / getDotsPerPoint(),
                firstPage.getHeight(c) / getDotsPerPoint());

        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(firstPageSize, 0, 0, 0, 0);
        writer = PdfWriter.getInstance(doc, os);
        writer.setPdfVersion(getPDFVersion());
        PDFEncryption pdfEncryption = getPDFEncryption();
        if (pdfEncryption != null) {
            writer.setEncryption(pdfEncryption.getUserPassword(), pdfEncryption.getOwnerPassword(),
                    pdfEncryption.getAllowedPrivileges(), pdfEncryption.getEncryptionType());
        }
        pdfDoc = doc;

        firePreOpen();
        doc.open();

        writePDF(pages, c, firstPageSize, doc, writer);

        fireOnClose();
        doc.close();
    }

    public void setDocumentFromString(String content, String baseUrl) {
        super.setDocumentFromString(content, baseUrl);
        minimalPagesCount = resolveMinimalPagesCount(content);
    }

    private void writePDF(java.util.List pages, RenderingContext c, com.itextpdf.text.Rectangle firstPageSize, com.itextpdf.text.Document doc,
                          PdfWriter writer) throws DocumentException, IOException {
        ITextOutputDevice outputDevice = getOutputDevice();
        outputDevice.setRoot(root);

        outputDevice.start(getDocument());
        outputDevice.setWriter(writer);
        outputDevice.initializePage(writer.getDirectContent(), firstPageSize.getHeight());

        root.getLayer().assignPagePaintingPositions(c, Layer.PAGED_MODE_PRINT);

        int pageCount = root.getLayer().getPages().size();
        c.setPageCount(pageCount);
        firePreWrite(pageCount);
        setMetaValues(doc);
        for (int i = 0; i < pageCount; i++) {
            PageBox currentPage = (PageBox) pages.get(i);
            c.setPage(i, currentPage);
            paintPage(c, writer, currentPage);
            outputDevice.finishPage();
            if (i != pageCount - 1) {
                PageBox nextPage = (PageBox) pages.get(i + 1);
                float dotsPerPoint = getDotsPerPoint();
                com.itextpdf.text.Rectangle nextPageSize =
                        new com.itextpdf.text.Rectangle(0, 0,
                                nextPage.getWidth(c) / dotsPerPoint, nextPage.getHeight(c) / dotsPerPoint);
                doc.setPageSize(nextPageSize);
                doc.newPage();
                outputDevice.initializePage(writer.getDirectContent(), nextPageSize.getHeight());
            }
        }

        outputDevice.finish(c, root);
    }

    private void paintPage(RenderingContext c, PdfWriter writer, PageBox page) throws IOException {
        provideMetadataToPage(writer, page);

        page.paintBackground(c, 0, Layer.PAGED_MODE_PRINT);
        page.paintMarginAreas(c, 0, Layer.PAGED_MODE_PRINT);
        page.paintBorder(c, 0, Layer.PAGED_MODE_PRINT);

        ITextOutputDevice outputDevice = getOutputDevice();
        Shape working = outputDevice.getClip();

        Rectangle content = page.getPrintClippingBounds(c);
        outputDevice.clip(content);

        int top = -page.getPaintingTop() + page.getMarginBorderPadding(c, CalculatedStyle.TOP);

        int left = page.getMarginBorderPadding(c, CalculatedStyle.LEFT);

        outputDevice.translate(left, top);
        root.getLayer().paint(c);
        outputDevice.translate(-left, -top);

        outputDevice.setClip(working);
    }

    private void firePreOpen() {
        if (getListener() != null) {
            getListener().preOpen(this);
        }
    }

    private void firePreWrite(int pageCount) {
        if (getListener() != null) {
            getListener().preWrite(this, pageCount);
        }
    }

    private void fireOnClose() {
        if (getListener() != null) {
            getListener().onClose(this);
        }
    }

    private void provideMetadataToPage(PdfWriter writer, PageBox page) throws IOException {
        byte[] metadata = null;
        if (page.getMetadata() != null) {
            try {
                String metadataBody = stringfyMetadata(page.getMetadata());
                if (metadataBody != null) {
                    metadata = createXPacket(stringfyMetadata(page.getMetadata())).getBytes("UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                // Can't happen
                throw new RuntimeException(e);
            }
        }

        if (metadata != null) {
            writer.setPageXmpMetadata(metadata);
        }
    }

    private String createXPacket(String metadata) {
        return "<?xpacket begin='\uFEFF' id='W5M0MpCehiHzreSzNTczkc9d'?>\n" +
                metadata +
                "\n<?xpacket end='r'?>";
    }

    private String stringfyMetadata(org.w3c.dom.Element element) {
        org.w3c.dom.Element target = getFirstChildElement(element);
        if (target == null) {
            return null;
        }

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter output = new StringWriter();
            transformer.transform(new DOMSource(target), new StreamResult(output));

            return output.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private static org.w3c.dom.Element getFirstChildElement(org.w3c.dom.Element element) {
        Node n = element.getFirstChild();
        while (n != null) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                return (org.w3c.dom.Element) n;
            }
            n = n.getNextSibling();
        }
        return null;
    }


    private void setMetaValues(com.itextpdf.text.Document doc) {
        setMeta(doc::addTitle, "title");
        setMeta(doc::addAuthor, "author");
        setMeta(doc::addSubject, "subject");
        setMeta(doc::addKeywords, "keywords");
    }

    private void setMeta(Consumer<String> consumer, String name) {
        String data = getOutputDevice().getMetadataByName(name);
        if (data != null) {
            consumer.accept(data);
        }
    }

    private int resolveMinimalPagesCount(String htmlDocument) {
        int start = htmlDocument.indexOf(DIRECTIVE_MINIMAL_PAGES_COUNT_START);
        if (start == -1) {
            return 0;
        }
        int end = htmlDocument.indexOf(DIRECTIVE_END, start);
        if (end == -1) {
            return 0;
        }
        String count = htmlDocument.substring(start + DIRECTIVE_MINIMAL_PAGES_COUNT_START.length(), end);
        return Integer.parseInt(count.trim());
    }



    private void addEmptyPagesToCount(int count) {
        for (int i = root.getLayer().getPages().size() ; i < count ; i++) {
            root.getLayer().addPage(layoutContext);
        }
    }

    private LayoutContext newLayoutContext() {
        LayoutContext result = getSharedContext().newLayoutContextInstance();
        result.setFontContext(new ITextFontContext());

        getSharedContext().getTextRenderer().setup(result.getFontContext());

        return result;
    }

    private Rectangle getInitialExtents(LayoutContext c) {
        PageBox first = Layer.createPageBox(c, "first");

        return new Rectangle(0, 0, first.getContentWidth(c), first.getContentHeight(c));
    }

    private RenderingContext newRenderingContext() {
        RenderingContext result = getSharedContext().newRenderingContextInstance();
        result.setFontContext(new ITextFontContext());
        result.setOutputDevice(getOutputDevice());
        getSharedContext().getTextRenderer().setup(result.getFontContext());
        result.setRootLayer(root.getLayer());
        return result;
    }
}
