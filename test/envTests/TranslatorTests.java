/**
 * 
 */
package envTests;

import static org.junit.Assert.*;

import org.junit.Test;

import eis.iilang.Action;
import eis.iilang.Identifier;
import env.Translator;
import jason.asSyntax.Literal;

/**
 *
 */
public class TranslatorTests {

	@Test
	public void stringToActionTest() {
		Action action = Translator.stringToAction("goto(shop1)");
		
		assertEquals("goto", action.getName());
		assertEquals(1, action.getParameters().size());
		assertEquals(new Identifier("facility=shop1"), action.getParameters().get(0)); 
	
	}
	
	
	@Test
	public void parametersToArgumentTest()
	{
		fail("not implemented");
	}
}
