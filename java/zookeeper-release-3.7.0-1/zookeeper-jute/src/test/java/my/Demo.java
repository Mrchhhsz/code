package my;

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;

import java.io.IOException;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/2/21 21:46
 * @Package: my
 */
public class Demo {

    public static void main(String[] args) {
        String path = "D:\\test.txt";


    }

    static class Person implements Record {

        private int age;
        private String name;

        public Person() {}

        @Override
        public void serialize(OutputArchive archive, String tag) throws IOException {
            archive.startRecord(this, tag);
            archive.writeInt(age, "age");
            archive.writeString(name, "name");
            archive.endRecord(this, tag);
        }

        @Override
        public void deserialize(InputArchive archive, String tag) throws IOException {
            archive.startRecord(tag);
            age = archive.readInt("age");
            name = archive.readString("name");
            archive.endRecord(tag);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}


