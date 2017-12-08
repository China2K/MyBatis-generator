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
package org.mybatis.generator.main;

import java.io.File;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;

public class MyBatisGeneratorMojo extends AbstractMojo {

	public static void main(String[] args) throws Exception {
		new MyBatisGeneratorMojo().execute();
	}

	public void execute() throws MojoExecutionException {
		try {
			Properties props = new Properties();
			String propsFile = "src/main/resource/medical/medical_generatorConfig.properties";
			String configXml = "src/main/resource/medical/medical_generatorConfig.xml";
			props.put("mybatis.generator.generatorConfig.properties", propsFile);
			props.put("mybatis.generator.generatorConfig.xml", configXml);
			ConfigurationParser cp = new ConfigurationParser(props, null);
			Configuration config = cp.parseConfiguration(new File(configXml));
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, false, null);
			myBatisGenerator.generate(null, null, null);
			System.out.println("tttttttttttttt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
