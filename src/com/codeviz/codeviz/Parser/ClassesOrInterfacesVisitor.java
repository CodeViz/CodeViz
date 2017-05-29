package com.codeviz.codeviz.Parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.declarations.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.typesystem.Type;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class ClassesOrInterfacesVisitor extends VoidVisitorAdapter<Object> {

	private static CombinedTypeSolver combinedTypeSolver;

	public ClassesOrInterfacesVisitor() {
		super();
		combinedTypeSolver = new CombinedTypeSolver();
		combinedTypeSolver.add(new ReflectionTypeSolver(false));
		combinedTypeSolver.add(new JavaParserTypeSolver(new File(Parser.getFolder())));
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Object oArg) {
		super.visit(n, oArg);

		List<ClassOrInterfaceType> extendz = n.getExtendedTypes();
		if (extendz != null) {
			for (ClassOrInterfaceType e : extendz) {
				Parser.setParent(e.toString());
			}
		}

		List<ClassOrInterfaceType> implementz = n.getImplementedTypes();
		if (implementz != null) {
			for (ClassOrInterfaceType i : implementz) {
				Parser.getInterfaces().add(i.toString());
			}
		}

		for (ParsedItem item : Parser.getClasses().values()) {

			if (item.cu.getClassByName(n.getNameAsString()).isPresent())
				continue;

			new VoidVisitorAdapter<Object>() {
				@Override
				public void visit(ClassOrInterfaceDeclaration p, Object arg) {
					super.visit(p, arg);

					for (ClassOrInterfaceType parent : p.getExtendedTypes()) {
						if (parent.getNameAsString().equals(n.getNameAsString())) {
							Parser.getChildren().add(p.getNameAsString());
						}
					}
					
					for (ClassOrInterfaceType parent : p.getImplementedTypes()) {
						if (parent.getNameAsString().equals(n.getNameAsString())) {
							Parser.getChildren().add(p.getNameAsString());
						}
					}
				}

				@Override
				public void visit(MethodCallExpr p, Object arg) {
					super.visit(p, arg);

					SymbolReference<MethodDeclaration> m = JavaParserFacade.get(combinedTypeSolver).solve(p);
					if (m.isSolved()) {
						MethodDeclaration md = m.getCorrespondingDeclaration();

						if (md.declaringType().getName().equals(n.getNameAsString())) {
							LinkedList<String> associations = Parser.getAssociations();
							if (!associations.contains(item.cu.getType(0).getNameAsString())) {
								associations.add(item.cu.getType(0).getNameAsString());
							}
						}
					}

				}

				@Override
				public void visit(ObjectCreationExpr p, Object arg) {
					super.visit(p, arg);

					Type type = JavaParserFacade.get(combinedTypeSolver).getType(p);
					if (!type.isReference())
						return;

					String name = type.asReferenceType().getTypeDeclaration().getName();
					ParsedItem pItem = (ParsedItem) oArg;
					if (pItem.cu.getClassByName(name).isPresent()) {
						LinkedList<String> associations = Parser.getAssociations();
						if (!associations.contains(item.cu.getType(0).getNameAsString())) {
							associations.add(item.cu.getType(0).getNameAsString());
						}
					}

				}

			}.visit(item.cu, null);
		}

	}

	int counter = 0;

	@Override
	public void visit(MethodCallExpr n, Object arg) {
		super.visit(n, arg);
		SymbolReference<MethodDeclaration> m = JavaParserFacade.get(combinedTypeSolver).solve(n);
		if (m.isSolved()) {
			MethodDeclaration md = m.getCorrespondingDeclaration();

			LinkedList<String> associations = Parser.getAssociations();
			if (!associations.contains(md.declaringType().getName())) {
				associations.add(md.declaringType().getName());
			}
		}

	}

	@Override
	public void visit(ObjectCreationExpr p, Object arg) {
		super.visit(p, arg);

		Type type = JavaParserFacade.get(combinedTypeSolver).getType(p);
		if (!type.isReference())
			return;

		LinkedList<String> associations = Parser.getAssociations();
		String name = type.asReferenceType().getTypeDeclaration().getName();
		if (!associations.contains(name)) {
			associations.add(name);
		}

	}

	@Override
	public void visit(FieldDeclaration n, Object arg) {
		super.visit(n, arg);
		if (n.isFinal() && n.isStatic()) { // ignore final static attributes
			return;
		}

		LinkedList<String> attributes = Parser.getAttributes();
		for (VariableDeclarator vd : n.getVariables()) {
			String name = vd.getNameAsString();
			if (!attributes.contains(name)) {
				attributes.add(name);
			}
		}

	}

	@Override
	public void visit(com.github.javaparser.ast.body.MethodDeclaration n, Object arg) {
		super.visit(n, arg);

		LinkedList<String> methods = Parser.getMethods();

		String name = n.getNameAsString();
		if (!methods.contains(name)) {
			methods.add(name);
		}

	}

}