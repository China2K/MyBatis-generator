/**
 *    Copyright 2006-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.controller.JavaControllerGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.BlankJavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.MixedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordDtoGenerator;
import org.mybatis.generator.codegen.mybatis3.model.BaseRecordGenerator;
import org.mybatis.generator.codegen.mybatis3.model.ExampleWithAssociationGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;
import org.mybatis.generator.codegen.mybatis3.service.JavaServiceGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.BlankXMLMapperGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.ObjectFactory;

/**
 * The Class IntrospectedTableMyBatis3Impl.
 *
 * @author Jeff Butler
 */
public class IntrospectedTableMyBatis3Impl extends IntrospectedTable {

	/** The java model generators. */
	protected List<AbstractJavaGenerator> javaModelGenerators;
	protected List<AbstractJavaGenerator> javaModelDtoGenerators;

	/** The client generators. */
	protected List<AbstractJavaGenerator> clientGenerators;
	protected List<AbstractJavaGenerator> blankClientGenerators;

	/** The service generators. */
	protected List<AbstractJavaGenerator> javaServiceGenerators;
	protected List<AbstractJavaGenerator> javaControllerGenerators;

	/** The xml mapper generator. */
	protected AbstractXmlGenerator xmlMapperGenerator;

	protected AbstractXmlGenerator blankXmlMapperGenerator;

	/**
	 * Instantiates a new introspected table my batis3 impl.
	 */
	public IntrospectedTableMyBatis3Impl() {
		super(TargetRuntime.MYBATIS3);
		javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
		javaModelDtoGenerators = new ArrayList<AbstractJavaGenerator>();
		clientGenerators = new ArrayList<AbstractJavaGenerator>();
		blankClientGenerators = new ArrayList<AbstractJavaGenerator>();
		javaServiceGenerators = new ArrayList<AbstractJavaGenerator>();
		javaControllerGenerators = new ArrayList<AbstractJavaGenerator>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mybatis.generator.api.IntrospectedTable#calculateGenerators(java.
	 * util.List, org.mybatis.generator.api.ProgressCallback)
	 */
	@Override
	public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
		calculateJavaModelGenerators(warnings, progressCallback);

		calculateJavaModelDtoGenerators(warnings, progressCallback);

		AbstractJavaClientGenerator javaClientGenerator = calculateClientGenerators(warnings, progressCallback);

		calculateXmlMapperGenerator(javaClientGenerator, warnings, progressCallback);

		AbstractJavaClientGenerator blankJavaClientGenerator = calculateBlankClientGenerators(warnings,
				progressCallback);

		calculateBlankXmlMapperGenerator(blankJavaClientGenerator, warnings, progressCallback);

		calculateServiceGenerators(warnings, progressCallback);

		calculateControllerGenerators(warnings, progressCallback);
	}

	/**
	 * Calculate xml mapper generator.
	 *
	 * @param javaClientGenerator
	 *            the java client generator
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void calculateXmlMapperGenerator(AbstractJavaClientGenerator javaClientGenerator, List<String> warnings,
			ProgressCallback progressCallback) {
		if (javaClientGenerator == null) {
			if (context.getSqlMapGeneratorConfiguration() != null) {
				xmlMapperGenerator = new XMLMapperGenerator();
			}
		} else {
			xmlMapperGenerator = javaClientGenerator.getMatchedXMLGenerator();
		}

		initializeAbstractGenerator(xmlMapperGenerator, warnings, progressCallback);
	}

	protected void calculateBlankXmlMapperGenerator(AbstractJavaClientGenerator blankJavaClientGenerator,
			List<String> warnings, ProgressCallback progressCallback) {
		if (blankJavaClientGenerator == null) {
			if (context.getBlankSqlMapGeneratorConfiguration() != null) {
				blankXmlMapperGenerator = new BlankXMLMapperGenerator();
			}
		} else {
			blankXmlMapperGenerator = blankJavaClientGenerator.getMatchedXMLGenerator();
		}

		initializeAbstractGenerator(blankXmlMapperGenerator, warnings, progressCallback);
	}

	/**
	 * Calculate client generators.
	 *
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 * @return true if an XML generator is required
	 */
	protected AbstractJavaClientGenerator calculateClientGenerators(List<String> warnings,
			ProgressCallback progressCallback) {
		if (!rules.generateJavaClient()) {
			return null;
		}

		AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		clientGenerators.add(javaGenerator);

		return javaGenerator;
	}

	protected AbstractJavaClientGenerator calculateBlankClientGenerators(List<String> warnings,
			ProgressCallback progressCallback) {
		if (!rules.generateJavaClient()) {
			return null;
		}

		AbstractJavaClientGenerator javaGenerator = createBlankJavaClientGenerator();
		if (javaGenerator == null) {
			return null;
		}

		initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
		blankClientGenerators.add(javaGenerator);

		return javaGenerator;
	}

	protected void calculateServiceGenerators(List<String> warnings, ProgressCallback progressCallback) {
		JavaServiceGenerator javaServiceGenerator = createJavaServiceGenerator();
		if (javaServiceGenerator == null) {
			return;
		}
		initializeAbstractGenerator(javaServiceGenerator, warnings, progressCallback);
		javaServiceGenerators.add(javaServiceGenerator);

	}

	protected void calculateControllerGenerators(List<String> warnings, ProgressCallback progressCallback) {
		JavaControllerGenerator javaControllerGenerator = createJavaControllerGenerator();
		if (javaControllerGenerator == null) {
			return;
		}
		initializeAbstractGenerator(javaControllerGenerator, warnings, progressCallback);
		javaControllerGenerators.add(javaControllerGenerator);

	}

	/**
	 * Creates the java client generator.
	 *
	 * @return the abstract java client generator
	 */
	protected AbstractJavaClientGenerator createJavaClientGenerator() {
		if (context.getJavaClientGeneratorConfiguration() == null) {
			return null;
		}

		String type = context.getJavaClientGeneratorConfiguration().getConfigurationType();

		AbstractJavaClientGenerator javaGenerator;
		if ("XMLMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new JavaMapperGenerator();
		} else if ("MIXEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new MixedClientGenerator();
		} else if ("ANNOTATEDMAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new AnnotatedClientGenerator();
		} else if ("MAPPER".equalsIgnoreCase(type)) { //$NON-NLS-1$
			javaGenerator = new JavaMapperGenerator();
		} else {
			javaGenerator = (AbstractJavaClientGenerator) ObjectFactory.createInternalObject(type);
		}

		return javaGenerator;
	}

	protected AbstractJavaClientGenerator createBlankJavaClientGenerator() {
		if (context.getBlankJavaClientGeneratorConfiguration() == null) {
			return null;
		}
		return new BlankJavaMapperGenerator();
	}

	protected JavaServiceGenerator createJavaServiceGenerator() {
		if (context.getJavaServiceGeneratorConfiguration() == null) {
			return null;
		}
		return new JavaServiceGenerator();
	}

	protected JavaControllerGenerator createJavaControllerGenerator() {
		if (context.getJavaControllerGeneratorConfiguration() == null) {
			return null;
		}
		return new JavaControllerGenerator();
	}

	/**
	 * Calculate java model generators.
	 *
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {

		if (getRules().generatePrimaryKeyClass()) {
			AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}

		if (getRules().generateBaseRecordClass()) {
			AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}

		if (getRules().generateRecordWithBLOBsClass()) {
			AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}
		
		if (getRules().generateExampleClass()) {
			AbstractJavaGenerator javaGenerator = new ExampleWithAssociationGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelGenerators.add(javaGenerator);
		}
	}

	protected void calculateJavaModelDtoGenerators(List<String> warnings, ProgressCallback progressCallback) {
		if (getRules().generateBaseRecordClass() || getRules().generateRecordWithBLOBsClass()) {
			if (context.getJavaModelDtoGeneratorConfiguration() == null) {
				return;
			}
			AbstractJavaGenerator javaGenerator = new BaseRecordDtoGenerator();
			initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
			javaModelDtoGenerators.add(javaGenerator);
		}
	}

	/**
	 * Initialize abstract generator.
	 *
	 * @param abstractGenerator
	 *            the abstract generator
	 * @param warnings
	 *            the warnings
	 * @param progressCallback
	 *            the progress callback
	 */
	protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings,
			ProgressCallback progressCallback) {
		if (abstractGenerator == null) {
			return;
		}

		abstractGenerator.setContext(context);
		abstractGenerator.setIntrospectedTable(this);
		abstractGenerator.setProgressCallback(progressCallback);
		abstractGenerator.setWarnings(warnings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGeneratedJavaFiles()
	 */
	@Override
	public List<GeneratedJavaFile> getGeneratedJavaFiles() {
		List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();

		for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getJavaModelGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getJavaModelGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator javaGenerator : javaModelDtoGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getJavaModelDtoGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getJavaModelDtoGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator javaGenerator : clientGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getJavaClientGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getJavaClientGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator javaGenerator : blankClientGenerators) {
			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getBlankJavaClientGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getBlankJavaClientGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator serviceGenerator : javaServiceGenerators) {
			List<CompilationUnit> compilationUnits = serviceGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getJavaServiceGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getJavaServiceGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		for (AbstractJavaGenerator serviceGenerator : javaControllerGenerators) {
			List<CompilationUnit> compilationUnits = serviceGenerator.getCompilationUnits();
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, context
						.getJavaControllerGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				gjf.setOverwrite(context.getJavaControllerGeneratorConfiguration().isOverwrite());
				answer.add(gjf);
			}
		}

		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGeneratedXmlFiles()
	 */
	@Override
	public List<GeneratedXmlFile> getGeneratedXmlFiles() {
		List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();

		if (xmlMapperGenerator != null) {
			Document document = xmlMapperGenerator.getDocument();
			GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3XmlMapperFileName(),
					getMyBatis3XmlMapperPackage(), context.getSqlMapGeneratorConfiguration().getTargetProject(), true,
					context.getXmlFormatter());
			gxf.setOverwrite(context.getSqlMapGeneratorConfiguration().isOverwrite());
			if (context.getPlugins().sqlMapGenerated(gxf, this)) {
				answer.add(gxf);
			}
		}

		return answer;
	}

	@Override
	public List<GeneratedXmlFile> getGeneratedBlankXmlFiles() {
		List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();

		if (blankXmlMapperGenerator != null) {
			Document document = blankXmlMapperGenerator.getDocument();
			GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3BlankXmlMapperFileName(),
					getMyBatis3BlankXmlMapperPackage(), context.getBlankSqlMapGeneratorConfiguration()
							.getTargetProject(), true, context.getXmlFormatter());
			gxf.setOverwrite(context.getBlankSqlMapGeneratorConfiguration().isOverwrite());
			if (context.getPlugins().sqlMapGenerated(gxf, this)) {
				answer.add(gxf);
			}
		}

		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#getGenerationSteps()
	 */
	@Override
	public int getGenerationSteps() {
		return javaModelGenerators.size() + clientGenerators.size() + (xmlMapperGenerator == null ? 0 : 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#isJava5Targeted()
	 */
	@Override
	public boolean isJava5Targeted() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mybatis.generator.api.IntrospectedTable#requiresXMLGenerator()
	 */
	@Override
	public boolean requiresXMLGenerator() {
		AbstractJavaClientGenerator javaClientGenerator = createJavaClientGenerator();

		if (javaClientGenerator == null) {
			return false;
		} else {
			return javaClientGenerator.requiresXMLGenerator();
		}
	}
}
