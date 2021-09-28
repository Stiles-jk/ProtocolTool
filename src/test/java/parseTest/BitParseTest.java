package parseTest;

import com.zstu.FrameManager;
import com.zstu.exception.CRCException;
import com.zstu.exception.NodeAttributeNotFoundException;
import com.zstu.exception.ParsedException;
import com.zstu.exception.ProtoNodeNotFoundException;
import com.zstu.structure.pojo.ParsedVar;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.List;

/**
 * @auther Stiles-JKY
 * @date 2021/4/1-15:28
 */
public class BitParseTest {

    public static void main(String[] args) throws JDOMException, NodeAttributeNotFoundException, ProtoNodeNotFoundException, IOException, ParsedException, CRCException {
        byte[] data = {0x0a, 0x12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) 0xeb, (byte) 0x90, (byte) 0xb2, (byte) 0xbc, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05, 0x00, 0x06, 0x00, 0x07, 0x00, 0x08, 0x00, 0x09, 0x00, 0x0a, 0x00, 0x0b, 0x00, 0x0c, 0x00, 0x0d, 0x00, 0x0e, 0x00, 0x0f, 0x00, 0x10};
        FrameManager frameManager = FrameManager.getInstance();
        frameManager.createFramesList("D:\\1553Data\\tagsProtocol.xml");
        List<ParsedVar> parsedVars = frameManager.doParse(data);
        parsedVars.stream().forEach(System.out::println);
    }
}
