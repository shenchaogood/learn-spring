package sc.learn.test.netty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Converter;

public class TestMeaagePack {

	@Test
	public void testSerialize() throws IOException {
		
		// Create serialize objects.
		List<String> src = new ArrayList<String>();
		src.add("msgpack");
		src.add("kumofs");
		src.add("中国");

		MessagePack msgpack = new MessagePack();
		// Serialize
		byte[] raw = msgpack.write(src);

		// Deserialize directly using a template
		List<String> dst1 = msgpack.read(raw, Templates.tList(Templates.TString));
		System.out.println(dst1.get(0));
		System.out.println(dst1.get(1));
		System.out.println(dst1.get(2));

		// Or, Deserialze to Value then convert type.
		Value dynamic = msgpack.read(raw);
		try(Converter converter = new Converter(dynamic)){
			List<String> dst2=converter.read(Templates.tList(Templates.TString));
			System.out.println(dst2.get(0));
			System.out.println(dst2.get(1));
			System.out.println(dst2.get(2));
		}
	}
}
