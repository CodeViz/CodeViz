package com.codeviz.codeviz.Parser;

import java.nio.file.Path;

import com.github.javaparser.ast.CompilationUnit;

public class ParsedItem {
	Path path;
	CompilationUnit cu;

	public ParsedItem() {
	}

	public ParsedItem(Path path, CompilationUnit cu) {
		this.path = path;
		this.cu = cu;
	}

}
