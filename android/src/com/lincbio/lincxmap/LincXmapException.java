package com.lincbio.lincxmap;

public class LincXmapException extends RuntimeException {
	private static final long serialVersionUID = 6999333603447642577L;

	public LincXmapException() {
		super();
	}

	public LincXmapException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public LincXmapException(String detailMessage) {
		super(detailMessage);
	}

	public LincXmapException(Throwable throwable) {
		super(throwable);
	}

}
