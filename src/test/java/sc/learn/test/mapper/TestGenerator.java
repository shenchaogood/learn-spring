package sc.learn.test.mapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import org.junit.Test;

public class TestGenerator {

	@Test
	public void test() throws Exception {
		List<String> warnings = new ArrayList<String>();

		String confFilePath = this.getClass().getResource("/config/generatorConfig.xml").getFile();

		File configFile = new File(confFilePath);

		Configuration config = new ConfigurationParser(warnings).parseConfiguration(configFile);

		DefaultShellCallback callback = new DefaultShellCallback(true);

		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

		myBatisGenerator.generate(null);
	}

}
