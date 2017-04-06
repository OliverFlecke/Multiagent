/**
 * 
 */
package envTests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;
import eis.iilang.*;
import env.Translator;

/**
 *
 */
public class TranslatorTests {

	@Test
	public void stringToActionTest() {
		Action action = Translator.stringToAction("goto(shop1)");
		
		assertEquals("goto", action.getName());
		assertEquals(1, action.getParameters().size());
		assertEquals(new Identifier("shop1"), action.getParameters().get(0)); 
		
		action = Translator.stringToAction("buy(item0, 10)");
		assertEquals("buy", action.getName());
		assertEquals(2, action.getParameters().size());
		assertEquals(new Identifier("item0"), action.getParameters().get(0));
		assertEquals(new Numeral(10.0), action.getParameters().get(1));
	}
	
	
	@Test
	public void parametersToArgumentTest()
	{
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Identifier("shop0"));
		parameters.add(new Numeral(10));
		
		Object[] arguments = Translator.parametersToArguments(parameters);
		
		assertEquals(2, arguments.length);
		assertEquals(new Identifier("shop0").toString(), arguments[0]);
		assertEquals(new Numeral(10).toString(), arguments[1]);
	}
}
