package javax0.geci.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSource {

    @Test
    public void testSource() throws IOException {

        var sut = new Source("xyz");
        sut.inMemory = true;
        sut.lines.addAll(List.of(
                "This is something there",
                "    // <editor-fold id=\"myId\">",
                "this is to be replaced",
                "this is also to be replaced",
                "//</editor-fold>",
                " // <editor-fold id=\"otherId\">",
                "this is unharmed",
                "this is also unharmed",
                "//</editor-fold>"
        ));
        var seg = sut.open("myId");
        seg.write("this is the replacement first line");
        seg.write("this is the replacement 2 line");
        seg.write("this is the replacement 3 line");
        seg.write_r("next is intended");
        seg.write_l("intended line");
        seg.write("normal line");
        sut.consolidate();
        assertEquals("This is something there\n" +
                "    // <editor-fold id=\"myId\">\n" +
                "    this is the replacement first line\n" +
                "    this is the replacement 2 line\n" +
                "    this is the replacement 3 line\n" +
                "    next is intended\n" +
                "        intended line\n" +
                "    normal line\n" +
                "//</editor-fold>\n" +
                " // <editor-fold id=\"otherId\">\n" +
                "this is unharmed\n" +
                "this is also unharmed\n" +
                "//</editor-fold>",String.join("\n",sut.lines));
    }
}
