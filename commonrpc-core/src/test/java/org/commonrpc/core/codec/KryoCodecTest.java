package org.commonrpc.core.codec;

import com.jingcai.apps.commonrpc.core.util.KryoUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.commonrpc.core.vo.Student;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lejing on 15/11/4.
 */
public class KryoCodecTest {
	private static final Logger logger = LoggerFactory.getLogger(KryoCodecTest.class);

	@Test
	public void test1() {
		Kryo kryo = new Kryo();
		// kryo.setReferences(true);
		// kryo.setRegistrationRequired(true);
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
			output = new Output(1, 4096);
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

			System.out.println(s.getName() + "," + s.getSex());
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
	public void test_cycle() throws Exception {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);//default

		Student student = new Student("zhangsan", "man", 0);
		student.setStudent1(student);
		{
			Output output = new Output(256);
			kryo.writeClassAndObject(output, student);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = kryo.readClassAndObject(input);

			System.out.println(stu);
			Student s = (Student) stu;
			System.out.println(s.getName() + "," + s.getSex());
			System.out.println(s.getStudent1() == s);
			Assert.assertEquals(s.getStudent1(), s);
		}
	}

	@Test
	public void test_references() throws Exception {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);//default

		Student student = new Student("zhangsan", "man", 0);
		Student student1 = new Student("stu1", "man", 1);
		student.setStudent1(student1);
		student.setStudent2(student1);
		{
			Output output = new Output(256);
			kryo.writeClassAndObject(output, student);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = kryo.readClassAndObject(input);

			System.out.println(stu);
			Student s = (Student) stu;
			System.out.println(s.getName() + "," + s.getSex());
			System.out.println(s.getStudent1() == s.getStudent2());
			Assert.assertEquals(s.getStudent1(), s.getStudent2());
		}

		kryo.setReferences(false);
		{
			Output output = new Output(256);
			kryo.writeClassAndObject(output, student);
			byte[] bytes = output.toBytes();

			Input input = new Input(bytes);
			Object stu = kryo.readClassAndObject(input);

			System.out.println(stu);
			Student s = (Student) stu;
			System.out.println(s.getName() + "," + s.getSex());
			System.out.println(s.getStudent1() == s.getStudent2());
			Assert.assertNotEquals(s.getStudent1(), s.getStudent2());
		}
	}
}
