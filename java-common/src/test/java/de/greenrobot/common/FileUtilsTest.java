package de.greenrobot.common;

import de.greenrobot.common.hash.Murmur3F;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/** Implicitly tests some of IoUtils. */
public class FileUtilsTest {

    private File file;

    @Before
    public void setUp() throws IOException {
        file = File.createTempFile("file-utils-test", ".txt");
        System.out.println(file.getAbsolutePath());
        file.deleteOnExit();
    }

    @Test
    public void testWriteAndReadUtf8() throws IOException {
        String text = "Hello, let's put in some Umlauts: öäüÖÄÜ €";
        FileUtils.writeUtf8(file, text);
        String text2 = FileUtils.readUtf8(file);
        Assert.assertEquals(text, text2);
    }

    @Test
    public void testWriteAndReadObject() throws Exception {
        String text = "Hello, let's put in some Umlauts: öäüÖÄÜ €";
        String text2 = "And one more";
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(text);
        strings.add(text2);
        FileUtils.writeObject(file, strings);
        ArrayList<String> strings2 = (ArrayList<String>) FileUtils.readObject(file);
        Assert.assertEquals(strings.size(), strings2.size());
        Assert.assertEquals(text, strings2.get(0));
        Assert.assertEquals(text2, strings2.get(1));
    }

    @Test
    public void testDigestMd5AndSha1() throws IOException, ClassNotFoundException {
        byte[] content = new byte[33333];
        new Random(42).nextBytes(content);
        FileUtils.writeBytes(file, content);

        Assert.assertEquals("E4DB2A1C03CA891DDDCE45150570ABEB", FileUtils.getMd5(file).toUpperCase());
        Assert.assertEquals("5123C97498170FFA46056190D9439DA203E5234C", FileUtils.getSha1(file).toUpperCase());
    }

    @Test
    public void testUpdateChecksumAndCopy() throws IOException, ClassNotFoundException {
        byte[] content = new byte[33333];
        new Random().nextBytes(content);

        Murmur3F murmur3F = new Murmur3F();
        murmur3F.update(content);
        String hash = murmur3F.getValueHexString();

        FileUtils.writeBytes(file, content);

        File file2 = File.createTempFile("file-utils-test", ".txt");
        file2.deleteOnExit();
        FileUtils.copyFile(file, file2);

        murmur3F.reset();
        FileUtils.updateChecksum(file, murmur3F);
        Assert.assertEquals(hash, murmur3F.getValueHexString());
    }

}