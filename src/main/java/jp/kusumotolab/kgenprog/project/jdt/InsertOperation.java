package jp.kusumotolab.kgenprog.project.jdt;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import jp.kusumotolab.kgenprog.project.GeneratedAST;
import jp.kusumotolab.kgenprog.project.GeneratedSourceCode;
import jp.kusumotolab.kgenprog.project.Location;

public class InsertOperation implements JDTOperation {
	private ASTNode astNode;

	public InsertOperation(ASTNode astNode) {
		this.astNode = astNode;
	}

	@Override
	public GeneratedSourceCode apply(GeneratedSourceCode generatedSourceCode, Location location) {
		JDTLocation jdtLocation = (JDTLocation) location;

		List<GeneratedAST> newASTs = generatedSourceCode.getFiles()
			.stream()
			.map(ast -> {
				if (ast.getSourceFile().equals(location.getSourceFile())) {
					return applyInsertion((GeneratedJDTAST)ast, jdtLocation);
				} else {
					return ast;
				}
		}).collect(Collectors.toList());

		return new GeneratedSourceCode(newASTs);
	}
	
	private GeneratedJDTAST applyInsertion(GeneratedJDTAST ast, JDTLocation location){
		CompilationUnit unit = ((GeneratedJDTAST) ast).getRoot();
		CompilationUnit newAST = (CompilationUnit) ASTNode.copySubtree(unit.getAST(), unit);
		ASTNode target = location.locate(newAST);
		
		StructuralPropertyDescriptor locationInParent = target.getLocationInParent();
		if(!locationInParent.isChildListProperty()){
			throw new RuntimeException("can only insert ASTNode into a list");
		}
		
		List siblings = (List) target.getParent().getStructuralProperty(locationInParent);
		int insertIdx = siblings.indexOf(target) + 1;
		siblings.add(insertIdx, this.astNode);
		
		return new GeneratedJDTAST(ast.getSourceFile(), newAST);
	}

}