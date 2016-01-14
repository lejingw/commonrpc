package org.commonrpc.core.codec;

import com.esotericsoftware.kryo.Serializer;
import com.jingcai.apps.commonrpc.core.util.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.commonrpc.core.vo.Student;
import org.commonrpc.core.vo.Student2;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by lejing on 15/11/4.
 */
public class KryoCodecTest {
	private static final Logger logger = LoggerFactory.getLogger(KryoCodecTest.class);
	private Serializer<RuntimeException> serializer = new Serializer<RuntimeException>() {
		@Override
		public void write(Kryo kryo, Output output, RuntimeException object) {
			output.writeString(object.getMessage());
		}

		@Override
		public RuntimeException read(Kryo kryo, Input input, Class type) {
			String msg = input.readString();
			return new RuntimeException(msg);
		}
	};
	private Serializer<IllegalArgumentException> serializer2 = new Serializer<IllegalArgumentException>() {
		@Override
		public void write(Kryo kryo, Output output, IllegalArgumentException object) {
			output.writeString(object.getMessage());
		}

		@Override
		public IllegalArgumentException read(Kryo kryo, Input input, Class type) {
			String msg = input.readString();
			return new IllegalArgumentException(msg);
		}
	};

	@Test
	public void testOutput2File() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		{
			Output output = new Output(new FileOutputStream("target/file.bin"));
			Student2 student2 = new Student2("zhangsan", "man", 10);
			kryo.writeObject(output, student2);
			byte[] bb = output.toBytes();
			System.out.println("length:" + bb.length);
			output.close();
		}
		{
			regist(kryo, RuntimeException.class, serializer, 100);
			regist(kryo, IllegalArgumentException.class, serializer2, 200);
			Output output = new Output(new FileOutputStream("target/file.bin2"));
			RuntimeException exception = new RuntimeException("aaa");
			kryo.writeObject(output, exception);
			output.close();
		}
	}

	private void regist(Kryo kryo, Class cls, Serializer serializer, int id) {
		kryo.register(cls, serializer, id);
	}

	@Test
	public void testInputFromFile() throws FileNotFoundException {
		Kryo kryo = new Kryo();
		{
			Input input = new Input(new FileInputStream("target/file.bin"));
			Student student = kryo.readObject(input, Student.class);
			input.close();
			assertEquals(student.getName(), "zhangsan");
			assertEquals(student.getSex(), "man");
			assertEquals(student.getAge(), 10);
		}
		{
			regist(kryo, RuntimeException.class, serializer, 100);
			regist(kryo, IllegalArgumentException.class, serializer2, 100);
			Input input = new Input(new FileInputStream("target/file.bin2"));
			RuntimeException student = kryo.readObject(input, RuntimeException.class);
			input.close();
			System.out.println(student.getMessage());
		}
	}

	@Test
	public void test1() {
		Kryo kryo = new Kryo();
		// kryo.setReferences(true);//default is true
//		 kryo.setRegistrationRequired(true);//default is false
		// kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		//注册类
		Registration registration = kryo.register(Student.class);
		long time = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			//序列化
			Output output = null;
			//ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//output = new Output( outStream , 4096);
			output = new Output(1, 4096);
			Student student = new Student("zhangsan", "man", 23);
			kryo.writeObject(output, student);
			byte[] bb = output.toBytes();
			output.flush();

			//反序列化
			Input input = null;
			//input = new Input(new ByteArrayInputStream(outStream.toByteArray()),4096);
			input = new Input(bb);
			Student s = (Student) kryo.readObject(input, registration.getType());
			input.close();

			//System.out.println(s.getName() + "," + s.getSex());
		}
		time = System.currentTimeMillis() - time;
		System.out.println("time:" + time);
	}

	@Test
	public void test2() {
		Kryo kryo = new Kryo();
//		 kryo.setReferences(true);
//		 kryo.setRegistrationRequired(true);
		// kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		//注册类
		//Registration registration = kryo.register(Student.class);
		long time = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			//序列化
			Output output = null;
			//ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			//output = new Output( outStream , 4096);
			output = new Output(1, -1);
			Student student = new Student("zhangsan", "man", 23);
			kryo.writeClassAndObject(output, student);
			byte[] bb = output.toBytes();
			output.flush();

			//反序列化
			Input input = null;
			//input = new Input(new ByteArrayInputStream(outStream.toByteArray()),4096);
			input = new Input(bb);
			Student s = (Student) kryo.readClassAndObject(input);
			input.close();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("time:" + time);
	}

	@Test
	public void test3() throws Exception {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);

		Student student = new Student("zhangsan", "man", 0);
		Student student1 = new Student("stu1", "man", 1);
		student.setStudent1(student1);
		student.setStudent2(student1);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			Output output = new Output(256);
			KryoUtils.getKryo().writeClassAndObject(output, student);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = KryoUtils.getKryo().readClassAndObject(input);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("time:" + time);
	}

	@Test
	public void test_references() throws Exception {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);//default

		Student originStu = new Student("zhangsan", "man", 0);
		Student originStuInner = new Student("stu1", "man", 1);
		originStu.setStudent1(originStuInner);
		originStu.setStudent2(originStuInner);
		{
			Output output = new Output(256);
			kryo.writeClassAndObject(output, originStu);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = kryo.readClassAndObject(input);

			Student s = (Student) stu;
			assertEquals(s.getName(), "zhangsan");
			assertEquals(s.getSex(), "man");
			assertNotEquals(s, originStu);
			assertNotEquals(s.getStudent1(), originStuInner);
			assertEquals(s.getStudent1(), s.getStudent2());
		}
		kryo.setReferences(false);
		{
			Output output = new Output(256);
			kryo.writeClassAndObject(output, originStu);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = kryo.readClassAndObject(input);

			Student s = (Student) stu;
			assertEquals(s.getName(), "zhangsan");
			assertEquals(s.getSex(), "man");
			assertNotEquals(s, originStu);
			assertNotEquals(s.getStudent1(), originStuInner);
			assertNotEquals(s.getStudent1(), s.getStudent2());
			assertEquals(s.getStudent1().getName(), s.getStudent2().getName());
		}
	}

	@Test
	public void test_cycle() throws Exception {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);//default,and must if it is cycle dependency

		Student student = new Student("zhangsan", "man", 0);
		student.setStudent1(student);

		Output output = new Output(256);
		kryo.writeClassAndObject(output, student);
		byte[] bytes = output.toBytes();

		Input input = new Input(bytes);
		Object stu = kryo.readClassAndObject(input);

		System.out.println(stu);
		Student s = (Student) stu;
		assertEquals(s.getName(), "zhangsan");
		assertEquals(s.getSex(), "man");

		assertNotEquals(s, student);
		assertEquals(s, s.getStudent1());

	}
}
