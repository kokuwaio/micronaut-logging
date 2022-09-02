package io.kokuwa.micronaut.logging.configurator;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

/**
 * Add custom actions.
 *
 * @author Stephan Schnabel
 */
public class MicronautJoranConfigurator extends JoranConfigurator {

	@Override
	public void addElementSelectorAndActionAssociations(RuleStore rs) {
		super.addElementSelectorAndActionAssociations(rs);
		rs.addRule(new ElementSelector("configuration/root/autoAppender"), () -> new RootAutoSelectAppenderAction());
	}
}
