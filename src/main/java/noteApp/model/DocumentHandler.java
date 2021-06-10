package noteApp.model;

import noteApp.model.note.Note;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledSegment;
import org.reactfx.util.Tuple2;

import java.io.*;

/**
 * Used to convert text (note contents) to and from StyledDocuments.
 * @author Matias Vainio
 */
public class DocumentHandler {

    /**
     * Decodes bytes to StyledDocument which is then added to StyleClassedTextArea to be shown to the user.
     * @param area StyleClassedTextArea where decoded note is to be displayed.
     * @param content text content which is going to be decoded.
     */
    public static void decode(InlineCssTextArea area, String content) {
        StyledDocument<String, String, String> doc = null;
        if (!content.isEmpty()) {
            if (area.getStyleCodecs().isPresent()) {
                Tuple2<Codec<String>, Codec<StyledSegment<String, String>>> codecs = area.getStyleCodecs().get();
                Codec<StyledDocument<String, String, String>>
                        codec = ReadOnlyStyledDocument.codec(codecs._1, codecs._2, area.getSegOps());


                try {
                    ByteArrayInputStream fis = new ByteArrayInputStream(content.getBytes());
                    DataInputStream dis = new DataInputStream(fis);
                    doc = codec.decode(dis);
                    fis.close();

                    if (doc != null) {
                        area.replaceSelection(doc);
                    }
                } catch (IOException | OutOfMemoryError ed) {
                    ed.printStackTrace();
                    area.replaceText(content);
                }
            }
        }
    }

    /**
     * Encodes note content with current style elements (bold, underline etc.)
     * @param area StyleClassedTextArea area where the note is gotten from.
     * @param note Note note to be encoded with the style.
     * @return StyledDocument document which contains text and corresponding style.
     */
    public static StyledDocument<String, String, String> encode(InlineCssTextArea area, Note note) {
        StyledDocument<String, String, String> doc = area.getDocument();

        // Use the Codec to save the document in a binary format
        area.getStyleCodecs().ifPresent(codecs -> {
            Codec<StyledDocument<String, String, String>> codec =
                    ReadOnlyStyledDocument.codec(codecs._1, codecs._2, area.getSegOps());
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(stream);
                codec.encode(dos, doc);
                if (note != null) {
                    note.setContent(stream.toString());
                }
                stream.close();
            } catch (IOException fnfe) {
                fnfe.printStackTrace();
            }
        });
        return doc;
    }
}
