package xs.spider.base.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class SimplePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	public SimplePropertyPlaceholderConfigurer() {
		setProperties(ConfigProvider.getProp());
	}
	
}



