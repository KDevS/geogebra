package org.geogebra.web.html5.util.pdf;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "pdfjsLib" )
public class PDFDocumentProxy {
	public int numPages;

	public native PagePromise<PDFPageProxy> getPage(int pageNumber);
}
